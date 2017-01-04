package com.kii.thingif;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.command.Action;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.internal.utils._Log;
import com.kii.thingif.schema.Schema;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThingIFAPIBuilder {

    private static final String TAG = ThingIFAPIBuilder.class.getSimpleName();
    private final @NonNull Context context;
    private final @NonNull KiiApp app;
    private final @NonNull Owner owner;
    private @Nullable Target target;
    private @Nullable String installationID;
    private @Nullable String tag;
    private final @NonNull List<Schema> schemas = new ArrayList<Schema>();

    private final @NonNull Map<String, Map<String, List<Class<? extends Action>>>> actionTypes;
    private final @NonNull Map<String, Map<String,List<Class<? extends ActionResult>>>> actionResultTypes;
    private final @NonNull Map<String, Class<? extends TargetState>> stateTypes;

    private ThingIFAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @NonNull Map<String, Map<String,List<Class<? extends Action>>>> actionTypes,
            @NonNull Map<String, Map<String,List<Class<? extends ActionResult>>>> actionResultTypes,
            @NonNull Map<String, Class<? extends TargetState>> stateTypes
            ) {
        this.context = context;
        this.app = app;
        this.owner = owner;
        this.actionTypes = actionTypes;
        this.actionResultTypes = actionResultTypes;
        this.stateTypes = stateTypes;
    }

    /** Instantiate new ThingIFAPIBuilder.
     * @param context Application context.
     * @param app Kii Cloud Application.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static ThingIFAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @NonNull Map<String, Map<String,List<Class<? extends Action>>>> actionTypes,
            @NonNull Map<String, Map<String,List<Class<? extends ActionResult>>>> actionResultTypes,
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
        return new ThingIFAPIBuilder(context, app, owner, actionTypes, actionResultTypes, stateTypes);
    }

    /**
     * Instantiate new ThingIFAPIBuilder without Context.
     * This method is for internal use only. Do not call it from your application.
     *
     * @param app Kii Cloud Application.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static ThingIFAPIBuilder _newBuilder(
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @NonNull Map<String, Map<String,List<Class<? extends Action>>>> actionTypes,
            @NonNull Map<String, Map<String,List<Class<? extends ActionResult>>>> actionResultTypes,
            @NonNull Map<String, Class<? extends TargetState>> stateTypes) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new ThingIFAPIBuilder(null, app, owner, actionTypes, actionResultTypes, stateTypes);
    }

    /** Add Schema to the ThingIFAPI.
     * @param schema schema for {@link ThingIFAPI} instance.
     * @return ThingIFAPIBuilder instance for method chaining.
     */
    @NonNull
    public ThingIFAPIBuilder addSchema(
            @NonNull Schema schema) {
        if (schema == null) {
            throw new IllegalArgumentException("schema is null");
        }
        this.schemas.add(schema);
        _Log.d(TAG, MessageFormat.format("Added new schema SchemaName={0}, SchemaVersion={1}", schema.getSchemaName(), schema.getSchemaVersion()));
        return this;
    }

    /**
     * Set target thing to the ThingIFAPI.
     * @param target target of {@link ThingIFAPI} instance.
     * @return builder instance for chaining call.
     */
    public ThingIFAPIBuilder setTarget(Target target) {
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
    public ThingIFAPIBuilder setTag(@Nullable String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Set InstallationID to the ThingIFAPI.
     * @param installationID installation id
     * @return builder instance for chaining call.
     */
    public ThingIFAPIBuilder setInstallationID(String installationID) {
        this.installationID = installationID;
        return this;
    }

    /** Instantiate new ThingIFAPI instance.
     * @return ThingIFAPI instance.
     * @throws IllegalStateException when schema is not present.
     */
    @NonNull
    public ThingIFAPI build() {
        if (this.schemas.size() == 0) {
            throw new IllegalStateException("Builder has no schemas");
        }
        _Log.d(TAG, MessageFormat.format("Initialize ThingIFAPI AppID={0}, AppKey={1}, BaseUrl={2}", app.getAppID(), app.getAppKey(), app.getBaseUrl()));
        return new ThingIFAPI(this.context, this.tag, app, this.owner, this.target, this.schemas, this.installationID, this.actionTypes, this.actionResultTypes, this.stateTypes);
    }

    /**
     * Register list of Action subclasses to specified alias. The registered action classes
     * will be used for serialization/deserialization the action.
     * If the same alias already registered, then will be updated
     * @param alias Alias to register
     * @param actionClasses List of Action subclasses
     * @return builder instance for chaining call.
     */
    @NonNull
    public ThingIFAPIBuilder registerActions(
            @NonNull String alias,
            @NonNull Map<String,List<Class<? extends Action>>> actionClasses){
        this.actionTypes.put(alias, actionClasses);
        return this;
    }

    /**
     * Register list of ActionResult subclasses to specified alias. The registered action result classes
     * will be used for serialization/deserialization the action result.
     * If the same alias already registered, then will be updated
     * @param alias Alias to register
     * @param actionResultClasses List of ActionResult subclasses
     * @return builder instance for chaining call.
     */
    @NonNull
    public ThingIFAPIBuilder registerActionResults(
            @NonNull String alias,
            @NonNull Map<String,List<Class<? extends ActionResult>>> actionResultClasses) {
        this.actionResultTypes.put(alias, actionResultClasses);
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
    public ThingIFAPIBuilder registerTargetState(
            @NonNull String alias,
            @NonNull Class<? extends TargetState> stateClass) {
        this.stateTypes.put(alias, stateClass);
        return this;
    }

}
