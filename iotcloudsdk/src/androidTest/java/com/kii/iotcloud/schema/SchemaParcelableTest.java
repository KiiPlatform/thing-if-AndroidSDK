package com.kii.iotcloud.schema;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import com.kii.iotcloud.SmallTestBase;
import com.kii.iotcloud.testschemas.LightState;
import com.kii.iotcloud.testschemas.SetColor;
import com.kii.iotcloud.testschemas.SetColorResult;
import com.kii.iotcloud.testschemas.SetColorTemperature;
import com.kii.iotcloud.testschemas.SetColorTemperatureResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SchemaParcelableTest extends SmallTestBase {
    @Test
    public void test() throws Exception {
        String thingType = "SmartLight";
        String schemaName = "TestSchema";
        int schemaVersion = 10;
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(thingType, schemaName, schemaVersion, LightState.class);
        sb.addActionClass(SetColor.class, SetColorResult.class);
        sb.addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);
        Schema schema = sb.build();

        Parcel parcel = Parcel.obtain();
        schema.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Schema deserializedSchema= Schema.CREATOR.createFromParcel(parcel);

        Assert.assertEquals(thingType, deserializedSchema.getThingType());
        Assert.assertEquals(schemaName, deserializedSchema.getSchemaName());
        Assert.assertEquals(schemaVersion, deserializedSchema.getSchemaVersion());
        Assert.assertEquals(LightState.class, deserializedSchema.getStateClass());
        Assert.assertEquals(2, deserializedSchema.getActionClasses().size());
        Assert.assertEquals(SetColor.class, deserializedSchema.getActionClasses().get(0));
        Assert.assertEquals(SetColorTemperature.class, deserializedSchema.getActionClasses().get(1));
        Assert.assertEquals(2, deserializedSchema.getActionResultClasses().size());
        Assert.assertEquals(SetColorResult.class, deserializedSchema.getActionResultClasses().get(0));
        Assert.assertEquals(SetColorTemperatureResult.class, deserializedSchema.getActionResultClasses().get(1));
    }
}
