package com.kii.thingif;

import android.app.Activity;
import android.net.Uri;

import com.kii.thingif.command.Action;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.EndNode;
import com.kii.thingif.gateway.Gateway;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.gateway.GatewayAddress;
import com.kii.thingif.gateway.GatewayInformation;
import com.kii.thingif.gateway.PendingEndNode;
import com.kii.thingif.gateway.TargetEndnodeThing;
import com.kii.thingif.gateway.TargetGatewayThing;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Temporal Class just for checking how to of the new APIs.
public class UsageReview extends Activity {

    public void usage() {
        // Gateway APIの作成。
        KiiApp app = new KiiApp("appID", "appKey", Site.JP);
        Uri gatewayUri = Uri.parse("");
        GatewayAPIBuilder b = GatewayAPIBuilder.newBuilder(this, app, gatewayUri);
        // Review: _newBuilder() が少し気になる。
        GatewayAPI localAPI = b.build();

        // ThingIF APIの作成
        ThingIFAPIBuilder builder = ThingIFAPIBuilder.
                newBuilder(this, app, new Owner(new TypedID(TypedID.Types.USER,"ownerID"), "accessToken"));
        ThingIFAPI thingIFAPIapi = builder.build();
        try {

            // Local Login
            localAPI.login("username", "password");

            // Local Onboard Gateway
            Gateway gateway = localAPI.onboardGateway();

            // Local Get info.
            GatewayInformation info  = localAPI.getGatewayInformation();

            // Cloud Onboard Gateway
            thingIFAPIapi.onboard(gateway.getThingID(), "password", null, null);

            // Local List Pending Nodes.
            List<PendingEndNode> nodes = localAPI.listPendingEndNodes();
            PendingEndNode node = nodes.get(0);

            // Cloud Onboard EndNode
            EndNode endnode = thingIFAPIapi.onboardEndnodeWithGateway(node, "dummy password");

            // Local Notify EndNode onboarding completion.
            localAPI.notifyOnboardingCompletion(endnode);

            // Cloud send command to End Node.
            // Copy API instance with EndNode target.
            ThingIFAPI endNodeAPI = thingIFAPIapi.copyWithTarget(endnode, "APIForEndNode");
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(new Action() {
                @Override
                public String getActionName() {
                    return "DUMMYACTION";
                }
            });
            endNodeAPI.postNewCommand("scheme", 1, actions);

        } catch (ThingIFException e) {
            e.printStackTrace();
        }
    }

}
