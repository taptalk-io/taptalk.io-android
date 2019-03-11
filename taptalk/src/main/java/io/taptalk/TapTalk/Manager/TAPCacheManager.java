package io.taptalk.TapTalk.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.LruCache;

import io.taptalk.TapTalk.Helper.DiskLruCache.DiskLruImageCache;
import io.taptalk.TapTalk.Helper.DiskLruCache.DiskLruUriCache;
import io.taptalk.Taptalk.R;


public class TAPCacheManager {
    private static final String TAG = TAPCacheManager.class.getSimpleName();
    private static TAPCacheManager instance;
    private Context context;

    //atribut untuk Memory Cache
    private LruCache<String, BitmapDrawable> memoryCache;

    //atribut untuk Disk Cache
    private DiskLruImageCache diskLruImageCache;
    private DiskLruUriCache diskLruUriCache;
    private final Object diskCacheLock = new Object();
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 100; // 100MB
    private static final int DISK_CACHE_SIZE_SMALL = 1024 * 1024 * 10; // 10MB

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
    private LruCache<String, BitmapDrawable> getMemoryCache() {
        return null == memoryCache ? initMemoryCache() : memoryCache;
    }

    private LruCache<String, BitmapDrawable> initMemoryCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount() / 1024;
            }
        };
        return memoryCache;
    }

    public void initAllCache() {
        initMemoryCache();
        initDiskImageCacheTask(context);
        initDiskUriCacheTask(context);
    }

    private void addBitmapDrawableToMemoryCache(String key, BitmapDrawable bitmapDrawable) {
        if (null == getBitmapDrawableFromMemoryCache(key)) {
            getMemoryCache().put(key, bitmapDrawable);
        }
    }

    private BitmapDrawable getBitmapDrawableFromMemoryCache(String key) {
        return getMemoryCache().get(key);
    }

    //untuk Disk Cache
    private void initDiskImageCacheTask(Context context, AddDiskCacheListener listener) {
        new Thread(() -> {
            try {
                if (null == diskLruImageCache) {
                    diskLruImageCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                            , DISK_CACHE_SIZE, Bitmap.CompressFormat.WEBP, 100);
                    diskCacheLock.notifyAll(); // Wake any waiting threads
                }
                listener.onDiskCacheNotNull();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initDiskImageCacheTask(Context context) {
        new Thread(() -> {
            synchronized (diskCacheLock) {
                try {
                    if (null == diskLruImageCache) {
                        diskLruImageCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                                , DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 100);
                        diskCacheLock.notifyAll(); // Wake any waiting threads
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initDiskUriCacheTask(Context context, AddDiskCacheListener listener) {
        new Thread(() -> {
            try {
                if (null == diskLruUriCache) {
                    diskLruUriCache = new DiskLruUriCache(context, context.getResources().getString(R.string.app_name), DISK_CACHE_SIZE);
                    diskCacheLock.notifyAll(); // Wake any waiting threads
                }
                listener.onDiskCacheNotNull();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initDiskUriCacheTask(Context context) {
        new Thread(() -> {
            synchronized (diskCacheLock) {
                try {
                    if (null == diskLruUriCache) {
                        diskLruUriCache = new DiskLruUriCache(context, context.getResources().getString(R.string.app_name), DISK_CACHE_SIZE_SMALL);
                        diskCacheLock.notifyAll(); // Wake any waiting threads
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //harus background thread
    public void addBitmapDrawableToCache(String key, BitmapDrawable bitmapDrawable) {
        new Thread(() -> {
            if (getBitmapDrawableFromMemoryCache(key) == null) {
                addBitmapDrawableToMemoryCache(key, bitmapDrawable);
            }

            new Thread(() -> initDiskImageCacheTask(context, () ->
                    addBitmapDrawableToDiskCache(key, bitmapDrawable))).start();
        }).start();
    }

    private void addBitmapDrawableToDiskCache(String key, BitmapDrawable bitmapDrawable) {
        // Also add to disk cache
        new Thread(() -> {
            if (diskLruImageCache != null && diskLruImageCache.getBitmapDrawable(context, key) == null) {
                diskLruImageCache.put(key, bitmapDrawable);
            }
        }).start();
    }

    public BitmapDrawable getBitmapDrawable(String key) {
        if (null != getMemoryCache().get(key)) {
            // Get image from memory cache
            return getMemoryCache().get(key);
        } else if (null != diskLruImageCache && diskLruImageCache.containsKey(key)) {
            // Get image from disk cache
            return diskLruImageCache.getBitmapDrawable(context, key);
        } else return null;
    }

    public void addUriToDiskCache(String key, Uri uri) {
        new Thread(() -> {
            if (diskLruUriCache != null && diskLruUriCache.getUri(key) == null) {
                diskLruUriCache.put(key, uri);
            }
        }).start();
    }

    public Uri getUri(String key) {
        if (null != diskLruUriCache && diskLruUriCache.containsKey(key)) {
            return diskLruUriCache.getUri(key);
        } else {
            return null;
        }
    }
}
