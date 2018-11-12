package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities;

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
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitEditLfgRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditLfgRequestActivity extends AppCompatActivity {

    public String updatedDescription, requestID, requestUserID;
    public EditText etUpdatedDescription;
    public static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_edit_lfg_request);

        //Set the tool bar to the be title of the request
        //Set the toolbar
        Toolbar lfgToolBar = (Toolbar) findViewById(R.id.editLfgRequestToolbar);
        lfgToolBar.setTitle("Edit post");
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the intent for the request info
        Intent requestIntent = getIntent();
        updatedDescription = requestIntent.getStringExtra("editableRequestDescription");
        requestID = String.valueOf(requestIntent.getIntExtra("requestID", 0));
        requestUserID = requestIntent.getStringExtra("userID");

        //Get the edit text view and set it to the text
        etUpdatedDescription = (EditText) findViewById(R.id.etUpdateLfgRequestDescription);
        etUpdatedDescription.setText(updatedDescription);


    }

    //Method to submit the edit to the server
    public void submitEditLfgRequest(){

        //Get our new comment
        String commentToUpdate = etUpdatedDescription.getText().toString();

        //Make sure our new description is not empty
        if (commentToUpdate.length() >= 1){


            //Setup a loading bar for Submitting the reply in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Submitting");
            progressBar.setMessage("Please wait. Submitting...");
            progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressBar.show();

            //Check if user logged in before in the preferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String savedUserID = sharedPreferences.getString("userid", DEFAULT);

            //Setup listener to submit the reply edit to the LFg request
            Response.Listener<String> submitResponseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success){

                            Toast.makeText(EditLfgRequestActivity.this, "Post Updated", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();


                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditLfgRequestActivity.this);
                            builder.setMessage("Failed to Post, try again.")
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
            SubmitEditLfgRequest submitRequest = new SubmitEditLfgRequest(requestUserID, requestID, commentToUpdate, submitResponseListener);
            RequestQueue queue = Volley.newRequestQueue(EditLfgRequestActivity.this);
            queue.add(submitRequest);


        } else {

            Toast.makeText(EditLfgRequestActivity.this, "Unable to edit, try relogging", Toast.LENGTH_LONG).show();
            finish();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_lfg_request_menu, menu);
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
            case R.id.action_edit_lfg_request_description:
                submitEditLfgRequest();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
