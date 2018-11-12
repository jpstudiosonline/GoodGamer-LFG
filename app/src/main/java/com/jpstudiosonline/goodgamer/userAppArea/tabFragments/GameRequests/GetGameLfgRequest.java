package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetGameLfgRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Games/GetGamesListing.php";

    private Map<String, String> params;

    public GetGameLfgRequest(String requesterID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requesterID", requesterID);

    }

    public GetGameLfgRequest(String requesterID, String gameID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requesterID", requesterID);
        params.put("gameID", gameID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
