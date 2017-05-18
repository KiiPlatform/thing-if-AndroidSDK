package com.kii.thing_if.query;

import android.os.Parcel;

import com.kii.thing_if.states.AirConditionerState;
import com.kii.thing_if.states.HumidityState;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class HistoryStateTest {

    @Test
    public void parcelableTest() {
        AirConditionerState state = new AirConditionerState();
        state.currentTemperature = 25;
        state.power = false;
        Date createdAt = new Date();
        HistoryState src = new HistoryState(state, createdAt);

        Assert.assertNotNull(src);
        Assert.assertEquals(state, src.getState());
        Assert.assertEquals(createdAt, src.getCreatedAt());

        Parcel parcel = Parcel.obtain();
        src.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        HistoryState dest = HistoryState.CREATOR.createFromParcel(parcel);

        Assert.assertNotNull(dest);
        Assert.assertNotNull(dest.getState());
        Assert.assertTrue(dest.getState() instanceof AirConditionerState);
        AirConditionerState destState = (AirConditionerState)dest.getState();
        Assert.assertNotSame(state, destState);
        Assert.assertEquals(state.currentTemperature, destState.currentTemperature);
        Assert.assertEquals(state.power, destState.power);
        Assert.assertEquals(createdAt, dest.getCreatedAt());
    }

    @Test
    public void equals_hashCodeTest() {
        AirConditionerState state1 = new AirConditionerState();
        HumidityState state2 = new HumidityState();
        Date createdAd = new Date(1);
        HistoryState target = new HistoryState(state1, createdAd);
        HistoryState sameOne = new HistoryState(state1, createdAd);
        HistoryState differentOne = new HistoryState(state2, createdAd);

        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals(state1));
    }
}
