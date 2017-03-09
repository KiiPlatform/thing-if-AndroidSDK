package com.kii.thingif.trigger;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kii.thingif.SmallTestBase;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class TriggerOptionsTest extends SmallTestBase {

    private static final class TestCase<T> {
        @NonNull String errorMessage;
        @NonNull TestData input;
        @NonNull T expected;

        TestCase(
                @NonNull String errorMessage,
                @NonNull TestData input,
                @NonNull T expected)
        {
            this.errorMessage = errorMessage;
            this.input = input;
            this.expected = expected;
        }
    }

    private static final class TestData {
        @Nullable String title;
        @Nullable String description;
        @Nullable JSONObject metadata;

        TestData(
                @Nullable String title,
                @Nullable String description,
                @Nullable JSONObject metadata)
        {
            this.title = title;
            this.description = description;
            this.metadata = metadata;
        }
    }

    private static List<TestCase<TestData>> createNormalTestCases()
            throws JSONException
    {
        List<TestCase<TestData>> retval = new ArrayList<>();
        JSONObject metadata = new JSONObject();
        metadata.put("key", "value");
        Collections.addAll(retval,
                createNormalTestData(
                        "1", new TestData(null, null, null)),
                createNormalTestData(
                        "2", new TestData(null, "description", null)),
                createNormalTestData(
                        "3", new TestData(null, null, metadata)),
                createNormalTestData(
                        "4", new TestData(null, "description", metadata)),
                createNormalTestData(
                        "5", new TestData("title", null, null)),
                createNormalTestData(
                        "6", new TestData("title", "description", null)),
                createNormalTestData(
                        "7", new TestData("title", null, metadata)),
                createNormalTestData(
                        "8", new TestData("title", "description", metadata)));
        return retval;
    }

    private static TestCase<TestData> createNormalTestData(
            @NonNull String errorMessage,
            @NonNull TestData testData)
    {
        return new TestCase<>(errorMessage, testData, testData);
    }

    @Test
    public void normalTest() throws Exception {

        for (TestCase<TestData> test : createNormalTestCases()) {
            TestData input = test.input;
            TestData expected = test.expected;
            String errorMessage = test.errorMessage;
            TriggerOptions.Builder builder = TriggerOptions.Builder.newBuilder();

            if (input.title != null) {
                Assert.assertEquals(errorMessage,
                        builder, builder.setTitle(input.title));
            }
            if (input.description != null) {
                Assert.assertEquals(errorMessage,
                        builder, builder.setDescription(input.description));
            }
            if (input.metadata != null) {
                Assert.assertEquals(errorMessage,
                        builder, builder.setMetadata(input.metadata));
            }

            Assert.assertEquals(errorMessage,
                    builder.getTitle(), expected.title);
            Assert.assertEquals(errorMessage,
                    builder.getDescription(), expected.description);
            assertJSONObject(errorMessage,
                    builder.getMetadata(), expected.metadata);

            TriggerOptions options = builder.build();
            Assert.assertNotNull(errorMessage, options);
            Assert.assertEquals(errorMessage,
                    expected.title, options.getTitle());
            Assert.assertEquals(errorMessage,
                    expected.description, options.getDescription());
            assertJSONObject(errorMessage,
                    expected.metadata, options.getMetadata());

            Parcel parcel = Parcel.obtain();
            options.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            TriggerOptions deserialized =
                    TriggerOptions.CREATOR.createFromParcel(parcel);
            Assert.assertNotNull(errorMessage, deserialized);
            Assert.assertEquals(errorMessage,
                    expected.title, deserialized.getTitle());
            Assert.assertEquals(errorMessage,
                    expected.description, deserialized.getDescription());
            assertJSONObject(errorMessage,
                    expected.metadata, deserialized.getMetadata());
        }
    }

    private List<TestCase<String>> createIllegalArgumentTestCases() {
        List<TestCase<String>> retval = new ArrayList<>();
        Collections.addAll(retval,
                new TestCase<>(
                        "1",
                        new TestData(RandomStringUtils.random(51), null, null),
                        "title is more than 50 charactors."),
                new TestCase<>(
                        "2",
                        new TestData(null, RandomStringUtils.random(201), null),
                        "description is more than 200 charactors."));
        return retval;
    }

    @Test
    public void illegalArgumentExceptionTest() throws Exception {
        for (TestCase<String> test : createIllegalArgumentTestCases()) {
            TestData input = test.input;
            String expected = test.expected;
            String errorMessage = test.errorMessage;
            TriggerOptions.Builder builder = TriggerOptions.Builder.newBuilder();
            IllegalArgumentException actual = null;

            try {
                if (input.title != null) {
                    builder.setTitle(input.title);
                } else if (input.description != null) {
                    builder.setDescription(input.description);
                }
            } catch (IllegalArgumentException e) {
                actual = e;
            }
            Assert.assertNotNull(errorMessage, actual);
            Assert.assertEquals(errorMessage, expected, actual.getMessage());
        }
    }

    @Test
    public void valueOverwriteTest() throws Exception {
        String title1 = "title1";
        String description1 = "description1";
        JSONObject metadata1 = new JSONObject();
        metadata1.put("key1", "value1");

        TriggerOptions.Builder builder = TriggerOptions.Builder.newBuilder();

        builder.setTitle(title1).setDescription(description1).
                setMetadata(metadata1);

        Assert.assertEquals(title1, builder.getTitle());
        Assert.assertEquals(description1, builder.getDescription());
        assertJSONObject(metadata1, builder.getMetadata());

        TriggerOptions options = builder.build();

        Assert.assertEquals(title1, options.getTitle());
        Assert.assertEquals(description1, options.getDescription());
        assertJSONObject(metadata1, options.getMetadata());


        String title2 = "title2";
        String description2 = "description2";
        JSONObject metadata2 = new JSONObject();
        metadata2.put("key2", "value2");

        builder.setTitle(title2).setDescription(description2).
                setMetadata(metadata2);

        Assert.assertEquals(title2, builder.getTitle());
        Assert.assertEquals(description2, builder.getDescription());
        assertJSONObject(metadata2, builder.getMetadata());

        options = builder.build();

        Assert.assertEquals(title2, options.getTitle());
        Assert.assertEquals(description2, options.getDescription());
        assertJSONObject(metadata2, options.getMetadata());
    }
}
