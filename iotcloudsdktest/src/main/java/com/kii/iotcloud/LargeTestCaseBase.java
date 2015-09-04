package com.kii.iotcloud;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.model.storage.KiiNormalUser;
import com.kii.iotcloud.schema.LightState;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.schema.SetBrightness;
import com.kii.iotcloud.schema.SetBrightnessResult;
import com.kii.iotcloud.schema.SetColor;
import com.kii.iotcloud.schema.SetColorResult;
import com.kii.iotcloud.schema.SetColorTemperature;
import com.kii.iotcloud.schema.SetColorTemperatureResult;
import com.kii.iotcloud.schema.TurnPower;
import com.kii.iotcloud.schema.TurnPowerResult;

public abstract class LargeTestCaseBase extends AndroidTestCase {

    private static final String DEV_JP_SERVER = "https://api-development-jp.internal.kii.com";
    public static final String DEMO_THING_TYPE = "LED";
    public static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    public static final int DEMO_SCHEMA_VERSION = 1;

    public enum TargetTestServer {
        DEV_SERVER_1(DEV_JP_SERVER, "50a62843", "2bde7d4e3eed1ad62c306dd2144bb2b0");

        private final String appID;
        private final String appKey;
        private final String baseUrl;
        private TargetTestServer(String baseUrl, String appID, String appKey) {
           this.baseUrl = baseUrl;
            this.appID = appID;
            this.appKey = appKey;
        }
        private TargetTestServer(Site site, String appID, String appKey) {
            this.baseUrl = site.getBaseUrl();
            this.appID = appID;
            this.appKey = appKey;
        }
        public String getBaseUrl() {
            return this.baseUrl;
        }
        public String getAppID() {
            return this.appID;
        }
        public String getAppKey() {
            return this.appKey;
        }
    }

    protected Owner createNewOwner(TargetTestServer server) throws Exception {
        KiiRest rest = new KiiRest(server.getAppID(), server.getAppKey(), server.getBaseUrl() + "/api");
        KiiNormalUser user = new KiiNormalUser().setUsername("test-" + System.currentTimeMillis());
        user = rest.api().users().register(user, "password");
        return new Owner(new TypedID(TypedID.Types.USER, user.getUserID()), user.getAccessToken());
    }
    protected Schema createDefaultSchema() {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        return sb.build();
    }
    protected IoTCloudAPI craeteIoTCloudAPIWithDemoSchema(TargetTestServer server) throws Exception {
        Owner owner = this.createNewOwner(server);
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), server.getAppID(), server.getAppKey(), server.getBaseUrl(), owner);
        builder.addSchema(this.createDefaultSchema());
        return builder.build();
    }
}
