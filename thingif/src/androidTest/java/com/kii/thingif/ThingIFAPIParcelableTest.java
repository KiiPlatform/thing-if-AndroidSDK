package com.kii.thingif;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import android.support.test.InstrumentationRegistry;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.SetBrightnessResult;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPIParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        String appID = "a12345";
        String appKey = "a1234567890k";
        String appHost = "api-jp.kii.com";
        String thingType = "SmartLight";
        String schemaName = "DemoLight";
        int schemaVersion = 2;
        TypedID ownerID = new TypedID(TypedID.Types.USER, "user1234");
        String ownerAccessToken = "abcd-1234";
        Owner owner = new Owner(ownerID, ownerAccessToken);

        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(thingType, schemaName, schemaVersion, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetBrightness.class, SetBrightnessResult.class);
        Schema schema = sb.build();
        KiiApp app = new KiiApp(appID, appKey, appHost);
        ThingIFAPIBuilder icab = ThingIFAPIBuilder.newBuilder(InstrumentationRegistry.getTargetContext(), app, owner);
        icab.addSchema(schema);

        ThingIFAPI api = icab.build();

        Parcel parcel = Parcel.obtain();
        api.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ThingIFAPI deserializedApi = ThingIFAPI.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(appID, deserializedApi.getAppID());
        Assert.assertEquals(appKey, deserializedApi.getAppKey());
        Assert.assertEquals("https://" + appHost, deserializedApi.getBaseUrl());
        Assert.assertEquals(appID, deserializedApi.getAppID());
        Assert.assertEquals(ownerID, deserializedApi.getOwner().getTypedID());
        Assert.assertEquals(ownerAccessToken, deserializedApi.getOwner().getAccessToken());

        Assert.assertEquals(1, deserializedApi.getSchemas().size());
        Assert.assertEquals(schemaName, deserializedApi.getSchemas().get(0).getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedApi.getSchemas().get(0).getSchemaVersion());
        Assert.assertEquals(thingType, deserializedApi.getSchemas().get(0).getThingType());
    }
}
