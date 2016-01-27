package com.kii.thingif;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.trigger.TriggerServerCodeResult;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ThingIFAPI_ListTriggerServerCodeResultsTest extends ThingIFAPITestBase {
    @Test
    public void listTriggerServerCodeResultsTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);
        String paginationKey = "pagination-12345-key";

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        TriggerServerCodeResult serverCodeResult1 = new TriggerServerCodeResult(true, "1234", System.currentTimeMillis(), null);
        TriggerServerCodeResult serverCodeResult2 = new TriggerServerCodeResult(true, "12.34", System.currentTimeMillis() + 1000, null);
        TriggerServerCodeResult serverCodeResult3 = new TriggerServerCodeResult(true, "true", System.currentTimeMillis() + 2000, null);
        TriggerServerCodeResult serverCodeResult4 = new TriggerServerCodeResult(true, "\"abcd\"", System.currentTimeMillis() + 3000, null);
        TriggerServerCodeResult serverCodeResult5 = new TriggerServerCodeResult(true, "{\"field\":\"abcd\"}", System.currentTimeMillis() + 4000, null);
        TriggerServerCodeResult serverCodeResult6 = new TriggerServerCodeResult(true, "[1, \"2\", 3]", System.currentTimeMillis() + 5000, null);
        TriggerServerCodeResult serverCodeResult7 = new TriggerServerCodeResult(false, null, System.currentTimeMillis() + 6000, "ReferenceError");

        this.addMockResponseForListTriggerServerCodeResults(200, new TriggerServerCodeResult[]{serverCodeResult1, serverCodeResult2, serverCodeResult3, serverCodeResult4}, paginationKey);
        this.addMockResponseForListTriggerServerCodeResults(200, new TriggerServerCodeResult[]{serverCodeResult5, serverCodeResult6, serverCodeResult7}, null);

        Pair<List<TriggerServerCodeResult>, String> result1 = api.listTriggerServerCodeResults(triggerID, 4, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<TriggerServerCodeResult> results1 = result1.first;
        Assert.assertEquals(4, results1.size());
        this.assertTriggerServerCodeResult(serverCodeResult1, results1.get(0));
        this.assertTriggerServerCodeResult(serverCodeResult2, results1.get(1));
        this.assertTriggerServerCodeResult(serverCodeResult3, results1.get(2));
        this.assertTriggerServerCodeResult(serverCodeResult4, results1.get(3));

        Pair<List<TriggerServerCodeResult>, String> result2 = api.listTriggerServerCodeResults(triggerID, 4, result1.second);
        Assert.assertNull(result2.second);
        List<TriggerServerCodeResult> results2 = result2.first;
        Assert.assertEquals(3, results2.size());
        this.assertTriggerServerCodeResult(serverCodeResult5, results2.get(0));
        this.assertTriggerServerCodeResult(serverCodeResult6, results2.get(1));
        this.assertTriggerServerCodeResult(serverCodeResult7, results2.get(2));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code?bestEffortLimit=4", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);

        // verify the 2nd request
        RecordedRequest request2 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code?bestEffortLimit=4&paginationKey=" + paginationKey, request2.getPath());
        Assert.assertEquals("GET", request2.getMethod());
        this.assertRequestHeader(expectedRequestHeaders, request2);
    }
    @Test
    public void listTriggerServerCodeResultsWithBestEffortLimitZeroTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        TriggerServerCodeResult serverCodeResult1 = new TriggerServerCodeResult(true, "1234", System.currentTimeMillis(), null);

        this.addMockResponseForListTriggerServerCodeResults(200, new TriggerServerCodeResult[]{serverCodeResult1}, null);

        Pair<List<TriggerServerCodeResult>, String> result1 = api.listTriggerServerCodeResults(triggerID, 0, null);
        Assert.assertNull(result1.second);
        List<TriggerServerCodeResult> results1 = result1.first;
        Assert.assertEquals(1, results1.size());
        this.assertTriggerServerCodeResult(serverCodeResult1, results1.get(0));


        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggerServerCodeResults404ErrorTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);

        this.addEmptyMockResponse(404);
        try {
            api.listTriggerServerCodeResults(triggerID, 0, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<String, String>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void listTriggerServerCodeResultsWithNullTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new Target(thingID, accessToken);

        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.setTarget(target);
        api.listTriggerServerCodeResults(null, 10, null);
    }
    @Test(expected = IllegalStateException.class)
    public void listTriggerServerCodeResultsWithNullTargetTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema(APP_ID, APP_KEY);
        api.listTriggerServerCodeResults(triggerID, 10, null);
    }
}
