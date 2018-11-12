package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

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
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetClanLogsRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClansLogHistoryFragment extends Fragment{

    ListView listView;
    String[] clanLogListArray;
    public String clanName;

    public ClansLogHistoryFragment() {
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

        getClanLogs();

        View view = inflater.inflate(R.layout.fragment_clans_loghistory, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.gameLetterList);

        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);


        // Assign adapter to ListView
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

    public void getClanLogs(){

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getMessagesListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("message");

                    if (String.valueOf(jsonArray) != "null"){

                        clanLogListArray = new String[jsonArray.length()];

                        if (success) {

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String clanInfo = jsonObject.getString("CLAN_LOG_DETAILS");
                                    clanLogListArray[i] = clanInfo;

                                }

                                //Update the list
                                listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, clanLogListArray));


                            } else {

                                clanLogListArray = new String[50];
                                clanLogListArray[0] = "0 Logs found.";
                            }



                        } else {


                            Toast.makeText(getContext(), "No Logs", Toast.LENGTH_LONG).show();
                        }


                    } else {

                        clanLogListArray = new String[50];
                        clanLogListArray[0] = "0 Logs found.";


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

        GetClanLogsRequest getClanList = new GetClanLogsRequest(userID, clanName, authToken, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanList);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            getClanLogs();

        }
        else {  }
    }

}