package com.kii.thing_if.internal.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.kii.thing_if.TypedID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class TypedIDAdapterTest {
    @Test
    public void serializationTest() {
        TypedID[] typedIDs = {
                new TypedID(TypedID.Types.USER, "id1"),
                new TypedID(TypedID.Types.GROUP, "id2"),
                new TypedID(TypedID.Types.THING, "id3")};
        String[] expectedStrings = {
                "user:id1",
                "group:id2",
                "thing:id3"};

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        TypedID.class,
                        new TypedIDAdapter())
                .create();
        Assert.assertEquals(
                "size should be same",
                expectedStrings.length,
                typedIDs.length);
        for (int i=0; i < expectedStrings.length; i++) {
            String expectedString = expectedStrings[i];
            Assert.assertEquals(
                    "failed on ["+i+"]",
                    expectedString,
                    gson.toJsonTree(typedIDs[i]).getAsString());
        }
    }

    @Test
    public void deserializationTest() {
        TypedID[] expectedTypedIDs = {
                new TypedID(TypedID.Types.USER, "id1"),
                new TypedID(TypedID.Types.GROUP, "id2"),
                new TypedID(TypedID.Types.THING, "id3")};

        JsonElement[] jsons = {
                new JsonPrimitive("user:id1"),
                new JsonPrimitive("group:id2"),
                new JsonPrimitive("thing:id3")};

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        TypedID.class,
                        new TypedIDAdapter())
                .create();

        for (int i=0; i<expectedTypedIDs.length; i++){
            TypedID expectedTypedID = expectedTypedIDs[i];
            TypedID deserializedTypedID = gson.fromJson(jsons[i], TypedID.class);
            Assert.assertEquals(expectedTypedID.getType(), deserializedTypedID.getType());
            Assert.assertEquals(expectedTypedID.getID(), deserializedTypedID.getID());
        }
    }
 }
