package com.kii.thing_if.trigger;

import android.os.Parcel;

import com.kii.thing_if.SmallTestBase;
import com.kii.thing_if.clause.trigger.AndClauseInTrigger;
import com.kii.thing_if.clause.trigger.EqualsClauseInTrigger;
import com.kii.thing_if.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thing_if.clause.trigger.OrClauseInTrigger;
import com.kii.thing_if.clause.trigger.RangeClauseInTrigger;
import com.kii.thing_if.clause.trigger.TriggerClause;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ConditionTest extends SmallTestBase{
    @Test
    public void parcelableTest() {
        String alias1 = "AirConditionerAlias";
        String alias2 = "HumidityAlias";

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

        for (TriggerClause clause: clauses) {
            Condition condition = new Condition(clause);
            Parcel parcel = Parcel.obtain();
            condition.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);

            Condition deserializedCondition = Condition.CREATOR.createFromParcel(parcel);
            assertSameTriggerClauses(clause, deserializedCondition.getClause());
        }

    }
}
