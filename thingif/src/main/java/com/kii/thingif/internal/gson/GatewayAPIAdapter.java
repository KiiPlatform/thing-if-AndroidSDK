package com.kii.thingif.internal.gson;

import android.content.Context;
import android.net.Uri;
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
import com.kii.thingif.KiiApp;
import com.kii.thingif.Target;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.GatewayAPI;

import java.lang.reflect.Type;

public class GatewayAPIAdapter implements JsonSerializer<GatewayAPI>, JsonDeserializer<GatewayAPI> {

    private Context androidContext;

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(Target.class, new TargetAdapter())
            .create();

    public GatewayAPIAdapter(Context context) {
        this.androidContext = context;
    }

    @Override
    public JsonElement serialize(GatewayAPI src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }

        JsonObject json = new JsonObject();

        json.add("app", gson.toJsonTree(src.getApp()));
        json.addProperty("gatewayAddress", src.getGatewayAddress().toString());

        if(!TextUtils.isEmpty(src.getTag())) {
            json.addProperty("tag", src.getTag());
        }

        if(!TextUtils.isEmpty(src.getAccessToken())) {
            json.addProperty("accessToken", src.getAccessToken());
        }

        return json;
    }

    @Override
    public GatewayAPI deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null ) {
            return null;
        }
        JsonObject json = jsonElement.getAsJsonObject();
        KiiApp app = gson.fromJson(json.getAsJsonObject("app"), KiiApp.class);
        Uri gatewayAddress = Uri.parse(json.get("gatewayAddress").getAsString());
        GatewayAPI.Builder builder = GatewayAPI.Builder.newBuilder(this.androidContext, app, gatewayAddress);
        if(json.has("tag")) {
            builder.setTag(json.get("tag").getAsString());
        }
        if(json.has("accessToken")) {
            builder.setAccessToken(json.get("accessToken").getAsString());
        }
        return builder.build();
    }
}
