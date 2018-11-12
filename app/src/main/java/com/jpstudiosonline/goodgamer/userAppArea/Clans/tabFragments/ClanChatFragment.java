package com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetUserMessagesRequest;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.SendUserMessageRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities.SelectGameActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClanChatFragment extends Fragment{

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false;
    private String clanName, serverSideClanID;
    private boolean canUserAdmin, fragmentStarted;

    //Timer for checking clan messages
    Handler h = new Handler();
    int delay = 15000; //15 seconds
    Runnable runnable;


    public ClanChatFragment() {
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

        View view = inflater.inflate(R.layout.fragment_clan_chat, container, false);

        buttonSend = (Button) view.findViewById(R.id.send);
        listView = (ListView) view.findViewById(R.id.msgview);

        fragmentStarted=  true;

        chatArrayAdapter = new ChatArrayAdapter(getContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) view.findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (chatText.getText().toString().length() > 0){

                        chatText.setText("");
                        return sendChatMessage(true, chatText.getText().toString(), "Now", "Me");

                    } else {

                        Toast.makeText(getContext(), "Message cannot be blank.", Toast.LENGTH_LONG).show();

                    }
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (chatText.getText().toString().length() > 0){

                    sendUserMessage(chatText.getText().toString());
                    sendChatMessage(true, chatText.getText().toString(), "Now", "Me");
                    chatText.setText("");

                } else {

                    Toast.makeText(getContext(), "Message cannot be blank.", Toast.LENGTH_LONG).show();
                }

            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        //Method used to get messages from user
        getUserMessages();

        h.postDelayed(new Runnable() {
            public void run() {
                //do something

                runnable=this;

                if (fragmentStarted){

                    checkUserMessages();

                } else {

                    h.removeCallbacks(runnable);
                }

                h.postDelayed(runnable, delay);
            }
        }, delay);

        return view;
    }

    private boolean sendChatMessage(boolean side, String Message, String dateSent, String fromUserName) {
        chatArrayAdapter.add(new ChatMessage(side, Message, dateSent, fromUserName));
        //Log.e("side", String.valueOf(side));
        return true;
    }

    //Method used to get messages from server/user
    public void getUserMessages(){

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getMessagesListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("message");

                    String messageHolderForRefresh = chatText.getText().toString();

                    if (String.valueOf(jsonArray) != "null"){

                        if (success) {

                            chatArrayAdapter.removeMessages();

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String messageTo = jsonObject.getString("clanID");
                                    String messageFromUserName = jsonObject.getString("messageFromUserName");
                                    String messageBody = jsonObject.getString("messageBody");
                                    boolean side = jsonObject.getBoolean("side");
                                    String sentDate = jsonObject.getString("sentDate");
                                    //Log.e("sideFromServer", String.valueOf(side));
                                    sendChatMessage(side, messageBody, sentDate, messageFromUserName);

                                }

                            }



                        } else {


                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Failed to load messages, try again.")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }

                    } else {

                        String[] mobileArray = {"0 Messages to read."};
                        //Dismiss the loading screen


                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences userInfoPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = userInfoPreferences.getString("userid", "");
        String userToken = userInfoPreferences.getString("userHash", "");
        GetUserMessagesRequest submitMessagesRequest = new GetUserMessagesRequest(userID, serverSideClanID, userToken, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(submitMessagesRequest);

    }

    //Method used to get messages from server/user
    public void sendUserMessage(String message){

        final ProgressDialog progressBar = new ProgressDialog(getContext());
        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading Messages");
        progressBar.setMessage("Please wait. While loading...");
        progressBar.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progressBar.show();

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

                        if (success) {

                            //Dismiss the loading screen
                            progressBar.dismiss();
                            getUserMessages();


                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage("Failed to load messages, try again.")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }

                        getUserMessages();

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();


                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen
                    progressBar.dismiss();

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences userInfoPreferences = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = userInfoPreferences.getString("userid", "N/A");
        String authToken = userInfoPreferences.getString("userHash", "N/A");

        SendUserMessageRequest submitMessagesRequest = new SendUserMessageRequest(userID, serverSideClanID, authToken, message, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(submitMessagesRequest);

    }

    @Override
    public void onDestroy () {

        fragmentStarted = false;
        h.removeCallbacks(runnable);
        super.onDestroy ();

    }

    public void checkUserMessages(){

        if (fragmentStarted && super.getUserVisibleHint()){

            getUserMessages();
        }
    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && fragmentStarted) {

            getUserMessages();

        }

        else {

            fragmentStarted = false;
            h.removeCallbacks(runnable);
        }
    }



}