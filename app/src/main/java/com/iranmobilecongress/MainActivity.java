package com.iranmobilecongress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ScrollView;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.iranmobilecongress.utils.CustomSlideshow;
import com.iranmobilecongress.utils.MySmsManager;
import com.iranmobilecongress.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements OnClickListener,
        AnimationListener {
    final String TAG = this.getClass().getName();
    final int SPONSOR_ID = 1;
    final String P_FIRSTRUN = "First-Run";
    final String P_JOINED = "Joined";
    final Context context = this;
    SharedPreferences prefs;
    int animRow;
    Animation animLeft, animRight, animApear, animLamp;
    Button btnAbout, btnGoal, btnHistory, btnProgram, btnCongress, btnAward,
            btnExibition, btnCalendar, btnRegistration, btnJoin;
    ScrollView scrollViewSponsers;
    SlidingDrawer slidingDrawerSponsers;
    Gallery gallerySlideshow;
    MySmsManager cSMS;
    Dialog progressSMS;
    private boolean doubleBackToExitPressedOnce;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        cSMS = new MySmsManager(this, getResources().getString(R.string.callCenterNo));

        if (isFirstRun() && savedInstanceState == null) {
            requestJoin();
        }

        CustomSlideshow cGallery = new CustomSlideshow(this);

        initForm();

        if (savedInstanceState == null
                && Integer.valueOf(android.os.Build.VERSION.SDK_INT) > 13) {
            startMyAnimation();
        }

        initSponsers();

    }

    private void requestJoin() {
        new AlertDialog.Builder(this)
                .setCustomTitle(
                        Utils.getTitle(
                                this,
                                getResources().getString(
                                        R.string.dialog_JoinSMS_title),
                                Utils.F_BTitr, android.R.drawable.ic_dialog_info))
                .setView(
                        Utils.getTextView(
                                this,
                                getResources().getString(
                                        R.string.dialog_JoinSMS), Utils.F_BZar))
                .setPositiveButton(
                        getResources().getString(R.string.dialog_btn_Join),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                cSMS.sendSMS(String.format(
                                                "%s - %s\nJoin!",
                                                getResources().getString(
                                                        R.string.app_name),
                                                getResources().getString(
                                                        R.string.app_version)),
                                        new MySmsManager.smsListener() {

                                            @Override
                                            public void OnSending() {
                                                // Utils.disableRotation((Activity)
                                                // context);
                                                progressSMS = ProgressDialog
                                                        .show(context,
                                                                getString(R.string.progress_sms_title),
                                                                getString(R.string.progress_sms));
                                            }

                                            @Override
                                            public void OnSent() {
                                                // Utils.enableRotation((Activity)
                                                // context);
                                                resultFirstRunSMS(true);

                                            }

                                            @Override
                                            public void OnNotSent() {
                                                // Utils.enableRotation((Activity)
                                                // context);
                                                resultFirstRunSMS(false);

                                            }

                                            @Override
                                            public void OnNotDelivered() {
                                                // TODO Auto-generated method
                                                // stub

                                            }

                                            @Override
                                            public void OnDelivered() {
                                                // TODO Auto-generated method
                                                // stub

                                            }
                                        });
                            }

                        })
                .setNegativeButton(
                        getResources().getString(R.string.dialog_btn_no),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                setJoined(false);
                            }
                        }).show();
    }

    private Boolean isFirstRun() {
        Boolean result = prefs.getBoolean(P_FIRSTRUN, true);
        if (result) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(P_FIRSTRUN, false);
            editor.commit();

            // sendNewUserSMS();
        }
        return result;

    }

    private Boolean isJoined() {
        return prefs.getBoolean(P_JOINED, false);
    }

    private void setJoined(Boolean isDone) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(P_JOINED, isDone);
        editor.commit();

        if (isDone)
            btnJoin.setVisibility(View.GONE);
    }

    // private void sendNewUserSMS() {
    // cSMS.sendSMS(String.format("%s - %s\nNewUser!", getResources()
    // .getString(R.string.app_name),
    // getResources().getString(R.string.app_version)), null);
    // }

    private void resultFirstRunSMS(Boolean isDone) {
        progressSMS.dismiss();

        setJoined(isDone);

        new AlertDialog.Builder(this)
                .setCustomTitle(
                        Utils.getTitle(
                                this,
                                getResources().getString(
                                        R.string.dialog_JoinSMS_title),
                                Utils.F_BTitr, android.R.drawable.ic_dialog_info))
                .setView(
                        Utils.getTextView(
                                this,
                                isDone ? getResources().getString(
                                        R.string.dialog_SMS_done)
                                        : getResources().getString(
                                        R.string.dialog_SMS_notdone),
                                Utils.F_BZar))
                .setPositiveButton(
                        getResources().getString(R.string.dialog_btn_close),
                        null).show();
    }

    private void initForm() {
        Log.d(TAG, "Form init Start!");

        btnAbout = (Button) findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(this);

        btnGoal = (Button) findViewById(R.id.btnGoal);
        btnGoal.setOnClickListener(this);

        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        btnProgram = (Button) findViewById(R.id.btnProgram);
        btnProgram.setOnClickListener(this);

        btnCongress = (Button) findViewById(R.id.btnCongress);
        btnCongress.setOnClickListener(this);

        btnAward = (Button) findViewById(R.id.btnAward);
        btnAward.setOnClickListener(this);

        btnExibition = (Button) findViewById(R.id.btnExibition);
        btnExibition.setOnClickListener(this);

        btnCalendar = (Button) findViewById(R.id.btnCalendar);
        btnCalendar.setOnClickListener(this);

        btnRegistration = (Button) findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(this);

        btnJoin = (Button) findViewById(R.id.btnJoin);
        btnJoin.setOnClickListener(this);
        if (isJoined()) {
            btnJoin.setVisibility(View.GONE);
        }

        scrollViewSponsers = (ScrollView) findViewById(R.id.scrollViewSponsers);

        slidingDrawerSponsers = (SlidingDrawer) findViewById(R.id.slidingDrawerSponsers);

        gallerySlideshow = (Gallery) findViewById(R.id.gallerySlideshow);


        if (isJoined()) {
            // TODO: hide join button...
        }

        Log.d(TAG, "Form init End!");
    }

    private void initSponsers() {
        Log.d(TAG, "Sponsers init Start!");

        final int count = 40;
        final int iconSize = 100;
        final int iconMargin = 5;

        TableLayout tblSponsers = new TableLayout(this);// (TableLayout)
        // findViewById(R.id.tblSponsers);
        FrameLayout.LayoutParams tblLayoutParams = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tblSponsers.setLayoutParams(tblLayoutParams);
        // tblSponsers.setBackgroundColor(color.holo_red_light);
        // int width = 600;// tblSponsers.getWidth();

        int width = getDisplayWidth();

        int colCount = width / iconSize;

        String[] urlArray = getResources().getStringArray(R.array.sponsersURL);

        int k = 0;
        for (int i = 0; i <= count / colCount && k < count; i++) {
            TableRow row = new TableRow(this);
            TableLayout.LayoutParams rowLayoutParams = new TableLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            rowLayoutParams.gravity = Gravity.CENTER;
            row.setLayoutParams(rowLayoutParams);

            for (int j = 0; j < colCount && k < count; j++) {
                ImageView img = new ImageView(this);
                img.setId(SPONSOR_ID);

                try {
                    int iconID = getResources().getIdentifier(
                            String.format("s%d", k), "drawable",
                            getPackageName());
                    img.setImageDrawable(getResources().getDrawable(iconID));
                } catch (NotFoundException e) {
                    k++;
                    int iconID = getResources().getIdentifier(
                            String.format("s%d", k), "drawable",
                            getPackageName());
                    img.setImageDrawable(getResources().getDrawable(iconID));
                }
                img.setContentDescription(urlArray[k]);
                TableRow.LayoutParams imgLayout = new TableRow.LayoutParams(
                        iconSize - 3 * iconMargin, iconSize - 3 * iconMargin);
                imgLayout.gravity = Gravity.CENTER;
                imgLayout.setMargins(iconMargin, iconMargin, iconMargin,
                        iconMargin);
                imgLayout.weight = 1;
                img.setLayoutParams(imgLayout);
                img.setScaleType(ScaleType.CENTER_INSIDE);
                img.setOnClickListener(this);

                row.addView(img);
                k++;
            }

            tblSponsers.addView(row);
        }

        // tblSponsers.notifyAll();

        scrollViewSponsers.addView(tblSponsers);
        // scrollViewSponsers.notify();

        Log.d(TAG, "Sponsers init End!");
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private int getDisplayWidth() {
        if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 13) {
            Display display = getWindowManager().getDefaultDisplay();
            return display.getWidth();
        } else {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            return size.x;
        }
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // getMenuInflater().inflate(R.menu.activity_main, menu);
    // return true;
    // }

    @Override
    public void onClick(View v) {
        Intent webviewIntent = new Intent(v.getContext(), WebViewActivity.class);
        Bundle bundle = new Bundle();

        switch (v.getId()) {
            case R.id.btnAbout:
                bundle.putString("fileName", "about_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnGoal:
                bundle.putString("fileName", "goal_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnHistory:
                bundle.putString("fileName", "history_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnProgram:
                bundle.putString("fileName", "program_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnCongress:
                bundle.putString("fileName", "congress_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnAward:
                bundle.putString("fileName", "award_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnExibition:
                bundle.putString("fileName", "exibition_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnCalendar:
                bundle.putString("fileName", "calendar_fa.html");
                webviewIntent.putExtras(bundle);
                startActivityForResult(webviewIntent, 0);
                break;
            case R.id.btnRegistration:
                Intent registryIntent = new Intent(v.getContext(),
                        RegistryActivity.class);
                startActivity(registryIntent);
                break;
            case R.id.btnJoin:
                requestJoin();
                break;
            case SPONSOR_ID:
                Uri uriSponser = Uri.parse(v.getContentDescription().toString());
                Intent intentSponser = new Intent(Intent.ACTION_VIEW, uriSponser);
                startActivity(intentSponser);
                break;

            case R.id.imgDetails:
            case R.id.imgLogo:
            case R.id.imgTitle:
                Uri uriCongress = Uri.parse(getResources().getString(
                        R.string.congressURL));
                Intent intentCongress = new Intent(Intent.ACTION_VIEW, uriCongress);
                startActivity(intentCongress);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doubleBackToExitPressedOnce = false;

    }

    @Override
    public void onBackPressed() {
        if (slidingDrawerSponsers.isOpened())
            slidingDrawerSponsers.animateOpen();
        else {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.msg_exit, Toast.LENGTH_SHORT).show();

            Timer t = new Timer();
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2500);
        }
    }

    private void startMyAnimation() {
        Log.d(TAG, "Animation Start!");

        gallerySlideshow.setVisibility(View.INVISIBLE);

        btnAbout.setVisibility(View.INVISIBLE);
        btnGoal.setVisibility(View.INVISIBLE);
        btnProgram.setVisibility(View.INVISIBLE);
        btnHistory.setVisibility(View.INVISIBLE);
        btnCongress.setVisibility(View.INVISIBLE);
        btnAward.setVisibility(View.INVISIBLE);
        btnCalendar.setVisibility(View.INVISIBLE);
        btnExibition.setVisibility(View.INVISIBLE);
        btnRegistration.setVisibility(View.INVISIBLE);
        btnJoin.setVisibility(View.INVISIBLE);

        animRow = 0;

        animApear = AnimationUtils.loadAnimation(this, R.anim.appearbtn);

        animApear.setAnimationListener(this);

        gallerySlideshow.startAnimation(animApear);
        scrollViewSponsers.startAnimation(animApear);

        // animLamp = AnimationUtils.loadAnimation(this, R.anim.appearimg);
        // imgLamp.startAnimation(animLamp);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        animApear = AnimationUtils.loadAnimation(this, R.anim.appearbtn);
        animLeft = AnimationUtils.loadAnimation(this, R.anim.righttoleftbtn);
        animRight = AnimationUtils.loadAnimation(this, R.anim.lefttorightbtn);
        animLeft.setAnimationListener(this);
        animApear.setAnimationListener(this);

        switch (animRow) {
            case 0:

                btnAbout.startAnimation(animLeft);
                btnGoal.startAnimation(animRight);
                break;
            case 1:

                btnProgram.startAnimation(animLeft);
                btnHistory.startAnimation(animRight);
                break;

            case 2:
                btnCongress.startAnimation(animLeft);
                btnAward.startAnimation(animRight);
                break;

            case 3:
                btnCalendar.startAnimation(animLeft);
                btnExibition.startAnimation(animRight);
                break;

            case 4:
                btnRegistration.startAnimation(animApear);
                break;
            case 5:
                if (!isJoined())
                    btnJoin.startAnimation(animApear);
                break;
        }

        animRow++;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
