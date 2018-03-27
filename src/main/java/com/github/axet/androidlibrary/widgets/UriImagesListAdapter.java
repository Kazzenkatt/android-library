package com.github.axet.androidlibrary.widgets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.github.axet.androidlibrary.R;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class UriImagesListAdapter extends BaseAdapter {
    public static final String TAG = UriImagesListAdapter.class.getSimpleName();

    UriImagesAdapter adapter = new UriImagesAdapter();

    public class UriImagesAdapter extends com.github.axet.androidlibrary.widgets.UriImagesAdapter {
        @Override
        public Bitmap downloadImageTask(DownloadImageTask task) {
            return UriImagesListAdapter.this.downloadImageTask(task);
        }

        @Override
        public void downloadTaskUpdate(DownloadImageTask task, Object item, Object view) {
            UriImagesListAdapter.this.downloadTaskUpdate(task, item, view);
        }

        @Override
        public void downloadTaskDone(DownloadImageTask task) {
            UriImagesListAdapter.this.downloadTaskDone(task);
        }

        public void downloadTaskDoneSuper(DownloadImageTask task) {
            super.downloadTaskDone(task);
        }
    }

    public void clearTasks() {
        adapter.clearTasks();
    }

    public void downloadTaskClean(Object view) {
        adapter.downloadTaskClean(view);
    }

    public void downloadTask(Object item, Object view) {
        adapter.downloadTask(item, view);
    }

    public Bitmap downloadImage(Uri cover) {
        return adapter.downloadImage(cover);
    }

    public Bitmap downloadImageTask(UriImagesAdapter.DownloadImageTask task) {
        return null;
    }

    public void downloadTaskDone(UriImagesAdapter.DownloadImageTask task) {
        adapter.downloadTaskDoneSuper(task);
    }

    public void downloadTaskUpdate(UriImagesAdapter.DownloadImageTask task, Object i, Object o) {
    }

    public void updateView(UriImagesAdapter.DownloadImageTask task, ImageView image, ProgressBar progress) {
        adapter.updateView(task, image, progress);
    }
}
