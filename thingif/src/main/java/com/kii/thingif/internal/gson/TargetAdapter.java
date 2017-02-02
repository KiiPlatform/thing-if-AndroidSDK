package com.kii.thingif.internal.gson;

import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.StandaloneThing;
import com.kii.thingif.Target;
import com.kii.thingif.TargetThing;
import com.kii.thingif.TypedID;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.Gateway;

import java.lang.reflect.Type;

public class TargetAdapter implements JsonSerializer<Target>, JsonDeserializer<Target> {
    @Override
    public JsonElement serialize(Target src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return null;
        }

        JsonObject json = new JsonObject();
        json.addProperty("typedID", src.getTypedID().toString());
        json.addProperty("class", src.getClass().getName());

        if(!TextUtils.isEmpty(src.getAccessToken())) {
            json.addProperty("accessToken", src.getAccessToken());
        }

        if (src instanceof TargetThing) {
            json.addProperty("vendorThingID", ((TargetThing) src).getVendorThingID());
        }

        return json;
    }

    @Override
    public Target deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (jsonElement == null) {
            return null;
        }

        JsonObject json = jsonElement.getAsJsonObject();
        String accessToken =
                json.has("accessToken")? json.get("accessToken").getAsString(): null;
        String vendorThingID =
                json.has("vendorThingID")? json.get("vendorThingID").getAsString(): null;
        String className = json.get("class").getAsString();
        TypedID typedID = TypedID.fromString(json.get("typedID").getAsString());
        if (StandaloneThing.class.getName().equals(className)) {
            return new StandaloneThing(typedID.getID(), vendorThingID, accessToken);
        }else if (Gateway.class.getName().equals(className)) {
            return new Gateway(typedID.getID(), vendorThingID);
        }else if (EndNode.class.getName().equals(className)) {
            return new EndNode(typedID.getID(), vendorThingID, accessToken);
        }
        throw  new JsonParseException("Detected unknown type "+ className);
    }
}
