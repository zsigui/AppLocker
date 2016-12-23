package com.noahmob.AppLocker.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.amigo.applocker.R;
import com.google.android.exoplayer.C;

public class SlideSwitch extends View {
    private static final int BACK_RADIA = 14;
    private static final int COLOR_BACKGROUND = Color.parseColor("#77999999");
    private static final int COLOR_THEME = Color.parseColor("#ff005500");
    private static final int FRONE_RADIA = 10;
    private static final int RIM_SIZE = 5;
    public static final int SHAPE_CIRCLE = 2;
    public static final int SHAPE_RECT = 1;
    private int alpha;
    private Rect backRect;
    private int color_theme;
    private int diffX;
    private int eventLastX;
    private int eventStartX;
    private Rect frontRect;
    private int frontRect_left;
    private int frontRect_left_begin;
    public boolean isOpen;
    private SlideListener listener;
    private int max_left;
    private int min_left;
    private Paint paint;
    private int shape;
    private boolean slideable;

    private int[] customAttrs = new int[]{R.attr.themeColor, R.attr.isOpen, R.attr.shape};

    public interface SlideListener {
        void close();

        void open();
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.frontRect_left_begin = 5;
        this.diffX = 0;
        this.slideable = true;
        this.listener = null;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        TypedArray a = context.obtainStyledAttributes(attrs, customAttrs);
        this.color_theme = a.getColor(0, COLOR_THEME);
        this.isOpen = a.getBoolean(1, false);
        this.shape = a.getInt(2, 1);
        a.recycle();
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context) {
        this(context, null);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureDimension(140, widthMeasureSpec);
        int height = measureDimension(70, heightMeasureSpec);
        if (this.shape == 2 && width < height) {
            width = height * 2;
        }
        setMeasuredDimension(width, height);
        initDrawingVal();
    }

    public void initDrawingVal() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        this.backRect = new Rect(0, 0, width, height);
        this.min_left = 5;
        if (this.shape == 1) {
            this.max_left = width / 2;
        } else {
            this.max_left = (width - (height - 10)) - 5;
        }
        if (this.isOpen) {
            this.frontRect_left = this.max_left;
            this.alpha = 255;
        } else {
            this.frontRect_left = 5;
            this.alpha = 0;
        }
        this.frontRect_left_begin = this.frontRect_left;
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == C.ENCODING_PCM_32BIT) {
            return specSize;
        }
        int result = defaultSize;
        if (specMode == Integer.MIN_VALUE) {
            return Math.min(result, specSize);
        }
        return result;
    }

    protected void onDraw(Canvas canvas) {
        if (this.shape == 1) {
            this.paint.setColor(COLOR_BACKGROUND);
            canvas.drawRect(this.backRect, this.paint);
            this.paint.setColor(this.color_theme);
            this.paint.setAlpha(this.alpha);
            canvas.drawRect(this.backRect, this.paint);
            this.frontRect = new Rect(this.frontRect_left, 5, (this.frontRect_left + (getMeasuredWidth() / 2)) - 5, getMeasuredHeight() - 5);
            this.paint.setColor(-1);
            canvas.drawRect(this.frontRect, this.paint);
            return;
        }
        int radius = (this.backRect.height() / 2) - 5;
        this.paint.setColor(COLOR_BACKGROUND);
        canvas.drawRoundRect(new RectF(this.backRect), 14.0f, 14.0f, this.paint);
        this.paint.setColor(this.color_theme);
        this.paint.setAlpha(this.alpha);
        canvas.drawRoundRect(new RectF(this.backRect), 14.0f, 14.0f, this.paint);
        this.frontRect = new Rect(this.frontRect_left, 5, (this.frontRect_left + this.backRect.height()) - 10, this.backRect.height() - 5);
        this.paint.setColor(-1);
        canvas.drawRoundRect(new RectF(this.frontRect), 10.0f, 10.0f, this.paint);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.slideable) {
            return super.onTouchEvent(event);
        }
        switch (MotionEventCompat.getActionMasked(event)) {
            case 0:
                this.eventStartX = (int) event.getRawX();
                return true;
            case 1:
            case 3:
                boolean toRight;
                int wholeX = (int) (event.getRawX() - ((float) this.eventStartX));
                this.frontRect_left_begin = this.frontRect_left;
                if (this.frontRect_left_begin > this.max_left / 2) {
                    toRight = true;
                } else {
                    toRight = false;
                }
                if (Math.abs(wholeX) < 3) {
                    if (toRight) {
                        toRight = false;
                    } else {
                        toRight = true;
                    }
                }
                moveToDest(toRight);
                return true;
            case 2:
                this.eventLastX = (int) event.getRawX();
                this.diffX = this.eventLastX - this.eventStartX;
                int tempX = this.diffX + this.frontRect_left_begin;
                if (tempX > this.max_left) {
                    tempX = this.max_left;
                }
                if (tempX < this.min_left) {
                    tempX = this.min_left;
                }
                if (tempX < this.min_left || tempX > this.max_left) {
                    return true;
                }
                this.frontRect_left = tempX;
                this.alpha = (int) ((255.0f * ((float) tempX)) / ((float) this.max_left));
                invalidateView();
                return true;
            default:
                return true;
        }
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setSlideListener(SlideListener listener) {
        this.listener = listener;
    }

    public void moveToDest(final boolean toRight) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    SlideSwitch.this.listener.open();
                } else {
                    SlideSwitch.this.listener.close();
                }
            }
        };
        new Thread(new Runnable() {
            public void run() {
                if (toRight) {
                    while (SlideSwitch.this.frontRect_left <= SlideSwitch.this.max_left) {
                        SlideSwitch.this.alpha = (int) ((((float) SlideSwitch.this.frontRect_left) * 255.0f) / ((float) SlideSwitch.this.max_left));
                        SlideSwitch.this.invalidateView();
                        SlideSwitch.this.frontRect_left = SlideSwitch.this.frontRect_left + 3;
                        try {
                            Thread.sleep(3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    SlideSwitch.this.alpha = 255;
                    SlideSwitch.this.frontRect_left = SlideSwitch.this.max_left;
                    SlideSwitch.this.isOpen = true;
                    if (SlideSwitch.this.listener != null) {
                        handler.sendEmptyMessage(1);
                    }
                    SlideSwitch.this.frontRect_left_begin = SlideSwitch.this.max_left;
                    return;
                }
                while (SlideSwitch.this.frontRect_left >= SlideSwitch.this.min_left) {
                    SlideSwitch.this.alpha = (int) ((((float) SlideSwitch.this.frontRect_left) * 255.0f) / ((float) SlideSwitch.this.max_left));
                    SlideSwitch.this.invalidateView();
                    SlideSwitch.this.frontRect_left = SlideSwitch.this.frontRect_left - 3;
                    try {
                        Thread.sleep(3);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                SlideSwitch.this.alpha = 0;
                SlideSwitch.this.frontRect_left = SlideSwitch.this.min_left;
                SlideSwitch.this.isOpen = false;
                if (SlideSwitch.this.listener != null) {
                    handler.sendEmptyMessage(0);
                }
                SlideSwitch.this.frontRect_left_begin = SlideSwitch.this.min_left;
            }
        }).start();
    }

    public void setState(boolean isOpen) {
        this.isOpen = isOpen;
        initDrawingVal();
        invalidateView();
        if (this.listener == null) {
            return;
        }
        if (isOpen) {
            this.listener.open();
        } else {
            this.listener.close();
        }
    }

    public void setShapeType(int shapeType) {
        this.shape = shapeType;
    }

    public void setSlideable(boolean slideable) {
        this.slideable = slideable;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.isOpen = bundle.getBoolean("isOpen");
            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putBoolean("isOpen", this.isOpen);
        return bundle;
    }
}
