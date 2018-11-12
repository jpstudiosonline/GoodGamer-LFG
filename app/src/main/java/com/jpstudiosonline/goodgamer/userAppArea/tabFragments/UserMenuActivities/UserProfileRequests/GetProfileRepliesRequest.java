package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetProfileRepliesRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/Users/UserProfile/viewUserReplies.php";

    private Map<String, String> params;

    public GetProfileRepliesRequest(String requesterID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requesterID", requesterID);

    }

    public GetProfileRepliesRequest(String requesterID, String profileID, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requesterID", requesterID);
        params.put("profileID", profileID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
