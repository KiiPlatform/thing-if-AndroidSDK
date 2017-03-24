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
import com.kii.thingif.exception.UnregisteredAliasException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AliasActionListAdapter implements
        JsonSerializer<List<AliasAction>>, JsonDeserializer<List<AliasAction>> {

    private Map<String, Class<? extends Action>> actionTypes;

    public AliasActionListAdapter(Map<String, Class<? extends Action>> actionTypes) {
        this.actionTypes = actionTypes;

    }

    private JsonElement combineAliasActionJson(String alias, JsonArray sameAliasActionArray) {
        if (sameAliasActionArray.size() == 1) {
            return sameAliasActionArray.get(0);
        } else {
            JsonArray allActions = new JsonArray();
            for (int j = 0; j < sameAliasActionArray.size(); j++) {
                JsonObject aliasAction = sameAliasActionArray.get(j).getAsJsonObject();
                allActions.addAll(aliasAction.get(alias).getAsJsonArray());
            }
            JsonObject newAliasAction = new JsonObject();
            newAliasAction.add(alias, allActions);
            return newAliasAction;
        }
    }
    @Override
    public JsonElement serialize(List<AliasAction> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return null;
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        AliasAction.class,
                        new AliasActionAdapter(this.actionTypes))
                .create();
        JsonArray aliasActionsArray = new JsonArray();

        int sameFlag = 0;
        JsonArray sameAliasActionArray = new JsonArray();
        for (int i=0; i<src.size(); i++) {
            String aliasOfSameFlag = src.get(sameFlag).getAlias();
            String alias = src.get(i).getAlias();

            // if index reach diff alias, then join the above same flag
            if (!alias.equals(aliasOfSameFlag)){
                // combine the previous same alias name aliasActions
                aliasActionsArray.add(combineAliasActionJson(aliasOfSameFlag, sameAliasActionArray));

                // reset sameAliasActionArray
                sameAliasActionArray = new JsonArray();
                // set sameFlag to same as index
                sameFlag = i;
            }

            sameAliasActionArray.add(gson.toJsonTree(src.get(i)));
        }
        if (sameAliasActionArray.size() != 0) {
            aliasActionsArray.add(combineAliasActionJson(
                    src.get(sameFlag).getAlias(),
                    sameAliasActionArray));
        }
        return aliasActionsArray;
    }

    @Override
    public List<AliasAction> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || !json.isJsonArray()) return null;

        JsonArray aliasActions = json.getAsJsonArray();

        List<AliasAction> ret = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement jsonElement: aliasActions) {
            JsonObject aliasAction = jsonElement.getAsJsonObject();

            if (!aliasAction.entrySet().iterator().hasNext()){
                continue;
            }
            Map.Entry<String, JsonElement> firstEntry = aliasAction.entrySet().iterator().next();
            String alias = firstEntry.getKey();
            if (!this.actionTypes.containsKey(alias)) {
                throw new JsonParseException(new UnregisteredAliasException(alias, true));
            }
            Class<? extends Action> actionCls = this.actionTypes.get(alias);
            JsonArray actions = firstEntry.getValue().getAsJsonArray();
            for (JsonElement action: actions) {
                Action actionInstance = gson.fromJson(action, actionCls);
                ret.add(new AliasAction(alias, actionInstance));
            }
        }
        return ret;
    }
}
