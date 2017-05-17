package com.kii.thingiftrait.trigger;

import android.os.Parcel;

import com.kii.thingiftrait.ServerError;
import com.kii.thingiftrait.SmallTestBase;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TriggeredServerCodeResultTest extends SmallTestBase{
    @Test
    public void objectTest() throws Exception {
        boolean succeeded = true;
        JSONObject returnedValue = new JSONObject("{\"f1\":\"aaa\",\"f2\":false,\"f3\":1000,\"f4\":100.05,\"f5\":[1,2,3],\"f6\":{}}");
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        assertJSONObject(returnedValue, deserializedResult.getReturnedValueAsJsonObject());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void arrayTest() throws Exception {
        boolean succeeded = false;
        JSONArray returnedValue = new JSONArray("[123, \"abc\", true, 123.05, [], {}]");
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        assertJSONArray(returnedValue, deserializedResult.getReturnedValueAsJsonArray());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void stringTest() throws Exception {
        boolean succeeded = true;
        String returnedValue = "abc";
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsString());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void emptyStringTest() throws Exception {
        boolean succeeded = false;
        String returnedValue = "";
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsString());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void intTest() throws Exception {
        boolean succeeded = true;
        Integer returnedValue = 1000;
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsInteger());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsNumber());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void longTest() throws Exception {
        boolean succeeded = false;
        Long returnedValue = (long)(Integer.MAX_VALUE * 2);
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsLong());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsNumber());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertNull(deserializedResult.getError());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
    }
    @Test
    public void doubleTest() throws Exception {
        boolean succeeded = true;
        Double returnedValue = 10.05;
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsDouble());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsNumber());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void booleanTest() throws Exception {
        boolean succeeded = false;
        Boolean returnedValue = Boolean.FALSE;
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValueAsBoolean());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void nullTest() throws Exception {
        boolean succeeded = true;
        Object returnedValue = null;
        long executedAt = System.currentTimeMillis();
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, returnedValue, executedAt, endpoint, null);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertEquals(returnedValue, deserializedResult.getReturnedValue());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertNull(deserializedResult.getError());
    }
    @Test
    public void errorTest() throws Exception {
        boolean succeeded = false;
        long executedAt = System.currentTimeMillis();
        ServerError error = new ServerError("Error found", "RUNTIME_ERROR", "faital error");
        String endpoint = "func1";

        TriggeredServerCodeResult result = new TriggeredServerCodeResult(succeeded, null, executedAt, endpoint, error);
        Parcel parcel = Parcel.obtain();
        result.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        TriggeredServerCodeResult deserializedResult = TriggeredServerCodeResult.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(succeeded, deserializedResult.isSucceeded());
        Assert.assertNull(deserializedResult.getReturnedValue());
        Assert.assertEquals(executedAt, deserializedResult.getExecutedAt());
        Assert.assertEquals(error.getErrorMessage(), deserializedResult.getError().getErrorMessage());
        Assert.assertEquals(error.getErrorCode(), deserializedResult.getError().getErrorCode());
        Assert.assertEquals(endpoint, deserializedResult.getEndpoint());
        Assert.assertEquals(error.getDetailMessage(), deserializedResult.getError().getDetailMessage());
    }
}
