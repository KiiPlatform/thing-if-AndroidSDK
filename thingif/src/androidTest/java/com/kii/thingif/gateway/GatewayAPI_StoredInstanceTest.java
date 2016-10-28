package com.kii.thingif.gateway;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.KiiApp;
import com.kii.thingif.exception.StoredGatewayAPIInstanceNotFoundException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_StoredInstanceTest extends GatewayAPITestBase {
    @Before
    public void before() throws Exception {
        super.before();
        this.clearSharedPreferences();
    }

    @Test
    public void loadFromStoredInstanceTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = InstrumentationRegistry.getTargetContext();
        GatewayAPIBuilder builder = GatewayAPIBuilder.newBuilder(context, app, gatewayAddress);
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        GatewayAPI restoredApi = GatewayAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());
    }

    @Test
    public void loadFromStoredInstanceWithTagTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = InstrumentationRegistry.getTargetContext();
        GatewayAPIBuilder builder = GatewayAPIBuilder.newBuilder(context, app, gatewayAddress).setTag("GatewayA");
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        GatewayAPI restoredApi = GatewayAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "GatewayA");

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());

        try {
            GatewayAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext(), "GatewayB");
            fail("StoredGatewayAPIInstanceNotFoundException should be thrown");
        } catch (StoredGatewayAPIInstanceNotFoundException e) {
        }
    }

    @Test(expected = StoredGatewayAPIInstanceNotFoundException.class)
    public void loadFromStoredInstanceWithoutStoredInstanceTest() throws Exception {
        GatewayAPI.loadFromStoredInstance(InstrumentationRegistry.getTargetContext());
    }

    @Test(expected = StoredGatewayAPIInstanceNotFoundException.class)
    public void loadFromStoredInstanceNoSDKVersionTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = InstrumentationRegistry.getTargetContext();
        GatewayAPIBuilder builder = GatewayAPIBuilder.newBuilder(context, app, gatewayAddress);
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("GatewayAPI_VERSION");
        editor.apply();

        GatewayAPI.loadFromStoredInstance(context);
    }

    @Test(expected = StoredGatewayAPIInstanceNotFoundException.class)
    public void loadFromStoredInstanceLowerSDKVersionTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = InstrumentationRegistry.getTargetContext();
        GatewayAPIBuilder builder = GatewayAPIBuilder.newBuilder(context, app, gatewayAddress);
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("GatewayAPI_VERSION", "0.0.0");
        editor.apply();

        GatewayAPI.loadFromStoredInstance(context);
    }

    @Test
    public void loadFromStoredInstanceUpperTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = InstrumentationRegistry.getTargetContext();
        GatewayAPIBuilder builder = GatewayAPIBuilder.newBuilder(context, app, gatewayAddress);
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        SharedPreferences preferences = context.getSharedPreferences(
                "com.kii.thingif.preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("GatewayAPI_VERSION", "1000.0.0");
        editor.apply();

        GatewayAPI restoredApi = GatewayAPI.loadFromStoredInstance(context);

        assertEquals(api.getAppID(), restoredApi.getAppID());
        assertEquals(api.getAppKey(), restoredApi.getAppKey());
        assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());
    }
}
