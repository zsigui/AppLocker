package com.noahmob.AppLocker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.LockPatternActivity;
import com.noahmob.AppLocker.LockScreenActivity;
import com.noahmob.AppLocker.Utils.EmailUtils;
import com.noahmob.AppLocker.config.Constant;

import java.util.Random;

import javax.mail.MessagingException;

public class RetrivePswActivity extends Activity implements OnClickListener {
    String default_email;
    EditText textCode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrivepsw);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                RetrivePswActivity.this.onBackPressed();
            }
        });
        this.textCode = (EditText) findViewById(R.id.retrive_code);
        TextView textEmail = (TextView) findViewById(R.id.retrive_email);
        this.default_email = AppLockerPreference.getInstance(this).getUserEmail();
        if (TextUtils.isEmpty(this.default_email)) {
            textEmail.setText("No Retrive Email Address");
        } else {
            textEmail.setText(this.default_email);
        }
        findViewById(R.id.retrive_ok).setOnClickListener(this);
        findViewById(R.id.retrive_send).setOnClickListener(this);
        findViewById(R.id.retrive_resend).setOnClickListener(this);
    }

    public void onBackPressed() {
        Intent intent;
        if (AppLockerPreference.getInstance(getApplicationContext()).isPasswordType()) {
            intent = new Intent(this, LockScreenActivity.class);
        } else {
            intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, this, LockPatternActivity.class);
        }
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void SendAction() {
        SendGetRetriveCodeEmail();
    }

    private void GoSetPassword() {
        AppLockerPreference.getInstance(getApplicationContext()).savePassword("");
        getSharedPreferences(Constant.DATA, 0).edit().putBoolean("isfirst", false).apply();
        AppLockerPreference.getInstance(getApplicationContext()).savePasswordType(true);
        startActivity(new Intent(this, SetPasswordActivity.class));
    }

    private String generateRetriveCode() {
        String code = "";
        for (int i = 0; i < 4; i++) {
            code = code + new Random().nextInt(10);
        }
        AppLockerPreference.getInstance(getApplicationContext()).saveRetriveCode(code);
        return code;
    }

    private void SendGetRetriveCodeEmail() {
        final String email = AppLockerPreference.getInstance(getApplicationContext()).getUserEmail();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EmailUtils.sendEmail(email, "Applocker Password Retrive Code", RetrivePswActivity.this.generateRetriveCode());
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
        Toast.makeText(getApplicationContext(), "Have Send Retrive Code to" + email, Toast.LENGTH_SHORT).show();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.retrive_send:
                if (TextUtils.isEmpty(this.default_email)) {
                    Toast.makeText(getApplicationContext(), "No Retrieve email", Toast.LENGTH_SHORT).show();
                    return;
                }
                view.setVisibility(View.GONE);
                this.textCode.setVisibility(View.VISIBLE);
                findViewById(R.id.retrive_ok).setVisibility(View.VISIBLE);
                findViewById(R.id.retrive_resend).setVisibility(View.VISIBLE);
                SendAction();
                return;
            case R.id.retrive_ok:
                if (TextUtils.equals(this.textCode.getText().toString(), AppLockerPreference.getInstance(getApplicationContext()).getRetriveCode())) {
                    GoSetPassword();
                    finish();
                    return;
                }
                this.textCode.setText("");
                Toast.makeText(getApplicationContext(), "Email pattern error, Please check email", Toast.LENGTH_SHORT).show();
                return;
            case R.id.retrive_resend:
                SendAction();
                return;
            default:
                return;
        }
    }
}
