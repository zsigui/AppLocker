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

package haibison.android.lockpattern;

import java.util.Calendar;
import java.util.GregorianCalendar;

import haibison.android.underdogs.NonNull;

/**
 * Some constants about the library.
 */
public final class Alp {

    // Singleton class
    private Alp() {}

    /**
     * The library name.
     */
    @NonNull
    public static final String LIB_NAME = "android-lockpattern";

    /**
     * The library version name.
     */
    @NonNull
    public static final String LIB_VERSION_NAME = "11.0.0";

    /**
     * Release date.
     */
    @NonNull
    public static final Calendar RELEASE_DATE = new GregorianCalendar(2016, Calendar.OCTOBER, 21);

    /**
     * This unique ID is used for some stuffs such as preferences' file name.
     *
     * @since v2.6 beta
     */
    @NonNull
    public static final String UID = "a6eedbe5-1cf9-4684-8134-ad4ec9f6a131";

    /**
     * Tag, which can be used for logging...
     */
    @NonNull
    public static final String TAG = "ALP_42447968_" + LIB_VERSION_NAME;

}