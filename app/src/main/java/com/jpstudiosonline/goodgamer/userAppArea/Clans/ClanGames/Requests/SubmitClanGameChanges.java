package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames.Requests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitClanGameChanges extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/Actions/modifyClanGames.php";

    private Map<String, String> params;

    public SubmitClanGameChanges(String userID, String clanName, String authToken, String modify, String gameName, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("clanName", clanName);
        params.put("authToken", authToken);
        params.put("modify", modify);
        params.put("gameName", gameName);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
