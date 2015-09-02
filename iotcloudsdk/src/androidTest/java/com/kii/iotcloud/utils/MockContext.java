package com.kii.iotcloud.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MockContext extends android.test.mock.MockContext {
    @Override
    public Context getApplicationContext() {
        // Default implementation of MockContext#getApplicationContext() throws java.lang.UnsupportedOperationException
        return this;
    }
    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        // Default implementation of MockContext#getSharedPreferences(String, int) throws java.lang.UnsupportedOperationException
        return null;
    }
}
