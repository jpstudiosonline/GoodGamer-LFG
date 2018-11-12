package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames.Requests.GetClanGameListings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelectClanGameActivity extends AppCompatActivity {

    private static List<ClanGamesList> requestGameList = new ArrayList<>();
    RecyclerView gameRecyclerView;
    public DesignClanGamesListRecyclerAdapter gameRecyclerAdapter;
    private SwipeRefreshLayout swipeContainer;
    private String clanName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //setContentView(R.layout.activity_create_lfg_request);

        setContentView(R.layout.activity_select_clan_game_activity);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeGameSelectContainer);

        //Set the toolbar
        Toolbar gameToolBar = (Toolbar) findViewById(R.id.gameSelectToolbar);
        gameToolBar.setTitle("Select Game");
        setSupportActionBar(gameToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        clanName = getIntent().getStringExtra("clanName");

        gameRecyclerView = (RecyclerView) findViewById(R.id.gameSelectRecycler);

        getGames(clanName);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                getGames(clanName);



                //Log.e("REfreshing", "refreshing");
            }
        });

        EditText searchText = (EditText) findViewById(R.id.etSearchGames);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    void filter(String text){
        List<ClanGamesList> temp = new ArrayList();
        for(ClanGamesList d: requestGameList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getgameName().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }
        //update recyclerview
        gameRecyclerAdapter.updateList(temp);
    }

    //Method used to get the games info from the server
    public void getGames(String clanName){

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
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    //Get the LFG list from the jpresponse json
                    JSONArray jsonArray = jsonResponse.optJSONArray("message");


                    if (success){

                        if (swipeContainer.isRefreshing()){

                            swipeContainer.setRefreshing(false);
                        }

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Cycle through the repsonse list
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int gamesListID = Integer.parseInt(jsonObject.optString("gameID").toString());
                            String gameListName = jsonObject.getString("gameName");
                            String gameClanName = jsonObject.getString("clanName");
                            int requestCount = Integer.parseInt(jsonObject.optString("requestCount").toString());

                            boolean canAddGame = Boolean.parseBoolean(jsonObject.optString("canAddGame").toString());
                            boolean canDeleteGame = Boolean.parseBoolean(jsonObject.optString("canDeleteGame").toString());


                            ClanGamesList request = new ClanGamesList(gamesListID, gameListName, requestCount, canAddGame, canDeleteGame, gameClanName);
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

                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectClanGameActivity.this);
                        builder.setMessage("Failed to find games!")
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

        SharedPreferences sharedPreferences = this.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Get the gamest list from the server
        GetClanGameListings submitGameListRequest = new GetClanGameListings(userID, clanName, authToken, gameListingListener);
        RequestQueue queue = Volley.newRequestQueue(SelectClanGameActivity.this);
        queue.add(submitGameListRequest);

        gameRecyclerAdapter = new DesignClanGamesListRecyclerAdapter(requestGameList);

        //Assign the adapter and set the layout
        gameRecyclerView.setAdapter(gameRecyclerAdapter);
        gameRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        gameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        gameRecyclerView.addItemDecoration(new ClanDividerItemDecoration(this, ClanDividerItemDecoration.VERTICAL_LIST));
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

                Intent data = new Intent();
                data.putExtra("refresh", true);
                setResult(RESULT_OK, data);
                finish();

                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
