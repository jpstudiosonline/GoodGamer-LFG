package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jahnplay on 11/1/2016.
 */

public class SubmitLfgVote extends StringRequest {

    private static final String SUBMIT_REQUEST_URL = "https://jpstudiosonline.com/goodgamer/UserActivities/Lfgrequests/submitLfgRequestVote.php";

    private Map<String, String> params;

    public SubmitLfgVote(String voteMethod, String lfgRequestID, String voterID, Response.Listener<String> listener){

        super(Method.POST, SUBMIT_REQUEST_URL, listener, null);

        //Put params in URL
        params = new HashMap<>();
        params.put("voteMethodType", voteMethod);
        params.put("lfgRequestID", lfgRequestID);
        params.put("userVoterID", voterID);

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
