package com.kii.thingif.largetests;

import android.support.test.runner.AndroidJUnit4;
import android.util.Pair;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.clause.query.AllClause;
import com.kii.thingif.query.HistoryState;
import com.kii.thingif.query.HistoryStatesQuery;
import com.kii.thingif.states.AirConditionerState;
import com.kii.thingif.states.HumidityState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class QueryHistoryStatesTest extends LargeTestCaseBase {

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
    public void successQueryEmptyResults_thingNeverUpdate_Test() throws Exception {
        HistoryStatesQuery query =
                HistoryStatesQuery.Builder.newBuilder(ALIAS1, new AllClause()).build();
        Pair<List<HistoryState<AirConditionerState>>, String > result =
                this.onboardedApi.query(query);

        Assert.assertNull(result.second);
        Assert.assertEquals(0, result.first.size());
    }

    @Test
    public void successQueryEmptyResult_thingUpdatedStates_Test() throws Exception{
        AirConditionerState[] airStates = {
                new AirConditionerState(true, 23),
                new AirConditionerState(true, 24),
                new AirConditionerState(true, 25),
                new AirConditionerState(true, 23)
        };

        for (AirConditionerState state: airStates) {
            updateTargetState(this.onboardedApi.getTarget(), new AirConditionerState[]{state});
        }

        HistoryStatesQuery query =
                HistoryStatesQuery.Builder.newBuilder(ALIAS1, new AllClause()).build();
        Pair<List<HistoryState<AirConditionerState>>, String> result =
                this.onboardedApi.query(query);

        Assert.assertEquals(4, result.first.size());
        Assert.assertNull(result.second);
    }
}
