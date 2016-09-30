package com.kii.thingif.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2016/09/30.
 */
@RunWith(AndroidJUnit4.class)
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

    @Test
    public void normalTest() throws Exception {

        for (TestCase<TestData> test : createNormalTestCases()) {
            TestData input = test.input;
            TestData expected = test.expected;
            String errorMessage = test.errorMessage;
            TriggerOptions.Builder builder = TriggerOptions.Builder.builder();

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
                    options.getTitle(), expected.title);
            Assert.assertEquals(errorMessage,
                    options.getDescription(), expected.description);
            assertJSONObject(errorMessage,
                    options.getMetadata(), expected.metadata);
        }
    }

    private Collection<TestCase<TestData>> createNormalTestCases()
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
        return new TestCase<TestData>(errorMessage, testData, testData);
    }

}
