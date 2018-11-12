package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.GamesList;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.GetLfgRequestListings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class SelectGameListActtivity extends AppCompatActivity {

    ListView gameListView;

    private static List<GamesList> requestGameList = new ArrayList<>();
    public ArrayAdapter<String> gameNameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Set out view then submit request to get the latest console list
        setContentView(R.layout.activity_lfg_select_game_list);
        gameListView = (ListView) findViewById(R.id.listView);

        //Set the toolbar
        Toolbar lfgToolBar = (Toolbar) findViewById(R.id.lfgSelectGameToolbar);
        lfgToolBar.setTitle("Select Game");
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Invoke method to get the listings
        getListing();
    }

    public void getListing(){

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

                        for (int i = 0; i < jsonArray.length(); i++) {

                            //Cycle through the repsonse list
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            int gamesListID = Integer.parseInt(jsonObject.optString("gameID").toString());
                            String gameListName = jsonObject.getString("gameName");


                            GamesList request = new GamesList(gamesListID, gameListName);
                            requestGameList.add(request);

                        }

                        //Call the setup UI method to setup the listings
                        setupUI();

                        //Dismiss the loading screen
                        progressBar.dismiss();


                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectGameListActtivity.this);
                        builder.setMessage("Failed to get games, try again.")
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

        //Since we are going to be using the autologin, call the server from the user info saved in the preferences
        //Submit login request with the server
        GetLfgRequestListings submitGameListRequest = new GetLfgRequestListings("GAME", gameListingListener);
        RequestQueue queue = Volley.newRequestQueue(SelectGameListActtivity.this);
        queue.add(submitGameListRequest);


    }


    //Setup the list UI after getting the response
    public void setupUI(){

        //Cycle through the server response and add it to string array to create the listings
        String[] mStringArray = new String[requestGameList.size()];
        for(int i = 0; i < requestGameList.size(); i++){
            mStringArray[i] = requestGameList.get(i).getgameName().toString();

        }

        //Set the listings adapter
        gameListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mStringArray));
        gameListView.setOnItemClickListener(new returnClickedItem());

    }


    class returnClickedItem implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //String item = (String) parent.getItemAtPosition(position);
            String item = (String) parent.getItemAtPosition(position);
            Intent data = new Intent();
            data.putExtra("GameName", item);
            data.putExtra("GameID",requestGameList.get(position).getgameId());
            setResult(RESULT_OK, data);
            finish();
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
