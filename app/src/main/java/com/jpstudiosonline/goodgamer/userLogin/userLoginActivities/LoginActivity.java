package com.jpstudiosonline.goodgamer.userLogin.userLoginActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.UserAreaActivity;
import com.jpstudiosonline.goodgamer.userLogin.userLoginRequests.SendLoginRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    public SharedPreferences sharedPreferences;
    public static final String DEFAULT = "N/A";
    public Button loginUserButton;
    public EditText etLoginUsername, etLoginUserpassword;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Get the views for registering a new account and restting password.
        Button registerView = (Button) findViewById(R.id.btRegisterButton);

        //Set the register button listener when user clicks to register
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Opens up a new window to allow user to register
                Intent registerUserIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerUserIntent);

            }
        });


        //Get the User login UI info, call the login once user hits the Login button.  Call different method
        loginUserButton = (Button) findViewById(R.id.btUserLoginButton);
        etLoginUsername = (EditText) findViewById(R.id.etLoginUsername);
        etLoginUserpassword = (EditText) findViewById(R.id.etLoginUserpassword);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        TextView tvContactAdmins = (TextView) findViewById(R.id.tvContactAdmins);

        //Setup the listener for the Login button and call method to get user input and verify & send to the server
        loginUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Call user login method without credentials
                userLogin("", "");

            }
        });

        //Check if user logged in before in the preferences
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //If we have user info try to login automaticlly
        if (sharedPreferences.contains("loginUsername") && sharedPreferences.contains("loginPassword") && sharedPreferences.contains("userid")){

            String savedUserName = sharedPreferences.getString("loginUsername", DEFAULT);
            String savedUserID = sharedPreferences.getString("userid", DEFAULT);
            String savedUserPassword = sharedPreferences.getString("loginPassword", DEFAULT);

            //Call user login method without credentials
            userLogin(savedUserName, savedUserPassword);

        }

        //Setup listener for the forgot password textview
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resetPasswordIntent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(resetPasswordIntent);
            }
        });

        //Seutp Listener for contact admins
        tvContactAdmins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Open up the browser to the JPSTudiosonline page
                Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/community/user/JPStudiosonline"));
                startActivity(intent);

            }
        });

    }


    //Method called when user hits the Login button
    public void userLogin (final String autoUsername, final String autoPassword){

        //Get the values of the entered info for Logging in a new user.  This will be sent to the server.
        final String loginUsername = etLoginUsername.getText().toString();
        final String loginUserpassword = etLoginUserpassword.getText().toString();

        //Get the input length for username, password and fields.  Make sure that the max length is 20
        int nameLength = loginUsername.length();
        int passwordLength = loginUserpassword.length();

        //Set the auto login to false then check if we are trying to auto login by checking the length.
        boolean autoLogin = false;
        if (autoUsername.length() >= 5 && autoPassword.length() >= 5){

            autoLogin = true;

        } else {

            autoLogin = false;
        }

        //check for valid user input.  Use the isValidEmail to check for email format.
        if (nameLength >= 5 && passwordLength >= 5 || autoUsername.length() >= 5 && autoPassword.length() >= 5) {

            //Setup a loading bar for Logging in a user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Submit request to server upon matching valid user/password length.
            //User info should be saved in savedpreferences.

            //Set the loading bar to let user know it's working
            progressBar.setTitle("Logging in");
            progressBar.setMessage("Please wait. Authenticating...");
            progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progressBar.show();

            //Setup listener to get the Login response from the server
            Response.Listener<String> loginResponseListener = new Response.Listener<String>(){

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success){

                            String userId = jsonResponse.getString("userid");
                            String userName = jsonResponse.getString("username");
                            String userTitle = jsonResponse.getString("title");
                            String userHash = jsonResponse.getString("hash");

                            //Save user info to shared perf
                            sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("loginUsername", userName);
                            editor.putString("userid", userId);
                            editor.putString("userTitle", userTitle);
                            editor.putString("userHash", userHash);


                            //Check and make sure the password is enough length when user autologin.
                            if (loginUserpassword.length() < 5){

                                editor.putString("loginPassword", autoPassword);

                            } else {

                                editor.putString("loginPassword", loginUserpassword);

                            }

                            editor.putBoolean("gcmRegistered", false);
                            editor.apply();

                            //Dismiss the loading screen
                            progressBar.dismiss();
                            finish();

                            //Opens up a new window for user to use the App in UserArea activity
                            Intent loginUserIntent = new Intent(LoginActivity.this, UserAreaActivity.class);
                            loginUserIntent.putExtra("userID", userId);
                            LoginActivity.this.startActivity(loginUserIntent);

                        } else {

                            //Dismiss the loading screen
                            progressBar.dismiss();

                            String errorResponse = jsonResponse.getString("response");

                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setMessage("Failed to login. " + errorResponse)
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

            if (autoLogin){

                //Since we are going to be using the autologin, call the server from the user info saved in the preferences
                //Submit login request with the server
                SendLoginRequest submitLoginRequest = new SendLoginRequest(autoUsername, autoPassword, loginResponseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(submitLoginRequest);

            } else {

                //Sign in using entered credentials since this is not auto login.
                //Submit login request with the server
                SendLoginRequest submitLoginRequest = new SendLoginRequest(loginUsername, loginUserpassword, loginResponseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(submitLoginRequest);
            }

        } else if (nameLength < 5) {

            Toast.makeText(LoginActivity.this, "Username too short", Toast.LENGTH_LONG).show();

        } else if (passwordLength < 5) {

            Toast.makeText(LoginActivity.this, "Password too short", Toast.LENGTH_LONG).show();

        } else {

            //If users enters an invalid user/password meeting above if statements then they have also entered an invalid user info
            Toast.makeText(LoginActivity.this, "Invalid User and Password combo", Toast.LENGTH_LONG).show();

        }


    }
}
