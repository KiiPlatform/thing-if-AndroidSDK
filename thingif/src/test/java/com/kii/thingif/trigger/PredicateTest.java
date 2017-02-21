package com.kii.thingif.trigger;

import android.os.Parcel;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.clause.trigger.AndClauseInTrigger;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Date;

@RunWith(RobolectricTestRunner.class)
public class PredicateTest extends SmallTestBase{
    @Test
    public void parcelableTest() {
        String alias1 = "AirConditionerAlias";
        String alias2 = "HumidityAlias";
        Long scheduleDate = new Date().getTime();

        // test StatePredicate
        StatePredicate[] statePredicates = {
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
                        TriggersWhen.CONDITION_TRUE)
        };

        for (StatePredicate predicate: statePredicates) {
            Parcel parcel = Parcel.obtain();
            predicate.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            Predicate deserializedPredicate = StatePredicate.CREATOR.createFromParcel(parcel);
            assertSamePredicate(predicate, deserializedPredicate);
        }

        // test SchedulePredicate
        SchedulePredicate schedulePredicate = new SchedulePredicate("1 * * * *");
        Parcel parcel = Parcel.obtain();
        schedulePredicate.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Predicate deserializedSchedulePredicate = SchedulePredicate.CREATOR.createFromParcel(parcel);
        assertSamePredicate(schedulePredicate, deserializedSchedulePredicate);

        // test ScheduleOncePredicate
        ScheduleOncePredicate scheduleOncePredicate = new ScheduleOncePredicate(scheduleDate);
        parcel = Parcel.obtain();
        scheduleOncePredicate.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Predicate deserializedScheduleOncePredicate = ScheduleOncePredicate.CREATOR.createFromParcel(parcel);
        assertSamePredicate(scheduleOncePredicate, deserializedScheduleOncePredicate);
    }
}
