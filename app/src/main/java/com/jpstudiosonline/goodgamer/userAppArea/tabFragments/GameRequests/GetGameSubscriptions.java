package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetGameSubscriptions extends StringRequest {


    //Used to see if user is registered for alerts for the game
    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Notifications/RegisterNotifications.php";

    private Map<String, String> params;

    public GetGameSubscriptions(String userID, String gameID, String method, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("gameID", gameID);
        params.put("method", method);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
