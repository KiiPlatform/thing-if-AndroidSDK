package com.kii.thingif.trigger;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
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
