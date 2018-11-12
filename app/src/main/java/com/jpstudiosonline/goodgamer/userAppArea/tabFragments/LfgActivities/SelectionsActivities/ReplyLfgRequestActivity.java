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
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitLfgRequestReply;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ReplyLfgRequestActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    public static final String DEFAULT = "N/A";
    public String savedUserName, savedUserID;
    public int requestID;
    public EditText etUserComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reply_lfg_request);

        //Set the tool bar to the be title of the request
        //Set the toolbar
        Toolbar replyToolBar = (Toolbar) findViewById(R.id.lfgReplyToolbar);
        replyToolBar.setTitle("New comment");
        setSupportActionBar(replyToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get the text view and set the title as the user name posting
        TextView tvReplyUserName = (TextView) findViewById(R.id.tvReplyUserName);
        TextView tvReplyRequestTitle = (TextView) findViewById(R.id.tvReplyRequestTitle);
        etUserComments = (EditText) findViewById(R.id.etLfgReplyComments);

        //Check if we have user ID and username
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //Check if user logged in before in the preferences
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //If we have user info try to login automaticlly
        if (sharedPreferences.contains("loginUsername") && sharedPreferences.contains("userid")){

            savedUserName = sharedPreferences.getString("loginUsername", DEFAULT);
            savedUserID = sharedPreferences.getString("userid", DEFAULT);

        } else {

            Toast.makeText(ReplyLfgRequestActivity.this, "Invalid user, try clearing app cache", Toast.LENGTH_LONG).show();
            finish();

        }

        //Get the info from intent for title
        Intent intent = getIntent();
        String postTitle = intent.getStringExtra("postTitle");
        requestID = intent.getIntExtra("requestID", 0);

        //Set the view with the user name
        tvReplyRequestTitle.setText("Posting to: " + postTitle );
        tvReplyUserName.setText("as: " + savedUserName);


    }

    //Method to submit the reply to the LFG request
    public void submitLfgRequestReply(){

        if (etUserComments.getText().toString().length() >= 1){

            String replyComment = etUserComments.getText().toString();

            //Setup a loading bar for Submitting the reply in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Submitting");
            progressBar.setMessage("Please wait. Submitting...");
            progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressBar.show();

            //Setup listener to submit the reply to the LFg request
            Response.Listener<String> submitResponseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success){

                            Toast.makeText(ReplyLfgRequestActivity.this, "Reply submitted", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();


                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(ReplyLfgRequestActivity.this);
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

            SubmitLfgRequestReply submitRequest = new SubmitLfgRequestReply(String.valueOf(requestID), savedUserID, replyComment, submitResponseListener);
            RequestQueue queue = Volley.newRequestQueue(ReplyLfgRequestActivity.this);
            queue.add(submitRequest);

        } else {

            Toast.makeText(ReplyLfgRequestActivity.this, "Comment is short, try again.", Toast.LENGTH_LONG).show();

        }

    }

    //Create the menu for this activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_lfg_reply_menu, menu);
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
            case R.id.action_submit_lfg_request_reply:
                // User chose the "Favorite" action, mark the current item
                submitLfgRequestReply();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
