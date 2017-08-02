package com.kii.thing_if.command.gateway;

import android.support.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.command.Action;

public class ListPendingNodesAction implements Action {
    @SerializedName("ListPendings")
    private JsonObject actionValue;

    public ListPendingNodesAction(KiiApp app) {
        this.actionValue = new JsonObject();
        this.actionValue.addProperty("serverLocation", app.getSiteName());
        this.actionValue.addProperty("appID", app.getAppID());
    }
}
