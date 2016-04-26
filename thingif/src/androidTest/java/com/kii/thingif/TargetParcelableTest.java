package com.kii.thingif;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TargetParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        Target target = new TargetStandaloneThing("thing1234", "token1234");
        Parcel parcel = Parcel.obtain();
        target.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Target deserializedTarget = TargetStandaloneThing.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(target.getTypedID(), deserializedTarget.getTypedID());
        Assert.assertEquals(target.getAccessToken(), deserializedTarget.getAccessToken());
    }
}
