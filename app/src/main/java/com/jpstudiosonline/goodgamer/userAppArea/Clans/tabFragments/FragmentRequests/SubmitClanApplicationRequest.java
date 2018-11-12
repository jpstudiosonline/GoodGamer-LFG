package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitClanApplicationRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/Actions/submitClanApplication.php";

    private Map<String, String> params;

    public SubmitClanApplicationRequest(String clanName, String parentUserID, String description, String authToken, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("clanName", clanName);
        params.put("parentUserID", parentUserID);
        params.put("description", description);
        params.put("authToken", authToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
