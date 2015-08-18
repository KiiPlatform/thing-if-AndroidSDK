package com.kii.iotcloud;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() throws Exception {
        super(Application.class);
        IoTCloudAPIBuilder b = IoTCloudAPIBuilder.newBuilder(null, "", "", Site.JP, null);
        IoTCloudAPI api = b.build();
        api.onBoard("", "", null, null);
    }
}