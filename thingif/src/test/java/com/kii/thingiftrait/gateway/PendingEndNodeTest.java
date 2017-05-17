package com.kii.thingiftrait.gateway;

import android.os.Parcel;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class PendingEndNodeTest {
    @Test
    public void parcelableTest() throws Exception{
        PendingEndNode endNode1 = new PendingEndNode("vendor-id1");
        Assert.assertEquals("vendor-id1", endNode1.getVendorThingID());
        Assert.assertNull(endNode1.getThingType());
        Assert.assertNull(endNode1.getFirmwareVersion());
        Assert.assertNull(endNode1.getThingProperties());

        PendingEndNode endNode2 = new PendingEndNode("vendor-id2", "type1");
        Assert.assertEquals("vendor-id2", endNode2.getVendorThingID());
        Assert.assertEquals("type1", endNode2.getThingType());
        Assert.assertNull(endNode2.getFirmwareVersion());
        Assert.assertNull(endNode2.getThingProperties());

        PendingEndNode endNode3 = new PendingEndNode(
                "vendor-id3",
                "type3",
                "v1",
                new JSONObject().put("k", "v"));
        Assert.assertEquals("vendor-id3", endNode3.getVendorThingID());
        Assert.assertEquals("type3", endNode3.getThingType());
        Assert.assertEquals("v1", endNode3.getFirmwareVersion());
        Assert.assertNotNull(endNode3.getThingProperties());
        Assert.assertEquals(
                new JSONObject().put("k", "v").toString(),
                endNode3.getThingProperties().toString());

        PendingEndNode[] endNodes = {endNode1, endNode2, endNode3};

        Gson gson = new Gson();
        for (int i=0; i<endNodes.length; i++) {
            PendingEndNode endNode = endNodes[i];
            Parcel parcel = Parcel.obtain();
            endNode.writeToParcel(parcel, 0);
            parcel.setDataPosition(0);
            PendingEndNode deserializedEndNode = PendingEndNode.CREATOR.createFromParcel(parcel);
            Assert.assertEquals(
                    "failed on ["+i+"]",
                    gson.toJson(endNode),
                    gson.toJson(deserializedEndNode));
        }
    }

    @Test
    public void constructWithJSONObjectTest() throws Exception {
        JSONObject json1 = new JSONObject().put("vendorThingID", "vendor-id1");
        PendingEndNode endNode1 = new PendingEndNode(json1);
        Assert.assertEquals("vendor-id1", endNode1.getVendorThingID());
        Assert.assertNull(endNode1.getThingType());
        Assert.assertNull(endNode1.getFirmwareVersion());
        Assert.assertNull(endNode1.getThingProperties());

        JSONObject json2 = new JSONObject()
                .put("vendorThingID", "vendor-id2")
                .put("thingProperties", new JSONObject()
                        .put("thingType", "type1"));
        PendingEndNode endNode2 = new PendingEndNode(json2);
        Assert.assertEquals("vendor-id2", endNode2.getVendorThingID());
        Assert.assertEquals("type1", endNode2.getThingType());
        Assert.assertNull(endNode2.getFirmwareVersion());
        Assert.assertNotNull(endNode2.getThingProperties());

        JSONObject json3 = new JSONObject()
                .put("vendorThingID", "vendor-id3")
                .put("thingProperties", new JSONObject()
                        .put("thingType", "type3")
                        .put("firmwareVersion", "v1")
                        .put("k", "v"));
        PendingEndNode endNode3 = new PendingEndNode(json3);

        Assert.assertEquals("vendor-id3", endNode3.getVendorThingID());
        Assert.assertEquals("type3", endNode3.getThingType());
        Assert.assertEquals("v1", endNode3.getFirmwareVersion());
        Assert.assertNotNull(endNode3.getThingProperties());
        Assert.assertEquals(
                new JSONObject()
                        .put("thingType", "type3")
                        .put("firmwareVersion", "v1")
                        .put("k", "v").toString(),
                endNode3.getThingProperties().toString());
    }
}
