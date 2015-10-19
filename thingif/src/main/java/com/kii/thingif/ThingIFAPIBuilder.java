package com.kii.thingif;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kii.thingif.schema.Schema;
import com.kii.thingif.internal.utils.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class ThingIFAPIBuilder {

    private static final String TAG = ThingIFAPIBuilder.class.getSimpleName();
    private final Context context;
    private final String appID;
    private final String appKey;
    private final Site site;
    private final String baseUrl;
    private final Owner owner;
    private Target target;
    private String installationID;
    private final List<Schema> schemas = new ArrayList<Schema>();

    private ThingIFAPIBuilder(
            @Nullable Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull Owner owner) {
        this.context = context;
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.baseUrl = null;
        this.owner = owner;
    }
    private ThingIFAPIBuilder(
            @Nullable Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull String baseUrl,
            @NonNull Owner owner) {
        this.context = context;
        this.appID = appID;
        this.appKey = appKey;
        this.site = null;
        this.baseUrl = baseUrl;
        this.owner = owner;
    }

    /** Instantiate new ThingIFAPIBuilder.
     * @param context Application context.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param site Application Site specified when create application.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static ThingIFAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull Owner owner) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty");
        }
        if (site == null) {
            throw new IllegalArgumentException("site is null");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new ThingIFAPIBuilder(context, appID, appKey, site, owner);
    }

    /** Instantiate new ThingIFAPIBuilder with custom URL.
     * For Custom plan users who run Kii Cloud on premise and need to use your own site URL.
     * @param context Application Context.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param baseUrl Custom Site base URL.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static ThingIFAPIBuilder newBuilder(
            @NonNull Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull String baseUrl,
            @NonNull Owner owner) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty");
        }
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("baseUrl is null or empty");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new ThingIFAPIBuilder(context, appID, appKey, baseUrl, owner);
    }

    /**
     * Instantiate new ThingIFAPIBuilder without Context.
     * This method is for internal use only. Do not call it from your application.
     *
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param baseUrl Custom Site base URL.
     * @param owner Specify who uses the ThingIFAPI.
     * @return ThingIFAPIBuilder instance.
     */
    @NonNull
    public static ThingIFAPIBuilder _newBuilder(
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull String baseUrl,
            @NonNull Owner owner) {
        if (TextUtils.isEmpty(appID)) {
            throw new IllegalArgumentException("appID is null or empty");
        }
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null or empty");
        }
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("baseUrl is null or empty");
        }
        if (owner == null) {
            throw new IllegalArgumentException("owner is null");
        }
        return new ThingIFAPIBuilder(null, appID, appKey, baseUrl, owner);
    }

    /** Add Schema to the ThingIFAPI.
     * @param schema
     * @return ThingIFAPIBuilder instance for method chaining.
     */
    @NonNull
    public ThingIFAPIBuilder addSchema(
            @NonNull Schema schema) {
        if (schema == null) {
            throw new IllegalArgumentException("schema is null");
        }
        this.schemas.add(schema);
        Log.d(TAG, MessageFormat.format("Added new schema SchemaName={0}, SchemaVersion={1}", schema.getSchemaName(), schema.getSchemaVersion()));
        return this;
    }

    /**
     * Set target thing to the ThingIFAPI.
     * @param target
     * @return
     */
    public ThingIFAPIBuilder setTarget(Target target) {
        this.target = target;
        return this;
    }

    /**
     * Set InstallationID to the ThingIFAPI.
     * @param installationID
     * @return
     */
    public ThingIFAPIBuilder setInstallationID(String installationID) {
        this.installationID = installationID;
        return this;
    }

    /** Instantiate new ThingIFAPI instance.
     * @return ThingIFAPI instance.
     */
    @NonNull
    public ThingIFAPI build() {
        return build(null);
    }
    /** Instantiate new ThingIFAPI instance.
     * @param tag
     * @return ThingIFAPI instance.
     */
    @NonNull
    public ThingIFAPI build(String tag) {
        String baseUrl = this.baseUrl;
        if (this.site != null) {
            baseUrl = this.site.getBaseUrl();
        }
        if (this.schemas.size() == 0) {
            throw new IllegalStateException("Builder has no schemas");
        }
        Log.d(TAG, MessageFormat.format("Initialize ThingIFAPI AppID={0}, AppKey={1}, BaseUrl={2}", this.appID, this.appKey, baseUrl));
        return new ThingIFAPI(this.context, tag, this.appID, this.appKey, baseUrl, this.owner, this.target, this.schemas, this.installationID);
    }

}
