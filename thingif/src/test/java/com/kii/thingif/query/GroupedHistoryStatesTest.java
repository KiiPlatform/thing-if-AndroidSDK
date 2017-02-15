package com.kii.thingif.query;

import android.os.Parcel;

import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        HistoryState<AirConditionerState> airHS = new HistoryState<>(air, createdAd);
        HistoryState<HumidityState> humHS = new HistoryState<>(hum, createdAd);
        List<HistoryState<AirConditionerState>> list1 =
                new ArrayList<HistoryState<AirConditionerState>>();
        list1.add(airHS);
        List<HistoryState<HumidityState>> list2 = new ArrayList<HistoryState<HumidityState>>();
        list2.add(humHS);

        GroupedHistoryStates<AirConditionerState> target = new GroupedHistoryStates<>(range, list1);
        GroupedHistoryStates<AirConditionerState> sameOne = new GroupedHistoryStates<>(range, list1);
        GroupedHistoryStates<HumidityState> differentOne = new GroupedHistoryStates<>(range, list2);

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
        HistoryState<AirConditionerState> state1 = new HistoryState<>(air1, new Date());
        AirConditionerState air2 = new AirConditionerState();
        air2.currentTemperature = 10;
        air2.power = true;
        HistoryState<AirConditionerState> state2 = new HistoryState<>(air2, new Date());
        List<HistoryState<AirConditionerState>> list =
                new ArrayList<HistoryState<AirConditionerState>>();
        list.add(state1);
        list.add(state2);
        GroupedHistoryStates<AirConditionerState> src = new GroupedHistoryStates<AirConditionerState>(range, list);

        Assert.assertNotNull(src);
        Assert.assertEquals(range, src.getTimeRange());
        Assert.assertTrue(Arrays.equals(list.toArray(), src.getObjects().toArray()));

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        GroupedHistoryStates<AirConditionerState> dest =
                GroupedHistoryStates.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertEquals(range, dest.getTimeRange());
        Assert.assertTrue(Arrays.equals(list.toArray(), dest.getObjects().toArray()));
    }
}
