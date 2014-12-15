package com.iranmobilecongress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.iranmobilecongress.utils.MySmsManager;
import com.iranmobilecongress.utils.Utils;

public class RegistryActivity extends Activity implements OnClickListener {

    final String TAG = this.getClass().getName();
    final Context context = this;

    Button btnSend;
    TextView tvFname, tvLname, tvBirthYear, tvPhoneNo, tvPhoneCode,
            tvEducation, tvCurses, tvCurseAndroid, tvCurseIos;
    EditText etFname, etLname, etBirthYear, etPhoneNo, etPhoneCode;
    Spinner spEducation;
    CheckBox cbMobile, cbAndroid, cbIos;

    Dialog progressSMS;

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

        setContentView(R.layout.activity_registry);

        initForm();
    }

    private void initForm() {
        btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        tvFname = (TextView) findViewById(R.id.tvFname);
        tvLname = (TextView) findViewById(R.id.tvLname);
        tvBirthYear = (TextView) findViewById(R.id.tvBirthYear);
        tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
        tvPhoneCode = (TextView) findViewById(R.id.tvPhoneCode);
        tvEducation = (TextView) findViewById(R.id.tvEducation);
        tvCurses = (TextView) findViewById(R.id.tvCurses);
        tvCurseAndroid = (TextView) findViewById(R.id.tvCurseAndroid);
        tvCurseIos = (TextView) findViewById(R.id.tvCurseIos);

        etFname = (EditText) findViewById(R.id.etFname);

        etFname = (EditText) findViewById(R.id.etFname);
        etLname = (EditText) findViewById(R.id.etLname);
        etBirthYear = (EditText) findViewById(R.id.etBirthYear);
        etPhoneNo = (EditText) findViewById(R.id.etPhoneNo);
        etPhoneCode = (EditText) findViewById(R.id.etPhoneCode);

        spEducation = (Spinner) findViewById(R.id.spEducation);

        cbAndroid = (CheckBox) findViewById(R.id.cbAndroid);
        cbIos = (CheckBox) findViewById(R.id.cbIos);
        cbMobile = (CheckBox) findViewById(R.id.cbMobile);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSend:
                if (checkFormData())
                    new AlertDialog.Builder(this)
                            .setCustomTitle(
                                    Utils.getTitle(
                                            this,
                                            getResources()
                                                    .getString(
                                                            R.string.dialog_RegistrySMS_title),
                                            Utils.F_BTitr,
                                            android.R.drawable.ic_dialog_info))
                            .setView(
                                    Utils.getTextView(
                                            this,
                                            getResources().getString(
                                                    R.string.dialog_RegistrySMS),
                                            Utils.F_BZar))
                            .setPositiveButton(
                                    getResources().getString(
                                            R.string.dialog_btn_yes),
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            sendSMS(createSMS());
                                        }

                                    })
                            .setNegativeButton(
                                    getResources()
                                            .getString(R.string.dialog_btn_no),
                                    null).show();

                break;

            default:
                break;
        }
    }

    private Boolean checkFormData() {
        Boolean result = true;
        String errorMessage = getResources().getString(
                R.string.strDialogRegPerInfoErrorText);

        if (etFname.getText().toString().trim().length() < 2) {
            errorMessage += "\n-"
                    + getResources().getString(R.string.strRegFormFnameError);
            result = false;
        }

        if (etLname.getText().toString().trim().length() < 2) {
            errorMessage += "\n-"
                    + getResources().getString(R.string.strRegFormLnameError);
            result = false;
        }

        if (etBirthYear.getText().toString().trim().length() != 4) {
            errorMessage += "\n-"
                    + getResources().getString(
                    R.string.strRegFormBirthYearError);
            result = false;
        } else if (!etBirthYear.getText().toString().trim().substring(0, 2)
                .equals("13")) {
            errorMessage += "\n-"
                    + getResources().getString(
                    R.string.strRegFormBirthYearError);
            result = false;
        }

        if (!cbAndroid.isChecked() && !cbIos.isChecked()
                && !cbMobile.isChecked()) {
            errorMessage += "\n-"
                    + getResources().getString(R.string.strRegFormCursesError);
            result = false;
        }

        if (result == false)
            new AlertDialog.Builder(this)
                    .setCustomTitle(
                            Utils.getTitle(
                                    this,
                                    getResources().getString(
                                            R.string.strDialogRegTitle),
                                    Utils.F_BTitr,
                                    android.R.drawable.ic_dialog_alert))
                    .setView(Utils.getTextView(this, errorMessage, Utils.F_BZar))
                    .setPositiveButton(
                            getResources().getString(R.string.dialog_btn_close),
                            null).show();

        return result;
    }

    private void sendSMS(String message) {
        MySmsManager cSMS = new MySmsManager(this, getResources().getString(
                R.string.callCenterNo));
        cSMS.sendSMS(message, new MySmsManager.smsListener() {

            @Override
            public void OnSending() {
                // Utils.disableRotation((Activity) context);
                progressSMS = ProgressDialog.show(context,
                        getString(R.string.progress_sms_title),
                        getString(R.string.progress_sms));
            }

            @Override
            public void OnSent() {
                // Utils.enableRotation((Activity) context);
                resultRegistrySMS(true);

            }

            @Override
            public void OnNotSent() {
                // Utils.enableRotation((Activity) context);
                resultRegistrySMS(false);

            }

            @Override
            public void OnNotDelivered() {
                // TODO Auto-generated method stub

            }

            @Override
            public void OnDelivered() {
                // TODO Auto-generated method stub

            }
        });
    }

    private void resultRegistrySMS(final Boolean isDone) {
        progressSMS.dismiss();

        new AlertDialog.Builder(this)
                .setCustomTitle(
                        Utils.getTitle(
                                this,
                                getResources().getString(
                                        R.string.dialog_RegistrySMS_title),
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
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (isDone)
                                    finish();
                            }
                        }).show();
    }

    private String createSMS() {
        String message = String
                .format("0. %s - %s\n1. %s - %s\n2. %s\n3. %s-%s\n4. %s\n5. %s - %s - %s",
                        getResources().getString(R.string.app_name),
                        getResources().getString(R.string.app_version), etFname
                                .getText().toString().trim(), etLname.getText()
                                .toString().trim(), etBirthYear.getText()
                                .toString().trim(), etPhoneCode.getText()
                                .toString().trim(), etPhoneNo.getText()
                                .toString().trim(), spEducation
                                .getSelectedItem().toString(), cbAndroid
                                .isChecked() ? "Android" : "", cbIos
                                .isChecked() ? "ios" : "",
                        cbMobile.isChecked() ? "Mobile" : "");

        return message;
    }
}
