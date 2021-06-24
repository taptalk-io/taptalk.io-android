package io.taptalk.TapTalk.Helper.DiskLruCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.taptalk.TapTalk.BuildConfig;

public class DiskLruImageCache {

    private DiskLruCache diskCache;
    private Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;
    private int compressQuality = 70;
    private static final int APP_VERSION = 1;
    private static final int VALUE_COUNT = 1;
    private static final String TAG = "DiskLruImageCache";

    public DiskLruImageCache(Context context, String uniqueName, int diskCacheSize,
                             Bitmap.CompressFormat compressFormat, int quality) {
        final File diskCacheDir = getDiskCacheDir(context, uniqueName);
        diskCache = DiskLruCache.open(diskCacheDir, APP_VERSION, VALUE_COUNT, diskCacheSize);
        mCompressFormat = compressFormat;
        compressQuality = quality;
    }

    private boolean writeBitmapToFile(Bitmap bitmap, DiskLruCache.Editor editor) throws IOException {
        if (null == bitmap) {
            return false;
        }
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(0), Utils.IO_BUFFER_SIZE);
            return bitmap.compress(mCompressFormat, compressQuality, out);
        } finally {
            if (out != null) {
                out.close();
            }
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

    public void put(String key, BitmapDrawable data) {
        if (null == key || null == data) {
            return;
        }
        DiskLruCache.Editor editor = null;
        try {
            editor = diskCache.edit(key);
            if (editor == null) {
                return;
            }

            if (writeBitmapToFile(data.getBitmap(), editor)) {
                diskCache.flush();
                editor.commit();
                if (BuildConfig.DEBUG) {
                    Log.d("cache_test_DISK_", "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                if (BuildConfig.DEBUG) {
                    Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
                }
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
            }
            if (editor != null) {
                editor.abort();
            }
        }
    }

    public BitmapDrawable getBitmapDrawable(Context context, String key) {
        if (null == key) {
            return null;
        }
        BitmapDrawable bitmapDrawable = null;
        try (DiskLruCache.Snapshot snapshot = diskCache.get(key)) {
            if (snapshot == null) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn =
                        new BufferedInputStream(in, Utils.IO_BUFFER_SIZE);
                bitmapDrawable = new BitmapDrawable(context.getResources(), BitmapFactory.decodeStream(buffIn));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", bitmapDrawable == null ? "" : "image read from disk " + key);
        }
        return bitmapDrawable;
    }

    public void remove(String key) {
        if (null == key) {
            return;
        }
        diskCache.remove(key);
    }

    public boolean containsKey(String key) {
        if (null == key) {
            return false;
        }
        boolean contained;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskCache.get(key);
            contained = snapshot != null;
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contained;

    }

    public void clearCache() {
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "disk cache CLEARED");
        }
        diskCache.delete();
    }

    public void closeCache() throws IOException {
        diskCache.close();
    }

    public File getCacheFolder() {
        return diskCache.getDirectory();
    }

}