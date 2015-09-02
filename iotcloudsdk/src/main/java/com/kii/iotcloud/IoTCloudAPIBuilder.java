package com.kii.iotcloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.utils.Log;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class IoTCloudAPIBuilder {

    private static final String TAG = IoTCloudAPIBuilder.class.getSimpleName();
    private final Context context;
    private final String appID;
    private final String appKey;
    private final Site site;
    private final String baseUrl;
    private final Owner owner;
    private final List<Schema> schemas = new ArrayList<Schema>();

    private IoTCloudAPIBuilder(
            Context context,
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
    private IoTCloudAPIBuilder(
            Context context,
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

    /** Instantiate new IoTCloudAPIBuilder.
     * @param context Application context.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param site Application Site specified when create application.
     * @param owner Specify who uses the IoTCloudAPI.
     * @return IoTCloudAPIBuilder instance.
     */
    @NonNull
    public static IoTCloudAPIBuilder newBuilder(
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
        return new IoTCloudAPIBuilder(context, appID, appKey, site, owner);
    }

    /** Instantiate new IoTCloudAPIBuilder with custom URL.
     * For Custom plan users who run Kii Cloud on premise and need to use your own site URL.
     * @param context Application Context.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param baseUrl Custom Site base URL.
     * @param owner Specify who uses the IoTCloudAPI.
     * @return IoTCloudAPIBuilder instance.
     */
    @NonNull
    public static IoTCloudAPIBuilder newBuilder(
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
        return new IoTCloudAPIBuilder(context, appID, appKey, baseUrl, owner);
    }

    @NonNull
    static IoTCloudAPIBuilder newBuilder(
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull Owner owner) {
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
        return new IoTCloudAPIBuilder(null, appID, appKey, site, owner);
    }
    @NonNull
    static IoTCloudAPIBuilder newBuilder(
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
        return new IoTCloudAPIBuilder(null, appID, appKey, baseUrl, owner);
    }

    /** Add Schema to the IoTCloudAPI.
     * @param schema
     * @return IoTCloudAPIBuilder instance for method chaining.
     */
    @NonNull
    public IoTCloudAPIBuilder addSchema(
            @NonNull Schema schema) {
        if (schema == null) {
            throw new IllegalArgumentException("schema is null");
        }
        this.schemas.add(schema);
        Log.d(TAG, MessageFormat.format("Added new schema SchemaName={0}, SchemaVersion={1}", schema.getSchemaName(), schema.getSchemaVersion()));
        return this;
    }

    /** Instantiate new IoTCloudAPI instance.
     * @return IoTCloudAPI instance.
     */
    @NonNull
    public IoTCloudAPI build() {
        String baseUrl = this.baseUrl;
        if (this.site != null) {
            baseUrl = this.site.getBaseUrl();
        }
        if (this.schemas.size() == 0) {
            throw new IllegalStateException("Builder has no schemas");
        }
        Log.d(TAG, MessageFormat.format("Initialize IoTCloudAPI AppID={0}, AppKey={1}, BaseUrl={2}", this.appID, this.appKey, baseUrl));
        return new IoTCloudAPI(this.context, this.appID, this.appKey, baseUrl, this.owner, this.schemas);
    }

}
