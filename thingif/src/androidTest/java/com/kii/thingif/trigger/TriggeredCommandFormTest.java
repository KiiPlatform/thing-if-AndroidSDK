package com.kii.thingif.trigger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import com.kii.thingif.SmallTestBase;
import com.kii.thingif.TypedID;
import com.kii.thingif.command.Action;
import com.kii.thingif.testschemas.SetColor;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TriggeredCommandFormTest extends SmallTestBase {

    private static final class TestCase {
        @NonNull public final TestData testData;
        @NonNull public final String title;

        TestCase(String title, TestData testData) {
            this.title = title;
            this.testData = testData;
        }
    }

    private static final class TestData {
        @NonNull public final String schemaName;
        @NonNull public final int schemaVersion;
        @NonNull public final List<Action> actions;
        @Nullable public TypedID targetID;
        @Nullable public String title;
        @Nullable public String description;
        @Nullable public JSONObject metadata;

        TestData(
                String schemaName,
                int schemaVersion,
                List<Action> actions,
                TypedID targetID,
                String title,
                String description,
                JSONObject metadata)
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

    private static List<TestCase> TEST_CASES;

    static {
        List<Action> actions = new ArrayList<Action>();
        actions.add(new SetColor(128, 0, 255));

        TEST_CASES = new ArrayList<TestCase>();
        Collections.addAll(TEST_CASES,
                new TestCase(
                    "1",
                    new TestData(
                        "schama name",
                        1,
                        actions,
                        new TypedID(TypedID.Types.THING, "dummy_id"),
                        null,
                        null,
                        null)));
    }

    @Test
    public void threeArgumentBuilderTest() throws Exception {
        for (TestCase testCase : this.TEST_CASES) {
            TestData data = testCase.testData;
            TriggeredCommandForm.Builder builder =
                    TriggeredCommandForm.Builder.builder(
                        data.schemaName,
                        data.schemaVersion,
                        data.actions);
            if (data.targetID != null) {
                Assert.assertSame(
                    builder, builder.setTargetID(data.targetID));
            }
            if (data.title != null) {
                Assert.assertSame(testCase.title,
                        builder, builder.setTitle(data.title));
            }
            if (data.description != null) {
                Assert.assertSame(testCase.title,
                        builder, builder.setDescription(data.description));
            }
            if (data.metadata != null) {
                Assert.assertSame(testCase.title,
                        builder, builder.setMetadata(data.metadata));
            }

            TriggeredCommandForm form = builder.build();
            Assert.assertNotNull(testCase.title, form);

            Assert.assertEquals(testCase.title,
                    data.schemaName, form.getSchemaName());
            Assert.assertEquals(testCase.title,
                    data.schemaVersion, form.getSchemaVersion());
            Assert.assertEquals(testCase.title,
                    data.actions, form.getActions());
            Assert.assertEquals(testCase.title,
                    data.targetID, form.getTargetID());
            Assert.assertEquals(testCase.title,
                    data.title, form.getTitle());
            Assert.assertEquals(testCase.title,
                    data.description, form.getDescription());
            Assert.assertEquals(testCase.title,
                    data.metadata, form.getMetadata());
        }
    }
}
