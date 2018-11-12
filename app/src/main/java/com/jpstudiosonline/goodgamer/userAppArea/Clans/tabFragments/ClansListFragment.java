package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClansAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ViewClanAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetTopClansRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClansListFragment extends Fragment{

    ListView listView;
    String[] clanListArray;
    SwipeRefreshLayout swipeContainer;

    public ClansListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get list of clans


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);
        getTopClans();
        View view = inflater.inflate(R.layout.fragment_clans_list, container, false);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                getTopClans();



                //Log.e("REfreshing", "refreshing");
            }
        });

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.topClanList);

        // Defined Array values to show in ListView

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);

        // Assign adapter to ListView
        //listView.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_1, clanListArray));
        //listView.setAdapter(adapter);

        // ListView Item Click Listener


        return view;
    }

    public void getTopClans(){

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

                        clanListArray = new String[jsonArray.length()];

                        if (success) {

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String clanInfo = jsonObject.getString("ClanList");
                                    clanListArray[i] = clanInfo;

                                }

                                //Update the list
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view,
                                                            int position, long id) {

                                        // ListView Clicked item index
                                        int itemPosition = position;

                                        // ListView Clicked item value

                                        String  clanName = clanListArray[position].substring(0, clanListArray[position].indexOf(": Members ("));
                                        Intent selectedClanIntent = new Intent(getContext(), ViewClanAreaActivity.class);
                                        selectedClanIntent.putExtra("clanName", clanName);
                                        startActivity(selectedClanIntent);


                                    }

                                });
                                listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, clanListArray));


                            } else {

                                clanListArray[0] = "0 Clans found.";
                            }



                        } else {

                            clanListArray[0] = "0 Clans found.";
                            listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, clanListArray));
                        }

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                    } else {

                        clanListArray = new String[250];
                        clanListArray[0] = "0 Clans found.";
                        //Dismiss the loading screen
                        swipeContainer.setRefreshing(false);


                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();
                    swipeContainer.setRefreshing(false);

                }

            }

        };

        SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("userHash", "N/A");

        GetTopClansRequest getClanList = new GetTopClansRequest(authToken, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanList);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            getTopClans();

        }
        else {  }
    }

}