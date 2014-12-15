package com.iranmobilecongress.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Utils {
    public static final String F_BTitr = "fonts/btitr.ttf";
    public static final String F_BTraffic = "fonts/btraffic.ttf";
    public static final String F_BZar = "fonts/bzar.ttf";

    public static TextView getTextView(Context ctx, String text, String font) {
        TextView view = new TextView(ctx);
        view.setText(text);
        view.setGravity(Gravity.RIGHT);
        view.setTextSize(20);
        view.setTextColor(Color.WHITE);
        view.setPadding(10, 0, 10, 0);
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), font);
        view.setTypeface(tf);
        return view;
    }

    public static View getTitle(Context ctx, String text, String font, int image) {
        RelativeLayout layout = new RelativeLayout(ctx);

        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setGravity(Gravity.RIGHT);
        tv.setTextColor(Color.WHITE);
        Typeface tf = Typeface.createFromAsset(ctx.getAssets(), font);
        tv.setTypeface(tf);
        tv.setId(0);
        layout.addView(tv);
        tv.setTextSize(20);
        RelativeLayout.LayoutParams params1 = (LayoutParams) tv
                .getLayoutParams();
        params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params1.addRule(RelativeLayout.CENTER_VERTICAL);
        params1.setMargins(0, 0, 5, 0);
        tv.setLayoutParams(params1);

        ImageView iv = new ImageView(ctx);
        iv.setBackgroundDrawable(ctx.getResources().getDrawable(image));
        RelativeLayout.LayoutParams params2 = new LayoutParams(convertDpToPxl(
                ctx, 50), convertDpToPxl(ctx, 60));
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(params2);
        params2.setMargins(20, 5, 5, 10);
        layout.addView(iv);

        return layout;
    }

    public static int convertDpToPxl(Context ctx, int dp) {
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, ctx.getResources().getDisplayMetrics());
        return px;
    }

    public static void playSound(Context ctx, int resID) {
        MediaPlayer mp = MediaPlayer.create(ctx, resID);
        mp.setVolume(.2f, .2f);
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }

        });
        mp.start();
    }

    public static String getHTML(String urlToRead) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(urlToRead);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static AlertDialog.Builder getDialog(Context ctx, String title,
                                                String Message, int image) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCustomTitle(getTitle(ctx, title, F_BTitr, image));
        dialog.setView(getTextView(ctx, Message, F_BZar));
        return dialog;
    }

    public static void disableScreenRotation(Activity activity) {
        final int orientation = activity.getResources().getConfiguration().orientation;
        @SuppressWarnings("deprecation")
        final int rotation = activity.getWindowManager().getDefaultDisplay()
                .getOrientation();

        // Copied from Android docs, since we don't have these values in Froyo
        // 2.2
        int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
        int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            SCREEN_ORIENTATION_REVERSE_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            SCREEN_ORIENTATION_REVERSE_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90
                || rotation == Surface.ROTATION_180
                || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        } else if (rotation == Surface.ROTATION_180
                || rotation == Surface.ROTATION_270) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_PORTRAIT);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }
    }

    public static void enableScreenRotation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
