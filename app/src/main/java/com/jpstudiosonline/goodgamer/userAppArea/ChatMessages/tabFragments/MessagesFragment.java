package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments;

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
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.SendMessagesActivity;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests.GetMainActivityMessages;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessagesFragment extends Fragment{

    ListView listView;

    public MessagesFragment() {
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

        View view = inflater.inflate(R.layout.fragment_chat_messages, container, false);

        // Get ListView object from xml
        listView = (ListView) view.findViewById(R.id.lvMessageList);

        // Defined Array values to show in ListView
        String[] values = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3",
        "4", "5", "6", "7", "8", "9", "0"};

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, listView, values);

        getUserMessagesList();


        return view;
    }

    //Method used to populate listview with messages
    public void getUserMessagesList(){

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

                        String[] mobileArray = new String[jsonArray.length()];

                        if (success) {

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String messagesInfo = jsonObject.getString("MessagesInfo");
                                    mobileArray[i] = messagesInfo;

                                }


                            } else {

                                mobileArray[0] = "0 Messages to read.";
                            }

                            //setup our messages listview
                            listMessages(mobileArray);


                        } else {


                            Toast.makeText(getContext(), "No Messages", Toast.LENGTH_LONG).show();
                        }

                    } else {

                        String[] mobileArray = {"0 Messages to read."};
                        //Dismiss the loading screen

                        //setup our messages listview
                        listMessages(mobileArray);

                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences userInfoPreferences = getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = userInfoPreferences.getString("userid", "");
        String userName = userInfoPreferences.getString("loginUsername", "");
        String userToken = userInfoPreferences.getString("userHash", "");

        GetMainActivityMessages submitMessagesRequest = new GetMainActivityMessages(userID, userName, userToken,  getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(submitMessagesRequest);

    }

    //Method used to list messages to listview when user opens the app
    public void listMessages(final String[] mobileArray){


        ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                R.layout.overviewmessages_textview, mobileArray);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (mobileArray[position] != "0 Messages to read."){

                    Intent openMessageIntent = new Intent(getContext(), SendMessagesActivity.class);
                    openMessageIntent.putExtra("intentUserToReadFrom", mobileArray[position].substring(0, mobileArray[position].indexOf(" (")));
                    startActivity(openMessageIntent);

                }


            }
        });

        listView.setAdapter(adapter);

    }

}