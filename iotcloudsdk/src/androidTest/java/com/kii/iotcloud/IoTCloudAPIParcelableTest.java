package com.kii.iotcloud;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;
import com.kii.iotcloud.utils.MockContext;

import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.testschemas.LightState;
import com.kii.iotcloud.testschemas.SetBrightness;
import com.kii.iotcloud.testschemas.SetBrightnessResult;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IoTCloudAPIParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        String appID = "a12345";
        String appKey = "a1234567890k";
        String baseUrl = "https://kii.com";
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
        IoTCloudAPIBuilder icab = IoTCloudAPIBuilder.newBuilder(new MockContext(), appID, appKey, baseUrl, owner);
        icab.addSchema(schema);

        IoTCloudAPI api = icab.build();

        Parcel parcel = Parcel.obtain();
        api.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        IoTCloudAPI deserializedApi = IoTCloudAPI.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(appID, deserializedApi.getAppID());
        Assert.assertEquals(appKey, deserializedApi.getAppKey());
        Assert.assertEquals(baseUrl, deserializedApi.getBaseUrl());
        Assert.assertEquals(appID, deserializedApi.getAppID());
        Assert.assertEquals(ownerID, deserializedApi.getOwner().getID());
        Assert.assertEquals(ownerAccessToken, deserializedApi.getOwner().getAccessToken());

        Assert.assertEquals(1, deserializedApi.getSchemas().size());
        Assert.assertEquals(schemaName, deserializedApi.getSchemas().get(0).getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedApi.getSchemas().get(0).getSchemaVersion());
        Assert.assertEquals(thingType, deserializedApi.getSchemas().get(0).getThingType());
    }
}
