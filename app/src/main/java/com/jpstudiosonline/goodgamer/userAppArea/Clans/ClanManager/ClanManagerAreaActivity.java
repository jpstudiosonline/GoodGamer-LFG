package com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.EditClanInfoFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.EditClanMembersFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.ClanManager.tabFragments.InviteClanMembersFragment;

import java.util.ArrayList;
import java.util.List;

public class ClanManagerAreaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private boolean canUserAdmin, canUserApplyToClan, userIsMember;
    private String clanName, viewClanDescription, serverSideClanID;
    public Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set toolbar and window seetings
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_clan_manager_area);

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.clansAdminAreaToolbar);
        toolbar.setTitle("Clan Admin mode");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        clanName = getIntent().getStringExtra("clanName");
        canUserAdmin = getIntent().getBooleanExtra("canUserAdmin", false);
        serverSideClanID  = getIntent().getStringExtra("serverSideClanID");

        if (!canUserAdmin){

            finish();
        }

        setupTabs();
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

    //Method to create activity tabs
    public void setupTabs(){

        //Page viewer for tabs
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.clansAdminAreaTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    //Tab fragments
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putString("clanName", clanName);
        bundle.putBoolean("canUserAdmin", canUserAdmin);
        bundle.putString("serverSideClanID", serverSideClanID);
        //Pass the clan name to fargment
        EditClanInfoFragment clanInfoFragment = new EditClanInfoFragment();
        clanInfoFragment.setArguments(bundle);

        EditClanMembersFragment editClanMembersFragment = new EditClanMembersFragment();
        editClanMembersFragment.setArguments(bundle);

        InviteClanMembersFragment inviteClanMembersFragment = new InviteClanMembersFragment();
        inviteClanMembersFragment.setArguments(bundle);

        adapter.addFragment(clanInfoFragment, "Edit Clan Info");
        adapter.addFragment(editClanMembersFragment, "Manage Members");
        adapter.addFragment(inviteClanMembersFragment, "Pending Members");

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
}
