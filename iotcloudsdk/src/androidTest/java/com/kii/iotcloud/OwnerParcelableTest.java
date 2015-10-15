package com.kii.iotcloud;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OwnerParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        Owner owner = new Owner(new TypedID(TypedID.Types.USER, "user1234"), "token1234");
        Parcel parcel = Parcel.obtain();
        owner.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Owner deserializedOwner = Owner.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(owner.getTypedID(), deserializedOwner.getTypedID());
        Assert.assertEquals(owner.getAccessToken(), deserializedOwner.getAccessToken());
    }
}
