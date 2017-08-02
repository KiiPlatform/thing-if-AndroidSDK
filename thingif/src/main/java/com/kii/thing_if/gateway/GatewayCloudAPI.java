package com.kii.thing_if.gateway;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
import com.kii.thing_if.command.CommandState;
import com.kii.thing_if.command.gateway.DisableNodeAction;
import com.kii.thing_if.command.gateway.EnableNodeAction;
import com.kii.thing_if.command.gateway.ListOnboardedNodesAction;
import com.kii.thing_if.command.gateway.ListPendingNodesAction;
import com.kii.thing_if.command.gateway.OnboardNodeAction;
import com.kii.thing_if.exception.ThingIFException;

import java.util.ArrayList;
import java.util.List;

public class GatewayCloudAPI {
    @NonNull
    private ThingIFAPI thingIFAPI;

    /**
     * Initialize with single alias for gateway command. It generates a ThingIFAPI instance for you.
     * @param context Context instance.
     * @param app KiiApp instance.
     * @param owner Owner of gateway.
     * @param commandAlias Alias for gateway command. Please note that it only apply one alias for the
     *                     gateway commands. If your app requires multiple alias for gateway commands,
     *                     please create ThingIFAPI separately by registering alias for each gateway
     *                     command actions, then call {@link #GatewayCloudAPI(ThingIFAPI)}.
     */
     GatewayCloudAPI(Context context, KiiApp app, Owner owner, String commandAlias) {
        this.thingIFAPI = ThingIFAPI.Builder
                .newBuilder(context, app, owner)
                .registerAction(commandAlias, "listPendings", ListPendingNodesAction.class)
                .registerAction(commandAlias, "onboardEndnode", OnboardNodeAction.class)
                .registerAction(commandAlias, "listEndnodes", ListOnboardedNodesAction.class)
                .registerAction(commandAlias, "enableEndnode", EnableNodeAction.class)
                .registerAction(commandAlias, "disableEndnode", DisableNodeAction.class)
                .build();
    }

    /**
     * Initialize with {@link ThingIFAPI} instance.
     * Please note that you need to register the following actions to ThingIFAPI instance by yourself:
     * {@link ListPendingNodesAction},{@link ListOnboardedNodesAction}, {@link OnboardNodeAction},
     * {@link EnableNodeAction}, and {@link DisableNodeAction}. These actions are used for gateway commands.
     * @param api ThingIFAPI instance.
     */
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

    /**
     * Onboard gateway with vendorThingID and password.
     * @param vendorThingID
     * @param password
     * @throws ThingIFException
     */
    public void onboardWithVendorThingID(String vendorThingID, String password) throws ThingIFException{
        this.getThingIFAPI().onboardWithVendorThingID(vendorThingID, password);
    }

    /**
     * Onboard gateway with thingID and password.
     * @param thingID
     * @param password
     * @throws ThingIFException
     */
    public void onboardWithThingID(String thingID, String password) throws ThingIFException {
        this.getThingIFAPI().onboardWithThingID(thingID, password);
    }
     /**
     * Retrieve pending endnodes from action results of listPendingNodes command.
     * It requires the state of cmd is {@link CommandState#DONE}, otherwise, null is returned.
     * @param cmd Command instance of listPendingNodes
     * @return List of PendingEndNode instance. If state of cmd is not DONE, then null is returned.
     */
    @Nullable
    public ArrayList<PendingEndNode> getListPendingNodesResult(Command cmd) {
        if (cmd.getCommandState() == CommandState.DONE) {
            List<ActionResult> results = cmd.getActionResult(getAliasOfAction(ListPendingNodesAction.class), "listPendings");
            ArrayList<PendingEndNode> pendingNodes = new ArrayList<>();
            if(results.size() == 1) {
                ActionResult result = results.get(0);
                Gson gson = new Gson();
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
            }
            return pendingNodes;
        }else {
            return null;
        }
    }

    public ArrayList<EndNode> getListOnboardedNodesResult(Command cmd) {
        //TODO: implement me. Similar to getListPendingNodesResult
        return new ArrayList<>();
    }

    /**
     * Post command to gateway to get list of pending end-nodes.
     * It requires gateway to update the action result with pending nodes, so the returned Command
     * instance may not has the result. You can either polling to call {@link ThingIFAPI#getCommand(String)}
     * or leverage push notification for update of result.
     * @return Command instance.
     * @throws ThingIFException
     */
    public Command postListOnboardedNodesCommand() throws ThingIFException {
        return postGatewayCommand(
                ListOnboardedNodesAction.class,
                new ListOnboardedNodesAction(this.thingIFAPI.getApp()));
    }

    /**
     * Post command to gateway to get list of onboarded end-nodes.
     * It requires gateway to update the action result, so the returned Command
     * instance may not has the result. You can either polling to call {@link ThingIFAPI#getCommand(String)}
     * or leverage push notification for update of result.
     * @return Command instance.
     * @throws ThingIFException
     */
    public Command postOnboardNodeCommand(String nodeVID, String nodeID) throws ThingIFException {
        return postGatewayCommand(OnboardNodeAction.class, new OnboardNodeAction(
                this.thingIFAPI.getApp(),
                nodeVID,
                nodeID));
    }

    /**
     * Post command to gateway to enable a specified end-node.
     * It requires gateway to update the action result, so the returned Command
     * instance may not has the result. You can either polling to call {@link ThingIFAPI#getCommand(String)}
     * or leverage push notification for update of result.
     * @return Command instance.
     * @throws ThingIFException
     */
    public Command postEnableCommand(String nodeID) throws ThingIFException {
        return postGatewayCommand(
                EnableNodeAction.class,
                new EnableNodeAction(this.thingIFAPI.getApp(), nodeID));
    }

    /**
     * Post command to gateway to disable a specified end-node
     * It requires gateway to update the action result, so the returned Command
     * instance may not has the result. You can either polling to call {@link ThingIFAPI#getCommand(String)}
     * or leverage push notification for update of result.
     * @return Command instance.
     * @throws ThingIFException
     */
    public Command postDisableCommand(String nodeID) throws ThingIFException {
        return postGatewayCommand(
                DisableNodeAction.class,
                new DisableNodeAction(this.thingIFAPI.getApp(), nodeID));
    }

}
