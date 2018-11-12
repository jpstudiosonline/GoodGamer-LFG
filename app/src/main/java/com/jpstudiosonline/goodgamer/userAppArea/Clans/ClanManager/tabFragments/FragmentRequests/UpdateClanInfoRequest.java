package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class UpdateClanInfoRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/Actions/updateClanInfo.php";

    private Map<String, String> params;

    public UpdateClanInfoRequest(String userID, String clanName, String clanID, String messageOfDay, String clanDescription, String LOGO, String clanOwner, String authToken, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("clanName", clanName);
        params.put("clanID", clanID);
        params.put("messageOfDay", messageOfDay);
        params.put("clanDescription", clanDescription);
        params.put("LOGO", LOGO);
        params.put("clanOwner", clanOwner);
        params.put("authToken", authToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
