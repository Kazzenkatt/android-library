package com.github.axet.androidlibrary.widgets;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;

public abstract class UriImagesRecyclerAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    public static final String TAG = UriImagesRecyclerAdapter.class.getSimpleName();

    UriImagesAdapter adapter = new UriImagesAdapter() {
        @Override
        public void downloadTaskUpdate(DownloadImageTask task, Object item, Object view) {
            UriImagesRecyclerAdapter.this.downloadTaskUpdate(task, item, view);
        }
    };

    public void clearTasks() {
        adapter.clearTasks();
    }

    public Bitmap downloadImageTask(UriImagesAdapter.DownloadImageTask task) {
        return adapter.downloadImageTask(task);
    }

    public void downloadTaskDone(UriImagesAdapter.DownloadImageTask task) {
        adapter.downloadTaskDone(task);
    }

    public void downloadTaskUpdate(UriImagesAdapter.DownloadImageTask task, Object i, Object o) {
        adapter.downloadTaskUpdate(task, i, o);
    }

}
