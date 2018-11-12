package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.CreateNewClanRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClansCreateFragment extends Fragment{

    ProgressDialog progressBar;
    public SharedPreferences sharedPreferences;

    public ClansCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);

        View view = inflater.inflate(R.layout.fragment_clans_create, container, false);


        //Declare our UI here
        final EditText etClanName = (EditText) view.findViewById(R.id.etClanName);
        final EditText etClanLogoURL = (EditText) view.findViewById(R.id.etClanLogoURL);
        final EditText etClanDescription = (EditText) view.findViewById(R.id.etClanDescription);
        Button btCreateClan = (Button) view.findViewById(R.id.btCreateClan);
        Button btClearInfo = (Button) view.findViewById(R.id.btClearInfo);

        //Button to clear user info
        btClearInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etClanName.setText("");
                etClanLogoURL.setText("");
                etClanDescription.setText("");

            }
        });


        btCreateClan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (etClanName.getText().toString().length() >= 3 && etClanDescription.getText().toString().length() >= 3){

                    submitCreateClanRequest(etClanName.getText().toString(), etClanLogoURL.getText().toString(), etClanDescription.getText().toString());

                } else {

                    //Submit request to server to create clan.
                    Toast.makeText(getContext(), "Clan name or Description is too short.", Toast.LENGTH_LONG).show();
                }

            }
        });


        //Button btCreateClan = (Button) view.findViewById(R.id.btCreateClan);

        // Defined Array values to show in ListView

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);

        return view;
    }

    public void submitCreateClanRequest(String clanName, String clanLogo, String clanDescription){

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
        String clanOwner = sharedPreferences.getString("loginUsername", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Since we are going to be using the autologin, call the server from the user info saved in the preferences
        //Submit login request with the server
        CreateNewClanRequest submitClansRequest = new CreateNewClanRequest(clanName, clanDescription, clanLogo, clanOwner, authToken, createClanResponseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(submitClansRequest);

    }

}