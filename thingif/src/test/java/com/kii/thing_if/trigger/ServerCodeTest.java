package com.kii.thing_if.trigger;

import android.os.Parcel;

import com.kii.thing_if.SmallTestBase;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ServerCodeTest extends SmallTestBase{

    @Test
    public void constructorWithFullParsTest() throws Exception {
        ServerCode serverCode = new ServerCode(
                "endpoint",
                "token",
                "appID",
                new JSONObject().put("k", "v"));
        Assert.assertEquals("endpoint", serverCode.getEndpoint());
        Assert.assertEquals("token", serverCode.getExecutorAccessToken());
        Assert.assertEquals("appID", serverCode.getTargetAppID());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                serverCode.getParameters().toString());
    }

    @Test
    public void constructorWith2ParsTest() throws Exception {
        ServerCode serverCode = new ServerCode("endpoint", "executor_access_token");
        Assert.assertEquals("endpoint", serverCode.getEndpoint());
        Assert.assertEquals("executor_access_token", serverCode.getExecutorAccessToken());
        Assert.assertNull(serverCode.getParameters());
        Assert.assertNull(serverCode.getTargetAppID());
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorWithNullEndpoint() throws Exception {
        new ServerCode(null, "executor_access_token");
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorWithEmptyEndpoint() throws Exception {
        new ServerCode("", "executor_access_token");
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorWithNullEndpoint2() throws Exception {
        new ServerCode(null, "executor_access_token", "appID", new JSONObject().put("k", "v"));
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorWithEmptyEndpoint2() throws Exception {
        new ServerCode("", "executor_access_token", "appID", new JSONObject().put("k", "v"));
    }
    @Test
    public void constructorTest3() throws Exception {
        ServerCode serverCode = new ServerCode("endpoint", null);
        Assert.assertEquals("endpoint", serverCode.getEndpoint());
        Assert.assertNull(serverCode.getExecutorAccessToken());
        Assert.assertNull(serverCode.getParameters());
        Assert.assertNull(serverCode.getTargetAppID());
    }
    @Test
    public void parcelableTest() throws Exception{
        ServerCode[] serverCodes = {
                new ServerCode("endPoint", "token"),
                new ServerCode("endPoint", null),
                new ServerCode("endPoint", "token", "appID", new JSONObject().put("k", "v")),
                new ServerCode("endPoint", "token", null, null),
                new ServerCode("endPoint", "token", "appID", null)
        };

        for (ServerCode serverCode : serverCodes) {
            Parcel parcel = Parcel.obtain();
            serverCode.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            ServerCode deserializedServerCode = ServerCode.CREATOR.createFromParcel(parcel);
            assertServerCode(serverCode, deserializedServerCode);
        }
    }
}
