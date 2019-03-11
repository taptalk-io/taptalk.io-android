package io.taptalk.TapTalk.Helper.DiskLruCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.taptalk.Taptalk.BuildConfig;

public class DiskLruUriCache {

    private DiskLruCache diskCache;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = "DiskLruUriCache";

    public DiskLruUriCache(Context context, String uniqueName, int diskCacheSize) {
        try {
            final File diskCacheDir = getDiskCacheDir(context, uniqueName);
            diskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getDiskCacheDir(Context context, String uniqueName) {

        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Utils.isExternalStorageRemovable() ?
                        Utils.getExternalCacheDir(context).getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    public void put(String key, Uri data) {
        DiskLruCache.Editor editor = null;
        try {
            editor = diskCache.edit(key);
            if (editor == null) {
                return;
            }

            editor.set(0, data.toString());
            diskCache.flush();
            editor.commit();
            Log.e(TAG, "put: " + data.toString());

            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "Uri put on disk cache " + key);
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "ERROR on: Uri put on disk cache " + key);
            }
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public Uri getUri(String key) {
        Uri uri = null;
        try (DiskLruCache.Snapshot snapshot = diskCache.get(key)) {
            if (snapshot == null) {
                return null;
            }
            uri = Uri.parse(snapshot.getString(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", uri == null ? "" : "Uri read from disk " + key);
        }
        return uri;
    }

    public boolean containsKey(String key) {
        boolean contained = false;
        try (DiskLruCache.Snapshot snapshot = diskCache.get(key)) {
            contained = snapshot != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contained;
    }

    public void clearCache() {
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "disk cache CLEARED");
        }
        try {
            diskCache.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeCache() throws IOException {
        diskCache.close();
    }

    public File getCacheFolder() {
        return diskCache.getDirectory();
    }

}