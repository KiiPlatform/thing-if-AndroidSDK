package com.kii.thingiftrait.command;

import android.os.Parcel;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ActionResultTest {
    @Test
    public void baseTest() {
        ActionResult result1 = new ActionResult("turnPower", true, null, null);
        Assert.assertEquals("turnPower", result1.getActionName());
        Assert.assertNull(result1.getErrorMessage());
        Assert.assertTrue(result1.isSucceeded());

        ActionResult result2 = new ActionResult("turnPower", false, "value invalid", null);
        Assert.assertEquals("turnPower", result2.getActionName());
        Assert.assertEquals("value invalid", result2.getErrorMessage());
        Assert.assertFalse(result2.isSucceeded());
    }
    @Test
    public void equals_hashCodeTest() {
        ActionResult result = new ActionResult("turnPower", true, null, null);

        Object[] sameResults = {
                result,
                new ActionResult("turnPower", true, null, null)
        };

        for (int i=0; i < sameResults.length; i++) {
            Assert.assertEquals(
                    "failed to test equals for ["+i+"]",
                    result,
                    sameResults[i]);
            Assert.assertEquals(
                    "failed to test hashCode for ["+i+"]",
                    result.hashCode(),
                    sameResults[i].hashCode());
        }

        ActionResult result2 = new ActionResult("turnPower", true, null, null);
        ActionResult result3 = new ActionResult("turnPower", true, null, null);

        // test symmetric equals
        Assert.assertTrue(result.equals(result2) == result2.equals(result));

        // test transitive equals
        Assert.assertTrue(result.equals(result2));
        Assert.assertTrue(result2.equals(result3));
        Assert.assertTrue(result.equals(result3));


        Assert.assertFalse("should not equals to null", result.equals(null));
        ActionResult[] diffObjs = {
                new ActionResult("turnPower", false, "value invalid", null),
                new ActionResult("setPresetTemp", true, null, null)
        };

        for (int i=0; i<diffObjs.length; i++) {
            Assert.assertFalse(
                    "failed to test equals for ["+i+"]",
                    result.equals(diffObjs[i]));

            Assert.assertFalse("failed to test hashCode for ["+i+"]",
                    result.hashCode() == diffObjs[i].hashCode());
        }
    }

    @Test
    public void parcelableTest() {
        ActionResult result = new ActionResult("turnPower", true, null, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ActionResult deserializedResult = new ActionResult(parcel);
        Assert.assertEquals(result.getActionName(), deserializedResult.getActionName());
        Assert.assertEquals(result.getErrorMessage(), deserializedResult.getErrorMessage());
        Assert.assertEquals(result.isSucceeded(), deserializedResult.isSucceeded());

        ActionResult result1 = new ActionResult("turnPower", false, "value is invalid", null);
        parcel = Parcel.obtain();
        result1.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ActionResult deserializedResult1 = new ActionResult(parcel);
        Assert.assertEquals(result1.getActionName(), deserializedResult1.getActionName());
        Assert.assertEquals(result1.getErrorMessage(), deserializedResult1.getErrorMessage());
        Assert.assertEquals(result1.isSucceeded(), deserializedResult1.isSucceeded());
    }
}
