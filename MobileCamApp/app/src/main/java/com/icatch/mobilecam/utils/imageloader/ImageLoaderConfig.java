package com.icatch.mobilecam.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImageLoaderConfig {

    private static final String TAG = "ImageLoaderConfig";
    private static FileNameGenerator fileNameGenerator;
    private static DiskCache diskCache;
    private static DisplayImageOptions options;
    private static ExecutorService taskExecutor;

    public static void initImageLoader(Context context, ImageDownloader imageDownloader) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        if(ImageLoader.getInstance().isInited()){
            ImageLoader.getInstance().destroy();
        }
        fileNameGenerator = new Md5FileNameGeneratorMatchFaceName();
//        diskCache = DefaultConfigurationFactory
//                .createDiskCache(context, fileNameGenerator, 1024 * 1024 * 1024, 5000);
//        diskCache = new UnlimitedDiskCache(cacheDir,null,fileNameGenerator);
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
//        taskExecutor = Executors.newSingleThreadExecutor();
//        config.taskExecutor(taskExecutor);
//        taskExecutor = (ExecutorService) DefaultConfigurationFactory
//                .createExecutor(1, Thread.NORM_PRIORITY - 2, QueueProcessingType.FIFO);
//        config.taskExecutor(taskExecutor);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.threadPoolSize(1);
        config.denyCacheImageMultipleSizesInMemory();
//        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheFileNameGenerator(fileNameGenerator);
        config.memoryCacheSize(1024 * 1024 * 100);
        config.diskCacheSize(1024 * 1024 * 500); // 200 MB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (imageDownloader != null) {
            config.imageDownloader(imageDownloader);
        }
        config.defaultDisplayImageOptions(getDefaultDisplayOptions());
//        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public static DisplayImageOptions getDefaultDisplayOptions() {
        return getSingletonDisplayOptions();
    }

    public static DisplayImageOptions getDefaultDisplayOptions(int defaultImg) {
        options = getSingletonDisplayOptions();
        options = new DisplayImageOptions.Builder()
                .cloneFrom(options)
                .showImageOnLoading(defaultImg)
                .showImageForEmptyUri(defaultImg)
                .showImageOnFail(defaultImg)
                .build();
        return options;
    }

    public static void clearDiskCache() {
        if (diskCache == null) {
            Log.e(TAG, "clearDiskCache: diskCache null");
            return;
        }
        diskCache.clear();
    }

    private static synchronized DisplayImageOptions getSingletonDisplayOptions() {
        if (options == null) {
            synchronized (ImageLoaderConfig.class) {
                if (options == null) {
                    options = new DisplayImageOptions.Builder()
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .considerExifParams(true)
                            .build();
                }
            }
        }
        return options;
    }

    public static void saveDiskCache(String url, Bitmap bitmap) {
        if (url == null || url.length() == 0 || bitmap == null) {
            return;
        }
        try {
            boolean ret = diskCache.save(url, bitmap);
            Log.d(TAG, "saveDiskCache ret:" + ret + " url:" + url);
        } catch (IOException e) {
            Log.e(TAG, "saveDiskCache IOException e:" + e.getMessage());
            e.printStackTrace();
        }

//        MemoryCache memoryCache = ImageLoader.getInstance().getMemoryCache();
    }

    public static void removeDiskCache(String url) {
        if (url == null || url.length() == 0) {
            return;
        }
        boolean ret = diskCache.remove(url);
//        MemoryCache memoryCache = ImageLoader.getInstance().getMemoryCache();
//        if(memoryCache != null){
//            memoryCache.remove(url);
//        }
        Log.d(TAG, "removeDiskCache ret:" + ret + " url:" + url);
    }

    public static void stopLoad(){
        ImageLoader.getInstance().stop();
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if(taskExecutor != null){
//            taskExecutor.shutdown();
//            while (true){
//                if(taskExecutor.isTerminated()){
//                    break;
//                }
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            try {
//                taskExecutor.awaitTermination(5000,TimeUnit.MINUTES);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
