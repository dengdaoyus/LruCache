package cn.ddy.lrucache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import java.io.IOException;

/**
 * 缓存Helper
 * Created by Administrator on 2017/12/13 0013.
 */

public class LruCacheHelper {
    private static final String DIR_NAME = "lruCache";
    private static final String TAG = "LruCacheHelper";
    private long maxMemory = Runtime.getRuntime().maxMemory();
    private int cacheSize = (int) (maxMemory / 8);
    private LruCache<String, Bitmap> lruCache;
    private DiskLruCacheHelper diskLruCacheHelper;

    public LruCacheHelper(Context context) {
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        try {
            diskLruCacheHelper = new DiskLruCacheHelper(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 添加缓存
     *
     * @param key 路径地址
     * @return Bitmap
     */
    public Bitmap getLruCache(String key) {
        if (!TextUtils.isEmpty(key) && lruCache != null && lruCache.get(key) != null) {
            return lruCache.get(key);
        } else {
            if (diskLruCacheHelper.get(key) != null) {
                return diskLruCacheHelper.getAsBitmap(key);
            } else {
                return null;
            }
        }
    }

    public void putLruCache(String key, Bitmap bmp) {
        try {
            if (!TextUtils.isEmpty(key) && bmp != null) {
                lruCache.put(key, bmp);
                diskLruCacheHelper.put(key, bmp);
            }
        } catch (Exception e) {

        }
    }

    public void deleteCache() {
        if (lruCache != null)
            lruCache.evictAll();
        if (diskLruCacheHelper != null) {
            try {
                diskLruCacheHelper.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeCache(String key) {
        if (lruCache != null)
            lruCache.remove(key);
        if (diskLruCacheHelper != null) {
            diskLruCacheHelper.remove(key);
        }
    }

    public int size() {
        int size = 0;
        if (lruCache != null)
            size += lruCache.size();
        return size;
    }
}
