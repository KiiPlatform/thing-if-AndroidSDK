package com.kii.thingif.largetests;

import android.support.test.InstrumentationRegistry;
import android.test.AndroidTestCase;
import android.text.TextUtils;

import com.google.gson.JsonParser;
import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.model.storage.KiiNormalUser;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.Owner;
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

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;

import java.io.InputStream;

public abstract class LargeTestCaseBase extends AndroidTestCase {

    public static final String DEMO_THING_TYPE = "LED";
    public static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    public static final int DEMO_SCHEMA_VERSION = 1;

    public static class TargetTestServer {

        private final String appID;
        private final String appKey;
        private final String baseUrl;
        private final String clientId;
        private final String clientSecret;
        private TargetTestServer(String baseUrl, String appID, String appKey, String clientId, String clientSecret) {
            this.baseUrl = baseUrl;
            this.appID = appID;
            this.appKey = appKey;
            this.clientId = clientId;
            this.clientSecret = clientSecret;
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
        public String getClientId() {
            return this.clientId;
        }
        public String getClientSecret() {
            return this.clientSecret;
        }
        public boolean hasAdminCredential() {
            return !(TextUtils.isEmpty(this.clientId) || TextUtils.isEmpty(this.clientSecret));
        }
    }

    protected TargetTestServer server = null;

    @Before
    public void before() throws Exception {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("assets/app.json");
        try {
            JSONObject json = new JSONObject(IOUtils.toString(is));
            this.server = new TargetTestServer(
                    json.getString("server"),
                    json.getString("app_id"),
                    json.getString("app_key"),
                    json.getString("client_id"),
                    json.getString("client_secret"));
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    protected Owner createNewOwner() throws Exception {
        KiiRest rest = new KiiRest(this.server.getAppID(), this.server.getAppKey(), this.server.getBaseUrl() + "/api");
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
    protected ThingIFAPI craeteThingIFAPIWithDemoSchema() throws Exception {
        Owner owner = this.createNewOwner();
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), this.server.getAppID(), this.server.getAppKey(), this.server.getBaseUrl(), owner);
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
