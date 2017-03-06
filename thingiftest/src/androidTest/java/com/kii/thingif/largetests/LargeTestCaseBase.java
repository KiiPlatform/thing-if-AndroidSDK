package com.kii.thingif.largetests;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.cloud.rest.client.KiiRest;
import com.kii.cloud.rest.client.exception.KiiRestException;
import com.kii.cloud.rest.client.logging.KiiDefaultLogger;
import com.kii.cloud.rest.client.model.KiiCredentials;
import com.kii.cloud.rest.client.model.storage.KiiNormalUser;
import com.kii.cloud.rest.client.model.storage.KiiThing;
import com.kii.cloud.rest.client.resource.KiiThingIfResource;
import com.kii.cloud.rest.client.resource.thingif.KiiThingIfTargetResource;
import com.kii.cloud.rest.client.resource.thingif.KiiThingIfTargetStatesResource;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;
import com.kii.thingif.states.StateToJSON;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class LargeTestCaseBase {

    public static final String DEFAULT_THING_TYPE = "MyAirConditioner";
    public static final String DEFAULT_FIRMWARE_VERSION = "v1";
    protected Context context;
    protected final String ALIAS1 = "AirConditionerAlias";
    protected final String ALIAS2 = "HumidityAlias";

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
        this.context = InstrumentationRegistry.getContext();
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

    protected void updateTargetState(Target thing, TargetState[] states) {
        KiiCredentials credentials = new KiiCredentials(thing.getAccessToken());
        KiiRest rest = new KiiRest(
                this.server.getAppID(),
                this.server.getAppKey(),
                this.server.getBaseUrl()+"/api",
                this.server.getBaseUrl()+"/thing-if", null);
        rest.setCredentials(credentials);
        KiiThingIfTargetStatesResource targetStates =
                new KiiThingIfTargetStatesResource(
                        new KiiThingIfTargetResource(
                                rest.thingif(),
                                thing.getTypedID().toString()
                        ));
        try {
            JsonObject aliasState = new JsonObject();
            for (TargetState state : states) {
                JsonObject stateJson =
                        new JsonParser()
                                .parse(((StateToJSON)state).toJSONObject().toString()).getAsJsonObject();
                if (state instanceof AirConditionerState) {
                    aliasState.add(ALIAS1, stateJson);
                } else if (state instanceof HumidityState) {
                    aliasState.add(ALIAS2, stateJson);
                } else {
                    throw new RuntimeException("not supported state for test");
                }
            }
            targetStates.save(aliasState, true);
        }catch (KiiRestException e) {
            throw new RuntimeException(e);
        }
    }

    protected Owner createNewOwner() throws Exception {
        KiiRest rest = new KiiRest(this.server.getAppID(), this.server.getAppKey(), this.server.getBaseUrl() + "/api");
        KiiNormalUser user = new KiiNormalUser().setUsername("test-" + System.currentTimeMillis());
        user = rest.api().users().register(user, "password");
        return new Owner(new TypedID(TypedID.Types.USER, user.getUserID()), user.getAccessToken());
    }

    protected ThingIFAPI createDefaultThingIFAPI() throws Exception {
        Owner owner = this.createNewOwner();
        String hostname = server.getBaseUrl().substring("https://".length());
        KiiApp app = KiiApp.Builder.builderWithHostName(server.getAppID(), server.getAppKey(), hostname).build();
        return ThingIFAPI.Builder.newBuilder(
                this.context,
                app,
                owner,
                getDefaultActionTypes(),
                getDefaultStateTypes()).build();
    }

    protected void assertJSONObject(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(new JsonParser().parse(expected.toString()), new JsonParser().parse(actual.toString()));
    }

    protected Map<String, Class<? extends Action>> getDefaultActionTypes () {
        Map<String, Class<? extends Action>> actionTypes = new HashMap<>();
        actionTypes.put(ALIAS1, AirConditionerActions.class);
        actionTypes.put(ALIAS2, HumidityActions.class);
        return actionTypes;
    }

    protected Map<String, Class<? extends TargetState>> getDefaultStateTypes () {
        Map<String, Class<? extends TargetState>> stateTypes = new HashMap<>();
        stateTypes.put(ALIAS1, AirConditionerState.class);
        stateTypes.put(ALIAS2, HumidityState.class);
        return stateTypes;
    }
}
