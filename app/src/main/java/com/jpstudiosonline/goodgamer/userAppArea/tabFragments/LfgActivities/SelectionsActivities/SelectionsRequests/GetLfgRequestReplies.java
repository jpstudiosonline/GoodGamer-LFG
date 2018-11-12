package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class GetLfgRequestReplies extends StringRequest {

    private static final String REPLY_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/getLfgRequestReplies.php";

    private Map<String, String> params;

    public GetLfgRequestReplies(String requestID, Response.Listener<String> listener){

        super(Method.POST, REPLY_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("parentRequestID", requestID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
