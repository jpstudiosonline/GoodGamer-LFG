package com.jpstudiosonline.goodgamer.userLogin.userLoginRequests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SendLoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/Users/UserLogin/AuthenticateUser.php";

    private Map<String, String> params;

    public SendLoginRequest(String username, String password, Response.Listener<String> listener){

        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
