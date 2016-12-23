package com.noahmob.AppLocker.Activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.AppLockerPreference;
import com.noahmob.AppLocker.config.Constant;

public class SetPasswordActivity extends Activity implements OnClickListener, TextWatcher {
    public Boolean Passed = Boolean.valueOf(false);
    public String Password = "";
    AppLockerPreference appLockerPreference;
    ImageView btn_clear;
    ImageView btn_delete;
    Button btn_ok;
    ImageView btn_psw1;
    ImageView btn_psw2;
    ImageView btn_psw3;
    ImageView btn_psw4;
    private boolean canChangePSW = false;
    private String firstEnterPsw;
    private boolean isFirstLauncher = false;
    TextView msg_text;
    Button number_button_0;
    Button number_button_1;
    Button number_button_2;
    Button number_button_3;
    Button number_button_4;
    Button number_button_5;
    Button number_button_6;
    Button number_button_7;
    Button number_button_8;
    Button number_button_9;
    int[] numbers = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    TextView password_field;
    ImageView[] pswImg;
    SharedPreferences sh_Pref;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setpassword);
        this.msg_text = (TextView) findViewById(R.id.txt_msg);
        this.password_field = (TextView) findViewById(R.id.password_field);
        this.password_field.addTextChangedListener(this);
        this.number_button_1 = (Button) findViewById(R.id.number_button_1);
        this.number_button_2 = (Button) findViewById(R.id.number_button_2);
        this.number_button_3 = (Button) findViewById(R.id.number_button_3);
        this.number_button_4 = (Button) findViewById(R.id.number_button_4);
        this.number_button_5 = (Button) findViewById(R.id.number_button_5);
        this.number_button_6 = (Button) findViewById(R.id.number_button_6);
        this.number_button_7 = (Button) findViewById(R.id.number_button_7);
        this.number_button_8 = (Button) findViewById(R.id.number_button_8);
        this.number_button_9 = (Button) findViewById(R.id.number_button_9);
        this.number_button_0 = (Button) findViewById(R.id.number_button_0);
        this.btn_psw1 = (ImageView) findViewById(R.id.psw_button_1);
        this.btn_psw2 = (ImageView) findViewById(R.id.psw_button_2);
        this.btn_psw3 = (ImageView) findViewById(R.id.psw_button_3);
        this.btn_psw4 = (ImageView) findViewById(R.id.psw_button_4);
        this.btn_clear = (ImageView) findViewById(R.id.btn_clear);
        this.btn_delete = (ImageView) findViewById(R.id.btn_delete);
        this.btn_ok = (Button) findViewById(R.id.Btn_Tick);
        this.number_button_0.setOnClickListener(this);
        this.number_button_1.setOnClickListener(this);
        this.number_button_2.setOnClickListener(this);
        this.number_button_3.setOnClickListener(this);
        this.number_button_4.setOnClickListener(this);
        this.number_button_5.setOnClickListener(this);
        this.number_button_6.setOnClickListener(this);
        this.number_button_7.setOnClickListener(this);
        this.number_button_8.setOnClickListener(this);
        this.number_button_9.setOnClickListener(this);
        this.btn_clear.setOnClickListener(this);
        this.btn_delete.setOnClickListener(this);
        this.btn_ok.setOnClickListener(this);
        Button[] img = new Button[]{this.number_button_0, this.number_button_1, this.number_button_2, this.number_button_3, this.number_button_4, this.number_button_5, this.number_button_6, this.number_button_7, this.number_button_8, this.number_button_9};
        this.pswImg = new ImageView[]{this.btn_psw1, this.btn_psw2, this.btn_psw3, this.btn_psw4};
        for (int i = 0; i < this.numbers.length; i++) {
            img[i].setText(i + "");
            img[i].setTag(i + "");
        }
        this.appLockerPreference = new AppLockerPreference(getApplicationContext());
        this.sh_Pref = getSharedPreferences(Constant.DATA, 0);
        if (!this.sh_Pref.getBoolean("isfirst", false)) {
            this.msg_text.setText("Enter password");
            this.isFirstLauncher = true;
        }
        if (this.isFirstLauncher) {
            this.msg_text.setText("Enter password");
        } else {
            this.msg_text.setText("Enter new password");
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.number_button_2:
                onnumclcick(Integer.parseInt(this.number_button_2.getTag().toString()));
                return;
            case R.id.number_button_1:
                onnumclcick(Integer.parseInt(this.number_button_1.getTag().toString()));
                return;
            case R.id.number_button_3:
                onnumclcick(Integer.parseInt(this.number_button_3.getTag().toString()));
                return;
            case R.id.number_button_5:
                onnumclcick(Integer.parseInt(this.number_button_5.getTag().toString()));
                return;
            case R.id.number_button_4:
                onnumclcick(Integer.parseInt(this.number_button_4.getTag().toString()));
                return;
            case R.id.number_button_6:
                onnumclcick(Integer.parseInt(this.number_button_6.getTag().toString()));
                return;
            case R.id.number_button_8:
                onnumclcick(Integer.parseInt(this.number_button_8.getTag().toString()));
                return;
            case R.id.number_button_7:
                onnumclcick(Integer.parseInt(this.number_button_7.getTag().toString()));
                return;
            case R.id.number_button_9:
                onnumclcick(Integer.parseInt(this.number_button_9.getTag().toString()));
                return;
            case R.id.number_button_0:
                onnumclcick(Integer.parseInt(this.number_button_0.getTag().toString()));
                return;
            case R.id.btn_clear:
                this.password_field.setText("");
                this.Password = "";
                setPswImgState(0);
                return;
            case R.id.btn_delete:
                int length = this.Password.toString().length();
                if (length > 0) {
                    String after = this.Password.substring(0, length - 1).trim();
                    this.password_field.setText(after);
                    this.Password = after;
                    setPswImgState(length - 1);
                    return;
                }
                this.password_field.setText("");
                this.Password = "";
                setPswImgState(0);
                return;
            case R.id.Btn_Tick:
                onnumclcick(0);
                return;
            default:
                return;
        }
    }

    public void onnumclcick(int number) {
        this.Password += "" + number;
        this.password_field.setText(this.Password);
    }

    public boolean verifyPassword() {
        if (this.Password == null) {
            return false;
        }
        this.appLockerPreference.savePasswordType(true);
        return this.Password.equals(AppLockerPreference.getInstance(this).getPassword());
    }

    public int passwordlength() {
        return AppLockerPreference.getInstance(this).getPassword().length();
    }

    public void onBackPressed() {
        if (this.isFirstLauncher) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN").addCategory("android.intent.category.HOME").addFlags(67108864);
            startActivity(intent);
        }
        finish();
    }

    public void showAlert(String title, String message) {
        Builder builder = new Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.create();
        builder.show();
    }

    private void setPswImgState(int length) {
        int i = 0;
        while (i < 4) {
            this.pswImg[i].setSelected(i < length);
            i++;
        }
    }

    public void afterTextChanged(Editable editable) {
        int length = editable.toString().length();
        if (length > 0 && length <= 4) {
            setPswImgState(length);
        }
        if (this.canChangePSW) {
            if (length != 4) {
                return;
            }
            if (TextUtils.equals(this.Password, this.firstEnterPsw)) {
                SetPswSuccess();
                return;
            }
            Toast.makeText(this, "Password error", Toast.LENGTH_LONG).show();
            clearPsw();
            this.canChangePSW = false;
            this.firstEnterPsw = "";
            if (this.isFirstLauncher) {
                this.msg_text.setText("Enter password");
            } else {
                this.msg_text.setText("Enter new password");
            }
        } else if (length == 4) {
            this.canChangePSW = true;
            this.firstEnterPsw = editable.toString();
            clearPsw();
            this.msg_text.setText("Enter again confirm");
        }
    }

    private void clearPsw() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SetPasswordActivity.this.Password = "";
                SetPasswordActivity.this.setPswImgState(0);
                SetPasswordActivity.this.password_field.setText("");
            }
        }, 200);
    }

    private void SetPswSuccess() {
        this.appLockerPreference.savePassword(this.firstEnterPsw);
        this.appLockerPreference.savePasswordType(true);
        this.canChangePSW = false;
        Editor editor = this.sh_Pref.edit();
        editor.putBoolean("isfirst", true);
        editor.commit();
        startService();
        showEmailActivity();
        finish();
        Toast.makeText(this, "Password Create Successfully", Toast.LENGTH_LONG).show();
    }

    private void showEmailActivity() {
        if (TextUtils.isEmpty(AppLockerPreference.getInstance(this).getUserEmail())) {
            startActivity(new Intent(this, SetMailActivity.class));
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    private void startService() {
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }
}
