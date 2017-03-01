package com.kii.thingif.clause.query;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.NotEqualsClauseInTrigger;

import org.junit.Assert;
import org.junit.Test;

public class NotEqualsClauseTest extends SmallTestBase {

    @Test
    public void testEquals_hashCode() {
        NotEqualsClauseInQuery clause = new NotEqualsClauseInQuery(
                new EqualsClauseInQuery("f", "v")
        );

        Object[] sameClause = {
                clause,
                new NotEqualsClauseInQuery(new EqualsClauseInQuery("f", "v")
                )
        };

        for(Object eClause : sameClause) {
            Assert.assertEquals("should equals", eClause, clause);
            Assert.assertTrue("should equals ", clause.equals(eClause));
            Assert.assertTrue("hash code should same", clause.hashCode() == eClause.hashCode());
        }

        Object[] diffClause = {
                null,
                new NotEqualsClauseInQuery(new EqualsClauseInQuery("f", 1)
                ),
                new NotEqualsClauseInTrigger(
                        new EqualsClauseInTrigger("alias", "f", "v")
                )
        };
        for (Object dClause : diffClause) {
            Assert.assertNotEquals("should not equals", clause, dClause);
            Assert.assertFalse("should not equals", clause.equals(dClause));
            if( dClause != null) {
                Assert.assertFalse("hash code should not same", clause.hashCode() == dClause.hashCode());
            }
        }
    }
}
