package com.jpstudiosonline.goodgamer.userAppArea.tabFragments;



import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.tabFragments.FragmentRequests.RemoveContactMembersRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GameRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GetLfgRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.RecyclerAdapterItems.DividerItemDecoration;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.EditProfileActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests.AddContactMembersRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests.GetCheckContactRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests.GetProfileRepliesRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.UserProfileRequests.GetUserProfileRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserProfileActivity extends AppCompatActivity {

    public static final String DEFAULT = "N/A";
    public TextView tvProfileUserName, tvProfileGamerTag, tvProfileScore, tvProfilePosts, tvProfileCreatedTime, tvListingType;
    public DesignDashboardRecyclerAdapter profiledRecyclerAdapter;
    public List<GameRequest> profileRequestList = new ArrayList<>();
    public String savedUserID, profileUserID, finalUserID, userTitle, serverProfileID, viewingProfileUserName, loggedInUserName;
    public RecyclerView profileRecyclerView, profileRepliesRecyclerView;
    private SwipeRefreshLayout swipeContainer;
    MenuItem editProfile, viewCommentsRecycler, viewRequestsRecycler, action_add_contact, action_message_user;
    public Toolbar lfgToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user_profile);

        //Set the toolbar
        lfgToolBar = (Toolbar) findViewById(R.id.userProfileToolbar);
        lfgToolBar.setTitle("View Profile");
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeProfileRequestSelectContainer);

        //Get the user ID if the user clicks the profile buttong to view their own profile
        //Otherwise use to get the userID from the click that started this activity

        //Get the preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        savedUserID = sharedPreferences.getString("userid", DEFAULT);
        userTitle  = sharedPreferences.getString("userTitle", DEFAULT);
        loggedInUserName  = sharedPreferences.getString("loginUsername", DEFAULT);

        //Get the UI info
        tvProfileUserName = (TextView) findViewById(R.id.tvProfileUserName);
        tvProfileGamerTag = (TextView) findViewById(R.id.tvProfileGamerTag);
        tvProfileScore = (TextView) findViewById(R.id.tvProfileScore);
        tvProfilePosts = (TextView) findViewById(R.id.tvProfilePosts);
        tvProfileCreatedTime = (TextView) findViewById(R.id.tvProfileCreated);
        tvListingType = (TextView) findViewById(R.id.tvListingType);

        //Get recycler
        profileRecyclerView = (RecyclerView) findViewById(R.id.userProfileRequestRecycler);
        profileRecyclerView.setNestedScrollingEnabled(false);

        profileRepliesRecyclerView = (RecyclerView) findViewById(R.id.userProfileCommentsRecycler);
        profileRepliesRecyclerView.setNestedScrollingEnabled(false);

        //Send to get the profile from the server using the savedUserID since there is no other input for the ID
        Intent intent = getIntent();
        if (intent.hasExtra("userID")){

            profileUserID = String.valueOf(intent.getStringExtra("userID"));
            finalUserID = String.valueOf(intent.getStringExtra("userID"));
            getUserInfo(String.valueOf(profileUserID));
            getProfilePosts(profileUserID, false);

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (viewCommentsRecycler.isVisible()){

                        // Remember to CLEAR OUT old items before appending in the new ones
                        //Get the user subscription
                        getUserInfo(String.valueOf(finalUserID));
                        getProfilePosts(finalUserID, false);

                    } else if (viewRequestsRecycler.isVisible()) {

                        // Remember to CLEAR OUT old items before appending in the new ones
                        //Get the user subscription
                        getUserInfo(String.valueOf(finalUserID));
                        getProfilePosts(finalUserID, true);
                    }



                }
            });

        } else {

            //Submit the profile ID using the shared perefrences
            //Set false for not getting replies
            finalUserID = savedUserID;
            getUserInfo(savedUserID);
            getProfilePosts(finalUserID, false);

            swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    if (viewRequestsRecycler.isVisible()){

                        // Remember to CLEAR OUT old items before appending in the new ones
                        //Get the user subscription
                        //Set false for not getting replies
                        getUserInfo(finalUserID);
                        getProfilePosts(finalUserID, true);

                    } else {

                        // Remember to CLEAR OUT old items before appending in the new ones
                        //Get the user subscription
                        //Set false for not getting replies
                        getUserInfo(finalUserID);
                        getProfilePosts(finalUserID, false);
                    }



                }
            });

        }

        checkUserContact(finalUserID);


    }

    //Method to get the user profile info
    public void getUserInfo(final String userID){

        String profileUserName, profileGamerTag;
        final int profileUserID, profileScore, profileTotalPosts;

        //Setup a loading bar for Logging in a user
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Submit request to server upon matching valid user/password length.
        //User info should be saved in savedpreferences.

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading Profile");
        progressBar.setMessage("Please wait. Loading...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Setup listener to get the Login response from the server
        Response.Listener<String> getProfileResponseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    if (success){

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);

                        }


                        //Get the json response
                        //Get the LFG list from the jpresponse json

                        String profileUserName = jsonResponse.getString("userName");
                        String profileUserID = jsonResponse.getString("userProfileID");
                        serverProfileID = jsonResponse.getString("userProfileID");
                        String profileUserGamerTag = jsonResponse.getString("gamerTag");
                        String profileUserScore = jsonResponse.getString("userScore");
                        String profileUserTitle = jsonResponse.getString("userTitle");
                        String profileUserPosts = jsonResponse.getString("totalPosts");
                        String profileCreatedTime = jsonResponse.getString("timeAdded");
                        setupProfileUIStats(profileUserName, profileUserGamerTag, profileUserScore, profileUserPosts, profileCreatedTime, userID);
                        viewingProfileUserName = profileUserName;

                        if (loggedInUserName.equals(viewingProfileUserName) ){

                            action_add_contact.setVisible(false);
                            action_message_user.setVisible(false);
                        }

                        progressBar.dismiss();
                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                        builder.setMessage("Failed to Load, try again.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen
                    progressBar.dismiss();

                    e.printStackTrace();

                    if (swipeContainer.isRefreshing()){

                        swipeContainer.setRefreshing(false);
                    }


                }

            }

        };


            //Submit the get the user profile request
            GetUserProfileRequest profileRequest= new GetUserProfileRequest(finalUserID, getProfileResponseListener);
            RequestQueue queue = Volley.newRequestQueue(UserProfileActivity.this);
            queue.add(profileRequest);

    }

    //Setup the UI for the user profile
    public void setupProfileUIStats(String userName, String gamerTag, String score, String posts, String created, String userID){

        tvProfileUserName.setText(userName);
        tvProfileGamerTag.setText(gamerTag);
        tvProfileScore.setText(score);
        tvProfilePosts.setText(posts);
        tvProfileCreatedTime.setText(created);
        lfgToolBar.setTitle(userName + "'s Profile");

        //Log.e("UserTitle2", userTitle);
        //Setup the edit menu button to be visible if our logged in  user ID is our saved user ID
        if (userID.equals(savedUserID) || userTitle.equals("ADMIN")){

            editProfile.setVisible(true);

        }
    }

    //Get the profile posts method
    public void getProfilePosts(String profileID, boolean getReplies){



        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> lfgRequestsResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("jpresponse");

                    if (success) {

                        if (swipeContainer.isRefreshing()){

                            profileRequestList.clear();
                            swipeContainer.setRefreshing(false);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Cycle through the repsonse list
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int lfgRequestID = Integer.parseInt(jsonObject.optString("requestID").toString());
                            String lfgRequestUsername = jsonObject.getString("requestUserName");
                            String lfgRequestGroup = jsonObject.getString("requestGroupType");
                            String lfgRequestTitle = jsonObject.getString("requestTitle");
                            String lfgRequestDescription = jsonObject.getString("requestDescription");
                            String lfgRequestConsole = jsonObject.getString("requestConsole");
                            String lfgRequestGamename = jsonObject.getString("requestGameName");
                            int lfgRequestTotalComments = Integer.parseInt(jsonObject.optString("requestTotalComments").toString());
                            int lfgRequestUpVotes = Integer.parseInt(jsonObject.optString("requestUpVotes").toString());
                            int lfgRequestUserID = Integer.parseInt(jsonObject.getString("requestUserID"));
                            String lfgRequestRequestersVote = jsonObject.getString("userVote");
                            String lfgRequestTimeCreated = jsonObject.getString("timeAdded");



                            GameRequest request = new GameRequest(lfgRequestID, lfgRequestUsername, lfgRequestGroup, lfgRequestTitle, lfgRequestDescription, lfgRequestConsole, lfgRequestGamename,
                                    lfgRequestTimeCreated, lfgRequestUpVotes, lfgRequestTotalComments, lfgRequestRequestersVote, lfgRequestUserID);
                            profileRequestList.add(request);

                            //Dismiss the loading screen

                        }

                        profiledRecyclerAdapter.notifyDataSetChanged();



                    } else {

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileActivity.this);
                        builder.setMessage("Failed to load requests, try again.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen

                    e.printStackTrace();

                    if (swipeContainer.isRefreshing()){

                        swipeContainer.setRefreshing(false);
                    }


                }

            }

        };

        if (getReplies){


            profiledRecyclerAdapter.clear();
            profileRequestList.clear();

            //Since we are going to be using the requester ID to get votes, call the server from the user info saved in the preferences
            //Submit LFG request request with the server
            GetProfileRepliesRequest submitRepliesRequest = new GetProfileRepliesRequest(savedUserID, profileID, lfgRequestsResponseListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(submitRepliesRequest);

            //Create the List adapter after we get our response from the server
            profiledRecyclerAdapter = new DesignDashboardRecyclerAdapter(profileRequestList);

            //Assign the adapter and set the layout
            profileRepliesRecyclerView.setAdapter(profiledRecyclerAdapter);
            profileRepliesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            profileRepliesRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            profileRepliesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        } else {

            if (!profileRequestList.isEmpty()){

                profiledRecyclerAdapter.clear();
                profileRequestList.clear();
            }

            //Since we are going to be using the requester ID to get votes, call the server from the user info saved in the preferences
            //Submit LFG request request with the server

            GetLfgRequest getLfgRequests = new GetLfgRequest(savedUserID, profileID, lfgRequestsResponseListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(getLfgRequests);

            //Create the List adapter after we get our response from the server
            profiledRecyclerAdapter = new DesignDashboardRecyclerAdapter(profileRequestList);

            //Assign the adapter and set the layout
            profileRecyclerView.setAdapter(profiledRecyclerAdapter);
            profileRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            profileRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            profileRecyclerView.setItemAnimator(new DefaultItemAnimator());

        }



    }


    //Set up the UI depending on the menu button pressed
    public void viewComments(){

        profileRecyclerView.setVisibility(View.INVISIBLE);
        profileRepliesRecyclerView.setVisibility(View.VISIBLE);
        getProfilePosts(finalUserID, true);
        tvListingType.setText("Comments:");

    }

    public void viewRequests(){

        profileRecyclerView.setVisibility(View.VISIBLE);
        profileRepliesRecyclerView.setVisibility(View.INVISIBLE);
        getProfilePosts(finalUserID, false);
        tvListingType.setText("Latest Requests");


    }

    public void checkUserContact(String checkUserID){

        SharedPreferences userSharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userid = userSharedPreferences.getString("userid", "N/A");
        String authToken = userSharedPreferences.getString("userHash", "N/A");

        //Setup listener to picku response
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");


                    if (success){

                        action_add_contact.setVisible(true);
                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);


                    } else {

                        action_add_contact.setVisible(false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        GetCheckContactRequest updateContactMembersRequest = new GetCheckContactRequest(userid, authToken, checkUserID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserProfileActivity.this);
        queue.add(updateContactMembersRequest);

    }

    public void addToContacts(){
        //Preferences
        SharedPreferences userSharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userid = userSharedPreferences.getString("userid", "N/A");
        String authToken = userSharedPreferences.getString("userHash", "N/A");

        //Setup listener to picku response
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    String message = jsonResponse.getString("message");

                    if (success){

                        Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_LONG).show();
                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);


                    } else {

                        Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        AddContactMembersRequest updateContactMembersRequest = new AddContactMembersRequest(userid, authToken, "ADD", finalUserID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(UserProfileActivity.this);
        queue.add(updateContactMembersRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_profile_menu, menu);

        editProfile = menu.findItem(R.id.action_edit_profile);
        editProfile.setVisible(false);

        viewCommentsRecycler = menu.findItem(R.id.action_view_profile_replies);
        viewRequestsRecycler = menu.findItem(R.id.action_view_profile_requests);
        action_add_contact = menu.findItem(R.id.action_add_contact);
        action_message_user = menu.findItem(R.id.action_message_user);

        //Setup info for viewing comments
        viewCommentsRecycler.setVisible(true);
        viewRequestsRecycler.setVisible(false);

        if (savedUserID == finalUserID){

            action_add_contact.setVisible(false);
            action_message_user.setVisible(false);
        }

        //Setup the listener for when user clicks the comments menu button
        viewCommentsRecycler.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                viewCommentsRecycler.setVisible(false);
                viewRequestsRecycler.setVisible(true);
                return false;
            }
        });

        viewRequestsRecycler.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                viewCommentsRecycler.setVisible(true);
                viewRequestsRecycler.setVisible(false);
                return false;
            }
        });


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

            //Show the edit profile activity
            case R.id.action_edit_profile:
                Intent editProfileIntent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
                editProfileIntent.putExtra("userID", finalUserID);
                editProfileIntent.putExtra("gamerTag", tvProfileGamerTag.getText().toString());
                startActivity(editProfileIntent);
                return true;
            case R.id.action_view_profile_replies:
                viewComments();
                return true;
            case R.id.action_view_profile_requests:
                viewRequests();
                return true;
            case R.id.action_add_contact:
                addToContacts();
                return true;
            case R.id.action_message_user:
                Intent openMessageIntent = new Intent(UserProfileActivity.this, com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.SendMessagesActivity.class);
                openMessageIntent.putExtra("intentUserToReadFrom", viewingProfileUserName);
                startActivity(openMessageIntent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
