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

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;

import haibison.android.underdogs.CallSuper;
import haibison.android.underdogs.NonNull;
import haibison.android.underdogs.Nullable;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * An implementation of {@link AsyncTask}, used to show a view while doing some background tasks, then hide it when done.
 *
 * @author Hai Bison
 */
public abstract class LoadingView<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final View view;

    /**
     * Delay time in milliseconds. Default delay is half a second.
     */
    private long delayTime = SECOND_IN_MILLIS / 2;

    /**
     * Flag to use along with {@link #delayTime}
     */
    private boolean finished = false;

    private Throwable lastException;

    /**
     * Creates new instance.
     *
     * @param context the context.
     * @param view    the view to be controlled by this async task.
     */
    public LoadingView(@NonNull Context context, @NonNull View view) {
        this.view = view;
    }//LoadingView()

    /**
     * If you override this method, you must call its super method at beginning of the method.
     */
    @Override
    @CallSuper
    protected void onPreExecute() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (finished == false) view.setVisibility(View.VISIBLE);
            }//run()

        }, getDelayTime());
    }//onPreExecute()

    /**
     * If you override this method, you must call its super method at beginning of the method.
     */
    @Override
    @CallSuper
    protected void onPostExecute(Result result) {
        doFinish();
    }//onPostExecute()

    /**
     * If you override this method, you must call its super method at beginning of the method.
     */
    @Override
    @CallSuper
    protected void onCancelled() {
        doFinish();
        super.onCancelled();
    }//onCancelled()

    private void doFinish() {
        finished = true;
        view.setVisibility(View.GONE);
    }//doFinish()

    /**
     * Gets the delay time before showing the view.
     *
     * @return the delay time, in milliseconds.
     */
    public long getDelayTime() {
        return delayTime;
    }//getDelayTime()

    /**
     * Sets the delay time before showing the view.
     *
     * @param delayTime the delay time to set, in milliseconds.
     * @return the instance of this object, for chaining multiple calls into a single statement.
     */
    @NonNull
    public LoadingView<Params, Progress, Result> setDelayTime(final int delayTime) {
        this.delayTime = delayTime >= 0 ? delayTime : 0;
        return this;
    }//setDelayTime()

    /**
     * Sets last exception. This method is useful in case an exception raises inside {@link #doInBackground(Object[])}.
     *
     * @param t {@link Throwable}
     */
    protected void setLastException(@Nullable Throwable t) {
        lastException = t;
    }//setLastException()

    /**
     * Gets last exception.
     *
     * @return {@link Throwable}
     */
    @Nullable
    public Throwable getLastException() {
        return lastException;
    }//getLastException()

}