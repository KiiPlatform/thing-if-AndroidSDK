package com.kii.iotcloud.schema;

import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.SmallTestBase;
import com.kii.iotcloud.testschemas.LightState;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.TurnPower;
import com.kii.iotcloud.testschemas.TurnPowerResult;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SchemaBuilderTest extends SmallTestBase {
    @Test
    public void basicTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        Schema schema = sb.build();
        Assert.assertEquals("SmartLight", schema.getThingType());
        Assert.assertEquals("LightDemoSchema", schema.getSchemaName());
        Assert.assertEquals(1, schema.getSchemaVersion());
        Assert.assertEquals(2, schema.getActionClasses().size());
        Assert.assertEquals(TurnPower.class, schema.getActionClasses().get(0));
        Assert.assertEquals(SetColor.class, schema.getActionClasses().get(1));
        Assert.assertEquals(2, schema.getActionResultClasses().size());
        Assert.assertEquals(TurnPowerResult.class, schema.getActionResultClasses().get(0));
        Assert.assertEquals(SetColorResult.class, schema.getActionResultClasses().get(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithNullThingTypeTest() throws Exception {
        SchemaBuilder.newSchemaBuilder(null, "LightDemoSchema", 1, LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithEmptyThingTypeTest() throws Exception {
        SchemaBuilder.newSchemaBuilder("", "LightDemoSchema", 1, LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithNullSchemaNameTest() throws Exception {
        SchemaBuilder.newSchemaBuilder("SmartLight", null, 1, LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithEmptySchemaNameTest() throws Exception {
        SchemaBuilder.newSchemaBuilder("SmartLight", "", 1, LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithNegativeSchemaVersionTest() throws Exception {
        SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", -1, LightState.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void newSchemaBuilderWithNullStateClassTest() throws Exception {
        SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionClassWithNullActionClassTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(null, TurnPowerResult.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionClassWithExistedActionClassTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(TurnPower.class, SetColorResult.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionClassWithNullActionResultClassTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionClassWithExistedActionResultClassTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, TurnPowerResult.class);
    }
    @Test(expected = IllegalArgumentException.class)
    public void addActionClassWithDifferenceActionNameTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(SetColor.class, TurnPowerResult.class);
    }
}
