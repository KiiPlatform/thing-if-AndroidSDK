package com.kii.thing_if.clause.trigger;

import com.kii.thing_if.SmallTestBase;

import org.junit.Assert;
import org.junit.Test;

public class RangeClauseTest extends SmallTestBase{
    @Test
    public void testEquals_hashCode() {

        // test half open range
        RangeClauseInTrigger clause =
                RangeClauseInTrigger.greaterThan("alias", "f", 23);

        Object[] sameObjs = {
                clause,
                RangeClauseInTrigger.greaterThan("alias", "f", 23)};
        for (Object obj: sameObjs) {
            Assert.assertEquals(obj, clause);
            Assert.assertTrue("should equals", obj.equals(clause));
            Assert.assertEquals("hashCode should be same", obj.hashCode(), clause.hashCode());
        }

        Object[] diffObjs = {
                null,
                RangeClauseInTrigger.greaterThan("alias", "e", 23),
                RangeClauseInTrigger.greaterThan("alias", "f", 23.0),
                RangeClauseInTrigger.lessThan("alias", "f", 23),
                RangeClauseInTrigger.greaterThanOrEqualTo("alias", "f", 23),
                RangeClauseInTrigger.lessThanOrEqualTo("alias", "f", 23),
                RangeClauseInTrigger.range("alias", "f", 230, false, 23, false)
        };

        for (int i=0; i < diffObjs.length; i++) {
            Assert.assertNotEquals("failed to test equals on ["+i+"]", diffObjs[i], clause);
            Assert.assertFalse("failed to test equals on ["+i+"]", clause.equals(diffObjs[i]));
            if(diffObjs[i] != null) {
                Assert.assertNotEquals(
                        "failed to test hashCode on ["+i+"]",
                        diffObjs[i].hashCode(),
                        clause.hashCode());
            }
        }

        // test close range
        RangeClauseInTrigger clause1 = RangeClauseInTrigger.range(
                "alias",
                "f",
                230,
                true,
                23,
                false
        );

        Object[] sameObjs1 = {
                clause1,
                RangeClauseInTrigger.range(
                        "alias",
                        "f",
                        230,
                        true,
                        23,
                        false
                )
        };
        for (Object obj :sameObjs1) {
            Assert.assertEquals("should equals", obj, clause1);
            Assert.assertTrue("should equals", clause1.equals(obj));
            Assert.assertEquals("hash code should same", clause1.hashCode(), obj.hashCode());
        }

        Object[] diffObjs1 = {
                null,
                RangeClauseInTrigger.range("alias", "e", 230, true, 23, true),
                RangeClauseInTrigger.greaterThan("alias", "f", 23.0),
                RangeClauseInTrigger.lessThan("alias", "f", 23),
                RangeClauseInTrigger.greaterThanOrEqualTo("alias", "f", 23),
                RangeClauseInTrigger.lessThanOrEqualTo("alias", "f", 23),
                RangeClauseInTrigger.range("alias", "f", 230, true, 23, true)
        };
        for (int i=0; i < diffObjs1.length; i++) {
            Assert.assertNotEquals("failed to test equals on ["+i+"]", diffObjs1[i], clause1);
            Assert.assertFalse("failed to test equals on ["+i+"]", clause1.equals(diffObjs1[i]));
            if (diffObjs1[i] != null) {
                Assert.assertNotEquals(
                        "failed to test hashCode on [" + i + "]",
                        clause1.hashCode(),
                        diffObjs1[i].hashCode());
            }
        }
    }
}
