package com.noahmob.AppLocker.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.ApplicationListActivity;

import java.util.regex.Pattern;

public class SetMailActivity extends Activity implements OnClickListener {
    EditText textEmail;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setemail);
        findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                SetMailActivity.this.onBackPressed();
            }
        });
        this.textEmail = (EditText) findViewById(R.id.emailInput);
        String default_email = AppLockerPreference.getInstance(this).getUserEmail();
        if (TextUtils.isEmpty(default_email)) {
            this.textEmail.setHint(getString(R.string.email_hint));
        } else {
            this.textEmail.setText(default_email);
        }
        findViewById(R.id.later).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
    }

    public void onClick(View view) {
        Intent intent = new Intent(this, ApplicationListActivity.class);
        switch (view.getId()) {
            case R.id.later:
                finish();
                startActivity(intent);
                return;
            case R.id.next:
                String email = this.textEmail.getText().toString();
                if (isEmail(email)) {
                    AppLockerPreference.getInstance(getApplicationContext()).saveUserEmail(email);
                    finish();
                    startActivity(intent);
                    return;
                }
                Toast.makeText(getApplicationContext(), "Email pattern error, Please check email", Toast.LENGTH_SHORT).show();
                return;
            default:
                return;
        }
    }

    public boolean isEmail(String strEmail) {
        return Pattern.compile("\\w+@\\w+\\.\\w+").matcher(strEmail).matches();
    }
}
