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

import com.google.gson.JsonParseException;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.exception.ThingIFRestException;
import com.kii.thingif.exception.StoredThingIFAPIInstanceNotFoundException;
import com.kii.thingif.exception.UnsupportedActionException;
import com.kii.thingif.exception.UnsupportedSchemaException;
import com.kii.thingif.internal.GsonRepository;
import com.kii.thingif.internal.http.IoTRestClient;
import com.kii.thingif.internal.http.IoTRestRequest;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.internal.utils.JsonUtils;
import com.kii.thingif.internal.utils.Path;
import com.squareup.okhttp.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class operates an IoT device that is specified by {@link #onboard(String, String, String, JSONObject)} method.
 */
public class ThingIFAPI implements Parcelable {

    private static final String SHARED_PREFERENCES_KEY_INSTANCE = "ThingIFAPI_INSTANCE";
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");
    private static final MediaType MEDIA_TYPE_INSTALLATION_CREATION_REQUEST = MediaType.parse("application/vnd.kii.InstallationCreationRequest+json");
    private static final MediaType MEDIA_TYPE_ONBOARDING_WITH_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithThingIDByOwner+json");
    private static final MediaType MEDIA_TYPE_ONBOARDING_WITH_VENDOR_THING_ID_BY_OWNER_REQUEST = MediaType.parse("application/vnd.kii.OnboardingWithVendorThingIDByOwner+json");

    private static Context context;
    private final String tag;
    private final String appID;
    private final String appKey;
    private final String baseUrl;
    private final Owner owner;
    private Target target;
    private final Map<Pair<String, Integer>, Schema> schemas = new HashMap<Pair<String, Integer>, Schema>();
    private final IoTRestClient restClient;
    private String installationID;

    /**
     * Try to load the instance of ThingIFAPI using stored serialized instance.
     *
     * @param context
     * @return ThingIFAPI instance.
     * @throws IllegalStateException Thrown when the instance has not stored.
     */
    public static ThingIFAPI loadFromStoredInstance(@NonNull Context context) throws StoredThingIFAPIInstanceNotFoundException {
        return loadFromStoredInstance(context, null);
    }
    /**
     * Try to load the instance of ThingIFAPI using stored serialized instance.
     *
     * @param context
     * @param  tag
     * @return ThingIFAPI instance.
     * @throws IllegalStateException Thrown when the instance has not stored.
     */
    public static ThingIFAPI loadFromStoredInstance(@NonNull Context context, String tag) throws StoredThingIFAPIInstanceNotFoundException {
        ThingIFAPI.context = context.getApplicationContext();
        SharedPreferences preferences = getSharedPreferences();
        String serializedJson = preferences.getString(getSharedPreferencesKey(tag), null);
        if (serializedJson != null) {
            return GsonRepository.gson().fromJson(serializedJson, ThingIFAPI.class);
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
        editor.commit();
    }
    /**
     * Remove saved specified instance in the SharedPreferences.
     *
     * @param tag
     */
    public static void removeStoredInstance(String tag) {
        SharedPreferences preferences = getSharedPreferences();
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(getSharedPreferencesKey(tag));
        editor.commit();
    }
    private static void saveInstance(ThingIFAPI instance) {
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

    ThingIFAPI(
            @Nullable Context context,
            @Nullable String tag,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull String baseUrl,
            @NonNull Owner owner,
            @Nullable Target target,
            @NonNull List<Schema> schemas,
            String installationID) {
        // Parameters are checked by ThingIFAPIBuilder
        if (context != null) {
            ThingIFAPI.context = context.getApplicationContext();
        }
        this.tag = tag;
        this.appID = appID;
        this.appKey = appKey;
        this.baseUrl = baseUrl;
        this.owner = owner;
        this.target = target;
        for (Schema schema : schemas) {
            this.schemas.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), schema);
        }
        this.installationID = installationID;
        this.restClient = new IoTRestClient();
    }
    /**
     * Create the clone instance that has specified target and tag.
     *
     * @param target
     * @param tag
     * @return ThingIFAPI instance
     */
    public ThingIFAPI copyWithTarget(@NonNull Target target, @Nullable String tag) {
        if (target == null) {
            throw new IllegalArgumentException("target is null");
        }
        ThingIFAPI api = new ThingIFAPI(context, tag, this.appID, this.appKey, this.baseUrl, this.owner, target, new ArrayList<Schema>(this.schemas.values()), this.installationID);
        saveInstance(api);
        return api;
    }

    /**
     * On board IoT Cloud with the specified vendor thing ID.
     * Specified thing will be owned by owner who is specified
     * IoT Cloud prepares communication channel to the target.
     * @param vendorThingID Thing ID given by vendor. Must be specified.
     * @param thingPassword Thing Password given by vendor. Must be specified.
     * @param thingType Type of the thing given by vendor.
     *                  If the thing is already registered, this value would be
     *                  ignored by IoT Cloud.
     * @param thingProperties Properties of thing.
     *                        If the thing is already registered, this value
     *                        would be ignored by IoT Cloud.<br>
     *                        Refer to the <a href="http://docs.kii.com/rest/#thing_management-register_a_thin">register_a_thing</a>
     *                        About the format of this Document.
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
            @Nullable String thingType,
            @Nullable JSONObject thingProperties)
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
        try {
            requestBody.put("vendorThingID", vendorThingID);
            requestBody.put("thingPassword", thingPassword);
            if (thingType != null) {
                requestBody.put("thingType", thingType);
            }
            if (thingProperties != null && thingProperties.length() > 0) {
                requestBody.put("thingProperties", thingProperties);
            }
            requestBody.put("owner", this.owner.getTypedID().toString());
        } catch (JSONException e) {
            // Won’t happen
        }
        return this.onboard(MEDIA_TYPE_ONBOARDING_WITH_VENDOR_THING_ID_BY_OWNER_REQUEST, requestBody);
    }

    /**
     * On board IoT Cloud with the specified thing ID.
     * When you are sure that the on boarding process has been done,
     * this method is more convenient than
     * {@link #onboard(String, String, String, JSONObject)}.
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
        try {
            requestBody.put("thingID", thingID);
            requestBody.put("thingPassword", thingPassword);
            requestBody.put("owner", this.owner.getTypedID().toString());
        } catch (JSONException e) {
            // Won’t happen
        }
        return this.onboard(MEDIA_TYPE_ONBOARDING_WITH_THING_ID_BY_OWNER_REQUEST, requestBody);
    }

    private Target onboard(MediaType contentType, JSONObject requestBody) throws ThingIFException {
        String path = MessageFormat.format("/thing-if/apps/{0}/onboardings", this.appID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, contentType, requestBody);
        JSONObject responseBody = this.restClient.sendRequest(request);
        String thingID = responseBody.optString("thingID", null);
        String accessToken = responseBody.optString("accessToken", null);
        this.target = new Target(new TypedID(TypedID.Types.THING, thingID), accessToken);
        saveInstance(this);
        return this.target;
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
        return this.installPush(deviceToken,pushBackend,false);
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

        String path = MessageFormat.format("/api/apps/{0}/installations", this.appID);
        String url = Path.combine(this.baseUrl, path);
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
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_INSTALLATION_CREATION_REQUEST, requestBody);
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
    void setInstallationID(String installationID) {
        this.installationID = installationID;
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
        String path = MessageFormat.format("/api/apps/{0}/installations/{1}", this.appID, installationID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.DELETE, headers);
        this.restClient.sendRequest(request);
    }

    /**
     * Post new command to IoT Cloud.
     * Command will be delivered to specified target and result will be notified
     * through push notification.
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Actions to be executed.
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
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull List<Action> actions) throws ThingIFException {
        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands", this.appID, this.target.getTypedID().toString());
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        Command command = new Command(schemaName, schemaVersion, this.owner.getTypedID(), actions);
        JSONObject requestBody = JsonUtils.newJson(GsonRepository.gson(schema).toJson(command));
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
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
     * @throws UnsupportedSchemaException Thrown when the returned response has a schema that cannot handle this instance.
     * @throws UnsupportedActionException Thrown when the returned response has a action that cannot handle this instance.
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
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands/{2}", this.appID, this.target.getTypedID().toString(), commandID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        String schemaName = responseBody.optString("schema", null);
        int schemaVersion = responseBody.optInt("schemaVersion");
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        return this.deserialize(schema, responseBody, Command.class);
    }
    /**
     * List Commands in the specified Target.
     * @param bestEffortLimit Maximum number of the Commands in the response.
     *                        if the value is <= 0, default limit internally
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
     * @throws UnsupportedSchemaException Thrown when the returned response has a schema that cannot handle this instance.
     * @throws UnsupportedActionException Thrown when the returned response has a action that cannot handle this instance.
     */
    public Pair<List<Command>, String> listCommands (
            int bestEffortLimit,
            @Nullable String paginationKey)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/commands", this.appID, this.target.getTypedID().toString());
        String url = Path.combine(this.baseUrl, path);
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
        List<Command> commands = new ArrayList<Command>();
        if (commandArray != null) {
            for (int i = 0; i < commandArray.length(); i++) {
                JSONObject commandJson = commandArray.optJSONObject(i);
                String schemaName = commandJson.optString("schema", null);
                int schemaVersion = commandJson.optInt("schemaVersion");
                Schema schema = this.getSchema(schemaName, schemaVersion);
                if (schema == null) {
                    throw new UnsupportedSchemaException(schemaName, schemaVersion);
                }
                commands.add(this.deserialize(schema, commandJson, Command.class));
            }
        }
        return new Pair<List<Command>, String>(commands, nextPaginationKey);
    }

    /**
     * Post new Trigger to IoT Cloud.
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Specify actions included in the Command is fired by the
     *                trigger.
     * @param predicate Specify when the Trigger fires command.
     * @return Instance of the Trigger registered in IoT Cloud.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger postNewTrigger(
            @NonNull String schemaName,
            int schemaVersion,
            @NonNull List<Action> actions,
            @NonNull Predicate predicate)
            throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate is null");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers", this.appID, this.target.getTypedID().toString());
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        Schema schema = this.getSchema(schemaName, schemaVersion);
        Command command = new Command(schemaName, schemaVersion, this.target.getTypedID(), this.owner.getTypedID(), actions);
        try {
            requestBody.put("predicate", JsonUtils.newJson(GsonRepository.gson(schema).toJson(predicate)));
            requestBody.put("triggersWhat", "COMMAND");
            requestBody.put("command", JsonUtils.newJson(GsonRepository.gson(schema).toJson(command)));
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.POST, headers, MEDIA_TYPE_JSON, requestBody);
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
     * @throws UnsupportedSchemaException Thrown when the returned response has a schema that cannot handle this instance.
     * @throws UnsupportedActionException Thrown when the returned response has a action that cannot handle this instance.
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

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.appID, this.target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);

        JSONObject commandObject = responseBody.optJSONObject("command");
        String schemaName = commandObject.optString("schema", null);
        int schemaVersion = commandObject.optInt("schemaVersion");
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        return this.deserialize(schema, responseBody, Trigger.class);
    }

    /**
     * Apply Patch to registered Trigger
     * Modify registered Trigger with specified patch.
     * @param triggerID ID ot the Trigger to apply patch
     * @param schemaName name of the schema.
     * @param schemaVersion version of schema.
     * @param actions Modified actions.
     *                If null NonNull predicate must be specified.
     * @param predicate Modified predicate.
     *                  If null NonNull actions must be specified.
     * @return Updated Trigger instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws IllegalArgumentException when both actions and predicates are null
     */
    @NonNull
    @WorkerThread
    public Trigger patchTrigger(
            @NonNull String triggerID,
            @NonNull String schemaName,
            int schemaVersion,
            @Nullable List<Action> actions,
            @Nullable Predicate predicate) throws
            ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }
        if (TextUtils.isEmpty(schemaName)) {
            throw new IllegalArgumentException("schemaName is null or empty");
        }
        if (actions == null || actions.size() == 0) {
            throw new IllegalArgumentException("actions is null or empty");
        }
        if (predicate == null) {
            throw new IllegalArgumentException("predicate is null");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.appID, this.target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        JSONObject requestBody = new JSONObject();
        Schema schema = this.getSchema(schemaName, schemaVersion);
        Command command = new Command(schemaName, schemaVersion, this.target.getTypedID(), this.owner.getTypedID(), actions);
        try {
            requestBody.put("predicate", JsonUtils.newJson(GsonRepository.gson(schema).toJson(predicate)));
            requestBody.put("command", JsonUtils.newJson(GsonRepository.gson(schema).toJson(command)));
        } catch (JSONException e) {
            // Won’t happen
        }
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PATCH, headers, MEDIA_TYPE_JSON, requestBody);
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
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}/{3}", this.appID, this.target.getTypedID().toString(), triggerID, (enable ? "enable" : "disable"));
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.PUT, headers);
        this.restClient.sendRequest(request);
        return this.getTrigger(triggerID);
    }

    /**
     * Delete the specified Trigger.
     * @param triggerID ID of the Trigger to be deleted.
     * @return Deleted Trigger Instance.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public Trigger deleteTrigger(
            @NonNull String triggerID) throws
            ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (TextUtils.isEmpty(triggerID)) {
            throw new IllegalArgumentException("triggerID is null or empty");
        }

        Trigger trigger = this.getTrigger(triggerID);
        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers/{2}", this.appID, target.getTypedID().toString(), triggerID);
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.DELETE, headers);
        this.restClient.sendRequest(request);
        return trigger;
    }

    /**
     * List Triggers belongs to the specified Target.
     * @param bestEffortLimit limit the maximum number of the Triggers in the
     *                        Response. It ensures numbers in
     *                        response is equals to or less than specified number.
     *                        But doesn't ensures number of the Triggers
     *                        in the response is equal to specified value.<br>
     *                        If the specified value <= 0, Default size of the limit
     *                        is applied by IoT Cloud.
     * @param paginationKey If specified obtain rest of the items.
     * @return first is list of the Triggers and second is paginationKey returned
     * by IoT Cloud. paginationKey is null when there is next page to be obtained.
     * Obtained paginationKey can be used to get the rest of the items stored
     * in the target.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     * @throws UnsupportedSchemaException Thrown when the returned response has a schema that cannot handle this instance.
     * @throws UnsupportedActionException Thrown when the returned response has a action that cannot handle this instance.
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

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/triggers", this.appID, this.target.getTypedID().toString());
        String url = Path.combine(this.baseUrl, path);
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
        List<Trigger> triggers = new ArrayList<Trigger>();
        if (triggerArray != null) {
            for (int i = 0; i < triggerArray.length(); i++) {
                JSONObject triggerJson = triggerArray.optJSONObject(i);
                JSONObject commandJson = triggerJson.optJSONObject("command");
                String schemaName = commandJson.optString("schema", null);
                int schemaVersion = commandJson.optInt("schemaVersion");
                Schema schema = this.getSchema(schemaName, schemaVersion);
                if (schema == null) {
                    throw new UnsupportedSchemaException(schemaName, schemaVersion);
                }
                triggers.add(this.deserialize(schema, triggerJson, Trigger.class));
            }
        }
        return new Pair<List<Trigger>, String>(triggers, nextPaginationKey);
    }

    /**
     * Get the State of specified Target.
     * State will be serialized with Gson library.
     * @param classOfS Specify class of the State.
     * @param <S> State class.
     * @return Instance of Target State.
     * @throws ThingIFException Thrown when failed to connect IoT Cloud Server.
     * @throws ThingIFRestException Thrown when server returns error response.
     */
    @NonNull
    @WorkerThread
    public <S extends TargetState> S getTargetState(
            @NonNull Class<S> classOfS) throws ThingIFException {

        if (this.target == null) {
            throw new IllegalStateException("Can not perform this action before onboarding");
        }
        if (classOfS == null) {
            throw new IllegalArgumentException("classOfS is null");
        }

        String path = MessageFormat.format("/thing-if/apps/{0}/targets/{1}/states", this.appID, this.target.getTypedID().toString());
        String url = Path.combine(this.baseUrl, path);
        Map<String, String> headers = this.newHeader();
        IoTRestRequest request = new IoTRestRequest(url, IoTRestRequest.Method.GET, headers);
        JSONObject responseBody = this.restClient.sendRequest(request);
        S ret = GsonRepository.gson().fromJson(responseBody.toString(), classOfS);
        return ret;
    }

    /**
     * Get AppID
     * @return
     */
    public String getAppID() {
        return this.appID;
    }
    /**
     * Get AppKey
     * @return
     */
    public String getAppKey() {
        return this.appKey;
    }
    /**
     * Get base URL
     * @return
     */
    public String getBaseUrl() {
        return this.baseUrl;
    }
    /**
     *
     * @return
     */
    public List<Schema> getSchemas() {
        return new ArrayList<Schema>(this.schemas.values());
    }
    /**
     * Get owner who uses the ThingIFAPI.
     * @return
     */
    public Owner getOwner() {
        return this.owner;
    }

    /**
     * Get target thing that is operated by the ThingIFAPI.
     * @return
     */
    public Target getTarget() {
        return this.target;
    }
    void setTarget(Target target) {
        this.target = target;
        saveInstance(this);
    }
    /**
     * Get a tag.
     * @return
     */
    public String getTag() {
        return this.tag;
    }

    private Schema getSchema(String schemaName, int schemaVersion) {
        return this.schemas.get(new Pair<String, Integer>(schemaName, schemaVersion));
    }
    private Action generateAction(String schemaName, int schemaVersion, String actionName, JSONObject actionParameters) throws ThingIFException {
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        Class<? extends Action> actionClass = schema.getActionClass(actionName);
        if (actionClass == null) {
            throw new UnsupportedActionException(schemaName, schemaVersion, actionName);
        }
        String json = actionParameters == null ? "{}" : actionParameters.toString();
        return this.deserialize(schema, json, actionClass);
    }
    private ActionResult generateActionResult(String schemaName, int schemaVersion, String actionName, JSONObject actionResult) throws ThingIFException {
        Schema schema = this.getSchema(schemaName, schemaVersion);
        if (schema == null) {
            throw new UnsupportedSchemaException(schemaName, schemaVersion);
        }
        Class<? extends ActionResult> actionResultClass = schema.getActionResultClass(actionName);
        if (actionResultClass == null) {
            throw new UnsupportedActionException(schemaName, schemaVersion, actionName);
        }
        String json = actionResult == null ? "{}" : actionResult.toString();
        return this.deserialize(schema, json, actionResultClass);
    }
    private Map<String, String> newHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(this.appID)) {
            headers.put("X-Kii-AppID", this.appID);
        }
        if (!TextUtils.isEmpty(this.appKey)) {
            headers.put("X-Kii-AppKey", this.appKey);
        }
        if (this.owner != null && !TextUtils.isEmpty(this.owner.getAccessToken())) {
            headers.put("Authorization", "Bearer " + this.owner.getAccessToken());
        }
        return headers;
    }
    private <T> T deserialize(Schema schema, JSONObject json, Class<T> clazz) throws ThingIFException {
        return this.deserialize(schema, json.toString(), clazz);
    }
    private <T> T deserialize(Schema schema, String json, Class<T> clazz) throws ThingIFException {
        try {
            return GsonRepository.gson(schema).fromJson(json, clazz);
        } catch (JsonParseException e) {
            if (e.getCause() instanceof ThingIFException) {
                throw (ThingIFException)e.getCause();
            }
            throw e;
        }
    }
    private static SharedPreferences getSharedPreferences() {
        if (context != null) {
            return context.getSharedPreferences("com.kii.thingif.preferences", Context.MODE_PRIVATE);
        }
        return null;
    }

    // Implementation of Parcelable
    protected ThingIFAPI(Parcel in) {
        this.tag = in.readString();
        this.appID = in.readString();
        this.appKey = in.readString();
        this.baseUrl = in.readString();
        this.owner = in.readParcelable(Owner.class.getClassLoader());
        this.target = in.readParcelable(Target.class.getClassLoader());
        ArrayList<Schema> schemas = in.createTypedArrayList(Schema.CREATOR);
        for (Schema schema : schemas) {
            this.schemas.put(new Pair<String, Integer>(schema.getSchemaName(), schema.getSchemaVersion()), schema);
        }
        this.restClient = new IoTRestClient();
        this.installationID = in.readString();
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
        dest.writeString(this.appID);
        dest.writeString(this.appKey);
        dest.writeString(this.baseUrl);
        dest.writeParcelable(this.owner, flags);
        dest.writeParcelable(this.target, flags);
        dest.writeTypedList(new ArrayList<Schema>(this.schemas.values()));
        dest.writeString(this.installationID);
    }

}
