package com.kii.iotcloud;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.ActionResult;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.utils.GsonRepository;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class IoTCloudAPIParcelableTest {
    @Test
    public void test() throws Exception {
        Command c = new Command("スキーマ", 10, new TypedID(TypedID.Types.THING, "12345"), new TypedID(TypedID.Types.USER, "9876"));
        c.addAction(new SetColor());
        c.addAction(new SetColorTemperature());
        c.addActionResult(new SetColorResult());
        c.addActionResult(new SetColorTemperatureResult());
        Gson gson = GsonRepository.gson();
        Assert.assertEquals("{}", gson.toJson(c));
    }


    public static class SetColor extends Action {
        public int[] color = new int[3];
        public String getActionName() {
            return "setColor";
        }
    }
    public static class SetColorResult extends ActionResult {
        public String getActionName() {
            return "setColor";
        }
    }
    public static class SetColorTemperature extends Action {
        public int colorTemperature;
        public String getActionName() {
            return "setColorTemperature";
        }
    }
    public static class SetColorTemperatureResult extends ActionResult{
        public String getActionName() {
            return "setColorTemperature";
        }
    }

}
