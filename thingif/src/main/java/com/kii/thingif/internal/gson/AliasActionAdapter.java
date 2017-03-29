package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.exception.UnsupportedActionException;
import com.kii.thingif.internal.utils.AliasUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AliasActionAdapter implements
        JsonSerializer<AliasAction>,
        JsonDeserializer<AliasAction> {

    private Map<String, Class<? extends Action>> actionTypes;

    public AliasActionAdapter(Map<String, Class<? extends Action>> actionTypes) {
        this.actionTypes = actionTypes;
    }
    @Override
    public JsonElement serialize(AliasAction src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;

        JsonArray actionsJson = new JsonArray();
        for (Action action: src.getActions()) {
            String key = AliasUtils.aliasActionKey(src.getAlias(), action.getActionName());
            Class<? extends Action> actionClass = this.actionTypes.get(key);
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                            Action.class,
                            new ActionAdapter(actionClass))
                    .create();
            actionsJson.add(gson.toJsonTree(action, Action.class));
        }
        JsonObject ret = new JsonObject();
        ret.add(src.getAlias(), actionsJson);
        return ret;
    }

    @Override
    public AliasAction deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) return null;

        JsonObject json = jsonElement.getAsJsonObject();

        if (!json.entrySet().iterator().hasNext()){
            return null;
        }
        Map.Entry<String, JsonElement> firstEntry = json.entrySet().iterator().next();
        String alias = firstEntry.getKey();
        List<Action> actions = new ArrayList<>();
        JsonArray actionsJA = firstEntry.getValue().getAsJsonArray();
        for (JsonElement acJE: actionsJA) {
            Map.Entry<String, JsonElement> actionME =
                    acJE.getAsJsonObject().entrySet().iterator().next();
            String acName = actionME.getKey();
            Class<? extends Action> acCls =
                    this.actionTypes.get(AliasUtils.aliasActionKey(alias, acName));
            if (acCls == null) {
                throw new JsonParseException(new UnsupportedActionException(acName, alias));
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(
                            Action.class,
                            new ActionAdapter(acCls))
                    .create();
            actions.add(gson.fromJson(acJE, Action.class));
        }
        return new AliasAction(alias, actions);
    }
}
