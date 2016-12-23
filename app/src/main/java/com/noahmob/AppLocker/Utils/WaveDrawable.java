package com.noahmob.AppLocker.Utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

@SuppressLint({"NewApi"})
public class WaveDrawable extends Drawable {
    protected int alpha;
    private ObjectAnimator alphaAnimator;
    private Interpolator alphaInterpolator;
    private long animationTime;
    private Animator animator;
    private AnimatorSet animatorSet;
    private int color;
    private int radius;
    private ObjectAnimator waveAnimator;
    private boolean waveFromCenter;
    private Interpolator waveInterpolator;
    private Paint wavePaint;
    protected float waveScale;

    public WaveDrawable(int color, int radius, long animationTime) {
        this(color, radius);
        this.animationTime = animationTime;
    }

    public WaveDrawable(int color, int radius) {
        this.animationTime = 500;
        this.color = color;
        this.radius = radius;
        this.waveScale = 0.0f;
        this.alpha = 255;
        this.wavePaint = new Paint(1);
        this.animatorSet = new AnimatorSet();
    }

    public WaveDrawable(int color, int radius, boolean waveFromCenter) {
        this.animationTime = 500;
        this.color = color;
        this.radius = radius;
        this.waveScale = 0.0f;
        this.alpha = 255;
        this.wavePaint = new Paint(1);
        this.animatorSet = new AnimatorSet();
        this.waveFromCenter = waveFromCenter;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        this.wavePaint.setStyle(Style.FILL);
        this.wavePaint.setColor(this.color);
        this.wavePaint.setAlpha(this.alpha);
        if (this.waveFromCenter) {
            canvas.drawCircle((float) bounds.centerX(), (float) bounds.centerY(), ((float) this.radius) * this.waveScale, this.wavePaint);
        } else {
            canvas.drawCircle(0.0f, 0.0f, ((float) this.radius) * this.waveScale, this.wavePaint);
        }
    }

    public void setWaveInterpolator(Interpolator interpolator) {
        this.waveInterpolator = interpolator;
    }

    public void setAlphaInterpolator(Interpolator interpolator) {
        this.alphaInterpolator = interpolator;
    }

    public void startAnimation() {
        this.animator = generateAnimation();
        this.animator.start();
    }

    public void stopAnimation() {
        if (this.animator.isRunning()) {
            this.animator.end();
        }
    }

    public boolean isAnimationRunning() {
        if (this.animator != null) {
            return this.animator.isRunning();
        }
        return false;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter cf) {
        this.wavePaint.setColorFilter(cf);
    }

    public int getOpacity() {
        // 此处不用理会
        return this.wavePaint.getAlpha();
    }

    protected void setWaveScale(float waveScale) {
        this.waveScale = waveScale;
        invalidateSelf();
    }

    protected float getWaveScale() {
        return this.waveScale;
    }

    private Interpolator getDefaultInterpolator() {
        return new LinearInterpolator();
    }

    private Animator generateAnimation() {
        this.waveAnimator = ObjectAnimator.ofFloat(this, "waveScale", new float[]{0.0f, 1.0f});
        this.waveAnimator.setDuration(this.animationTime);
        this.waveAnimator.setInterpolator(getDefaultInterpolator());
        if (this.waveInterpolator != null) {
            this.waveAnimator.setInterpolator(this.waveInterpolator);
        }
        this.alphaAnimator = ObjectAnimator.ofInt(this, "alpha", new int[]{255, 0});
        this.alphaAnimator.setDuration(this.animationTime);
        if (this.alphaInterpolator != null) {
            this.alphaAnimator.setInterpolator(this.alphaInterpolator);
        }
        this.animatorSet.playTogether(new Animator[]{this.waveAnimator, this.alphaAnimator});
        return this.animatorSet;
    }
}
