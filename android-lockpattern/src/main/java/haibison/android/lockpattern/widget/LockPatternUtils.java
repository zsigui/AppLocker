/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package haibison.android.lockpattern.widget;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import haibison.android.lockpattern.collect.Lists;
import haibison.android.lockpattern.utils.Randoms;
import haibison.android.underdogs.NonNull;

import static haibison.android.lockpattern.Alp.TAG;
import static haibison.android.lockpattern.BuildConfig.DEBUG;

/**
 * Utilities for the lock pattern and its settings.
 */
public class LockPatternUtils {

    /**
     * Used for debugging...
     */
    private static final String CLASSNAME = LockPatternUtils.class.getSimpleName();

    /**
     * "UTF-8"
     */
    public static final String UTF8 = "UTF-8";

    /**
     * "SHA-1"
     */
    public static final String SHA1 = "SHA-1";

    // Singleton class
    private LockPatternUtils() {}

    /**
     * Deserialize a pattern.
     *
     * @param string The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    @NonNull
    public static List<LockPatternView.Cell> stringToPattern(@NonNull String string) {
        final List<LockPatternView.Cell> result = Lists.newArrayList();

        try {
            final byte[] bytes = string.getBytes(UTF8);
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                result.add(LockPatternView.Cell.of(b / 3, b % 3));
            }//for
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return result;
    }//stringToPattern()

    /**
     * Serialize a pattern.
     *
     * @param pattern The pattern.
     * @return The pattern in string form.
     */
    @NonNull
    public static String patternToString(@NonNull List<LockPatternView.Cell> pattern) {
        if (pattern == null) return "";
        final int patternSize = pattern.size();

        final byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.row * 3 + cell.column);
        }//for
        try {
            return new String(res, UTF8);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        }
    }//patternToString()

    /**
     * Serializes a pattern
     *
     * @param pattern The pattern
     * @return The SHA-1 string of the pattern got from {@link #patternToString(List)}
     */
    @NonNull
    public static String patternToSha1(@NonNull List<LockPatternView.Cell> pattern) {
        try {
            final MessageDigest md = MessageDigest.getInstance(SHA1);
            md.update(patternToString(pattern).getBytes(UTF8));

            final byte[] digest = md.digest();
            final BigInteger bi = new BigInteger(1, digest);
            return String.format((Locale) null, "%0" + (digest.length * 2) + "x", bi).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            return "";
        }
    }//patternToSha1()

    /**
     * Generates a random "CAPTCHA" pattern. By saying "CAPTCHA", this method ensures that the generated pattern is easy for the user to re-draw.
     * <p>
     * <strong>Notes:</strong> This method is <b>not</b> optimized and <b>not</b> benchmarked yet for large size of the pattern's matrix.
     * Currently it works fine with a matrix of {@code 3x3} cells. Be careful when the size increases.
     *
     * @param size the size of the pattern to be generated.
     * @return the generated pattern.
     * @throws IndexOutOfBoundsException if {@code size <= 0} or {@code size > }
     *                                   {@link haibison.android.lockpattern.widget.LockPatternView#MATRIX_SIZE LockPatternView.MATRIX_SIZE}.
     * @author Hai Bison
     * @since v2.7 beta
     */
    @NonNull
    public static ArrayList<LockPatternView.Cell> genCaptchaPattern(final int size) throws IndexOutOfBoundsException {
        if (size <= 0 || size > LockPatternView.MATRIX_SIZE) throw new IndexOutOfBoundsException(
                "`size` must be in range [1, `LockPatternView.MATRIX_SIZE`]"
        );

        final List<Integer> usedIds = Lists.newArrayList();
        int lastId = Randoms.randInt(LockPatternView.MATRIX_SIZE);
        usedIds.add(lastId);

        while (usedIds.size() < size) {
            // We start from an empty matrix, so there's always a break point to exit this loop.

            if (DEBUG) Log.d(TAG, CLASSNAME + " >> lastId = " + lastId);

            final int lastRow = lastId / LockPatternView.MATRIX_WIDTH;
            final int lastCol = lastId % LockPatternView.MATRIX_WIDTH;

            // This is the max available rows/columns that we can reach from the cell of `lastId` to the border of the matrix.
            final int maxDistance = Math.max(
                    Math.max(lastRow, LockPatternView.MATRIX_WIDTH - lastRow),
                    Math.max(lastCol, LockPatternView.MATRIX_WIDTH - lastCol)
            );

            lastId = -1;

            // Starting from `distance` = 1, find the closest-available neighbour value of the cell [lastRow, lastCol].
            for (int distance = 1; distance <= maxDistance; distance++) {
                // Now we have a square surrounding the current cell. We call it ABCD, in which A is top-left, and C is bottom-right.
                final int rowA = lastRow - distance;
                final int colA = lastCol - distance;
                final int rowC = lastRow + distance;
                final int colC = lastCol + distance;

                int[] randomValues;

                // Process randomly AB, BC, CD, and DA. Break the loop as soon as we find one value.
                final int[] lines = Randoms.randIntArray(4);
                for (final int line : lines) {
                    switch (line) {
                    case 0: {
                        if (rowA >= 0) {
                            randomValues = Randoms.randIntArray(Math.max(0, colA), Math.min(LockPatternView.MATRIX_WIDTH, colC + 1));
                            for (final int c : randomValues) {
                                lastId = rowA * LockPatternView.MATRIX_WIDTH + c;
                                if (usedIds.contains(lastId)) lastId = -1;
                                else break;
                            }//for
                        }//if

                        break;
                    }//AB

                    case 1: {
                        if (colC < LockPatternView.MATRIX_WIDTH) {
                            randomValues = Randoms.randIntArray(Math.max(0, rowA + 1), Math.min(LockPatternView.MATRIX_WIDTH, rowC + 1));
                            for (final int r : randomValues) {
                                lastId = r * LockPatternView.MATRIX_WIDTH + colC;
                                if (usedIds.contains(lastId)) lastId = -1;
                                else break;
                            }//for
                        }//if

                        break;
                    }//BC

                    case 2: {
                        if (rowC < LockPatternView.MATRIX_WIDTH) {
                            randomValues = Randoms.randIntArray(Math.max(0, colA), Math.min(LockPatternView.MATRIX_WIDTH, colC));
                            for (final int c : randomValues) {
                                lastId = rowC * LockPatternView.MATRIX_WIDTH + c;
                                if (usedIds.contains(lastId)) lastId = -1;
                                else break;
                            }//for
                        }//if

                        break;
                    }//DC

                    case 3: {
                        if (colA >= 0) {
                            randomValues = Randoms.randIntArray(Math.max(0, rowA + 1), Math.min(LockPatternView.MATRIX_WIDTH, rowC));
                            for (final int r : randomValues) {
                                lastId = r * LockPatternView.MATRIX_WIDTH + colA;
                                if (usedIds.contains(lastId)) lastId = -1;
                                else break;
                            }//for
                        }//if

                        break;
                    }//AD
                    }

                    if (lastId >= 0) break;
                }//for line

                if (lastId >= 0) break;
            }//for distance

            usedIds.add(lastId);
        }//while

        final ArrayList<LockPatternView.Cell> result = Lists.newArrayList();
        for (final int id : usedIds) result.add(LockPatternView.Cell.of(id));

        return result;
    }//genCaptchaPattern()

}