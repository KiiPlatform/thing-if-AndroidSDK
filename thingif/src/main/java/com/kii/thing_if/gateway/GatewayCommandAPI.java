package com.kii.thing_if.gateway;

import android.support.annotation.NonNull;

import com.kii.thing_if.ThingIFAPI;
import com.kii.thing_if.KiiApp;
import com.kii.thing_if.Owner;
import com.kii.thing_if.command.ActionResult;
import com.kii.thing_if.exception.ThingIFException;

/**
 * GatewayCommandAPI represents APIs, sending command to gateway through kii cloud.
 * It requires the gateway has already been onboarded by the owner.
 * <p>
 * To onboard gateway, please refer to {@link ThingIFAPI#onboardWithVendorThingID(String, String)} or
 * {@link ThingIFAPI#onboardWithThingID(String, String)}.
 * <p>
 * Since gateway can't parse trait format command yet. So if the gateway onboard with trait
 * configured thingType and firmwareVersion, should not use GatewayCommandAPI.
 */
public class GatewayCommandAPI {
    private @NonNull  KiiApp app;
    private @NonNull Owner owner;
    private @NonNull String gatewayID;

    /**
     * Initialize GatewayCommandAPI instance.
     * @param app app instance.
     * @param owner owner of gateway.
     * @param gatewayID thingID of gateway.
     */
    public GatewayCommandAPI(
            @NonNull KiiApp app,
            @NonNull Owner owner,
            @NonNull String gatewayID) {
        this.app = app;
        this.owner = owner;
        this.gatewayID = gatewayID;
    }

    /**
     * Send command to gateway to get list of pending nodes in gateway.
     * @return command id
     * @throws ThingIFException
     */
    public String postListPendingEndNodesCommand() throws ThingIFException {
        // TODO: implement me
        return "command-id-1";
    }

    /**
     * Send command to gateway to notify it with completion of onboarding for end node.
     * @param endNode EndNode instance.
     * @return command id
     * @throws ThingIFException
     */
    public String postOnboardCompletionCommand(@NonNull EndNode endNode) throws ThingIFException {
        // TODO: implement me
        return "command-id-2";
    }

    @NonNull
    public KiiApp getApp() {
        return app;
    }

    @NonNull
    public Owner getOwner() {
        return owner;
    }

    @NonNull
    public String getGatewayID() {
        return gatewayID;
    }

    /**
     * Get action results of list pending endnode command.
     * @param commandID command id.
     * @return ActionResult instance.
     * @throws ThingIFException
     */
    public ActionResult getListPendingEndNodesResult(String commandID) throws ThingIFException {
        return null;
    }

    /**
     * Get action results of onboard completion command.
     * @param commandID command id.
     * @return ActionResult instance.
     * @throws ThingIFException
     */
    public ActionResult getOnboardCompletionResult(String commandID) throws ThingIFException {
        return null;
    }
}
