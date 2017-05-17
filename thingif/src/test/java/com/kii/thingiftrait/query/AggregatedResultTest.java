package com.kii.thingiftrait.query;

import android.os.Parcel;

import com.kii.thingiftrait.states.AirConditionerState;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AggregatedResultTest {

    @Test
    public void equals_hashCodeTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        AirConditionerState air1 = new AirConditionerState();
        air1.currentTemperature = 25;
        air1.power = false;
        HistoryState<AirConditionerState> airHS = new HistoryState<>(air1, new Date());
        List<HistoryState<AirConditionerState>> list1 = new ArrayList<>();
        list1.add(airHS);
        List<HistoryState<AirConditionerState>> list2 = new ArrayList<>();

        AggregatedResult<Integer, AirConditionerState> target =
                new AggregatedResult<>(range, 25, list1);
        AggregatedResult<Integer, AirConditionerState> sameOne =
                new AggregatedResult<>(range, 25, list1);
        AggregatedResult<Integer, AirConditionerState> different1 =
                new AggregatedResult<>(range, 10, list1);
        AggregatedResult<Integer, AirConditionerState> different2 =
                new AggregatedResult<>(range, 25, list2);
        AggregatedResult<Integer, AirConditionerState> different3 =
                new AggregatedResult<>(range, 25, null);

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(different1));
        Assert.assertNotSame(target.hashCode(), different1.hashCode());
        Assert.assertFalse(target.equals(different2));
        Assert.assertNotSame(target.hashCode(), different2.hashCode());
        Assert.assertFalse(target.equals(different3));
        Assert.assertNotSame(target.hashCode(), different3.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)airHS));
    }

    @Test
    public void parcelableTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        Integer value = 25;
        AirConditionerState air1 = new AirConditionerState();
        air1.currentTemperature = 25;
        air1.power = false;
        HistoryState<AirConditionerState> airHS = new HistoryState<>(air1, new Date());
        List<HistoryState<AirConditionerState>> list = new ArrayList<>();
        list.add(airHS);

        AggregatedResult<Integer, AirConditionerState> src =
                new AggregatedResult<>(range, value, list);

        Assert.assertNotNull(src);
        Assert.assertEquals(range, src.getTimeRange());
        Assert.assertEquals(value, src.getValue());
        Assert.assertNotNull(src.getAggregatedObjects());
        Assert.assertTrue(Arrays.equals(list.toArray(), src.getAggregatedObjects().toArray()));

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AggregatedResult<Integer, AirConditionerState> dest =
                AggregatedResult.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertEquals(range, dest.getTimeRange());
        Assert.assertEquals(value, dest.getValue());
        Assert.assertNotNull(dest.getAggregatedObjects());
        Assert.assertTrue(Arrays.equals(list.toArray(), dest.getAggregatedObjects().toArray()));
    }

    @Test
    public void parcelableNullableTest() {
        TimeRange range = new TimeRange(new Date(1), new Date(100));
        Integer value = 30;
        AirConditionerState air1 = new AirConditionerState();
        air1.currentTemperature = 10;
        air1.power = true;

        AggregatedResult<Integer, AirConditionerState> src =
                new AggregatedResult<>(range, value, null);

        Assert.assertNotNull(src);
        Assert.assertEquals(range, src.getTimeRange());
        Assert.assertEquals(value, src.getValue());
        Assert.assertNull(src.getAggregatedObjects());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AggregatedResult<Integer, AirConditionerState> dest =
                AggregatedResult.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertEquals(range, dest.getTimeRange());
        Assert.assertEquals(value, dest.getValue());
        Assert.assertNull(dest.getAggregatedObjects());
    }
}
