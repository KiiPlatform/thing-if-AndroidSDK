package com.kii.thingiftrait.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingiftrait.clause.trigger.TriggerClause;
import com.kii.thingiftrait.trigger.Condition;
import com.kii.thingiftrait.trigger.Predicate;
import com.kii.thingiftrait.trigger.ScheduleOncePredicate;
import com.kii.thingiftrait.trigger.SchedulePredicate;
import com.kii.thingiftrait.trigger.StatePredicate;
import com.kii.thingiftrait.trigger.TriggersWhen;

import java.lang.reflect.Type;

public class PredicateAdapter implements
        JsonSerializer<Predicate>,
        JsonDeserializer<Predicate>{
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(TriggerClause.class, new TriggerClauseAdapter())
            .create();
    @Override
    public JsonElement serialize(Predicate src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;

        if (src instanceof StatePredicate) {
            JsonElement clause = gson.toJsonTree(
                    ((StatePredicate) src).getCondition().getClause(),
                    TriggerClause.class);
            JsonObject ret = new JsonObject();
            ret.add("condition", clause);
            ret.addProperty("triggersWhen", ((StatePredicate) src).getTriggersWhen().name());
            ret.addProperty("eventSource", src.getEventSource().name());
            return ret;
        } else {
            JsonObject ret = gson.toJsonTree(src).getAsJsonObject();
            ret.addProperty("eventSource", src.getEventSource().name());
            return ret;
        }
    }

    @Override
    public Predicate deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null || !jsonElement.isJsonObject()) return null;
        JsonObject json = jsonElement.getAsJsonObject();
        if (!json.has("eventSource")) {
            throw new JsonParseException("not eventSource found");
        }
        String eventSource = json.get("eventSource").getAsString();
        if (eventSource.equals("STATES")) {
            JsonObject clauseJson = json.get("condition").getAsJsonObject();
            String triggerWhenString = json.get("triggersWhen").getAsString();
            TriggersWhen triggersWhen;
            if (triggerWhenString.equals("CONDITION_TRUE")) {
                triggersWhen = TriggersWhen.CONDITION_TRUE;
            }else if (triggerWhenString.equals("CONDITION_FALSE_TO_TRUE")) {
                triggersWhen = TriggersWhen.CONDITION_FALSE_TO_TRUE;
            }else if (triggerWhenString.equals("CONDITION_CHANGED")) {
                triggersWhen = TriggersWhen.CONDITION_CHANGED;
            }else {
                throw new JsonParseException("invalid triggersWhen");
            }
            Condition condition = new Condition(gson.fromJson(clauseJson, TriggerClause.class));
            return new StatePredicate(condition, triggersWhen);
        }else if (eventSource.equals("SCHEDULE")) {
            String schedule = json.get("schedule").getAsString();
            return new SchedulePredicate(schedule);
        }else if (eventSource.equals("SCHEDULE_ONCE")) {
            long scheduleAt = json.get("scheduleAt").getAsLong();
            return new ScheduleOncePredicate(scheduleAt);
        }else {
            throw new JsonParseException("invalid eventSource value");
        }
    }
}
