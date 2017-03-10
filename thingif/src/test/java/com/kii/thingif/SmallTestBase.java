package com.kii.thingif;


import com.google.gson.JsonParser;
import com.kii.thingif.clause.trigger.TriggerClause;
import com.kii.thingif.command.Command;
import com.kii.thingif.query.GroupedHistoryStates;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.Trigger;
import com.kii.thingif.trigger.TriggeredServerCodeResult;
import com.kii.thingif.utils.JsonUtil;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SmallTestBase {


    protected void assertJSONObject(
            String errorMessage,
            JSONObject expected,
            JSONObject actual)
    {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(errorMessage,
                new JsonParser().parse(expected.toString()),
                new JsonParser().parse(actual.toString()));
    }
    protected void assertJSONObject(JSONObject expected, JSONObject actual) {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(new JsonParser().parse(expected.toString()), new JsonParser().parse(actual.toString()));
    }
    protected void assertJSONArray(JSONArray expected, JSONArray actual) throws JSONException {
        if (expected == null && actual == null) {
            return;
        }
        Assert.assertEquals(expected.length(), actual.length());
        for (int i = 0; i< expected.length(); i++) {
            Object e = expected.get(i);
            Object a = actual.get(i);
            if (a instanceof JSONObject) {
                assertJSONObject((JSONObject)e, (JSONObject)a);
            } else if (a instanceof JSONArray) {
                assertJSONArray((JSONArray)e, (JSONArray)a);
            } else {
                Assert.assertEquals(e, a);
            }
        }
    }

    protected void assertSameCommands(Command expected, Command actual) {
        Assert.assertEquals(
                JsonUtil.commandToJson(expected).toString(),
                JsonUtil.commandToJson(actual).toString());
    }

    protected void assertSameTriggerClauses(String message, TriggerClause expected, TriggerClause actual) {
        assertJSONObject(
                message,
                JsonUtil.triggerClauseToJson(expected),
                JsonUtil.triggerClauseToJson(actual));
    }
    protected void assertSameTriggerClauses(TriggerClause expected, TriggerClause actual) {
        Assert.assertEquals(
                JsonUtil.triggerClauseToJson(expected).toString(),
                JsonUtil.triggerClauseToJson(actual).toString());
    }

    protected void assertSamePredicate(String message, Predicate expected, Predicate actual) {
        assertJSONObject(
                message,
                JsonUtil.predicateToJson(expected),
                JsonUtil.predicateToJson(actual));
    }
        protected void assertSamePredicate(Predicate expected, Predicate actual) {
        Assert.assertEquals(
                JsonUtil.predicateToJson(expected).toString(),
                JsonUtil.predicateToJson(actual).toString());
    }

    protected void assertServerCode(ServerCode expected, ServerCode actual) {
        Assert.assertEquals(
                JsonUtil.serverCodeToJson(expected).toString(),
                JsonUtil.serverCodeToJson(actual).toString());
    }

    protected void assertSameTrigger(Trigger expected, Trigger actual) {
        assertJSONObject(
                JsonUtil.triggerToJson(expected),
                JsonUtil.triggerToJson(actual));
    }

    protected void assertSameTrigger(String message, Trigger expected, Trigger actual) {
        assertJSONObject(
                message,
                JsonUtil.triggerToJson(expected),
                JsonUtil.triggerToJson(actual));
    }

    protected void assertSameTriggeredServerCodeResults(
            String message,
            TriggeredServerCodeResult expected,
            TriggeredServerCodeResult actual) {
        assertJSONObject(
                message,
                JsonUtil.triggeredServerCodeResultToJson(expected),
                JsonUtil.triggeredServerCodeResultToJson(actual)
        );
    }
    protected void assertSameTriggeredServerCodeResults(
            TriggeredServerCodeResult expected,
            TriggeredServerCodeResult actual) {
        assertJSONObject(
                JsonUtil.triggeredServerCodeResultToJson(expected),
                JsonUtil.triggeredServerCodeResultToJson(actual)
        );
    }

    protected void assertSameGroupedHistoryStates(
            GroupedHistoryStates expected,
            GroupedHistoryStates actual) {
        assertJSONObject(
                JsonUtil.groupedHistoryStateToJson(expected),
                JsonUtil.groupedHistoryStateToJson(actual)
        );
    }

    protected void assertSameGroupedHistoryStates(
            String message,
            GroupedHistoryStates expected,
            GroupedHistoryStates actual) {
        assertJSONObject(
                message,
                JsonUtil.groupedHistoryStateToJson(expected),
                JsonUtil.groupedHistoryStateToJson(actual)
        );
    }
}
