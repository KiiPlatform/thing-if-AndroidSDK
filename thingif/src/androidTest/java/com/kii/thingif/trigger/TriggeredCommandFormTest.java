package com.kii.thingif.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.command.Command;
import com.kii.thingif.testschemas.SetBrightness;
import com.kii.thingif.testschemas.SetColor;

import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

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
        @Nullable final String schemaName;
        final int schemaVersion;
        @Nullable final List<Action> actions;
        @Nullable TypedID targetID;
        @Nullable String title;
        @Nullable String description;
        @Nullable JSONObject metadata;

        TestData(
                @Nullable String schemaName,
                int schemaVersion,
                @Nullable List<Action> actions,
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

        TestData(
                @Nullable String schemaName,
                int schemaVersion,
                @Nullable List<Action> actions)
        {
            this(schemaName, schemaVersion, actions, null, null, null, null);
        }
    }

    @NonNull
    private static TestCase<TestData> createNormalTestCase(
            @NonNull String errorMessage,
            @NonNull TestData inputAndExpected)
    {
        return new TestCase<>(errorMessage, inputAndExpected,
                inputAndExpected);
    }

    @NonNull
    private static List<TestCase<TestData>> createNormalTestDataSet()
        throws JSONException
    {
        List<Action> actions = new ArrayList<>();
        actions.add(new SetColor(128, 0, 255));
        JSONObject json = new JSONObject();
        json.put("key", "value");

        List<TestCase<TestData>> retval = new ArrayList<>();
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
    public void constructWithThreeArgumentsNormalTest() throws Exception {
        for (TestCase<TestData> testCase : createNormalTestDataSet()) {
            TestData data = testCase.input;
            TestData expected = testCase.expected;
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
                    expected.schemaName, form.getSchemaName());
            Assert.assertEquals(testCase.errorMessge,
                    expected.schemaVersion, form.getSchemaVersion());
            Assert.assertEquals(testCase.errorMessge,
                    expected.actions, form.getActions());
            Assert.assertEquals(testCase.errorMessge,
                    expected.targetID, form.getTargetID());
            Assert.assertEquals(testCase.errorMessge,
                    expected.title, form.getTitle());
            Assert.assertEquals(testCase.errorMessge,
                    expected.description, form.getDescription());
            assertJSONObject(testCase.errorMessge,
                    expected.metadata, form.getMetadata());
        }
    }

    @NonNull
    private static Command createCommand(
            @NonNull TestData data,
            @NonNull TypedID defaultTarget) {
        Command retval = mock(Command.class);
        when(retval.getSchemaName()).thenReturn(data.schemaName);
        when(retval.getSchemaVersion()).thenReturn(data.schemaVersion);
        when(retval.getActions()).thenReturn(data.actions);
        when(retval.getTargetID()).thenReturn(
            data.targetID != null ? data.targetID : defaultTarget);
        when(retval.getTitle()).thenReturn(data.title);
        when(retval.getDescription()).thenReturn(data.description);
        when(retval.getMetadata()).thenReturn(data.metadata);
        return retval;
    }

    @Test
    public void constructWithCommandNormalTest() throws Exception {
        TypedID defaultTarget =
                new TypedID(TypedID.Types.THING, "default_target");
        for (TestCase<TestData> testCase : createNormalTestDataSet()) {
            TestData data = testCase.input;
            TestData expected = testCase.expected;
            TriggeredCommandForm.Builder builder =
                    TriggeredCommandForm.Builder.builder(
                        createCommand(data, defaultTarget));

            TypedID expectedTargetID = data.targetID != null ?
                    expected.targetID : defaultTarget;

            TriggeredCommandForm form = builder.build();
            Assert.assertNotNull(testCase.errorMessge, form);

            Assert.assertEquals(testCase.errorMessge,
                    expected.schemaName, form.getSchemaName());
            Assert.assertEquals(testCase.errorMessge,
                    expected.schemaVersion, form.getSchemaVersion());
            Assert.assertEquals(testCase.errorMessge,
                    expected.actions, form.getActions());
            Assert.assertEquals(testCase.errorMessge,
                    expectedTargetID, form.getTargetID());
            Assert.assertEquals(testCase.errorMessge,
                    expected.title, form.getTitle());
            Assert.assertEquals(testCase.errorMessge,
                    expected.description, form.getDescription());
            assertJSONObject(testCase.errorMessge,
                    expected.metadata, form.getMetadata());
        }
    }

    @NonNull
    private List<TestCase<String>> createConstructingWithExceptionTestCases() {
        List<Action> actions = new ArrayList<>();
        actions.add(new SetColor(128, 0, 255));

        List<TestCase<String>> retval = new ArrayList<>();
        Collections.addAll(retval,
                new TestCase<>(
                    "1",
                    new TestData(null, 1, actions),
                    "schemaName is null or empty."),
                new TestCase<>(
                    "2",
                    new TestData("", 1, actions),
                    "schemaName is null or empty."),
                new TestCase<>(
                    "3",
                    new TestData("schema name", 1, null),
                    "actions is null or empty."),
                new TestCase<>(
                    "4",
                    new TestData("schema name", 1, new ArrayList<Action>()),
                    "actions is null or empty."));
        return retval;
    }

    @Test
    public void constructIllegalArgumentExceptionTest() throws Exception {
        for (TestCase<String> testCase :
                     createConstructingWithExceptionTestCases()) {
            TestData input = testCase.input;
            String expected = testCase.expected;
            IllegalArgumentException actual = null;
            try {
                TriggeredCommandForm.Builder.builder(
                    input.schemaName,
                    input.schemaVersion,
                    input.actions);
            } catch (IllegalArgumentException e) {
                actual = e;
            }
            Assert.assertNotNull(testCase.errorMessge, actual);
            Assert.assertEquals(testCase.errorMessge,
                    expected, actual.getMessage());
        }
    }

    @NonNull
    private List<TestCase<String>> createSetterWithExceptionTestCases() {
        List<Action> actions = new ArrayList<>();
        actions.add(new SetColor(128, 0, 255));

        List<TestCase<String>> retval = new ArrayList<>();
        Collections.addAll(retval,
                new TestCase<>(
                    "1",
                    new TestData("schema name", 1, actions,
                            new TypedID(TypedID.Types.USER, "dummy-id"),
                            null, null, null),
                    "targetID type must be Types.THING"),
                new TestCase<>(
                    "2",
                    new TestData("schema name", 1, actions,
                            null, RandomStringUtils.random(51), null, null),
                    "title is more than 50 charactors."),
                new TestCase<>(
                    "3",
                    new TestData("schema name", 1, actions,
                            null, null, RandomStringUtils.random(201), null),
                    "description is more than 200 charactors."));
        return retval;
    }

    @Test
    public void setterIllegalArgumentExceptionTest() throws Exception {
        for (TestCase<String> testCase :
                     createSetterWithExceptionTestCases()) {
            TestData input = testCase.input;
            String expected = testCase.expected;
            IllegalArgumentException actual = null;

            TriggeredCommandForm.Builder builder =
                    TriggeredCommandForm.Builder.builder(
                        input.schemaName,
                        input.schemaVersion,
                        input.actions);
            try {
                if (input.targetID != null) {
                    builder.setTargetID(input.targetID);
                } else if (input.title != null) {
                    builder.setTitle(input.title);
                } else if (input.description != null) {
                    builder.setDescription(input.description);
                }
            } catch (IllegalArgumentException e) {
                actual = e;
            }
            Assert.assertNotNull(testCase.errorMessge, actual);
            Assert.assertEquals(testCase.errorMessge,
                    expected, actual.getMessage());
        }
    }

    @Test
    public void valueOverwriteTest() throws Exception {
        String schemaName1 = "schema name 1";
        int schemaVersion1 = 1;
        List<Action> actions1 = new ArrayList<>();
        actions1.add(new SetColor(128, 0, 255));

        TriggeredCommandForm.Builder builder =
                TriggeredCommandForm.Builder.builder(
                    schemaName1, schemaVersion1, actions1);
        Assert.assertEquals(schemaName1, builder.getSchemaName());
        Assert.assertEquals(schemaVersion1, builder.getSchemaVersion());
        Assert.assertEquals(actions1, builder.getActions());
        Assert.assertNull(builder.getTargetID());
        Assert.assertNull(builder.getTitle());
        Assert.assertNull(builder.getDescription());
        Assert.assertNull(builder.getMetadata());

        TriggeredCommandForm form = builder.build();

        Assert.assertEquals(schemaName1, form.getSchemaName());
        Assert.assertEquals(schemaVersion1, form.getSchemaVersion());
        Assert.assertEquals(actions1, form.getActions());
        Assert.assertNull(form.getTargetID());
        Assert.assertNull(form.getTitle());
        Assert.assertNull(form.getDescription());
        Assert.assertNull(form.getMetadata());

        TypedID targetID1 = new TypedID(TypedID.Types.THING, "dummy_id1");
        String title1 = "title 1";
        String description1 = "description 1";
        JSONObject json1 = new JSONObject();
        json1.put("key1", "value1");

        builder.setTargetID(targetID1).setTitle(title1).
                setDescription(description1).setMetadata(json1);

        Assert.assertEquals(schemaName1, builder.getSchemaName());
        Assert.assertEquals(schemaVersion1, builder.getSchemaVersion());
        Assert.assertEquals(actions1, builder.getActions());
        Assert.assertEquals(targetID1, builder.getTargetID());
        Assert.assertEquals(title1, builder.getTitle());
        Assert.assertEquals(description1, builder.getDescription());
        assertJSONObject(json1, builder.getMetadata());

        form = builder.build();

        Assert.assertEquals(schemaName1, form.getSchemaName());
        Assert.assertEquals(schemaVersion1, form.getSchemaVersion());
        Assert.assertEquals(actions1, form.getActions());
        Assert.assertEquals(targetID1, form.getTargetID());
        Assert.assertEquals(title1, form.getTitle());
        Assert.assertEquals(description1, form.getDescription());
        assertJSONObject(json1, form.getMetadata());

        String schemaName2 = "schema name 2";
        int schemaVersion2 = 2;
        List<Action> actions2 = new ArrayList<>();
        actions2.add(new SetBrightness(128));
        TypedID targetID2 = new TypedID(TypedID.Types.THING, "dummy_id2");
        String title2 = "title 2";
        String description2 = "description 2";
        JSONObject json2 = new JSONObject();
        json2.put("key2", "value2");

        builder.setSchemaName(schemaName2).setSchemaVersion(schemaVersion2).
                setActions(actions2).setTargetID(targetID2).setTitle(title2).
                setDescription(description2).setMetadata(json2);


        Assert.assertEquals(schemaName2, builder.getSchemaName());
        Assert.assertEquals(schemaVersion2, builder.getSchemaVersion());
        Assert.assertEquals(actions2, builder.getActions());
        Assert.assertEquals(targetID2, builder.getTargetID());
        Assert.assertEquals(title2, builder.getTitle());
        Assert.assertEquals(description2, builder.getDescription());
        assertJSONObject(json2, builder.getMetadata());

        form = builder.build();

        Assert.assertEquals(schemaName2, form.getSchemaName());
        Assert.assertEquals(schemaVersion2, form.getSchemaVersion());
        Assert.assertEquals(actions2, form.getActions());
        Assert.assertEquals(targetID2, form.getTargetID());
        Assert.assertEquals(title2, form.getTitle());
        Assert.assertEquals(description2, form.getDescription());
        assertJSONObject(json2, form.getMetadata());
    }

}
