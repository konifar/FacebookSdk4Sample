package com.konifar.facebooksdk4sample;

import android.app.Application;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MainApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    initUniversalImageLoader();
  }

  private void initUniversalImageLoader() {
    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheOnDisk(true)
        .cacheInMemory(true)
        .showImageForEmptyUri(R.color.grey200)
        .showImageOnFail(R.color.grey200)
        .imageScaleType(ImageScaleType.EXACTLY)
        .build();

    ImageLoaderConfiguration config =
        new ImageLoaderConfiguration.Builder(getApplicationContext()).discCache(
            new UnlimitedDiscCache(StorageUtils.getCacheDirectory(getApplicationContext())))
            .diskCacheSize(64 * 1024 * 1024)
            .memoryCache(new LruMemoryCache(16 * 1024 * 1024))
            .memoryCacheSize(16 * 1024 * 1024)
            .defaultDisplayImageOptions(options)
            .build();

    ImageLoader.getInstance().init(config);
  }
}
