package io.taptalk.TapTalk.Manager;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;

import androidx.annotation.Nullable;

import io.taptalk.TapTalk.Helper.DiskLruCache.DiskLruImageCache;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.R;


public class TAPCacheManager {
    private static final String TAG = TAPCacheManager.class.getSimpleName();
    private static TAPCacheManager instance;
    private Context context;

    // Memory Cache Attributes
    private LruCache<String, BitmapDrawable> memoryCache;

    // Disk Cache Attributes
    private DiskLruImageCache diskLruCache;
    private final Object diskCacheLock = new Object();
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

    // Memory Cache
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
                if (null == value || null == value.getBitmap()) {
                    return 0;
                }
                return value.getBitmap().getByteCount() / 1024;
            }
        };
        return memoryCache;
    }

    public void initAllCache() {
        initMemoryCache();
        initDiskCacheTask(context);
    }

    private void addBitmapDrawableToMemoryCache(String key, BitmapDrawable bitmapDrawable) {
        if (null != key && null == getBitmapDrawableFromMemoryCache(key)) {
            getMemoryCache().put(key, bitmapDrawable);
        }
    }

    private BitmapDrawable getBitmapDrawableFromMemoryCache(String key) {
        if (null == key) {
            return null;
        }
        return getMemoryCache().get(key);
    }

    // Disk Cache
    private void initDiskCacheTask(Context context, AddDiskCacheListener listener) {
        new Thread(() -> {
            try {
                if (null == diskLruCache) {
                    diskLruCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                            , DISK_CACHE_SIZE, Bitmap.CompressFormat.WEBP, 100);
                    diskCacheLock.notifyAll(); // Wake any waiting threads
                }
                listener.onDiskCacheNotNull();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initDiskCacheTask(Context context) {
        new Thread(() -> {
            synchronized (diskCacheLock) {
                try {
                    if (null == diskLruCache) {
                        diskLruCache = new DiskLruImageCache(context, context.getResources().getString(R.string.app_name)
                                , DISK_CACHE_SIZE, Bitmap.CompressFormat.JPEG, 100);
                        diskCacheLock.notifyAll(); // Wake any waiting threads
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Requires Background Thread
    public void addBitmapDrawableToCache(String key, BitmapDrawable bitmapDrawable) {
        if (null == key || null == bitmapDrawable) {
            return;
        }
        if (key.length() > 100) {
            key = TAPUtils.getLastCharacters(key, 100);
        }
        String finalKey = key;
        new Thread(() -> {
            if (getBitmapDrawableFromMemoryCache(finalKey) == null) {
                addBitmapDrawableToMemoryCache(finalKey, bitmapDrawable);
            }

            new Thread(() -> initDiskCacheTask(context, () ->
                    addBitmapDrawableToDiskCache(finalKey, bitmapDrawable))).start();
        }).start();
    }

    private void addBitmapDrawableToDiskCache(String key, BitmapDrawable bitmapDrawable) {
        // Also add to disk cache
        new Thread(() -> {
            if (null == key || null == diskLruCache) {
                return;
            }
            if (diskLruCache.getBitmapDrawable(context, key) == null) {
                diskLruCache.put(key, bitmapDrawable);
            }
        }).start();
    }

    @Nullable
    public BitmapDrawable getBitmapDrawable(TAPMessageModel message) {
        if (message == null || message.getData() == null) {
            return null;
        }
        BitmapDrawable drawable = null;
        String fileUrl = (String) message.getData().get(FILE_URL);
        if (null != fileUrl && !fileUrl.isEmpty()) {
            drawable = getBitmapDrawable(getCacheKeyFromUrl(fileUrl));
        }
        if (null == drawable) {
            String fileID = (String) message.getData().get(FILE_ID);
            if (null != fileID && !fileID.isEmpty()) {
                drawable = getBitmapDrawable(fileID);
            }
        }
        return drawable;
    }

    @Nullable
    public BitmapDrawable getBitmapDrawable(String fileUrl, String fileID) {
        if (fileUrl == null && fileID == null) {
            return null;
        }
        BitmapDrawable drawable = null;
        if (null != fileUrl && !fileUrl.isEmpty()) {
            drawable = getBitmapDrawable(getCacheKeyFromUrl(fileUrl));
        }
        if (null == drawable && null != fileID && !fileID.isEmpty()) {
            drawable = getBitmapDrawable(fileID);
        }
        return drawable;
    }

    @Nullable
    public BitmapDrawable getBitmapDrawable(String key) {
        if (null == key) {
            return null;
        }
        if (key.length() > 100) {
            key = TAPUtils.getLastCharacters(key, 100);
        }
        if (null != getMemoryCache().get(key)) {
            // Get image from memory cache
            return getMemoryCache().get(key);
        } else if (null != diskLruCache && diskLruCache.containsKey(key)) {
            // Get image from disk cache
            return diskLruCache.getBitmapDrawable(context, key);
        } else {
            return null;
        }
    }

    public void removeFromCache(String key) {
        new Thread(() -> {
            if (null == key || null == diskLruCache) {
                return;
            }

            getMemoryCache().remove(key);
            diskLruCache.remove(key);

            String trimmedKey = key;
            if (trimmedKey.length() > 100) {
                trimmedKey = TAPUtils.getLastCharacters(key, 100);
                getMemoryCache().remove(trimmedKey);
                diskLruCache.remove(trimmedKey);
            }
        }).start();
    }

    public boolean containsCache(TAPMessageModel message) {
        if (message == null || message.getData() == null) {
            return false;
        }
        boolean isCacheExists = false;
        String fileUrl = (String) message.getData().get(FILE_URL);
        if (null != fileUrl && !fileUrl.isEmpty()) {
            isCacheExists = containsCache(getCacheKeyFromUrl(fileUrl));
        }
        if (isCacheExists) {
            return true;
        }
        String fileID = (String) message.getData().get(FILE_ID);
        if (null != fileID && !fileID.isEmpty()) {
            isCacheExists = containsCache(fileID);
        }
        return isCacheExists;
    }

    public boolean containsCache(String key) {
        if (null == key || null == diskLruCache) {
            return false;
        }
        if (key.length() > 100) {
            key = TAPUtils.getLastCharacters(key, 100);
        }
        return null != getMemoryCache().get(key) || diskLruCache.containsKey(key);
    }

    private String getCacheKeyFromUrl(String url) {
        String key = TAPUtils.removeNonAlphaNumeric(url).toLowerCase();
        key = TAPUtils.getLastCharacters(key, 100);
        return key;
    }

    public void clearCache() {
        new Thread(() -> {
            if (null == diskLruCache) {
                return;
            }
            getMemoryCache().evictAll();
            diskLruCache.clearCache();
            diskLruCache = null;
        }).start();
    }
}
