package com.kii.thingiftrait.utils;

import com.kii.thingiftrait.ServerError;
import com.kii.thingiftrait.actions.ActionToJSON;
import com.kii.thingiftrait.clause.query.AllClause;
import com.kii.thingiftrait.clause.query.AndClauseInQuery;
import com.kii.thingiftrait.clause.query.EqualsClauseInQuery;
import com.kii.thingiftrait.clause.query.NotEqualsClauseInQuery;
import com.kii.thingiftrait.clause.query.OrClauseInQuery;
import com.kii.thingiftrait.clause.query.QueryClause;
import com.kii.thingiftrait.clause.query.RangeClauseInQuery;
import com.kii.thingiftrait.clause.trigger.AndClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.EqualsClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.NotEqualsClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.OrClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.RangeClauseInTrigger;
import com.kii.thingiftrait.clause.trigger.TriggerClause;
import com.kii.thingiftrait.command.Action;
import com.kii.thingiftrait.command.ActionResult;
import com.kii.thingiftrait.command.AliasAction;
import com.kii.thingiftrait.command.AliasActionResult;
import com.kii.thingiftrait.command.Command;
import com.kii.thingiftrait.query.GroupedHistoryStates;
import com.kii.thingiftrait.query.HistoryState;
import com.kii.thingiftrait.query.TimeRange;
import com.kii.thingiftrait.states.StateToJson;
import com.kii.thingiftrait.trigger.Predicate;
import com.kii.thingiftrait.trigger.ScheduleOncePredicate;
import com.kii.thingiftrait.trigger.SchedulePredicate;
import com.kii.thingiftrait.trigger.ServerCode;
import com.kii.thingiftrait.trigger.StatePredicate;
import com.kii.thingiftrait.trigger.Trigger;
import com.kii.thingiftrait.trigger.TriggerOptions;
import com.kii.thingiftrait.trigger.TriggeredServerCodeResult;

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

            ret.putOpt("issuer", cmd.getIssuerID().toString());
            ret.putOpt("title", cmd.getTitle());
            ret.putOpt("description", cmd.getDescription());
            ret.putOpt("metadata", cmd.getMetadata());
            ret.putOpt("commandID", cmd.getCommandID());
            ret.putOpt("createdAt", cmd.getCreated());
            ret.putOpt("modifiedAt", cmd.getModified());
            if (cmd.getCommandState() != null) {
                ret.putOpt("commandState", cmd.getCommandState().name());
            }
            if (cmd.getTargetID() != null) {
                ret.put("target", cmd.getTargetID().toString());
            }
            ret.putOpt("firedByTriggerID", cmd.getFiredByTriggerID());

            JSONArray aliasActionsArray = new JSONArray();
            for (AliasAction aliasAction : cmd.getAliasActions()) {
                JSONArray actions = new JSONArray();
                for (Action action: aliasAction.getActions()) {
                    if (!(action instanceof ActionToJSON)) {
                        Assert.fail(action.getClass().getName()+
                                " not extend ToJSON interface for test purpose");
                    }else{
                        actions.put(((ActionToJSON)action).toJSONObject());
                    }
                }
                aliasActionsArray.put(new JSONObject().put(
                        aliasAction.getAlias(),
                        actions));
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

    public static JSONObject triggerToJson (Trigger trigger) {
        return createTriggerJson(trigger.getTriggerID(),
                trigger.getCommand(),
                trigger.getServerCode(),
                trigger.getPredicate(),
                TriggerOptions.Builder.newBuilder()
                        .setMetadata(trigger.getMetadata())
                        .setTitle(trigger.getTitle())
                        .setDescription(trigger.getDescription())
                        .build(),
                trigger.disabled(),
                trigger.getDisabledReason());
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

    public static JSONObject triggeredServerCodeResultToJson(TriggeredServerCodeResult result) {
        JSONObject ret = new JSONObject();
        try{
            ret.put("succeeded", result.isSucceeded());
            ret.putOpt("returnedValue", result.getReturnedValue());
            ret.put("executedAt", result.getExecutedAt());
            ret.putOpt("endpoint", result.getEndpoint());
            if (result.getError() != null) {
                ret.putOpt("error", serverErrorToJson(result.getError()));
            }
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject serverErrorToJson(ServerError err) {
        JSONObject ret = new JSONObject();
        try{
            ret.put("errorMessage", err.getErrorMessage());
            ret.put("details", new JSONObject()
                    .put("errorCode", err.getErrorCode())
                    .put("message", err.getDetailMessage()));
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject triggerOptionToJson(TriggerOptions option) {
        JSONObject ret = new JSONObject();
        try{
            ret.put("title", option.getTitle());
            ret.put("description", option.getDescription());
            ret.put("metadata", option.getMetadata());
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject queryClauseToJson(QueryClause clause) {
        try {
            if (clause instanceof EqualsClauseInQuery) {
                JSONObject json = new JSONObject();
                EqualsClauseInQuery eq = (EqualsClauseInQuery)clause;
                json.put("type", "eq");
                json.put("field", eq.getField());
                json.put("value", eq.getValue());
                return json;
            } else if(clause instanceof NotEqualsClauseInQuery) {
                return new JSONObject()
                        .put("type", "not")
                        .put("clause", queryClauseToJson(((NotEqualsClauseInQuery) clause).getEquals()));
            } else if (clause instanceof RangeClauseInQuery) {
                JSONObject rangeJson = new JSONObject();
                RangeClauseInQuery range = (RangeClauseInQuery) clause;
                rangeJson.put("type", "range");
                rangeJson.put("field", range.getField());
                rangeJson.putOpt("lowerIncluded", range.getLowerIncluded());
                rangeJson.putOpt("lowerLimit", range.getLowerLimit());
                rangeJson.putOpt("upperIncluded", range.getUpperIncluded());
                rangeJson.putOpt("upperLimit", range.getUpperLimit());
                return rangeJson;
            } else if (clause instanceof AndClauseInQuery) {
                JSONArray clauses = new JSONArray();
                for (QueryClause subClause : ((AndClauseInQuery)clause).getClauses()) {
                    clauses.put(queryClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "and")
                        .put("clauses", clauses);
            } else if (clause instanceof OrClauseInQuery) {

                JSONArray clauses = new JSONArray();
                for (QueryClause subClause : ((OrClauseInQuery) clause).getClauses()) {
                    clauses.put(queryClauseToJson(subClause));
                }
                return new JSONObject()
                        .put("type", "or")
                        .put("clauses", clauses);
            }else if (clause instanceof AllClause) {
                return new JSONObject().put("type", "all");
            } else {
                throw new RuntimeException("not support trigger clause");
            }
        }catch (JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JSONObject historyStateToJson(HistoryState historyState) {
        try{
            JSONObject ret = ((StateToJson)historyState.getState()).toJSONObject();
            ret.put("_created", historyState.getCreatedAt().getTime());
            return ret;
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject groupedHistoryStateToJson(GroupedHistoryStates groupedHistoryStates) {
        try{
            JSONObject ret = new JSONObject()
                    .put("range", timeRangeToJson(groupedHistoryStates.getTimeRange()));

            JSONArray historyStates = new JSONArray();
            for (int i=0; i<groupedHistoryStates.getObjects().size(); i++) {
                HistoryState historyState = (HistoryState) groupedHistoryStates.getObjects().get(i);
                historyStates.put(historyStateToJson(historyState));
            }
            return ret.put("objects", historyStates);

        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject timeRangeToJson(TimeRange range) {
        try{
            return new JSONObject()
                    .put("from", range.getFrom().getTime())
                    .put("to", range.getTo().getTime());
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject timeRangeToClause(TimeRange range) {
        try{
            return new JSONObject()
                    .put("type", "withinTimeRange")
                    .put("upperLimit", range.getTo().getTime())
                    .put("lowerLimit", range.getFrom().getTime());
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject aliasActionToJson(AliasAction aliasAction) {
        try{
            JSONArray actions = new JSONArray();
            for (Action action : aliasAction.getActions()) {
                actions.put(((ActionToJSON)action).toJSONObject());
            }
            return new JSONObject()
                    .put(aliasAction.getAlias(), actions);
        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
