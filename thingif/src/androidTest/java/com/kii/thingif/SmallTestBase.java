package com.kii.thingif;

import android.test.AndroidTestCase;

import com.kii.thingif.internal.InternalUtils;

import org.junit.Before;

public abstract class SmallTestBase extends AndroidTestCase {
    @Before
    public void before() throws Exception {
        // GsonRepository will cache the schema to static field.
        // So unit test must clear that cache in order to avoid the side effect.
        InternalUtils.gsonRepositoryClearCache();
    }
}
