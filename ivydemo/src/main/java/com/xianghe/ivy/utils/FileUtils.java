package com.xianghe.ivy.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xianghe.ivy.app.IvyApp;
import com.xianghe.ivy.ui.media.config.MediaConfig;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;

    public final static String IMAGE_NAME = "/cache";

    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
        makeAppDir();
    }

    public static String getExtensionName(String fileName) {
        String result = null;
        if (!TextUtils.isEmpty(fileName)) {
            result = fileName.substring(fileName.lastIndexOf('.') + 1);
        }
        return result;
    }

    public String makeAppDir() {
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        path = path + IMAGE_NAME;
        folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        return path;
    }

    /**
     * 获取储存Image的目录
     * 此处目录不对 MediaConfig.VIDEO_XH_RECORDER  ，请校验后再调用此方法
     *
     * @return
     */
    public String getStorageDirectory() {
        String localPath = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ?
                mSdRootPath + MediaConfig.VIDEO_XH_RECORDER : mDataRootPath + MediaConfig.VIDEO_XH_RECORDER;
        File folderFile = new File(localPath);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        return localPath;
    }

    public String getMediaVideoPath() {
        String directory = getStorageDirectory();
        directory += "/video";
        File file = new File(directory);
        if (!file.exists()) {
            file.mkdir();
        }
        return directory;
    }

    /**
     * 删除文件
     */
    public void deleteFile(String deletePath, String videoPath) {
        File file = new File(deletePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    if (f.listFiles().length == 0) {
                        f.delete();
                    } else {
                        deleteFile(f.getAbsolutePath(), videoPath);
                    }
                } else if (!f.getAbsolutePath().equals(videoPath)) {
                    f.delete();
                }
            }
        }
    }


    // ------------ audio start ---------------

    /**
     * 传入目录和文件后缀获取到临时文件名
     *
     * @param dir
     * @param name
     * @return
     */
    public static File createTempFile(String dir, String name) {
        dir = File.separator + dir;
        // /storage/emulated/0/ivy_audio_recorder/1540537584886.mp3
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath() + dir;
        }
        try {
            File file = createFile(dir, name);
            if (file != null) {
                return file;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 创建目录
        dir = IvyApp.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + dir;
        File f = new File(dir);
        // 要保证目录存在，如果不存在则主动创建
        if (!f.exists()) {
            f.mkdirs();
        }
        try {
            return createFile(dir, name);
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
    }

    /**
     * 创建临时文件夹
     *
     * @param dir
     * @return
     */
    public static String createTempDir(String dir) {
        dir = File.separator + dir;
        // /storage/emulated/0/ivy_audio_recorder
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory().getAbsolutePath() + dir;
            File recordDir = new File(dir);
            // 要保证目录存在，如果不存在则主动创建
            if (!recordDir.exists()) {
                recordDir.mkdirs();
            }
            return dir;
        }
        try {
            boolean isDir = com.blankj.utilcode.util.FileUtils.createOrExistsDir(dir);
            if (isDir) {
                return dir;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dir = IvyApp.getInstance().getApplicationContext().getFilesDir().getAbsolutePath() + dir;
            File f = new File(dir);
            // 要保证目录存在，如果不存在则主动创建
            if (!f.exists()) {
                f.mkdirs();
            }
        }
        return dir;
    }


    private static File createFile(String dir, String name) throws IOException {
        boolean isDir = com.blankj.utilcode.util.FileUtils.createOrExistsDir(dir);
        if (isDir) {
            File file = com.blankj.utilcode.util.FileUtils.getFileByPath(dir + File.separator + name);
            if (file == null) return null;
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw new IOException();
                }
            }
            return file;
        } else {
            return null;
        }
    }

    // 删除单个文件
    public static boolean deleteTempFile(File file) {
        return com.blankj.utilcode.util.FileUtils.deleteFile(file);
    }

    //删除单个文件
    public static boolean deleteTempFilePath(String srcFilePath) {
        return deleteTempFilePathALL(srcFilePath, true);
    }

   public static boolean deleteTempFilePathALL(String srcFilePath) {
        return deleteTempFilePathALL(srcFilePath, false);
    }

    private static boolean deleteTempFilePathALL(String srcFilePath, boolean is_xh_recorder) {
        if (null == srcFilePath || TextUtils.isEmpty(srcFilePath)) {
            return false;
        }
        if (is_xh_recorder) {
            if (srcFilePath.contains(MediaConfig.VIDEO_XH_RECORDER)) { // 只删除xh_record里面的视频
                return com.blankj.utilcode.util.FileUtils.deleteFile(srcFilePath);
            } else {
                return false;
            }
        } else {
            return com.blankj.utilcode.util.FileUtils.deleteFile(srcFilePath);
        }
    }

    //copy单个文件
    public static boolean copyTempFilePath(String srcFilePath, String destFilePath) {
        if (null == srcFilePath || TextUtils.isEmpty(srcFilePath)) {
            return false;
        }
        return com.blankj.utilcode.util.FileUtils.copyFile(srcFilePath, destFilePath, null);
    }

    // 删除指定文件夹文件
    public static boolean deleteTempFileDir(File file) {
        return com.blankj.utilcode.util.FileUtils.deleteDir(file);
    }

    // 删除指定文件夹文件
    public static boolean deleteTempFileDir(String srcFilePath) {
        if (null == srcFilePath || TextUtils.isEmpty(srcFilePath)) {
            return false;
        }
        if (srcFilePath.contains(MediaConfig.VIDEO_XH_RECORDER)) { // 只删除xh_record里面的视频
            return com.blankj.utilcode.util.FileUtils.deleteDir(srcFilePath);
        } else {
            return false;
        }
    }

    //删除视频录制目录下所有文件，除了临时图片文件和视频文件
    public static boolean deleteFilesInRecordExpertFilter(List<String> filePath) {
        return deleteFilesInDirWithFilter(FileUtils.createTempDir(MediaConfig.VIDEO_XH_RECORDER), file -> {
            // 合成的目录内的文件不删除
            if ((file.isFile() || file.isDirectory()) && (filePath.contains(file.getAbsolutePath()))) {
                return false;
            }
            return true;
        });
    }

    //删除视频录制目录下所有文件
    public static boolean deleteFilesAllRecord() {
        return com.blankj.utilcode.util.FileUtils.deleteAllInDir(FileUtils.createTempDir(MediaConfig.VIDEO_XH_RECORDER));
    }

    // -------------------  删除文件，指定部分不删除  start  ------------
    public static boolean deleteFilesInDirWithFilter(final String dirPath,
                                                     final FileFilter filter) {
        return deleteFilesInDirWithFilter(com.blankj.utilcode.util.FileUtils.getFileByPath(dirPath), filter);
    }

    public static boolean deleteFilesInDirWithFilter(final File dir, final FileFilter filter) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file, filter)) return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean deleteDir(final File dir, final FileFilter filter) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        if (!filter.accept(dir)) return false; // 如果目录在里面也不删除
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (filter.accept(file)) {
                    if (file.isFile()) {
                        if (!file.delete()) return false;
                    } else if (file.isDirectory()) {
                        if (!deleteDir(file, filter)) return false;
                    }
                }
            }
        }
        return dir.delete();
    }

    // -------------------  删除文件，指定部分不删除  end  ------------
    public static String sizeTempFile(File file) {
        return com.blankj.utilcode.util.FileUtils.getFileSize(file);
    }
    // ------------ audio end ---------------


    /**
     * 这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片了！
     *
     * @param context context
     * @param file    file (注:要想相册能够成功显示, 文件名必须有规范的后缀名. 如：.mp4)
     */
    public static void scanFile(@NonNull Context context, @Nullable File file) {
        if (file == null) {
            return;
        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 获取SD卡的剩余容量，单位是Byte
     *
     * @return
     */
    public static long getSDFreeMemory() {
        try {
            if (isSDExists()) {
                File pathFile = Environment.getExternalStorageDirectory();
                // Retrieve overall information about the space on a filesystem.
                // This is a Wrapper for Unix statfs().
                StatFs statfs = new StatFs(pathFile.getPath());
                // 获取SDCard上每一个block的SIZE
                long nBlockSize = statfs.getBlockSize();
                // 获取可供程序使用的Block的数量
                // long nAvailBlock = statfs.getAvailableBlocksLong();
                long nAvailBlock = statfs.getAvailableBlocks();
                // 计算SDCard剩余大小Byte
                long nSDFreeSize = nAvailBlock * nBlockSize;
                return nSDFreeSize;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * SD卡存在并可以使用
     */
    public static boolean isSDExists() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
