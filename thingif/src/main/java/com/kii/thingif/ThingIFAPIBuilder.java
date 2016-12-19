package com.kii.thingif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.internal.utils._Log;
import com.kii.thingif.schema.Schema;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ThingIFAPIBuilder<T extends Alias> {

    private static final String TAG = ThingIFAPIBuilder.class.getSimpleName();
    private final Context context;
    private final KiiApp app;
    private final Owner owner;
    private Target target;
    private String installationID;
    private String tag;
    private final List<Schema> schemas = new ArrayList<Schema>();

    private ThingIFAPIBuilder(
            @Nullable Context context,
            @NonNull KiiApp app,
            @NonNull Owner owner) {
        this.context = context;
        this.app = app;
        this.owner = owner;
    }

    /** Instantiate new ThingIFAPIBuilder.
     * @param context Application context.
     * @param app Kii Cloud Application.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static <T1 extends Alias>  ThingIFAPIBuilder<T1> newBuilder(
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
        return new ThingIFAPIBuilder<>(context, app, owner);
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
            @NonNull Owner owner) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new ThingIFAPIBuilder(null, app, owner);
    }

    /** Add Schema to the ThingIFAPI.
     * @param schema schema for {@link ThingIFAPI} instance.
     * @return ThingIFAPIBuilder instance for method chaining.
     */
    @NonNull
    public ThingIFAPIBuilder<T> addSchema(
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
    public ThingIFAPIBuilder<T> setTarget(Target target) {
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
    public ThingIFAPIBuilder<T> setTag(@Nullable String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Set InstallationID to the ThingIFAPI.
     * @param installationID installation id
     * @return builder instance for chaining call.
     */
    public ThingIFAPIBuilder<T> setInstallationID(String installationID) {
        this.installationID = installationID;
        return this;
    }

    /** Instantiate new ThingIFAPI instance.
     * @return ThingIFAPI instance.
     * @throws IllegalStateException when schema is not present.
     */
    @NonNull
    public ThingIFAPI<T> build() {
        if (this.schemas.size() == 0) {
            throw new IllegalStateException("Builder has no schemas");
        }
        _Log.d(TAG, MessageFormat.format("Initialize ThingIFAPI AppID={0}, AppKey={1}, BaseUrl={2}", app.getAppID(), app.getAppKey(), app.getBaseUrl()));
        return new ThingIFAPI<>(this.context, this.tag, app, this.owner, this.target, this.schemas, this.installationID);
    }

}
