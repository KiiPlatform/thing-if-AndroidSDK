package com.kii.iotcloud;

import org.junit.Before;

public abstract class SmallTestBase {
    @Before
    public void before() throws Exception {
        // GsonRepository will cache the schema to static field.
        // So unit test must clear that cache in order to avoid the side effect.
        GsonRepository.clearCache();
    }
}
