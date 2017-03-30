package com.kii.thingif.command;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.actions.SetPresetHumidity;
import com.kii.thingif.actions.TurnPower;
import com.kii.thingif.utils.JsonUtil;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CommandFormBuilderTest extends SmallTestBase {

    private static final String DEMO_TITLE = "DemoTitle";
    private static final String DEMO_DESCRIPTION = "DemoDESCRIPTION";

    @Test
    public void basicTest() throws Exception{

        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        AliasAction aliasAction1 = new AliasAction(
                "AirConditionerAlias",
                actions1);

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        AliasAction aliasAction2 = new AliasAction(
                "HumidityAlais",
                actions2);
        JSONObject metaData = new JSONObject("{f:v}");
        CommandForm form = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(aliasAction1)
                .addAliasAction(aliasAction2)
                .setTitle(DEMO_TITLE)
                .setDescription(DEMO_DESCRIPTION)
                .setMetadata(metaData)
                .build();
        Assert.assertEquals(2, form.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                JsonUtil.aliasActionToJson(form.getAliasActions().get(0)));
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction2),
                JsonUtil.aliasActionToJson(form.getAliasActions().get(1)));

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
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new TurnPower(true));
        AliasAction aliasAction1 = new AliasAction(
                "AirConditionerAlias",
                actions1);

        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetPresetHumidity(45));
        AliasAction aliasAction2 = new AliasAction(
                "HumidityAlais",
                actions2);

        CommandForm form = CommandForm
                .Builder
                .newBuilder()
                .addAliasAction(aliasAction1)
                .addAliasAction(aliasAction2)
                .build();
        Assert.assertEquals(2, form.getAliasActions().size());
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction1),
                JsonUtil.aliasActionToJson(form.getAliasActions().get(0)));
        assertJSONObject(
                JsonUtil.aliasActionToJson(aliasAction2),
                JsonUtil.aliasActionToJson(form.getAliasActions().get(1)));
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
