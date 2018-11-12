package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests.SubmitEditUserProfileRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileActivity extends AppCompatActivity {

    EditText etProfileGamerTag;
    String userID, savedUserID, userTitle;
    public static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_profile);

        //Set the toolbar
        Toolbar profileToolBar = (Toolbar) findViewById(R.id.editUserProfileToolbar);
        profileToolBar.setTitle("Edit Profile");
        setSupportActionBar(profileToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get our profile info
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        String gamerTag = intent.getStringExtra("gamerTag");

        //Set the gamertag to the edit text and let the user edit it.
        etProfileGamerTag = (EditText) findViewById(R.id.etProfileGamerTag);

        etProfileGamerTag.setText(gamerTag);

        //Get our logged in user to verify
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        savedUserID = sharedPreferences.getString("userid", DEFAULT);
        userTitle  = sharedPreferences.getString("userTitle", DEFAULT);


    }

    //submit the edit profile to the server
    public void submitEditProfile(){

        String newGamerTag = etProfileGamerTag.getText().toString();

        //Make sure our gamertag length is good
        if (newGamerTag.length() > 1 && newGamerTag.length() <= 20 && userID.equals(savedUserID) || userTitle.equals("ADMIN")){

            //Setup a loading bar for Submitting the reply in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Submitting");
            progressBar.setMessage("Please wait. Submitting...");
            progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressBar.show();

            //Setup listener to submit the reply edit to the LFg request
            Response.Listener<String> submitResponseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success){

                            Toast.makeText(EditProfileActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();


                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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

            //Send request to server to update the LFG request
            SubmitEditUserProfileRequest submitRequest = new SubmitEditUserProfileRequest(userID, newGamerTag, submitResponseListener);
            RequestQueue queue = Volley.newRequestQueue(EditProfileActivity.this);
            queue.add(submitRequest);

        } else {

            //Respond to user input properly
            if (newGamerTag.length() <= 1){

                Toast.makeText(EditProfileActivity.this, "Gamer Tag is short", Toast.LENGTH_LONG).show();

            } else if (newGamerTag.length() > 20){

                Toast.makeText(EditProfileActivity.this, "Gamer Tag is long", Toast.LENGTH_LONG).show();

            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);

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
            case R.id.action_submit_edit_profile:
                submitEditProfile();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
