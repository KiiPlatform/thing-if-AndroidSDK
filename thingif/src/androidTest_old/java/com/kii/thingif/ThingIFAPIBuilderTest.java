package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;

import android.support.test.InstrumentationRegistry;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.testschemas.TurnPowerResult;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPIBuilderTest extends SmallTestBase {
    @Test
    public void basicTest() throws Exception {
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        Schema schema = sb.build();
        builder.addSchema(schema);
        ThingIFAPI api = builder.build();

        Assert.assertEquals("appid", api.getAppID());
        Assert.assertEquals("appkey", api.getAppKey());
        Assert.assertEquals(Site.JP.getBaseUrl(), api.getBaseUrl());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), api.getOwner().getTypedID());
        Assert.assertEquals("token", api.getOwner().getAccessToken());
    }
    @Test(expected = IllegalStateException.class)
    public void buildWithEmptySchemasTest() throws Exception {
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        builder.build();
    }
    @Test(expected = IllegalArgumentException.class)
    public void addSchemaWithNullSchemaTest() throws Exception {
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
        builder.addSchema(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullContextTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(null, new KiiApp("appid", "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppIDTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp(null, "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppIDTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("", "appkey", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAppKeyTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", null, Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyAppKeyTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "", Site.JP), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullSiteTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", (Site) null), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullOwnerTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", Site.JP), null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullBaseUrlTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", (String) null), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithEmptyBaseUrlTest() throws Exception {
        ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), new KiiApp("appid", "appkey", ""), new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token"));
    }
}