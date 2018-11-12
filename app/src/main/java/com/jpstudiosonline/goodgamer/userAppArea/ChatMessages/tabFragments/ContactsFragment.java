package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments;

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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests.UpdateContactMembersRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment{

    public DesignContactsListRecyclerAdapter gameRecyclerAdapter;
    private static List<ContactMembersList> requestGameList = new ArrayList<>();
    RecyclerView gameRecyclerView;
    public ContactsFragment() {
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

        View view = inflater.inflate(R.layout.fragment_contact_members, container, false);

        gameRecyclerView = (RecyclerView) view.findViewById(R.id.gameSelectRecycler);

        getContacts();
        return view;
    }

    public void getContacts(){

        final ProgressDialog progressBar = new ProgressDialog(getContext());

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading List");
        progressBar.setMessage("Please wait. Getting Games List...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Make sure we empty the list if user already opened the listing screen
        if (!requestGameList.isEmpty()){

            requestGameList.clear();
        }

        //Setup listener to get the Members list response from the server
        Response.Listener<String> getMembersListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");
                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("message");

                    if (success){

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Cycle through the repsonse list
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int ownerParentID = Integer.parseInt(jsonObject.optString("ownerParentID").toString());
                            String ownerUserName = jsonObject.optString("ownerUserName").toString();
                            int contactUserID = Integer.parseInt(jsonObject.optString("contactUserID").toString());
                            String contactUserName = jsonObject.getString("contactUserName");

                            ContactMembersList request = new ContactMembersList(ownerParentID, ownerUserName, contactUserID, contactUserName);
                            requestGameList.add(request);

                        }

                        gameRecyclerAdapter.notifyDataSetChanged();
                        //Dismiss the loading screen
                        progressBar.dismiss();


                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();


                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Failed to find contacts!")
                                .setNegativeButton("Ok", null)
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

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Get the gamest list from the server
        UpdateContactMembersRequest updateClanMembersRequest = new UpdateContactMembersRequest(userID, authToken, getMembersListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(updateClanMembersRequest);

        gameRecyclerAdapter = new DesignContactsListRecyclerAdapter(requestGameList);

        //Assign the adapter and set the layout
        gameRecyclerView.setAdapter(gameRecyclerAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gameRecyclerView.addItemDecoration(new ContactMemberDividerItemDecoration(getContext(), ContactMemberDividerItemDecoration.VERTICAL_LIST));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());

    }

}