package com.kii.thingif.internal.gson;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.kii.thingif.KiiApp;
import com.kii.thingif.Owner;
import com.kii.thingif.Target;
import com.kii.thingif.TargetState;
import com.kii.thingif.ThingIFAPI;
import com.kii.thingif.command.Action;

import java.lang.reflect.Type;
import java.util.Map;

public final class ThingIFAPIAdapter implements JsonSerializer<ThingIFAPI>, JsonDeserializer<ThingIFAPI>{

    private Type actionTypesType =
            new TypeToken<Map<String, Class<? extends Action>>>(){}.getType();
    private Type stateTypesType =
            new TypeToken<Map<String, Class<? extends TargetState>>>(){}.getType();
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Target.class, new TargetAdapter())
            .create();
    @Override
    public JsonElement serialize(ThingIFAPI src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }

        JsonObject json = new JsonObject();

        json.add("app", gson.toJsonTree(src.getApp()));

        if(!TextUtils.isEmpty(src.getTag())) {
            json.addProperty("tag", src.getTag());
        }

        json.add("owner", gson.toJsonTree(src.getOwner()));

        if(src.getTarget() != null) {
            json.add("target", gson.toJsonTree(src.getTarget(), Target.class));
        }else {
            json.add("target", JsonNull.INSTANCE);
        }

        json.addProperty("installationID", src.getInstallationID());

        json.add("actionTypes", gson.toJsonTree(src.getActionTypes(), this.actionTypesType));
        json.add("stateTypes", gson.toJsonTree(src.getStateTypes(), this.stateTypesType));
        return json;
    }

    @Override
    public ThingIFAPI deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null ) {
            return null;
        }
        JsonObject json = jsonElement.getAsJsonObject();
        KiiApp app = gson.fromJson(json.getAsJsonObject("app"), KiiApp.class);
        Owner owner = gson.fromJson(json.getAsJsonObject("owner"), Owner.class);
        Map<String, Class<? extends Action>> actionTypes =
                gson.fromJson(
                        json.getAsJsonObject("actionTypes"),
                        this.actionTypesType);
        Map<String, Class<? extends TargetState>> stateTypes =
                gson.fromJson(
                        json.getAsJsonObject("stateTypes"),
                        this.stateTypesType);
        ThingIFAPI.Builder builder = ThingIFAPI.Builder._newBuilder(app, owner, actionTypes, stateTypes);
        if(json.has("tag")) {
            builder.setTag(json.get("tag").getAsString());
        }
        if (json.has("target")) {
            Target target = gson.fromJson(json.getAsJsonObject("target"), Target.class);
            builder.setTarget(target);
        }
        if (json.has("installationID")) {
            builder.setInstallationID(json.get("installationID").getAsString());
        }
        return builder.build();
    }
}
