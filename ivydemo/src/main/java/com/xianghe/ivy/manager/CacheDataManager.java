package com.xianghe.ivy.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.util.ConvertUtils;

import java.io.File;
import java.math.BigDecimal;

/**
 * 缓存管理
 */
public class CacheDataManager {
    /**
     * 获取缓存大小
     * @param context
     * @return
     */
    public static String getTotalCacheSize(Context context) {

        long cacheSize = 0;
        try {
            cacheSize = getFolderSize(context.getCacheDir());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                cacheSize += getFolderSize(context.getExternalCacheDir());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return byte2FitMemorySize(cacheSize);
    }

    /**
     * Size of byte to fit size of memory.
     * <p>to three decimal places</p>
     *
     * @param byteSize Size of byte.
     * @return fit size of memory
     */
    @SuppressLint("DefaultLocale")
    public static String byte2FitMemorySize(final long byteSize) {
        if (byteSize < 0) {
            return "shouldn't be less than zero!";
        } else if (byteSize == 0){
            return String.format("%.2fM",0.0);
        }else if (byteSize < MemoryConstants.KB) {
            return String.format("%.2fB", BigDecimal.valueOf(byteSize).setScale(2,BigDecimal.ROUND_DOWN));
        } else if (byteSize < MemoryConstants.MB) {
            return String.format("%.2fK", BigDecimal.valueOf(byteSize).divide(BigDecimal.valueOf(MemoryConstants.KB),2,BigDecimal.ROUND_DOWN));
        } else if (byteSize < MemoryConstants.GB) {
            return String.format("%.2fM", BigDecimal.valueOf(byteSize).divide(BigDecimal.valueOf(MemoryConstants.MB),2,BigDecimal.ROUND_DOWN));
        } else {
            return String.format("%.2fG", BigDecimal.valueOf(byteSize).divide(BigDecimal.valueOf(MemoryConstants.GB),2,BigDecimal.ROUND_DOWN));
        }
    }

    /**
     * 清理缓存
     * @param context
     * @return
     */
    public static boolean clearAllCache(Context context) {

        boolean result;
        result = deleteDir(context.getCacheDir());

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            result = false;
            result = deleteDir(context.getExternalCacheDir());

        }
        return result;
    }

    private static boolean deleteDir(File dir) {

        if (dir == null){
            return true;
        }
        if (dir.isDirectory()) {

            String[] children = dir.list();

            for (int i = 0; i < children.length; i++) {

                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }

            }
        }
        return dir.delete();
    }

    // 获取文件

    // Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/

    // 目录，一般放一些长时间保存的数据

    // Context.getExternalCacheDir() -->

    // SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据

    private static long getFolderSize(File file) throws Exception {

        long size = 0;

        try {

            File[] fileList = file.listFiles();

            for (int i = 0; i < fileList.length; i++) {

                // 如果下面还有文件

                if (fileList[i].isDirectory()) {

                    size = size + getFolderSize(fileList[i]);

                } else {

                    size = size + fileList[i].length();

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return size;

    }

}

