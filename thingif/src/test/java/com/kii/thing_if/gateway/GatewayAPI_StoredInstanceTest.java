package com.kii.thing_if.gateway;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.kii.thing_if.KiiApp;
import com.kii.thing_if.exception.StoredInstanceNotFoundException;
import com.kii.thing_if.exception.UnloadableInstanceVersionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
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
        Context context = RuntimeEnvironment.application.getApplicationContext();
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(context, app, gatewayAddress);
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        GatewayAPI restoredApi = GatewayAPI.loadFromStoredInstance(context);

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        Assert.assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        Assert.assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());
    }

    @Test
    public void loadFromStoredInstanceWithTagTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = RuntimeEnvironment.application.getApplicationContext();
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(context, app, gatewayAddress).setTag("GatewayA");
        GatewayAPI api = builder.build();

        this.addMockResponseForLogin(200, accessToken);
        api.login(username, password);
        Assert.assertEquals(accessToken, api.getAccessToken());

        GatewayAPI restoredApi = GatewayAPI.loadFromStoredInstance(context, "GatewayA");

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        Assert.assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        Assert.assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());

        try {
            GatewayAPI.loadFromStoredInstance(context, "GatewayB");
            Assert.fail("StoredInstanceNotFoundException should be thrown");
        } catch (StoredInstanceNotFoundException e) {
        }
    }

    @Test(expected = StoredInstanceNotFoundException.class)
    public void loadFromStoredInstanceWithoutStoredInstanceTest() throws Exception {
        GatewayAPI.loadFromStoredInstance(RuntimeEnvironment.application.getApplicationContext());
    }

    @Test(expected = UnloadableInstanceVersionException.class)
    public void loadFromStoredInstanceNoSDKVersionTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = RuntimeEnvironment.application.getApplicationContext();
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(context, app, gatewayAddress);
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

    @Test(expected = UnloadableInstanceVersionException.class)
    public void loadFromStoredInstanceLowerSDKVersionTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = RuntimeEnvironment.application.getApplicationContext();
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(context, app, gatewayAddress);
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
    public void loadFromStoredInstanceUpperSDKVersionTest() throws Exception {
        String username = "user01";
        String password = "pa$$word";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(APP_ID, APP_KEY);
        Uri gatewayAddress = getGatewayAddress();
        Context context = RuntimeEnvironment.application.getApplicationContext();
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(context, app, gatewayAddress);
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

        Assert.assertEquals(api.getAppID(), restoredApi.getAppID());
        Assert.assertEquals(api.getAppKey(), restoredApi.getAppKey());
        Assert.assertEquals(api.getGatewayAddress().toString(), restoredApi.getGatewayAddress().toString());
        Assert.assertEquals(api.getGatewayAddress().getPort(), restoredApi.getGatewayAddress().getPort());
        Assert.assertEquals(api.getGatewayAddress().getScheme(), restoredApi.getGatewayAddress().getScheme());
        Assert.assertEquals(api.getAccessToken(), restoredApi.getAccessToken());

        GatewayAPI.removeAllStoredInstances();
        try {
            GatewayAPI.loadFromStoredInstance(context, "ThingB");
            Assert.fail("StoredInstanceNotFoundException should be thrown");
        } catch (StoredInstanceNotFoundException e) {
        }
    }
}
