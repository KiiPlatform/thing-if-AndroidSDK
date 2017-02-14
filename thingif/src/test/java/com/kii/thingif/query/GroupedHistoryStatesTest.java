package com.kii.thingif.query;

import android.os.Parcel;

import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class GroupedHistoryStatesTest {

    @Test
    public void equals_hashCodeTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        AirConditionerState air = new AirConditionerState();
        HumidityState hum = new HumidityState();
        Date createdAd = new Date(1);
        HistoryState airHS = new HistoryState<AirConditionerState>(air, createdAd);
        HistoryState humHS = new HistoryState<HumidityState>(hum, createdAd);
        List<HistoryState> list1 = new ArrayList<HistoryState>();
        list1.add(airHS);
        List<HistoryState> list2 = new ArrayList<HistoryState>();
        list2.add(humHS);

        GroupedHistoryStates target = new GroupedHistoryStates(range, list1);
        GroupedHistoryStates sameOne = new GroupedHistoryStates(range, list1);
        GroupedHistoryStates differentOne = new GroupedHistoryStates(range, list2);

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)airHS));
    }

    @Test
    public void parcelableTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        AirConditionerState air1 = new AirConditionerState();
        air1.currentTemperature = 25;
        air1.power = false;
        HistoryState state1 = new HistoryState(air1, new Date());
        AirConditionerState air2 = new AirConditionerState();
        air2.currentTemperature = 10;
        air2.power = true;
        HistoryState state2 = new HistoryState(air2, new Date());
        List<HistoryState> list = new ArrayList<HistoryState>();
        list.add(state1);
        list.add(state2);
        GroupedHistoryStates src = new GroupedHistoryStates(range, list);

        Assert.assertNotNull(src);
        Assert.assertEquals(range, src.getTimeRange());
        Assert.assertTrue(Arrays.equals(list.toArray(), src.getObjects().toArray()));

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GroupedHistoryStates dest = GroupedHistoryStates.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertEquals(range, dest.getTimeRange());
        Assert.assertTrue(Arrays.equals(list.toArray(), dest.getObjects().toArray()));
    }
}
