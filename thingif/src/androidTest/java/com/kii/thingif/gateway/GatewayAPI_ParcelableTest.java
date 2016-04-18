package com.kii.thingif.gateway;

import android.os.Parcel;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.KiiApp;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GatewayAPI_ParcelableTest extends GatewayAPITestBase {
    @Test
    public void test() throws Exception {
        String appID = "appid-abcd1234";
        String appKey = "appkey-abcd1234";
        String accessToken = "token-abcd1234";
        KiiApp app = this.getApp(appID, appKey);
        GatewayAPI api = new GatewayAPI(InstrumentationRegistry.getTargetContext(), app);

        this.addMockResponseForLogin(200, accessToken);
        api.login("username", "password");

        Parcel parcel = Parcel.obtain();
        api.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GatewayAPI deserializedApi = GatewayAPI.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(app.getAppID(), deserializedApi.appID);
        Assert.assertEquals(app.getAppKey(), deserializedApi.appKey);
        Assert.assertEquals(app.getSiteName(), deserializedApi.siteName);
        Assert.assertEquals(app.getBaseUrl(), deserializedApi.baseUrl);
        Assert.assertEquals(accessToken, deserializedApi.accessToken);
    }
}
