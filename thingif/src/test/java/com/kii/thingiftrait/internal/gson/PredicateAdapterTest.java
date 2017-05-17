package com.kii.thingiftrait.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kii.thingiftrait.SmallTestBase;
import com.kii.thingiftrait.clause.trigger.AndClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.RangeClauseInTrigger;
import com.kii.thingiftrait.trigger.Condition;
import com.kii.thingiftrait.trigger.Predicate;
import com.kii.thingiftrait.trigger.ScheduleOncePredicate;
import com.kii.thingiftrait.trigger.SchedulePredicate;
import com.kii.thingiftrait.trigger.StatePredicate;
import com.kii.thingiftrait.trigger.TriggersWhen;
import com.kii.thingiftrait.utils.JsonUtil;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class PredicateAdapterTest extends SmallTestBase{
    private static final String alias1 = "AirConditionerAlias";
    private static final String alias2 = "HumidityAlias";
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Predicate.class, new PredicateAdapter())
            .create();
    @Test
    public void serializationTest() throws Exception{
        Long scheduleDate = new Date().getTime();

        Predicate[] expectedPredicates = {
                new StatePredicate(
                        new Condition(new EqualsClauseInTrigger(alias1, "power", true)),
                        TriggersWhen.CONDITION_CHANGED),
                new StatePredicate(
                        new Condition(RangeClauseInTrigger.greaterThan(alias2, "humidity", 56)),
                        TriggersWhen.CONDITION_FALSE_TO_TRUE),
                new StatePredicate(
                        new Condition(new AndClauseInTrigger()
                                .addClause(new EqualsClauseInTrigger(alias1, "power", true))
                                .addClause(RangeClauseInTrigger.lessThan(alias2, "humidity", 45))),
                        TriggersWhen.CONDITION_TRUE),
                new SchedulePredicate("1 * * * *"),
                new ScheduleOncePredicate(scheduleDate)
        };

        for (int i=0; i<expectedPredicates.length; i++) {
            Predicate expectedPredicate = expectedPredicates[i];
            String jsonString = gson.toJson(expectedPredicate, Predicate.class);
            assertJSONObject("failed on ["+i+"]",
                    JsonUtil.predicateToJson(expectedPredicate),
                    new JSONObject(jsonString));
        }
    }

    @Test
    public void deserializationTest() {
        Long scheduleDate = new Date().getTime();

        Predicate[] expectedPredicates = {
                new StatePredicate(
                        new Condition(new EqualsClauseInTrigger(alias1, "power", true)),
                        TriggersWhen.CONDITION_CHANGED),
                new StatePredicate(
                        new Condition(RangeClauseInTrigger.greaterThan(alias2, "humidity", 56)),
                        TriggersWhen.CONDITION_FALSE_TO_TRUE),
                new StatePredicate(
                        new Condition(new AndClauseInTrigger()
                                .addClause(new EqualsClauseInTrigger(alias1, "power", true))
                                .addClause(RangeClauseInTrigger.lessThan(alias2, "humidity", 45))),
                        TriggersWhen.CONDITION_TRUE),
                new SchedulePredicate("1 * * * *"),
                new ScheduleOncePredicate(scheduleDate)
        };

        for (int i=0; i<expectedPredicates.length; i++) {
            Predicate expectedPredicate = expectedPredicates[i];
            Predicate deserializedPredicate = gson.fromJson(
                    JsonUtil.predicateToJson(expectedPredicate).toString(),
                    Predicate.class);
            assertSamePredicate(
                    "failed on ["+i+"]",
                    expectedPredicate,
                    deserializedPredicate);
        }
    }
}
