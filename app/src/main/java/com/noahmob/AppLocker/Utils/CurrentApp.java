package com.noahmob.AppLocker.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CurrentApp {
    private static final int AID_APP = 10000;
    private static final int AID_USER = 100000;

    public static class ForegroundApp {
        public final int pid;
        public final String processName;
        public final int uid;
        public final String uidName;

        private ForegroundApp(String processName, String uidName, int pid, int uid) {
            this.processName = processName;
            this.uidName = uidName;
            this.pid = pid;
            this.uid = uid;
        }

        public String getPackageName() {
            return this.processName.split(":")[0];
        }

        public PackageInfo getPackageInfo(Context context) {
            try {
                return context.getPackageManager().getPackageInfo(getPackageName(), 0);
            } catch (NameNotFoundException e) {
                throw new RuntimeException("WTF", e);
            }
        }
    }

    public static ForegroundApp getForegroundApp() {
        Exception e;
        File[] files = new File("/proc").listFiles();
        int lowestOomScore = Integer.MAX_VALUE;
        int length = files.length;
        int i = 0;
        ForegroundApp foregroundApp = null;
        while (i < length) {
            ForegroundApp foregroundApp2;
            File file = files[i];
            if (file.isDirectory()) {
                try {
                    int pid = Integer.parseInt(file.getName());
                    try {
                        String[] lines = read(String.format("/proc/%d/cgroup", new Object[]{Integer.valueOf(pid)})).split("\n");
                        if (lines.length != 2) {
                            foregroundApp2 = foregroundApp;
                        } else {
                            String cpuSubsystem = lines[0];
                            String cpuaccctSubsystem = lines[1];
                            if (!cpuaccctSubsystem.endsWith(Integer.toString(pid))) {
                                foregroundApp2 = foregroundApp;
                            } else if (cpuSubsystem.endsWith("bg_non_interactive")) {
                                foregroundApp2 = foregroundApp;
                            } else {
                                String cmdline = read(String.format("/proc/%d/cmdline", new Object[]{Integer.valueOf(pid)}));
                                if (cmdline.contains("com.android.systemui") || cmdline.contains("android.process.acore")) {
                                    foregroundApp2 = foregroundApp;
                                } else {
                                    int uid = Integer.parseInt(cpuaccctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
                                    if (uid < 1000 || uid > 1038) {
                                        int appId = uid - 10000;
                                        int userId = 0;
                                        while (appId > AID_USER) {
                                            appId -= AID_USER;
                                            userId++;
                                        }
                                        if (appId < 0) {
                                            foregroundApp2 = foregroundApp;
                                        } else {
                                            String uidName = String.format("u%d_a%d", new Object[]{Integer.valueOf(userId), Integer.valueOf(appId)});
                                            File file2 = new File(String.format("/proc/%d/oom_score_adj", new Object[]{Integer.valueOf(pid)}));
                                            if (!file2.canRead() || Integer.parseInt(read(file2.getAbsolutePath())) == 0) {
                                                int oomscore = Integer.parseInt(read(String.format("/proc/%d/oom_score", new Object[]{Integer.valueOf(pid)})));
                                                if (oomscore < lowestOomScore) {
                                                    lowestOomScore = oomscore;
                                                    foregroundApp2 = new ForegroundApp(cmdline, uidName, pid, uid);
                                                }
                                                foregroundApp2 = foregroundApp;
                                            } else {
                                                foregroundApp2 = foregroundApp;
                                            }
                                        }
                                    } else {
                                        foregroundApp2 = foregroundApp;
                                    }
                                }
                            }
                        }
                    } catch (IOException e2) {
                        e = e2;
                        e.printStackTrace();
                        foregroundApp2 = foregroundApp;
                        i++;
                        foregroundApp = foregroundApp2;
                    } catch (NumberFormatException e3) {
                        e = e3;
                        e.printStackTrace();
                        foregroundApp2 = foregroundApp;
                        i++;
                        foregroundApp = foregroundApp2;
                    }
                } catch (NumberFormatException e4) {
                    foregroundApp2 = foregroundApp;
                }
            } else {
                foregroundApp2 = foregroundApp;
            }
            i++;
            foregroundApp = foregroundApp2;
        }
        return foregroundApp;
    }

    private static String read(String path) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(path));
        output.append(reader.readLine());
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            output.append('\n').append(line);
        }
        reader.close();
        return output.toString();
    }
}
