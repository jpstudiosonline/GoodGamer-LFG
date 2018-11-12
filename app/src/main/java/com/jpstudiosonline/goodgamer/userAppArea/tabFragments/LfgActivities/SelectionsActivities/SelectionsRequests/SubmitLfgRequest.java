package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitLfgRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/submitNewLfgRequest.php";

    private Map<String, String> params;

    public SubmitLfgRequest(String userID, String groupID, String titleRequest, String lfgRequestDescription, String consoleID, String gameID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requestUserID", userID);
        params.put("requestGroupID", groupID);
        params.put("requestTitle", titleRequest);
        params.put("requestDescription", lfgRequestDescription);
        params.put("requestConsoleID", consoleID);
        params.put("requestGameID", gameID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
