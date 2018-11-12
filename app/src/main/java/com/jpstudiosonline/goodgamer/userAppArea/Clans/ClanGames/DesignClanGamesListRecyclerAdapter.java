package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.data.DataHolder;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames.Requests.SubmitClanGameChanges;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jahnplay on 11/13/2016.
 */

public class DesignClanGamesListRecyclerAdapter extends RecyclerView.Adapter<DesignClanGamesListRecyclerAdapter.ViewHolder> {

    private List<ClanGamesList> repliesList;

    DesignClanGamesListRecyclerAdapter(List<ClanGamesList> repliesList) {

        this.repliesList = repliesList;

    }

    public void add(int position, ClanGamesList item) {
        repliesList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(ClanGamesList item) {
        int position = repliesList.indexOf(item);
        repliesList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvGameName;
        public LinearLayout lyGameListLayout;
        public ImageView ivAddClanGame, ivRemoveClanGame;

        ViewHolder(View v) {
            super(v);

            tvGameName = (TextView) v.findViewById(R.id.tvGameListName);
            lyGameListLayout = (LinearLayout) v.findViewById(R.id.lyGameListLayout);
            ivAddClanGame = (ImageView) v.findViewById(R.id.ivAddClanGame);
            ivRemoveClanGame = (ImageView) v.findViewById(R.id.ivRemoveClanGame);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_select_clangame_list_row, viewGroup, false);

        return new ViewHolder(v);

    }

    //Controls the list item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Context con;

        //Get the reply info
        final ClanGamesList gamesList = repliesList.get(i);
        viewHolder.tvGameName.setText(gamesList.getgameName());

        boolean canAddGame = gamesList.getCanAddGame();
        boolean canDeleteGame = gamesList.getCanDeleteGame();

        if (canAddGame){

            viewHolder.ivRemoveClanGame.setVisibility(View.GONE);
            viewHolder.ivAddClanGame.setVisibility(View.VISIBLE);

        } else {

            viewHolder.ivRemoveClanGame.setVisibility(View.VISIBLE);
            viewHolder.ivAddClanGame.setVisibility(View.GONE);

        }

        //start activity to view requests for the game id clicked
        viewHolder.ivAddClanGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //viewHolder.ivAddClanGame.setVisibility(View.GONE);
                //viewHolder.ivRemoveClanGame.setVisibility(View.VISIBLE);
                //start activity to view requests for the game id clicked
                //Intent viewGameRequestsIntent = new Intent(v.getContext(), GameRequestsActivity.class);
                //viewGameRequestsIntent.putExtra("gameID", gamesList.getgameId());
                //viewGameRequestsIntent.putExtra("gameName", gamesList.getgameName());
                //v.getContext().startActivity(viewGameRequestsIntent);
                sendClanGameChange(v, gamesList.getClanName(), "ADD", gamesList.getgameName());
                viewHolder.ivAddClanGame.setVisibility(View.GONE);
                viewHolder.ivRemoveClanGame.setVisibility(View.VISIBLE);

            }
        });

        //start activity to view requests for the game id clicked
        viewHolder.ivRemoveClanGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendClanGameChange(v, gamesList.getClanName(), "REMOVE", gamesList.getgameName());
                viewHolder.ivAddClanGame.setVisibility(View.VISIBLE);
                viewHolder.ivRemoveClanGame.setVisibility(View.GONE);

                //viewHolder.ivAddClanGame.setVisibility(View.VISIBLE);
                //viewHolder.ivRemoveClanGame.setVisibility(View.GONE);
                //start activity to view requests for the game id clicked
                //Intent viewGameRequestsIntent = new Intent(v.getContext(), GameRequestsActivity.class);
                //viewGameRequestsIntent.putExtra("gameID", gamesList.getgameId());
                //viewGameRequestsIntent.putExtra("gameName", gamesList.getgameName());
                //v.getContext().startActivity(viewGameRequestsIntent);

            }
        });


    }

    public void sendClanGameChange(final View view, final String clanName, final String changeType, final String gameName){

        //Preferences
        SharedPreferences userSharedPreferences = view.getContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String userid = userSharedPreferences.getString("userid", "N/A");
        String authToken = userSharedPreferences.getString("userHash", "N/A");

        //Setup listener to picku response
        Response.Listener<String> responseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("jpresponse");

                    if (success && changeType == "ADD"){

                        Toast.makeText(view.getContext(), gameName +" Game has been added successfully.", Toast.LENGTH_LONG).show();
                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);


                    } else if (success && changeType == "REMOVE") {

                        Toast.makeText(view.getContext(), gameName + " Game has been removed successfully.", Toast.LENGTH_LONG).show();
                        //view.findViewById(R.id.btRequestDownVoteButton).setBackgroundResource(R.drawable.ic_thumb_down_red_18dp);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        SubmitClanGameChanges getClanList = new SubmitClanGameChanges(userid, clanName, authToken, changeType, gameName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        queue.add(getClanList);
    }

    @Override
    public int getItemCount() {

        return repliesList.size();

    }
    // Clean all elements of the recycler
    public void clear() {
        repliesList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<ClanGamesList> list) {
        repliesList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<ClanGamesList> list){
        repliesList = list;
        notifyDataSetChanged();
    }

}
