package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.query.TimeRange;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

public class TimeRangeAdapterTest {

    @Test
    public void deserializationTest() throws Exception {
        TimeRange expected = new TimeRange(new Date(1), new Date(100));

        JsonObject json = new JsonObject();
        json.addProperty("from", expected.getFrom().getTime());
        json.addProperty("to", expected.getTo().getTime());

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(TimeRange.class, new TimeRangeAdapter())
                .create();

        TimeRange actual = gson.fromJson(json, TimeRange.class);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected, actual);
    }
}
