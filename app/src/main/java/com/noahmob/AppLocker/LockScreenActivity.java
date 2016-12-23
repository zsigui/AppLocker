package com.noahmob.AppLocker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.Activity.RetrivePswActivity;
import com.noahmob.AppLocker.Utils.ScreenUtils;

import java.util.Random;

public class LockScreenActivity extends Activity implements OnClickListener, TextWatcher {
    public static final String ACTION_APPLICATION_PASSED = "com.noahmob.AppLocker.applicationpassedtest";
    public static final String BlockedActivityName = "locked activity name";
    public static final String BlockedPackageName = "locked package name";
    public static final String EXTRA_PACKAGE_NAME = "com.noahmob.AppLocker.extra.package.name";
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
    TextView textViewForgetPWD;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lockscreen);
        this.appLockerPreference = new AppLockerPreference(getApplicationContext());
        this.textViewForgetPWD = (TextView) findViewById(R.id.textViewForgetPWD);
        ImageView applogo = (ImageView) findViewById(R.id.img_applogo);
        this.textViewForgetPWD.setVisibility(View.VISIBLE);
        String blockPackageName = getIntent().getStringExtra("locked package name");
        if (!(TextUtils.isEmpty(blockPackageName) || blockPackageName.equals(getPackageName()))) {
            PackageManager pm = getApplicationContext().getPackageManager();
            try {
                applogo.setImageDrawable(pm.getApplicationIcon(getIntent().getStringExtra("locked package name")));
                LayoutParams para = applogo.getLayoutParams();
                para.height = ScreenUtils.dip2px(this, 68.0f);
                para.width = ScreenUtils.dip2px(this, 68.0f);
                applogo.setLayoutParams(para);
                ApplicationInfo applicationInfo = pm.getApplicationInfo(getIntent().getStringExtra("locked package " +
                        "name"), 0);
            } catch (NameNotFoundException e) {
            }
        }
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
        Button[] img = new Button[]{this.number_button_0, this.number_button_1, this.number_button_2, this
                .number_button_3, this.number_button_4, this.number_button_5, this.number_button_6, this
                .number_button_7, this.number_button_8, this.number_button_9};
        this.pswImg = new ImageView[]{this.btn_psw1, this.btn_psw2, this.btn_psw3, this.btn_psw4};
        if (AppLockerPreference.getInstance(this).isRandomKeyboard()) {
            shuffleArray(this.numbers);
        }
        for (int i = 0; i < this.numbers.length; i++) {
            if (this.numbers[i] == 0) {
                img[i].setText("0");
                img[i].setTag("0");
            } else if (this.numbers[i] == 1) {
                img[i].setText("1");
                img[i].setTag("1");
            } else if (this.numbers[i] == 2) {
                img[i].setText("2");
                img[i].setTag("2");
            } else if (this.numbers[i] == 3) {
                img[i].setText("3");
                img[i].setTag("3");
            } else if (this.numbers[i] == 4) {
                img[i].setText("4");
                img[i].setTag("4");
            } else if (this.numbers[i] == 5) {
                img[i].setText("5");
                img[i].setTag("5");
            } else if (this.numbers[i] == 6) {
                img[i].setText("6");
                img[i].setTag("6");
            } else if (this.numbers[i] == 7) {
                img[i].setText("7");
                img[i].setTag("7");
            } else if (this.numbers[i] == 8) {
                img[i].setText("8");
                img[i].setTag("8");
            } else if (this.numbers[i] == 9) {
                img[i].setText("9");
                img[i].setTag("9");
            }
        }
        this.textViewForgetPWD.setOnClickListener(new OnClickListener() {
            @SuppressLint({"InflateParams"})
            public void onClick(View v) {
                LockScreenActivity.this.retrivePsw();
            }
        });
        FBAD_init();
    }

    private void FBAD_init() {
        RelativeLayout adViewContainer = (RelativeLayout) findViewById(R.id.adViewContainer);
    }

    private void retrivePsw() {
        startActivity(new Intent(this, RetrivePswActivity.class));
    }

    private void shuffleArray(int[] ar) {
        Random rnd = new Random();
        for (int i = ar.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
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
                int length = this.password_field.getText().length();
                if (length > 0) {
                    String after = this.password_field.getText().toString().substring(0, length - 1).trim();
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
        if (this.Password.length() == passwordlength()) {
            if (verifyPassword()) {
                this.Passed = Boolean.valueOf(true);
                test_passed();
                return;
            }
            this.Passed = Boolean.valueOf(false);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    LockScreenActivity.this.Password = "";
                    LockScreenActivity.this.setPswImgState(0);
                    LockScreenActivity.this.password_field.setText("");
                    Toast.makeText(LockScreenActivity.this.getApplicationContext(),
                            "Incorrect password, Please try again", Toast.LENGTH_SHORT).show();
                }
            }, 200);
        } else if (this.Password.length() > passwordlength()) {
            this.Password = "";
            setPswImgState(0);
            this.password_field.setText("");
            Toast.makeText(getApplicationContext(), "Incorrect password, Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void test_passed() {
        sendBroadcast(new Intent().setAction("com.noahmob.AppLocker.applicationpassedtest")
                .putExtra("com.noahmobAppLocker.extra.package.name", getIntent().getStringExtra("locked package name")));
        finish();
    }

    public boolean verifyPassword() {
        if (this.Password == null) {
            return false;
        }
        return this.Password.equals(AppLockerPreference.getInstance(this).getPassword());
    }

    public int passwordlength() {
        return AppLockerPreference.getInstance(this).getPassword().length();
    }

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN").addCategory("android.intent.category.HOME").addFlags(67108864);
        startActivity(intent);
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
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
