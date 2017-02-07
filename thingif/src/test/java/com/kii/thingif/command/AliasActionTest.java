package com.kii.thingif.command;

import android.os.Parcel;

import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class AliasActionTest {
    @Test(expected = IllegalArgumentException.class)
    public void createWithNullAliasTest() {
        new AliasAction<>(null, new AirConditionerActions(true, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyAliasTest() {
        new AliasAction<>("", new AirConditionerActions(false, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullActionTest() {
        new AliasAction<>("alias", (Action)null);
    }

    @Test
    public void equals_hashCodeTest() {
        AirConditionerActions action = new AirConditionerActions(true, null);
        AliasAction<AirConditionerActions> aliasAction =
                new AliasAction<>("alias", action);

        Object[] sameObjects = {
                aliasAction,
                new AliasAction<>("alias", action)};

        for(int i=0; i<sameObjects.length; i++) {
            Assert.assertEquals(
                    "failed to test equals for ["+i+"]",
                    aliasAction,
                    sameObjects[i]);

            Assert.assertEquals(
                    "failed to test hashCode for ["+i+"]",
                    aliasAction.hashCode(),
                    sameObjects[i].hashCode());
        }

        Object[] diffObjects = {
                null,
                new AliasAction<>("another alias", action),
                new AliasAction<>("humidity", new HumidityActions(45)),
                new AliasAction<>("alias", new HumidityActions(45)),
                23
        };
        for (int i=0; i < diffObjects.length; i++) {
            Assert.assertFalse(
                    "failed to test equals for ["+i+"]",
                    aliasAction.equals(diffObjects[i]));
            if (diffObjects[i] != null) {
                Assert.assertFalse(
                        "failed to test hashCode for [" + i + "]",
                        aliasAction.hashCode() == diffObjects[i].hashCode());
            }
        }
    }

    @Test
    public void parcelableTest() {
        AirConditionerActions action = new AirConditionerActions(false, null);
        AliasAction<AirConditionerActions> aliasAction = new AliasAction<>("alias", action);
        Parcel parcel = Parcel.obtain();
        aliasAction.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        AliasAction<AirConditionerActions> deserializedAction = new AliasAction<>(parcel);

        Assert.assertEquals(aliasAction.getAlias(), deserializedAction.getAlias());
        Assert.assertEquals(aliasAction.getAction(), deserializedAction.getAction());
        Assert.assertEquals(aliasAction, deserializedAction);
    }
}
