package com.kii.thingiftest.largetests;

import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingiftest.schema.SetColor;
import com.kii.thingiftest.schema.SetColorTemperature;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)

public class OneTimeTriggerTest extends LargeTestCaseBase {
    @Test
    public void basicScheduleOncePredicateTriggerTest() throws Exception {
        ThingIFAPI api = this.craeteThingIFAPIWithDemoSchema();
        String vendorThingID = UUID.randomUUID().toString();
        String thingPassword = "password";

        // on-boarding thing
        Target target = api.onboard(vendorThingID, thingPassword, DEMO_THING_TYPE, null);
        Assert.assertEquals(TypedID.Types.THING, target.getTypedID().getType());
        Assert.assertNotNull(target.getAccessToken());

        // create new trigger
        List<Action> actions1 = new ArrayList<Action>();
        SetColor setColor = new SetColor(128, 0, 255);
        SetColorTemperature setColorTemperature = new SetColorTemperature(25);
        actions1.add(setColor);
        actions1.add(setColorTemperature);
        long scheduledAtNextMinutes = System.currentTimeMillis() + (60*1000);
        Predicate predicate1 = new ScheduleOncePredicate(scheduledAtNextMinutes);

        Trigger trigger1 = api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions1, predicate1);
        Assert.assertNotNull(trigger1.getTriggerID());
        Assert.assertFalse(trigger1.disabled());
        Assert.assertNull(trigger1.getDisabledReason());
        Assert.assertEquals(target.getTypedID(), trigger1.getTargetID());
        Assert.assertNull(trigger1.getServerCode());

        Trigger trigger2 = api.getTrigger(trigger1.getTriggerID());
        Assert.assertNotNull(trigger2.getTriggerID());
    }
}
