package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitDeleteLfgRequest extends StringRequest {

    private static final String REPLY_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/editLfgRequest.php";

    private Map<String, String> params;

    //Use this to edit the LFG request description
    public SubmitDeleteLfgRequest(String methodType, String requestUserID, String requestID, Response.Listener<String> listener){

        super(Method.POST, REPLY_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("methodType", methodType);
        params.put("requestUserID", requestUserID);
        params.put("requestID", requestID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
