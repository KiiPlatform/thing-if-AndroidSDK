package com.kii.thingif;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.kii.thingif.exception.StoredThingIFAPIInstanceNotFoundException;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.exception.ThingIFRestException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.PendingEndNode;
import com.kii.thingif.internal.GsonRepository;

public class TraitThingIFAPI implements Parcelable{
    private ThingIFAPI thingIfApi;
    private static final String SHARED_PREFERENCES_KEY_INSTANCE = "TraitThingIFAPI_INSTANCE";
    private static Context context;
    private final String tag;

    TraitThingIFAPI(ThingIFAPI thingIfApi){
        this.thingIfApi = thingIfApi;
        tag = null;
    }

    /**
     * Try to load the instance of TraitThingIFAPI using stored serialized instance.
     * <BR>
     * Instance is automatically saved when following methods are called.
     * <BR>
     * {@link #onboard(String, String)},
     * {@link #copyWithTarget(Target, String)}
     * and {@link #installPush} has been successfully completed.
     * <BR>
     * (When {@link #copyWithTarget(Target, String)} is called, only the copied instance is saved.)
     * <BR>
     * <BR>
     *
     * If the TraitThingIFAPI instance is build without the tag, all instance is saved in same place
     * and overwritten when the instance is saved.
     * <BR>
     * <BR>
     *
     * If the TraitThingIFAPI instance is build with the tag(optional), tag is used as key to distinguish
     * the storage area to save the instance. This would be useful to saving multiple instance.
     * You need specify tag to load the instance by the
     * {@link #loadFromStoredInstance(Context, String) api}.
     *
     * @param context context
     * @return ThingIFAPI instance.
     * @throws StoredThingIFAPIInstanceNotFoundException when the instance has not stored yet.
     */
    @NonNull
    public static TraitThingIFAPI loadFromStoredInstance(@NonNull Context context) throws StoredThingIFAPIInstanceNotFoundException {
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
     * @throws StoredThingIFAPIInstanceNotFoundException when the instance has not stored yet.
     */
    @NonNull
    public static TraitThingIFAPI loadFromStoredInstance(@NonNull Context context, String tag) throws StoredThingIFAPIInstanceNotFoundException {
        TraitThingIFAPI.context = context.getApplicationContext();
        SharedPreferences preferences = getSharedPreferences();
        String serializedJson = preferences.getString(getSharedPreferencesKey(tag), null);
        if (serializedJson != null) {
            return  GsonRepository.gson().fromJson(serializedJson, TraitThingIFAPI.class);
        }
        throw new StoredThingIFAPIInstanceNotFoundException(tag);
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
        editor.remove(getSharedPreferencesKey(tag));
        editor.apply();
    }
    private static void saveInstance(TraitThingIFAPI instance) {
        SharedPreferences preferences = getSharedPreferences();
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(getSharedPreferencesKey(instance.tag), GsonRepository.gson().toJson(instance));
            editor.apply();
        }
    }
    private static String getSharedPreferencesKey(String tag) {
        return SHARED_PREFERENCES_KEY_INSTANCE + (tag == null ? "" : "_"  +tag);
    }

    TraitThingIFAPI(
            @Nullable Context context,
            @Nullable String tag,
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @Nullable Target target,
            String installationID) {
        // Parameters are checked by TraitThingIFAPIBuilder
        if (context != null) {
            TraitThingIFAPI.context = context.getApplicationContext();
        }
        this.tag = tag;
        this.thingIfApi = ThingIFAPIBuilder
                .newBuilder(context, app, owner)
                .setTag(tag)
                .setTarget(target)
                .setInstallationID(installationID)
                .build();
    }
    /**
     * Create the clone instance that has specified target and tag.
     *
     * @param target coping target.
     * @param tag A key to store instnace.
     * @return ThingIFAPI instance
     */
    public TraitThingIFAPI copyWithTarget(@NonNull Target target, @Nullable String tag) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }
        TraitThingIFAPI api = new TraitThingIFAPI(this.thingIfApi.copyWithTarget(target, tag));
        saveInstance(api);
        return api;
    }

    /**
     * On board IoT Cloud with the specified vendor thing ID.
     * Specified thing will be owned by owner who is specified
     * IoT Cloud prepares communication channel to the target.
     * If you are using a gateway, you need to use {@link #onboardEndnodeWithGateway(PendingEndNode, String)} instead.
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
    public Target onboard(
            @NonNull String vendorThingID,
            @NonNull String thingPassword,
            @Nullable OnboardWithVendorThingIDOptions options)
            throws ThingIFException {
        return thingIfApi.onboard(vendorThingID, thingPassword, options);
    }

    /**
     * On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * If you are using a gateway, you need to use {@link #onboardEndnodeWithGateway(PendingEndNode, String)} instead.
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
    public Target onboard(
            @NonNull String thingID,
            @NonNull String thingPassword) throws
            ThingIFException {
        return thingIfApi.onboard(thingID, thingPassword);
    }

    /**
     * On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * {@link #onboard(String, String, OnboardWithVendorThingIDOptions)}.
     * If you are using a gateway, you need to use {@link #onboardEndnodeWithGateway(PendingEndNode, String)} instead.
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
    public Target onboard(
            @NonNull String thingID,
            @NonNull String thingPassword,
            @Nullable OnboardWithThingIDOptions options)
            throws ThingIFException {
        return thingIfApi.onboard(thingID, thingPassword, options);
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
    public EndNode onboardEndnodeWithGateway(
            @NonNull PendingEndNode pendingEndNode,
            @NonNull String endnodePassword)
            throws ThingIFException {
        return thingIfApi.onboardEndnodeWithGateway(pendingEndNode, endnodePassword);
    }

    /**
     * Endpoints execute onboarding for the thing and merge MQTT channel to the gateway.
     * Thing act as Gateway is already registered and marked as Gateway.
     *
     * @param pendingEndNode Pending endnode
     * @param endnodePassword Password of the End Node
     * @param options optional parameters inside.
     * @return Target instance can be used to operate target, manage resources of the target.
     * @throws IllegalStateException Thrown when this instance is already onboarded.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    public EndNode onboardEndnodeWithGateway(
            @NonNull PendingEndNode pendingEndNode,
            @NonNull String endnodePassword,
            @Nullable OnboardEndnodeWithGatewayOptions options)
            throws ThingIFException {
        return thingIfApi.onboardEndnodeWithGateway(pendingEndNode, endnodePassword, options);
    }

    /**
     * Checks whether on boarding is done.
     * @return true if done, otherwise false.
     */
    public boolean onboarded()
    {
        return thingIfApi.onboarded();
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
        return thingIfApi.installPush(deviceToken, pushBackend);
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
        return thingIfApi.installPush(deviceToken, pushBackend, development);
    }
    /**
     * Get installationID if the push is already installed.
     * null will be returned if the push installation has not been done.
     * @return Installation ID used in IoT Cloud.
     */
    @Nullable
    public String getInstallationID() {
        return thingIfApi.getInstallationID();
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
        thingIfApi.uninstallPush(installationID);
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
        return thingIfApi.getVendorThingID();
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
        thingIfApi.updateVendorThingID(newVendorThingID, newPassword);
    }

    /** Get Kii App
     * @return Kii Cloud Application.
     */
    @NonNull
    public KiiApp getApp() {
        return thingIfApi.getApp();
    }
    /**
     * Get AppID
     * @return app ID
     */
    @NonNull
    public String getAppID() {
        return thingIfApi.getAppID();
    }
    /**
     * Get AppKey
     * @return app key
     */
    @NonNull
    public String getAppKey() {
        return thingIfApi.getAppKey();
    }
    /**
     * Get base URL
     * @return base URL
     */
    @NonNull
    public String getBaseUrl() {
        return thingIfApi.getBaseUrl();
    }
    /**
     * Get owner who uses the ThingIFAPI.
     * @return owner
     */
    @NonNull
    public Owner getOwner() {
        return thingIfApi.getOwner();
    }

    /**
     * Get target thing that is operated by the ThingIFAPI.
     * @return target of this ThingIFAPI.
     */
    @Nullable
    public Target getTarget() {
        return thingIfApi.getTarget();
    }
    /**
     * Get a tag.
     * @return tag.
     */
    @Nullable
    public String getTag() {
        return this.tag;
    }

    // Implementation of Parcelable
    protected TraitThingIFAPI(Parcel in) {
        this.tag = in.readString();
        this.thingIfApi = in.readParcelable(TraitThingIFAPI.class.getClassLoader());
    }
    public static final Parcelable.Creator<TraitThingIFAPI> CREATOR = new Parcelable.Creator<TraitThingIFAPI>() {
        @Override
        public TraitThingIFAPI createFromParcel(Parcel in) {
            return new TraitThingIFAPI(in);
        }

        @Override
        public TraitThingIFAPI[] newArray(int size) {
            return new TraitThingIFAPI[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tag);
        dest.writeParcelable(this.thingIfApi, flags);
    }

    /**
     * Get version of the SDK.
     * @return Version string.
     */
    @NonNull
    public static String getSDKVersion() {
        return ThingIFAPI.getSDKVersion();
    }

    private static SharedPreferences getSharedPreferences() {
        if (context != null) {
            return context.getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        }
        return null;
    }

}
