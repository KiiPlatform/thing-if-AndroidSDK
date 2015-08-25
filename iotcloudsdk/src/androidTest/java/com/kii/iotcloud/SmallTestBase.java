package com.kii.iotcloud;

import org.junit.Before;

public abstract class SmallTestBase {
    @Before
    public void before() throws Exception {
        GsonRepository.clearCache();
    }
}
