package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequests.GetGameListings;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.GamesList;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.RecyclerAdapterItems.DividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectGameActivity extends AppCompatActivity {

    private static List<GamesList> requestGameList = new ArrayList<>();
    RecyclerView gameRecyclerView;
    public DesignGamesListRecyclerAdapter gameRecyclerAdapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_create_lfg_request);

        setContentView(R.layout.activity_select_game_activity);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeGameSelectContainer);

        //Set the toolbar
        Toolbar gameToolBar = (Toolbar) findViewById(R.id.gameSelectToolbar);
        gameToolBar.setTitle("Select Game");
        setSupportActionBar(gameToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String gameLetter = intent.getStringExtra("gameLetter");

        gameRecyclerView = (RecyclerView) findViewById(R.id.gameSelectRecycler);

        getGames(gameLetter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                getGames(gameLetter);



                //Log.e("REfreshing", "refreshing");
            }
        });

    }

    //Method used to get the games info from the server
    public void getGames(String gameLetter){

        //Show that we are loading the request
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading List");
        progressBar.setMessage("Please wait. Getting Games List...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Make sure we empty the list if user already opened the listing screen
        if (!requestGameList.isEmpty()){

            requestGameList.clear();
        }

        //Setup listener to get the Console list response from the server
        Response.Listener<String> gameListingListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("jpresponse");

                    if (success){

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Cycle through the repsonse list
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int gamesListID = Integer.parseInt(jsonObject.optString("gameID").toString());
                            String gameListName = jsonObject.getString("gameName");
                            int requestCount = Integer.parseInt(jsonObject.optString("requestCount").toString());

                            GamesList request = new GamesList(gamesListID, gameListName, requestCount);
                            requestGameList.add(request);

                        }

                        gameRecyclerAdapter.notifyDataSetChanged();
                        //Dismiss the loading screen
                        progressBar.dismiss();


                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectGameActivity.this);
                        builder.setMessage("Failed to find games, try suggesting one!")
                                .setNegativeButton("Ok", null)
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

        //Get the gamest list from the server
        GetGameListings submitGameListRequest = new GetGameListings(gameLetter, gameListingListener);
        RequestQueue queue = Volley.newRequestQueue(SelectGameActivity.this);
        queue.add(submitGameListRequest);

        gameRecyclerAdapter = new DesignGamesListRecyclerAdapter(requestGameList);

        //Assign the adapter and set the layout
        gameRecyclerView.setAdapter(gameRecyclerAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gameRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
