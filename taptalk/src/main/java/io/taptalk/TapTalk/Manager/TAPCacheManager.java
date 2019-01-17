package io.taptalk.TapTalk.Manager;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

import io.taptalk.TapTalk.Helper.DiskLruCache.DiskLruImageCache;
import io.taptalk.Taptalk.R;


public class TAPCacheManager {
    private static final String TAG = TAPCacheManager.class.getSimpleName();
    private static TAPCacheManager instance;
    private Context context;

    //atribut untuk Memory Cache
    private LruCache<String, Bitmap> mMemoryCache;

    //atribut untuk Disk Cache
    private DiskLruImageCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100MB

    private interface AddDiskCacheListener {
        void onDiskCacheNotNull();
    }

    public TAPCacheManager(Context context) {
        this.context = context;
    }

    public static TAPCacheManager getInstance(Context context) {
        return null == instance ? instance = new TAPCacheManager(context) : instance;
    }

    //untuk Memory Cache
    private LruCache<String, Bitmap> getMemoryCache() {
        return null == mMemoryCache ? initMemoryCache() : mMemoryCache;
    }

    private LruCache<String, Bitmap> initMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
        return mMemoryCache;
    }

    public void initAllCache() {
        initMemoryCache();
        initDiskCacheTask(context);
    }

    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (null == getBitmapFromMemCache(key)) {
            getMemoryCache().put(key, bitmap);
        }
    }

    private Bitmap getBitmapFromMemCache(String key) {
        return getMemoryCache().get(key);
    }

    //untuk Disk Cache
    private void initDiskCacheTask(Context context, AddDiskCacheListener listener) {
        new Thread(() -> {
            synchronized (mDiskCacheLock) {
                try {
                    if (null == mDiskLruCache) {
                        mDiskLruCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                                , DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 100);
                        mDiskCacheStarting = false; // Finished initialization
                        mDiskCacheLock.notifyAll(); // Wake any waiting threads
                    }
                    listener.onDiskCacheNotNull();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initDiskCacheTask(Context context) {
        new Thread(() -> {
            synchronized (mDiskCacheLock) {
                try {
                    if (null == mDiskLruCache) {
                        mDiskLruCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                                , DISK_CACHE_SIZE, Bitmap.CompressFormat.WEBP, 100);
                        mDiskCacheStarting = false; // Finished initialization
                        mDiskCacheLock.notifyAll(); // Wake any waiting threads


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //harus background thread
    public void addBitmapToCache(String key, Bitmap bitmap) throws IOException {
        // Add to memory cache as before
        if (getBitmapFromMemCache(key) == null) {
            addBitmapToMemoryCache(key, bitmap);
        }

        new Thread(() -> initDiskCacheTask(context, () -> {
            try {
                addBitmapToDiskCache(key, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        })).start();
    }

    private void addBitmapToDiskCache(String key, Bitmap bitmap) throws IOException {
        // Also add to disk cache
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && mDiskLruCache.getBitmap(key) == null) {
                mDiskLruCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBipmapPerKey(String key) {
        synchronized (mDiskCacheLock) {
            if (null != getMemoryCache().get(key)) {
                Log.e(TAG, "getBipmapPerKey: 1");
                return getMemoryCache().get(key);
            } else if (null != mDiskLruCache && mDiskLruCache.containsKey(key)) {
                Log.e(TAG, "getBipmapPerKey: 2");
                return mDiskLruCache.getBitmap(key);
            } else return null;
        }
    }

    public void getBipmapPerKey(Context context, String key, @Nullable int placeholder, ImageView ivImage, RequestManager glide) {
        new Thread(() -> {
            synchronized (mDiskCacheLock) {
                Bitmap imageBitmap;
                if (null != getMemoryCache().get(key)) {
                    imageBitmap = getMemoryCache().get(key);
                    ((Activity) context).runOnUiThread(() -> glide.load(imageBitmap).transition(DrawableTransitionOptions.withCrossFade(100))
                            .apply(new RequestOptions().placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(ivImage));
                } else if (null != mDiskLruCache && mDiskLruCache.containsKey(key)) {
                    imageBitmap = mDiskLruCache.getBitmap(key);
                    ((Activity) context).runOnUiThread(() -> glide.load(imageBitmap).transition(DrawableTransitionOptions.withCrossFade(100))
                            .apply(new RequestOptions().placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.NONE))
                            .into(ivImage));
                } else {
                    // TODO: 16/01/19 minta ko kepin tggu push dlu yaa :3
                    Log.e(TAG, "setImageMessage2: ");
                }
            }
        }).start();
    }
}
