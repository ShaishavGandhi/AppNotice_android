package com.evidon.privacy.appnoticesdk.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.evidon.privacy.appnoticesdk.callbacks.LogoDownload_Callback;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/*
 * To Do: Make code cleaner/modular
 */
public class ImageDownloader {

    private Activity activity;
    private Map<String, SoftReference<Bitmap>> imageCache;
    private LogoDownload_Callback logoDownloadCallback;
    private int position;

    public ImageDownloader(Activity activity, int position, LogoDownload_Callback logoDownloadCallback) {
        imageCache = new HashMap<String, SoftReference<Bitmap>>();
        this.activity = activity;
        this.position = position;
        this.logoDownloadCallback = logoDownloadCallback;
    }

    public void download(String url, ImageView imageView) {
        if (url != null && url.length() > 0 && cancelPotentialDownload(url, imageView)) {
            Bitmap bitmap = null;
            // Caching
            String filename = String.valueOf(url.hashCode());
            File file = new File(imageView.getContext().getCacheDir(), filename);

            // Is bitmap in our memory cache?
            SoftReference<Bitmap> bitmapRef = (SoftReference<Bitmap>) imageCache.get(file.getPath());

            if (bitmapRef == null) {
                bitmap = BitmapFactory.decodeFile(file.getPath());
                bitmapRef = new SoftReference<Bitmap>(bitmap);

                if (bitmap != null) {
                    imageCache.put(file.getPath(), bitmapRef);
                }
            } else {
                bitmap = bitmapRef.get();
            }

            // download it if not in cache
            if (bitmap == null) {
                BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
                DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
                imageView.setImageDrawable(downloadedDrawable);
                task.execute(url);
            } else {
                // set the image
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    // cancel a download (internal only)
    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    // gets an existing download if one exists for the imageview
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    // download asynctask
    public class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;
        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            url = (String) params[0];
            return FileDownloader.getBitmapFromUrl(params[0]);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
                // Change bitmap only if this process is still associated with it
                if (this == bitmapDownloaderTask) {
                    imageView.setImageBitmap(bitmap);

                    // cache the image
                    String filename = String.valueOf(url.hashCode());
                    File f = new File(imageView.getContext().getCacheDir(), filename);
                    SoftReference<Bitmap> bitmapRef = new SoftReference<Bitmap>(bitmap);
                    imageCache.put(f.getPath(), bitmapRef);
                    FileWriter.writeBitmap(bitmap, f);

                    // Let the specified callback know it finished downloading...
                    if (logoDownloadCallback != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                logoDownloadCallback.onDownloaded(position);
                            }
                        });
                    }
                }
            }
        }

    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.WHITE);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

}