package com.kii.thingif.internal.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.TypedID;

import java.lang.reflect.Type;

public class TypedIDAdapter implements
        JsonSerializer<TypedID>,
        JsonDeserializer<TypedID>{

    @Override
    public JsonElement serialize(TypedID src, Type typeOfSrc, JsonSerializationContext context) {
        return src == null? null: new JsonPrimitive(src.toString());
    }

    @Override
    public TypedID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return json == null? null: TypedID.fromString(json.getAsString());
    }
}
