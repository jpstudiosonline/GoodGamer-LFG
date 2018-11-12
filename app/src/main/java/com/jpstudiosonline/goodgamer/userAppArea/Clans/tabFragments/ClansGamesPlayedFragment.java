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
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames.SelectClanGameActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetClansGamesRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClansGamesPlayedFragment extends Fragment{

    ListView listView;
    String[] gameListArray;
    private String clanName;
    private boolean canUserAdmin;

    public ClansGamesPlayedFragment() {
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

        getGamesList();
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_game_name, container, false);


        View view = inflater.inflate(R.layout.fragment_clans_games_played, container, false);


        Button btAddClanGames = (Button) view.findViewById(R.id.btAddClanGames);

        if (canUserAdmin){

            btAddClanGames.setVisibility(View.VISIBLE);

        } else {

            btAddClanGames.setVisibility(View.GONE);

        }

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.gameLetterList);
        listView.setTextFilterEnabled(true);

        // Defined Array values to show in ListView
        final String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);


        // Assign adapter to ListView
        listView.setTextFilterEnabled(true);
        //listView.setAdapter(new ArrayAdapter<String>(view.getContext(),android.R.layout.simple_list_item_multiple_choice, gameListArray));
        //listView.setAdapter(adapter);

        btAddClanGames.setOnClickListener(new gameHandleButton());

        if (getActivity().getIntent().hasExtra("refresh")){

            getGamesList();
        }




        return view;
    }

    //Open the correct activity when user clicks the button
    class gameHandleButton implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), SelectClanGameActivity.class);
            intent.putExtra("clanName", clanName);
            startActivityForResult(intent, 0);
        }
    }

    public void getGamesList(){

        gameListArray = new String[50];

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

                        gameListArray = new String[jsonArray.length()];

                        if (success) {

                            if (jsonArray.length() > 0){


                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String gameName = jsonObject.getString("gameName");
                                    gameListArray[i] = gameName;


                                }


                                //Update the list
                                //SimpleAdapter adapter = new SimpleAdapter(getContext(), data,android.R.layout.simple_list_item_1, new String[] { "AAA" },
                                        //new int[] { 1 });
                                //listView.setAdapter(adapter);
                                listView.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, gameListArray));


                            } else {

                                gameListArray[0] = "0 Games found.";
                            }



                        } else {


                            Toast.makeText(getContext(), "No Games found.", Toast.LENGTH_LONG).show();
                        }


                    } else {

                        gameListArray[0] = "0 Games found.";
                        //Dismiss the loading screen


                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences sharedPreferences = this.getActivity().getApplicationContext().getSharedPreferences("UserInfo", getActivity().MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        GetClansGamesRequest getClanList = new GetClansGamesRequest(userID, clanName, authToken, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(getClanList);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            getGamesList();

        }
        else {  }
    }


}
