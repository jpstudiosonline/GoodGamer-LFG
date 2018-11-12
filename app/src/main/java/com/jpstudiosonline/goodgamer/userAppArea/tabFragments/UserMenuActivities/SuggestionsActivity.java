package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitSuggestionRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SuggestionsActivity extends AppCompatActivity {

    EditText etSuggestion;
    public static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_area);

        setContentView(R.layout.activity_suggestions);

        //Set the toolbar
        Toolbar suggestToolBar = (Toolbar) findViewById(R.id.suggestionsToolbar);
        suggestToolBar.setTitle("New Suggestion");
        setSupportActionBar(suggestToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        etSuggestion = (EditText) findViewById(R.id.etSuggestion);

    }



    //Method that is caleld to submit the suggestion
    public void submitSuggestion(){

        String suggestionDescription = etSuggestion.getText().toString();

        if (suggestionDescription.length() >= 5){

            SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            String savedUserID = sharedPreferences.getString("userid", DEFAULT);

            //Setup a loading bar for Logging in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Submitting");
            progressBar.setMessage("Please wait...");
            progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressBar.show();

            //Setup listener to get the Login response from the server
            Response.Listener<String> suggestionResponseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success){

                            Toast.makeText(SuggestionsActivity.this, "Suggestion submited, thanks!.", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();

                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(SuggestionsActivity.this);
                            builder.setMessage("Failed to submit, try again.")
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

            //Submit the suggestion
            SubmitSuggestionRequest submitSuggestionRequest = new SubmitSuggestionRequest(String.valueOf(savedUserID), suggestionDescription, suggestionResponseListener);
            RequestQueue queue = Volley.newRequestQueue(SuggestionsActivity.this);
            queue.add(submitSuggestionRequest);

        } else {

            Toast.makeText(SuggestionsActivity.this, "Description is short.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.suggestions_menu, menu);
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
            case R.id.action_submit_new_suggestion:
                submitSuggestion();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
