package com.noahmob.AppLocker;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amigo.applocker.R;
import com.noahmob.AppLocker.Activity.RetrivePswActivity;
import com.noahmob.AppLocker.Utils.ScreenUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import haibison.android.lockpattern.utils.AlpSettings.Display;
import haibison.android.lockpattern.utils.AlpSettings.Security;
import haibison.android.lockpattern.utils.Encrypter;
import haibison.android.lockpattern.utils.InvalidEncrypterException;
import haibison.android.lockpattern.utils.LoadingView;
import haibison.android.lockpattern.utils.UI;
import haibison.android.lockpattern.widget.LockPatternUtils;
import haibison.android.lockpattern.widget.LockPatternView;
import haibison.android.lockpattern.widget.LockPatternView.Cell;
import haibison.android.lockpattern.widget.LockPatternView.DisplayMode;
import haibison.android.lockpattern.widget.LockPatternView.OnPatternListener;

public class LockPatternActivity extends Activity {
    private static final String CLASSNAME = LockPatternActivity.class.getName();
    public static final String ACTION_COMPARE_PATTERN = (CLASSNAME + ".compare_pattern");
    public static final String ACTION_CREATE_PATTERN = (CLASSNAME + ".create_pattern");
    public static final String ACTION_VERIFY_CAPTCHA = (CLASSNAME + ".verify_captcha");
    private static final long DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW = 1000;
    public static final String EXTRA_PATTERN = (CLASSNAME + ".pattern");
    public static final String EXTRA_PENDING_INTENT_CANCELLED = (CLASSNAME + ".pending_intent_cancelled");
    public static final String EXTRA_PENDING_INTENT_FORGOT_PATTERN = (CLASSNAME + ".pending_intent_forgot_pattern");
    public static final String EXTRA_PENDING_INTENT_OK = (CLASSNAME + ".pending_intent_ok");
    public static final String EXTRA_RESULT_RECEIVER = (CLASSNAME + ".result_receiver");
    public static final String EXTRA_RETRY_COUNT = (CLASSNAME + ".retry_count");
    public static final String EXTRA_THEME = (CLASSNAME + ".theme");
    public static final int RESULT_FAILED = 2;
    public static final int RESULT_FORGOT_PATTERN = 3;
    private boolean mAutoSave;
    private Button mBtnCancel;
    private final OnClickListener mBtnCancelOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            LockPatternActivity.this.finishWithNegativeResult(0);
        }
    };
    private Button mBtnConfirm;
    private final OnClickListener mBtnConfirmOnClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (LockPatternActivity.ACTION_CREATE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                if (LockPatternActivity.this.mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    LockPatternActivity.this.mBtnOkCmd = ButtonOkCommand.DONE;
                    LockPatternActivity.this.mLockPatternView.clearPattern();
                    LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                    LockPatternActivity.this.mBtnConfirm.setText(R.string.alp_42447968_cmd_confirm);
                    LockPatternActivity.this.mBtnConfirm.setTextSize(1, 15.0f);
                    LockPatternActivity.this.mBtnConfirm.setEnabled(false);
                    return;
                }
                char[] pattern = LockPatternActivity.this.getIntent().getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                if (LockPatternActivity.this.mAutoSave) {
                    Security.setPattern(LockPatternActivity.this, pattern);
                }
                LockPatternActivity.this.finishWithResultOk(pattern);
            } else if (LockPatternActivity.ACTION_COMPARE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.finish();
                LockPatternActivity.this.startActivity(new Intent(LockPatternActivity.this, RetrivePswActivity.class));
            }
        }
    };
    private ButtonOkCommand mBtnOkCmd;
    private int mCaptchaWiredDots;
    private Encrypter mEncrypter;
    private View mFooter;
    private Intent mIntentResult;
    private LoadingView<Void, Void, Object> mLoadingView;
    private LockPatternView mLockPatternView;
    private final OnPatternListener mLockPatternViewListener = new OnPatternListener() {
        public void onPatternStart() {
            LockPatternActivity.this.mLockPatternView.removeCallbacks(LockPatternActivity.this.mLockPatternViewReloader);
            LockPatternActivity.this.mLockPatternView.setDisplayMode(DisplayMode.Correct);
            if (LockPatternActivity.ACTION_CREATE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_release_finger_when_done);
                LockPatternActivity.this.mBtnConfirm.setEnabled(false);
                if (LockPatternActivity.this.mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    LockPatternActivity.this.getIntent().removeExtra(LockPatternActivity.EXTRA_PATTERN);
                }
            } else if (LockPatternActivity.ACTION_COMPARE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_draw_pattern_to_unlock);
            } else if (LockPatternActivity.ACTION_VERIFY_CAPTCHA.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
            }
        }

        public void onPatternDetected(List<Cell> pattern) {
            if (LockPatternActivity.ACTION_CREATE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.doCheckAndCreatePattern(pattern);
            } else if (LockPatternActivity.ACTION_COMPARE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.doComparePattern(pattern);
            } else if (LockPatternActivity.ACTION_VERIFY_CAPTCHA.equals(LockPatternActivity.this.getIntent().getAction()) && !DisplayMode.Animate.equals(LockPatternActivity.this.mLockPatternView.getDisplayMode())) {
                LockPatternActivity.this.doComparePattern(pattern);
            }
        }

        public void onPatternCleared() {
            LockPatternActivity.this.mLockPatternView.removeCallbacks(LockPatternActivity.this.mLockPatternViewReloader);
            if (LockPatternActivity.ACTION_CREATE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mLockPatternView.setDisplayMode(DisplayMode.Correct);
                LockPatternActivity.this.mBtnConfirm.setEnabled(false);
                if (LockPatternActivity.this.mBtnOkCmd == ButtonOkCommand.CONTINUE) {
                    LockPatternActivity.this.getIntent().removeExtra(LockPatternActivity.EXTRA_PATTERN);
                    LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_draw_an_unlock_pattern);
                    return;
                }
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
            } else if (LockPatternActivity.ACTION_COMPARE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mLockPatternView.setDisplayMode(DisplayMode.Correct);
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_draw_pattern_to_unlock);
            } else if (LockPatternActivity.ACTION_VERIFY_CAPTCHA.equals(LockPatternActivity.this.getIntent().getAction())) {
                LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                ArrayList<Cell> list = LockPatternActivity.this.getIntent().getParcelableArrayListExtra(LockPatternActivity.EXTRA_PATTERN);
                LockPatternActivity.this.mLockPatternView.setPattern(DisplayMode.Animate, list);
            }
        }

        public void onPatternCellAdded(List<Cell> list) {
        }
    };
    private final Runnable mLockPatternViewReloader = new Runnable() {
        public void run() {
            LockPatternActivity.this.mLockPatternView.clearPattern();
            LockPatternActivity.this.mLockPatternViewListener.onPatternCleared();
        }
    };
    private int mMaxRetries;
    private int mMinWiredDots;
    private int mRetryCount = 0;
    private boolean mStealthMode;
    private TextView mTextInfo;
    private View mViewGroupProgressBar;
    private final OnClickListener mViewGroupProgressBarOnClickListener = new OnClickListener() {
        public void onClick(View v) {
        }
    };

    private enum ButtonOkCommand {
        CONTINUE,
        FORGOT_PATTERN,
        DONE
    }

    public static Intent newIntentToCreatePattern(Context context) {
        return new Intent(ACTION_CREATE_PATTERN, null, context, LockPatternActivity.class);
    }

    public static boolean startToCreatePattern(Object caller, Context context, int requestCode) {
        return callStartActivityForResult(caller, newIntentToCreatePattern(context), requestCode);
    }

    public static boolean callStartActivityForResult(Object caller, Intent intent, int requestCode) {
        try {
            Method method = caller.getClass().getMethod("startActivityForResult", new Class[]{Intent.class, Integer.TYPE});
            method.setAccessible(true);
            method.invoke(caller, new Object[]{intent, Integer.valueOf(requestCode)});
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Intent newIntentToComparePattern(Context context, char[] pattern) {
        Intent result = new Intent(ACTION_COMPARE_PATTERN, null, context, LockPatternActivity.class);
        if (pattern != null) {
            result.putExtra(EXTRA_PATTERN, pattern);
        }
        return result;
    }

    public static boolean startToComparePattern(Object caller, Context context, int requestCode, char[] pattern) {
        return callStartActivityForResult(caller, newIntentToComparePattern(context, pattern), requestCode);
    }

    public static Intent newIntentToVerifyCaptcha(Context context) {
        return new Intent(ACTION_VERIFY_CAPTCHA, null, context, LockPatternActivity.class);
    }

    public static boolean startToVerifyCaptcha(Object caller, Context context, int requestCode) {
        return callStartActivityForResult(caller, newIntentToVerifyCaptcha(context), requestCode);
    }

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(1);
        if (getIntent().hasExtra(EXTRA_THEME)) {
            setTheme(getIntent().getIntExtra(EXTRA_THEME, R.style.Alp_42447968_Theme_Dark));
        }
        super.onCreate(savedInstanceState);
        loadSettings();
        this.mIntentResult = new Intent();
        setResult(0, this.mIntentResult);
        initContentView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initContentView();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
            return super.onKeyDown(keyCode, event);
        }
        if (this.mLoadingView != null) {
            this.mLoadingView.cancel(true);
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN").addCategory("android.intent.category.HOME").addFlags(67108864);
        startActivity(intent);
        finish();
        finishWithNegativeResult(0);
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (VERSION.SDK_INT < 11 && event.getAction() == 0 && getWindow().peekDecorView() != null) {
            boolean isOutOfBounds;
            int x = (int) event.getX();
            int y = (int) event.getY();
            int slop = ViewConfiguration.get(this).getScaledWindowTouchSlop();
            View decorView = getWindow().getDecorView();
            if (x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop) {
                isOutOfBounds = true;
            } else {
                isOutOfBounds = false;
            }
            if (isOutOfBounds) {
                finishWithNegativeResult(0);
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    protected void onDestroy() {
        if (this.mLoadingView != null) {
            this.mLoadingView.cancel(true);
        }
        super.onDestroy();
    }

    private void loadSettings() {
        char[] encrypterClass;
        Bundle metaData = null;
        try {
            metaData = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA).metaData;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (metaData == null || !metaData.containsKey(Display.METADATA_MIN_WIRED_DOTS)) {
            this.mMinWiredDots = Display.getMinWiredDots(this);
        } else {
            this.mMinWiredDots = Display.validateMinWiredDots(this, metaData.getInt(Display.METADATA_MIN_WIRED_DOTS));
        }
        if (metaData == null || !metaData.containsKey(Display.METADATA_MAX_RETRIES)) {
            this.mMaxRetries = Display.getMaxRetries(this);
        } else {
            this.mMaxRetries = Display.validateMaxRetries(this, metaData.getInt(Display.METADATA_MAX_RETRIES));
        }
        if (metaData == null || !metaData.containsKey(Security.METADATA_AUTO_SAVE_PATTERN)) {
            this.mAutoSave = Security.isAutoSavePattern(this);
        } else {
            this.mAutoSave = metaData.getBoolean(Security.METADATA_AUTO_SAVE_PATTERN);
        }
        if (metaData == null || !metaData.containsKey(Display.METADATA_CAPTCHA_WIRED_DOTS)) {
            this.mCaptchaWiredDots = Display.getCaptchaWiredDots(this);
        } else {
            this.mCaptchaWiredDots = Display.validateCaptchaWiredDots(this, metaData.getInt(Display.METADATA_CAPTCHA_WIRED_DOTS));
        }
        if (metaData == null || !metaData.containsKey(Display.METADATA_STEALTH_MODE)) {
            this.mStealthMode = Display.isStealthMode(this);
        } else {
            this.mStealthMode = metaData.getBoolean(Display.METADATA_STEALTH_MODE);
        }
        if (metaData == null || !metaData.containsKey(Security.METADATA_ENCRYPTER_CLASS)) {
            encrypterClass = Security.getEncrypterClass(this);
        } else {
            encrypterClass = metaData.getString(Security.METADATA_ENCRYPTER_CLASS).toCharArray();
        }
        if (encrypterClass != null) {
            try {
                this.mEncrypter = (Encrypter) Class.forName(new String(encrypterClass), false, getClassLoader()).newInstance();
            } catch (Throwable th) {
                InvalidEncrypterException invalidEncrypterException = new InvalidEncrypterException();
            }
        }
    }

    private void initContentView() {
        CharSequence infoText = this.mTextInfo != null ? this.mTextInfo.getText() : null;
        Boolean btnOkEnabled = this.mBtnConfirm != null ? this.mBtnConfirm.isEnabled() : null;
        DisplayMode lastDisplayMode = this.mLockPatternView != null ? this.mLockPatternView.getDisplayMode() : null;
        List<Cell> lastPattern = this.mLockPatternView != null ? this.mLockPatternView.getPattern() : null;
        setContentView(R.layout.alp_42447968_lock_pattern_activity);
        UI.adjustDialogSizeForLargeScreens(getWindow());
        this.mTextInfo = (TextView) findViewById(R.id.alp_42447968_textview_info);
        this.mLockPatternView = (LockPatternView) findViewById(R.id.alp_42447968_view_lock_pattern);
        this.mFooter = findViewById(R.id.alp_42447968_viewgroup_footer);
        this.mBtnCancel = (Button) findViewById(R.id.alp_42447968_button_cancel);
        this.mBtnConfirm = (Button) findViewById(R.id.alp_42447968_button_confirm);
        this.mViewGroupProgressBar = findViewById(R.id.alp_42447968_view_group_progress_bar);
        ImageView applogo = (ImageView) findViewById(R.id.img_applogo);
        String blockedPackageName = getIntent().getStringExtra("locked package name");
        if (!(TextUtils.isEmpty(blockedPackageName) || blockedPackageName.equals(getPackageName()))) {
            PackageManager pm = getApplicationContext().getPackageManager();
            try {
                applogo.setImageDrawable(pm.getApplicationIcon(getIntent().getStringExtra("locked package name")));
                LayoutParams para = applogo.getLayoutParams();
                para.height = ScreenUtils.dip2px(this, 68.0f);
                para.width = ScreenUtils.dip2px(this, 68.0f);
                applogo.setLayoutParams(para);
                ApplicationInfo applicationInfo = pm.getApplicationInfo(getIntent().getStringExtra("locked package name"), 0);
            } catch (NameNotFoundException e) {
            }
        }
        this.mViewGroupProgressBar.setOnClickListener(this.mViewGroupProgressBarOnClickListener);
        boolean hapticFeedbackEnabled = false;
        try {
            hapticFeedbackEnabled = android.provider.Settings.System.getInt(getContentResolver(), "haptic_feedback_enabled", 0) != 0;
        } catch (Throwable th) {
        }
        this.mLockPatternView.setTactileFeedbackEnabled(hapticFeedbackEnabled);
        LockPatternView lockPatternView = this.mLockPatternView;
        boolean z = this.mStealthMode && !ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction());
        lockPatternView.setInStealthMode(z);
        this.mLockPatternView.setOnPatternListener(this.mLockPatternViewListener);
        if (!(lastPattern == null || lastDisplayMode == null || ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction()))) {
            this.mLockPatternView.setPattern(lastDisplayMode, lastPattern);
        }
        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
            this.mBtnCancel.setOnClickListener(this.mBtnCancelOnClickListener);
            this.mBtnConfirm.setOnClickListener(this.mBtnConfirmOnClickListener);
            this.mBtnCancel.setVisibility(View.VISIBLE);
            this.mFooter.setVisibility(View.VISIBLE);
            if (infoText != null) {
                this.mTextInfo.setText(infoText);
            } else {
                this.mTextInfo.setText(R.string.alp_42447968_msg_draw_an_unlock_pattern);
            }
            if (this.mBtnOkCmd == null) {
                this.mBtnOkCmd = ButtonOkCommand.CONTINUE;
            }
            switch (this.mBtnOkCmd) {
                case CONTINUE:
                    this.mBtnConfirm.setText(R.string.alp_42447968_cmd_continue);
                    this.mBtnConfirm.setTextSize(1, 15.0f);
                    break;
                case DONE:
                    this.mBtnConfirm.setText(R.string.alp_42447968_cmd_confirm);
                    this.mBtnConfirm.setTextSize(1, 15.0f);
                    break;
            }
            if (btnOkEnabled != null) {
                this.mBtnConfirm.setEnabled(btnOkEnabled);
            }
        } else if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
            if (TextUtils.isEmpty(infoText)) {
                this.mTextInfo.setText(R.string.alp_42447968_msg_draw_pattern_to_unlock);
            } else {
                this.mTextInfo.setText(infoText);
            }
            this.mBtnConfirm.setOnClickListener(this.mBtnConfirmOnClickListener);
            this.mBtnConfirm.setText(R.string.alp_42447968_cmd_forgot_pattern);
            this.mBtnConfirm.setTextSize(1, 10.0f);
            this.mBtnConfirm.setEnabled(true);
            this.mFooter.setVisibility(View.VISIBLE);
        } else if (ACTION_VERIFY_CAPTCHA.equals(getIntent().getAction())) {
            ArrayList<Cell> pattern;
            this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
            if (getIntent().hasExtra(EXTRA_PATTERN)) {
                pattern = getIntent().getParcelableArrayListExtra(EXTRA_PATTERN);
            } else {
                Intent intent = getIntent();
                String str = EXTRA_PATTERN;
                pattern = LockPatternUtils.genCaptchaPattern(this.mCaptchaWiredDots);
                intent.putParcelableArrayListExtra(str, pattern);
            }
            this.mLockPatternView.setPattern(DisplayMode.Animate, pattern);
        }
    }

    private void doComparePattern(final List<Cell> pattern) {
        if (pattern != null) {
            this.mLoadingView = new LoadingView<Void, Void, Object>(this, this.mViewGroupProgressBar) {
                protected Object doInBackground(Void... params) {
                    if (LockPatternActivity.ACTION_COMPARE_PATTERN.equals(LockPatternActivity.this.getIntent().getAction())) {
                        char[] currentPattern = LockPatternActivity.this.getIntent().getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                        if (currentPattern == null) {
                            currentPattern = Security.getPattern(LockPatternActivity.this);
                        }
                        if (currentPattern != null) {
                            if (LockPatternActivity.this.mEncrypter != null) {
                                return Boolean.valueOf(pattern.equals(LockPatternActivity.this.mEncrypter.decrypt(LockPatternActivity.this, currentPattern)));
                            }
                            return Boolean.valueOf(Arrays.equals(currentPattern, LockPatternUtils.patternToSha1(pattern).toCharArray()));
                        }
                    } else if (LockPatternActivity.ACTION_VERIFY_CAPTCHA.equals(LockPatternActivity.this.getIntent().getAction())) {
                        return Boolean.valueOf(pattern.equals(LockPatternActivity.this.getIntent().getParcelableArrayListExtra(LockPatternActivity.EXTRA_PATTERN)));
                    }
                    return Boolean.valueOf(false);
                }

                protected void onPostExecute(Object result) {
                    super.onPostExecute(result);
                    if (((Boolean) result).booleanValue()) {
                        LockPatternActivity.this.finishWithResultOk(null);
                        return;
                    }
                    LockPatternActivity.this.mRetryCount = LockPatternActivity.this.mRetryCount + 1;
                    LockPatternActivity.this.mIntentResult.putExtra(LockPatternActivity.EXTRA_RETRY_COUNT, LockPatternActivity.this.mRetryCount);
                    if (LockPatternActivity.this.mRetryCount >= LockPatternActivity.this.mMaxRetries) {
                        LockPatternActivity.this.finishWithNegativeResult(2);
                        return;
                    }
                    LockPatternActivity.this.mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                    LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_try_again);
                    LockPatternActivity.this.mLockPatternView.postDelayed(LockPatternActivity.this.mLockPatternViewReloader, LockPatternActivity.DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
                }
            };
            this.mLoadingView.execute(new Void[0]);
        }
    }

    private void doCheckAndCreatePattern(final List<Cell> pattern) {
        if (pattern.size() < this.mMinWiredDots) {
            this.mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            this.mTextInfo.setText(getResources().getQuantityString(R.plurals.alp_42447968_pmsg_connect_x_dots, this.mMinWiredDots, new Object[]{Integer.valueOf(this.mMinWiredDots)}));
            this.mLockPatternView.postDelayed(this.mLockPatternViewReloader, DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
        } else if (getIntent().hasExtra(EXTRA_PATTERN)) {
            this.mLoadingView = new LoadingView<Void, Void, Object>(this, this.mViewGroupProgressBar) {
                protected Object doInBackground(Void... params) {
                    if (LockPatternActivity.this.mEncrypter != null) {
                        return Boolean.valueOf(pattern.equals(LockPatternActivity.this.mEncrypter.decrypt(LockPatternActivity.this, LockPatternActivity.this.getIntent().getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN))));
                    }
                    return Boolean.valueOf(Arrays.equals(LockPatternActivity.this.getIntent().getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN), LockPatternUtils.patternToSha1(pattern).toCharArray()));
                }

                protected void onPostExecute(Object result) {
                    super.onPostExecute(result);
                    if (((Boolean) result).booleanValue()) {
                        LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_your_new_unlock_pattern);
                        LockPatternActivity.this.mBtnConfirm.setEnabled(true);
                        return;
                    }
                    LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_redraw_pattern_to_confirm);
                    LockPatternActivity.this.mBtnConfirm.setEnabled(false);
                    LockPatternActivity.this.mLockPatternView.setDisplayMode(DisplayMode.Wrong);
                    LockPatternActivity.this.mLockPatternView.postDelayed(LockPatternActivity.this.mLockPatternViewReloader, LockPatternActivity.DELAY_TIME_TO_RELOAD_LOCK_PATTERN_VIEW);
                }
            };
            this.mLoadingView.execute(new Void[0]);
        } else {
            this.mLoadingView = new LoadingView<Void, Void, Object>(this, this.mViewGroupProgressBar) {
                protected Object doInBackground(Void... params) {
                    if (LockPatternActivity.this.mEncrypter != null) {
                        return LockPatternActivity.this.mEncrypter.encrypt(LockPatternActivity.this, pattern);
                    }
                    return LockPatternUtils.patternToSha1(pattern).toCharArray();
                }

                protected void onPostExecute(Object result) {
                    super.onPostExecute(result);
                    LockPatternActivity.this.getIntent().putExtra(LockPatternActivity.EXTRA_PATTERN, (char[]) result);
                    LockPatternActivity.this.mTextInfo.setText(R.string.alp_42447968_msg_pattern_recorded);
                    LockPatternActivity.this.mBtnConfirm.setEnabled(true);
                }
            };
            this.mLoadingView.execute(new Void[0]);
        }
    }

    private void finishWithResultOk(char[] pattern) {
        if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
            this.mIntentResult.putExtra(EXTRA_PATTERN, pattern);
        } else {
            this.mIntentResult.putExtra(EXTRA_RETRY_COUNT, this.mRetryCount + 1);
            test_passed();
        }
        setResult(-1, this.mIntentResult);
        ResultReceiver receiver = (ResultReceiver) getIntent().getParcelableExtra(EXTRA_RESULT_RECEIVER);
        if (receiver != null) {
            Bundle bundle = new Bundle();
            if (ACTION_CREATE_PATTERN.equals(getIntent().getAction())) {
                bundle.putCharArray(EXTRA_PATTERN, pattern);
            } else {
                bundle.putInt(EXTRA_RETRY_COUNT, this.mRetryCount + 1);
            }
            receiver.send(-1, bundle);
        }
        PendingIntent pi = (PendingIntent) getIntent().getParcelableExtra(EXTRA_PENDING_INTENT_OK);
        if (pi != null) {
            try {
                pi.send(this, -1, this.mIntentResult);
            } catch (Throwable t) {
                Log.e(CLASSNAME, "Error sending PendingIntent: " + pi, t);
            }
        }
        finish();
    }

    private void test_passed() {
        System.out.println("tested");
        sendBroadcast(new Intent().setAction("com.noahmob.AppLocker.applicationpassedtest").putExtra("com.noahmob.AppLocker.extra.package.name", getIntent().getStringExtra("locked package name")));
    }

    private void finishWithNegativeResult(int resultCode) {
        if (getIntent() != null) {
            if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                this.mIntentResult.putExtra(EXTRA_RETRY_COUNT, this.mRetryCount);
            }
            setResult(resultCode, this.mIntentResult);
            ResultReceiver receiver = (ResultReceiver) getIntent().getParcelableExtra(EXTRA_RESULT_RECEIVER);
            if (receiver != null) {
                Bundle resultBundle = null;
                if (ACTION_COMPARE_PATTERN.equals(getIntent().getAction())) {
                    resultBundle = new Bundle();
                    resultBundle.putInt(EXTRA_RETRY_COUNT, this.mRetryCount);
                }
                receiver.send(resultCode, resultBundle);
            }
            PendingIntent pi = (PendingIntent) getIntent().getParcelableExtra(EXTRA_PENDING_INTENT_CANCELLED);
            if (pi != null) {
                try {
                    pi.send(this, resultCode, this.mIntentResult);
                } catch (Throwable t) {
                    Log.e(CLASSNAME, "Error sending PendingIntent: " + pi, t);
                }
            }
            finish();
        }
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
}
