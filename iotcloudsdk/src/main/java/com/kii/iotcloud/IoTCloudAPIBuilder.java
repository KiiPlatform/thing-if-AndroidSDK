package com.kii.iotcloud;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.kii.iotcloud.schema.Schema;

import java.util.ArrayList;
import java.util.List;

public class IoTCloudAPIBuilder {

    private final Context context;
    private final String appID;
    private final String appKey;
    private final Site site;
    private final Owner owner;
    private final List<Schema> schemas = new ArrayList<Schema>();

    private IoTCloudAPIBuilder(
            @NonNull Context context,
            @NonNull String appID,
            @NonNull String appKey,
            @NonNull Site site,
            @NonNull Owner owner) {
        this.context = context;
        this.appID = appID;
        this.appKey = appKey;
        this.site = site;
        this.owner = owner;
    }

    /** Instantiate new IoTCloudAPIBuilder.
     * @param context
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
        return this;
    }

    /** Instantiate new IoTCloudAPI instance.
     * @return IoTCloudAPI instance.
     */
    @NonNull
    public IoTCloudAPI build() {
        // TODO: Implement it.
        return null;
    }

}
