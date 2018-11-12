package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameRequestsActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.GamesList;

import java.util.List;

/**
 * Created by jahnplay on 11/13/2016.
 */

public class DesignGamesListRecyclerAdapter extends RecyclerView.Adapter<DesignGamesListRecyclerAdapter.ViewHolder> {

    private List<GamesList> repliesList;

    DesignGamesListRecyclerAdapter(List<GamesList> repliesList) {

        this.repliesList = repliesList;

    }

    public void add(int position, GamesList item) {
        repliesList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(GamesList item) {
        int position = repliesList.indexOf(item);
        repliesList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvGameName, tvTotalRequests;
        public LinearLayout lyGameListLayout;

        ViewHolder(View v) {
            super(v);

            tvGameName = (TextView) v.findViewById(R.id.tvGameListName);
            tvTotalRequests = (TextView) v.findViewById(R.id.tvGameTotalRequests);
            lyGameListLayout = (LinearLayout) v.findViewById(R.id.lyGameListLayout);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activitiy_select_game_list_row, viewGroup, false);

        return new ViewHolder(v);

    }

    //Controls the list item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Context con;

        //Get the reply info
        final GamesList gamesList = repliesList.get(i);
        viewHolder.tvGameName.setText(gamesList.getgameName());
        viewHolder.tvTotalRequests.setText("Total Requests " + gamesList.getRequestCount());

        //start activity to view requests for the game id clicked
        viewHolder.lyGameListLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //start activity to view requests for the game id clicked
                Intent viewGameRequestsIntent = new Intent(v.getContext(), GameRequestsActivity.class);
                viewGameRequestsIntent.putExtra("gameID", gamesList.getgameId());
                viewGameRequestsIntent.putExtra("gameName", gamesList.getgameName());
                v.getContext().startActivity(viewGameRequestsIntent);

            }
        });


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
    public void addAll(List<GamesList> list) {
        repliesList.addAll(list);
        notifyDataSetChanged();
    }

}
