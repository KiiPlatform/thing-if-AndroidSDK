package com.kii.thingiftrait.clause.trigger;

import com.kii.thingiftrait.SmallTestBase;
import com.kii.thingiftrait.clause.query.EqualsClauseInQuery;

import org.junit.Assert;
import org.junit.Test;

public class EqulasClauseTest extends SmallTestBase{

    @Test
    public void testEquals_HashCode() throws Exception {
        EqualsClauseInTrigger clause = new EqualsClauseInTrigger("alias", "f", "v");

        // objects should equals to
        Object[] sameObjects = {
                clause,
                new EqualsClauseInTrigger("alias", "f", "v")
        };
        for (int i= 0; i < sameObjects.length; i++) {
            Assert.assertTrue(
                    "failed to test equals on ["+i+"]",
                    clause.equals(sameObjects[i]));
            Assert.assertTrue(
                    "failed to test hashCode on ["+i+"]",
                    clause.hashCode() == sameObjects[i].hashCode());
        }

        // objects should not equals to
        Object[] diffObjects = {
                null,
                new EqualsClauseInTrigger("alias1", "f", "v"),
                new EqualsClauseInTrigger("alias", "f1", "v"),
                new EqualsClauseInTrigger("alias", "f", "v1"),
                new EqualsClauseInTrigger("alias", "f", 1),
                new EqualsClauseInQuery("f", "v")
        };
        for (int j=0; j < diffObjects.length; j++) {
            Assert.assertFalse(
                    "failed to test equals on ["+j+"]",
                    clause.equals(diffObjects[j]));
            if(diffObjects[j] != null) {
                Assert.assertFalse(
                        "failed to test hashCode on [" + j + "]",
                        clause.hashCode() == diffObjects[j].hashCode());
            }
        }
    }
}
