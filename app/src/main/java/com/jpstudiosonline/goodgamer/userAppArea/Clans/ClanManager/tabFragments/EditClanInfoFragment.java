package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments;

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
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests.GetClanInfoRequest;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests.UpdateClanInfoRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class EditClanInfoFragment extends Fragment{

    ListView listView;
    String clanName, serverClanID;
    public EditText etClanMessageOfDay, etClanLogoURL, etClanDescription, etClanNameAndTitle;

    public EditClanInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            clanName = getArguments().getString("clanName");
        } else {

            this.getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);

        getClanInfo();

        View view = inflater.inflate(R.layout.fragment_edit_clan_info, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.gameLetterList);

        //Declare our UI here
        etClanMessageOfDay = (EditText) view.findViewById(R.id.etClanMessageOfDay);
        etClanNameAndTitle = (EditText) view.findViewById(R.id.etClanNameAndTitle);
        etClanLogoURL = (EditText) view.findViewById(R.id.etClanLogoURL);
        etClanDescription = (EditText) view.findViewById(R.id.etClanDescription);
        Button btSubmitClanInfoUpdate = (Button) view.findViewById(R.id.btSubmitClanInfoUpdate);

        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);

        btSubmitClanInfoUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitClanInfoUpdate();
            }
        });


        return view;
    }

    public void getClanInfo(){

        final ProgressDialog progressBar = new ProgressDialog(getContext());

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginUsername = sharedPreferences.getString("loginUsername", "N/A");
        String userid = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Get clan info from server
        //Setup listener to get the Login response from the server
        Response.Listener<String> getClanInfoListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    if (success){

                        String clanID = jsonResponse.getString("clanID");
                        String clanName = jsonResponse.getString("clanName");
                        String totalMemberCount = jsonResponse.getString("totalMemberCount");
                        String clanDescription = jsonResponse.getString("clanDescription");
                        String logo = jsonResponse.getString("logo");
                        String clanStatus = jsonResponse.getString("clanStatus");
                        String messageOfTheDay = jsonResponse.getString("messageOfTheDay");
                        String clanOwner = jsonResponse.getString("clanOwner");
                        String canAdmin = jsonResponse.getString("canAdmin");
                        String canApplyToClan = jsonResponse.getString("canApplyToClan");
                        String isMember = jsonResponse.getString("isMember");
                        String clanApplicationStatus = jsonResponse.getString("clanApplicationStatus");
                        serverClanID = clanID;
                        etClanMessageOfDay.setText(messageOfTheDay);
                        etClanNameAndTitle.setText(clanName);
                        etClanLogoURL.setText(logo);
                        etClanDescription.setText(clanDescription);


                        progressBar.dismiss();



                    } else {

                        //Log.e("Token failed", token);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Failed to find Clan info.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();

                        getActivity().finish();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    progressBar.dismiss();
                    getActivity().finish();

                }

            }

        };

        //Get info from server
        GetClanInfoRequest getClanInfoRequest = new GetClanInfoRequest(clanName, loginUsername, userid, authToken, getClanInfoListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanInfoRequest);
    }

    public void submitClanInfoUpdate(){

        String newClanMesageOfDay = etClanMessageOfDay.getText().toString();
        String newClanNameAndTitle = etClanNameAndTitle.getText().toString();
        String newClanLogoURL = etClanLogoURL.getText().toString();
        String newClanDescription = etClanDescription.getText().toString();

        final ProgressDialog progressBar = new ProgressDialog(getContext());

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginUsername = sharedPreferences.getString("loginUsername", "N/A");
        String userid = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Setup listener to picku response
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    String message = jsonResponse.getString("message");

                    if (success){

                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);
                        progressBar.dismiss();


                    } else {

                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        progressBar.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        UpdateClanInfoRequest updateClanInfoRequest = new UpdateClanInfoRequest(userid, newClanNameAndTitle, serverClanID, newClanMesageOfDay,newClanDescription, newClanLogoURL, loginUsername, authToken, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(updateClanInfoRequest);





    }

}