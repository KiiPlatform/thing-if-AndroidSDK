package com.kii.thingif.command;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;

import junit.framework.Assert;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CommandFormTest extends SmallTestBase {
    protected static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    protected static final int DEMO_SCHEMA_VERSION = 1;
    protected static final String DEMO_TITLE = "DemoTitle";
    protected static final String DEMO_DESCRIPTION = "DemoDESCRIPTION";

    @Test
    public void createTest() throws Exception {
        JSONObject metaData = new JSONObject();
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        CommandForm form = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        form.setTitle(DEMO_TITLE);
        form.setDescription(DEMO_DESCRIPTION);
        form.setMetadata(metaData);

        Assert.assertEquals(DEMO_SCHEMA_NAME, form.getSchemaName());
        Assert.assertEquals(DEMO_SCHEMA_VERSION, form.getSchemaVersion());
        Assert.assertEquals(actions, form.getActions());
        Assert.assertEquals(DEMO_TITLE, form.getTitle());
        Assert.assertEquals(DEMO_DESCRIPTION, form.getDescription());
        Assert.assertEquals(metaData, form.getMetadata());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullSchemaNameTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        new CommandForm(null, DEMO_SCHEMA_VERSION, actions);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithNullActionsTest() throws Exception {
        new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithEmptyActionsTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();

        new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
    }

    @Test
    public void setTilteWithNullTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        CommandForm f = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        f.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerTilteTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        CommandForm f = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        f.setTitle(RandomStringUtils.randomAlphabetic(51));
    }

    @Test
    public void setDescriptionWithNullTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        CommandForm f = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        f.setDescription(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void tooLongerDescriptionTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));

        CommandForm f = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        f.setDescription(RandomStringUtils.randomAlphabetic(201));
    }

    @Test
    public void percelableTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));
        actions.add(new SetColorTemperature(25));
        JSONObject metadata = new JSONObject("{ field : value }");

        CommandForm src = new CommandForm(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions);
        src.setTitle(DEMO_TITLE);
        src.setDescription(DEMO_DESCRIPTION);
        src.setMetadata(metadata);

        Parcel parcel1 = Parcel.obtain();
        src.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        CommandForm dest1 = CommandForm.CREATOR.createFromParcel(parcel1);

        Assert.assertEquals(src.getSchemaName(), dest1.getSchemaName());
        Assert.assertEquals(src.getSchemaVersion(), dest1.getSchemaVersion());
        Assert.assertEquals(src.getActions().size(), dest1.getActions().size());
        Assert.assertEquals(src.getActions().get(0).getActionName(),
                dest1.getActions().get(0).getActionName());
        Assert.assertEquals(src.getActions().get(1).getActionName(),
                dest1.getActions().get(1).getActionName());
        Assert.assertEquals(src.getTitle(), dest1.getTitle());
        Assert.assertEquals(src.getDescription(), dest1.getDescription());
        Assert.assertEquals(src.getMetadata().toString(), dest1.getMetadata().toString());

        src.setTitle(null);
        src.setDescription(null);
        src.setMetadata(null);

        Parcel parcel2 = Parcel.obtain();
        src.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        CommandForm dest2 = CommandForm.CREATOR.createFromParcel(parcel2);

        Assert.assertEquals(src.getSchemaName(), dest2.getSchemaName());
        Assert.assertEquals(src.getSchemaVersion(), dest2.getSchemaVersion());
        Assert.assertEquals(src.getActions().size(), dest2.getActions().size());
        Assert.assertEquals(src.getActions().get(0).getActionName(),
                dest2.getActions().get(0).getActionName());
        Assert.assertEquals(src.getActions().get(1).getActionName(),
                dest2.getActions().get(1).getActionName());
        Assert.assertNull(dest2.getTitle());
        Assert.assertNull(dest2.getDescription());
        Assert.assertNull(dest2.getMetadata());
    }
}
