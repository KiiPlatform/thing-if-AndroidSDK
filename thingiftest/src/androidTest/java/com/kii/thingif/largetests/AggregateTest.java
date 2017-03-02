package com.kii.thingif.largetests;

import com.kii.thingif.OnboardWithVendorThingIDOptions;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.clause.query.EqualsClauseInQuery;
import com.kii.thingif.clause.query.NotEqualsClauseInQuery;
import com.kii.thingif.clause.query.QueryClause;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.Command;
import com.kii.thingif.command.CommandForm;
import com.kii.thingif.command.CommandState;
import com.kii.thingif.query.AggregatedResult;
import com.kii.thingif.query.Aggregation;
import com.kii.thingif.query.GroupedHistoryStatesQuery;
import com.kii.thingif.query.TimeRange;
import com.kii.thingif.states.AirConditionerState;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AggregateTest  extends LargeTestCaseBase {

    @Test
    public void baseTest() throws Exception {
        ThingIFAPI api = createDefaultThingIFAPI();
        String vendorThingID = "4649";//UUID.randomUUID().toString();
        String thingPassword = "1234";

        // on-boarding thing
        OnboardWithVendorThingIDOptions options =
                new OnboardWithVendorThingIDOptions.Builder()
                        .setThingType(DEFAULT_THING_TYPE)
                        .setFirmwareVersion(DEFAULT_FIRMWARE_VERSION).build();
        Target target = api.onboardWithVendorThingID(vendorThingID, thingPassword, options);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new command
        String commandTitle = "title";
        String commandDescription = "description";
        JSONObject metaData = new JSONObject().put("k", "v");
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(
                new AliasAction<>(
                        ALIAS1,
                        new AirConditionerActions(true, 25)));
        aliasActions.add(
                new AliasAction<Action>(
                        ALIAS2,
                        new HumidityActions(50)));

        CommandForm form = CommandForm
                .Builder
                .newBuilder(aliasActions)
                .setTitle(commandTitle)
                .setDescription(commandDescription)
                .setMetadata(metaData)
                .build();
        Command command1 = api.postNewCommand(form);
        Assert.assertNotNull(command1.getCommandID());
        Assert.assertEquals(commandTitle, command1.getTitle());
        Assert.assertEquals(commandDescription, command1.getDescription());
        Assert.assertNotNull(command1.getMetadata());
        Assert.assertEquals(metaData.toString(), command1.getMetadata().toString());
        Assert.assertEquals(CommandState.SENDING, command1.getCommandState());
        Assert.assertNotNull(command1.getCreated());
        Assert.assertNotNull(command1.getModified());
        Assert.assertNull(command1.getFiredByTriggerID());
        Assert.assertNull(command1.getAliasActionResults());

        // aggregate history state.
        TimeRange range = new TimeRange(new Date(1), new Date());
        GroupedHistoryStatesQuery query = GroupedHistoryStatesQuery.Builder
                .newBuilder(ALIAS1, range)
                .build();
        Aggregation aggregation = Aggregation.newMeanAggregation("currentTemperature",
                Aggregation.FieldType.INTEGER);

        List<AggregatedResult<Integer, AirConditionerState>> results = api.aggregate(query, aggregation);

        Assert.assertNotNull(results);
    }
}
