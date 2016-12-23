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
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.util.TypedValue;

import haibison.android.underdogs.AttrRes;
import haibison.android.underdogs.NonNull;

/**
 * Resources' utilities.
 *
 * @author Hai Bison
 */
public class ResUtils {

    // Singleton class
    private ResUtils() {}

    /**
     * Convenient method for {@link Context#getTheme()} and {@link Theme#resolveAttribute(int, TypedValue, boolean)}.
     *
     * @param context the context.
     * @param resAttr The resource identifier of the desired theme attribute.
     * @return the resource ID that {@link TypedValue#resourceId} points to, or {@code 0} if not found.
     */
    public static int resolveResourceId(@NonNull Context context, @AttrRes int resAttr) {
        return resolveResourceId(context, resAttr, 0);
    }//resolveResourceId()

    /**
     * Convenient method for {@link Context#getTheme()} and {@link Theme#resolveAttribute(int, TypedValue, boolean)}.
     *
     * @param context      the context.
     * @param resAttr      The resource identifier of the desired theme attribute.
     * @param defaultValue the default value if cannot resolve {@code resId}.
     * @return the resource ID that {@link TypedValue#resourceId} points to, or {@code defaultValue} if not found.
     */
    public static int resolveResourceId(@NonNull Context context, @AttrRes int resAttr, int defaultValue) {
        final TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(resAttr, typedValue, true)) return typedValue.resourceId;
        return defaultValue;
    }//resolveResourceId()

}