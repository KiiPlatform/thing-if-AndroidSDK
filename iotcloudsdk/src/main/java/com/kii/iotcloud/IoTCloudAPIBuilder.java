package com.kii.iotcloud;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kii.iotcloud.schema.Schema;

public class IoTCloudAPIBuilder {

    private IoTCloudAPIBuilder() {}

    /** Instantiate new IoTCloudAPIBuilder.
     * @param appID Application ID given by Kii Cloud.
     * @param appKey Application Key given by Kii Cloud.
     * @param baseUrl Application Site specified when create application.
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
        // TODO: Implement it.
        return null;
    }

    /** Add Schema to the IoTCloudAPI.
     * @param schema
     * @return IoTCloudAPIBuilder instance for method chaining.
     */
    @NonNull
    public IoTCloudAPIBuilder addSchema(
            @NonNull Schema schema) {
        // TODO: Implement it.
        return null;
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
