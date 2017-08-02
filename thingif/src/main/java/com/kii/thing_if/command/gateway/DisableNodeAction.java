package com.kii.thing_if.command.gateway;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.command.Action;

public class DisableNodeAction implements Action {
    @SerializedName("disableEndnode")
    private JsonObject actionValue;
    public DisableNodeAction(KiiApp app, String nodeID) {
        this.actionValue = new JsonObject();
        this.actionValue.addProperty("serverLocation", app.getSiteName());
        this.actionValue.addProperty("appID", app.getAppID());
        this.actionValue.addProperty("thingID", nodeID);
    }
}
