package com.kii.thingif.query;

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
}
