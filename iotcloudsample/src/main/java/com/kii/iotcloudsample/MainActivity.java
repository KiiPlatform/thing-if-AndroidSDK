package com.kii.iotcloudsample;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.kii.iotcloud.IoTCloudAPI;
import com.kii.iotcloud.IoTCloudAPIBuilder;
import com.kii.iotcloud.Owner;
import com.kii.iotcloud.Site;
import com.kii.iotcloud.schema.Schema;
import com.kii.iotcloud.schema.SchemaBuilder;
import com.kii.iotcloud.Target;
import com.kii.iotcloud.TypedID;
import com.kii.iotcloud.command.Action;
import com.kii.iotcloud.command.Command;
import com.kii.iotcloud.trigger.Predicate;
import com.kii.iotcloud.trigger.Schedule;
import com.kii.iotcloud.trigger.SchedulePredicate;
import com.kii.iotcloud.trigger.Trigger;
import com.kii.iotcloudsample.smart_light_demo.LightState;
import com.kii.iotcloudsample.smart_light_demo.SetBrightness;
import com.kii.iotcloudsample.smart_light_demo.SetBrightnessResult;
import com.kii.iotcloudsample.smart_light_demo.SetColor;
import com.kii.iotcloudsample.smart_light_demo.SetColorResult;
import com.kii.iotcloudsample.smart_light_demo.SetColorTemperature;
import com.kii.iotcloudsample.smart_light_demo.SetColorTemperatureResult;
import com.kii.iotcloudsample.smart_light_demo.TurnPower;
import com.kii.iotcloudsample.smart_light_demo.TurnPowerResult;

import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDeferredManager;
import org.jdeferred.android.DeferredAsyncTask;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private IoTCloudAPI api;
    private AndroidDeferredManager adm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adm = new AndroidDeferredManager();

        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder("smartLight",
                "SmartLight-Demo",
                1, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class).
                addActionClass(SetColor.class, SetColorResult.class).
                addActionClass(SetBrightness.class, SetBrightnessResult.class).
                addActionClass(SetColorTemperature.class, SetColorTemperatureResult.class);

        Schema smartLightSchema = sb.build();

        IoTCloudAPIBuilder ib = IoTCloudAPIBuilder.newBuilder(this, "myAppID",
                "myAppKey", Site.JP, this.getOwner());
        ib.addSchema(smartLightSchema);
        api = ib.build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adm != null)
            adm.getExecutorService().shutdown();
    }

    private Owner getOwner() {
        // Dummy.
        return null;
    }

    // On board in background.
    Promise<Target, Throwable, Void> onBoard(final String thingID, final String thingPassword) {
        return adm.when(new DeferredAsyncTask<Void, Void, Target>() {
            @Override
            protected Target doInBackgroundSafe(Void... voids) throws
                    Exception {
                return api.onBoard(thingID, thingPassword);
            }
        });
    }

    // Post new command in background as a member of group owns target.
    Promise<Command, Throwable, Void> postNewCommand(final Target target) {
        return adm.when(new DeferredAsyncTask<Void, Void, Command>() {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws
                    Exception {
                List<Action> actions = new ArrayList<Action>();

                TurnPower a1 = new TurnPower();
                a1.power = true;

                SetBrightness a2 = new SetBrightness();
                a2.brightness = 90;

                SetColor a3 = new SetColor();
                a3.color = new int[] {255, 0, 255};

                SetColorTemperature a4 = new SetColorTemperature();
                a4.colorTemperature = 5000;

                actions.add(a1);
                actions.add(a2);
                actions.add(a3);
                actions.add(a4);
                return api.postNewCommand(target, "SmartLight-Demo", 1, actions);
            }
        });
    }

    // Get Command in background.
    Promise<Command, Throwable, Void> getCommand(final Target target, final String commandID) {
        return adm.when(new DeferredAsyncTask<Void, Void, Command> () {
            @Override
            protected Command doInBackgroundSafe(Void... voids) throws
                    Exception {
                return api.getCommand(target, commandID);
            }
        });
    }

    // Post new schedule Trigger in background.
    Promise<Trigger, Throwable, Void> postNewTrigger(final Target target) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws
                    Exception {
                List<Action> actions = new ArrayList<Action>();
                TurnPower a1 = new TurnPower();
                a1.power = true;

                SetBrightness a2 = new SetBrightness();
                a2.brightness = 20;

                Schedule schedule = new Schedule("/15 * * * *");
                Predicate predicate = new SchedulePredicate(schedule);
                return api.postNewTrigger(target, "SmartLight-Demo", 1,
                        actions, predicate);
            }
        });
    }

    // Get the Trigger in background.
    Promise<Trigger, Throwable, Void> getTrigger(final Target target, final String triggerID) {
        return adm.when(new DeferredAsyncTask<Void, Void, Trigger>() {
            @Override
            protected Trigger doInBackgroundSafe(Void... voids) throws
                    Exception {
                return api.getTrigger(target, triggerID);
            }
        });
    }

    // Get State in background
    Promise<LightState, Throwable, Void> getState(final Target target) {
        return adm.when(new DeferredAsyncTask<Void, Void, LightState>() {
            @Override
            protected LightState doInBackgroundSafe(Void... voids) throws
                    Exception {
                return api.getTargetState(target, LightState.class);
            }
        });
    }
}
