package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.SubmitClanApplicationRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class ClansApplyFragment extends Fragment{

    ProgressDialog progressBar;
    public SharedPreferences sharedPreferences;
    public String clanName, serverSideClanID;

    public ClansApplyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            clanName = getArguments().getString("clanName");
            serverSideClanID = getArguments().getString("serverSideClanID");
        } else {

            this.getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);


        View view = inflater.inflate(R.layout.fragment_clans_apply, container, false);


        final EditText etClanApplicaitonDescription = (EditText) view.findViewById(R.id.etClanApplicaitonDescription);
        Button btClanSubmitApplication = (Button) view.findViewById(R.id.btClanSubmitApplication);
        Button btClearInfo = (Button) view.findViewById(R.id.btClearInfo);

        btClanSubmitApplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etClanApplicaitonDescription.getText().toString().length() >= 1){

                    submitClanApplication(etClanApplicaitonDescription.getText().toString());

                } else {

                    Toast.makeText(getContext(), "Description too short", Toast.LENGTH_LONG).show();
                }

            }
        });


        btClearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etClanApplicaitonDescription.setText("");
            }
        });

        return view;
    }

    public void submitClanApplication(String applicationDescription){


        progressBar = new ProgressDialog(this.getContext());
        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait. While loading...");
        progressBar.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progressBar.show();


        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> createClanResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    String message = jsonResponse.getString("message");

                    if (success) {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        //Submit request to server to create clan.
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(message)
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen
                    progressBar.dismiss();

                    e.printStackTrace();

                }

            }

        };

        sharedPreferences = this.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Since we are going to be using the autologin, call the server from the user info saved in the preferences
        //Submit login request with the server
        SubmitClanApplicationRequest submitClansRequest = new SubmitClanApplicationRequest(clanName, userID, applicationDescription,authToken, createClanResponseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(submitClansRequest);

    }

}