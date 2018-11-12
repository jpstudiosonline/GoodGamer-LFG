package com.jpstudiosonline.goodgamer.userAppArea.tabFragments.UserMenuActivities;



import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jpstudiosonline.goodgamer.R;


public class ForumsActivity extends AppCompatActivity {

    public WebView forumsView;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //Set the activity to be full screen

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forums);

        //Set the toolbar
        Toolbar forumsToolbar = (Toolbar) findViewById(R.id.forumsToolbar);
        forumsToolbar.setTitle("Social Media");
        setSupportActionBar(forumsToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Get webview that we will open the forums in
        forumsView = (WebView) findViewById(R.id.forumsWebView);
        WebSettings webSettings = forumsView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        forumsView.setWebViewClient(new WebViewClient());
        forumsView.loadUrl("https://jpstudiosonline.com/community/");


    }

    //On back button go back for webview
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && forumsView.canGoBack()) {
            forumsView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.forums_menu, menu);
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

            case R.id.action_action_forums_go_back:
                //Go back if we can
                if (forumsView.canGoBack()){

                    forumsView.goBack();

                }

                return true;

            case R.id.action_forums_go_forward:

                //Go forward if we can
                if (forumsView.canGoForward()){

                    forumsView.goForward();

                }
                return true;

            case R.id.action_open_browser:
                //Open current open URL in a browser
                Intent intent = new Intent(Intent.ACTION_VIEW, uri.parse(forumsView.getUrl()));
                startActivity(intent);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }
}
