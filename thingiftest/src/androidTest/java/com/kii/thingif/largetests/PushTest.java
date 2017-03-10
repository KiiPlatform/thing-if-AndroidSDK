package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.PushBackend;
import com.kii.thingif.ThingIFAPI;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PushTest extends LargeTestCaseBase {
    @Test
    public void basicTest() throws Exception {
        ThingIFAPI api = this.createDefaultThingIFAPI();
        String installationID = api.installPush("GCM-REGISTRATION-ID", PushBackend.GCM);
        api.uninstallPush(installationID);
    }
}