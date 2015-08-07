package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class Junit4Test {
    @Test
    public void test() throws Exception {
       int i = 1;
        Assert.assertEquals(2, i);
    }
}

