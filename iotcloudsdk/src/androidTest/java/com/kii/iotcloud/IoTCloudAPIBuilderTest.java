package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;
import android.test.mock.MockContext;

import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testmodel.LightState;
import com.kii.iotcloud.testmodel.SetColor;
import com.kii.iotcloud.testmodel.SetColorResult;
import com.kii.iotcloud.testmodel.TurnPower;
import com.kii.iotcloud.testmodel.TurnPowerResult;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IoTCloudAPIBuilderTest extends SmallTestBase {
    @Test
    public void basicTest() throws Exception {
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        Schema schema = sb.build();
        builder.addSchema(schema);
        IoTCloudAPI api = builder.build();

        Assert.assertEquals("appid", api.getAppID());
        Assert.assertEquals("appkey", api.getAppKey());
        Assert.assertEquals(Site.JP.getBaseUrl(), api.getBaseUrl());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), api.getOwner().getID());
        Assert.assertEquals("token", api.getOwner().getAccessToken());
    }
    @Test(expected = IllegalStateException.class)
    public void buildWithEmptySchemasTest() throws Exception {
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        builder.build();
    }
    @Test(expected = IllegalArgumentException.class)
    public void addSchemaWithNullSchemaTest() throws Exception {
        IoTCloudAPIBuilder builder = IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        builder.addSchema(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullContextTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(null, "appid", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppIDTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), null, "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppIDTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "", "appkey", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppKeyTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", null, Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppKeyTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "", Site.JP, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullSiteTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", (Site)null, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullOwnerTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", Site.JP, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullBaseUrlTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", (String)null, new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyBaseUrlTest() throws Exception {
        IoTCloudAPIBuilder.newBuilder(new MockContext(), "appid", "appkey", "", new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
}