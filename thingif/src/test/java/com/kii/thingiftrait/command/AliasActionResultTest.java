package com.kii.thingiftrait.command;


import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class AliasActionResultTest {
    @Test
    public void baseTest() {
        ActionResult result1 = new ActionResult("turnPower", true, null, null);
        ActionResult result2 = new ActionResult("setPresetTemperature", true, null, null);
        List<ActionResult> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);

        AliasActionResult aliasResult = new AliasActionResult("alias", results);

        Assert.assertEquals("alias", aliasResult.getAlias());
        Assert.assertEquals(2, aliasResult.getResults().size());
        Assert.assertEquals(result1, aliasResult.getResults().get(0));
        Assert.assertEquals(result2, aliasResult.getResults().get(1));
    }

    @Test
    public void equals_hashCodeTest() {
        ActionResult result1 = new ActionResult("turnPower", true, null, null);
        ActionResult result2 = new ActionResult("setPresetTemp", false, "value invalid", null);
        List<ActionResult> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);

        AliasActionResult aliasResult = new AliasActionResult("alias", results);

        List<ActionResult> sameResultArray = new ArrayList<>();
        sameResultArray.add(result1);
        sameResultArray.add(result2);

        Object[] sameResults = {
                aliasResult,
                new AliasActionResult("alias", sameResultArray)
        };

        for (int i=0; i < sameResults.length; i++) {
            Assert.assertEquals(
                    "failed to test equals for ["+i+"]",
                    aliasResult,
                    sameResults[i]);
            Assert.assertEquals(
                    "failed to test hashCode for ["+i+"]",
                    aliasResult.hashCode(),
                    sameResults[i].hashCode());
        }

        AliasActionResult aliasResult2 = new AliasActionResult("alias", sameResultArray);
        AliasActionResult aliasResult3 = new AliasActionResult("alias", sameResultArray);

        // test symmetric equals
        Assert.assertTrue(aliasResult.equals(aliasResult2) == aliasResult2.equals(aliasResult));

        // test transitive equals
        Assert.assertTrue(aliasResult.equals(aliasResult2));
        Assert.assertTrue(aliasResult2.equals(aliasResult3));
        Assert.assertTrue(aliasResult.equals(aliasResult3));


        Assert.assertFalse("should not equals to null", aliasResult.equals(null));

        List<ActionResult> diffResultArray = new ArrayList<>();
        diffResultArray.add(result1);
        AliasActionResult[] diffObjs = {
                new AliasActionResult("alias", diffResultArray),
                new AliasActionResult("anotherAlias", results)
        };

        for (int i=0; i<diffObjs.length; i++) {
            Assert.assertFalse(
                    "failed to test equals for ["+i+"]",
                    aliasResult.equals(diffObjs[i]));

            Assert.assertFalse("failed to test hashCode for ["+i+"]",
                    aliasResult.hashCode() == diffObjs[i].hashCode());
        }
    }

    @Test
    public void parcelableTest() {
        ActionResult result1 = new ActionResult("turnPower", true, null, null);
        ActionResult result2 = new ActionResult("setPresetTemperature", true, null, null);
        List<ActionResult> results = new ArrayList<>();
        results.add(result1);
        results.add(result2);

        AliasActionResult aliasResult = new AliasActionResult("alias", results);

        Parcel parcel = Parcel.obtain();
        aliasResult.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        AliasActionResult deserializedResult = new AliasActionResult(parcel);
        Assert.assertEquals(aliasResult.getAlias(), deserializedResult.getAlias());
        Assert.assertEquals(aliasResult.getResults().size(), deserializedResult.getResults().size());
        Assert.assertEquals(aliasResult.getResults().get(0), deserializedResult.getResults().get(0));
        Assert.assertEquals(aliasResult.getResults().get(1), deserializedResult.getResults().get(1));
    }
}
