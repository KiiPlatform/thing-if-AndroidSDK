package com.kii.thingiftrait.query;

import android.os.Parcel;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class TimeRangeTest {

    @Test
    public void parcelableTest() throws Exception {
        Date from = new Date(1);
        Date to = new Date(100);
        TimeRange src = new TimeRange(from, to);

        Assert.assertNotNull(src);
        Assert.assertEquals(from, src.getFrom());
        Assert.assertEquals(to, src.getTo());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TimeRange dest = TimeRange.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertEquals(from, dest.getFrom());
        Assert.assertEquals(to, dest.getTo());
    }

    @Test
    public void equals_hashCodeTest() {
        Date date1 = new Date(1);
        Date date2 = new Date(2);
        Date date3 = new Date(3);
        TimeRange target = new TimeRange(date1, date2);
        TimeRange sameOne = new TimeRange(date1, date2);
        TimeRange differentOne = new TimeRange(date1, date3);

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals(date1));
    }
}
