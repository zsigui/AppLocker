package com.noahmob.AppLocker.upgrade;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.amigo.applocker.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OptimUtils {
    public static boolean isSupportDoubleSd = false;
    private static HashMap<String, Boolean> sdcardMountStateMap = new HashMap(4);

    public static String getVersionCodeCatV(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            versionName = BuildConfig.VERSION_NAME;
        }
        return "V " + versionName;
    }

    public static String getVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return BuildConfig.VERSION_NAME;
        }
    }

    public static int getScreenWidth(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
    }

    public static String getReadableSize(long filesize, DecimalFormat df) {
        StringBuilder mstrbuf = new StringBuilder();
        DecimalFormatSymbols localDecimalFormatSymbols = df.getDecimalFormatSymbols();
        localDecimalFormatSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(localDecimalFormatSymbols);
        if (filesize < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            mstrbuf.append(filesize);
            mstrbuf.append(" ");
            mstrbuf.append("B");
        } else if (filesize < 1048576) {
            mstrbuf.append(df.format(((double) filesize) / 1024.0d));
            mstrbuf.append(" ");
            mstrbuf.append("KB");
        } else if (filesize < 1073741824) {
            mstrbuf.append(df.format(((double) filesize) / 1048576.0d));
            mstrbuf.append(" ");
            mstrbuf.append("MB");
        } else {
            mstrbuf.append(df.format(((double) filesize) / 1.073741824E9d));
            mstrbuf.append(" ");
            mstrbuf.append("GB");
        }
        return mstrbuf.toString();
    }

    public static String getReadableSize(long filesize) {
        DecimalFormat df = new DecimalFormat("#.00");
        StringBuilder mstrbuf = new StringBuilder();
        DecimalFormatSymbols localDecimalFormatSymbols = df.getDecimalFormatSymbols();
        localDecimalFormatSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(localDecimalFormatSymbols);
        if (filesize < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            mstrbuf.append(filesize);
            mstrbuf.append(" ");
            mstrbuf.append("B");
        } else if (filesize < 1048576) {
            mstrbuf.append(df.format(((double) filesize) / 1024.0d));
            mstrbuf.append(" ");
            mstrbuf.append("KB");
        } else if (filesize < 1073741824) {
            mstrbuf.append(df.format(((double) filesize) / 1048576.0d));
            mstrbuf.append(" ");
            mstrbuf.append("MB");
        } else {
            mstrbuf.append(df.format(((double) filesize) / 1.073741824E9d));
            mstrbuf.append(" ");
            mstrbuf.append("GB");
        }
        return mstrbuf.toString();
    }

    public static String[] getBoardDisplayData(long filesize) {
        DecimalFormat df = new DecimalFormat("#.0");
        DecimalFormatSymbols localDecimalFormatSymbols = df.getDecimalFormatSymbols();
        localDecimalFormatSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(localDecimalFormatSymbols);
        StringBuilder mstrbuf = new StringBuilder();
        String[] result = new String[2];
        if (filesize < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            result[0] = String.valueOf(filesize);
            result[1] = "B";
        } else if (filesize < 1048576) {
            result[0] = String.valueOf(df.format(((double) filesize) / 1024.0d));
            result[1] = "KB";
        } else if (filesize < 1073741824) {
            mstrbuf.append(df.format(((double) filesize) / 1048576.0d));
            result[0] = String.valueOf(df.format(((double) filesize) / 1048576.0d));
            result[1] = "MB";
        } else {
            result[0] = String.valueOf(df.format(((double) filesize) / 1.073741824E9d));
            result[1] = "GB";
        }
        return result;
    }

    public static String getReadableIntegerSize(long filesize) {
        DecimalFormat df = new DecimalFormat("#.0");
        StringBuilder mstrbuf = new StringBuilder();
        DecimalFormatSymbols localDecimalFormatSymbols = df.getDecimalFormatSymbols();
        localDecimalFormatSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(localDecimalFormatSymbols);
        if (filesize < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            mstrbuf.append(filesize);
            mstrbuf.append("B");
        } else if (filesize < 1048576) {
            mstrbuf.append(df.format(((double) filesize) / 1024.0d));
            mstrbuf.append("K");
        } else if (filesize < 1073741824) {
            mstrbuf.append(df.format(((double) filesize) / 1048576.0d));
            mstrbuf.append("M");
        } else {
            mstrbuf.append(df.format(((double) filesize) / 1.073741824E9d));
            mstrbuf.append("G");
        }
        return mstrbuf.toString();
    }

    public static String getReaderSizeBaseG(long filesize) {
        DecimalFormat df;
        long base;
        String unit;
        StringBuilder mstrbuf = new StringBuilder();
        if (filesize >= 1073741824) {
            df = new DecimalFormat("#");
            base = 1073741824;
            unit = "G";
        } else if (filesize >= 104857600 && filesize < 1073741824) {
            df = new DecimalFormat("0.0");
            base = 1073741824;
            unit = "G";
        } else if (filesize >= 104857600 || filesize < 1048576) {
            df = new DecimalFormat("0.0");
            base = 1048576;
            unit = "M";
        } else {
            df = new DecimalFormat("#");
            base = 1048576;
            unit = "M";
        }
        DecimalFormatSymbols localDecimalFormatSymbols = df.getDecimalFormatSymbols();
        localDecimalFormatSymbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(localDecimalFormatSymbols);
        mstrbuf.append(df.format(((double) filesize) / ((double) base)));
        mstrbuf.append(unit);
        return mstrbuf.toString();
    }

    public static String getReadAboutSize(long filesize) {
        DecimalFormat df = new DecimalFormat("#");
        StringBuilder mstrbuf = new StringBuilder();
        if (filesize < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            mstrbuf.append(filesize);
            mstrbuf.append("B");
        } else if (filesize < 1048576) {
            mstrbuf.append(df.format(filesize / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));
            mstrbuf.append("KB");
        } else if (filesize < 1073741824) {
            mstrbuf.append(df.format(filesize / 1048576));
            mstrbuf.append("MB");
        } else {
            mstrbuf.append(df.format(filesize / 1073741824));
            mstrbuf.append("GB");
        }
        return mstrbuf.toString();
    }

    public static String percent(long p1, long p2) {
        double p3 = ((double) p1) / ((double) p2);
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(0);
        return nf.format(p3);
    }

    public HashMap<String, Boolean> getSdcardMountStateMap() {
        return sdcardMountStateMap;
    }

    public static ArrayList<String> getDeviceSdPaths(StorageManager storageManager) {
        ArrayList<String> mounedList = new ArrayList(4);
        try {
            if (VERSION.SDK_INT > 14) {
                sdcardMountStateMap = new HashMap(4);
                String[] volumelist = (String[]) StorageManager.class.getMethod("getVolumePaths", new Class[0]).invoke(storageManager, new Object[0]);
                if (volumelist == null) {
                    return mounedList;
                }
                for (String path : volumelist) {
                    Method volumeMethod = StorageManager.class.getMethod("getVolumeState", new Class[]{String.class});
                    if ("mounted".equals((String) volumeMethod.invoke(storageManager, new Object[]{path}))) {
                        if (!mounedList.contains(path)) {
                            mounedList.add(path);
                        }
                        sdcardMountStateMap.put(path, Boolean.valueOf(true));
                    }
                }
                return mounedList;
            }
            ArrayList<String> sdMoutsList = new ArrayList();
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/mounts"), 8192);
            do {
                String eachLine = localBufferedReader.readLine();
                if (eachLine.contains("uid=1000") && ((eachLine.contains("gid=1015") || eachLine.contains("gid=1023")) && !eachLine.contains("asec"))) {
                    String[] spliteLineInfo = eachLine.split("\\s+");
                    if (!(sdMoutsList == null || sdMoutsList.contains(spliteLineInfo[0]))) {
                        sdMoutsList.add(spliteLineInfo[0]);
                        mounedList.add(spliteLineInfo[1]);
                    }
                }
            } while (localBufferedReader.read() != -1);
            return mounedList;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e2) {
            e2.printStackTrace();
            return null;
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
            return null;
        } catch (NoSuchMethodException e4) {
            e4.printStackTrace();
            return null;
        } catch (FileNotFoundException e5) {
            return mounedList;
        } catch (IOException e6) {
            return mounedList;
        }
    }

    public static SdMountsInfo getDeviceTotalStatsInfo(Context context) {
        ArrayList<SdMountsInfo> list = getDeviceMountInfoList((StorageManager) context.getSystemService(Context.STORAGE_SERVICE));
        SdMountsInfo info = new SdMountsInfo();
        if (list == null || list.size() <= 0) {
            long[] rom = getRomMemory();
            if (rom != null && rom.length > 1) {
                info.totalSize += rom[0];
                info.availSize += rom[1];
            }
        } else {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                SdMountsInfo sdMount = (SdMountsInfo) it.next();
                info.availSize += sdMount.availSize;
                info.totalSize += sdMount.totalSize;
            }
        }
        return info;
    }

    public static ArrayList<SdMountsInfo> getDeviceMountInfoList(StorageManager storageManager) {
        ArrayList<SdMountsInfo> arrayList = new ArrayList(4);
        ArrayList<String> sdInfoList = getDeviceSdPaths(storageManager);
        if (sdInfoList != null) {
            String separator;
            String firPath;
            String secPath;
            long[] firstSdMemory;
            long[] secondSdMemory;
            switch (sdInfoList.size()) {
                case 0:
                    break;
                case 1:
                    SdMountsInfo mountInfo = new SdMountsInfo();
                    String path = (String) sdInfoList.get(0);
                    if (sdcardMountStateMap != null) {
                        Boolean bool = (Boolean) sdcardMountStateMap.get(path);
                        if (bool == null || !bool.booleanValue()) {
                            mountInfo.isExternalSd = true;
                        } else {
                            isSupportDoubleSd = true;
                        }
                    } else if (Environment.isExternalStorageRemovable()) {
                        mountInfo.isExternalSd = true;
                    } else {
                        isSupportDoubleSd = true;
                    }
                    long[] sdMemory = getSdMemory(path);
                    mountInfo.totalSize = sdMemory[0];
                    mountInfo.availSize = sdMemory[1];
                    mountInfo.sdPath = path;
                    long[] romMemory = getRomMemory();
                    if (romMemory != null && romMemory[0] == sdMemory[0] && romMemory[1] == sdMemory[1]) {
                        isSupportDoubleSd = false;
                    }
                    arrayList.add(mountInfo);
                    break;
                case 2:
                    isSupportDoubleSd = true;
                    String externPath = Environment.getExternalStorageDirectory().toString();
                    boolean externStorage = Environment.isExternalStorageRemovable();
                    SdMountsInfo firstMountInfo = new SdMountsInfo();
                    SdMountsInfo secondMountInfo = new SdMountsInfo();
                    String firstPath = (String) sdInfoList.get(0);
                    String secondPath = (String) sdInfoList.get(1);
                    separator = File.separator;
                    if (!(firstPath == null || secondPath == null)) {
                        firPath = firstPath + separator;
                        secPath = secondPath + separator;
                        if (firPath.contains(secPath)) {
                            firstMountInfo.isExternalSd = true;
                            secondMountInfo.isExternalSd = false;
                        } else if (secPath.contains(firPath)) {
                            firstMountInfo.isExternalSd = false;
                            secondMountInfo.isExternalSd = true;
                            if ("K-Touch W806+".equals(Build.MODEL)) {
                                firstMountInfo.isExternalSd = true;
                                secondMountInfo.isExternalSd = false;
                            }
                        } else if (externStorage) {
                            if (firstPath != null && firstPath.equals(externPath)) {
                                firstMountInfo.isExternalSd = true;
                                secondMountInfo.isExternalSd = false;
                            }
                            if (secondPath != null && secondPath.equals(externPath)) {
                                firstMountInfo.isExternalSd = false;
                                secondMountInfo.isExternalSd = true;
                            }
                        } else {
                            if (firstPath != null && firstPath.equals(externPath)) {
                                firstMountInfo.isExternalSd = false;
                                secondMountInfo.isExternalSd = true;
                            }
                            if (secondPath != null && secondPath.equals(externPath)) {
                                secondMountInfo.isExternalSd = true;
                                secondMountInfo.isExternalSd = false;
                            }
                        }
                        firstSdMemory = getSdMemory(firstPath);
                        firstMountInfo.totalSize = firstSdMemory[0];
                        firstMountInfo.availSize = firstSdMemory[1];
                        firstMountInfo.sdPath = firstPath;
                        arrayList.add(firstMountInfo);
                        secondSdMemory = getSdMemory(secondPath);
                        secondMountInfo.totalSize = secondSdMemory[0];
                        secondMountInfo.availSize = secondSdMemory[1];
                        secondMountInfo.sdPath = secondPath;
                        arrayList.add(secondMountInfo);
                        break;
                    }
                default:
                    ArrayList<String> arrayList2 = new ArrayList(4);
                    String externPathExt;
                    Iterator it;
                    String realSdPath;
                    long[] firstMemoryEx;
                    if (sdcardMountStateMap == null) {
                        String exterSdEx = null;
                        externPathExt = Environment.getExternalStorageDirectory().toString();
                        it = arrayList2.iterator();
                        while (it.hasNext()) {
                            realSdPath = (String) it.next();
                            if (realSdPath.equals(externPathExt)) {
                                exterSdEx = realSdPath;
                            }
                        }
                        if (exterSdEx != null) {
                            SdMountsInfo mountInfoExt = new SdMountsInfo();
                            firstMemoryEx = getSdMemory(exterSdEx);
                            mountInfoExt.totalSize = firstMemoryEx[0];
                            mountInfoExt.availSize = firstMemoryEx[1];
                            mountInfoExt.sdPath = exterSdEx;
                            arrayList.add(mountInfoExt);
                            break;
                        }
                    }
                    it = sdInfoList.iterator();
                    while (it.hasNext()) {
                        String sdPath = (String) it.next();
                        if (sdcardMountStateMap.containsKey(sdPath)) {
                            arrayList2.add(sdPath);
                        }
                    }
                    switch (arrayList2.size()) {
                        case 0:
                            break;
                        case 1:
                            SdMountsInfo firstMountInfoExt = new SdMountsInfo();
                            if (Environment.isExternalStorageRemovable()) {
                                isSupportDoubleSd = true;
                            } else {
                                firstMountInfoExt.isExternalSd = true;
                            }
                            String firstSdPathExt = (String) arrayList2.get(0);
                            long[] firstMemoryExt = getSdMemory(firstSdPathExt);
                            firstMountInfoExt.totalSize = firstMemoryExt[0];
                            firstMountInfoExt.availSize = firstMemoryExt[1];
                            firstMountInfoExt.sdPath = firstSdPathExt;
                            arrayList.add(firstMountInfoExt);
                            break;
                        default:
                            SdMountsInfo fMountInfoExt = new SdMountsInfo();
                            SdMountsInfo sMountInfoExt = new SdMountsInfo();
                            externPathExt = Environment.getExternalStorageDirectory().toString();
                            String extPath = null;
                            String internPath = null;
                            it = arrayList2.iterator();
                            while (it.hasNext()) {
                                realSdPath = (String) it.next();
                                if (realSdPath.equals(externPathExt)) {
                                    extPath = realSdPath;
                                } else {
                                    internPath = realSdPath;
                                }
                            }
                            if (extPath != null || internPath == null) {
                                if (extPath == null || internPath != null) {
                                    if (!(extPath == null || internPath == null)) {
                                        String externPathEx = Environment.getExternalStorageDirectory().toString();
                                        boolean externStorageEx = Environment.isExternalStorageRemovable();
                                        separator = File.separator;
                                        if (!(internPath == null || extPath == null)) {
                                            firPath = internPath + separator;
                                            secPath = extPath + separator;
                                            if (firPath.contains(secPath)) {
                                                fMountInfoExt.isExternalSd = true;
                                                sMountInfoExt.isExternalSd = false;
                                            } else if (secPath.contains(firPath)) {
                                                fMountInfoExt.isExternalSd = false;
                                                sMountInfoExt.isExternalSd = true;
                                                if ("K-Touch W806+".equals(Build.MODEL)) {
                                                    fMountInfoExt.isExternalSd = true;
                                                    sMountInfoExt.isExternalSd = false;
                                                }
                                            } else if (externStorageEx) {
                                                if (internPath != null && internPath.equals(externPathEx)) {
                                                    fMountInfoExt.isExternalSd = true;
                                                    sMountInfoExt.isExternalSd = false;
                                                }
                                                if (extPath != null && extPath.equals(externPathEx)) {
                                                    fMountInfoExt.isExternalSd = false;
                                                    sMountInfoExt.isExternalSd = true;
                                                }
                                            } else {
                                                if (internPath != null && internPath.equals(externPathEx)) {
                                                    fMountInfoExt.isExternalSd = false;
                                                    sMountInfoExt.isExternalSd = true;
                                                }
                                                if (extPath != null && extPath.equals(externPathEx)) {
                                                    fMountInfoExt.isExternalSd = true;
                                                    sMountInfoExt.isExternalSd = false;
                                                }
                                            }
                                            firstSdMemory = getSdMemory(internPath);
                                            fMountInfoExt.totalSize = firstSdMemory[0];
                                            fMountInfoExt.availSize = firstSdMemory[1];
                                            fMountInfoExt.sdPath = internPath;
                                            arrayList.add(fMountInfoExt);
                                            secondSdMemory = getSdMemory(extPath);
                                            sMountInfoExt.totalSize = secondSdMemory[0];
                                            sMountInfoExt.availSize = secondSdMemory[1];
                                            sMountInfoExt.sdPath = extPath;
                                            arrayList.add(sMountInfoExt);
                                            break;
                                        }
                                    }
                                }
                                firstMemoryEx = getSdMemory(extPath);
                                fMountInfoExt.totalSize = firstMemoryEx[0];
                                fMountInfoExt.availSize = firstMemoryEx[1];
                                fMountInfoExt.sdPath = extPath;
                                arrayList.add(fMountInfoExt);
                                break;
                            }
                            if (Environment.isExternalStorageRemovable()) {
                                isSupportDoubleSd = true;
                            } else {
                                fMountInfoExt.isExternalSd = true;
                            }
                            firstMemoryEx = getSdMemory(internPath);
                            fMountInfoExt.totalSize = firstMemoryEx[0];
                            fMountInfoExt.availSize = firstMemoryEx[1];
                            fMountInfoExt.sdPath = internPath;
                            arrayList.add(fMountInfoExt);
                            break;
                    }
                    break;
            }
        }
        return arrayList;
    }

    public static long[] getSdMemory(String path) {
        long[] sdInfos = new long[]{0, 0};
        try {
            StatFs sf = new StatFs(new File(path).getPath());
            long bSize = (long) sf.getBlockSize();
            long availBlocks = (long) sf.getAvailableBlocks();
            sdInfos[0] = bSize * ((long) sf.getBlockCount());
            sdInfos[1] = bSize * availBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sdInfos;
    }

    public static long[] getRomMemory() {
        long[] romInfo = new long[]{0, 0};
        try {
            StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
            long blockSize = (long) stat.getBlockSize();
            long availableBlocks = (long) stat.getAvailableBlocks();
            romInfo[0] = ((long) stat.getBlockCount()) * blockSize;
            romInfo[1] = availableBlocks * blockSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return romInfo;
    }

    public static int px2dip(Context context, float pxValue) {
        return (int) ((pxValue / context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        return (int) ((dipValue * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        return (int) ((spValue * context.getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int getStatusBarHeight(Context ctx) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            statusBarHeight = ctx.getResources().getDimensionPixelSize(Integer.parseInt(clazz.getField("status_bar_height").get(clazz.newInstance()).toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    public static Animation getRunningImageAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 359.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(600);
        rotateAnimation.setRepeatCount(-1);
        return rotateAnimation;
    }

    public static Animation getSmallLoadingImageAnimation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0.0f, 359.0f, 1, 0.5f, 1, 0.5f);
        rotateAnimation.setFillEnabled(true);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(1500);
        rotateAnimation.setRepeatCount(-1);
        return rotateAnimation;
    }

    public static String formatDateToM(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd kk:mm", dateTaken).toString();
    }

    public static String timeFormatToDay(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd", dateTaken).toString();
    }

    public static String getScreenResolution(Context context) {
        return getScreenHeight(context) + "x" + getScreenWidth(context);
    }

    public static String getDeviceResolution(Context context) {
        return "ScreenSolution :  " + getScreenResolution(context) + ": scale = " + context.getResources().getDisplayMetrics().density;
    }

    public static long getSdAvailSpace() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    public static boolean belowHoneycomb() {
        return VERSION.SDK_INT < 11;
    }

    public static void clipSector(Canvas canvas, float center_X, float center_Y, float r, float startAngle, float sweepAngle) {
        Path path = new Path();
        path.moveTo(center_X, center_Y);
        path.lineTo((float) (((double) center_X) + (((double) r) * Math.cos((((double) startAngle) * 3.141592653589793d) / 180.0d))), (float) (((double) center_Y) + (((double) r) * Math.sin((((double) startAngle) * 3.141592653589793d) / 180.0d))));
        path.lineTo((float) (((double) center_X) + (((double) r) * Math.cos((((double) sweepAngle) * 3.141592653589793d) / 180.0d))), (float) (((double) center_Y) + (((double) r) * Math.sin((((double) sweepAngle) * 3.141592653589793d) / 180.0d))));
        path.close();
        path.addArc(new RectF(center_X - r, center_Y - r, center_X + r, center_Y + r), startAngle, sweepAngle - startAngle);
        canvas.clipPath(path);
    }

    public static boolean isRunOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static void checkRunOnUIThread() {
        if (!isRunOnMainThread()) {
            throw new RuntimeException(" The method must run in UI Thread");
        }
    }

    public static void checkRunOnNotUIThread() {
        if (isRunOnMainThread()) {
            throw new RuntimeException("The method  must run in Non UI Thread");
        }
    }
}
