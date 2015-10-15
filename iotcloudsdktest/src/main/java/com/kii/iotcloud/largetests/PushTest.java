package com.kii.iotcloud.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.IoTCloudAPI;
import com.kii.iotcloud.PushBackend;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PushTest extends LargeTestCaseBase {
    @Test
    public void basicTest() throws Exception {
        IoTCloudAPI api = this.craeteIoTCloudAPIWithDemoSchema(TargetTestServer.DEV_SERVER_1);
        String installationID = api.installPush("GCM-REGISTRATION-ID", PushBackend.GCM);
        api.uninstallPush(installationID);
    }
}