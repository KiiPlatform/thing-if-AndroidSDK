package com.kii.thingif.thingifapi;

import android.content.Context;
import android.util.Pair;

import com.kii.thingif.ServerError;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPITestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.exception.NotFoundException;
import com.kii.thingif.thingifapi.utils.ThingIFAPIUtils;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.trigger.TriggeredServerCodeResultFactory;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class ListTriggeredServerCodeResultTest extends ThingIFAPITestBase{

    private Context context;

    @Before
    public void before() throws Exception {
        context = RuntimeEnvironment.application.getApplicationContext();
        this.server = new MockWebServer();
        this.server.start();
    }

    @After
    public void after() throws Exception {
        this.server.shutdown();
    }

    @Test
    public void listTriggeredServerCodeResultsTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);
        String paginationKey = "pagination-12345-key";

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        TriggeredServerCodeResult serverCodeResult1 =
                TriggeredServerCodeResultFactory.create(true, "1234", System.currentTimeMillis(), "func1", null);
        TriggeredServerCodeResult serverCodeResult2 =
                TriggeredServerCodeResultFactory.create(true, "12.34", System.currentTimeMillis() + 1000, "func2", null);
        TriggeredServerCodeResult serverCodeResult3 =
                TriggeredServerCodeResultFactory.create(true, "true", System.currentTimeMillis() + 2000, "func3", null);
        TriggeredServerCodeResult serverCodeResult4 =
                TriggeredServerCodeResultFactory.create(true, "\"abcd\"", System.currentTimeMillis() + 3000, "func4", null);
        TriggeredServerCodeResult serverCodeResult5 =
                TriggeredServerCodeResultFactory.create(true, "{\"field\":\"abcd\"}", System.currentTimeMillis() + 4000, "func5", null);
        TriggeredServerCodeResult serverCodeResult6 =
                TriggeredServerCodeResultFactory.create(true, "[1, \"2\", 3]", System.currentTimeMillis() + 5000, "func6", null);
        TriggeredServerCodeResult serverCodeResult7 =
                TriggeredServerCodeResultFactory.create(false, null, System.currentTimeMillis() + 6000, "func7", new ServerError("Error found", "RUNTIME_ERROR", "faital error"));

        this.addMockResponseForListTriggeredServerCodeResults(200, new TriggeredServerCodeResult[]{serverCodeResult1, serverCodeResult2, serverCodeResult3, serverCodeResult4}, paginationKey);
        this.addMockResponseForListTriggeredServerCodeResults(200, new TriggeredServerCodeResult[]{serverCodeResult5, serverCodeResult6, serverCodeResult7}, null);

        Pair<List<TriggeredServerCodeResult>, String> result1 = api.listTriggeredServerCodeResults(triggerID, 4, null);
        Assert.assertEquals(paginationKey, result1.second);
        List<TriggeredServerCodeResult> results1 = result1.first;
        Assert.assertEquals(4, results1.size());
        assertSameTriggeredServerCodeResults(serverCodeResult1, results1.get(0));
        assertSameTriggeredServerCodeResults(serverCodeResult2, results1.get(1));
        assertSameTriggeredServerCodeResults(serverCodeResult3, results1.get(2));
        assertSameTriggeredServerCodeResults(serverCodeResult4, results1.get(3));

        Pair<List<TriggeredServerCodeResult>, String> result2 = api.listTriggeredServerCodeResults(triggerID, 4, result1.second);
        Assert.assertNull(result2.second);
        List<TriggeredServerCodeResult> results2 = result2.first;
        Assert.assertEquals(3, results2.size());
        assertSameTriggeredServerCodeResults(serverCodeResult5, results2.get(0));
        assertSameTriggeredServerCodeResults(serverCodeResult6, results2.get(1));
        assertSameTriggeredServerCodeResults(serverCodeResult7, results2.get(2));

        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code?bestEffortLimit=4", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
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
    public void listTriggeredServerCodeResultsWithBestEffortLimitZeroTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        TriggeredServerCodeResult serverCodeResult1 =
                TriggeredServerCodeResultFactory.create(true, "1234", System.currentTimeMillis(), "func1", null);

        this.addMockResponseForListTriggeredServerCodeResults(200, new TriggeredServerCodeResult[]{serverCodeResult1}, null);

        Pair<List<TriggeredServerCodeResult>, String> result1 = api.listTriggeredServerCodeResults(triggerID, 0, null);
        Assert.assertNull(result1.second);
        List<TriggeredServerCodeResult> results1 = result1.first;
        Assert.assertEquals(1, results1.size());
        assertSameTriggeredServerCodeResults(serverCodeResult1, results1.get(0));


        // verify the 1st request
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test
    public void listTriggeredServerCodeResults404ErrorTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);

        this.addEmptyMockResponse(404);
        try {
            api.listTriggeredServerCodeResults(triggerID, 0, null);
            Assert.fail("ThingIFRestException should be thrown");
        } catch (NotFoundException e) {
        }
        RecordedRequest request1 = this.server.takeRequest(1, TimeUnit.SECONDS);
        Assert.assertEquals(BASE_PATH + "/targets/" + thingID.toString() + "/triggers/" + triggerID + "/results/server-code", request1.getPath());
        Assert.assertEquals("GET", request1.getMethod());

        Map<String, String> expectedRequestHeaders = new HashMap<>();
        expectedRequestHeaders.put("X-Kii-AppID", APP_ID);
        expectedRequestHeaders.put("X-Kii-AppKey", APP_KEY);
        expectedRequestHeaders.put("Authorization", "Bearer " + api.getOwner().getAccessToken());
        this.assertRequestHeader(expectedRequestHeaders, request1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void listTriggeredServerCodeResultsWithNullTriggerIDTest() throws Exception {
        TypedID thingID = new TypedID(TypedID.Types.THING, "th.1234567890");
        String accessToken = "thing-access-token-1234";
        Target target = new StandaloneThing(thingID.getID(), "vendor-thing-id", accessToken);

        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        ThingIFAPIUtils.setTarget(api, target);
        api.listTriggeredServerCodeResults(null, 10, null);
    }
    @Test(expected = IllegalStateException.class)
    public void listTriggeredServerCodeResultsWithNullTargetTest() throws Exception {
        String triggerID = UUID.randomUUID().toString();
        ThingIFAPI api = this.createDefaultThingIFAPI(this.context, APP_ID, APP_KEY);
        api.listTriggeredServerCodeResults(triggerID, 10, null);
    }
}
