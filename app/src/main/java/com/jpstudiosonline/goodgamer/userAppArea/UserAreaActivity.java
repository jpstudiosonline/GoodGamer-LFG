package com.jpstudiosonline.goodgamer.userAppArea;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.ChatMessages.ChatAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClansAreaActivity;
import com.jpstudiosonline.goodgamer.userAppArea.Notifications.GCMRegistrationIntentService;
import com.jpstudiosonline.goodgamer.userAppArea.Notifications.SubmitGcmRegister;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.DashBoardFragment;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.GameNameFragment;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.ForumsActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities.SuggestionsActivity;
import com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserProfileActivity;
import com.jpstudiosonline.goodgamer.userLogin.userLoginActivities.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserAreaActivity extends AppCompatActivity {

    //Get items for toobar and tabs
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public String loggedInUserId, token, loggedInUserName;
    public SharedPreferences sharedPreferences;
    public Uri uri;

    //Creating a broadcast receiver for gcm registration
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_user_area);

        //Set the toolbar
        toolbar = (Toolbar) findViewById(R.id.userAreatoolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.userAreatabs);
        tabLayout.setupWithViewPager(viewPager);

        Intent intent = getIntent();
        loggedInUserId = intent.getStringExtra("userID");

        //Get user info
        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        final String savedToken = sharedPreferences.getString("gcmToken","NA");

        loggedInUserName = sharedPreferences.getString("loginUsername", "N/A");
        //Log.e("Saved token", savedToken);
        //Initializing our broadcast receiver
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            //When the broadcast received
            //We are sending the broadcast from GCMRegistrationIntentService

            @Override
            public void onReceive(Context context, Intent intent) {
                //If the broadcast has received with success
                //that means device is registered successfully
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Getting the registration token from the intent
                    token = intent.getStringExtra("token");

                    if (!savedToken.equals(token)){

                        //Log.e("Trying to register", "now restg");
                        //Save user info to shared perf
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("gcmToken", token);
                        editor.putBoolean("gcmRegistered", true);
                        editor.apply();
                        registerTokenWithServer(token);

                    } else {



                    }

                    //Displaying the token as toast
                    //Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();

                    //if the intent is not with success then displaying error messages
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
                }
            }
        };

        //Checking play service is available or not
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        //if play service is not available
        if(ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Displaying message that play service is not installed
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());

                //If play service is not supported
                //Displaying an error message
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }

            //If play service is available
        } else {
            //Starting intent to register device
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        //This opens the menu on the left side of the app
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_user_area);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        //Left NAV menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.tvUserName);
        nav_user.setText(loggedInUserName);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                if (id == R.id.profile_nav) {

                    Intent profileIntent = new Intent(UserAreaActivity.this, UserProfileActivity.class);
                    startActivity(profileIntent);

                } else if (id == R.id.clans_nav) {

                    Intent clansIntent = new Intent(UserAreaActivity.this, ClansAreaActivity.class);
                    startActivity(clansIntent);

                } else if (id == R.id.contacts_nav) {

                    Intent contactsIntent = new Intent(UserAreaActivity.this, ChatAreaActivity.class);
                    startActivity(contactsIntent);

                } else if (id == R.id.nav_webportal) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/goodgamer/LFG-Portal/"));
                    startActivity(intent);

                }  else if (id == R.id.nav_webportal) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/goodgamer/LFG-Portal/"));
                    startActivity(intent);

                }  else if (id == R.id.nav_share_app) {

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.jpstudiosonline.goodgamer");
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "Share GoodGamer"));

                } else if (id == R.id.nav_rate_app) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://play.google.com/store/apps/details?id=com.jpstudiosonline.goodgamer"));
                    startActivity(intent);
                } else if (id == R.id.jpstudios_action) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/community/"));
                    startActivity(intent);

                } else if (id == R.id.nav_help) {

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/main/goodgamer/lfg-finder-goodgamer-free-app/"));
                    startActivity(intent);

                } else if (id == R.id.suggestion_nav) {

                    Intent suggestIntent = new Intent(UserAreaActivity.this, SuggestionsActivity.class);
                    startActivity(suggestIntent);

                }


                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_user_area);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    //For the drawer hide and show
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_user_area);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Register our notification token with the server
    public void registerTokenWithServer(final String token){

        //Setup listener to get the Login response from the server
        Response.Listener<String> registerGcmResponseListener = new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("successcode");

                    if (success){

                        //Save user info to shared perf
                        sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("gcmToken", token);
                        editor.putBoolean("gcmRegistered", true);
                        editor.apply();

                        //Log.e("registered", "Regsitered withs erver");

                    } else {

                        //Log.e("Token failed", token);
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
                        builder.setMessage("Failed to login, try register GCM.")
                                .setNegativeButton("Retry", null)
                                .create()
                                .show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

        };

        //Since we are going to be using the autologin, call the server from the user info saved in the preferences
        //Submit login request with the server
        SubmitGcmRegister submitRegisterRequest = new SubmitGcmRegister(loggedInUserId, token, registerGcmResponseListener);
        RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
        queue.add(submitRegisterRequest);


    }

    //Registering receiver on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }


    //Unregistering receiver on activity paused
    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DashBoardFragment(), "Dashboard");
        adapter.addFragment(new GameNameFragment(), "Find (LFG)");
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
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
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
                return true;
            case R.id.action_open_forums:
                //Open the forums activity
                Intent forumsActivity = new Intent(UserAreaActivity.this, ForumsActivity.class);
                startActivity(forumsActivity);
                return true;

            //Create outlog
            case R.id.action_logout:

                //Logout from the server GCM token
                registerTokenWithServer(String.valueOf(0));

                //Get user info and clear out the preferences
                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                sharedPreferences.edit().clear().apply();
                finish();

                //Open the login screen
                Intent loginIntent = new Intent(UserAreaActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                return true;

            //This will be used to create a list of apps that the user can share the app with
            case R.id.action_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.jpstudiosonline.goodgamer");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share GoodGamer"));
                return true;

            case R.id.action_open_portal:
                Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse("https://jpstudiosonline.com/goodgamer/LFG-Portal/"));
                startActivity(intent);
                return true;

            case R.id.action_suggestions:
                Intent suggestIntent = new Intent(UserAreaActivity.this, SuggestionsActivity.class);
                startActivity(suggestIntent);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
}
