package com.kii.thing_if.command.gateway;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.command.Action;

public class EnableNodeAction implements Action {
    @SerializedName("enableEndnode")
    private JsonObject actionValue;
    public EnableNodeAction(KiiApp app, String nodeID) {
        this.actionValue = new JsonObject();
        this.actionValue.addProperty("serverLocation", app.getSiteName());
        this.actionValue.addProperty("appID", app.getAppID());
        this.actionValue.addProperty("thingID", nodeID);
    }
}
