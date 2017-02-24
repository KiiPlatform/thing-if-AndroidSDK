package com.kii.thingif.gateway;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.KiiApp;
import com.kii.thingif.MediaTypes;
import com.kii.thingif.SDKVersion;
import com.kii.thingif.exception.StoredInstanceNotFoundException;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.exception.UnloadableInstanceVersionException;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.Path;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GatewayAPI implements Parcelable {

    private static final String SHARED_PREFERENCES_KEY_INSTANCE = "GatewayAPI_INSTANCE";
    private static final String SHARED_PREFERENCES_SDK_VERSION_KEY = "GatewayAPI_VERSION";
    private static final String MINIMUM_LOADABLE_SDK_VERSION = "0.13.0";
    private static Context context;
    private final String tag;
    private final KiiApp app;
    private final String gatewayAddress;
    private String accessToken;
    private transient final IoTRestClient restClient;

    public static class Builder {

        private final Context context;
        private String tag;
        private final KiiApp app;
        private final Uri gatewayAddress;

        private Builder(
                @Nullable Context context,
                @NonNull KiiApp app,
                @NonNull Uri gatewayAddress) {
            this.context = context;
            this.app = app;
            this.gatewayAddress = gatewayAddress;
        }

        /** Set tag to this GatewayAPI instance.
         * tag is used to distinguish storage area of instance.
         * <br>
         * If the api instance is tagged with same string, It will be overwritten.
         * <br>
         * If the api instance is tagged with different string, Different key is used to store the
         * instance.
         * <br>
         * <br>
         * Please refer to {@link GatewayAPI#loadFromStoredInstance(Context, String)} as well.
         * @param tag if null or empty string is passed, it will be ignored.
         * @return builder instance for chaining call.
         */
        @NonNull
        public Builder setTag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * Instantiate new Builder.
         *
         * @param context Application context.
         * @param app Kii Cloud Application.
         * @param gatewayAddress address information for the gateway
         * @return Builder instance.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull Context context,
                @NonNull KiiApp app,
                @NonNull Uri gatewayAddress) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            if (app == null) {
                throw new IllegalArgumentException("app is null");
            }
            if (gatewayAddress == null) {
                throw new IllegalArgumentException("gatewayAddress is null");
            }
            return new Builder(context, app, gatewayAddress);
        }

        /**
         * Instantiate new GatewayAPI instance.
         * @return GatewayAPI instance.
         */
        @WorkerThread
        @NonNull
        public GatewayAPI build() {
            GatewayAPI api = new GatewayAPI(this.context, this.tag, this.app, this.gatewayAddress);
            return api;
        }
    }

    GatewayAPI(@Nullable Context context,
               @NonNull KiiApp app,
               @NonNull Uri gatewayAddress) {
        this(context, null, app, gatewayAddress);
    }
    GatewayAPI(@Nullable Context context,
               @Nullable String tag,
               @NonNull KiiApp app,
               @NonNull Uri gatewayAddress) {
        if (context != null) {
            GatewayAPI.context = context.getApplicationContext();
        }
        this.app = app;
        this.tag =tag;
        this.gatewayAddress = gatewayAddress.toString();
        this.restClient = new IoTRestClient();
    }

    protected Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + this.accessToken);
        return headers;
    }

    /** Login to the Gateway.
     * Local authentication for the Gateway access.
     * Required prior to call other APIs access to the gateway.
     * @param username Username of the Gateway.
     * @param password Password of the Gateway.
     * @throws ThingIFException Thrown when gateway returns error response.
     */
    @WorkerThread
    public void login(@NonNull String username, @NonNull String password) throws ThingIFException {
        if (TextUtils.isEmpty(username)) {
            throw new IllegalArgumentException("username is null or empty");
        }
        if (TextUtils.isEmpty(password)) {
            throw new IllegalArgumentException("password is null or empty");
        }
        String path = MessageFormat.format("/{0}/token", this.app.getSiteName());
        String url = Path.combine(this.gatewayAddress, path);

        String credential = this.app.getAppID() + ":" + this.app.getAppKey();
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP));

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("username", username);
            requestBody.put("password", password);
        } catch (JSONException e) {
            // Won’t happen
        }

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        JSONObject responseBody = new IoTRestClient().sendRequest(request);
        this.accessToken = responseBody.optString("accessToken", null);
        this.saveInstance(this);
    }

    /** Let the Gateway Onboard.
     * @return Gateway instance that has ThingID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @NonNull
    @WorkerThread
    public Gateway onboardGateway() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/onboarding", this.app.getSiteName(), this.app.getAppID());
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        try {
            // FIXME:Gateway should return the vendorThingID
            return new Gateway(responseBody.getString("thingID"), null);
        } catch (JSONException e) {
            throw new ThingIFException("", e);
        }
    }

    /**
     * Get Gateway ID
     * @return Thing ID assigned by Kii Cloud.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    @NonNull
    public String getGatewayID() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/id", this.app.getSiteName(), this.app.getAppID());
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("thingID", null);
    }

    /** List connected end nodes which has been onboarded.
     * @return List of end nodes
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    @NonNull
    public List<EndNode> listOnboardedEndNodes() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/onboarded", this.app.getSiteName(), this.app.getAppID());
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        List<EndNode> nodes = new ArrayList<EndNode>();
        JSONArray results = responseBody.optJSONArray("results");
        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                try {
                    String thingID = results.getJSONObject(i).getString("thingID");
                    String vendorThingID = results.getJSONObject(i).getString("vendorThingID");
                    nodes.add(new EndNode(thingID, vendorThingID, null));
                } catch (JSONException ignore) {
                }
            }
        }
        return nodes;
    }

    /** List connected end nodes which has not been onboarded.
     * @return List of end nodes connected to the gateway but waiting for onboarding.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    @NonNull
    public List<PendingEndNode> listPendingEndNodes() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/pending", this.app.getSiteName(), this.app.getAppID());
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        List<PendingEndNode> nodes = new ArrayList<PendingEndNode>();
        JSONArray results = responseBody.optJSONArray("results");
        if (results != null) {
            for (int i = 0; i < results.length(); i++) {
                try {
                    nodes.add(new PendingEndNode(results.getJSONObject(i)));
                } catch (JSONException ignore) {
                }
            }
        }
        return nodes;
    }

    /** Notify Onboarding completion
     * Call this api when the End Node onboarding is done.
     * After the call succeeded, End Node will be fully connected to Kii Cloud through the Gateway.
     * @param endNode Onboarded EndNode
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public void notifyOnboardingCompletion(@NonNull EndNode endNode) throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        if (endNode == null) {
            throw new IllegalArgumentException("endNode is null");
        }
        if (TextUtils.isEmpty(endNode.getThingID())) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(endNode.getVendorThingID())) {
            throw new IllegalArgumentException("venderThingID is null or empty");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/VENDOR_THING_ID:{2}", this.app.getSiteName(), this.app.getAppID(), endNode.getVendorThingID());
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thingID", endNode.getThingID());
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
    }

    /** Restore the Gateway.
     * This API can be used only for the Gateway App.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public void restore() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = "/gateway-app/gateway/restore";
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers);
        this.restClient.sendRequest(request);
    }

    /**
     * Replace end-node by new vendorThingID for end node thingID.
     *
     * @param endNodeThingID ID of the end-node assigned by Kii Cloud.
     * @param endNodeVenderThingID ID of the end-node assigned by End Node vendor.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     * See {@link #login(String, String)}
     */
    @WorkerThread
    public void replaceEndNode(@NonNull String endNodeThingID, @NonNull String endNodeVenderThingID) throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        if (TextUtils.isEmpty(endNodeThingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(endNodeVenderThingID)) {
            throw new IllegalArgumentException("venderThingID is null or empty");
        }
        String path = MessageFormat.format("/{0}/apps/{1}/gateway/end-nodes/THING_ID:{2}", this.app.getSiteName(), this.app.getAppID(), endNodeThingID);
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("vendorThingID", endNodeVenderThingID);
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
    }

    /**
     * Get information of the Gateway.
     * When the end user replaces the Gateway, Gateway App/End Node App need to obtain the new Gateway’s vendorThingID.
     *
     * @return Gateway Information.
     * @throws ThingIFException Thrown when gateway returns error response.
     * @throws IllegalStateException Thrown when user is not logged in.
     */
    @WorkerThread
    @NonNull
    public GatewayInformation getGatewayInformation() throws ThingIFException {
        if (!isLoggedIn()) {
            throw new IllegalStateException("Needs user login before execute this API");
        }
        String path = "/gateway-info";
        String url = Path.combine(this.gatewayAddress, path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return new GatewayInformation(responseBody.optString("vendorThingID", null));
    }

    /** Check If user is logged in to the Gateway.
     * @return true if user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(this.accessToken);
    }

    /**
     * Get a tag.
     * @return tag of this Gateway instance.
     */
    @Nullable
    public String getTag() {
        return this.tag;
    }
    /** Get Kii App
     * @return Kii Cloud Application.
     */
    @NonNull
    public KiiApp getApp() {
        return this.app;
    }
    /**
     * Get AppID
     * @return app ID
     */
    @NonNull
    public String getAppID() {
        return this.app.getAppID();
    }
    /**
     * Get AppKey
     * @return app key
     */
    @NonNull
    public String getAppKey() {
        return this.app.getAppKey();
    }

    /** Get GatewayAddress
     * @return Gateway Address
     */
    @NonNull
    public Uri getGatewayAddress() {
        return Uri.parse(this.gatewayAddress);
    }

    /**
     * Get Access Token
     * @return access token.
     */
    @Nullable
    public String getAccessToken() {
        return this.accessToken;
    }

    // Implementation of Parcelable
    public static final Creator<GatewayAPI> CREATOR = new Creator<GatewayAPI>() {
        @Override
        public GatewayAPI createFromParcel(Parcel in) {
            return new GatewayAPI(in);
        }

        @Override
        public GatewayAPI[] newArray(int size) {
            return new GatewayAPI[size];
        }
    };

    protected GatewayAPI(Parcel in) {
        this.tag = in.readString();
        this.app = in.readParcelable(KiiApp.class.getClassLoader());
        this.gatewayAddress = in.readString();
        this.accessToken = in.readString();
        this.restClient = new IoTRestClient();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
        dest.writeParcelable(this.app, flags);
        dest.writeString(this.gatewayAddress);
        dest.writeString(this.accessToken);
    }

    /**
     * Try to load the instance of GatewayAPI using stored serialized instance.
     * <BR>
     * Instance is automatically saved when {@link #login(String, String)} is called
     * and successfully completed.
     * <BR>
     *
     * If the GatewayAPI instance is build without the tag, all instance is saved in same place
     * and overwritten when the instance is saved.
     * <BR>
     * <BR>
     *
     * If the GatewayAPI instance is build with the tag(optional), tag is used as key to distinguish
     * the storage area to save the instance. This would be useful to saving multiple instance.
     * You need specify tag to load the instance by the
     * {@link #loadFromStoredInstance(Context, String) api}.
     *
     * When you catch exceptions, please call {@link #login(String, String)}
     * for saving or updating serialized instance.
     *
     * @param context context
     * @return ThingIFAPI instance.
     * @throws StoredInstanceNotFoundException when the instance has not stored yet.
     * @throws UnloadableInstanceVersionException when the instance couldn't be loaded.
     */
    @NonNull
    public static GatewayAPI loadFromStoredInstance(@NonNull Context context) throws StoredInstanceNotFoundException, UnloadableInstanceVersionException {
        return loadFromStoredInstance(context, null);
    }

    /**
     * Try to load the instance of GatewayAPI using stored serialized instance.
     * <BR>
     * For details please refer to the {@link #loadFromStoredInstance(Context)} document.
     *
     * @param context context
     * @param  tag specified when the ThingIFAPI has been built.
     * @return GatewayAPI instance.
     * @throws StoredInstanceNotFoundException when the instance has not stored yet.
     * @throws UnloadableInstanceVersionException when the instance couldn't be loaded.
     */
    @NonNull
    public static GatewayAPI loadFromStoredInstance(@NonNull Context context, @Nullable String tag) throws StoredInstanceNotFoundException, UnloadableInstanceVersionException {
        GatewayAPI.context = context.getApplicationContext();
        SharedPreferences preferences = getSharedPreferences();

        String serializedJson = preferences.getString(getStoredInstanceKey(tag), null);
        if (serializedJson == null) {
            throw new StoredInstanceNotFoundException(tag);
        }

        String storedSDKVersion = preferences.getString(getStoredSDKVersionKey(tag), null);
        if (!isLoadableSDKVersion(storedSDKVersion)) {
            throw new UnloadableInstanceVersionException(tag, storedSDKVersion,
                    MINIMUM_LOADABLE_SDK_VERSION);
        }

        Gson gson = new GsonBuilder().create();
        return  gson.fromJson(serializedJson, GatewayAPI.class);
    }
    /**
     * Clear all saved instances in the SharedPreferences.
     */
    public static void removeAllStoredInstances() {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
    /**
     * Remove saved specified instance in the SharedPreferences.
     *
     * @param tag tag to specify removing stored instance.
     */
    public static void removeStoredInstance(@Nullable String tag) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getStoredSDKVersionKey(tag));
        editor.remove(getStoredInstanceKey(tag));
        editor.apply();
    }
    private static void saveInstance(GatewayAPI instance) {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getStoredSDKVersionKey(instance.tag), SDKVersion.versionString);
            Gson gson = new GsonBuilder().create();
            editor.putString(getStoredInstanceKey(instance.tag), gson.toJson(instance));
            editor.apply();
        }
    }
    private static String getStoredInstanceKey(String tag) {
        return SHARED_PREFERENCES_KEY_INSTANCE + (tag == null ? "" : "_"  +tag);
    }

    private static String getStoredSDKVersionKey(String tag) {
        return SHARED_PREFERENCES_SDK_VERSION_KEY + (tag == null ? "" : "_"  +tag);
    }

    private static SharedPreferences getSharedPreferences() {
        if (context != null) {
            return context.getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        }
        return null;
    }

    private static boolean isLoadableSDKVersion(String storedSDKVersion) {
        if (storedSDKVersion == null) {
            return false;
        }

        String[] actualVersions = storedSDKVersion.split("\\.");
        if (actualVersions.length != 3) {
            return false;
        }

        String[] minimumLoadableVersions = GatewayAPI.MINIMUM_LOADABLE_SDK_VERSION.split("\\.");
        for (int i = 0; i < 3; ++i) {
            int actual = Integer.parseInt(actualVersions[i]);
            int expect = Integer.parseInt(minimumLoadableVersions[i]);
            if (actual < expect) {
                return false;
            } else if (actual > expect) {
                break;
            }
        }
        return true;
    }
}
