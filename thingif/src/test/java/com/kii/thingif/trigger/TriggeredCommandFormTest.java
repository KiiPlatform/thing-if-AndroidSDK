package com.kii.thingif.trigger;

import com.kii.thingif.TypedID;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class TriggeredCommandFormTest {

    @Test
    public void equals_hashCode_AliasActionsTest() {
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        AliasAction<HumidityActions> humAlias =
                new AliasAction<>("alias", new HumidityActions(10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder(aliasActions).build();
        TriggeredCommandForm sameOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .build();
        TriggeredCommandForm differentOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(humAlias)
                .build();
        try {
            TriggeredCommandForm.Builder.newBuilder().build();
            Assert.fail("IllegalArgumentException must be thrown.");
        } catch (IllegalArgumentException e) {
            // expected.
        } catch (Exception e) {
            Assert.fail("Unknown exception must not be thrown.");
        }

        Assert.assertTrue(target.equals(target));
        Assert.assertEquals(target.hashCode(), target.hashCode());
        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)airAlias));
    }

    @Test
    public void equals_hashCode_TargetIDTest() {
        TypedID typedID1 = new TypedID(TypedID.Types.THING, "dummy");
        TypedID typedID2 = new TypedID(TypedID.Types.THING, "differ");
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID1)
                .build();
        TriggeredCommandForm sameOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID1)
                .build();
        TriggeredCommandForm differentOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID2)
                .build();
        TriggeredCommandForm differentNull = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .build();

        Assert.assertTrue(target.equals(target));
        Assert.assertEquals(target.hashCode(), target.hashCode());
        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)typedID1));
    }

    @Test
    public void equals_hashCode_TitleTest() {
        String title = "dummyTitle";
        TypedID typedID = new TypedID(TypedID.Types.THING, "dummy");
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .build();
        TriggeredCommandForm sameOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .build();
        TriggeredCommandForm differentOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle("differentTitle")
                .build();
        TriggeredCommandForm differentNull = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .build();

        Assert.assertTrue(target.equals(target));
        Assert.assertEquals(target.hashCode(), target.hashCode());
        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)title));
    }

    @Test
    public void equals_hashCode_DescriptionTest() {
        String description = "dummyDescription";
        String title = "dummyTitle";
        TypedID typedID = new TypedID(TypedID.Types.THING, "dummy");
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .build();
        TriggeredCommandForm sameOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .build();
        TriggeredCommandForm differentOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription("differentDescrition")
                .build();
        TriggeredCommandForm differentNull = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .build();

        Assert.assertTrue(target.equals(target));
        Assert.assertEquals(target.hashCode(), target.hashCode());
        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)description));
    }

    @Test
    public void equals_hashCode_MetadataTest() {
        JSONObject metadata = null;
        try {
            metadata = new JSONObject("{ \"key\" : \"value\" }");
        } catch (JSONException e) {
            Assert.fail("JSONException must not be thrown.");
        }
        String description = "dummyDescription";
        String title = "dummyTitle";
        TypedID typedID = new TypedID(TypedID.Types.THING, "dummy");
        AliasAction<AirConditionerActions> airAlias =
                new AliasAction<>("alias", new AirConditionerActions(false, 10));
        List<AliasAction<? extends Action>> aliasActions = new ArrayList<>();
        aliasActions.add(airAlias);

        TriggeredCommandForm target = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .setMetadata(metadata)
                .build();
        TriggeredCommandForm sameOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .setMetadata(metadata)
                .build();
        TriggeredCommandForm differentOne = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .setMetadata(new JSONObject())
                .build();
        TriggeredCommandForm differentNull = TriggeredCommandForm.Builder.newBuilder()
                .addAliasAction(airAlias)
                .setTargetID(typedID)
                .setTitle(title)
                .setDescription(description)
                .build();

        Assert.assertTrue(target.equals(target));
        Assert.assertEquals(target.hashCode(), target.hashCode());
        Assert.assertTrue(target.equals(sameOne));
        Assert.assertEquals(target.hashCode(), sameOne.hashCode());

        Assert.assertFalse(target.equals(differentOne));
        Assert.assertNotSame(target.hashCode(), differentOne.hashCode());
        Assert.assertFalse(target.equals(differentNull));
        Assert.assertNotSame(target.hashCode(), differentNull.hashCode());

        Assert.assertFalse(target.equals(null));
        Assert.assertFalse(target.equals((Object)metadata));
    }
}
