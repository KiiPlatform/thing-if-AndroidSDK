package com.kii.thingif.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.AliasAction;
import com.kii.thingif.exception.UnregisteredAliasException;

import java.lang.reflect.Type;
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

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Action.class,
                        new ActionAdapter(this.actionTypes.get(src.getAlias())))
                .create();
        JsonObject json = new JsonObject();
        json.add(
                src.getAlias(),
                gson.toJsonTree(src.getAction(), Action.class));
        return json;
    }

    @Override
    public AliasAction deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//        if (jsonElement == null) return null;
//
//        JsonObject json = jsonElement.getAsJsonObject();
//
//        if (!json.entrySet().iterator().hasNext()){
//            return null;
//        }
//        Map.Entry<String, JsonElement> firstEntry = json.entrySet().iterator().next();
//        String alias = firstEntry.getKey();
//
//        if (!this.actionTypes.containsKey(firstEntry.getKey())) {
//            throw new JsonParseException(new UnregisteredAliasException(alias, true));
//        }
//        Class<? extends Action> actionClass = this.actionTypes.get(alias);
//        JsonElement actionJson = json.get(alias);
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(
//                        Action.class,
//                        new ActionAdapter(actionClass))
//                .create();
//        Action action = gson.fromJson(actionJson, Action.class);
//        return new AliasAction(alias, action);
        //TODO: // FIXME: 2017/03/27
        return null;
    }
}
