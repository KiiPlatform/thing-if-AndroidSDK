package test.android.example.com.testappforcrosstrigger;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Site;
import com.kii.thingif.Target;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.ThingIFAPIBuilder;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.schema.Schema;
import com.kii.thingif.schema.SchemaBuilder;
import com.kii.thingif.trigger.Condition;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggersWhen;
import com.kii.thingif.trigger.clause.Equals;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected static final String APP_ID = "a4c6c6a7";
    protected static final String APP_KEY = "1154dcfd901bf1fdf5606d3b3d92e095";
    protected static final String DEMO_THING_TYPE = "LED";
    protected static final String DEMO_SCHEMA_NAME = "SmartLightDemo";
    protected static final int DEMO_SCHEMA_VERSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCreateTrigger(View v) {
        KiiApp app = new KiiApp(APP_ID, APP_KEY, Site.JP);
        Owner owner = new Owner(
                new TypedID(TypedID.Types.USER, "88eb20051321-743a-5e11-a7d9-0ce9503d"),
                "0nnEiJMO_sVQLTUZebhczNdmH6YptA7f6ziKv9xZy60");

        final List<Action> actions = new ArrayList<Action>();
        TurnPower action = new TurnPower(true);
        actions.add(action);
        final StatePredicate predicate = new StatePredicate(
                new Condition(new Equals("power", true)),
                TriggersWhen.CONDITION_FALSE_TO_TRUE);

        final ThingIFAPI api = ThingIFAPIBuilder.newBuilder(this, app, owner)
                .addSchema(createDefaultSchema()).build();
        final ThingIFAPI api2 = ThingIFAPIBuilder.newBuilder(this, app, owner)
                .addSchema(createDefaultSchema()).build();
        AsyncTask<Void, Void, Trigger> task = new AsyncTask<Void, Void, Trigger>() {
            Target commandTarget = null;
            protected Trigger doInBackground(Void... args) {
                Trigger trigger = null;
                try {
                    commandTarget = api2.onboard("test2", "1234", null, null);
                    api.onboard("test1", "1234", null, null);
                    trigger = api.postNewTrigger(DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, actions,
                            predicate, commandTarget);
                } catch (ThingIFException e) {
                    Log.e(this.getClass().getName(), e.getMessage(), e);
                }
                return trigger;
            }
            protected void onPostExecute(Trigger response) {
                if (response != null) {
                    Log.d(this.getClass().getName(), "API target ID     : " + api.getTarget().getTypedID().getID());
                    Log.d(this.getClass().getName(), "Command target ID : " + commandTarget.getTypedID().getID());
                    Log.d(this.getClass().getName(), "Trigger target ID : " + response.getTargetID().getID());
                    Log.d(this.getClass().getName(), "Trigger com tar ID: " + response.getCommand().getTargetID().getID());
                    Log.d(this.getClass().getName(), "Trigger command ID: " + response.getCommand().getCommandID());
                } else {
                    Log.d(this.getClass().getName(), "failed.");
                }
            }
        };
        task.execute();
    }

    protected Schema createDefaultSchema() {
        SchemaBuilder sb = SchemaBuilder.newSchemaBuilder(
                DEMO_THING_TYPE, DEMO_SCHEMA_NAME, DEMO_SCHEMA_VERSION, LightState.class);
        sb.addActionClass(TurnPower.class, TurnPowerResult.class);
        return sb.build();
    }
}
