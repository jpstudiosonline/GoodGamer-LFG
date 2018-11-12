package com.jpstudiosonline.goodgamer.userAppArea.Clans;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.jpstudiosonline.goodgamer.R;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansApplyFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansCreateFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansDetailsFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansGamesPlayedFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansListFragment;
import com.jpstudiosonline.goodgamer.userAppArea.Clans.tabFragments.ClansMyClanFragment;

import java.util.ArrayList;
import java.util.List;

public class ClansAreaActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set toolbar and window seetings
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_clans_area);

        //Set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.clansAreaToolbar);
        toolbar.setTitle("Clans");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Page viewer for tabs

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.clansAreaTabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    //Tab fragments
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ClansListFragment(), "TOP");
        adapter.addFragment(new ClansCreateFragment(), "Create");
        adapter.addFragment(new ClansMyClanFragment(), "My Clans");
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
    public void onResume(){
        //will be executed onResume

        setupViewPager(viewPager);
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

        }

        return super.onOptionsItemSelected(item);
    }
}
