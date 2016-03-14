package com.kii.thingif.trigger;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ServerCodeTest extends SmallTestBase {
    @Test
    public void constructorTest1() throws Exception {
        ServerCode serverCode = new ServerCode("endpoint", "executor_access_token");
        assertEquals("endpoint", serverCode.getEndpoint());
        assertEquals("executor_access_token", serverCode.getExecutorAccessToken());
    }
    @Test(expected=IllegalArgumentException.class)
    public void constructorTest2() throws Exception {
        new ServerCode(null, "executor_access_token");
    }
    @Test
    public void constructorTest3() throws Exception {
        ServerCode serverCode = new ServerCode("endpoint", null);
        assertEquals("endpoint", serverCode.getEndpoint());
        assertNull(serverCode.getExecutorAccessToken());
    }
}
