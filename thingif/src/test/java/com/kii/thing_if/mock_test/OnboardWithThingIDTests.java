package com.kii.thing_if.mock_test;

import com.kii.thing_if.StandaloneThing;
import com.kii.thing_if.Target;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.exception.ForbiddenException;
import com.kii.thing_if.exception.NotFoundException;
import com.kii.thing_if.mock_test.utils.EquableTarget;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Config(manifest = "src/main/AndroidManifest.xml", assetDir = "../test/assets")
@RunWith(RobolectricTestRunner.class)
public class OnboardWithThingIDTests extends ThingHTTPMockTestBase {

    @Test
    public void onboardWithThingIDByOwner() throws Exception {
        ThingIFAPI api =
                createThingIFAPIBuilder("test-onboard-with-id-success").build();

        assertFalse("This api must be not onboarded.", api.onboarded());
        Target target = api.onboardWithThingID("thing-id", "thing-password");
        assertTrue("This api must be onboarded.", api.onboarded());
        assertEquals(
            new EquableTarget(
                new StandaloneThing("thing-id", null, "accesstoken")),
            new EquableTarget(target));
    }

    @Test(expected = ForbiddenException.class)
    public void onboardWithThingIDByOwner403Error() throws Exception {
        createThingIFAPIBuilder("test-onboard-with-id-403")
                .build().onboardWithThingID("thing-id", "thing-password");
    }

    @Test(expected = NotFoundException.class)
    public void onboardWithThingIDByOwner404Error() throws Exception {
        createThingIFAPIBuilder("test-onboard-with-id-404")
                .build().onboardWithThingID("thing-id", "thing-password");
    }

}
