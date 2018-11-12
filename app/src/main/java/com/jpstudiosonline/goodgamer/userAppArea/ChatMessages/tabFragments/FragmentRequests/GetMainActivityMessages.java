package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 10/1/2017.
 */

public class GetMainActivityMessages extends StringRequest {


    private static final String REGISTER_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Chat/messagesOverView.php";

    private Map<String, String> params;

    //Get the user input and send it to the server in params using POST
    public GetMainActivityMessages(String userID, String userName, String authToken, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("userName", userName);
        params.put("authToken", authToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
