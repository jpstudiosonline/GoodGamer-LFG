package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitLfgRequestReply extends StringRequest {

    private static final String REPLY_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/submitLfgRequestReply.php";

    private Map<String, String> params;

    public SubmitLfgRequestReply(String requestID, String replyUserID, String replyComment, Response.Listener<String> listener){

        super(Method.POST, REPLY_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("requestID", requestID);
        params.put("replyUserID", replyUserID);
        params.put("replyComment", replyComment);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
