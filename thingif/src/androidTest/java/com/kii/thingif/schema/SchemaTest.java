package com.kii.thingif.schema;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.testschemas.LightState;
import com.kii.thingif.testschemas.SetColor;
import com.kii.thingif.testschemas.SetColorResult;
import com.kii.thingif.testschemas.TurnPower;
import com.kii.thingif.testschemas.TurnPowerResult;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SchemaTest extends SmallTestBase {
    @Test
    public void basicTest() throws Exception {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("SmartLight", "LightDemoSchema", 1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        Schema schema = sb.build();
        Assert.assertEquals(TurnPower.class, schema.getActionClass(new TurnPower().getActionName()));
        Assert.assertEquals(SetColor.class, schema.getActionClass(new SetColor().getActionName()));
        Assert.assertEquals(TurnPowerResult.class, schema.getActionResultClass(new TurnPower().getActionName()));
        Assert.assertEquals(SetColorResult.class, schema.getActionResultClass(new SetColor().getActionName()));
    }
}
