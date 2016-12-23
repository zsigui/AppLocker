/*
 *   Copyright 2012 Hai Bison
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package haibison.android.lockpattern.utils;

import android.os.Handler;

import java.util.List;

import haibison.android.lockpattern.collect.Lists;
import haibison.android.underdogs.NonNull;
import haibison.android.underdogs.Nullable;

/**
 * Float animator.
 *
 * @author Hai Bison
 */
public class FloatAnimator {

    /**
     * Event listener.
     *
     * @author Hai Bison
     */
    public interface EventListener {

        /**
         * Will be called when animation starts.
         *
         * @param animator the animator.
         */
        void onAnimationStart(@NonNull FloatAnimator animator);

        /**
         * Will be called when new animated value is calculated.
         *
         * @param animator the animator.
         */
        void onAnimationUpdate(@NonNull FloatAnimator animator);

        /**
         * Will be called when animation cancels.
         *
         * @param animator the animator.
         */
        void onAnimationCancel(@NonNull FloatAnimator animator);

        /**
         * Will be called when animation ends.
         *
         * @param animator the animator.
         */
        void onAnimationEnd(@NonNull FloatAnimator animator);

    }// EventListener

    /**
     * Simple event listener.
     *
     * @author Hai Bison
     */
    public static class SimpleEventListener implements EventListener {

        @Override
        public void onAnimationStart(@NonNull FloatAnimator animator) {
        }//onAnimationStart()

        @Override
        public void onAnimationUpdate(@NonNull FloatAnimator animator) {
        }//onAnimationUpdate()

        @Override
        public void onAnimationCancel(@NonNull FloatAnimator animator) {
        }//onAnimationCancel()

        @Override
        public void onAnimationEnd(@NonNull FloatAnimator animator) {
        }//onAnimationEnd()

    }// SimpleEventListener

    /**
     * Animation delay, in milliseconds.
     */
    private static final long ANIMATION_DELAY = 1;

    private final float startValue, endValue;
    private final long duration;
    private float animatedValue;

    private List<EventListener> eventListeners;
    private Handler handler;
    private long startTime;

    /**
     * Creates new instance.
     *
     * @param start    start value.
     * @param end      end value.
     * @param duration duration, in milliseconds. This should not be long, as delay value between animation frames is just 1 millisecond.
     */
    public FloatAnimator(float start, float end, long duration) {
        startValue = start;
        endValue = end;
        this.duration = duration;

        animatedValue = startValue;
    }// FloatAnimator()

    /**
     * Adds event listener.
     *
     * @param listener the listener.
     */
    public void addEventListener(@Nullable EventListener listener) {
        if (listener == null) return;

        if (eventListeners == null) eventListeners = Lists.newArrayList();
        eventListeners.add(listener);
    }// addEventListener()

    /**
     * Gets animated value.
     *
     * @return animated value.
     */
    public float getAnimatedValue() {
        return animatedValue;
    }// getAnimatedValue()

    /**
     * Starts animating.
     */
    public void start() {
        if (handler != null) return;

        notifyAnimationStart();

        startTime = System.currentTimeMillis();

        handler = new Handler();
        handler.post(new Runnable() {

            @Override
            public void run() {
                final Handler handler = FloatAnimator.this.handler;
                if (handler == null) return;

                final long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime > duration) {
                    FloatAnimator.this.handler = null;
                    notifyAnimationEnd();
                } else {
                    float fraction = duration > 0 ? (float) (elapsedTime) / duration : 1f;
                    float delta = endValue - startValue;
                    animatedValue = startValue + delta * fraction;

                    notifyAnimationUpdate();
                    handler.postDelayed(this, ANIMATION_DELAY);
                }
            }// run()

        });
    }// start()

    /**
     * Cancels animating.
     */
    public void cancel() {
        if (handler == null) return;

        handler.removeCallbacksAndMessages(null);
        handler = null;

        notifyAnimationCancel();
        notifyAnimationEnd();
    }// cancel()

    /**
     * Notifies all listeners that animation starts.
     */
    protected void notifyAnimationStart() {
        final List<EventListener> listeners = eventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationStart(this);
        }// if
    }// notifyAnimationStart()

    /**
     * Notifies all listeners that animation updates.
     */
    protected void notifyAnimationUpdate() {
        final List<EventListener> listeners = eventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationUpdate(this);
        }// if
    }// notifyAnimationUpdate()

    /**
     * Notifies all listeners that animation cancels.
     */
    protected void notifyAnimationCancel() {
        final List<EventListener> listeners = eventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationCancel(this);
        }// if
    }// notifyAnimationCancel()

    /**
     * Notifies all listeners that animation ends.
     */
    protected void notifyAnimationEnd() {
        final List<EventListener> listeners = eventListeners;
        if (listeners != null) {
            for (EventListener listener : listeners)
                listener.onAnimationEnd(this);
        }// if
    }// notifyAnimationEnd()

}