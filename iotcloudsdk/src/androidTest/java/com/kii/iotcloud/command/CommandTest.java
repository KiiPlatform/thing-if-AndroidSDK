package com.kii.iotcloud.command;

import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.SmallTestBase;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.TurnPower;
import com.kii.iotcloud.testschemas.TurnPowerResult;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CommandTest extends SmallTestBase {
    @Test
    public void basicTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 2, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);

        Assert.assertEquals("DemoSchema", command.getSchemaName());
        Assert.assertEquals(2, command.getSchemaVersion());
        Assert.assertEquals(new TypedID(TypedID.Types.THING, "thing1234"), command.getTargetID());
        Assert.assertEquals(new TypedID(TypedID.Types.USER, "user1234"), command.getIssuerID());
        Assert.assertEquals(1, command.getActions().size());
        Assert.assertEquals(TurnPower.class, command.getActions().get(0).getClass());

        command.addActionResult(new TurnPowerResult(true));
        TurnPowerResult actionResult = (TurnPowerResult)command.getActionResult(new TurnPower());
        Assert.assertTrue(actionResult.succeeded);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullSchemaNameTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command(null, 1, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithEmptySchemaNameTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("", 1, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullTargetTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 1, null, new TypedID(TypedID.Types.USER, "user1234"), actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullIssuerTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 1, new TypedID(TypedID.Types.THING, "thing1234"), null, actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithNullActionTest() throws Exception {
        Command command = new Command("DemoSchema", 1, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void constructorWithEmptyActionTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        Command command = new Command("DemoSchema", 1, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionResultWithNullActionResultTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 2, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
        command.addActionResult(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionResultWithUnrelatedActionResultTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 2, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
        command.addActionResult(new SetColorResult(true));
    }
    @Test(expected = IllegalArgumentException.class)
    public void getActionResultWithNullActionTest() throws Exception {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new TurnPower());
        Command command = new Command("DemoSchema", 2, new TypedID(TypedID.Types.THING, "thing1234"), new TypedID(TypedID.Types.USER, "user1234"), actions);
        command.addActionResult(new TurnPowerResult());
        command.getActionResult(null);
    }
}