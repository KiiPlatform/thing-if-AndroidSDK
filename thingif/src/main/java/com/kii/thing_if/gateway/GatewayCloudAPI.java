package com.kii.thing_if.gateway;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.Owner;
import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.command.Action;
import com.kii.thing_if.command.ActionResult;
import com.kii.thing_if.command.AliasAction;
import com.kii.thing_if.command.Command;
import com.kii.thing_if.command.CommandForm;
import com.kii.thing_if.command.gateway.DisableNodeAction;
import com.kii.thing_if.command.gateway.EnableNodeAction;
import com.kii.thing_if.command.gateway.ListOnboardedNodesAction;
import com.kii.thing_if.command.gateway.ListPendingNodesAction;
import com.kii.thing_if.command.gateway.OnboardNodeAction;
import com.kii.thing_if.exception.ActionExecutionException;
import com.kii.thing_if.exception.ThingIFException;

import java.util.ArrayList;
import java.util.List;


public class GatewayCloudAPI {
    @NonNull
    private ThingIFAPI thingIFAPI;
    public GatewayCloudAPI(Context context, KiiApp app, Owner owner, String commandAlias) {
        this.thingIFAPI = ThingIFAPI.Builder
                .newBuilder(context, app, owner)
                .registerAction(commandAlias, "listPendings", ListPendingNodesAction.class)
                .registerAction(commandAlias, "onboardEndnode", OnboardNodeAction.class)
                .registerAction(commandAlias, "listEndnodes", ListOnboardedNodesAction.class)
                .registerAction(commandAlias, "enableEndnode", EnableNodeAction.class)
                .registerAction(commandAlias, "disableEndnode", DisableNodeAction.class)
                .build();
    }
    public GatewayCloudAPI(ThingIFAPI api) {
        this.thingIFAPI = api;
    }

    @NonNull
    public ThingIFAPI getThingIFAPI() {
        return thingIFAPI;
    }

    private String getAliasOfAction(Class<? extends Action> action) {
        //TODO: implement me
        return "";
    }

    private Command postGatewayCommand(Class<? extends Action> actionClass, Action action) throws ThingIFException {
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(action);
        CommandForm form = CommandForm.Builder
                .newBuilder()
                .addAliasAction(new AliasAction(
                        getAliasOfAction(actionClass),
                        actions))
                .build();
        return this.thingIFAPI.postNewCommand(form);
    }

    public ArrayList<PendingEndNode> getListPendingNodesResult(String commandID) throws ThingIFException {
        Command cmd = this.thingIFAPI.getCommand(commandID);
        List<ActionResult> results = cmd.getActionResult(getAliasOfAction(ListPendingNodesAction.class), "listPendings");
        ArrayList<PendingEndNode> pendingNodes = new ArrayList<>();
        if(results.size() == 1) {
            ActionResult result = results.get(0);
            Gson gson = new Gson();
            if(result.isSucceeded()){
                if (result.getData() != null) {
                    JsonObject data = (new JsonParser()).parse(result.getData().toString()).getAsJsonObject();
                    if(data.has("endnodes")) {
                        JsonArray nodes = data.getAsJsonArray("endnodes");
                        for (JsonElement n: nodes) {
                            PendingEndNode node = gson.fromJson(n, PendingEndNode.class);
                            pendingNodes.add(node);
                        }
                    }
                }else {
                    // won't happend
                }

            }else {
                throw new ActionExecutionException(result);
            }
        }
        return pendingNodes;
    }

    public ArrayList<EndNode> getListOnboardedNodesResult(String commandID) {
        //TODO: implement me. Similar to getListPendingNodesResult
        return new ArrayList<>();
    }


    public Command postListOnboardedNodesCommand() throws ThingIFException {
        return postGatewayCommand(
                ListOnboardedNodesAction.class,
                new ListOnboardedNodesAction(this.thingIFAPI.getApp()));
    }

    public Command postOnboardNodeCommand(String nodeVID, String nodeID) throws ThingIFException {
        return postGatewayCommand(OnboardNodeAction.class, new OnboardNodeAction(
                this.thingIFAPI.getApp(),
                nodeVID,
                nodeID));
    }

    public Command postEnableCommand(String nodeID) throws ThingIFException {
        return postGatewayCommand(
                EnableNodeAction.class,
                new EnableNodeAction(this.thingIFAPI.getApp(), nodeID));
    }

    public Command postDisableCommand(String nodeID) throws ThingIFException {
        return postGatewayCommand(
                DisableNodeAction.class,
                new DisableNodeAction(this.thingIFAPI.getApp(), nodeID));
    }

}
