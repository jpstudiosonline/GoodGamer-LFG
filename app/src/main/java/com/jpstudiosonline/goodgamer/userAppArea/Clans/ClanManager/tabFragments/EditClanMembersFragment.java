package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments;

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
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests.UpdateClanMembersRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditClanMembersFragment extends Fragment{

    ListView listView;
    public DesignClanMembersListRecyclerAdapter gameRecyclerAdapter;
    private static List<ClanManageMembersList> requestGameList = new ArrayList<>();
    RecyclerView gameRecyclerView;
    boolean canUserAdmin;
    public String clanName, serverSideClanID;

    public EditClanMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            clanName = getArguments().getString("clanName");
            canUserAdmin = getArguments().getBoolean("canUserAdmin");
            serverSideClanID  = getArguments().getString("serverSideClanID");


        } else {

            this.getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);



        View view = inflater.inflate(R.layout.fragment_edit_clan_members, container, false);
        gameRecyclerView = (RecyclerView) view.findViewById(R.id.gameSelectRecycler);

        getClanMembers();
        // Get ListView object from xml
        //Show that we are loading the request


        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);




        return view;
    }

    public void getClanMembers(){

        final ProgressDialog progressBar = new ProgressDialog(getContext());

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading List");
        progressBar.setMessage("Please wait...");
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

                            int clanID = Integer.parseInt(jsonObject.optString("clanID").toString());
                            int clanMemberUserID = Integer.parseInt(jsonObject.optString("clanMemberID").toString());
                            int parentUserID = Integer.parseInt(jsonObject.optString("parentUserID").toString());
                            boolean isUserAdmin = jsonObject.getBoolean("memberCanAdmin");
                            String userName = jsonObject.getString("parentUserName");
                            String clanName = jsonObject.getString("clanName");
                            boolean canDeleteMember = jsonObject.getBoolean("canDeleteMember");
                            boolean canApproveUser = jsonObject.getBoolean("canApproveUser");


                            ClanManageMembersList request = new ClanManageMembersList(clanID, userName, clanMemberUserID, parentUserID, clanName, canDeleteMember, canApproveUser, isUserAdmin);
                            requestGameList.add(request);

                        }

                        gameRecyclerAdapter.notifyDataSetChanged();
                        //Dismiss the loading screen
                        progressBar.dismiss();


                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();


                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Failed to find members, try suggesting one!")
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
        UpdateClanMembersRequest updateClanMembersRequest = new UpdateClanMembersRequest(String.valueOf(userID), String.valueOf(serverSideClanID), String.valueOf(clanName), String.valueOf(authToken), getMembersListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(updateClanMembersRequest);

        gameRecyclerAdapter = new DesignClanMembersListRecyclerAdapter(requestGameList);

        //Assign the adapter and set the layout
        gameRecyclerView.setAdapter(gameRecyclerAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gameRecyclerView.addItemDecoration(new ClanMemberDividerItemDecoration(getContext(), ClanMemberDividerItemDecoration.VERTICAL_LIST));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            getClanMembers();
        }
        else {  }
    }

}