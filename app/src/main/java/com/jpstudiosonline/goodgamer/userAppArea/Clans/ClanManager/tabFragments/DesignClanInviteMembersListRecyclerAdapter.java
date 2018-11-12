package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanGames.Requests.SubmitClanGameChanges;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.FragmentRequests.UpdateClanInviteMembersRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by jahnplay on 11/13/2016.
 */

public class DesignClanInviteMembersListRecyclerAdapter extends RecyclerView.Adapter<DesignClanInviteMembersListRecyclerAdapter.ViewHolder> {

    private List<ClanManageMembersList> repliesList;

    DesignClanInviteMembersListRecyclerAdapter(List<ClanManageMembersList> repliesList) {

        this.repliesList = repliesList;

    }

    public void add(int position, ClanManageMembersList item) {
        repliesList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(ClanManageMembersList item) {
        int position = repliesList.indexOf(item);
        repliesList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvUserName, tvMessageFromuser;
        public LinearLayout lyGameListLayout;
        public ImageView ivApproveUser, ivRemoveUser, ivMakeUserAdmin, ivRemoveUserAdmin;

        ViewHolder(View v) {
            super(v);

            tvUserName = (TextView) v.findViewById(R.id.tvUserName);
            lyGameListLayout = (LinearLayout) v.findViewById(R.id.lyGameListLayout);
            ivApproveUser = (ImageView) v.findViewById(R.id.ivApproveUser);
            ivRemoveUser = (ImageView) v.findViewById(R.id.ivRemoveUser);
            tvMessageFromuser = (TextView) v.findViewById(R.id.tvMessageFromuser);
            ivMakeUserAdmin = (ImageView) v.findViewById(R.id.ivMakeUserAdmin);
            ivRemoveUserAdmin = (ImageView) v.findViewById(R.id.ivRemoveUserAdmin);



        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_manage_clanmember_list_row, viewGroup, false);

        return new ViewHolder(v);

    }

    //Controls the list item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        Context con;

        //Get the reply info
        final ClanManageMembersList membersList = repliesList.get(i);
        viewHolder.tvUserName.setText(membersList.getuserName());

        boolean canApproveUser = membersList.getcanApproveUser();
        boolean canDeleteMember = membersList.getcanRemoveUser();

        if (canDeleteMember && !canApproveUser){

            viewHolder.ivRemoveUser.setVisibility(View.VISIBLE);
            viewHolder.ivApproveUser.setVisibility(View.GONE);
            viewHolder.tvMessageFromuser.setVisibility(View.GONE);

        } else if (canApproveUser && !canDeleteMember){

            viewHolder.ivRemoveUser.setVisibility(View.GONE);
            viewHolder.tvMessageFromuser.setVisibility(View.GONE);
            viewHolder.ivApproveUser.setVisibility(View.VISIBLE);

        } else if (canApproveUser && canDeleteMember) {

            viewHolder.ivRemoveUser.setVisibility(View.VISIBLE);
            viewHolder.ivApproveUser.setVisibility(View.VISIBLE);
            viewHolder.tvMessageFromuser.setVisibility(View.VISIBLE);
            viewHolder.tvMessageFromuser.setText(membersList.getClanName());


        } else {

            viewHolder.ivRemoveUser.setVisibility(View.GONE);
            viewHolder.ivApproveUser.setVisibility(View.GONE);
            viewHolder.tvMessageFromuser.setVisibility(View.GONE);

        }

        if (membersList.getIsUserAdmin()){

            viewHolder.ivMakeUserAdmin.setVisibility(View.GONE);
            viewHolder.ivRemoveUserAdmin.setVisibility(View.GONE);
        } else {

            viewHolder.ivMakeUserAdmin.setVisibility(View.GONE);
            viewHolder.ivRemoveUserAdmin.setVisibility(View.GONE);
        }

        //start activity to view requests for the game id clicked
        viewHolder.ivApproveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewHolder.ivApproveUser.setVisibility(View.GONE);
                viewHolder.ivRemoveUser.setVisibility(View.GONE);
                Log.e("EE", String.valueOf(membersList.getUserID()));
                Log.e("EE", String.valueOf(membersList.getclanID()));
                sendClanInviteChange(v, String.valueOf(membersList.getclanID()), membersList.getClanName(), "APPROVE", String.valueOf(membersList.getUserID()));

                //showDeleteRequestDialog(v, viewHolder, String.valueOf(membersList.getclanID()), membersList.getClanName(), "APPROVE", String.valueOf(membersList.getUserID()));

                //viewHolder.ivAddClanGame.setVisibility(View.GONE);
                //viewHolder.ivRemoveClanGame.setVisibility(View.VISIBLE);
                //start activity to view requests for the game id clicked
                //Intent viewGameRequestsIntent = new Intent(v.getContext(), GameRequestsActivity.class);
                //viewGameRequestsIntent.putExtra("gameID", membersList.getgameId());
                //viewGameRequestsIntent.putExtra("gameName", membersList.getgameName());
                //v.getContext().startActivity(viewGameRequestsIntent);

            }
        });

        //start activity to view requests for the game id clicked
        viewHolder.ivRemoveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //viewHolder.ivApproveUser.setVisibility(View.GONE);
                //viewHolder.ivRemoveUser.setVisibility(View.GONE);

                //sendClanInviteChange(v, String.valueOf(membersList.getclanID()), membersList.getClanName(), "REMOVE", String.valueOf(membersList.getUserID()));

                showDeleteRequestDialog(v, viewHolder, String.valueOf(membersList.getclanID()), membersList.getClanName(), "REMOVE", String.valueOf(membersList.getUserID()));

                //viewHolder.ivAddClanGame.setVisibility(View.VISIBLE);
                //viewHolder.ivRemoveClanGame.setVisibility(View.GONE);
                //start activity to view requests for the game id clicked
                //Intent viewGameRequestsIntent = new Intent(v.getContext(), GameRequestsActivity.class);
                //viewGameRequestsIntent.putExtra("gameID", membersList.getgameId());
                //viewGameRequestsIntent.putExtra("gameName", membersList.getgameName());
                //v.getContext().startActivity(viewGameRequestsIntent);

            }
        });


    }

    public void sendClanInviteChange(final View view, final String clanID, final String clanName, final String changeType, final String userTobeApprovedID){

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

                    String message = jsonResponse.getString("message");

                    if (success){

                        Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                        //view.findViewById(R.id.btRequestUpVoteButton).setBackgroundResource(R.drawable.ic_thumb_up_green_18dp);


                    } else {

                        Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


        };

        UpdateClanInviteMembersRequest getClanList = new UpdateClanInviteMembersRequest(userid, clanID, clanName, authToken, changeType, userTobeApprovedID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(view.getContext());
        queue.add(getClanList);
    }



    //Dialog to see if user really wants to delete the request
    private void showDeleteRequestDialog(final View view, final ViewHolder holder, final String clanID, final String clanName, final String method, final String userID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Rejecting member.");
        builder.setMessage("Reject this clan application?  Cannot be undone.");

        String positiveText = "Yes";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        //Submit the delete request
                        holder.ivApproveUser.setVisibility(View.GONE);
                        holder.ivRemoveUser.setVisibility(View.GONE);
                        sendClanInviteChange(view, clanID, clanName, method, userID);

                    }
                });

        String negativeText = "Cancel";
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
    public int getItemCount() {

        return repliesList.size();

    }
    // Clean all elements of the recycler
    public void clear() {
        repliesList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<ClanManageMembersList> list) {
        repliesList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateList(List<ClanManageMembersList> list){
        repliesList = list;
        notifyDataSetChanged();
    }

}
