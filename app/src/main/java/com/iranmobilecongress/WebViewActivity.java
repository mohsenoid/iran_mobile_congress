package com.iranmobilecongress;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;

public class WebViewActivity extends Activity {

    private static WebView webview;
    private static String fileName;
    private static TextView txtStatus;

    @Override
    protected void onStart() {
        super.onStart();

        EasyTracker.getInstance().activityStart(this); // Google Analytic
    }

    @Override
    protected void onStop() {
        super.onStop();

        EasyTracker.getInstance().activityStop(this); // Google Analytic
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // changeLocalLanguage("fa");
        setContentView(R.layout.activity_webview);

        txtStatus = (TextView) findViewById(R.id.txtStatus);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        // webview.getSettings().setBuiltInZoomControls(false);

        try {
            Bundle bundle = getIntent().getExtras();
            fileName = bundle.getString("fileName");
            if (fileName == null) {
                txtStatus.setVisibility(View.VISIBLE);
                webview.setVisibility(View.INVISIBLE);
            } else

                webview.loadUrl(String.format("file:/android_asset/html/%s",
                        fileName));
        } catch (Exception e) {
            e.printStackTrace();
            txtStatus.setVisibility(View.VISIBLE);
        }

    }

    // private void changeLocalLanguage(String languageToLoad) {
    // Locale locale = new Locale(languageToLoad);
    // Locale.setDefault(locale);
    // Configuration config = new Configuration();
    // config.locale = locale;
    // getBaseContext().getResources().updateConfiguration(config,
    // getBaseContext().getResources().getDisplayMetrics());
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_back:
                finish();
                break;
        }
        return true;
    }

}
