package com.kii.thingif.clause.trigger;

import com.kii.thingif.SmallTestBase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class OrClauseTest extends SmallTestBase {

    @Test
    public void testToJSONObject() throws JSONException {
        JSONObject[] clauses1 = {
                RangeClauseInTrigger.greaterThan("alais", "f", 23).toJSONObject(),
                RangeClauseInTrigger.lessThan("alais", "f", 230).toJSONObject()
        };
        JSONObject[] clauses2 = {
                new NotEqualsClauseInTrigger(new EqualsClauseInTrigger("alias", "f", 46)).toJSONObject(),
                RangeClauseInTrigger.greaterThan("alias", "f", 23).toJSONObject()
        };
        JSONObject[] clauses3 = {
                new NotEqualsClauseInTrigger(new EqualsClauseInTrigger("alias", "f", 100)).toJSONObject(),
                RangeClauseInTrigger.range("alias", "f", 23, false, 230, false).toJSONObject()
        };

        JSONObject[] expectedJsons = {
                new JSONObject().put("type", "or").put("clauses", clauses1),
                new JSONObject().put("type", "or").put("clauses", clauses2),
                new JSONObject().put("type", "or").put("clauses", clauses3)
        };

        OrClauseInTrigger[] actualClauses = {
                new OrClauseInTrigger(
                        RangeClauseInTrigger.greaterThan("alais", "f", 23),
                        RangeClauseInTrigger.lessThan("alais", "f", 230)),
                new OrClauseInTrigger(
                        new NotEqualsClauseInTrigger(new EqualsClauseInTrigger("alias", "f", 46)),
                        RangeClauseInTrigger.greaterThan("alias", "f", 23)),
                new OrClauseInTrigger(
                        new NotEqualsClauseInTrigger(new EqualsClauseInTrigger("alias", "f", 100)),
                        RangeClauseInTrigger.range("alias", "f", 23, false, 230, false))
        };

        Assert.assertEquals("size should be same", expectedJsons.length, actualClauses.length);

        for (int i = 0; i < expectedJsons.length; i++) {
            assertJSONObject("failed on ["+i+"]", expectedJsons[i], actualClauses[i].toJSONObject());
        }
    }
}
