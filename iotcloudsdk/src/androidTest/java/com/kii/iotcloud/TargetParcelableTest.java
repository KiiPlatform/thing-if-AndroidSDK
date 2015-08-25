package com.kii.iotcloud;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TargetParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        Target target = new Target(new TypedID(TypedID.Types.THING, "thing1234"), "token1234");
        Parcel parcel = Parcel.obtain();
        target.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Target deserializedTarget = Target.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(target.getID(), deserializedTarget.getID());
        Assert.assertEquals(target.getAccessToken(), deserializedTarget.getAccessToken());
    }
}
