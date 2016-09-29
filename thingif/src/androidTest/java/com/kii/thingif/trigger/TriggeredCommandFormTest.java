package com.kii.thingif.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.testschemas.SetColor;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TriggeredCommandFormTest extends SmallTestBase {

    private static final class TestCase<T> {
        @NonNull final TestData input;
        @NonNull final T expected;
        @NonNull final String errorMessge;

        TestCase(
                @NonNull String errorMessge,
                @NonNull TestData input,
                @NonNull T expected)
        {
            this.errorMessge = errorMessge;
            this.input = input;
            this.expected = expected;
        }
    }

    private static final class TestData {
        @NonNull final String schemaName;
        final int schemaVersion;
        @NonNull final List<Action> actions;
        @Nullable TypedID targetID;
        @Nullable String title;
        @Nullable String description;
        @Nullable JSONObject metadata;

        TestData(
                @NonNull String schemaName,
                int schemaVersion,
                @NonNull List<Action> actions,
                @Nullable TypedID targetID,
                @Nullable String title,
                @Nullable String description,
                @Nullable JSONObject metadata)
        {
            this.schemaName = schemaName;
            this.schemaVersion = schemaVersion;
            this.actions = actions;
            this.targetID = targetID;
            this.title = title;
            this.description = description;
            this.metadata = metadata;
        }
    }

    private static TestCase<TestData> createNormalTestCase(
            @NonNull String errorMessage,
            @NonNull TestData inputAndExpected)
    {
        return new TestCase<>(errorMessage, inputAndExpected,
                inputAndExpected);
    }

    private static List<TestCase> createNormalTestDataSet()
        throws JSONException
    {
        List<Action> actions = new ArrayList<>();
        actions.add(new SetColor(128, 0, 255));
        JSONObject json = new JSONObject();
        json.put("key", "value");

        List<TestCase> retval = new ArrayList<>();
        Collections.addAll(retval,
                createNormalTestCase(
                    "no optional",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        null,
                        null,
                        null)),
                createNormalTestCase(
                    "one optional 1",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        null,
                        null,
                        null)),
                createNormalTestCase(
                    "one optional 2",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        "title",
                        null,
                        null)),
                createNormalTestCase(
                    "one optional 3",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        null,
                        "description",
                        null)),
                createNormalTestCase(
                    "one optional 4",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        null,
                        null,
                        json)),
                createNormalTestCase(
                    "two optional 1",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        "title",
                        null,
                        null)),
                createNormalTestCase(
                    "two optional 2",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        null,
                        "description",
                        null)),
                createNormalTestCase(
                    "two optional 3",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        null,
                        null,
                        json)),
                createNormalTestCase(
                    "two optional 4",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        "title",
                        "description",
                        null)),
                createNormalTestCase(
                    "two optional 5",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        "title",
                        null,
                        json)),
                createNormalTestCase(
                    "two optional 6",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        null,
                        "description",
                        json)),
                createNormalTestCase(
                    "three optional 1",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        "title",
                        null,
                        json)),
                createNormalTestCase(
                    "three optional 2",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        null,
                        "description",
                        json)),
                createNormalTestCase(
                    "three optional 3",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        null,
                        "title",
                        "description",
                        json)),
                createNormalTestCase(
                    "four optional 1",
                    new TestData(
                        "schema name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        "title",
                        "description",
                        json))
                           );

        return retval;
    }

    @Test
    public void normalTests() throws Exception {
        for (TestCase testCase : createNormalTestDataSet()) {
            TestData data = testCase.input;
            TriggeredCommandForm.Builder builder =
                    TriggeredCommandForm.Builder.builder(
                        data.schemaName,
                        data.schemaVersion,
                        data.actions);
            if (data.targetID != null) {
                Assert.assertSame(testCase.errorMessge,
                    builder, builder.setTargetID(data.targetID));
            }
            if (data.title != null) {
                Assert.assertSame(testCase.errorMessge,
                        builder, builder.setTitle(data.title));
            }
            if (data.description != null) {
                Assert.assertSame(testCase.errorMessge,
                        builder, builder.setDescription(data.description));
            }
            if (data.metadata != null) {
                Assert.assertSame(testCase.errorMessge,
                        builder, builder.setMetadata(data.metadata));
            }

            TriggeredCommandForm form = builder.build();
            Assert.assertNotNull(testCase.errorMessge, form);

            Assert.assertEquals(testCase.errorMessge,
                    data.schemaName, form.getSchemaName());
            Assert.assertEquals(testCase.errorMessge,
                    data.schemaVersion, form.getSchemaVersion());
            Assert.assertEquals(testCase.errorMessge,
                    data.actions, form.getActions());
            Assert.assertEquals(testCase.errorMessge,
                    data.targetID, form.getTargetID());
            Assert.assertEquals(testCase.errorMessge,
                    data.title, form.getTitle());
            Assert.assertEquals(testCase.errorMessge,
                    data.description, form.getDescription());
            Assert.assertEquals(testCase.errorMessge,
                    data.metadata, form.getMetadata());
        }
    }
}
