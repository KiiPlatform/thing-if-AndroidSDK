package com.kii.thingif.command;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorTemperature;

import junit.framework.Assert;

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
}
