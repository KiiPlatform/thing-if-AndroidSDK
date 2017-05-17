package com.kii.thingiftrait.command;

import android.os.Parcel;

import com.kii.thingiftrait.actions.SetPresetHumidity;
import com.kii.thingiftrait.actions.SetPresetTemperature;
import com.kii.thingiftrait.actions.TurnPower;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AliasActionTest {
    private List<Action> getDefaultACActions() {
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(true));
        actions.add(new SetPresetTemperature(23));
        return actions;
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullAliasTest() {
        new AliasAction(null, getDefaultACActions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyAliasTest() {
        new AliasAction("", getDefaultACActions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullActionTest() {
        new AliasAction("alias", null);
    }

    @Test
    public void equals_hashCodeTest() {
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));

        AliasAction aliasAction =
                new AliasAction("alias", actions1);
        Object[] sameObjects = {
                aliasAction,
                new AliasAction("alias", actions1)};

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



        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        Object[] diffObjects = {
                null,
                new AliasAction("another alias", actions2),
                new AliasAction("alias", actions2),
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
    public void retrieveActionTest() {
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        AliasAction aliasAction = new AliasAction("alias", actions1);

        // test getActions by action class
        Assert.assertEquals(1, aliasAction.getActions(TurnPower.class).size());
        Assert.assertTrue(aliasAction.getActions(TurnPower.class).get(0).getPower());

        Assert.assertEquals(1, aliasAction.getActions(SetPresetTemperature.class).size());
        Assert.assertEquals(
                23,
                aliasAction.getActions(SetPresetTemperature.class).get(0).getTemperature().intValue());
        Assert.assertEquals(0, aliasAction.getActions(SetPresetHumidity.class).size());

        // test getActions
        Assert.assertEquals(2, aliasAction.getActions().size());
        Action action1 = aliasAction.getActions().get(0);
        Assert.assertTrue(action1 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)action1).getPower());
        Action action2 = aliasAction.getActions().get(1);
        Assert.assertTrue(action2 instanceof SetPresetTemperature);
        Assert.assertEquals(23, ((SetPresetTemperature)action2).getTemperature().intValue());
    }

    @Test
    public void parcelableTest() {
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        actions1.add(new SetPresetTemperature(23));
        AliasAction aliasAction = new AliasAction("alias", actions1);

        Parcel parcel = Parcel.obtain();
        aliasAction.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AliasAction deserializedAction = new AliasAction(parcel);

        Assert.assertEquals(aliasAction.getAlias(), deserializedAction.getAlias());
        Assert.assertEquals(2, deserializedAction.getActions().size());
        Action action1 = deserializedAction.getActions().get(0);
        Assert.assertTrue(action1 instanceof TurnPower);
        Assert.assertTrue(((TurnPower)action1).getPower());
        Action action2 = deserializedAction.getActions().get(1);
        Assert.assertTrue(action2 instanceof SetPresetTemperature);
        Assert.assertEquals(23, ((SetPresetTemperature)action2).getTemperature().intValue());
    }
}
