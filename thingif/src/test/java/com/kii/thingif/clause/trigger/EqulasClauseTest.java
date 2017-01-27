package com.kii.thingif.clause.trigger;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.clause.query.EqualsClauseInQuery;

import org.json.JSONObject;
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

    @Test
    public void testToJSONObject() {
        String alias = "alias";
        String field = "f";
        try {
            JSONObject[] expectedJsons = {
                    new JSONObject()
                            .put("type", "eq")
                            .put("alias", alias)
                            .put("field", field)
                            .put("value", "v"),
                    new JSONObject()
                            .put("type", "eq")
                            .put("alias", alias)
                            .put("field", field)
                            .put("value", 1),
                    new JSONObject()
                            .put("type", "eq")
                            .put("alias", alias)
                            .put("field", field)
                            .put("value", true)
            };

            EqualsClauseInTrigger[] clauses = {
                    new EqualsClauseInTrigger(alias, field, "v"),
                    new EqualsClauseInTrigger(alias, field, 1),
                    new EqualsClauseInTrigger(alias, field, true)
            };

            for (int i=0; i < clauses.length; i++) {
                assertJSONObject("failed on ["+i+"]", expectedJsons[i], clauses[i].toJSONObject());
            }
        }catch (Exception ex) {
            Assert.fail("failed caused by exception:"+ex.toString());
        }
    }
}
