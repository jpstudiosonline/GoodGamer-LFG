package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.LfgActivities.SelectionsActivities.SelectionsRequests.RepliesList;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserProfileActivity;

import java.util.List;

/**
 * Created by jahnplay on 11/13/2016.
 */

public class DesignLfgRepliesRecyclerAdapter extends RecyclerView.Adapter<DesignLfgRepliesRecyclerAdapter.ViewHolder> {

    private List<RepliesList> repliesList;
    public SharedPreferences sharedPreferences;
    public static final String DEFAULT = "N/A";
    public String savedUserID, userTitle, replyID;

    DesignLfgRepliesRecyclerAdapter(List<RepliesList> repliesList) {

        this.repliesList = repliesList;


    }

    public void add(int position, RepliesList item) {
        repliesList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(RepliesList item) {
        int position = repliesList.indexOf(item);
        repliesList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvReplyUserName, tvReplyCreatedDate, tvReplyComment;

        ViewHolder(View v) {
            super(v);

            tvReplyUserName = (TextView) v.findViewById(R.id.tvReplyUserName);
            tvReplyCreatedDate = (TextView) v.findViewById(R.id.tvReplyCreatedDate);
            tvReplyComment = (TextView) v.findViewById(R.id.tvReplyComment);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_view_lfg_request_reply_list_row, viewGroup, false);
        return new ViewHolder(v);

    }

    //Controls the list item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Context con;

        //Get the reply info
        final RepliesList reply = repliesList.get(i);
        viewHolder.tvReplyUserName.setText("By: " + reply.getReplyUserName());
        viewHolder.tvReplyCreatedDate.setText("Posted: " + reply.getReplyCreatedTime());
        viewHolder.tvReplyComment.setText(reply.getReplyComment());

        viewHolder.tvReplyUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Start profile activity since the user clicked on the username
                Intent profileIntent = new Intent(v.getContext(), UserProfileActivity.class);
                profileIntent.putExtra("userID", String.valueOf(reply.getReplyUserID()));
                v.getContext().startActivity(profileIntent);

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
    public void addAll(List<RepliesList> list) {
        repliesList.addAll(list);
        notifyDataSetChanged();
    }

}
