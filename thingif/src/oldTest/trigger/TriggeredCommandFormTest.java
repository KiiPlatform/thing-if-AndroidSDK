package com.kii.thingif.trigger;

import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
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
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
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
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
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
        AliasAction<AirConditionerActions> airAlias1 =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        AliasAction<AirConditionerActions> airAlias2 =
                new AliasAction<>("alias", new AirConditionerActions(true, 25));

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias1)
                .addAliasAction(airAlias2)
                .build();

        Assert.assertNotNull(target);
        List<AliasAction<? extends Action>> aliasActions = target.getAliasActions();
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
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
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
