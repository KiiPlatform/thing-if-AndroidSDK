package com.kii.thingif.command;

import android.os.Parcel;

import com.kii.thingif.actions.AirConditionerActions;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class CommandFormTest {
    private static final String DEMO_TITLE = "DemoTitle";
    private static final String DEMO_DESCRIPTION = "DemoDESCRIPTION";

    @Test
    public void parcelableTest() throws Exception {
        List<AliasAction<? extends Action>> actions = new ArrayList<>();
        actions.add(
                new AliasAction<>(
                        "airConditionerAlias",
                        new AirConditionerActions(true, null)));

        JSONObject metadata = new JSONObject("{ field : value }");

        CommandForm src = CommandForm
                .Builder
                .newBuilder(actions)
                .setTitle(DEMO_TITLE)
                .setDescription(DEMO_DESCRIPTION)
                .setMetadata(metadata)
                .build();

        Parcel parcel1 = Parcel.obtain();
        src.writeToParcel(parcel1, 0);
        parcel1.setDataPosition(0);
        CommandForm dest1 = CommandForm.CREATOR.createFromParcel(parcel1);

        Assert.assertEquals(src.getAliasActions().size(), dest1.getAliasActions().size());
        Assert.assertEquals(
                src.getAliasActions().get(0),
                dest1.getAliasActions().get(0));
        Assert.assertEquals(src.getTitle(), dest1.getTitle());
        Assert.assertEquals(src.getDescription(), dest1.getDescription());
        Assert.assertNotNull(src.getMetadata());
        Assert.assertNotNull(dest1.getMetadata());
        Assert.assertEquals(src.getMetadata().toString(), dest1.getMetadata().toString());

        src = CommandForm
                .Builder
                .newBuilder(actions)
                .build();
        Parcel parcel2 = Parcel.obtain();
        src.writeToParcel(parcel2, 0);
        parcel2.setDataPosition(0);
        CommandForm dest2 = CommandForm.CREATOR.createFromParcel(parcel2);

        Assert.assertEquals(src.getAliasActions().size(), dest2.getAliasActions().size());
        Assert.assertEquals(src.getAliasActions().get(0),
                dest2.getAliasActions().get(0));
        Assert.assertNull(dest2.getTitle());
        Assert.assertNull(dest2.getDescription());
        Assert.assertNull(dest2.getMetadata());
    }
}
