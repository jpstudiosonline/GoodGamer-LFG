package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectConsoleListActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectGameListActtivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectGroupListActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitLfgRequest;
import com.jpstudiosonline.goodgamer.userLogin.userLoginActivities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateLfgRequestActivity extends AppCompatActivity {

    public int selectedConsoleID, selectedGameID, selectedGroupID = 0;
    EditText lfgRequestTitle, lfgRequestDescription;
    public SharedPreferences sharedPreferences;
    public static final String DEFAULT = "N/A";
    String savedUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_lfg_request);

        //Set the toolbar
        Toolbar lfgToolBar = (Toolbar) findViewById(R.id.createLfgRequestToolbar);
        lfgToolBar.setTitle("New LFG Request");
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the user input UI
        lfgRequestTitle = (EditText) findViewById(R.id.etLfgRequestTitle);
        lfgRequestDescription = (EditText) findViewById(R.id.etLfgRequestDescription);

        //Get the buttons that we want to set for the request parameters
        findViewById(R.id.btLfgRequestGroupSelect).setOnClickListener(new groupHandleButton());
        findViewById(R.id.btLfgRequestConsoleSelect).setOnClickListener(new consoleHandleButton());
        Button btLfgRequestGameSelect = (Button) findViewById(R.id.btLfgRequestGameSelect);

        //Get user ID if user wants to submit the request
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //If we have user info try to get the user ID value
        if (sharedPreferences.contains("loginUsername") && sharedPreferences.contains("loginPassword") && sharedPreferences.contains("userid")){

            savedUserID = sharedPreferences.getString("userid", DEFAULT);

        }

        //Get the intent so we can get the gameID and gamename
        Intent intent = getIntent();

        if (intent.hasExtra("gameID")){

            selectedGameID = Integer.parseInt(intent.getStringExtra("gameID"));
            btLfgRequestGameSelect.setText(intent.getStringExtra("gameName"));


        } else {

            btLfgRequestGameSelect.setOnClickListener(new gameHandleButton());

        }

    }

    //Open the correct activity when user clicks the button
    class gameHandleButton implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(CreateLfgRequestActivity.this, SelectGameListActtivity.class);
            startActivityForResult(intent, 0);
        }
    }

    class groupHandleButton implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(CreateLfgRequestActivity.this, SelectGroupListActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    class consoleHandleButton implements View.OnClickListener {
        public void onClick(View v) {
            Intent intent = new Intent(CreateLfgRequestActivity.this, SelectConsoleListActivity.class);
            startActivityForResult(intent, 0);
        }
    }

    //Setup what happens once we recieve what a user has selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data != null && data.hasExtra("GameName") && data.hasExtra("GameID")) {

            //Toast.makeText(this, data.getStringExtra("GameName") + " Selected.", Toast.LENGTH_LONG).show();
            Button button = (Button) findViewById(R.id.btLfgRequestGameSelect);
            button.setText(data.getStringExtra("GameName"));

            selectedGameID = data.getIntExtra("GameID", 0);

        } else if (data != null && data.hasExtra("Consolename") && data.hasExtra("ConsoleID")){

            Button btConsoleSelectList = (Button) findViewById(R.id.btLfgRequestConsoleSelect);
            btConsoleSelectList.setText(data.getStringExtra("Consolename"));

            selectedConsoleID = data.getIntExtra("ConsoleID", 0);

        } else if (data != null && data.hasExtra("GroupName") && data.hasExtra("GroupID")){

            Button btGroupSelectList = (Button) findViewById(R.id.btLfgRequestGroupSelect);
            btGroupSelectList.setText(data.getStringExtra("GroupName"));

            selectedGroupID = data.getIntExtra("GroupID", 0);

        }
        else {

            Toast.makeText(this, "Nothing Selection!", Toast.LENGTH_LONG).show();

        }
    }

    //Call this method to submit request to server for a new LFG request
    public void submitLfgRequest(){

        //Make sure user selected console and game and a group
        if (selectedConsoleID != 0 && selectedGameID != 0 && selectedGroupID != 0){

            //Make sure user entered valid request title and description
            if (lfgRequestTitle.length() > 5 && lfgRequestDescription.length() > 5){

                //Setup a loading bar for Logging in a user
                final ProgressDialog progressBar = new ProgressDialog(this);

                //Submit request to server upon matching valid user/password length.
                //User info should be saved in savedpreferences.

                //Set the loading bar to let user know it's working
                progressBar.setTitle("Submitting");
                progressBar.setMessage("Please wait. Submitting...");
                progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progressBar.show();

                //Setup listener to get the Login response from the server
                Response.Listener<String> submitResponseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("successcode");

                            if (success){

                                Toast.makeText(CreateLfgRequestActivity.this, "LFG Request submitted", Toast.LENGTH_LONG).show();
                                progressBar.dismiss();
                                finish();


                            } else {

                                //Dismiss the loading screen
                                progressBar.dismiss();

                                AlertDialog.Builder builder = new AlertDialog.Builder(CreateLfgRequestActivity.this);
                                builder.setMessage("Failed to Submit, try again.")
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

                SubmitLfgRequest submitRequest = new SubmitLfgRequest(savedUserID, String.valueOf(selectedGroupID), lfgRequestTitle.getText().toString(), lfgRequestDescription.getText().toString(), String.valueOf(selectedConsoleID), String.valueOf(selectedGameID), submitResponseListener);
                RequestQueue queue = Volley.newRequestQueue(CreateLfgRequestActivity.this);
                queue.add(submitRequest);

                //Log.e("Submit","Submit request");


            } else if (lfgRequestTitle.length() < 5){

                Toast.makeText(CreateLfgRequestActivity.this, "Title too short", Toast.LENGTH_SHORT).show();

            } else if (lfgRequestDescription.length() < 5){

                Toast.makeText(CreateLfgRequestActivity.this, "Description too short", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(CreateLfgRequestActivity.this, "Invalid Title or Description", Toast.LENGTH_SHORT).show();
            }

        } else if (selectedGameID == 0) {

            Toast.makeText(CreateLfgRequestActivity.this, "You must select a Game", Toast.LENGTH_SHORT).show();

        } else if (selectedGroupID == 0) {

            Toast.makeText(CreateLfgRequestActivity.this, "You must select a Group", Toast.LENGTH_SHORT).show();

        } else if (selectedConsoleID == 0) {

            Toast.makeText(CreateLfgRequestActivity.this, "You must select a Console", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(CreateLfgRequestActivity.this, "You did not choose a selection", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_lfg_request_menu, menu);
        return true;

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
            case R.id.action_submit_lfg_request:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                submitLfgRequest();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
