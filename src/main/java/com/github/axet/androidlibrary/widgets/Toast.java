package com.github.axet.androidlibrary.widgets;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class Toast {
    public static final int LENGTH_LONG = android.widget.Toast.LENGTH_LONG;
    public static final int LENGTH_SHORT = android.widget.Toast.LENGTH_SHORT;

    public static final long SHORT_DURATION_TIMEOUT = 5000;
    public static final long LONG_DURATION_TIMEOUT = 1000;

    public int d;
    public android.widget.Toast toast;
    public PopupWindow w;
    public Handler handler = new Handler();
    Runnable hide = new Runnable() {
        @Override
        public void run() {
            cancel();
        }
    };

    public static Toast makeText(Context context, int r, int d) {
        return new Toast(android.widget.Toast.makeText(context, r, d), d);
    }

    public static Toast makeText(Context context, CharSequence t, int d) {
        return new Toast(android.widget.Toast.makeText(context, t, d), d);
    }

    public Toast(android.widget.Toast t, int d) {
        toast = t;
        this.d = d;
    }

    public Toast center() {
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null)
            v.setGravity(Gravity.CENTER);
        return this;
    }

    public void cancel() {
        toast.cancel();
        if (w != null) {
            w.dismiss();
            w = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    public void setDuration(int d) {
        toast.setDuration(d);
    }

    public long getDuration() {
        int d = toast.getDuration();
        if (d != 0)
            return d;
        return this.d == LENGTH_SHORT ? SHORT_DURATION_TIMEOUT : LONG_DURATION_TIMEOUT;
    }

    public void show() {
        Context context = toast.getView().getContext();
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            if (w != null)
                w.dismiss();
            View v = toast.getView();
            int ww = context.getResources().getDisplayMetrics().widthPixels;
            int hh = context.getResources().getDisplayMetrics().heightPixels;
            v.measure(View.MeasureSpec.makeMeasureSpec(ww, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(hh, View.MeasureSpec.AT_MOST));
            w = new PopupWindow(v, v.getMeasuredWidth(), v.getMeasuredHeight(), false);
            w.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            w.setContentView(v);
            w.setAnimationStyle(android.R.style.Animation_Toast);
            View p = v;
            if (context instanceof Activity)
                p = ((Activity) context).getWindow().getDecorView();
            w.showAtLocation(p, Gravity.BOTTOM, 0, hh / 6);
            handler.postDelayed(hide, getDuration());
        } else {
            toast.show();
        }
    }

}
