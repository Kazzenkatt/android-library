package com.github.axet.androidlibrary.widgets;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Add users permission to app manifest:
 * <p>
 * &lt;uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" /&gt;
 */
public class OptimizationPreferenceCompat extends SwitchPreferenceCompat {
    // http://stackoverflow.com/questions/31638986/protected-apps-setting-on-huawei-phones-and-how-to-handle-it/35220476
    Intent huawei = new Intent();
    // http://stackoverflow.com/questions/37205106/how-do-i-avoid-that-my-app-enters-optimization-on-samsung-devices
    // http://stackoverflow.com/questions/34074955/android-exact-alarm-is-always-3-minutes-off/34085645#34085645
    Intent samsung = new Intent();

    @TargetApi(21)
    public OptimizationPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        create();
    }

    @TargetApi(21)
    public OptimizationPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        create();
    }

    public OptimizationPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        create();
    }

    public OptimizationPreferenceCompat(Context context) {
        super(context);
        create();
    }

    void create() {
        huawei.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
        samsung.setClassName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity");
        onResume();
    }

    String getUserSerial() {
        Object userManager = getContext().getSystemService(Context.USER_SERVICE);
        if (null == userManager)
            return "";
        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

    void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    boolean isHuawei() {
        return isCallable(huawei);
    }

    AlertDialog.Builder huaweiWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Huawei Settings");
        builder.setMessage("You have to change the power plan to “normal” under settings → power saving to let application be exact on time.");
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder;
    }

    boolean isSamsung() {
        return isCallable(samsung);
    }

    AlertDialog.Builder samsungWarninig() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Samsung Settings");
        builder.setMessage("Consider disabling Samsung SmartManager to keep application running.");
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        return builder;
    }

    public void onResume() {
        final PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        final String n = getContext().getPackageName();
        if (Build.VERSION.SDK_INT < 23) {
            if (isHuawei()) {
                setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean b = (boolean) newValue;
                        if (b) {
                            AlertDialog.Builder builder = huaweiWarning();
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    huaweiProtectedApps();
                                }
                            });
                            builder.show();
                        }
                        return false;
                    }
                });
            } else if (isSamsung()) {
                setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean b = (boolean) newValue;
                        if (b) {
                            AlertDialog.Builder builder = samsungWarninig();
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getContext().startActivity(samsung);
                                }
                            });
                            builder.show();
                        }
                        return false;
                    }
                });
            } else {
                setVisible(false);
            }
        } else {
            setChecked(pm.isIgnoringBatteryOptimizations(n));
            setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                @TargetApi(23)
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (isHuawei()) {
                        AlertDialog.Builder builder = huaweiWarning();
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        });
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showOptimization();
                            }
                        });
                        builder.show();
                    } else if (isSamsung()) {
                        AlertDialog.Builder builder = samsungWarninig();
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showOptimization();
                            }
                        });
                        builder.show();
                    } else {
                        showOptimization();
                    }
                    return false;
                }
            });
        }
    }

    @TargetApi(23)
    void showOptimization() {
        final PowerManager pm = (PowerManager) getContext().getSystemService(Context.POWER_SERVICE);
        final String n = getContext().getPackageName();
        if (pm.isIgnoringBatteryOptimizations(n)) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            getContext().startActivity(intent);
        } else {
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + n));
            getContext().startActivity(intent);
        }
    }

}
