package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 10/1/2017.
 */

public class GetChatMessagesRequest extends StringRequest {


    private static final String REGISTER_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Chat/getUserMessage.php";

    private Map<String, String> params;

    //Get the user input and send it to the server in params using POST
    public GetChatMessagesRequest(String messageFrom, String messageTo, String token, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("messageFrom", messageFrom);
        params.put("messageTo", messageTo);
        params.put("token", token);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
