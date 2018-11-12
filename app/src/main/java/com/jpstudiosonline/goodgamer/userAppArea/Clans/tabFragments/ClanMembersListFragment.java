package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetClansMembersRequest;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetPendingInviteCountRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ClanMembersListFragment extends Fragment{

    ListView listView;
    String[] clanMembersListArray;
    public String clanName;
    public TextView clanPendingAppInvites;
    private boolean canUserAdmin;

    public ClanMembersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            clanName = getArguments().getString("clanName");
            canUserAdmin = getArguments().getBoolean("canUserAdmin");
        } else {

            this.getActivity().finish();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);

        getClanMembers();

        View view = inflater.inflate(R.layout.fragment_clan_members_list, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.gameLetterList);
        clanPendingAppInvites =  (TextView) view.findViewById(R.id.tvPendingClanApplications);

        if (!canUserAdmin){
            clanPendingAppInvites.setVisibility(View.GONE);

        }

        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);


        // Assign adapter to ListView
        //listView.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, values));
        //listView.setAdapter(adapter);

        // ListView Item Click Listener

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value

                // Show Alert
                //Toast.makeText(getContext(),"Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG).show();

            }

        });

        return view;
    }

    public void getClanMembers(){

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getMembersListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("message");

                    if (String.valueOf(jsonArray) != "null"){

                        clanMembersListArray = new String[jsonArray.length()];

                        if (success) {

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String clanInfo = jsonObject.getString("memberUserName");
                                    clanMembersListArray[i] = clanInfo;

                                }

                                //Update the list
                                listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, clanMembersListArray));

                                if (canUserAdmin){

                                    getClanPendingInvites();

                                }



                            } else {

                                clanMembersListArray[0] = "0 Clans found.";
                            }



                        } else {


                            Toast.makeText(getContext(), "No Messages", Toast.LENGTH_LONG).show();
                        }


                    } else {

                        clanMembersListArray[0] = "0 Clans found.";
                        //Dismiss the loading screen


                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        GetClansMembersRequest getClanList = new GetClansMembersRequest(userID, clanName, authToken, getMembersListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanList);


    }


    public void getClanPendingInvites(){

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getMembersListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    if (success){

                        //Get the LFG list from the jpresponse json
                        String pendingInviteCount = jsonResponse.getString("pendingInviteCount");
                        clanPendingAppInvites.setText("Pending Clan applications: " +pendingInviteCount);

                    }


                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        GetPendingInviteCountRequest getClanList = new GetPendingInviteCountRequest(userID, clanName, authToken, getMembersListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanList);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            getClanMembers();
            getClanPendingInvites();

        }
        else {  }
    }

}