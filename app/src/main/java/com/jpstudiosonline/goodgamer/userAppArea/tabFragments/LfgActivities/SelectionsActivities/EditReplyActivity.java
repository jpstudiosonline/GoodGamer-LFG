package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitEditReplyRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class EditReplyActivity extends AppCompatActivity {

    EditText etNewReply;
    String replyID, replyUserID, requestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_lfg_request);

        setContentView(R.layout.activity_edit_reply);

        //Set the toolbar
        Toolbar replyToolBar = (Toolbar) findViewById(R.id.editReplyToolbar);
        replyToolBar.setTitle("Update reply");
        setSupportActionBar(replyToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get the intent info for the reply

        Intent intent = getIntent();
        String reply = intent.getStringExtra("ReplyComments");
        replyID = intent.getStringExtra("replyID");
        replyUserID = intent.getStringExtra("replyUserID");
        requestID = intent.getStringExtra("requestID");

        //Log.e("replyID", replyID);
        //Log.e("replyUserID", replyUserID);
        //Log.e("requestID", requestID);

        //Set the edit text with the reply
        etNewReply = (EditText) findViewById(R.id.etNewReply);
        etNewReply.setText(reply);



    }

    //Method used to update the replies on the server
    public void submitEditReply(){

        String newComment = etNewReply.getText().toString();

        if (newComment.length() >= 1){

            //Setup a loading bar for Submitting the reply in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Submitting");
            progressBar.setMessage("Please wait. Updating...");
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

                            Toast.makeText(EditReplyActivity.this, "Reply Updated", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();


                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            AlertDialog.Builder builder = new AlertDialog.Builder(EditReplyActivity.this);
                            builder.setMessage("Failed to Update, try again.")
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

            //Send request to server to update the reply
            SubmitEditReplyRequest submitRequest = new SubmitEditReplyRequest("UPDATE", replyID, requestID, replyUserID, newComment, submitResponseListener);
            RequestQueue queue = Volley.newRequestQueue(EditReplyActivity.this);
            queue.add(submitRequest);


        } else {

            //Invalid length
            Toast.makeText(EditReplyActivity.this, "Reply is short.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_reply_menu, menu);
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

            case R.id.action_submit_edit_reply:
                //Call method to update the reply with the server
                submitEditReply();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

