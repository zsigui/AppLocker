package com.noahmob.AppLocker.Utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import eu.chainfire.libsuperuser.Shell.SH;
import java.util.ArrayList;
import java.util.List;

public class ProcessManager {
    private static final String APP_ID_PATTERN;
    private static final String TAG = "ProcessManager";

    public static class Process implements Parcelable {
        public static final Creator<Process> CREATOR = new Creator<Process>() {
            public Process createFromParcel(Parcel source) {
                return new Process(source);
            }

            public Process[] newArray(int size) {
                return new Process[size];
            }
        };
        public final int cpu;
        public final String name;
        public final int niceness;
        public final String pc;
        public final int pid;
        public final String policy;
        public final int ppid;
        public final int priority;
        public final int realTimePriority;
        public final long rss;
        public final int schedulingPolicy;
        public final String state;
        public final long systemTime;
        public final int uid;
        public final String user;
        public final long userTime;
        public final long vsize;
        public final String wchan;

        private Process(String line) throws Exception {
            String[] fields = line.split("\\s+");
            this.user = fields[0];
            this.uid = android.os.Process.getUidForName(this.user);
            this.pid = Integer.parseInt(fields[1]);
            this.ppid = Integer.parseInt(fields[2]);
            this.vsize = (long) (Integer.parseInt(fields[3]) * 1024);
            this.rss = (long) (Integer.parseInt(fields[4]) * 1024);
            this.cpu = Integer.parseInt(fields[5]);
            this.priority = Integer.parseInt(fields[6]);
            this.niceness = Integer.parseInt(fields[7]);
            this.realTimePriority = Integer.parseInt(fields[8]);
            this.schedulingPolicy = Integer.parseInt(fields[9]);
            if (fields.length == 16) {
                this.policy = "";
                this.wchan = fields[10];
                this.pc = fields[11];
                this.state = fields[12];
                this.name = fields[13];
                this.userTime = (long) (Integer.parseInt(fields[14].split(":")[1].replace(",", "")) * 1000);
                this.systemTime = (long) (Integer.parseInt(fields[15].split(":")[1].replace(")", "")) * 1000);
                return;
            }
            this.policy = fields[10];
            this.wchan = fields[11];
            this.pc = fields[12];
            this.state = fields[13];
            this.name = fields[14];
            this.userTime = (long) (Integer.parseInt(fields[15].split(":")[1].replace(",", "")) * 1000);
            this.systemTime = (long) (Integer.parseInt(fields[16].split(":")[1].replace(")", "")) * 1000);
        }

        private Process(Parcel in) {
            this.user = in.readString();
            this.uid = in.readInt();
            this.pid = in.readInt();
            this.ppid = in.readInt();
            this.vsize = in.readLong();
            this.rss = in.readLong();
            this.cpu = in.readInt();
            this.priority = in.readInt();
            this.niceness = in.readInt();
            this.realTimePriority = in.readInt();
            this.schedulingPolicy = in.readInt();
            this.policy = in.readString();
            this.wchan = in.readString();
            this.pc = in.readString();
            this.state = in.readString();
            this.name = in.readString();
            this.userTime = in.readLong();
            this.systemTime = in.readLong();
        }

        public String getPackageName() {
            if (!this.user.matches(ProcessManager.APP_ID_PATTERN)) {
                return null;
            }
            if (this.name.contains(":")) {
                return this.name.split(":")[0];
            }
            return this.name;
        }

        public PackageInfo getPackageInfo(Context context, int flags) throws NameNotFoundException {
            String packageName = getPackageName();
            if (packageName != null) {
                return context.getPackageManager().getPackageInfo(packageName, flags);
            }
            throw new NameNotFoundException(this.name + " is not an application process");
        }

        public ApplicationInfo getApplicationInfo(Context context, int flags) throws NameNotFoundException {
            String packageName = getPackageName();
            if (packageName != null) {
                return context.getPackageManager().getApplicationInfo(packageName, flags);
            }
            throw new NameNotFoundException(this.name + " is not an application process");
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.user);
            dest.writeInt(this.uid);
            dest.writeInt(this.pid);
            dest.writeInt(this.ppid);
            dest.writeLong(this.vsize);
            dest.writeLong(this.rss);
            dest.writeInt(this.cpu);
            dest.writeInt(this.priority);
            dest.writeInt(this.niceness);
            dest.writeInt(this.realTimePriority);
            dest.writeInt(this.schedulingPolicy);
            dest.writeString(this.policy);
            dest.writeString(this.wchan);
            dest.writeString(this.pc);
            dest.writeString(this.state);
            dest.writeString(this.name);
            dest.writeLong(this.userTime);
            dest.writeLong(this.systemTime);
        }
    }

    static {
        if (VERSION.SDK_INT >= 17) {
            APP_ID_PATTERN = "u\\d+_a\\d+";
        } else {
            APP_ID_PATTERN = "app_\\d+";
        }
    }

    public static List<Process> getRunningProcesses() {
        List<Process> processes = new ArrayList();
        for (String line : SH.run("toolbox ps -p -P -x -c")) {
            try {
                processes.add(new Process(line));
            } catch (Exception e) {
                Log.d(TAG, "Failed parsing line " + line);
            }
        }
        return processes;
    }

    public static ArrayList<Process> getRunningApps() {
        ArrayList<Process> processes = new ArrayList();
        List<String> stdout = SH.run("dalvikvm -cp /sdcard/foreground.dex com.jrummyapps.tests.CurrentApp");
        int myPid = android.os.Process.myPid();
        for (String line : stdout) {
            try {
                Process process = new Process(line);
                if (!(!process.user.matches(APP_ID_PATTERN) || process.ppid == myPid || process.name.equals("toolbox"))) {
                    processes.add(process);
                }
            } catch (Exception e) {
                Log.d(TAG, "Failed parsing line " + line);
            }
        }
        return processes;
    }
}
