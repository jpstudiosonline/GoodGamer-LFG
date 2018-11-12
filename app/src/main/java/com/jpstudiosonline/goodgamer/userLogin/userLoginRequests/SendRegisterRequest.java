package com.jpstudiosonline.goodgamer.userLogin.userLoginRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 10/30/2016.
 */

//Sends request to register new user with the server
public class SendRegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/Users/Registration/UserRegistration.php";

    private Map<String, String> params;

    //Get the user input and send it to the server in params using POST
    public SendRegisterRequest(String username, String password, String email, Response.Listener<String> listener){

        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("email", email);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}