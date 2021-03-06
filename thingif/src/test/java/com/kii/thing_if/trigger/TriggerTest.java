package com.kii.thing_if.trigger;

import android.os.Parcel;

import com.kii.thing_if.SmallTestBase;
import com.kii.thing_if.TypedID;
import com.kii.thing_if.actions.SetPresetHumidity;
import com.kii.thing_if.actions.TurnPower;
import com.kii.thing_if.clause.trigger.EqualsClauseInTrigger;
import com.kii.thing_if.command.Action;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.command.CommandFactory;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
public class TriggerTest extends SmallTestBase{
    private static final String alias1 = "AirConditionerAlias";
    private static final String alias2 = "HumidityAlias";
    @Test
    public void commandTriggerTest() throws Exception {
        TypedID target = new TypedID(TypedID.Types.THING, "thing1234");
        TypedID issuer = new TypedID(TypedID.Types.USER, "user1234");

        List<AliasAction> aliasActions = new ArrayList<>();
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        aliasActions.add(new AliasAction(alias1, actions1));

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));

        aliasActions.add(new AliasAction(alias2, actions2));


        String commandTitle = "command title";
        String commandDescription = "command description";
        JSONObject commandMetaData = new JSONObject().put("k", "v");
        Command command = CommandFactory.newCommand(
                issuer,
                aliasActions,
                null,
                target,
                null,
                null,
                null,
                null,
                null,
                commandTitle,
                commandDescription,
                commandMetaData);

        EqualsClauseInTrigger equals = new EqualsClauseInTrigger(alias1, "power", true);
        Condition condition = new Condition(equals);
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        String triggerID = "trigger1234";
        boolean disabled = true;
        String disabledReason = "reasonXXXX";
        String title = "Title of Trigger";
        String description = "Description of Trigger";
        JSONObject metadata = new JSONObject();
        metadata.put("sound", "phone.mp3");

        Trigger trigger = new Trigger(
                triggerID,
                target,
                predicate,
                command,
                null,
                disabled,
                disabledReason,
                title,
                description,
                metadata);

        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(target, trigger.getTargetID());
        Assert.assertTrue(trigger.getPredicate() instanceof StatePredicate);
        Assert.assertEquals(equals, ((StatePredicate)trigger.getPredicate()).getCondition().getClause());
        assertSameCommands(command, trigger.getCommand());
        Assert.assertNull(trigger.getServerCode());
        Assert.assertEquals(disabled, trigger.disabled());
        Assert.assertEquals(disabledReason, trigger.getDisabledReason());
        Assert.assertEquals(title, trigger.getTitle());
        Assert.assertEquals(description, trigger.getDescription());
        assertJSONObject(metadata, trigger.getMetadata());

        Parcel parcel = Parcel.obtain();
        trigger.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Trigger deserializedTrigger = Trigger.CREATOR.createFromParcel(parcel);

        Assert.assertNull(deserializedTrigger.getServerCode());
        assertSameCommands(trigger.getCommand(), deserializedTrigger.getCommand());

        Assert.assertEquals(equals, ((StatePredicate)deserializedTrigger.getPredicate()).getCondition().getClause());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, ((StatePredicate) deserializedTrigger.getPredicate()).getTriggersWhen());

        Assert.assertEquals(triggerID, deserializedTrigger.getTriggerID());
        Assert.assertEquals(disabled, deserializedTrigger.disabled());
        Assert.assertEquals(disabledReason, deserializedTrigger.getDisabledReason());
        Assert.assertEquals(title, deserializedTrigger.getTitle());
        Assert.assertEquals(description, deserializedTrigger.getDescription());
        assertJSONObject(metadata, deserializedTrigger.getMetadata());
        Assert.assertEquals(TriggersWhat.COMMAND, deserializedTrigger.getTriggersWhat());
    }
    @Test
    public void serverCodeTriggerTest() throws Exception {
        TypedID target = new TypedID(TypedID.Types.THING, "thing1234");

        String endpoint = "function_name";
        String executorAccessToken = UUID.randomUUID().toString();
        String targetAppID = UUID.randomUUID().toString().substring(0, 8);
        JSONObject parameters = new JSONObject("{\"name\":\"kii\", \"age\":30, \"enabled\":true}");
        ServerCode serverCode = new ServerCode(endpoint, executorAccessToken, targetAppID, parameters);

        EqualsClauseInTrigger equals = new EqualsClauseInTrigger(alias1, "power", true);
        Condition condition = new Condition(equals);
        StatePredicate predicate = new StatePredicate(condition, TriggersWhen.CONDITION_TRUE);

        String triggerID = "trigger1234";
        boolean disabled = true;
        String disabledReason = "reasonXXXX";
        String title = "Title of Trigger";
        String description = "Description of Trigger";
        JSONObject metadata = new JSONObject();
        metadata.put("sound", "phone.mp3");

        Trigger trigger = new Trigger(
                triggerID,
                target,
                predicate,
                null,
                serverCode,
                disabled,
                disabledReason,
                title,
                description,
                metadata);

        Assert.assertEquals(triggerID, trigger.getTriggerID());
        Assert.assertEquals(target, trigger.getTargetID());
        Assert.assertTrue(trigger.getPredicate() instanceof StatePredicate);
        Assert.assertEquals(equals, ((StatePredicate)trigger.getPredicate()).getCondition().getClause());
        Assert.assertNull(trigger.getCommand());
        Assert.assertNotNull(trigger.getServerCode());
        Assert.assertEquals(endpoint, trigger.getServerCode().getEndpoint());
        Assert.assertEquals(executorAccessToken, trigger.getServerCode().getExecutorAccessToken());
        Assert.assertEquals(targetAppID, trigger.getServerCode().getTargetAppID());
        assertJSONObject(parameters, trigger.getServerCode().getParameters());
        Assert.assertEquals(disabled, trigger.disabled());
        Assert.assertEquals(disabledReason, trigger.getDisabledReason());
        Assert.assertEquals(title, trigger.getTitle());
        Assert.assertEquals(description, trigger.getDescription());
        assertJSONObject(metadata, trigger.getMetadata());

        Parcel parcel = Parcel.obtain();
        trigger.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Trigger deserializedTrigger = Trigger.CREATOR.createFromParcel(parcel);

        Assert.assertNull(deserializedTrigger.getCommand());
        Assert.assertEquals(endpoint, deserializedTrigger.getServerCode().getEndpoint());
        Assert.assertEquals(executorAccessToken, deserializedTrigger.getServerCode().getExecutorAccessToken());
        Assert.assertEquals(targetAppID, deserializedTrigger.getServerCode().getTargetAppID());
        assertJSONObject(parameters, deserializedTrigger.getServerCode().getParameters());

        Assert.assertEquals(equals, ((StatePredicate)deserializedTrigger.getPredicate()).getCondition().getClause());
        Assert.assertEquals(TriggersWhen.CONDITION_TRUE, ((StatePredicate) deserializedTrigger.getPredicate()).getTriggersWhen());

        Assert.assertEquals(triggerID, deserializedTrigger.getTriggerID());
        Assert.assertEquals(disabled, deserializedTrigger.disabled());
        Assert.assertEquals(disabledReason, deserializedTrigger.getDisabledReason());
        Assert.assertEquals(title, deserializedTrigger.getTitle());
        Assert.assertEquals(description, deserializedTrigger.getDescription());
        assertJSONObject(metadata, deserializedTrigger.getMetadata());
        Assert.assertTrue(deserializedTrigger.getTriggersWhat().equals(TriggersWhat.SERVER_CODE));
    }
}
