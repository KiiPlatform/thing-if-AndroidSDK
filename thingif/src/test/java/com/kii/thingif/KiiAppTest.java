package com.kii.thingif;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class KiiAppTest {
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppIDTest() throws Exception {
        new KiiApp(null, "appkey", Site.JP);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppIDTest() throws Exception {
        new KiiApp("", "appkey", Site.JP);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppKeyTest() throws Exception {
        new KiiApp("appid", null, Site.JP);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppKeyTest() throws Exception {
        new KiiApp("appid", "", Site.JP);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullSiteTest() throws Exception {
        new KiiApp("appid", "appkey", (Site) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullBaseUrlTest() throws Exception {
        new KiiApp("appid", "appkey", (String)null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyBaseUrlTest() throws Exception {
        new KiiApp("appid", "appkey", "");
    }
}
