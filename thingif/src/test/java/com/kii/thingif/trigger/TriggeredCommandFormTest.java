package com.kii.thingif.trigger;

import com.kii.thingif.TypedID;
import com.kii.thingif.actions.SetPresetTemperature;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class TriggeredCommandFormTest {

    @Test
    public void minValueTest() {
        
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(false));
        actions.add(new SetPresetTemperature(10));
        AliasAction airAlias =
                new AliasAction("alias", actions);
        List<AliasAction> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder
                .newBuilder(aliasActions)
                .build();

        Assert.assertNotNull(target);
        Assert.assertEquals(aliasActions, target.getAliasActions());
        Assert.assertNull(target.getTargetID());
        Assert.assertNull(target.getTitle());
        Assert.assertNull(target.getDescription());
        Assert.assertNull(target.getMetadata());
    }

    @Test
    public void maxValueTest() {
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(false));
        actions.add(new SetPresetTemperature(10));
        AliasAction airAlias =
                new AliasAction("alias", actions);
        List<AliasAction> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);
        TypedID targetID = new TypedID(TypedID.Types.THING, "id");
        JSONObject metadata = new JSONObject();

        TriggeredCommandForm target = TriggeredCommandForm.Builder
                .newBuilder(aliasActions)
                .setTargetID(targetID)
                .setTitle("title")
                .setDescription("description")
                .setMetadata(metadata)
                .build();

        Assert.assertNotNull(target);
        Assert.assertEquals(aliasActions, target.getAliasActions());
        Assert.assertEquals(targetID, target.getTargetID());
        Assert.assertEquals("title", target.getTitle());
        Assert.assertEquals("description", target.getDescription());
        Assert.assertNotNull(target.getMetadata());
        Assert.assertEquals(metadata.toString(), target.getMetadata().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyAliasActionsTest() {
        TriggeredCommandForm.Builder.newBuilder().build();
    }

    @Test
    public void addAliasActionTest() {
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(false));
        actions1.add(new SetPresetTemperature(10));
        AliasAction airAlias1 =
                new AliasAction("alias", actions1);

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new TurnPower(true));
        actions2.add(new SetPresetTemperature(25));
        AliasAction airAlias2 =
                new AliasAction("alias", actions2);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias1)
                .addAliasAction(airAlias2)
                .build();

        Assert.assertNotNull(target);
        List<AliasAction> aliasActions = target.getAliasActions();
        Assert.assertNotNull(aliasActions);
        Assert.assertEquals(2, aliasActions.size());
        Assert.assertEquals(airAlias1, aliasActions.get(0));
        Assert.assertEquals(airAlias2, aliasActions.get(1));
        Assert.assertNull(target.getTargetID());
        Assert.assertNull(target.getTitle());
        Assert.assertNull(target.getDescription());
        Assert.assertNull(target.getMetadata());
    }

    @Test
    public void nullableValueTest() {
        List<Action> actions = new ArrayList<>();
        actions.add(new TurnPower(false));
        actions.add(new SetPresetTemperature(10));
        AliasAction airAlias =
                new AliasAction("alias", actions);
        List<AliasAction> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder
                .newBuilder(aliasActions)
                .setTargetID(null)
                .setTitle(null)
                .setDescription(null)
                .setMetadata(null)
                .build();

        Assert.assertNotNull(target);
        Assert.assertEquals(aliasActions, target.getAliasActions());
        Assert.assertNull(target.getTargetID());
        Assert.assertNull(target.getTitle());
        Assert.assertNull(target.getDescription());
        Assert.assertNull(target.getMetadata());
    }

    @Test(expected = IllegalArgumentException.class)
    public void targetIDTypeIsUserTest() {
        TypedID targetID = new TypedID(TypedID.Types.USER, "id");
        TriggeredCommandForm.Builder.newBuilder().setTargetID(targetID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void targetIDTypeIsGroupTest() {
        TypedID targetID = new TypedID(TypedID.Types.GROUP, "id");
        TriggeredCommandForm.Builder.newBuilder().setTargetID(targetID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerTitleTest() throws Exception {
        TriggeredCommandForm.Builder.newBuilder().setTitle(RandomStringUtils.randomAlphabetic(51));
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerDescriptionTest() throws Exception {
        TriggeredCommandForm.Builder.newBuilder().setDescription(RandomStringUtils.randomAlphabetic(201));
    }
}
