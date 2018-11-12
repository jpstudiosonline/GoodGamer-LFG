package com.jpstudiosonline.goodgamer.userLogin.userLoginRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SendHashResetRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/Users/UserLogin/ResetUserPassword.php";

    private Map<String, String> params;

    public SendHashResetRequest(String email, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("email", email);

    }

    public SendHashResetRequest(String email, String hashCode, String newPassword, Response.Listener<String> listener){

        super(Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("email", email);
        params.put("hashCode", hashCode);
        params.put("newPassword", newPassword);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
