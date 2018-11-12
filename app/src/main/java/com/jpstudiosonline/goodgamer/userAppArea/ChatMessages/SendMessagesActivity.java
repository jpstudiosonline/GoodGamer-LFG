package com.jpstudiosonline.goodgamer.userAppArea.ChatMessages;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests.GetChatMessagesRequest;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests.SendUserMessageRequest;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ChatMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SendMessagesActivity extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText, etToUserName;
    private Button buttonSend;
    private boolean side = false;

    private String intentUserToReadFrom = "";
    private TextView tvMessageToUserName;
    Handler h = new Handler();
    int delay = 15000; //15 seconds
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_send_messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sendMessageToolBar);
        toolbar.setTitle("Messaging");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        buttonSend = (Button) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);
        etToUserName = (EditText) findViewById(R.id.etToUserName);
        tvMessageToUserName = (TextView) findViewById(R.id.tvMessageToUserName);

        Intent intent = getIntent();

        if (intent.hasExtra("intentUserToReadFrom")){

            intentUserToReadFrom = intent.getStringExtra("intentUserToReadFrom");
            etToUserName.setVisibility(View.GONE);
            tvMessageToUserName.setText("Messaging: " + intentUserToReadFrom);

            getUserMessages(intentUserToReadFrom);

            h.postDelayed(new Runnable() {
                public void run() {
                    //do something

                    runnable=this;
                    getUserMessages(intentUserToReadFrom);

                    h.postDelayed(runnable, delay);
                }
            }, delay);
        }

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    if (chatText.getText().toString().length() > 0){

                        chatText.setText("");
                        return sendChatMessage(true, chatText.getText().toString(), "Now", "Me");

                    } else {

                        Toast.makeText(SendMessagesActivity.this, "Message cannot be blank.", Toast.LENGTH_LONG).show();

                    }
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (chatText.getText().toString().length() > 0 && etToUserName.getText().toString().length() > 0 || chatText.getText().toString().length() > 0 && intentUserToReadFrom.length() > 0){

                    if (intentUserToReadFrom != ""){

                        sendUserMessage(chatText.getText().toString(), intentUserToReadFrom);

                    } else {

                        sendUserMessage(chatText.getText().toString(), etToUserName.getText().toString());

                    }

                    sendChatMessage(true, chatText.getText().toString(), "Now", "Me");
                    chatText.setText("");

                } else {

                    Toast.makeText(SendMessagesActivity.this, "Message or TO: field cannot be blank.", Toast.LENGTH_LONG).show();
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

    }

    private boolean sendChatMessage(boolean side, String Message, String dateSent, String fromUserName) {
        chatArrayAdapter.add(new ChatMessage(side, Message, dateSent, fromUserName));
        //chatText.setText("");
        //Log.e("side", String.valueOf(side));
        return true;
    }

    //Method used to get messages from server/user
    public void getUserMessages(String userToReadFrom){


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

                            chatArrayAdapter.removeMessages();

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    //Cycle through the repsonse list
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String messageTo = jsonObject.getString("MessageTo");
                                    String messageFrom = jsonObject.getString("MessageFrom");
                                    String messageBody = jsonObject.getString("MessageBody");
                                    boolean side = jsonObject.getBoolean("side");
                                    String sentDate = jsonObject.getString("SentDate");
                                    //Log.e("sideFromServer", String.valueOf(side));
                                    sendChatMessage(side, messageBody, sentDate, messageFrom);

                                }

                            }


                        } else {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("Failed to load messages, try again.")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }

                    } else {

                        String[] mobileArray = {"0 Messages to read."};


                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

        };

        SharedPreferences userInfoPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginUsername = userInfoPreferences.getString("loginUsername", "N/A");
        String userToken = userInfoPreferences.getString("userHash", "N/A");

        GetChatMessagesRequest submitMessagesRequest = new GetChatMessagesRequest(userToReadFrom, loginUsername, userToken, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(SendMessagesActivity.this);
        queue.add(submitMessagesRequest);

    }

    //Method used to get messages from server/user
    public void sendUserMessage(String message, final String messageToUserName){

        final ProgressDialog progressBar = new ProgressDialog(this);
        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading Messages");
        progressBar.setMessage("Please wait. While loading...");
        progressBar.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        intentUserToReadFrom = messageToUserName;

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getMessagesListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    //Get the LFG list from the jpresponse json
                    String message = jsonResponse.getString("message");

                    if (success){

                        progressBar.dismiss();

                        getUserMessages(messageToUserName);
                        etToUserName.setVisibility(View.GONE);
                        tvMessageToUserName.setText("Messaging: " + intentUserToReadFrom);

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessagesActivity.this);
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

        SharedPreferences userInfoPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginUsername = userInfoPreferences.getString("loginUsername", "N/A");
        String userToken = userInfoPreferences.getString("userHash", "N/A");

        SendUserMessageRequest submitMessagesRequest = new SendUserMessageRequest(loginUsername, messageToUserName, userToken, message, getMessagesListener);
        RequestQueue queue = Volley.newRequestQueue(SendMessagesActivity.this);
        queue.add(submitMessagesRequest);

    }

    @Override
    protected void onStop() {
        h.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
