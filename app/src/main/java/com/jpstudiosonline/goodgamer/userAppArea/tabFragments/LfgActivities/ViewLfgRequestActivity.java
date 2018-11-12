package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.UserAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.EditLfgRequestActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.EditReplyActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.ReplyLfgRequestActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.GetLfgRequestReplies;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.RepliesList;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitDeleteLfgRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitEditReplyRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.SubmitLfgReportRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.RecyclerAdapterItems.DividerItemDecoration;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewLfgRequestActivity extends AppCompatActivity {

    public String requestTitle, loggedInUserName, loggedInUserID, lfgRequestPosterUsername, editableRequestDescription, lfgRequestUserID, userTitle, requestUserID;
    public static final String DEFAULT = "N/A";
    public int requestID;
    public FloatingActionButton fabAddReplyButton;
    public DesignLfgRepliesRecyclerAdapter replyRecyclerAdapter;
    public List<RepliesList> requestReplyList = new ArrayList<>();
    RecyclerView replyRecyclerView;
    public Toolbar lfgToolBar;
    public SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeContainer;
    boolean refreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_lfg_request);

        //Get the intent for the request info
        Intent requestIntent = getIntent();
        requestTitle = requestIntent.getStringExtra("lfgRequestTitle");
        requestID = requestIntent.getIntExtra("lfgRequestID", 0);

        //Set the tool bar to the be title of the request
        //Set the toolbar
        lfgToolBar = (Toolbar) findViewById(R.id.viewLfgRequestToolbar);
        lfgToolBar.setTitle(requestTitle);
        setSupportActionBar(lfgToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Set the textviews for the request
        TextView tvLfgRequestTitle = (TextView) findViewById(R.id.tvRequestTitle);
        TextView tvTimeCreated = (TextView) findViewById(R.id.tvLfgRequestCreated);
        TextView tvGroupType= (TextView) findViewById(R.id.tvLfgRequestGroupType);
        TextView tvConsoleType = (TextView) findViewById(R.id.tvLfgRequestConsole);
        TextView tvGameName = (TextView) findViewById(R.id.tvRequestGameName);
        TextView tvRequestVotes = (TextView) findViewById(R.id.tvLfgRequestVotes);
        TextView tvRequestComments = (TextView) findViewById(R.id.tvRequestCommentCount);
        TextView tvLfgPostedBy = (TextView) findViewById(R.id.tvPostedBy);
        TextView tvLfgDescription = (TextView) findViewById(R.id.tvRequestDescription);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeRepliesSelectContainer);

        //Set the texts
        tvLfgRequestTitle.setText(requestTitle);
        tvTimeCreated.setText(requestIntent.getStringExtra("requestTime"));
        tvGroupType.setText(requestIntent.getStringExtra("requestGroup"));
        tvConsoleType.setText(requestIntent.getStringExtra("requestConsole"));
        tvGameName.setText(requestIntent.getStringExtra("gameName"));
        tvRequestVotes.setText(String.valueOf(requestIntent.getIntExtra("totalVotes", 0)));
        tvRequestComments.setText(String.valueOf(requestIntent.getIntExtra("commentCount", 0)));
        tvLfgPostedBy.setText(requestIntent.getStringExtra("requestUser"));
        tvLfgDescription.setText(requestIntent.getStringExtra("requestDescription"));
        replyRecyclerView = (RecyclerView) findViewById(R.id.lfgRequestRecycler);
        replyRecyclerView.setNestedScrollingEnabled(false);

        lfgRequestPosterUsername = requestIntent.getStringExtra("requestUser");
        lfgRequestUserID = String.valueOf(requestIntent.getIntExtra("requestUserID", 0));

        //set the string for description so if user can edit it.
        editableRequestDescription = requestIntent.getStringExtra("requestDescription");

        //Create fab button to allow users to submit a new LFG request
        fabAddReplyButton = (FloatingActionButton) findViewById(R.id.lfgReplyFab);
        fabAddReplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Click action create new window for users to submit a new request
                //open activity to let user enter a LFG request reply
                Intent replyIntent = new Intent(ViewLfgRequestActivity.this, ReplyLfgRequestActivity.class);
                replyIntent.putExtra("postTitle", requestTitle);
                replyIntent.putExtra("requestID", requestID);
                startActivity(replyIntent);

            }
        });

        //Get user ID if user wants to submit the request
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        loggedInUserName = sharedPreferences.getString("loginUsername", DEFAULT);
        loggedInUserID  = sharedPreferences.getString("userid", DEFAULT);
        userTitle  = sharedPreferences.getString("userTitle", DEFAULT);

        //call method to get the replies for the LFG request
        getLfgRequestReplies(false);

    }

    public void getLfgRequestReplies(final boolean deleting){

        //Setup a loading bar for Logging in a user
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Submit request to server upon matching valid user/password length.
        //User info should be saved in savedpreferences.

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait. Getting replies...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Setup listener to get the Login response from the server
        Response.Listener<String> getRepliesResponseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");



                    if (success){

                        //check and see if we are refreshing
                        if (swipeContainer.isRefreshing()){

                            //Cancel the refresh

                            replyRecyclerAdapter.clear();
                            requestReplyList.clear();
                            //replyRecyclerView.removeOnItemTouchListener(disable);     // enable the scrolling
                            swipeContainer.setRefreshing(false);

                            // Now we call setRefreshing(false) to signal refresh has finished


                        } else {


                        }

                        //If we are deleting the post, then let the user know and close the activity since it is no longer valid.
                        if (deleting){

                            Toast.makeText(ViewLfgRequestActivity.this, "Post Deleted", Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            finish();

                            //Start new activity of the dashboard
                            //Intent intent = new Intent(ViewLfgRequestActivity.this, UserAreaActivity.class);
                            //startActivity(intent);

                        } else {

                            //Get the LFG list from the jpresponse json
                            JSONArray jsonArray = jsonResponse.optJSONArray("jpresponse");

                            //Cycle through the json reply
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                int replyID = jsonObject.getInt("replyID");
                                int parentRequestID = jsonObject.getInt("parentRequestID");
                                String replyUserName = jsonObject.getString("replyUserName");
                                String requestReplyComment = jsonObject.getString("requestReplyComment");
                                String timeAdded = jsonObject.getString("timeAdded");
                                int replyUserID = jsonObject.getInt("replyUserID");
                                int replyVotes = jsonObject.getInt("replyVotes");

                                //RepliesList repliesList = new RepliesList(replyUserName, requestReplyComment, replyUserID, timeAdded);
                                RepliesList repliesList = new RepliesList(replyID, parentRequestID, replyUserID, replyVotes, replyUserName, requestReplyComment, timeAdded);
                                requestReplyList.add(repliesList);

                            }

                            progressBar.dismiss();
                            replyRecyclerAdapter.notifyDataSetChanged();

                        }


                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();

                        //check and see if we are refreshing
                        if (swipeContainer.isRefreshing()){

                            //Cancel the refresh
                            swipeContainer.setRefreshing(false);
                            // Now we call setRefreshing(false) to signal refresh has finished


                        } else {


                        }

                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen
                    progressBar.dismiss();

                    e.printStackTrace();

                }

            }

        };

        //Submit the request depending on if we are deleting or not
        //Since we are delting the post submit the delete
        if (deleting){

            //Submit Delete the post request
            SubmitDeleteLfgRequest deleteLfgRequest = new SubmitDeleteLfgRequest("DELETE", lfgRequestUserID, String.valueOf(requestID), getRepliesResponseListener);
            RequestQueue queue = Volley.newRequestQueue(ViewLfgRequestActivity.this);
            queue.add(deleteLfgRequest);

        } else {

            //Get the replies since we are not deleting
            GetLfgRequestReplies getLfgReplies = new GetLfgRequestReplies(String.valueOf(requestID), getRepliesResponseListener);
            RequestQueue queue = Volley.newRequestQueue(ViewLfgRequestActivity.this);
            queue.add(getLfgReplies);

        }



        replyRecyclerAdapter = new DesignLfgRepliesRecyclerAdapter(requestReplyList);

        //Assign the adapter and set the layout
        replyRecyclerView.setAdapter(replyRecyclerAdapter);
        replyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        replyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        replyRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        replyRecyclerView.setItemAnimator(new DefaultItemAnimator());
        replyRecyclerView.setNestedScrollingEnabled(false);


        //Make sure we don't set mulitple listeners if we are refreshing
        if (!refreshing){

            replyRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, replyRecyclerView, new ClickListener() {

                //Click listener for the latest list
                @Override
                public void onClick(final View view, int position) {


                }

                @Override
                public void onLongClick(View view, int position) {

                    //Disable the listener.
                    //RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();
                    //replyRecyclerView.addOnItemTouchListener(disable);        // disables scolling

                    //Log.e("Addinglistener2", "listener added");
                    //Show the edit user menu
                    final RepliesList request = requestReplyList.get(position);

                    //If the logged in user is our reply user show the edit menu and pass params to it
                    if (Integer.parseInt(loggedInUserID) == request.getReplyUserID() || userTitle.equals("ADMIN")){

                        showEditReplyDialog(String.valueOf(request.getReplyID()), String.valueOf(request.getReplyUserID()), String.valueOf(requestID), request.getReplyComment(), true);

                        //Log.e("getReplyID",String.valueOf(request.getReplyID()));
                        //Log.e("getReplyUserID", String.valueOf(request.getReplyUserID()));
                        //Log.e("requestID", String.valueOf(String.valueOf(requestID)));

                    } else {

                        showEditReplyDialog("", String.valueOf(request.getReplyUserID()), "", String.valueOf(requestID), true);
                        //Log.e("getReplyID",String.valueOf(request.getReplyID()));
                        //Log.e("getReplyUserID", String.valueOf(request.getReplyUserID()));
                        //Log.e("requestID", String.valueOf(String.valueOf(requestID)));

                    }


                }
            }));

        }

        //Let the recycler hide the fab button when users scrolls down
        replyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0)
                    fabAddReplyButton.hide();

                else if (dy < 0)
                    fabAddReplyButton.show();

            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // Remember to CLEAR OUT old items before appending in the new ones
                requestReplyList.removeAll(requestReplyList);
                replyRecyclerAdapter.clear();
                requestReplyList.clear();
                replyRecyclerView.setAdapter(null);
                refreshing = true;
                //Disable the listener.
                //RecyclerView.OnItemTouchListener disable = new RecyclerViewTouch();
                //replyRecyclerView.addOnItemTouchListener(disable);        // disables scolling
                getLfgRequestReplies(false);

            }
        });

    }

    //Method used to submit a report againist this request
    public void submitReport(){

        //Setup a loading bar for Logging in a user
        final ProgressDialog progressBar = new ProgressDialog(this);

        //Submit request to server upon matching valid user/password length.
        //User info should be saved in savedpreferences.

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Submitting");
        progressBar.setMessage("Please wait. Getting submitting...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        //Setup listener to get the Login response from the server
        Response.Listener<String> submitReportResponseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");



                    if (success){

                            //Get the LFG list from the jpresponse json
                            JSONArray jsonArray = jsonResponse.optJSONArray("jpresponse");

                            progressBar.dismiss();
                            Toast.makeText(ViewLfgRequestActivity.this, "Report submitted, thanks!", Toast.LENGTH_LONG).show();

                    } else {

                        //Dismiss the loading screen
                        progressBar.dismiss();
                        Toast.makeText(ViewLfgRequestActivity.this, "You have already reported this post!", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {

                    //Dismiss the loading screen
                    progressBar.dismiss();

                    e.printStackTrace();

                }

            }

        };

        //Submit report request with the server
        SubmitLfgReportRequest submitLoginRequest = new SubmitLfgReportRequest(loggedInUserID, String.valueOf(requestID), submitReportResponseListener);
        RequestQueue queue = Volley.newRequestQueue(ViewLfgRequestActivity.this);
        queue.add(submitLoginRequest);

    }

    //Dialog to see if user really wants to delete the request
    private void showDeleteRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLfgRequestActivity.this);
        builder.setTitle("Deleting post.");
        builder.setMessage("Delete this post???  Cannot be undone.");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        //Submit the delete request
                        getLfgRequestReplies(true);

                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    //Dialog to see if user really wants to delete the reply
    private void showDeleteReplyDialog(final String replyID, final String replyUserID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLfgRequestActivity.this);
        builder.setTitle("Deleting reply.");
        builder.setMessage("Delete this reply???  Cannot be undone.");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        //Submit the delete request

                        //Setup a loading bar for Submitting the reply in a user
                        final ProgressDialog progressBar = new ProgressDialog(ViewLfgRequestActivity.this);

                        //Submit request to server upon matching valid user/password length.
                        //User info should be saved in savedpreferences.

                        //Set the loading bar to let user know it's working
                        progressBar.setTitle("Submitting");
                        progressBar.setMessage("Please wait. Submitting...");
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

                                        Toast.makeText(ViewLfgRequestActivity.this, "Reply Deleted", Toast.LENGTH_LONG).show();
                                        progressBar.dismiss();


                                    } else {

                                        //Dismiss the loading screen
                                        progressBar.dismiss();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLfgRequestActivity.this);
                                        builder.setMessage("Failed to Delete, try again.")
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
                        SubmitEditReplyRequest submitRequest = new SubmitEditReplyRequest("HIDE", replyID, String.valueOf(requestID), replyUserID, submitResponseListener);
                        RequestQueue queue = Volley.newRequestQueue(ViewLfgRequestActivity.this);
                        queue.add(submitRequest);


                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    //Dialog to show list of opetions like delete, edit replies or  view user profile
    private void showEditReplyDialog(final String replyID, final String replyUserID, final String requestID, final String replyComment, boolean showProfileEnabled) {

        //Log.e("RefreshingREplies", "resfrhing");
        //check and see if we are refreshing
        if (!swipeContainer.isRefreshing()){

            AlertDialog.Builder builder = new AlertDialog.Builder(ViewLfgRequestActivity.this);
            //Cancel the refresh
            // Now we call setRefreshing(false) to signal refresh has finished

            final CharSequence[] items2 = {"View Profile"};

            //Make sure our user has valid info
            if (replyID.length() != 0 && replyUserID.length() != 0 && replyComment.length() != 0 || userTitle.equals("ADMIN")){

                final CharSequence[] items = {"Edit", "Delete", "View Profile"};

                builder.setTitle("Choose an option").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        String selected = items[item].toString();

                        if (selected.equals("Edit") ){

                            dialog.dismiss();
                            dialog.cancel();

                            //Open up the activity to allow the user to eid the reply
                            Intent editReplyIntent = new Intent(ViewLfgRequestActivity.this, EditReplyActivity.class);
                            editReplyIntent.putExtra("ReplyComments", replyComment);
                            editReplyIntent.putExtra("replyID", String.valueOf(replyID));
                            editReplyIntent.putExtra("replyUserID", String.valueOf(replyUserID));
                            editReplyIntent.putExtra("requestID", String.valueOf(requestID));

                            startActivity(editReplyIntent);

                        } else if (selected.equals("Delete")){

                            dialog.dismiss();
                            dialog.cancel();

                            //Show dialog to see if user wants to delete the reply.
                            showDeleteReplyDialog(String.valueOf(replyID), String.valueOf(replyUserID));

                        } else if (selected.equals("View Profile")){

                            dialog.dismiss();
                            dialog.cancel();

                            //SHow the users profile
                            Intent profileIntent = new Intent(ViewLfgRequestActivity.this, UserProfileActivity.class);
                            profileIntent.putExtra("userID", String.valueOf(replyUserID));
                            startActivity(profileIntent);

                        }

                    }
                });


            } else {

                builder.setTitle("Choose an option").setItems(items2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        String selected = items2[item].toString();

                        if (selected.equals("View Profile")){

                            dialog.dismiss();
                            dialog.cancel();

                            //SHow the users profile
                            Intent profileIntent = new Intent(ViewLfgRequestActivity.this, UserProfileActivity.class);
                            profileIntent.putExtra("userID", String.valueOf(replyUserID));
                            startActivity(profileIntent);

                        }


                    }
                });

            }

            //Possitive button
            String positiveText = "Yes";
            //builder.setPositiveButton(positiveText,
            //        new DialogInterface.OnClickListener() {
            //            @Override
            //            public void onClick(DialogInterface dialog, int which) {
            //                // positive button logic
            //                //Submit the delete request
//
            //            }
            //        });

            String negativeText = getString(android.R.string.cancel);
            builder.setNegativeButton(negativeText,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // negative button logic
                            dialog.dismiss();
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            // display dialog
            dialog.show();


        }

    }

    //Dialog to see if user really wants to report the request
    private void showReportRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewLfgRequestActivity.this);
        builder.setTitle("Report post");
        builder.setMessage("Are you sure you want to report this post?");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        //Submit the delete request
                        submitReport();

                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_lfg_request_menu, menu);

        //Change menu items depending if user can edit the current post
        MenuItem editPostItem = menu.findItem(R.id.action_lfg_edit_post);
        MenuItem deletePostItem = menu.findItem(R.id.action_lfg_delete_post);
        MenuItem profileItem = menu.findItem(R.id.action_lfg_view_profile);
        MenuItem reportItem = menu.findItem(R.id.action_lfg_report_post);

        //setup the listener for the profile button
        profileItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent profileIntent = new Intent(ViewLfgRequestActivity.this, UserProfileActivity.class);
                profileIntent.putExtra("userID", lfgRequestUserID);
                startActivity(profileIntent);
                return false;
            }
        });


        //Make sure our user is the owner of the request then allow the delete/edit buttons to up in the menu
        if (String.valueOf(loggedInUserName).equals(lfgRequestPosterUsername) || userTitle.equals("ADMIN")){

            //Set the buttons visible
            editPostItem.setVisible(true);
            deletePostItem.setVisible(true);

            //Add the edit LFG request button listener
            editPostItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    //When user clicks the edit button open the edit LFG activity
                    Intent editRequestIntent = new Intent(ViewLfgRequestActivity.this, EditLfgRequestActivity.class);
                    editRequestIntent.putExtra("editableRequestDescription", editableRequestDescription);
                    editRequestIntent.putExtra("requestID", requestID);
                    editRequestIntent.putExtra("userID", lfgRequestUserID);
                    startActivity(editRequestIntent);
                    return false;
                }
            });

            //Set the listener for the delete post menu buttom
            deletePostItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    //Show dialog if user really wants to delete the request
                    showDeleteRequestDialog();
                    return false;
                }
            });

        } else {

            editPostItem.setVisible(false);
            deletePostItem.setVisible(false);

        }

        //Setup the report post menu button
        reportItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showReportRequestDialog();
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
        }

        return super.onOptionsItemSelected(item);
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    //Create a touch listener for our replies recycler
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        boolean createdListener = false;

        private GestureDetector gestureDetector;
        private ViewLfgRequestActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ViewLfgRequestActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
                return false;
            }
            return false;
        }

        //Disable refresh and click events at the same time
        @Override
        public void onTouchEvent(RecyclerView recycler, MotionEvent event) {

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow Parent RecyclerView to intercept touch events.
                    recycler.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow Parent RecyclerView to intercept touch events.
                    recycler.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public class RecyclerViewTouch implements RecyclerView.OnItemTouchListener {

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            return true;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}

