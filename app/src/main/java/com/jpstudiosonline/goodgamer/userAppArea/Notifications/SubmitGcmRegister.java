package com.jpstudiosonline.goodgamer.userAppArea.Notifications;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitGcmRegister extends StringRequest {

    private static final String SUBMIT_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Notifications/RegisterGcmUser.php";

    private Map<String, String> params;

    public SubmitGcmRegister(String userID, String gcmToken, Response.Listener<String> listener){

        super(Method.POST, SUBMIT_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("userID", userID);
        params.put("token", gcmToken);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
