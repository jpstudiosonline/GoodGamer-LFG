package com.jpstudiosonline.goodgamer.userAppArea.tabFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequests.GetGameLfgRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequests.GetGameSubscriptions;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.CreateLfgRequestActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GameRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.RecyclerAdapterItems.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class GameRequestsActivity extends AppCompatActivity {

    RecyclerView gameRequestSelectRecycler;
    public List<GameRequest> gameRequestList = new ArrayList<>();
    public DesignDashboardRecyclerAdapter gameRequestsRecyclerAdapter;
    public static final String DEFAULT = "N/A";
    public String loggedInUserID, gameID;
    public FloatingActionButton fabAddLfgRequest;
    MenuItem notificationActive, notificationOff;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game_requests);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeLfgGameSelectContainer);

        //Get game info
        Intent intent = getIntent();
        final String gameName = intent.getStringExtra("gameName");
        gameID = String.valueOf(intent.getIntExtra("gameID", 0));

        //Set the toolbar
        Toolbar lfgToolBar = (Toolbar) findViewById(R.id.gameRequestToolbar);
        lfgToolBar.setTitle(gameName);
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        gameRequestSelectRecycler = (RecyclerView) findViewById(R.id.gameRequestSelectRecycler);

        //Check if user logged in before in the preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        loggedInUserID = sharedPreferences.getString("userid", DEFAULT);

        //get the requests
        getRequests(loggedInUserID, gameID);

        //Create fab button to allow users to submit a new LFG request
        fabAddLfgRequest = (FloatingActionButton) findViewById(R.id.addRequestFab);
        fabAddLfgRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click action create new window for users to submit a new request
                Intent createLfgRequestIntent = new Intent(GameRequestsActivity.this, CreateLfgRequestActivity.class);
                createLfgRequestIntent.putExtra("gameID", gameID);
                createLfgRequestIntent.putExtra("gameName", gameName);
                startActivity(createLfgRequestIntent);
            }
        });

        //Get the user subscription
        getSubscriptions("GETSUB");

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                //Get the user subscription
                getRequests(loggedInUserID, gameID);
                getSubscriptions("GETSUB");
                //get the requests

            }
        });

    }


    //Method called to get if user is subed to this game
    public void getSubscriptions(final String method){

        //Setup listener to get the LFG requests response from the server
        Response.Listener<String> getSubResponseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("jpresponse");


                    //If success then user is subbed to the game
                    if (success) {

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        if (method.equals("REGISTER") || method.equals("GETSUB")){

                            notificationActive.setVisible(true);
                            notificationOff.setVisible(false);

                        } else if (method.equals("UNREGISTER")) {

                            notificationActive.setVisible(false);
                            notificationOff.setVisible(true);

                        }


                    } else {

                        notificationActive.setVisible(false);
                        notificationOff.setVisible(true);

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                    if (swipeContainer.isRefreshing()){

                        swipeContainer.setRefreshing(false);
                    }

                }

            }

        };

            //Submit Delete the post request
            GetGameSubscriptions deleteLfgRequest = new GetGameSubscriptions(loggedInUserID, gameID, method, getSubResponseListener);
            RequestQueue queue = Volley.newRequestQueue(GameRequestsActivity.this);
            queue.add(deleteLfgRequest);

    }

    public void getRequests(String requesterID, String gameID){

        //Setup a loading bar for Logging in a user
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Submit request to server upon matching valid user/password length.
        //User info should be saved in savedpreferences.

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait. Getting requests...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Make sure we empty the list if user already opened the listing screen
        if (!gameRequestList.isEmpty()){

            gameRequestList.clear();
        }


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

                            gameRequestList.clear();
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
                            gameRequestList.add(request);

                            //Dismiss the loading screen



                        }

                        progressBar.dismiss();

                        gameRequestsRecyclerAdapter.notifyDataSetChanged();

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(GameRequestsActivity.this);
                        builder.setMessage("Failed to find requests, try posting one!")
                                .setNegativeButton("Ok", null)
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

        //Since we are going to be using the requester ID to get votes, call the server from the user info saved in the preferences
        //Submit LFG request request with the server
        GetGameLfgRequest submitLfgRequest = new GetGameLfgRequest(requesterID, gameID, lfgRequestsResponseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(submitLfgRequest);

        //Create the List adapter after we get our response from the server
        gameRequestsRecyclerAdapter = new DesignDashboardRecyclerAdapter(gameRequestList);

        //Assign the adapter and set the layout
        gameRequestSelectRecycler.setAdapter(gameRequestsRecyclerAdapter);
        gameRequestSelectRecycler.setLayoutManager(new LinearLayoutManager(this));
        gameRequestSelectRecycler.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        gameRequestSelectRecycler.setItemAnimator(new DefaultItemAnimator());

        //Let the recycler hide the fab button when users scrolls down
        gameRequestSelectRecycler.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fabAddLfgRequest.hide();
                else if (dy < 0)
                    fabAddLfgRequest.show();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_requests_menu, menu);

        //Get notification buttons and hide depending if user is subbed to this game or not
        notificationActive = menu.findItem(R.id.action_notificaiton_active);
        notificationOff = menu.findItem(R.id.action_notificaiton_off);

        notificationActive.setVisible(false);
        notificationOff.setVisible(false);
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
            case R.id.action_notificaiton_off:
                getSubscriptions("REGISTER");
                Toast.makeText(getApplicationContext(), "Subscribed for Game notifications", Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_notificaiton_active:
                getSubscriptions("UNREGISTER");
                Toast.makeText(getApplicationContext(), "UnSubscribed from notifications", Toast.LENGTH_LONG).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
