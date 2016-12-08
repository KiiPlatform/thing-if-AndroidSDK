package com.kii.thingif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.internal.utils._Log;

import java.text.MessageFormat;

public class TraitThingIFAPIBuilder {

    private static final String TAG = ThingIFAPIBuilder.class.getSimpleName();
    private final Context context;
    private final KiiApp app;
    private final Owner owner;
    private Target target;
    private String installationID;
    private String tag;

    private TraitThingIFAPIBuilder(
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
    public static TraitThingIFAPIBuilder newBuilder(
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
        return new TraitThingIFAPIBuilder(context, app, owner);
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
    public static TraitThingIFAPIBuilder _newBuilder(
            @NonNull KiiApp app,
            @NonNull Owner owner) {
        if (app == null) {
            throw new IllegalArgumentException("app is null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new TraitThingIFAPIBuilder(null, app, owner);
    }

    /**
     * Set target thing to the ThingIFAPI.
     * @param target target of {@link ThingIFAPI} instance.
     * @return builder instance for chaining call.
     */
    public TraitThingIFAPIBuilder setTarget(Target target) {
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
    public TraitThingIFAPIBuilder setTag(@Nullable String tag) {
        this.tag = tag;
        return this;
    }

    /**
     * Set InstallationID to the ThingIFAPI.
     * @param installationID installation id
     * @return builder instance for chaining call.
     */
    public TraitThingIFAPIBuilder setInstallationID(String installationID) {
        this.installationID = installationID;
        return this;
    }

    /** Instantiate new ThingIFAPI instance.
     * @return ThingIFAPI instance.
     * @throws IllegalStateException when schema is not present.
     */
    @NonNull
    public TraitThingIFAPI build() {
        _Log.d(TAG, MessageFormat.format("Initialize ThingIFAPI AppID={0}, AppKey={1}, BaseUrl={2}", app.getAppID(), app.getAppKey(), app.getBaseUrl()));

        ThingIFAPI thingIfApi = ThingIFAPIBuilder
                .newBuilder(context, app, owner)
                .setTag(tag)
                .setTarget(target)
                .setInstallationID(installationID)
                .build();

        return new TraitThingIFAPI(thingIfApi);
    }

}
