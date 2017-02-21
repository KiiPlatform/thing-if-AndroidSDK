package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingif.SmallTestBase;
import com.kii.thingif.clause.trigger.AndClauseInTrigger;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.OrClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;
import com.kii.thingif.clause.trigger.TriggerClause;
import com.kii.thingif.utils.JsonUtil;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TriggerClauseAdapterTest extends SmallTestBase{
    private final static String alias1 = "AirConditionerAlias";
    private final static String alias2 = "HumidityAlias";

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    TriggerClause.class,
                    new TriggerClauseAdapter())
            .create();
    @Test
    public void serializationTest() throws Exception{
        TriggerClause[] clauses = {
                new EqualsClauseInTrigger(alias1, "power", true),
                new NotEqualsClauseInTrigger(new EqualsClauseInTrigger(alias1, "temperature", 23)),
                RangeClauseInTrigger.range(alias2, "humidity", 45, true, 23, true),
                new AndClauseInTrigger()
                        .addClause(new EqualsClauseInTrigger(alias1, "power", true))
                        .addClause(RangeClauseInTrigger.lessThan(alias2, "humidity", 34)),
                new OrClauseInTrigger().addClause(new EqualsClauseInTrigger(alias2, "humidity", 45))
                        .addClause(RangeClauseInTrigger.greaterThan(alias1, "temperature", 23))
        };
        for (int i=0; i<clauses.length; i++) {
            TriggerClause clause = clauses[i];
            String jsonString = gson.toJson(clause, TriggerClause.class);
            JSONObject deserializedJson = new JSONObject(jsonString);
            assertJSONObject(
                    "failed on ["+i+"]",
                    JsonUtil.triggerClauseToJson(clause),
                    deserializedJson);
        }
    }

    @Test
    public void deserializationTest() {
        TriggerClause[] expectedClauses = {
                new EqualsClauseInTrigger(alias1, "power", false),
                new NotEqualsClauseInTrigger(new EqualsClauseInTrigger(alias1, "temperature", 10)),
                RangeClauseInTrigger.range(alias2, "humidity", 45, true, 23, true),
                new AndClauseInTrigger()
                        .addClause(new EqualsClauseInTrigger(alias1, "power", true))
                        .addClause(RangeClauseInTrigger.lessThan(alias2, "humidity", 34)),
                new OrClauseInTrigger().addClause(new EqualsClauseInTrigger(alias2, "humidity", 45))
                        .addClause(RangeClauseInTrigger.greaterThan(alias1, "temperature", 23))
        };

        for (int i=0; i < expectedClauses.length; i++) {
            TriggerClause expectedClause = expectedClauses[i];
            String jsonString = JsonUtil.triggerClauseToJson(expectedClause).toString();
            TriggerClause deserializedClause = gson.fromJson(jsonString, TriggerClause.class);
            assertSameTriggerClauses(
                    "failed on ["+i+"]",
                    expectedClause,
                    deserializedClause);
        }
    }
}
