package com.kii.thingif.largetests;

import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;

import com.google.gson.JsonParser;
import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.model.storage.KiiNormalUser;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.TypedID;
import com.kii.thingif.schema.LightState;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.schema.SetBrightness;
import com.kii.thingif.schema.SetBrightnessResult;
import com.kii.thingif.schema.SetColor;
import com.kii.thingif.schema.SetColorResult;
import com.kii.thingif.schema.SetColorTemperature;
import com.kii.thingif.schema.SetColorTemperatureResult;
import com.kii.thingif.schema.TurnPower;
import com.kii.thingif.schema.TurnPowerResult;

import junit.framework.Assert;

import org.json.JSONObject;

public abstract class LargeTestCaseBase extends AndroidTestCase {

    private static final String DEV_JP_SERVER = "https://api-jp.kii.com";
    public static final String DEMO_THING_TYPE = "LED";
    public static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    public static final int DEMO_SCHEMA_VERSION = 1;

    public enum TargetTestServer {
        DEV_SERVER_1(DEV_JP_SERVER, "9ab34d8b", "7a950d78956ed39f3b0815f0f001b43b");

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
    protected ThingIFAPI craeteThingIFAPIWithDemoSchema(TargetTestServer server) throws Exception {
        Owner owner = this.createNewOwner(server);
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), server.getAppID(), server.getAppKey(), server.getBaseUrl(), owner);
        builder.addSchema(this.createDefaultSchema());
        return builder.build();
    }
    protected void assertJSONObject(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(new JsonParser().parse(expected.toString()), new JsonParser().parse(actual.toString()));
    }
}
