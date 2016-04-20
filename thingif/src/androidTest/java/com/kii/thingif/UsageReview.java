package com.kii.thingif;

import android.app.Activity;

import com.kii.thingif.command.Action;
import com.kii.thingif.exception.ThingIFException;
import com.kii.thingif.gateway.GatewayAPI;
import com.kii.thingif.gateway.GatewayAPIBuilder;
import com.kii.thingif.gateway.GatewayAddress;
import com.kii.thingif.gateway.PendingEndNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Temporal Class just for checking how to of the new APIs.
public class UsageReview extends Activity {

    public void usage() {
        // Gateway APIの作成。
        KiiApp app = new KiiApp("appID", "appKey", Site.JP);
        GatewayAddress addr = new GatewayAddress("localhost");
        GatewayAPIBuilder b = GatewayAPIBuilder.newBuilder(this, app, addr);
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
            String gatewayThingID = localAPI.onboardGateway();

            // Local Get info.
            String gatewayVID = localAPI.getGatewayInformation();
            // Review: getGatewayInformation API would be fixed to return Object.

            // Cloud Onboard Gateway
            JSONObject gatewayProps = new JSONObject();
            try {
                gatewayProps.put("layoutPosition", "GATEWAY");
            } catch (JSONException e) {
                throw new RuntimeException("Unexpected error", e);
            }
            // このPropertyを作成する手順(layoutPositionの設定)が難しいと思われる。
            thingIFAPIapi.onboard(gatewayVID, "password", null, gatewayProps);

            // Local List Pending Nodes.
            List<PendingEndNode> nodes = localAPI.listPendingEndNodes();
            PendingEndNode node = nodes.get(0);

            // Cloud Onboard EndNode
            Target endnode = thingIFAPIapi.onboardEndnodeWithGatewayVendorThingID(gatewayVID, node.getVendorThingID(), "nodepassword", null, null);
            // Review: Onboard済みだと、IllegalStateExceptionはちょっと使いにくい。copyWithTargetが使えない？
            // Review: GatewayでOnboard済みでもgatewayVendorThingIDが必要？
            // Review: このAPIはちょっとややこしい(vendorThingIDとthingIDを間違える。)のでEndNode class, Gateway class を引数にするなどしたい。

            // Local Notify EndNode onboarding completion.
            localAPI.notifyOnboardingCompletion(endnode.getTypedID().getID(), node.getVendorThingID());
            // Review: このAPIもちょっとややこしい。(引数の順番、IDの種類を間違える。)

            // Cloud send command to End Node.
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(new Action() {
                @Override
                public String getActionName() {
                    return "DUMMYACTION";
                }
            });
            thingIFAPIapi.postNewCommand("scheme",1, actions);

        } catch (ThingIFException e) {
            e.printStackTrace();
        }
    }

}
