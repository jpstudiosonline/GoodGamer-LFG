package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetCheckContactRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Chat/getContactInfo.php";

    private Map<String, String> params;

    public GetCheckContactRequest(String userID, String authToken, String profileID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("authToken", authToken);
        params.put("profileID", profileID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
