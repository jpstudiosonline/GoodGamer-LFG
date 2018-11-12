package com.jpstudiosonline.goodgamer.userAppArea.Clans;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.ClanManagerAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClanChatFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClanMembersListFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansApplyFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansDetailsFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansGamesPlayedFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansLogHistoryFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.FragmentRequests.GetViewClanRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ViewClanAreaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean canUserAdmin, canUserApplyToClan, userIsMember;
    private String clanName, viewClanDescription, serverSideClanID;
    public Menu mainMenu;
    private TextView tvTotalMembers, tvMessageOfTheDay, tvUserClanApplicationStatus;
    private ImageView ivClanLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set toolbar and window seetings
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_view_clan_area);

        //Get intent values
        clanName = getIntent().getStringExtra("clanName");

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.viewClansAreaToolbar);
        toolbar.setTitle(clanName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get our clan UI stats and set them.
        tvTotalMembers = (TextView) findViewById(R.id.tvTotalMembers);
        tvMessageOfTheDay = (TextView) findViewById(R.id.tvMessageOfTheDay);
        ivClanLogo = (ImageView) findViewById(R.id.ivClanLogo);
        tvUserClanApplicationStatus = (TextView) findViewById(R.id.tvUserClanApplicationStatus);

        //Get user info before getting clan info

        getClanInfo();
    }

    public void getClanInfo(){

        final ProgressDialog progressBar = new ProgressDialog(this);

        //Set the loading bar to let user know it's working
        progressBar.setTitle("Loading");
        progressBar.setMessage("Please wait...");
        progressBar.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progressBar.show();

        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String loginUsername = sharedPreferences.getString("loginUsername", "N/A");
        String userid = sharedPreferences.getString("userid", "N/A");
        String authToken = sharedPreferences.getString("userHash", "N/A");

        //Get clan info from server
        //Setup listener to get the Login response from the server
        Response.Listener<String> getClanInfoListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    if (success){

                        String clanID = jsonResponse.getString("clanID");
                        String clanName = jsonResponse.getString("clanName");
                        String totalMemberCount = jsonResponse.getString("totalMemberCount");
                        String clanDescription = jsonResponse.getString("clanDescription");
                        String logo = jsonResponse.getString("logo");
                        String clanStatus = jsonResponse.getString("clanStatus");
                        String messageOfTheDay = jsonResponse.getString("messageOfTheDay");
                        String clanOwner = jsonResponse.getString("clanOwner");
                        String canAdmin = jsonResponse.getString("canAdmin");
                        String canApplyToClan = jsonResponse.getString("canApplyToClan");
                        String isMember = jsonResponse.getString("isMember");
                        String clanApplicationStatus = jsonResponse.getString("clanApplicationStatus");

                        serverSideClanID = clanID;

                        canUserAdmin = Boolean.valueOf(canAdmin);
                        canUserApplyToClan = Boolean.valueOf(canApplyToClan);
                        userIsMember = Boolean.valueOf(isMember);
                        viewClanDescription = clanDescription;



                        tvTotalMembers.setText("Total Members: " + totalMemberCount);
                        tvUserClanApplicationStatus.setText("My Status: " + clanApplicationStatus);

                        URL logoURL = ConvertToUrl(logo);

                        ivClanLogo.setImageBitmap(getRemoteImage(logoURL));

                        if (messageOfTheDay == "null"){

                            tvMessageOfTheDay.setText("Message of the day is empty!");

                        } else {

                            tvMessageOfTheDay.setText(messageOfTheDay);

                        }

                        progressBar.dismiss();

                        //Need to setup the tabs here as if it loads before data, then we can't create new tabs
                        setupTabs();


                    } else {

                        //Log.e("Token failed", token);
                        AlertDialog.Builder builder = new AlertDialog.Builder(ViewClanAreaActivity.this);
                        builder.setMessage("Failed to find Clan info.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();

                        finish();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    progressBar.dismiss();
                    finish();

                }

            }

        };

        //Get info from server
        GetViewClanRequest getViewClanRequest = new GetViewClanRequest(clanName, loginUsername, userid, authToken, getClanInfoListener);
        RequestQueue queue = Volley.newRequestQueue(ViewClanAreaActivity.this);
        queue.add(getViewClanRequest);
    }


    //Method to create activity tabs
    public void setupTabs(){

        //Page viewer for tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.clansAreaTabs);
        tabLayout.setupWithViewPager(viewPager);

        if (canUserAdmin){

            MenuItem adminMenu = mainMenu.findItem(R.id.action_clanadmin_settings);
            adminMenu.setVisible(true);
        }
    }

    //Converts string to URL
    private URL ConvertToUrl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                    url.getHost(), url.getPort(), url.getPath(),
                    url.getQuery(), url.getRef());
            url = uri.toURL();
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Gets image from URL
    public Bitmap getRemoteImage(final URL aURL) {
        try {
            final URLConnection conn = aURL.openConnection();
            conn.connect();
            final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            final Bitmap bm = BitmapFactory.decodeStream(bis);
            bis.close();
            return bm;
        } catch (IOException e) {}
        return null;
    }


    //Tab fragments
    private void setupViewPager(ViewPager viewPager) {

        Bundle bundle = new Bundle();
        bundle.putString("clanName", clanName);
        bundle.putString("clanDescription", viewClanDescription);
        bundle.putBoolean("canUserAdmin", canUserAdmin);
        bundle.putString("serverSideClanID", serverSideClanID);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Pass the clan name to fargment
        ClansApplyFragment clansApplyFragment = new ClansApplyFragment();
        clansApplyFragment.setArguments(bundle);

        //If our user is not a member show the apply screen
        if (!userIsMember && canUserApplyToClan){

            adapter.addFragment(clansApplyFragment, "Apply");

        }

        if (userIsMember){

            //Open clan chat fragment
            ClanChatFragment clanChatFragment = new ClanChatFragment();
            clanChatFragment.setArguments(bundle);
            adapter.addFragment(clanChatFragment, "Chat");

        }

        ClansDetailsFragment clansDetailsFragment = new ClansDetailsFragment();
        clansDetailsFragment.setArguments(bundle);

        adapter.addFragment(clansDetailsFragment, "Info");

        ClansGamesPlayedFragment clansGamesPlayedFragment = new ClansGamesPlayedFragment();
        clansGamesPlayedFragment.setArguments(bundle);

        adapter.addFragment(clansGamesPlayedFragment, "Games");

        if (userIsMember){

            //Pass the clan name to fargment
            ClanMembersListFragment clanMembersListFragment = new ClanMembersListFragment();
            clanMembersListFragment.setArguments(bundle);

            ClansLogHistoryFragment clansLogHistoryFragment = new ClansLogHistoryFragment();
            clansLogHistoryFragment.setArguments(bundle);

            adapter.addFragment(clanMembersListFragment, "Members");
            adapter.addFragment(clansLogHistoryFragment, "Log");

        }
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_clan_menu, menu);

        mainMenu = menu;
        if (!canUserAdmin){

            MenuItem adminMenu = menu.findItem(R.id.action_clanadmin_settings);
            adminMenu.setVisible(false);

        }
        return true;

    }

    @Override
    public void onResume(){
        //will be executed onResume

        getClanInfo();
        super.onResume();
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
            case R.id.action_clanadmin_settings:
                Intent clanAdminIntent = new Intent(this, ClanManagerAreaActivity.class);
                clanAdminIntent.putExtra("clanName", clanName);
                clanAdminIntent.putExtra("canUserAdmin", canUserAdmin);
                clanAdminIntent.putExtra("serverSideClanID", serverSideClanID);
                startActivity(clanAdminIntent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}

