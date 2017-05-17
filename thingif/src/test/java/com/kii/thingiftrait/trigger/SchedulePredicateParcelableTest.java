package com.kii.thingiftrait.trigger;

import android.os.Parcel;

import com.kii.thingiftrait.SmallTestBase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class SchedulePredicateParcelableTest extends SmallTestBase {
    @Test
    public void parcelableTest() throws Exception {
        SchedulePredicate predicate = new SchedulePredicate("1 * * * *");
        Parcel parcel = Parcel.obtain();
        predicate.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        SchedulePredicate parceled =
                SchedulePredicate.CREATOR.createFromParcel(parcel);
        Assert.assertEquals(predicate.getSchedule(), parceled.getSchedule());
        Assert.assertEquals(predicate.getEventSource(),
                parceled.getEventSource());
    }
}
