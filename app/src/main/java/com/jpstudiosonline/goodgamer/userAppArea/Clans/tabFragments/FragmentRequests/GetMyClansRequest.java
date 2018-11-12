package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetMyClansRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/myClans.php";

    private Map<String, String> params;

    public GetMyClansRequest(String userName, String parentUserID, String authToken, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userName", userName);
        params.put("parentUserID", parentUserID);
        params.put("authToken", authToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
