package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class CommandTest extends LargeTestCaseBase {
    @Test
    public void test() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        Target target = api.onBoard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertNotNull(target);
    }
}
