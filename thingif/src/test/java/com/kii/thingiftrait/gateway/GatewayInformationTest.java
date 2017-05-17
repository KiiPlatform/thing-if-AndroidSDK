package com.kii.thingiftrait.gateway;

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class GatewayInformationTest {
    @Test
    public void parcelableTest() {
        GatewayInformation expected = new GatewayInformation("vendor-id");

        Parcel parcel = Parcel.obtain();
        expected.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GatewayInformation deserialized = GatewayInformation.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(expected.getVendorThingID(), deserialized.getVendorThingID());
    }
}
