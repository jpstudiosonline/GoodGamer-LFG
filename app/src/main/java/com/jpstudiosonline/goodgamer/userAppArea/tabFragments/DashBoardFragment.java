package com.jpstudiosonline.goodgamer.userAppArea.tabFragments;

/**
 * Created by jahnplay on 11/2/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.UserAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.CreateLfgRequestActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GameRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GetLfgRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.RecyclerAdapterItems.DividerItemDecoration;
import com.jpstudiosonline.goodgamer.userLogin.userLoginActivities.LoginActivity;
import com.jpstudiosonline.goodgamer.userLogin.userLoginRequests.SendLoginRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.jpstudiosonline.goodgamer.userLogin.userLoginActivities.LoginActivity.DEFAULT;

public class DashBoardFragment extends Fragment {

    View view;
    public DesignDashboardRecyclerAdapter dashboardRecyclerAdapter;
    public List<GameRequest> dashBoardRequestList = new ArrayList<>();
    public SharedPreferences sharedPreferences;
    public static final String DEFAULT = "N/A";
    String savedUserID;
    ProgressDialog progressBar;
    private SwipeRefreshLayout swipeContainer;
    RecyclerView dashBoardRecyclerView;
    public FloatingActionButton fabAddLfgRequest;

    //This is the main fragment for once the user logs in, this will show the latest game requests
    public DashBoardFragment() {


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        progressBar = new ProgressDialog(this.getContext());
        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait. While loading...");
        progressBar.setCancelable(true); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Get the user ID that will be doing viewing the LFG requests from the server
        sharedPreferences = this.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        //If we have user info get the user ID
        if (sharedPreferences.contains("loginUsername") && sharedPreferences.contains("loginPassword") && sharedPreferences.contains("userid")) {

            savedUserID = sharedPreferences.getString("userid", DEFAULT);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        //Get the Recycler view and set  the game requests as a list adapter
        dashBoardRecyclerView = (RecyclerView) view.findViewById(R.id.dashBoardRecycler);

        //Call the method to get the latest LFG requests
        getLfgRequests();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                getLfgRequests();



                //Log.e("REfreshing", "refreshing");
            }
        });


        //Create fab button to allow users to submit a new LFG request
        fabAddLfgRequest = (FloatingActionButton) view.findViewById(R.id.fab);
        fabAddLfgRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click action create new window for users to submit a new request
                Intent createLfgRequestIntent = new Intent(getContext(), CreateLfgRequestActivity.class);
                startActivity(createLfgRequestIntent);
            }
        });

        return view;
    }

    public void getLfgRequests(){

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

                        //check and see if we are refreshing
                        if (swipeContainer.isRefreshing()){

                            //Cancel the refresh
                            swipeContainer.setRefreshing(false);
                            // Now we call setRefreshing(false) to signal refresh has finished
                            clear();


                        } else {


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
                            dashBoardRequestList.add(request);

                            //Dismiss the loading screen
                            progressBar.dismiss();
                        }

                        if (swipeContainer.isRefreshing()){

                            addAll(dashBoardRequestList);

                        } else {

                            //Let the recycler know that we have the data recieved upon start up of activity.
                            dashboardRecyclerAdapter.notifyDataSetChanged();

                        }

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("Failed to login, try again.")
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

        //Make sure we don't have a null userID
        if (savedUserID != null && savedUserID != DEFAULT) {

            //Since we are going to be using the requester ID to get votes, call the server from the user info saved in the preferences
            //Submit LFG request request with the server
            GetLfgRequest submitLfgRequest = new GetLfgRequest(savedUserID, lfgRequestsResponseListener);
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(submitLfgRequest);

            //Create the List adapter after we get our response from the server
            dashboardRecyclerAdapter = new DesignDashboardRecyclerAdapter(dashBoardRequestList);

            //Assign the adapter and set the layout
            dashBoardRecyclerView.setAdapter(dashboardRecyclerAdapter);
            dashBoardRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            dashBoardRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
            dashBoardRecyclerView.setItemAnimator(new DefaultItemAnimator());

            //Let the recycler hide the fab button when users scrolls down
            dashBoardRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                    if (dy > 0)
                        fabAddLfgRequest.hide();
                    else if (dy < 0)
                        fabAddLfgRequest.show();
                }
            });


        } else {


        }

    }

    // Clean all elements of the recycler
    public void clear() {
        dashBoardRequestList.clear();
        dashboardRecyclerAdapter.notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<GameRequest> list) {
        dashBoardRequestList.addAll(list);
        dashboardRecyclerAdapter.notifyDataSetChanged();
    }

}