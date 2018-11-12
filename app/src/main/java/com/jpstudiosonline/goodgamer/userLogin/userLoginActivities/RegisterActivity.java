package com.jpstudiosonline.goodgamer.userLogin.userLoginActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userLogin.userLoginRequests.SendRegisterRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Get the Toolbar from the activity_register.xml layout file
        Toolbar registerToolbar = (Toolbar) findViewById(R.id.registertoolbar);
        setSupportActionBar(registerToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the edit text values from the user input
        final EditText etRegisterNewUsername = (EditText) findViewById(R.id.etRegisterUsername);
        final EditText etRegisterNewUserpassword = (EditText) findViewById(R.id.etRegisterUserpassword);
        final EditText etConfirmRegisterNewUserpassword = (EditText) findViewById(R.id.etConfirmRegisterUserpassword);
        final EditText etRegisterNewUseremail = (EditText) findViewById(R.id.etRegisterUseremail);
        final EditText etConfirmRegisterUseremail = (EditText) findViewById(R.id.etConfirmRegisterUseremail);
        TextView tvUserEula = (TextView) findViewById(R.id.tvUserEula);

        //Get the register user button to set up the listener
        Button registerButton = (Button) findViewById(R.id.btSubmitRegisterButton);

        //Setup a loading bar for registing user
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Setup the listener for the register user button to submit user registartion to the server.
        //URL to register: http://goodgamer.jpstudiosonline.com/Users/Registration/UserRegistration.php
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the values of the entered info for registering new user.  This will be sent to the server.
                final String newUsername = etRegisterNewUsername.getText().toString();
                final String newUserpassword = etRegisterNewUserpassword.getText().toString();
                final String newConfirmPassword = etConfirmRegisterNewUserpassword.getText().toString();
                final String newUseremail = etRegisterNewUseremail.getText().toString();
                final String confirmNewUseremail = etConfirmRegisterUseremail.getText().toString();

                //Get the input length for username, password and fields.  Make sure that the max length is 20
                int nameLength = newUsername.length();
                int passwordLength = newUserpassword.length();
                int emailLength = newUseremail.length();

                //Setup the listener that will check when we register
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("jpresponse");
                            if (success) {

                                //Cancel loading since we registered with the server.
                                progressBar.dismiss();

                                //Notify user we registered
                                Toast.makeText(RegisterActivity.this, "Signup successfull, please validate your email before logging in.  Check your spam folder!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                                finish();

                            } else {
                                progressBar.dismiss();

                                String errorResponse = jsonResponse.getString("response");


                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Registation Failed. " + errorResponse)
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.dismiss();

                        }

                    }
                };

                //check for valid user input.  Use the isValidEmail to check for email format.
                if (nameLength >= 5 && nameLength <= 20 && passwordLength >= 5 && emailLength >= 5 && isValidEmail(newUseremail) && newUserpassword.equals(newConfirmPassword) && confirmNewUseremail.equals(newUseremail)) {

                    //Set the loading bar to let user know it's working
                    progressBar.setTitle("Registering");
                    progressBar.setMessage("Please wait. Registering...");
                    progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progressBar.show();

                    //Submit request to register
                    SendRegisterRequest submitRegisterRequest = new SendRegisterRequest(newUsername, newUserpassword, newUseremail, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(submitRegisterRequest);

                } else if (nameLength < 5) {

                    Toast.makeText(RegisterActivity.this, "Username too short", Toast.LENGTH_LONG).show();

                } else if (passwordLength < 5) {

                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_LONG).show();

                } else if (emailLength < 5) {

                    Toast.makeText(RegisterActivity.this, "Email too short", Toast.LENGTH_LONG).show();

                } else if(!newUserpassword.matches(newConfirmPassword)){

                    Toast.makeText(RegisterActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();

                } else if(nameLength > 20){

                    Toast.makeText(RegisterActivity.this, "Username is too long.", Toast.LENGTH_LONG).show();

                } else if (!confirmNewUseremail.equals(newUseremail)){

                    Toast.makeText(RegisterActivity.this, "E-mails do not match!", Toast.LENGTH_LONG).show();
                }

                else {

                    //If users enters an invalid email meeting above if statements then they have also entered an invalid email address
                    Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_LONG).show();

                }


            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //Open up the eula in a browser
        tvUserEula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("http://goodgamer.jpstudiosonline.com/Users/Registration/eula.html"));
                startActivity(intent);


            }
        });
    }

    //Check for valid email.
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
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


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Register Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
