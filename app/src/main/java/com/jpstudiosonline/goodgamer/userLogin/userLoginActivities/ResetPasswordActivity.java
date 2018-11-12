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
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userLogin.userLoginRequests.SendHashResetRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText etPasswordEmail, etHashCode, etNewPassword, etNewPassword2;
    Button btGetCodeButton, btValidateHashCode, btAlreadyHaveCode;
    TextView tvResetInstructions, tvResetPasswordWebPortalLink;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_reset_password);

        Toolbar passwordToolBar = (Toolbar) findViewById(R.id.resetPasswordToolbar);
        passwordToolBar.setTitle("Reset password");
        setSupportActionBar(passwordToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get the UI
        etPasswordEmail = (EditText) findViewById(R.id.etPasswordEmail);
        etHashCode = (EditText) findViewById(R.id.etHashCode);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etNewPassword2 = (EditText) findViewById(R.id.etNewPassword2);
        tvResetInstructions = (TextView) findViewById(R.id.tvResetInstructions);
        tvResetPasswordWebPortalLink = (TextView) findViewById(R.id.tvResetPasswordWebPortalLink);


        etHashCode.setVisibility(View.INVISIBLE);
        etNewPassword.setVisibility(View.INVISIBLE);
        etNewPassword2.setVisibility(View.INVISIBLE);

        btGetCodeButton = (Button) findViewById(R.id.btGetCodeButton);
        btAlreadyHaveCode = (Button) findViewById(R.id.btAlreadyHaveCode);
        btValidateHashCode = (Button) findViewById(R.id.btValidateHashCode);

        btValidateHashCode.setVisibility(View.INVISIBLE);

        btAlreadyHaveCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btValidateHashCode.setVisibility(View.VISIBLE);
                btAlreadyHaveCode.setVisibility(View.INVISIBLE);
                btGetCodeButton.setVisibility(View.INVISIBLE);

                etHashCode.setVisibility(View.VISIBLE);
                etNewPassword.setVisibility(View.VISIBLE);
                etNewPassword2.setVisibility(View.VISIBLE);

                tvResetInstructions.setText("Enter code recieved with your email, and password filled in below.");

            }
        });

        btGetCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getHashReset();

            }
        });

        btValidateHashCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitNewPasswordChange();
            }
        });

        //Our method to open the web portal to reset password
        tvResetPasswordWebPortalLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://lfg.cloud/index.php/Users/Login/forgotpassword"));
                startActivity(intent);

            }
        });

    }


    //Method used to get a hash code sent to the user email
    public void getHashReset(){

        if (etPasswordEmail.getText().length() >= 5 && isValidEmail(etPasswordEmail.getText().toString())){

            //Setup a loading bar for registing user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Setup the listener that will check when we register
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success) {

                            //Cancel loading since we registered with the server.
                            progressBar.dismiss();

                            //Notify user we registered
                            Toast.makeText(ResetPasswordActivity.this, "Email sent with reset code", Toast.LENGTH_SHORT).show();

                        } else {
                            progressBar.dismiss();
                            String errorResponse = jsonResponse.getString("response");

                            AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                            builder.setMessage("Failed. " + errorResponse)
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

            //Submit request to get a hash code
            SendHashResetRequest submitHashRequest = new SendHashResetRequest(etPasswordEmail.getText().toString(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(ResetPasswordActivity.this);
            queue.add(submitHashRequest);

        } else {

            Toast.makeText(ResetPasswordActivity.this, "Email too short or invalid", Toast.LENGTH_LONG).show();
        }

    }

    //Method used to reset the users password
    public void submitNewPasswordChange(){

        String userEmail = etPasswordEmail.getText().toString();
        String hashCode = etHashCode.getText().toString();
        String firstPassword = etNewPassword.getText().toString();
        String secondPassword = etNewPassword2.getText().toString();

        //Make sure we have all the items we need before submitting to the server.
        if (userEmail.length() >= 5 && hashCode.length() >= 1 && firstPassword.length() >= 5 && secondPassword.length() >= 5 && firstPassword.matches(secondPassword) && isValidEmail(userEmail)){

            //Setup a loading bar for registing user
            final ProgressDialog progressBar = new ProgressDialog(this);

            //Setup the listener that will check when we register
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("successcode");

                        if (success) {

                            //Cancel loading since we registered with the server.
                            progressBar.dismiss();

                            //Notify user we registered
                            Toast.makeText(ResetPasswordActivity.this, "Password has been reset", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            progressBar.dismiss();

                            String errorResponse = jsonResponse.getString("response");

                            AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
                            builder.setMessage("Failed to reset password. " + errorResponse)
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

            //Submit request to reset the password
            SendHashResetRequest submitHashRequest = new SendHashResetRequest(userEmail, hashCode, firstPassword, responseListener);
            RequestQueue queue = Volley.newRequestQueue(ResetPasswordActivity.this);
            queue.add(submitHashRequest);

        } else if (!firstPassword.matches(secondPassword)){

            Toast.makeText(ResetPasswordActivity.this, "Passwords don't match", Toast.LENGTH_LONG).show();

        } else if (!isValidEmail(userEmail)){

            Toast.makeText(ResetPasswordActivity.this, "Invalid email", Toast.LENGTH_LONG).show();

        } else if (hashCode.length() < 1){

            Toast.makeText(ResetPasswordActivity.this, "Invalid code", Toast.LENGTH_LONG).show();

        } else if (firstPassword.isEmpty() || secondPassword.isEmpty()){

            Toast.makeText(ResetPasswordActivity.this, "Passwords cannot be empty", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(ResetPasswordActivity.this, "Invalid info", Toast.LENGTH_LONG).show();

        }

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
}
