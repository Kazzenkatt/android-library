package com.github.axet.androidlibrary.widgets;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.RemoteViews;

import com.github.axet.androidlibrary.R;

// Check android notification_template_base.xml for constants
public class RemoteNotificationCompat extends NotificationCompat {

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    public static class Builder extends NotificationCompat.Builder {
        public NotificationChannelCompat channel;
        public RemoteViews view;
        public ContextThemeWrapper theme;

        public Builder(Context context, int layoutId) {
            super(context);
            create(layoutId);
        }

        public void create(int layoutId) {
            view = new RemoteViews(mContext.getPackageName(), layoutId);
            if (Build.VERSION.SDK_INT >= 21)
                setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            setContent(view);
        }

        public Builder setChannel(NotificationChannelCompat channel) {
            this.channel = channel;
            channel.apply(this);
            return this;
        }

        @SuppressLint("RestrictedApi")
        public Builder setTheme(int id) {
            theme = new ContextThemeWrapper(mContext, id);
            RemoteViewsCompat.applyTheme(theme, view);
            return this;
        }

        public Builder setMainIntent(PendingIntent main) {
            view.setOnClickPendingIntent(R.id.status_bar_latest_event_content, main);
            if (Build.VERSION.SDK_INT < 11)
                setContentIntent(main);
            return this;
        }

        public Builder setTitle(String title) {
            return setTitle(title, null);
        }

        public Builder setTitle(String title, String ticker) {
            view.setTextViewText(R.id.title, title);
            setContentTitle(title);
            setTicker(ticker); // few secs short tooltip
            return this;
        }

        public Builder setText(String text) {
            view.setTextViewText(R.id.text, text);
            setContentText(text);
            return this;
        }

        public Builder setWhen(Notification n) {
            setWhen(n == null ? System.currentTimeMillis() : n.when);
            return this;
        }

        public Builder setImageViewTint(int id, int attr) { // android:tint="?attr/..." crashing <API21
            RemoteViewsCompat.setImageViewTint(view, id, ThemeUtils.getThemeColor(theme, attr));
            return this;
        }

        public Builder setIcon(int id) {
            view.setImageViewResource(R.id.icon, id);
            return this;
        }

        public Builder setViewVisibility(int id, int v) {
            view.setViewVisibility(id, v);
            return this;
        }

        public Builder setImageViewResource(int id, int res) {
            view.setImageViewResource(id, res);
            return this;
        }

        public Builder setOnClickPendingIntent(int id, PendingIntent pe) {
            view.setOnClickPendingIntent(id, pe);
            return this;
        }

        @Override
        public Notification build() {
            Notification n = super.build();
            NotificationChannelCompat.setChannelId(n, channel.channelId); // builder recreate Notification object by prorerty
            return n;
        }
    }

    public static class Compact extends Builder {
        public RemoteViews big;

        public Compact(Context context) {
            super(context, R.layout.remoteview_compact);
            view.setTextViewText(R.id.app_name_text, getApplicationName(context));
        }

        public Compact(Context context, int layoutId) {
            super(context, Build.VERSION.SDK_INT >= 26 ? R.layout.remoteview_compact : layoutId);
            view.setTextViewText(R.id.app_name_text, getApplicationName(context));
            if (Build.VERSION.SDK_INT >= 26) {
                big = new RemoteViews(mContext.getPackageName(), layoutId);
                setCustomBigContentView(big);
            }
        }

        @Override
        public Builder setTitle(String title, String ticker) {
            if (big != null)
                big.setTextViewText(R.id.title, title);
            return super.setTitle(title, ticker);
        }

        @Override
        public Builder setText(String text) {
            view.setViewVisibility(R.id.header_text_divider, View.VISIBLE);
            view.setTextViewText(R.id.header_text, text);
            view.setViewVisibility(R.id.header_text, View.VISIBLE);
            if (big != null)
                big.setTextViewText(R.id.text, text);
            return super.setText(text);
        }

        @Override
        public Builder setTheme(int id) {
            super.setTheme(id);
            if (big != null)
                RemoteViewsCompat.applyTheme(theme, big);
            return this;
        }

        @Override
        public Builder setImageViewTint(int id, int attr) {
            if (big != null)
                RemoteViewsCompat.setImageViewTint(big, id, ThemeUtils.getThemeColor(theme, attr));
            return super.setImageViewTint(id, attr);
        }

        @Override
        public Builder setMainIntent(PendingIntent main) {
            if (big != null)
                big.setOnClickPendingIntent(R.id.status_bar_latest_event_content, main);
            return super.setMainIntent(main);
        }

        @Override
        public Builder setIcon(int id) {
            if (big != null)
                big.setImageViewResource(R.id.icon, id);
            return super.setIcon(id);
        }

        public Builder setViewVisibility(int id, int v) {
            if (big != null)
                big.setViewVisibility(id, v);
            return super.setViewVisibility(id, v);
        }

        public Builder setImageViewResource(int id, int res) {
            if (big != null)
                big.setImageViewResource(id, res);
            return super.setImageViewResource(id, res);
        }

        public Builder setOnClickPendingIntent(int id, PendingIntent pe) {
            if (big != null)
                big.setOnClickPendingIntent(id, pe);
            return super.setOnClickPendingIntent(id, pe);
        }

        @Override
        public NotificationCompat.Builder setSmallIcon(int icon) {
            view.setImageViewResource(R.id.icon, icon);
            setImageViewTint(R.id.icon_circle, R.attr.colorButtonNormal);
            return super.setSmallIcon(icon);
        }
    }
}
