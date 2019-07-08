package com.github.axet.androidlibrary.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.axet.androidlibrary.widgets.OptimizationPreferenceCompat;

// Several services types available:
//
// 1) Persistent Service + Persistent Icon (Torrent Client)
//    - Battery Optimization settings
//    - No Persistent Icon option (override PersistentService.updateIcon() to keep intent != null)
// 2) If Enabled Service + Periodic events (Hourly Reminder / Volume Warning)
//    - Battery Optimization settings
//    - Persistent Icon option (PersistentService.isPersistent mandatory call)
// 3) If Enabled Service (Call Recorder / Media Merger)
//    - Battery Optimization settings
//    - Persistent Icon option (OptimizationPreferenceCompat.setIcon() mandatory call)
// 4) Long Operation Service (Audio Recorder)
//    - No Battery Optimization settings
//    - No Persistent Icon option (override PersistentService.updateIcon() to keep intent != null, ServiceReceiver.isOptimization() {return true})
// 5) Long Operation no kill check (Hourly Reminder FireAlarmService)
//    - No Battery Optimization settings
//    - No Persistent Icon option (override PersistentService.updateIcon() to keep intent != null, override onCreateOptimization() {})
public class PersistentService extends Service {
    public static final String TAG = PersistentService.class.getSimpleName();

    protected OptimizationPreferenceCompat.ServiceReceiver optimization;

    public static void start(Context context, Intent intent) {
        OptimizationPreferenceCompat.startService(context, intent);
    }

    public static void stop(Context context, Intent intent) {
        context.stopService(intent);
    }

    public static boolean isPersistent(Context context, boolean b, String key) {
        OptimizationPreferenceCompat.State state = OptimizationPreferenceCompat.getState(context, key);
        return (Build.VERSION.SDK_INT < 26 && b) || state.icon;
    }

    public static boolean startIfPersistent(Context context, boolean b, Intent intent, String key) { // if service is optional keep running service for <API26
        if (isPersistent(context, b, key)) {
            start(context, intent);
            return true;
        } else {
            stop(context, intent);
            return false;
        }
    }

    public PersistentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        onCreateOptimization();
    }

    public void onCreateOptimization() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        if (optimization != null) {
            optimization.close();
            optimization = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (optimization.onStartCommand(intent, flags, startId)) {
            Log.d(TAG, "onStartCommand restart"); // crash fail
            onRestartCommand();
        }
        if (intent != null) {
            String action = intent.getAction();
            Log.d(TAG, "onStartCommand " + action);
            onStartCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void onRestartCommand() {
    }

    public void onStartCommand(Intent intent) {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        optimization.onTaskRemoved(rootIntent);
    }
}
