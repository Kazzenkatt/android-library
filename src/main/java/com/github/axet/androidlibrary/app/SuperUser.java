package com.github.axet.androidlibrary.app;

import android.content.ComponentName;
import android.content.Intent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class SuperUser {
    public static String TAG = SuperUser.class.getSimpleName();

    public static final String BIN_SU = "/system/xbin/su";
    public static final String BIN_TRUE = "/usr/bin/true";
    public static final String BIN_REBOOT = "/system/bin/reboot";

    public static final String KILL = " || kill -9 $$"; // some su does not return error codes in scripts, kill it

    public static int su(String cmd) {
        try {
            Process su = Runtime.getRuntime().exec(BIN_SU);
            DataOutputStream os = new DataOutputStream(su.getOutputStream());
            os.writeBytes(cmd + KILL + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            su.waitFor();
            return su.exitValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return -1;
    }

    public static void reboot() {
        su(BIN_REBOOT);
    }

    public static boolean isRooted() {
        File f = new File(BIN_SU);
        return f.exists();
    }

    public static boolean rootTest() {
        try {
            su(BIN_TRUE);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public static void startService(Intent intent) {
        startService(intent.getComponent());
    }

    public static void startService(ComponentName name) {
        su("am startservice -n " + name.flattenToShortString());
    }

    public static void stopService(Intent intent) {
        stopService(intent.getComponent());
    }

    public static void stopService(ComponentName name) {
        su("am stopservice -n " + name.flattenToShortString());
    }

    public static boolean isReboot() {
        File f2 = new File(BIN_REBOOT);
        return isRooted() && f2.exists();
    }

    public static String escapePath(String p) {
        p = p.replaceAll(" ", "\\ "); // ' ' -> '\'
        p = p.replaceAll("\"", "\\\""); // '"' -> '\"'
        return p;
    }

    public static boolean touch(File f) {
        String p = f.getAbsolutePath();
        return su("touch -a " + escapePath(p)) == 0;
    }

    public static boolean mkdirs(File f) {
        String p = f.getAbsolutePath();
        return su("mkdir -p " + escapePath(p)) == 0;
    }

    public static boolean delete(File f) {
        String p = f.getAbsolutePath();
        return su("rm -rf " + escapePath(p)) == 0;
    }

    public static boolean mv(File f, File to) {
        String p1 = escapePath(f.getAbsolutePath());
        String pp = p1 + " " + escapePath(to.getAbsolutePath());
        String mv = "mv " + pp;
        String cp = "cp " + pp + " && rm " + p1;
        return su(mv + " || " + cp) == 0;
    }
}
