package com.jpstudiosonline.goodgamer.userAppArea.tabFragments;

/**
 * Created by jahnplay on 11/3/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.ViewLfgRequestActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.GameRequest;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgRequests.SubmitLfgVote;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class DesignDashboardRecyclerAdapter extends RecyclerView.Adapter<DesignDashboardRecyclerAdapter.ViewHolder> {

    private List<GameRequest> requestList;
    private boolean loading;
    public String votes = "0";
    public boolean selectedUpVote, selectedDownVote;

    DesignDashboardRecyclerAdapter(List<GameRequest> requestList) {

        this.requestList = requestList;

    }

    public void add(int position, GameRequest item) {
        requestList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(GameRequest item) {
        int position = requestList.indexOf(item);
        requestList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //private final TextView mTextView;
        public TextView requestTitle, requestGroup, requestUserName, requestDescription, requestTotalVotes, gameName, timeLayout, requestCommentCount;
        public final Button upVoteButton, downVoteButton;
        public final LinearLayout upVoteLayout, downVoteLayout, requestLayout;

        ViewHolder(View v) {
            super(v);

            //Get the UI info for setting up the game requests
            requestTitle = (TextView)v.findViewById(R.id.tvRequestTitle);
            requestGroup = (TextView) v.findViewById(R.id.tvRequestGroup);
            requestUserName = (TextView) v.findViewById(R.id.tvRequestUsername);
            requestDescription = (TextView) v.findViewById(R.id.tvRequestDescription);
            gameName = (TextView) v.findViewById(R.id.tvRequestGameName);
            timeLayout = (TextView) v.findViewById(R.id.requestTime);

            //Get the votes UI
            requestTotalVotes = (TextView) v.findViewById(R.id.tvGroupFindingTotalVotes);
            requestCommentCount = (TextView) v.findViewById(R.id.tvRequestCommentCount);

            //Up vote UI
            requestLayout = (LinearLayout) v.findViewById(R.id.groupRequestLayout);
            upVoteButton = (Button) v.findViewById(R.id.btRequestUpVoteButton);
            upVoteLayout = (LinearLayout) v.findViewById(R.id.groupUpVoteLayout);

            //DownVote UI
            downVoteLayout = (LinearLayout) v.findViewById(R.id.groupDownVoteLayout);
            downVoteButton = (Button) v.findViewById(R.id.btRequestDownVoteButton);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashboard_list_row, viewGroup, false);

        return new ViewHolder(v);
    }

    //Controls the list item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Context con;

        //Get the request info
        final GameRequest request = requestList.get(i);
        viewHolder.requestTitle.setText(request.getTitle());
        viewHolder.requestGroup.setText(request.getRequestGroup() + ":" + request.getRequestConsole());

        viewHolder.requestUserName.setText("By: " + request.getRequestUserName());
        viewHolder.requestDescription.setText(request.getRequestDescription());
        viewHolder.gameName.setText(request.getGameName());
        viewHolder.timeLayout.setText(request.getTime());
        viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));
        viewHolder.requestCommentCount.setText("Total Replies: " + String.valueOf(request.getCommentCount()));

        final String currentVotesTotal = String.valueOf(request.getTotalVotes());
        viewHolder.upVoteButton.setBackgroundResource(0);
        viewHolder.downVoteButton.setBackgroundResource(0);
        viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_black_18dp);
        viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_black_18dp);

        switch (request.getUserVote()){
            case "NOTHING":
                break;
            case "UP":
                viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);
                break;
            case "DOWN":
                viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);
                break;
        }


        //Set a clicker for the username to open the user profile activity
        viewHolder.requestUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Setup the intent to open a new activity
                Intent profileIntent = new Intent(v.getContext(), UserProfileActivity.class);
                profileIntent.putExtra("userID", String.valueOf(request.getUserID()));
                v.getContext().startActivity(profileIntent);
                //Log.e("userID",  String.valueOf(request.getUserID()));

            }
        });

        //Setup click listner for the upvote button
        viewHolder.upVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (request.getUserVote().equals("NOTHING")){

                    request.setUserVote("UP");
                    //Toast.makeText(v.getContext(),"Upvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);

                    //Send the upvote request
                    submitVote(v, "UP", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes + 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else if (request.getUserVote().equals("DOWN")){

                    request.setUserVote("UP");
                    //Toast.makeText(v.getContext(),"Upvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_black_18dp);
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);

                    //Send the upvote request
                    submitVote(v, "UP", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes + 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else {}

            }
        });

        //Setup click listner for the upvote layout
        viewHolder.upVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (request.getUserVote().equals("NOTHING")){

                    request.setUserVote("UP");
                    //Toast.makeText(v.getContext(),"Upvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);

                    //Send the upvote request
                    submitVote(v, "UP", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes + 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else if (request.getUserVote().equals("DOWN")){

                    request.setUserVote("UP");
                    //Toast.makeText(v.getContext(),"Upvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_black_18dp);
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);

                    //Send the upvote request
                    submitVote(v, "UP", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes + 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else {}

            }
        });

        //Setup click listner for the DownVote button
        viewHolder.downVoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (request.getUserVote().equals("NOTHING")){

                    request.setUserVote("DOWN");
                    //Toast.makeText(v.getContext(),"Downvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);

                    //Send the downvote request
                    submitVote(v, "DOWN", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes - 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else if (request.getUserVote().equals("UP")){

                    request.setUserVote("DOWN");
                    //Toast.makeText(v.getContext(),"Downvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_black_18dp);

                    //Send the downvote request
                    submitVote(v, "DOWN", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes - 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else {}

            }
        });

        //Setup click listner for the downvote layout
        viewHolder.downVoteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (request.getUserVote().equals("NOTHING")){

                    request.setUserVote("DOWN");
                    //Toast.makeText(v.getContext(),"Downvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);

                    //Send the downvote request
                    submitVote(v, "DOWN", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes - 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else if (request.getUserVote().equals("UP")){

                    request.setUserVote("DOWN");
                    //Toast.makeText(v.getContext(),"Downvote" + request.getGameName(), Toast.LENGTH_SHORT).show();
                    viewHolder.downVoteButton.setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);
                    viewHolder.upVoteButton.setBackgroundResource(R.drawable.ic_thumb_up_black_18dp);

                    //Send the downvote request
                    submitVote(v, "DOWN", String.valueOf(request.getRequestID()));

                    int updatedVotes = Integer.parseInt(String.valueOf(request.getTotalVotes()));
                    updatedVotes = updatedVotes - 1;
                    request.setTotalVotes(updatedVotes);
                    viewHolder.requestTotalVotes.setText(String.valueOf(request.getTotalVotes()));

                } else {}

            }
        });

        //Setup click listener for the request layout which will open the LFG request that was clicked
        viewHolder.requestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ViewLfgRequestActivity.class);
                intent.putExtra("lfgRequestID", request.getRequestID());
                intent.putExtra("lfgRequestTitle", request.getTitle());
                intent.putExtra("gameName", request.getGameName());
                intent.putExtra("requestUser", request.getRequestUserName());
                intent.putExtra("requestConsole", request.getRequestConsole());
                intent.putExtra("requestDescription", request.getRequestDescription());
                intent.putExtra("requestGroup", request.getRequestGroup());
                intent.putExtra("requestTime", request.getTime());
                intent.putExtra("totalVotes", request.getTotalVotes());
                intent.putExtra("commentCount", request.getCommentCount());
                intent.putExtra("requestUserID", request.getUserID());
                v.getContext().startActivity(intent);

            }
        });

        ////Sets listener for this requestTitle textview
        //viewHolder.requestTitle.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Context context = view.getContext();
        //        //context.startActivity(new Intent(context, UserAreaActivity.class));
        //        //Toast.makeText(context, "Button: " + i, Toast.LENGTH_SHORT).show();
        //    }
        //});
    }

    public void submitVote(final View view, final String voteMethod, final String requestID){

        //Preferences
        SharedPreferences userSharedPreferences = view.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        //String prefUserName = userSharedPreferences.getString("username", "");
        String prefUserID = userSharedPreferences.getString("userid", "N/A");

        //Setup listener to picku response
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    if (success && voteMethod == "UP"){

                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);


                    } else if (success && voteMethod == "DOWN") {

                        //view.findViewById(R.id.btRequestDownVoteButton).setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        SubmitLfgVote submitVoteRequest = new SubmitLfgVote(voteMethod, requestID, prefUserID,responseListener);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        queue.add(submitVoteRequest);
    }

    @Override
    public int getItemCount() {

        return requestList.size();

    }
    // Clean all elements of the recycler
    public void clear() {
        requestList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<GameRequest> list) {
        requestList.addAll(list);
        notifyDataSetChanged();
    }




}
