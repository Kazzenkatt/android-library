package com.github.axet.androidlibrary.widgets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.axet.androidlibrary.services.DeviceAdmin;

import java.util.ArrayList;

public class AdminPreferenceCompat extends SwitchPreferenceCompat {
    public static final String TITLE = "Enable device admin access";
    public static final String ENABLED = "(Device Owner enabled)";

    public static final String ERASE_ALL_DATA = "ERASE_ALL_DATA";
    public static final String LOCK_SCREEN = "LOCK_SCREEN";

    public Activity a;
    public Fragment f;
    public int code;
    public String m;
    public String[] mm; // messages

    @TargetApi(21)
    public AdminPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onResume();
    }

    @TargetApi(21)
    public AdminPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onResume();
    }

    public AdminPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        onResume();
    }

    public AdminPreferenceCompat(Context context) {
        super(context);
        onResume();
    }

    public void setActivity(Activity a, int code) {
        this.a = a;
        this.code = code;
    }

    public void setFragment(Fragment f, int code) {
        this.f = f;
        this.code = code;
    }

    public void setMessages(String m, String[] mm) {
        this.m = m;
        this.mm = mm;
    }

    public void setMessages(Object... oo) {
        int i = 0;
        Object m = oo[i];
        if (m instanceof Integer) {
            this.m = getContext().getString((int) m);
        }
        if (m instanceof String) {
            this.m = (String) m;
        }
        i++;
        ArrayList<String> mm = new ArrayList<>();
        for (; i < oo.length; i += 2) {
            String k = (String) oo[i];
            Object v = oo[i + 1];
            if (v instanceof Integer) {
                mm.add(k);
                mm.add(getContext().getString((int) v));
            }
            if (v instanceof String) {
                mm.add(k);
                mm.add((String) v);
            }
        }
        this.mm = mm.toArray(new String[]{});
    }

    public void onResume() {
        updateAdmin();
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Context context = getContext();
                boolean b = (boolean) o;
                if (b) {
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName c = new ComponentName(context, DeviceAdmin.class);
                    if (!dpm.isAdminActive(c)) {
                        if (Build.VERSION.SDK_INT >= 18) {
                            if (dpm.isDeviceOwnerApp(context.getPackageName())) // already device owner exit
                                return true; // allow change
                        }
                        requestAdmin();
                        return false; // cancel change
                    }
                } else {
                    DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    ComponentName c = new ComponentName(context, DeviceAdmin.class);
                    if (dpm.isAdminActive(c)) {
                        dpm.removeActiveAdmin(c);
                    }
                    if (Build.VERSION.SDK_INT >= 18) {
                        if (dpm.isDeviceOwnerApp(context.getPackageName())) { // device owner can changed
                            DeviceAdmin.removeDeviceOwner(context);
                        }
                    }
                    updateAdminSummary(); // update summary
                }
                return true; // allow change
            }
        });
    }

    public void updateAdminSummary() {
        Context context = getContext();
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        String summary = TITLE;

        if (Build.VERSION.SDK_INT >= 18) {
            if (dpm.isDeviceOwnerApp(context.getPackageName())) { // device owner can't cahnge
                summary += " " + ENABLED;
            }
        }

        setSummary(summary);
    }

    public void updateAdmin() {
        Context context = getContext();

        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName c = new ComponentName(context, DeviceAdmin.class);

        if (isChecked()) {
            boolean b = dpm.isAdminActive(c);
            if (Build.VERSION.SDK_INT >= 24) {
                b |= dpm.isDeviceOwnerApp(context.getPackageName());
            }
            setChecked(b);
        }

        updateAdminSummary();
    }

    public boolean requestAdmin() {
        Context context = getContext();
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final ComponentName c = new ComponentName(context, DeviceAdmin.class);
        if (!dpm.isAdminActive(c)) {
            final Runnable run = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, c);
                    if (a != null)
                        a.startActivityForResult(intent, code);
                    if (f != null)
                        f.startActivityForResult(intent, code);
                }
            };
            if (mm != null) {
                int p5 = ThemeUtils.dp2px(context, 5);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(TITLE);
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.setPadding(ThemeUtils.dp2px(context, 20), p5, p5, p5);
                if (m != null) {
                    TextView message = new TextView(context);
                    TextViewCompat.setTextAppearance(message, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Body1);
                    message.setText(m);
                    ll.addView(message);
                }
                for (int i = 0; i < mm.length; i += 2) {
                    LinearLayout llp = new LinearLayout(context);
                    llp.setOrientation(LinearLayout.VERTICAL);
                    llp.setPadding(ThemeUtils.dp2px(context, 30), p5, p5, p5);
                    TextView title = new TextView(context);
                    TextViewCompat.setTextAppearance(title, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Subhead);
                    title.setText(mm[i]);
                    title.setTypeface(null, Typeface.BOLD);
                    llp.addView(title);
                    TextView message = new TextView(context);
                    TextViewCompat.setTextAppearance(message, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Body1);
                    message.setText(mm[i + 1]);
                    llp.addView(message);
                    ll.addView(llp);
                }
                builder.setView(ll);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        run.run();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                return true;
            } else {
                run.run();
            }
            return true;
        }
        return false;
    }

    public void onActivityResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) // 0 - cancel, -1 - ok
            setChecked(true);
    }
}
