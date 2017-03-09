package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Pair;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.kii.thingif.clause.query.QueryClause;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.AliasActionResult;
import com.kii.thingif.command.AliasActionResultAdapter;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandForm;
import com.kii.thingif.exception.BadRequestException;
import com.kii.thingif.exception.ConflictException;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.exception.StoredInstanceNotFoundException;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.exception.ThingIFRestException;
import com.kii.thingif.exception.UnloadableInstanceVersionException;
import com.kii.thingif.exception.UnregisteredAliasException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.PendingEndNode;
import com.kii.thingif.internal.gson.AliasActionAdapter;
import com.kii.thingif.internal.gson.HistoryStateAdapter;
import com.kii.thingif.internal.gson.JSONObjectAdapter;
import com.kii.thingif.internal.gson.PredicateAdapter;
import com.kii.thingif.internal.gson.QueryClauseAdapter;
import com.kii.thingif.internal.gson.ThingIFAPIAdapter;
import com.kii.thingif.trigger.TriggeredServerCodeResultAdapter;
import com.kii.thingif.internal.gson.TypedIDAdapter;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.internal.utils._Log;
import com.kii.thingif.query.AggregatedResult;
import com.kii.thingif.query.Aggregation;
import com.kii.thingif.query.GroupedHistoryStates;
import com.kii.thingif.query.GroupedHistoryStatesQuery;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.HistoryStatesQuery;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.internal.utils.Path;
import com.kii.thingif.trigger.TriggerOptions;
import com.kii.thingif.trigger.TriggeredCommandForm;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.TriggersWhat;
import com.squareup.okhttp.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * ThingIFAPI represent an API instance to access Thing-IF APIs for a specified target.
 */
public class ThingIFAPI implements Parcelable {

    private static final String SHARED_PREFERENCES_KEY_INSTANCE = "ThingIFAPI_INSTANCE";
    private static final String SHARED_PREFERENCES_SDK_VERSION_KEY = "ThingIFAPI_VERSION";
    private static final String MINIMUM_LOADABLE_SDK_VERSION = "0.13.0";

    private static Context context;
    private final String tag;
    private final KiiApp app;
    private final Owner owner;
    private Target target;
    private final IoTRestClient restClient;
    private String installationID;

    private final Map<String, Class<? extends Action>> actionTypes;
    private final Map<String, Class<? extends TargetState>> stateTypes;

    private Gson gson;

    public static class Builder {

        private static final String TAG = Builder.class.getSimpleName();
        private final @NonNull Context context;
        private final @NonNull KiiApp app;
        private final @NonNull Owner owner;
        private @Nullable Target target;
        private @Nullable String installationID;
        private @Nullable String tag;

        private final @NonNull Map<String, Class<? extends Action>> actionTypes;
        private final @NonNull Map<String, Class<? extends TargetState>> stateTypes;

        private Builder(
                @Nullable Context context,
                @NonNull KiiApp app,
                @NonNull Owner owner,
                @NonNull Map<String, Class<? extends Action>> actionTypes,
                @NonNull Map<String, Class<? extends TargetState>> stateTypes
        ) {
            this.context = context;
            this.app = app;
            this.owner = owner;
            this.actionTypes = actionTypes;
            this.stateTypes = stateTypes;
        }

        /** Instantiate new Builder.
         * @param context Application context.
         * @param app Kii Cloud Application.
         * @param owner Specify who uses the ThingIFAPI.
         * @param actionTypes Map of alias and action class.
         * @param stateTypes Map of alias and target state class.
         * @return Builder instance.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull Context context,
                @NonNull KiiApp app,
                @NonNull Owner owner,
                @NonNull Map<String, Class<? extends Action>> actionTypes,
                @NonNull Map<String, Class<? extends TargetState>> stateTypes) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            if (app == null) {
                throw new IllegalArgumentException("app is null");
            }
            if (owner == null) {
                throw new IllegalArgumentException("owner is null");
            }
            return new Builder(context, app, owner, actionTypes, stateTypes);
        }

        /** Instantiate new Builder.
         * @param context Application context.
         * @param app Kii Cloud Application.
         * @param owner Specify who uses the ThingIFAPI.
         * @return Builder instance.
         */
        @NonNull
        public static Builder newBuilder(
                @NonNull Context context,
                @NonNull KiiApp app,
                @NonNull Owner owner) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            if (app == null) {
                throw new IllegalArgumentException("app is null");
            }
            if (owner == null) {
                throw new IllegalArgumentException("owner is null");
            }
            return new Builder(
                    context,
                    app,
                    owner,
                    new HashMap<String, Class<? extends Action>>(),
                    new HashMap<String, Class<? extends TargetState>>());
        }

        /**
         * Instantiate new Builder without Context.
         * This method is for internal use only. Do not call it from your application.
         *
         * @param app Kii Cloud Application.
         * @param owner Specify who uses the ThingIFAPI.
         * @return Builder instance.
         */
        @NonNull
        public static Builder _newBuilder(
                @NonNull KiiApp app,
                @NonNull Owner owner,
                @NonNull Map<String, Class<? extends Action>> actionTypes,
                @NonNull Map<String, Class<? extends TargetState>> stateTypes) {
            if (app == null) {
                throw new IllegalArgumentException("app is null");
            }
            if (owner == null) {
                throw new IllegalArgumentException("owner is null");
            }
            return new Builder(null, app, owner, actionTypes, stateTypes);
        }

        /**
         * Set target thing to the ThingIFAPI.
         * @param target target of {@link ThingIFAPI} instance.
         * @return builder instance for chaining call.
         */
        public Builder setTarget(Target target) {
            this.target = target;
            return this;
        }

        /** Set tag to this ThingIFAPI instance.
         * tag is used to distinguish storage area of instance.
         * <br>
         * If the api instance is tagged with same string, It will be overwritten.
         * <br>
         * If the api instance is tagged with different string, Different key is used to store the
         * instance.
         * <br>
         * <br>
         * Please refer to {@link ThingIFAPI#loadFromStoredInstance(Context, String)} as well.
         * @param tag if null or empty string is passed, it will be ignored.
         * @return builder instance for chaining call.
         */
        @NonNull
        public Builder setTag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * Set InstallationID to the ThingIFAPI.
         * @param installationID installation id
         * @return builder instance for chaining call.
         */
        public Builder setInstallationID(
                @NonNull  String installationID) {
            this.installationID = installationID;
            return this;
        }

        /** Instantiate new ThingIFAPI instance.
         * @return ThingIFAPI instance.
         * @throws IllegalStateException when actionTypes or stateTypes is empty.
         */
        @NonNull
        public ThingIFAPI build() {

            _Log.d(TAG, MessageFormat.format("Initialize ThingIFAPI AppID={0}, AppKey={1}, BaseUrl={2}", app.getAppID(), app.getAppKey(), app.getBaseUrl()));
            if (this.actionTypes.size() == 0) {
                throw new IllegalStateException("actionTypes is empty");
            }
            if (this.stateTypes.size() == 0) {
                throw new IllegalStateException("stateTypes is empty");
            }
            return new ThingIFAPI(this.context, this.tag, app, this.owner, this.target, this.installationID, this.actionTypes, this.stateTypes);
        }

        /**
         * Register list of Action subclasses to specified alias. The registered action classes
         * will be used for serialization/deserialization the action.
         * If the same alias already registered, then will be updated
         * @param alias Alias to register
         * @param actionClass List of Action subclasses
         * @return builder instance for chaining call.
         */
        @NonNull
        public Builder registerActions(
                @NonNull String alias,
                @NonNull Class<? extends Action> actionClass){
            this.actionTypes.put(alias, actionClass);
            return this;
        }

        /**
         * Register TargetState to specified alias.
         * The registered stateClass will be used when deserialization state from server.
         * If the same alias already registered, then will be updated.
         * @param alias Alias to register.
         * @param stateClass Class of TargetState subclass.
         * @return builder instance for chaining call.
         */
        @NonNull
        public Builder registerTargetState(
                @NonNull String alias,
                @NonNull Class<? extends TargetState> stateClass) {
            this.stateTypes.put(alias, stateClass);
            return this;
        }
    }

    /**
     * Try to load the instance of ThingIFAPI using stored serialized instance.
     * <BR>
     * Instance is automatically saved when following methods are called.
     * <BR>
     * {@link #onboardWithThingID(String, String)}, {@link #onboardWithVendorThingID(String, String)},
     * {@link #copyWithTarget(Target, String)}
     * and {@link #installPush} has been successfully completed.
     * <BR>
     * (When {@link #copyWithTarget(Target, String)} is called, only the copied instance is saved.)
     * <BR>
     * <BR>
     *
     * If the ThingIFAPI instance is build without the tag, all instance is saved in same place
     * and overwritten when the instance is saved.
     * <BR>
     * <BR>
     *
     * If the ThingIFAPI instance is build with the tag(optional), tag is used as key to distinguish
     * the storage area to save the instance. This would be useful to saving multiple instance.
     * You need specify tag to load the instance by the
     * {@link #loadFromStoredInstance(Context, String) api}.
     *
     * When you catch exceptions, please call onboard methods
     * for saving or updating serialized instance.
     *
     * @param context context
     * @return ThingIFAPI instance.
     * @throws StoredInstanceNotFoundException when the instance has not stored yet.
     * @throws UnloadableInstanceVersionException when the instance couldn't be loaded.
     */
    @NonNull
    public static ThingIFAPI loadFromStoredInstance(@NonNull Context context) throws StoredInstanceNotFoundException, UnloadableInstanceVersionException {
        return loadFromStoredInstance(context, null);
    }

    /**
     * Try to load the instance of ThingIFAPI using stored serialized instance.
     * <BR>
     * For details please refer to the {@link #loadFromStoredInstance(Context)} document.
     *
     * @param context context
     * @param  tag specified when the ThingIFAPI has been built.
     * @return ThingIFAPI instance.
     * @throws StoredInstanceNotFoundException when the instance has not stored yet.
     * @throws UnloadableInstanceVersionException when the instance couldn't be loaded.
     */
    @NonNull
    public static ThingIFAPI loadFromStoredInstance(@NonNull Context context, String tag) throws StoredInstanceNotFoundException, UnloadableInstanceVersionException {
        ThingIFAPI.context = context.getApplicationContext();
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ThingIFAPI.class, new ThingIFAPIAdapter())
                .create();
        return gson.fromJson(serializedJson, ThingIFAPI.class);
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
     * @param tag tag to specify stored instance.
     */
    public static void removeStoredInstance(@Nullable String tag) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getStoredSDKVersionKey(tag));
        editor.remove(getStoredInstanceKey(tag));
        editor.apply();
    }
    private static void saveInstance(ThingIFAPI instance) {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getStoredSDKVersionKey(instance.tag), SDKVersion.versionString);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(ThingIFAPI.class, new ThingIFAPIAdapter())
                    .create();
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

    private static boolean isLoadableSDKVersion(String storedSDKVersion) {
        if (storedSDKVersion == null) {
            return false;
        }

        String[] actualVersions = storedSDKVersion.split("\\.");
        if (actualVersions.length != 3) {
            return false;
        }

        String[] minimumLoadableVersions = ThingIFAPI.MINIMUM_LOADABLE_SDK_VERSION.split("\\.");
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

    ThingIFAPI(
            @Nullable Context context,
            @Nullable String tag,
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @Nullable Target target,
            @Nullable String installationID,
            @NonNull Map<String, Class<? extends Action>> actionTypes,
            @NonNull Map<String, Class<? extends TargetState>> stateTypes
            ) {
        // Parameters are checked by Builder
        if (context != null) {
            ThingIFAPI.context = context.getApplicationContext();
        }
        this.tag = tag;
        this.app = app;
        this.owner = owner;
        this.target = target;
        this.installationID = installationID;
        this.restClient = new IoTRestClient();
        this.actionTypes = actionTypes;
        this.stateTypes = stateTypes;

        this.gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasAction.class,
                        new AliasActionAdapter(this.actionTypes))
                .registerTypeAdapter(
                        AliasActionResult.class,
                        new AliasActionResultAdapter())
                .registerTypeAdapter(
                        TypedID.class,
                        new TypedIDAdapter())
                .registerTypeAdapter(
                        JSONObject.class,
                        new JSONObjectAdapter())
                .registerTypeAdapter(
                        Predicate.class,
                        new PredicateAdapter())
                .registerTypeAdapter(
                        TriggeredServerCodeResult.class,
                        new TriggeredServerCodeResultAdapter())
                .registerTypeAdapter(
                        QueryClause.class,
                        new QueryClauseAdapter())
                .create();
    }
    /**
     * Create the clone instance that has specified target and tag.
     *
     * @param target coping target.
     * @param tag A key to store instnace.
     * @return ThingIFAPI instance
     */
    public ThingIFAPI copyWithTarget(@NonNull Target target, @Nullable String tag) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }
        ThingIFAPI api = new ThingIFAPI(context, tag, this.app, this.owner, target, this.installationID, this.actionTypes, this.stateTypes);
        saveInstance(api);
        return api;
    }

    /**
     * On board IoT Cloud with the specified vendor thing ID.
     * Specified thing will be owned by owner who is specified
     * IoT Cloud prepares communication channel to the target.
     * If you are using a gateway, you need to use {@link #onboardEndNodeWithGateway(PendingEndNode, String)} instead.
     * @param vendorThingID Thing ID given by vendor. Must be specified.
     * @param thingPassword Thing Password given by vendor. Must be specified.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onboardWithVendorThingID(
            @NonNull String vendorThingID,
            @NonNull String thingPassword)
            throws ThingIFException {
        OnboardWithVendorThingIDOptions.Builder builder = new OnboardWithVendorThingIDOptions.Builder();
        return onboardWithVendorThingID(vendorThingID, thingPassword, builder.build());
    }

    /**
     * On board IoT Cloud with the specified vendor thing ID.
     * Specified thing will be owned by owner who is specified
     * IoT Cloud prepares communication channel to the target.
     * If you are using a gateway, you need to use {@link #onboardEndNodeWithGateway(PendingEndNode, String)} instead.
     * @param vendorThingID Thing ID given by vendor. Must be specified.
     * @param thingPassword Thing Password given by vendor. Must be specified.
     * @param options optional parameters inside.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onboardWithVendorThingID(
            @NonNull String vendorThingID,
            @NonNull String thingPassword,
            @Nullable OnboardWithVendorThingIDOptions options)
            throws ThingIFException {
        if (this.onboarded()) {
            throw new IllegalStateException("This instance is already onboarded.");
        }
        if (TextUtils.isEmpty(vendorThingID)) {
            throw new IllegalArgumentException("vendorThingID is null or empty");
        }
        if (TextUtils.isEmpty(thingPassword)) {
            throw new IllegalArgumentException("thingPassword is null or empty");
        }
        JSONObject requestBody = new JSONObject();
        LayoutPosition layoutPosition = null;
        try {
            requestBody.put("vendorThingID", vendorThingID);
            requestBody.put("thingPassword", thingPassword);
            if (options != null) {
                String thingType = options.getThingType();
                String firmwareVersion = options.getFirmwareVersion();
                JSONObject thingProperties = options.getThingProperties();
                layoutPosition = options.getLayoutPosition();
                if (thingType != null) {
                    requestBody.put("thingType", thingType);
                }
                if (firmwareVersion != null) {
                    requestBody.put("firmwareVersion", firmwareVersion);
                }
                if (thingProperties != null && thingProperties.length() > 0) {
                    requestBody.put("thingProperties", thingProperties);
                }
                if (layoutPosition != null) {
                    requestBody.put("layoutPosition", layoutPosition.name());
                }
            }
            requestBody.put("owner", this.owner.getTypedID().toString());
        } catch (JSONException e) {
            // Won’t happen
        }
        return this.onboard(MediaTypes.MEDIA_TYPE_ONBOARDING_WITH_VENDOR_THING_ID_BY_OWNER_REQUEST, requestBody, vendorThingID, layoutPosition);
    }

    /**
     * On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * {@link #onboardWithThingID(String, String, OnboardWithThingIDOptions)}.
     * If you are using a gateway, you need to use {@link #onboardEndNodeWithGateway(PendingEndNode, String)} instead.
     * @param thingID Thing ID given by IoT Cloud. Must be specified.
     * @param thingPassword Thing password given by vendor. Must be specified.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onboardWithThingID(
            @NonNull String thingID,
            @NonNull String thingPassword) throws
            ThingIFException {
        return onboardWithThingID(thingID, thingPassword, null);
    }

    /**
     * On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * If you are using a gateway, you need to use {@link #onboardEndNodeWithGateway(PendingEndNode, String)} instead.
     * @param thingID Thing ID given by IoT Cloud. Must be specified.
     * @param thingPassword Thing password given by vendor. Must be specified.
     * @param options optional parameters inside.
     * @return Target instance can be used to operate target, manage resources
     * of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Target onboardWithThingID(
            @NonNull String thingID,
            @NonNull String thingPassword,
            @Nullable OnboardWithThingIDOptions options)
            throws ThingIFException {
        if (this.onboarded()) {
            throw new IllegalStateException("This instance is already onboarded.");
        }
        if (TextUtils.isEmpty(thingID)) {
            throw new IllegalArgumentException("thingID is null or empty");
        }
        if (TextUtils.isEmpty(thingPassword)) {
            throw new IllegalArgumentException("thingPassword is null or empty");
        }
        JSONObject requestBody = new JSONObject();
        LayoutPosition layoutPosition = null;
        try {
            requestBody.put("thingID", thingID);
            requestBody.put("thingPassword", thingPassword);
            requestBody.put("owner", this.owner.getTypedID().toString());
            if (options != null) {
                layoutPosition = options.getLayoutPosition();
                if (layoutPosition != null) {
                    requestBody.put("layoutPosition", layoutPosition.name());
                }
            }
        } catch (JSONException e) {
            // Won’t happen
        }
        // FIXME: Currently, Server does not return the VendorThingID when onboarding is successful.
        return this.onboard(MediaTypes.MEDIA_TYPE_ONBOARDING_WITH_THING_ID_BY_OWNER_REQUEST, requestBody, null, layoutPosition);
    }

    private Target onboard(MediaType contentType, JSONObject requestBody, String vendorThingID, LayoutPosition layoutPosition) throws ThingIFException {
        String path = MessageFormat.format("/thing-if/apps/{0}/onboardings", this.app.getAppID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, contentType, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        String thingID = responseBody.optString("thingID", null);
        String accessToken = responseBody.optString("accessToken", null);
        if (layoutPosition == LayoutPosition.GATEWAY) {
            this.target = new Gateway(thingID, vendorThingID);
        } else if (layoutPosition == LayoutPosition.ENDNODE) {
            this.target = new EndNode(thingID, vendorThingID, accessToken);
        } else {
            this.target = new StandaloneThing(thingID, vendorThingID,
                    accessToken);
        }
        saveInstance(this);
        return this.target;
    }

    /**
     * Endpoints execute onboarding for the thing and merge MQTT channel to the gateway.
     * Thing act as Gateway is already registered and marked as Gateway.
     *
     * @param pendingEndNode Pending endnode
     * @param endnodePassword Password of the End Node
     * @return Target instance can be used to operate target, manage resources of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public EndNode onboardEndNodeWithGateway(
            @NonNull PendingEndNode pendingEndNode,
            @NonNull String endnodePassword)
            throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding the gateway");
        }
        if (this.target instanceof EndNode) {
            throw new IllegalStateException("Target must be Gateway");
        }
        if (pendingEndNode == null) {
            throw new IllegalArgumentException("pendingEndNode is null or empty");
        }
        if (TextUtils.isEmpty(pendingEndNode.getVendorThingID())) {
            throw new IllegalArgumentException("vendorThingID is null or empty");
        }
        if (TextUtils.isEmpty(endnodePassword)) {
            throw new IllegalArgumentException("endnodePassword is null or empty");
        }
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("gatewayThingID", this.target.getTypedID().getID());
            requestBody.put("endNodeVendorThingID", pendingEndNode.getVendorThingID());
            requestBody.put("endNodePassword", endnodePassword);
            if (!TextUtils.isEmpty(pendingEndNode.getThingType())) {
                requestBody.put("endNodeThingType", pendingEndNode.getThingType());
            }
            if (pendingEndNode.getThingProperties() != null && pendingEndNode.getThingProperties().length() > 0) {
                requestBody.put("endNodeThingProperties", pendingEndNode.getThingProperties());
            }
            requestBody.put("owner", this.owner.getTypedID().toString());
        } catch (JSONException e) {
            // Won’t happen
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/onboardings", this.app.getAppID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_ONBOARDING_ENDNODE_WITH_GATEWAY_THING_ID_REQUEST, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        String thingID = responseBody.optString("endNodeThingID", null);
        String accessToken = responseBody.optString("accessToken", null);
        return new EndNode(thingID, pendingEndNode.getVendorThingID(), accessToken);
    }

    /**
     * Checks whether on boarding is done.
     * @return true if done, otherwise false.
     */
    public boolean onboarded()
    {
        return this.target != null;
    }

    /**
     * Install push notification to receive notification from IoT Cloud. This will install on production environment.
     * IoT Cloud will send notification when the Target replies to the Command.
     * Application can receive the notification and check the result of Command
     * fired by Application or registered Trigger.
     * After installation is done Installation ID is managed in this class.
     * @param deviceToken for GCM, specify token obtained by
     *                    InstanceID.getToken().
     *                    for JPUSH, specify id obtained by
     *                    JPushInterface.getUdid().
     * @param pushBackend Specify backend to use.
     * @return Installation ID used in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @see #installPush(String, PushBackend, boolean) for development/production environment installation.
     */
    @NonNull
    @WorkerThread
    public String installPush(
            @Nullable String deviceToken,
            @NonNull PushBackend pushBackend
    ) throws ThingIFException {
        return this.installPush(deviceToken, pushBackend, false);
    }

    /**
     * Install push notification to receive notification from IoT Cloud.
     * IoT Cloud will send notification when the Target replies to the Command.
     * Application can receive the notification and check the result of Command
     * fired by Application or registered Trigger.
     * After installation is done Installation ID is managed in this class.
     * @param deviceToken for GCM, specify token obtained by
     *                    InstanceID.getToken().
     *                    for JPUSH, specify id obtained by
     *                    JPushInterface.getUdid().
     * @param pushBackend Specify backend to use.
     * @param development Specify development flag to use. Indicates if the installation is for development or production environment.
     * @return Installation ID used in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public String installPush(
            @Nullable String deviceToken,
            @NonNull PushBackend pushBackend,
            boolean development
    ) throws ThingIFException{
        if (pushBackend == null) {
            throw new IllegalArgumentException("pushBackend is null");
        }

        String path = MessageFormat.format("/api/apps/{0}/installations", this.app.getAppID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        try {
            if (!TextUtils.isEmpty(deviceToken)) {
                requestBody.put("installationRegistrationID", deviceToken);
            }
            if (development){
                requestBody.put("development", true);
            }
            requestBody.put("deviceType", pushBackend.getDeviceType());
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_INSTALLATION_CREATION_REQUEST, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        this.installationID = responseBody.optString("installationID", null);
        saveInstance(this);
        return this.installationID;
    }
    /**
     * Get installationID if the push is already installed.
     * null will be returned if the push installation has not been done.
     * @return Installation ID used in IoT Cloud.
     */
    @Nullable
    public String getInstallationID() {
        return this.installationID;
    }

    /**
     * Uninstall push notification.
     * After done, notification from IoT Cloud won't be notified.
     * @param installationID installation ID returned from
     *                       {@link #installPush(String, PushBackend)}
     *                       if null is specified, value obtained by
     *                       {@link #getInstallationID()} is used.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public void uninstallPush(@NonNull String installationID) throws ThingIFException {
        if (installationID == null) {
            throw new IllegalArgumentException("installationID is null");
        }
        String path = MessageFormat.format("/api/apps/{0}/installations/{1}", this.app.getAppID(), installationID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.DELETE, headers);
        this.restClient.sendRequest(request);
    }

    /**
     * Post new command to IoT Cloud.
     * Command will be delivered to specified target and result will be notified
     * through push notification.
     * @param form form of command. It contains name of schema, version of
     * schema, list of actions etc.
     * @return Created Command instance. At this time, Command is delivered to
     * the target Asynchronously and may not finished. Actual Result will be
     * delivered through push notification or you can check the latest status
     * of the command by calling {@link #getCommand}.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Command postNewCommand(
            @NonNull CommandForm form) throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands",
                this.app.getAppID(), this.target.getTypedID().toString());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = createPostNewCommandRequestBody(form);
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers,
                MediaTypes.MEDIA_TYPE_POST_NEW_COMMAND_TRAIT, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);

        String commandID = responseBody.optString("commandID", null);
        return this.getCommand(commandID);
    }

    /**
     * Get specified command.
     * @param commandID ID of the command to obtain. ID is present in the
     *                  instance returned by {@link #postNewCommand}
     *                  and can be obtained by {@link Command#getCommandID}
     *
     * @return Command instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     */
    @NonNull
    @WorkerThread
    public Command getCommand(
            @NonNull String commandID)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(commandID)) {
            throw new IllegalArgumentException("commandID is null or empty");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands/{2}", this.app.getAppID(), this.target.getTypedID().toString(), commandID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        try {
            return this.gson.fromJson(responseBody.toString(), Command.class);
        }catch (JsonParseException ex) {
            if (ex.getCause() instanceof ThingIFException) {
                throw (ThingIFException)ex.getCause();
            }else{
                throw ex;
            }
        }
    }
    /**
     * List Commands in the specified Target.<br>
     * @param bestEffortLimit Maximum number of the Commands in the response.
     *                        if the value is {@literal <}= 0, default limit internally
     *                        defined is applied.
     *                        Meaning of 'bestEffort' is if the specified limit
     *                        is greater than default limit, default limit is
     *                        applied.
     * @param paginationKey Used to get the next page of previously obtained.
     *                      If there is further page to obtain, this method
     *                      returns paginationKey as the 2nd element of pair.
     *                      Applying this key to the argument results continue
     *                      to get the result from the next page.
     * @return 1st Element is Commands belongs to the Target. 2nd element is
     * paginationKey if there is next page to be obtained.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     */
    @NonNull
    public Pair<List<Command>, String> listCommands (
            int bestEffortLimit,
            @Nullable String paginationKey)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands", this.app.getAppID(), this.target.getTypedID().toString());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        if (bestEffortLimit > 0) {
            request.addQueryParameter("bestEffortLimit", bestEffortLimit);
        }
        if (!TextUtils.isEmpty(paginationKey)) {
            request.addQueryParameter("paginationKey", paginationKey);
        }

        JSONObject responseBody = this.restClient.sendRequest(request);
        String nextPaginationKey = responseBody.optString("nextPaginationKey", null);
        JSONArray commandArray = responseBody.optJSONArray("commands");
        List<Command> commands = new ArrayList<>();

        if (commandArray != null) {
            try {
                for (int i = 0; i < commandArray.length(); i++) {
                    JSONObject commandJson = commandArray.optJSONObject(i);
                    commands.add(this.gson.fromJson(commandJson.toString(), Command.class));
                }
            }catch (JsonParseException ex) {
                if (ex.getCause() instanceof ThingIFException) {
                    throw (ThingIFException)ex.getCause();
                }else{
                    throw ex;
                }
            }
        }
        return new Pair<>(commands, nextPaginationKey);
    }

    /**
     * Post new Trigger with commands to IoT Cloud.
     *
     * <p>
     * When thing retrieved by {@link #getTarget()} of this ThingIFAPI
     * instance meets condition described by predicate, A command registered
     * by {@link TriggeredCommandForm} sends to thing given by {@link
     * TriggeredCommandForm#getTargetID()}.
     * </p>
     *
     * <p>
     * {@link #getTarget()} instance and thing specified by {@link
     * TriggeredCommandForm#getTargetID()} must be same owner's things.
     * </p>
     *
     * @param form Form of triggered command. It contains name of schema,
     * version of schema, list of actions, target IDof thing etc. You can see
     * detail of form in {@link TriggeredCommandForm}.
     * @param predicate Specify when the Trigger fires command.
     * @param options option fileds of this trigger.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if any of form, predicate and options is/are null.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull TriggeredCommandForm form,
            @NonNull Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        return postNewTriggerWithForm(form, predicate, options);
    }

    /**
     * Post new Trigger with commands to IoT Cloud.
     *
     * <p>
     * When thing retrieved by {@link #getTarget()} of this ThingIFAPI
     * instance meets condition described by predicate, A command registered
     * by {@link TriggeredCommandForm} sends to thing given by {@link
     * TriggeredCommandForm#getTargetID()}.
     * </p>
     * Limited version of {@link #postNewTrigger(TriggeredCommandForm, Predicate, TriggerOptions)}
     * <p>
     * {@link #getTarget()} instance and thing specified by {@link
     * TriggeredCommandForm#getTargetID()} must be same owner's things.
     * </p>
     *
     * @param form Form of triggered command. It contains name of schema,
     * version of schema, list of actions, target IDof thing etc. You can see
     * detail of form in {@link TriggeredCommandForm}.
     * @param predicate Specify when the Trigger fires command.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if any of form and predicate is/are null.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull TriggeredCommandForm form,
            @NonNull Predicate predicate)
            throws ThingIFException
    {
        return postNewTriggerWithForm(form, predicate, null);
    }
    private Trigger postNewTriggerWithForm(
            @NonNull TriggeredCommandForm form,
            @NonNull Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (form == null) {
            throw new IllegalArgumentException("form is null.");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate is null.");
        }
        JSONObject requestBody = options != null ?
                JsonUtils.newJson(this.gson.toJson(options)) :
                new JSONObject();
        try {
            requestBody.put("triggersWhat", TriggersWhat.COMMAND.name());
            requestBody.put("predicate", JsonUtils.newJson(
                        this.gson.toJson(predicate, Predicate.class)));
            JSONObject command = JsonUtils.newJson(
                this.gson.toJson(form));
            command.put("issuer", this.owner.getTypedID());
            if (!command.has("target")) {
                command.put("target", this.target.getTypedID().toString());
            }
            requestBody.put("command", command);
        } catch (JSONException e) {
            // Won't happen.
            // TODO: remove this after test finished.
            throw new RuntimeException(e);
        }
        return postNewTrigger(requestBody);
    }

    /**
     * Post new Trigger with server code to IoT Cloud.
     *
     * @param serverCode Specify server code you want to execute.
     * @param predicate Specify when the Trigger fires command.
     * @param options option fileds of this trigger.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if any of serverCode, predicate and options is/are null.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull ServerCode serverCode,
            @NonNull Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        return postServerCodeNewTrigger(serverCode, predicate, options);
    }

    /**
     * Post new Trigger with server code to IoT Cloud.
     *
     * <p>
     * Limited version of {@link #postNewTrigger(ServerCode, Predicate,
     * TriggerOptions)}. This method can not be set title, description and
     * metadata of {@link Trigger}.
     * </p>
     *
     * @param serverCode Specify server code you want to execute.
     * @param predicate Specify when the Trigger fires command.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if any of serverCode and predicate is/are null.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull ServerCode serverCode,
            @NonNull Predicate predicate)
            throws ThingIFException {
        return postServerCodeNewTrigger(serverCode, predicate, null);
    }

    @NonNull
    @WorkerThread
    private Trigger postServerCodeNewTrigger(
            @NonNull ServerCode serverCode,
            @NonNull Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        if (this.target == null) {
            throw new IllegalStateException(
                "Can not perform this action before onboarding");
        }
        if (serverCode == null) {
            throw new IllegalArgumentException("serverCode is null");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate is null");
        }
        JSONObject requestBody = options != null ?
                JsonUtils.newJson(this.gson.toJson(options)) :
                new JSONObject();
        try {
            requestBody.put("predicate", JsonUtils.newJson(this.gson.toJson(predicate, Predicate.class)));
            requestBody.put("triggersWhat", TriggersWhat.SERVER_CODE.name());
            requestBody.put("serverCode", JsonUtils.newJson(this.gson.toJson(serverCode)));
        } catch (JSONException e) {
            // Won't happen
            throw new RuntimeException(e);
        }
        return this.postNewTrigger(requestBody);
    }
    private Trigger postNewTrigger(@NonNull JSONObject requestBody) throws ThingIFException {
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers", this.app.getAppID(), this.target.getTypedID().toString());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();

        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        String triggerID = responseBody.optString("triggerID", null);
        return this.getTrigger(triggerID);
    }

    /**
     * Get specified Trigger.
     * @param triggerID ID of the Trigger to get.
     * @return Trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     */
    @NonNull
    @WorkerThread
    public Trigger getTrigger(
            @NonNull String triggerID)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.app.getAppID(), this.target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        JsonObject triggerJson = new JsonParser().parse(responseBody.toString()).getAsJsonObject();
        triggerJson.add("targetID", gson.toJsonTree(this.target.getTypedID()));
        try {
            return this.gson.fromJson(triggerJson, Trigger.class);
        }catch (JsonParseException ex) {
            if (ex.getCause() instanceof ThingIFException) {
                throw (ThingIFException) ex.getCause();
            } else {
                throw ex;
            }
        }
    }

    /**
     * Apply patch to registered trigger.
     * Modify registered trigger with specified patch.
     *
     * @param triggerID ID of the trigger to apply patch.
     * @param form Form of triggered command. It contains name of schema,
     * version of schema, list of actions, target IDof thing etc. You can see
     * detail of form in {@link TriggeredCommandForm}.
     * @param predicate Modified predicate.
     * @param options option fileds of this trigger.
     * @return Updated trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException This exception is thrown if one or
     * more following conditions are met.
     * <ul>
     *  <li>triggerID is null or empty string.</li>
     *  <li>All of form, predicate and options are null.</li>
     * </ul>
     */
    @NonNull
    @WorkerThread
    public Trigger patchCommandTrigger(
            @NonNull String triggerID,
            @Nullable TriggeredCommandForm form,
            @Nullable Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        if (this.target == null) {
            throw new IllegalStateException(
                    "Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }
        if (form == null && predicate == null && options == null) {
            throw new IllegalArgumentException(
                    "All of form, predicate and options are null.");
        }

        JSONObject requestBody = null;
        try {
            if (options != null) {
                requestBody =
                        JsonUtils.newJson(this.gson.toJson(options));
            } else {
                requestBody = new JSONObject();
            }

            requestBody.put("triggersWhat", TriggersWhat.COMMAND.name());
            if (predicate != null) {
                requestBody.put("predicate",
                        JsonUtils.newJson(this.gson.toJson(predicate, Predicate.class)));
            }
            if (form != null) {
                JSONObject command = JsonUtils.newJson(
                        this.gson.toJson(form));
                command.put("issuer", this.owner.getTypedID());
                if (form.getTargetID() == null) {
                    command.put("target", this.target.getTypedID().toString());
                }
                requestBody.put("command", command);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return this.patchTrigger(triggerID, requestBody);
    }

    /**
     * Apply patch to registered trigger.
     * Modify registered trigger with specified patch.
     * <p>
     * Limited version of {@link #patchCommandTrigger(String, TriggeredCommandForm, Predicate, TriggerOptions)}
     * <p>
     * @param triggerID ID of the trigger to apply patch.
     * @param form Form of triggered command. It contains name of schema,
     * version of schema, list of actions, target IDof thing etc. You can see
     * detail of form in {@link TriggeredCommandForm}.
     * @param predicate Modified predicate.
     * @return Updated trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException This exception is thrown if one or
     * more following conditions are met.
     * <ul>
     *  <li>triggerID is null or empty string.</li>
     *  <li>both form and predicate are null.</li>
     * </ul>
     */
    @NonNull
    @WorkerThread
    public Trigger patchCommandTrigger(
            @NonNull String triggerID,
            @Nullable TriggeredCommandForm form,
            @Nullable Predicate predicate)
            throws ThingIFException
    {
        return patchCommandTrigger(triggerID, form, predicate, null);
    }

    /**
     * Apply Patch to registered Trigger
     * Modify registered Trigger with specified patch.
     *
     * @param triggerID ID ot the Trigger to apply patch
     * @param serverCode Specify server code you want to execute.
     * @param predicate Modified predicate.
     * @param options option fileds of this trigger.
     * @return Updated Trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException This exception is thrown if one or
     * more following conditions are met.
     * <ul>
     *  <li>triggerID is null or empty string.</li>
     *  <li>all of serverCode, predicate and options are null.</li>
     * </ul>
     */
    @NonNull
    @WorkerThread
    public Trigger patchServerCodeTrigger(
            @NonNull String triggerID,
            @Nullable ServerCode serverCode,
            @Nullable Predicate predicate,
            @Nullable TriggerOptions options)
        throws ThingIFException
    {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }
        if (serverCode == null && predicate == null && options == null) {
            throw new IllegalArgumentException(
                    "serverCode, predicate and options are null.");
        }
        JSONObject requestBody = null;
        try {
            if (options != null) {
                requestBody = JsonUtils.newJson(
                        this.gson.toJson(options));
            } else {
                requestBody = new JSONObject();
            }
            if (predicate != null) {
                requestBody.put("predicate",
                        JsonUtils.newJson(
                                this.gson.toJson(predicate, Predicate.class)));
            }
            if (serverCode != null) {
                requestBody.put("serverCode",
                        JsonUtils.newJson(
                                this.gson.toJson(serverCode)));
            }
            requestBody.put("triggersWhat", TriggersWhat.SERVER_CODE.name());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return this.patchTrigger(triggerID, requestBody);
    }

    /**
     * Apply Patch to registered Trigger
     * Modify registered Trigger with specified patch.
     *
     * <p>
     * Limited version of {@link #patchServerCodeTrigger(String, ServerCode, Predicate,
     * TriggerOptions)}
     * <p>
     *
     * @param triggerID ID ot the Trigger to apply patch
     * @param serverCode Specify server code you want to execute. If null,
     * predicate must not be null.
     * @param predicate Modified predicate. If null, serverCode must not be
     * null.
     * @return Updated Trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException This exception is thrown if one or
     * more following conditions are met.
     * <ul>
     *  <li>triggerID is null or empty string.</li>
     *  <li>both serverCode and predicate are null.</li>
     * </ul>
     */
    @NonNull
    @WorkerThread
    public Trigger patchServerCodeTrigger(
            @NonNull String triggerID,
            @Nullable ServerCode serverCode,
            @Nullable Predicate predicate) throws ThingIFException {
        if (serverCode == null && predicate == null) {
            throw new IllegalArgumentException(
                "serverCode and predicate are null.");
        }
        return patchServerCodeTrigger(triggerID, serverCode, predicate, null);
    }

    private Trigger patchTrigger(@NonNull String triggerID, @NonNull JSONObject requestBody) throws ThingIFException {
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.app.getAppID(), this.target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PATCH, headers, MediaTypes.MEDIA_TYPE_JSON, requestBody);
        this.restClient.sendRequest(request);
        return this.getTrigger(triggerID);
    }

    /**
     * Enable/Disable registered Trigger
     * If its already enabled(/disabled),
     * this method won't throw Exception and behave as succeeded.
     * @param triggerID ID of the Trigger to be enabled(/disabled).
     * @param enable specify whether enable of disable the Trigger.
     * @return Updated Trigger Instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger enableTrigger(
            @NonNull String triggerID,
            boolean enable)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}/{3}", this.app.getAppID(), this.target.getTypedID().toString(), triggerID, (enable ? "enable" : "disable"));
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers);
        this.restClient.sendRequest(request);
        return this.getTrigger(triggerID);
    }

    /**
     * Delete the specified Trigger.
     * @param triggerID ID of the Trigger to be deleted.
     * @return Deleted Trigger Id.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public String deleteTrigger(
            @NonNull String triggerID) throws
            ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }


        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.app.getAppID(), target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.DELETE, headers);
        this.restClient.sendRequest(request);
        return triggerID;
    }

    /**
     * Retrieves list of server code results that was executed by the specified trigger. Results will be listing with order by modified date descending (latest first)
     * @param triggerID trigger ID to retrieve server code results.
     * @param bestEffortLimit limit the maximum number of the results in the
     *                        Response. It ensures numbers in
     *                        response is equals to or less than specified number.
     *                        But doesn't ensures number of the results
     *                        in the response is equal to specified value.<br>
     *                        If the specified value {@literal <}= 0, Default size of the limit
     *                        is applied by IoT Cloud.
     * @param paginationKey If specified obtain rest of the items.
     * @return first is list of the results and second is paginationKey returned
     * by IoT Cloud. paginationKey is null when there is next page to be obtained.
     * Obtained paginationKey can be used to get the rest of the items stored
     * in the target.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Pair<List<TriggeredServerCodeResult>, String> listTriggeredServerCodeResults (
            @NonNull String triggerID,
            int bestEffortLimit,
            @Nullable String paginationKey
    ) throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}/results/server-code", this.app.getAppID(), this.target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        if (bestEffortLimit > 0) {
            request.addQueryParameter("bestEffortLimit", bestEffortLimit);
        }
        if (!TextUtils.isEmpty(paginationKey)) {
            request.addQueryParameter("paginationKey", paginationKey);
        }

        JSONObject responseBody = this.restClient.sendRequest(request);
        String nextPaginationKey = responseBody.optString("nextPaginationKey", null);
        JSONArray resultArray = responseBody.optJSONArray("triggerServerCodeResults");

        List<TriggeredServerCodeResult> results = new ArrayList<>();
        if (resultArray != null) {
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject resultJson = resultArray.optJSONObject(i);
                results.add(this.gson.fromJson(resultJson.toString(), TriggeredServerCodeResult.class));
            }
        }
        return new Pair<>(results, nextPaginationKey);
    }

    /**
     * List Triggers belongs to the specified Target.<br>
     * If the Schema of the Trigger included in the response does not matches with the Schema
     * registered this ThingIfAPI instance, It won't be included in returned value.
     * @param bestEffortLimit limit the maximum number of the Triggers in the
     *                        Response. It ensures numbers in
     *                        response is equals to or less than specified number.
     *                        But doesn't ensures number of the Triggers
     *                        in the response is equal to specified value.<br>
     *                        If the specified value {@literal <}= 0, Default size of the limit
     *                        is applied by IoT Cloud.
     * @param paginationKey If specified obtain rest of the items.
     * @return first is list of the Triggers and second is paginationKey returned
     * by IoT Cloud. paginationKey is null when there is next page to be obtained.
     * Obtained paginationKey can be used to get the rest of the items stored
     * in the target.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     */
    @NonNull
    @WorkerThread
    public Pair<List<Trigger>, String> listTriggers(
            int bestEffortLimit,
            @Nullable String paginationKey) throws
            ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers", this.app.getAppID(), this.target.getTypedID().toString());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        if (bestEffortLimit > 0) {
            request.addQueryParameter("bestEffortLimit", bestEffortLimit);
        }
        if (!TextUtils.isEmpty(paginationKey)) {
            request.addQueryParameter("paginationKey", paginationKey);
        }

        JSONObject responseBody = this.restClient.sendRequest(request);
        String nextPaginationKey = responseBody.optString("nextPaginationKey", null);
        JSONArray triggerArray = responseBody.optJSONArray("triggers");
        List<Trigger> triggers = new ArrayList<>();
        try {
            if (triggerArray != null) {
                for (int i = 0; i < triggerArray.length(); i++) {
                    JsonObject triggerJson = new JsonParser()
                            .parse(triggerArray.optJSONObject(i).toString()).getAsJsonObject();
                    triggerJson.add("targetID", gson.toJsonTree(this.target.getTypedID()));
                    triggers.add(gson.fromJson(triggerJson, Trigger.class));
                }
            }
        }catch (JsonParseException ex) {
        if (ex.getCause() instanceof ThingIFException) {
            throw (ThingIFException) ex.getCause();
        } else {
            throw ex;
        }
    }
        return new Pair<>(triggers, nextPaginationKey);
    }

    /**
     * Get the state of specified target.
     * @return Map of alias and state instance.
     *  If state of target had never be updated, empty map is returned.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     */
    @NonNull
    @WorkerThread
    public Map<String, ? extends TargetState> getTargetState() throws ThingIFException{
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/states",
                this.app.getAppID(), this.target.getTypedID().toString());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

        JSONObject responseBody = this.restClient.sendRequest(request);
        Map retMap = new HashMap<>();
        Iterator it = responseBody.keys();
        while (it.hasNext()) {
            String key = (String)it.next();
            if (!this.stateTypes.containsKey(key)) {
                throw new UnregisteredAliasException(key, false);
            }
            try {
                JsonObject object = new JsonParser()
                        .parse(responseBody.getJSONObject(key).toString()).getAsJsonObject();
                retMap.put(key, this.gson.fromJson(object, this.stateTypes.get(key)));
            } catch (JSONException e) {
                throw new ThingIFException("Unexpected exception.");
            }
        }
        return retMap;
    }
    /**
     * Get the State of specified alias.
     * State will be serialized with Gson library.
     * @param alias Specify alias to get state.
     * @param targetStateClass Class of S.
     * @param <S> Class implements TargetState interface
     * @return Instance of Target State.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws ClassCastException Thrown when S is different with registered class for this alias.
     * @throws UnregisteredAliasException Thrown when alias cannot be handled.
     * @throws IllegalArgumentException Thrown when targetStateClass is null.
     */
    @NonNull
    @WorkerThread
    public <S extends TargetState> S getTargetState(
            @NonNull String alias,
            @NonNull Class<S> targetStateClass) throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (!this.stateTypes.containsKey(alias)) {
            throw new UnregisteredAliasException(alias, false);
        }
        if (targetStateClass == null) {
            throw new IllegalArgumentException("targetStateClass is null");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/states/aliases/{2}",
                this.app.getAppID(), this.target.getTypedID().toString(), alias);
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);

        JSONObject responseBody = this.restClient.sendRequest(request);
        JsonObject object = new JsonParser().parse(responseBody.toString()).getAsJsonObject();
        return targetStateClass.cast(this.gson.fromJson(object, this.stateTypes.get(alias)));
    }

    /**
     * Get the Vendor Thing ID of specified Target.
     *
     * @return Vendor Thing ID
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     */
    @NonNull
    @WorkerThread
    public String getVendorThingID() throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        String path = MessageFormat.format("/api/apps/{0}/things/{1}/vendor-thing-id", this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        return responseBody.optString("_vendorThingID", null);
    }

    /**
     * Update the Vendor Thing ID of specified Target.
     *
     * @param newVendorThingID New vendor thing id
     * @param newPassword New password
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     */
    @WorkerThread
    public void updateVendorThingID(@NonNull String newVendorThingID, @NonNull String newPassword) throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(newPassword)) {
            throw new IllegalArgumentException("newPassword is null or empty");
        }
        if (TextUtils.isEmpty(newVendorThingID)) {
            throw new IllegalArgumentException("newVendorThingID is null or empty");
        }
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("_vendorThingID", newVendorThingID);
            requestBody.put("_password", newPassword);
        } catch (JSONException e) {
            // Won’t happen
        }

        String path = MessageFormat.format("/api/apps/{0}/things/{1}/vendor-thing-id", this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers, MediaTypes.MEDIA_TYPE_VENDOR_THING_ID_UPDATE_REQUEST, requestBody);
        this.restClient.sendRequest(request);
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
    /**
     * Get base URL
     * @return base URL
     */
    @NonNull
    public String getBaseUrl() {
        return this.app.getBaseUrl();
    }
    /**
     * Get owner who uses the ThingIFAPI.
     * @return owner
     */
    @NonNull
    public Owner getOwner() {
        return this.owner;
    }

    /**
     * Get target thing that is operated by the ThingIFAPI.
     * @return target of this ThingIFAPI.
     */
    @Nullable
    public Target getTarget() {
        return this.target;
    }
    /**
     * Get a tag.
     * @return tag.
     */
    @Nullable
    public String getTag() {
        return this.tag;
    }

    /**
     * Get actionTypes of this ThingIFAPI instance.
     * @return actionTypes.
     */
    public Map<String, Class<? extends Action>> getActionTypes() {
        return this.actionTypes;
    }

    /**
     * Get stateTypes of this ThingIFAPI instance.
     * @return stateTypes.
     */
    public Map<String, Class<? extends TargetState>> getStateTypes() {
        return this.stateTypes;
    }

    private Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.getAppID())) {
            headers.put("X-Kii-AppID", this.getAppID());
        }
        if (!TextUtils.isEmpty(this.getAppKey())) {
            headers.put("X-Kii-AppKey", this.getAppKey());
        }
        if (this.owner != null && !TextUtils.isEmpty(this.owner.getAccessToken())) {
            headers.put("Authorization", "Bearer " + this.owner.getAccessToken());
        }
        return headers;
    }
    private JSONObject createPostNewCommandRequestBody(CommandForm src) throws ThingIFException {
        JSONObject ret = JsonUtils.newJson(this.gson.toJson(src));
        try {
            ret.put("issuer", this.owner.getTypedID().toString());
        } catch (JSONException e) {
            throw new AssertionError(e);
        }
        return ret;
    }
//    private <T> T deserialize(JSONObject json, Class<T> clazz) throws ThingIFException {
//        return this.deserialize(null, json, clazz);
//    }
//    private <T> T deserialize(JSONObject json, Class<T> clazz) throws ThingIFException {
//        return this.deserialize(schema, json.toString(), clazz);
//    }
//    private Trigger deserialize(JSONObject json, TypedID targetID) throws ThingIFException {
//        JSONObject copied = null;
//        try {
//            copied = new JSONObject(json.toString());
//            copied.put("targetID", targetID.toString());
//        } catch (JSONException e) {
//            throw new ThingIFException("unexpected error.", e);
//        }
//        return this.deserialize(schema, copied.toString(), Trigger.class);
//    }
//    private <T> T deserialize(Schema schema, String json, Class<T> clazz) throws ThingIFException {
//        try {
//            return GsonRepository.gson(schema).fromJson(json, clazz);
//        } catch (JsonParseException e) {
//            if (e.getCause() instanceof ThingIFException) {
//                throw (ThingIFException)e.getCause();
//            }
//            throw e;
//        }
//    }
    private static SharedPreferences getSharedPreferences() {
        if (context != null) {
            return context.getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        }
        return null;
    }

    // Implementation of Parcelable
    protected ThingIFAPI(Parcel in) {
        this.tag = in.readString();
        this.app = in.readParcelable(KiiApp.class.getClassLoader());
        this.owner = in.readParcelable(Owner.class.getClassLoader());
        this.target = in.readParcelable(Target.class.getClassLoader());
        this.restClient = new IoTRestClient();
        this.installationID = in.readString();

        //TODO: // FIXME: 12/20/16 read the register action classes
        int size = in.readInt();
        this.actionTypes = new HashMap<>(size);
        //TODO: // FIXME: 12/20/16 read the registered targetState classes
        int size2 = in.readInt();
        this.stateTypes = new HashMap<>(size2);

    }
    public static final Creator<ThingIFAPI> CREATOR = new Creator<ThingIFAPI>() {
        @Override
        public ThingIFAPI createFromParcel(Parcel in) {
            return new ThingIFAPI(in);
        }

        @Override
        public ThingIFAPI[] newArray(int size) {
            return new ThingIFAPI[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
        dest.writeParcelable(this.app, flags);
        dest.writeParcelable(this.owner, flags);
        dest.writeParcelable(this.target, flags);
        dest.writeString(this.installationID);
        //TODO: // FIXME: 12/20/16 implement write registered action, actionResult, targetState classes
    }

    /**
     * Get version of the SDK.
     * @return Version string.
     */
    @NonNull
    public static String getSDKVersion() {
        return SDKVersion.versionString;
    }
    

    /**
     * Update thingType to using trait for the thing .
     * @param thingType Name of thing type, which should be already defined.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if thingType is null.
     * @throws ConflictException Thrown when a thing type was already configured.
     */
    @WorkerThread
    public void updateThingType(
            @NonNull String thingType) throws ThingIFException{
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(thingType)) {
            throw new IllegalArgumentException("thingType is null or empty");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/things/{1}/thing-type",
                this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("thingType", thingType);
        } catch (JSONException e) {
            // Unexpected.
            throw new RuntimeException(e);
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers,
                MediaTypes.MEDIA_TYPE_THING_TYPE_UPDATE_REQUEST, requestBody);
        this.restClient.sendRequest(request);
    }

    /**
     * Update firmware version of the thing
     * @param firmwareVersion New firmware version.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException if firmwareVersion is null or empty.
     */
    @WorkerThread
    public void updateFirmwareVersion(
            @NonNull String firmwareVersion) throws ThingIFException{
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(firmwareVersion)) {
            throw new IllegalArgumentException("firmwareVersion is null or empty");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/things/{1}/firmware-version",
                this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("firmwareVersion", firmwareVersion);
        } catch (JSONException e) {
            // Unexpected.
            throw new RuntimeException(e);
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers,
                MediaTypes.MEDIA_TYPE_THING_FIRMWARE_VERSION_UPDATE_REQUEST, requestBody);
        this.restClient.sendRequest(request);
    }

    /**
     * Get thing type of the thing.
     * @return Name of thing type of the thing. If thing type is not set, null is returned.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     */
    @Nullable
    @WorkerThread
    public String getThingType() throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/things/{1}/thing-type",
                this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody;
        try {
            responseBody = this.restClient.sendRequest(request);
        } catch (NotFoundException e) {
            String code = e.getErrorCode();
            if ("THING_WITHOUT_THING_TYPE".equals(code)) {
                return null;
            }
            throw e;
        }
        return responseBody.optString("thingType", null);
    }

    /**
     * Get firmware version of the thing.
     * @return Firmware version of the thing. If firmware version is not set, null is returned.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     */
    @Nullable
    @WorkerThread
    public String getFirmwareVersion() throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/things/{1}/firmware-version",
                this.app.getAppID(), this.target.getTypedID().getID());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody;
        try {
            responseBody = this.restClient.sendRequest(request);
        } catch (NotFoundException e) {
            String code = e.getErrorCode();
            // TODO: When server response fixed, change to FIRMWARE_VERSION_NOT_FOUND.
            if ("THING_WITHOUT_THING_TYPE".equals(code)) {
                return null;
            }
            throw e;
        }
        return responseBody.optString("firmwareVersion", null);
    }

    /**
     * Query history states.
     * @param query Instance of {@link HistoryStatesQuery}.
     * @param targetStateClass class of S, used to verify with registered target state class for the
     *                         specified alias
     * @param <S> Type of subclass of {@link TargetState}.
     * @return Pair instance. First element is list of target state.
     *  Second element is next pagination key.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnregisteredAliasException Thrown when the returned response contains alias that cannot be handled.
     * @throws ClassCastException Thrown when targetStateClass is different with registered target state
     * class for the specified alias.
     * @throws IllegalArgumentException Thrown when any of query and targetStateClass is/are null.
     */
    public <S extends TargetState> Pair<List<HistoryState<S>>, String> query(
            @NonNull HistoryStatesQuery query,
            @NonNull Class<S> targetStateClass) throws ThingIFException{
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (query == null) {
            throw new IllegalArgumentException("query is null");
        }
        if (targetStateClass == null) {
            throw new IllegalArgumentException("targetStateClass is null");
        }

        if (!this.stateTypes.containsKey(query.getAlias())) {
            throw new UnregisteredAliasException(query.getAlias(), false);
        }

        Class<? extends TargetState> storedStateClass = this.stateTypes.get(query.getAlias());

        if (!storedStateClass.equals(targetStateClass)) {
            throw new ClassCastException("registered target state class is different with " +
                    "targetStateClass parameter");
        }

        String path =  MessageFormat.format("/thing-if/apps/{0}/targets/{1}/states/aliases/{2}/query",
                this.app.getAppID(), this.target.getTypedID().toString(), query.getAlias());
        String url = Path.combine(this.app.getBaseUrl(), path);
        Map<String, String> headers = this.newHeader();

        JSONObject requestBody =
                JsonUtils.newJson(this.gson.toJson(query, HistoryStatesQuery.class));

        JSONObject clauseObject =
                JsonUtils.newJson(this.gson.toJson(query.getClause(), QueryClause.class));
        try {
            requestBody.put(
                    "query",
                    new JSONObject().put("clause", clauseObject));
        }catch (JSONException e) {
            // never happen
            throw new RuntimeException(e);
        }

        IoTRestRequest request = new IoTRestRequest(
                url,
                IoTRestRequest.Method.POST,
                headers,
                MediaTypes.MEDIA_TYPE_TRAIT_STATE_QUERY_REQUEST,
                requestBody);

        JSONObject responseBody;
        List<HistoryState<S>> states = new ArrayList<>();

        try {
            responseBody = this.restClient.sendRequest(request);
        }catch (ConflictException e) {
            // when thing never update its state to server, server returns this error.
            if (e.getErrorCode() != null && e.getErrorCode().equals("STATE_HISTORY_NOT_AVAILABLE")) {
                return new Pair<>(states, null);
            } else {
                throw e;
            }
        }
        String nextPaginationKey = responseBody.optString("nextPaginationKey", null);
        JSONArray statesArray = responseBody.optJSONArray("results");


        if (statesArray != null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(HistoryState.class, new HistoryStateAdapter(targetStateClass))
                    .create();
            Type historyStateType = new TypeToken<HistoryState<S>>(){}.getType();
            for (int i = 0; i < statesArray.length(); i++) {
                JSONObject stateJson = statesArray.optJSONObject(i);
                HistoryState<S> historyState = gson.fromJson(stateJson.toString(), historyStateType);
                states.add(historyState);
            }
        }
        return new Pair<>(states, nextPaginationKey);
    }

    /**
     * Group history state
     * @param query {@link GroupedHistoryStatesQuery} instance. timeRange in query should less than
     *                                               60 data grouping intervals.
     * @param targetStateClass class of S, used to verify with registered target state class for the
     *                         specified alias
     * @param <S> Type of subclass of {@link TargetState}.
     * @return List of {@link GroupedHistoryStates} instances.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws BadRequestException Thrown if timeRange of query is over 60 data grouping intervals.
     * @throws ClassCastException Thrown when targetStateClass is different with registered target
     * class for the specified alias.
     */
    public <S extends TargetState> List<GroupedHistoryStates<S>> query(
            @NonNull GroupedHistoryStatesQuery query,
            @NonNull Class<S> targetStateClass) throws ThingIFException{
        //TODO: // FIXME: 12/21/16 implement the logic
        return new ArrayList<>();
    }

    /**
     * Aggregate history states
     * @param groupedQuery {@link GroupedHistoryStatesQuery} instance. timeRange in query should less than
     *                                               60 data grouping intervals.
     * @param aggregation {@link Aggregation} instance.
     * @param targetStateClass class of S, used to verify with registered target state class for the
     *                         specified alias
     * @param valueClass Class of {@link AggregatedResult#value}.
     * @param <T> Type of {@link AggregatedResult#value}.
     * @param <S> Type of subclass of {@link TargetState}.
     * @return List of {@link AggregatedResult} instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws BadRequestException Thrown if timeRange of query is over 60 data grouping intervals.
     * @throws ClassCastException Thrown when targetStateClass is different with registered target
     * class for the specified alias.
     */
    public <T extends Number, S extends TargetState> List<AggregatedResult<T, S>> aggregate(
            @NonNull GroupedHistoryStatesQuery groupedQuery,
            @NonNull Aggregation aggregation,
            @NonNull Class<S> targetStateClass,
            @NonNull Class<T> valueClass) throws ThingIFException {
        //TODO: // FIXME: 12/21/16 implement the logic
        return new ArrayList<>();
    }
}
