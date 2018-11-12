package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class UpdateClanMembersRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/Actions/manageClanMembers.php";

    private Map<String, String> params;

    public UpdateClanMembersRequest(String userID, String clanID, String clanName, String authToken, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("clanID", clanID);
        params.put("clanName", clanName);
        params.put("authToken", authToken);

    }

    public UpdateClanMembersRequest(String userID, String clanID, String clanName, String authToken, String method, String userTobeApprovedID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("clanID", clanID);
        params.put("clanName", clanName);
        params.put("authToken", authToken);
        params.put("method", method);
        params.put("userTobeApprovedID", userTobeApprovedID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
