package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 10/1/2017.
 */

public class GetUserMessagesRequest extends StringRequest {


    private static final String REGISTER_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Clans/Actions/ClanMessages/getClanMessages.php";

    private Map<String, String> params;

    //Get the user input and send it to the server in params using POST
    public GetUserMessagesRequest(String readersUserID, String messageFromClanID, String authToken, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("readersUserID", readersUserID);
        params.put("messageFromClanID", messageFromClanID);
        params.put("authToken", authToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
