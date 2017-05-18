package com.kii.thing_if.largetests;

import com.kii.thing_if.OnboardWithVendorThingIDOptions;
import com.kii.thing_if.Target;
import com.kii.thing_if.TargetState;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.states.AirConditionerState;
import com.kii.thing_if.states.HumidityState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

public class GetTargetStateTest extends LargeTestCaseBase {

    private ThingIFAPI onboardedApi;

    @Before
    public void before() throws Exception{
        super.before();
        this.onboardedApi = this.createDefaultThingIFAPI();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";
        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = this.onboardedApi.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());
    }

    @Test
    public void successEmptyResultsTest() throws Exception {
        Map results = onboardedApi.getTargetState();

        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());

        try {
            onboardedApi.getTargetState(ALIAS1, AirConditionerState.class);
            Assert.fail("should fail");
        } catch (NotFoundException e) {
            Assert.assertEquals("STATE_NOT_FOUND", e.getErrorCode());
            Assert.assertNotNull(e.getMessage());
            Assert.assertEquals(404, e.getStatusCode());
        }
    }

    @Test
    public void successTest() throws Exception {
        AirConditionerState airState = new AirConditionerState(true, 23);
        HumidityState humState = new HumidityState(50);

        updateTargetState(
                this.onboardedApi.getTarget(),
                new TargetState[]{airState, humState});
        Thread.sleep(1000);

        Map results = onboardedApi.getTargetState();

        Assert.assertNotNull(results);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(airState, (AirConditionerState) results.get(ALIAS1));
        Assert.assertEquals(humState, (HumidityState) results.get(ALIAS2));

        AirConditionerState gotAirSteta = onboardedApi.getTargetState(ALIAS1,
                AirConditionerState.class);
        Assert.assertEquals(airState, gotAirSteta);

        HumidityState gotHumSteta = onboardedApi.getTargetState(ALIAS2,
                HumidityState.class);
        Assert.assertEquals(humState, gotHumSteta);
    }
}
