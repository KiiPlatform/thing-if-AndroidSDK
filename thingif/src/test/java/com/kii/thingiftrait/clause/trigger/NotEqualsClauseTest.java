package com.kii.thingiftrait.clause.trigger;

import com.kii.thingiftrait.SmallTestBase;
import com.kii.thingiftrait.clause.query.EqualsClauseInQuery;
import com.kii.thingiftrait.clause.query.NotEqualsClauseInQuery;

import org.junit.Assert;
import org.junit.Test;

public class NotEqualsClauseTest extends SmallTestBase {

    @Test
    public void testEquals_hashCode() {
        NotEqualsClauseInTrigger clause = new NotEqualsClauseInTrigger(
                new EqualsClauseInTrigger("alias", "f", "v")
        );

        Object[] sameClause = {
                clause,
                new NotEqualsClauseInTrigger(
                        new EqualsClauseInTrigger("alias", "f", "v")
                )
        };

        for(Object eClause : sameClause) {
            Assert.assertEquals("should equals", eClause, clause);
            Assert.assertTrue("should equals ", clause.equals(eClause));
            Assert.assertTrue("hash code should same", clause.hashCode() == eClause.hashCode());
        }

        Object[] diffClause = {
                null,
                new NotEqualsClauseInTrigger(
                        new EqualsClauseInTrigger("alias", "f", 1)
                ),
                new NotEqualsClauseInQuery(
                        new EqualsClauseInQuery("f", "v")
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
