package com.kii.thingif.utils;

import com.kii.thingif.actions.ToJSON;
import com.kii.thingif.clause.trigger.AndClauseInTrigger;
import com.kii.thingif.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thingif.clause.trigger.OrClauseInTrigger;
import com.kii.thingif.clause.trigger.RangeClauseInTrigger;
import com.kii.thingif.clause.trigger.TriggerClause;
import com.kii.thingif.command.ActionResult;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.command.AliasActionResult;
import com.kii.thingif.command.Command;
import com.kii.thingif.trigger.Predicate;
import com.kii.thingif.trigger.ScheduleOncePredicate;
import com.kii.thingif.trigger.SchedulePredicate;
import com.kii.thingif.trigger.ServerCode;
import com.kii.thingif.trigger.StatePredicate;
import com.kii.thingif.trigger.TriggerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;

public class JsonUtil {
    public static JSONObject triggerClauseToJson(TriggerClause clause) {
        try {
            if (clause instanceof EqualsClauseInTrigger) {
                JSONObject json = new JSONObject();
                EqualsClauseInTrigger eq = (EqualsClauseInTrigger)clause;
                json.put("type", "eq");
                json.put("alias", eq.getAlias());
                json.put("field", eq.getField());
                json.put("value", eq.getValue());
                return json;
            } else if(clause instanceof NotEqualsClauseInTrigger) {
                return new JSONObject()
                        .put("type", "not")
                        .put("clause", triggerClauseToJson(((NotEqualsClauseInTrigger) clause).getEquals()));
            } else if (clause instanceof RangeClauseInTrigger) {
                JSONObject rangeJson = new JSONObject();
                RangeClauseInTrigger range = (RangeClauseInTrigger) clause;
                rangeJson.put("type", "range");
                rangeJson.put("alias", range.getAlias());
                rangeJson.put("field", range.getField());
                rangeJson.putOpt("lowerIncluded", range.getLowerIncluded());
                rangeJson.putOpt("lowerLimit", range.getLowerLimit());
                rangeJson.putOpt("upperIncluded", range.getUpperIncluded());
                rangeJson.putOpt("upperLimit", range.getUpperLimit());
                return rangeJson;
            } else if (clause instanceof AndClauseInTrigger) {
                JSONArray clauses = new JSONArray();
                for (TriggerClause subClause : ((AndClauseInTrigger)clause).getClauses()) {
                    clauses.put(triggerClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "and")
                        .put("clauses", clauses);
            } else if (clause instanceof OrClauseInTrigger) {

                JSONArray clauses = new JSONArray();
                for (TriggerClause subClause : ((OrClauseInTrigger) clause).getClauses()) {
                    clauses.put(triggerClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "or")
                        .put("clauses", clauses);
            }else{
                throw new RuntimeException("not support trigger clause");
            }
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
    public static JSONObject predicateToJson(Predicate predicate) {
        try {
            JSONObject predicateJson = new JSONObject().put(
                    "eventSource",
                    predicate.getEventSource().name());
            if (predicate instanceof StatePredicate) {
                return predicateJson
                        .put("triggersWhen",
                                ((StatePredicate) predicate).getTriggersWhen().name())
                        .put("condition",
                                triggerClauseToJson(((StatePredicate) predicate).getCondition().getClause()));
            } else if (predicate instanceof ScheduleOncePredicate) {
                return predicateJson.put(
                        "scheduleAt",
                        ((ScheduleOncePredicate) predicate).getScheduleAt());
            } else if (predicate instanceof SchedulePredicate) {
                return predicateJson.put(
                        "schedule",
                        ((SchedulePredicate) predicate).getSchedule());
            } else {
                throw new RuntimeException("not support predicate");
            }
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject serverCodeToJson(ServerCode serverCode) {
        JSONObject ret = new JSONObject();
        try {
            ret.put("endpoint", serverCode.getEndpoint());
            ret.putOpt("executorAccessToken", serverCode.getExecutorAccessToken());
            ret.putOpt("targetAppID", serverCode.getTargetAppID());
            ret.putOpt("parameters", serverCode.getParameters());
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject commandToJson(Command cmd) {
        try{
            JSONObject ret = new JSONObject();
            ret.putOpt("title", cmd.getTitle());
            ret.putOpt("description", cmd.getDescription());
            ret.putOpt("metadata", cmd.getMetadata());
            ret.putOpt("commandID", cmd.getCommandID());
            ret.putOpt("createdAt", cmd.getCreated());
            ret.putOpt("modifiedAt", cmd.getModified());
            if (cmd.getCommandState() != null) {
                ret.putOpt("commandState", cmd.getCommandState().name());
            }
            ret.putOpt("firedByTriggerID", cmd.getFiredByTriggerID());

            JSONArray aliasActionsArray = new JSONArray();
            for (AliasAction aliasAction : cmd.getAliasActions()) {
                if (!(aliasAction.getAction() instanceof ToJSON)) {
                    Assert.fail(aliasAction.getAction().getClass().getName()+
                            " not extend ToJSON interface for test purpose");
                }else{
                    aliasActionsArray.put(new JSONObject()
                            .put(aliasAction.getAlias(),
                                    ((ToJSON)aliasAction.getAction()).toJSONArray()));
                }
            }
            if (aliasActionsArray.length() != 0) {
                ret.put("actions", aliasActionsArray);
            }

            if (cmd.getAliasActionResults() != null && cmd.getAliasActionResults().size()!= 0) {
                JSONArray aliasActionResultArray = new JSONArray();
                for (AliasActionResult aliasResult: cmd.getAliasActionResults()) {
                    JSONArray aliasResultJson = new JSONArray();
                    for (ActionResult result: aliasResult.getResults()) {
                        JSONObject resultJson = new JSONObject();
                        resultJson.put("succeeded", result.isSucceeded());
                        resultJson.putOpt("errorMessage", result.getErrorMessage());
                        resultJson.putOpt("data", result.getData());
                        aliasResultJson.put(new JSONObject().put(
                                result.getActionName(),
                                resultJson));
                    }
                    aliasActionResultArray.put(new JSONObject().put(
                            aliasResult.getAlias(),
                            aliasResultJson));
                }
                ret.put("actionResults", aliasActionResultArray);
            }
            return ret;

        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JSONObject createTriggerJson(
            String triggerID,
            Command command,
            ServerCode serverCode,
            Predicate predicate,
            TriggerOptions options,
            Boolean disabled,
            String disabledReason) {
        JSONObject ret = new JSONObject();
        try {
            if (options != null) {
                ret.putOpt("title", options.getTitle());
                ret.putOpt("description", options.getDescription());
                ret.putOpt("metadata", options.getMetadata());
            }
                ret.put("triggerID", triggerID);
            if (command != null) {
                ret.put("command", JsonUtil.commandToJson(command));
            }
            if (serverCode != null) {
                ret.put("serverCode", JsonUtil.serverCodeToJson(serverCode));
            }
            if (predicate != null) {
                ret.put("predicate", JsonUtil.predicateToJson(predicate));
            }
            ret.putOpt("disabled", disabled);
            ret.putOpt("disabledReason", disabledReason);
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}