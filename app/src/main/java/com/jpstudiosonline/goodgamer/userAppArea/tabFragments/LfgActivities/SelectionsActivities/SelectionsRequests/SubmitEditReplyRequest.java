package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitEditReplyRequest extends StringRequest {

    private static final String REPLY_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/editLfgReply.php";

    private Map<String, String> params;

    //Use this to edit the LFG request description
    public SubmitEditReplyRequest(String method, String replyID, String requestID, String userID, Response.Listener<String> listener){

        super(Method.POST, REPLY_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("method", method);
        params.put("replyID", replyID);
        params.put("requestID", requestID);
        params.put("userID", userID);

    }

    //Use this to edit the LFG request description
    public SubmitEditReplyRequest(String method, String replyID, String requestID, String userID, String newComment, Response.Listener<String> listener){

        super(Method.POST, REPLY_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("method", method);
        params.put("replyID", replyID);
        params.put("requestID", requestID);
        params.put("userID", userID);
        params.put("newComment", newComment);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
