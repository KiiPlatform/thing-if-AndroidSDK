package com.kii.thingif.command;

import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.kii.thingif.actions.AirConditionerActions;
import com.kii.thingif.actions.HumidityActions;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CommandFormBuilderTest {

    private static final String DEMO_TITLE = "DemoTitle";
    private static final String DEMO_DESCRIPTION = "DemoDESCRIPTION";

    @Test
    public void basicTest() throws Exception{
        AliasAction<AirConditionerActions> action1 = new AliasAction<>(
                "AirConditionerAlias",
                new AirConditionerActions(true, null));
        AliasAction<HumidityActions> action2 = new AliasAction<>(
                "HumidityAlais",
                new HumidityActions(45));
        JSONObject metaData = new JSONObject("{f:v}");
        CommandForm form = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(action1)
                .addAliasAction(action2)
                .setTitle(DEMO_TITLE)
                .setDescription(DEMO_DESCRIPTION)
                .setMetadata(metaData)
                .build();
        Assert.assertEquals(2, form.getAliasActions().size());
        Assert.assertEquals(action1, form.getAliasActions().get(0));
        Assert.assertEquals(action2, form.getAliasActions().get(1));
        Assert.assertEquals(DEMO_TITLE, form.getTitle());
        Assert.assertEquals(DEMO_DESCRIPTION, form.getDescription());
        Assert.assertNotNull(form.getMetadata());
        Assert.assertEquals(metaData.toString(), form.getMetadata().toString());
    }

    @Test(expected = IllegalStateException.class)
    public void createWithNullActionsTest() throws Exception {
        CommandForm.Builder.newBuilder().build();
    }

    @Test
    public void setTitleWithNullTest() throws Exception {
        CommandForm.Builder.newBuilder().setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerTitleTest() throws Exception {
        CommandForm.Builder.newBuilder().setTitle(RandomStringUtils.randomAlphabetic(51));
    }

    @Test
    public void setDescriptionWithNullTest() throws Exception {
        CommandForm.Builder.newBuilder().setDescription(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerDescriptionTest() throws Exception {
        CommandForm.Builder.newBuilder().setDescription(RandomStringUtils.randomAlphabetic(201));
    }

    @Test
    public void addAliasActionTest() {
        AliasAction<AirConditionerActions> action1 = new AliasAction<>(
                "AirConditionerAlias",
                new AirConditionerActions(true, null));
        AliasAction<HumidityActions> action2 = new AliasAction<>(
                "HumidityAlais",
                new HumidityActions(45));
        CommandForm form = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(action1)
                .addAliasAction(action2)
                .build();
        Assert.assertEquals(2, form.getAliasActions().size());
        Assert.assertEquals(action1, form.getAliasActions().get(0));
        Assert.assertEquals(action2, form.getAliasActions().get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addNullAliasActionTest() {
        CommandForm.Builder.newBuilder().addAliasAction(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newBuilderWithNullAliasActionTest() {
        CommandForm.Builder.newBuilder(null);
    }
}
