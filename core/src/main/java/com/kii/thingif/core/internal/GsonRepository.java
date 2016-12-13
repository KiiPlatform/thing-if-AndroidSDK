package com.kii.thingif.core.internal;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.kii.thingif.core.StandaloneThing;
import com.kii.thingif.core.Target;
import com.kii.thingif.core.TargetThing;
import com.kii.thingif.core.TypedID;
import com.kii.thingif.core.gateway.EndNode;
import com.kii.thingif.core.gateway.Gateway;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Customize json serialization and deserialization for domain model of ThingIFSDK.
 * This class is for internal use only. Do not use it from your application.
 */
public class GsonRepository {

    private static final Map<Pair<String, Integer>, Gson> REPOSITORY = Collections.synchronizedMap(new HashMap<Pair<String, Integer>, Gson>());
    private static final Gson DEFAULT_GSON;
    private static final Gson PURE_GSON = new Gson();

    private static final JsonSerializer<JSONObject> ORG_JSON_OBJECT_SERIALIZER = new JsonSerializer<JSONObject>() {
        @Override
        public JsonElement serialize(JSONObject src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonParser().parse(src.toString());
        }
    };
    private static final JsonDeserializer<JSONObject> ORG_JSON_OBJECT_DESERIALIZER = new JsonDeserializer<JSONObject>() {
        @Override
        public JSONObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            try {
                return new JSONObject(json.toString());
            } catch (JSONException e) {
                throw new JsonParseException(e);
            }
        }
    };
    private static final JsonSerializer<TypedID> TYPED_ID_SERIALIZER = new JsonSerializer<TypedID>() {
        @Override
        public JsonElement serialize(TypedID src, Type typeOfSrc, JsonSerializationContext context) {
            return src == null ? null : new JsonPrimitive(src.toString());
        }
    };
    private static final JsonDeserializer<TypedID> TYPED_ID_DESERIALIZER = new JsonDeserializer<TypedID>() {
        @Override
        public TypedID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json == null) {
                return null;
            }
            return TypedID.fromString(json.getAsString());
        }
    };

    private static final JsonSerializer<Target> TARGET_SERIALIZER = new JsonSerializer<Target>() {
        @Override
        public JsonElement serialize(Target src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return null;
            }
            JsonObject json = new JsonObject();
            json.addProperty("typedID", src.getTypedID().toString());
            json.addProperty("class", src.getClass().getName());
            if (!TextUtils.isEmpty(src.getAccessToken())) {
                json.addProperty("accessToken", src.getAccessToken());
            }
            if (src instanceof TargetThing) {
                json.addProperty("vendorThingID", ((TargetThing)src).getVendorThingID());
            }
            return json;
        }
    };
    private static final JsonDeserializer<Target> TARGET_DESERIALIZER = new JsonDeserializer<Target>() {
        @Override
        public Target deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (jsonElement == null) {
                return null;
            }
            JsonObject json = (JsonObject)jsonElement;
            String accessToken = json.has("accessToken") ? json.get("accessToken").getAsString() : null;
            String vendorThingID = json.has("vendorThingID") ? json.get("vendorThingID").getAsString() : null;
            String className = json.get("class").getAsString();
            TypedID typedID = TypedID.fromString(json.get("typedID").getAsString());
            if (StandaloneThing.class.getName().equals(className)) {
                return new StandaloneThing(typedID.getID(), vendorThingID, accessToken);
            } else if (Gateway.class.getName().equals(className)) {
                return new Gateway(typedID.getID(), vendorThingID);
            } else if (EndNode.class.getName().equals(className)) {
                return new EndNode(typedID.getID(), vendorThingID, accessToken);
            }
            throw new JsonParseException("Detected unknown type " + className);
        }
    };

    private static final JsonSerializer<Uri> URI_SERIALIZER = new JsonSerializer<Uri>() {
        @Override
        public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("uri", src.toString());
            return json;
        }
    };

    private static final JsonDeserializer<Uri> URI_DESERIALIZER = new JsonDeserializer<Uri>() {
        @Override
        public Uri deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject json = (JsonObject)jsonElement;
            if (json.has("uri")) {
                return Uri.parse(json.get("uri").getAsString());
            }
            return null;
        }
    };

    static {
        DEFAULT_GSON = new GsonBuilder()
                .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_SERIALIZER)
                .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_DESERIALIZER)
                .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                .registerTypeAdapter(Target.class, TARGET_DESERIALIZER)
                .registerTypeAdapter(StandaloneThing.class, TARGET_SERIALIZER)
                .registerTypeAdapter(Gateway.class, TARGET_SERIALIZER)
                .registerTypeAdapter(EndNode.class, TARGET_SERIALIZER)
                .registerTypeAdapter(Uri.class, URI_SERIALIZER)
                .registerTypeAdapter(Uri.class, URI_DESERIALIZER)
                .create();
    }

    /**
     * Returns the Gson instance that can handle Action class and ActionResult class that are defined specified schema.
     * Action class and ActionResult class depend on the schema name and version.
     *
     * @return
     */
    public static Gson gson() {

        return new GsonBuilder()
                    .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_SERIALIZER)
                    .registerTypeAdapter(JSONObject.class, ORG_JSON_OBJECT_DESERIALIZER)
                    .registerTypeAdapter(TypedID.class, TYPED_ID_SERIALIZER)
                    .registerTypeAdapter(TypedID.class, TYPED_ID_DESERIALIZER)
                    .registerTypeAdapter(Target.class, TARGET_DESERIALIZER)
                    .registerTypeAdapter(StandaloneThing.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(Gateway.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(EndNode.class, TARGET_SERIALIZER)
                    .registerTypeAdapter(Uri.class, URI_SERIALIZER)
                    .registerTypeAdapter(Uri.class, URI_DESERIALIZER)
                    .create();
    }
    /**
     * This method is for unit tests use only. Do not use it from SDK code.
     */
    static void clearCache() {
        REPOSITORY.clear();
    }
}
