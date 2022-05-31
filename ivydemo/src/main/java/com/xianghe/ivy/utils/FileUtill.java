package com.xianghe.ivy.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtill {
    /**
     * 创建文件
     * <p>
     * 如果是/sdcard/download/123.doc则只需传入filePath=download/123.doc
     *
     * @param filePath 文件路径
     * @return 创建文件的路径
     */
    public static String creatFile2SDCard(String filePath) throws IOException {
        // 无论传入什么值 都是从根目录开始 即/sdcard/+filePath
        // 创建文件路径包含的文件夹
        String filedir = creatDir2SDCard(getFileDir(filePath));
        String fileFinalPath = filedir + getFileName(filePath);
        File file = new File(fileFinalPath);
        if (!file.exists()) {
            file.createNewFile();
        }
        return fileFinalPath;
    }

    public static String creatSdFile(String filePath) {
        return creatDir2SDCard(getFileDir(filePath));
    }

    public static String createSaveFile(Context context, String dirName) {
        String savePath;
        try {
            savePath = FileUtill.creatFile2SDCard(dirName);
        } catch (IOException e) {
            savePath = FileUtill.creatDir2SDCard(context.getFilesDir().getAbsolutePath() + dirName);
        }
        return savePath;
    }

    /**
     * 创建文件夹
     */
    public static String creatDir2SDCard(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {// 判断文件是否存在
            file.mkdirs();
        }
        return dirPath;
    }

    /**
     * 创建文件夹
     */
    public static File creatDir2SDCardFile(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {// 判断文件是否存在
            file.mkdirs();
        }
        return file;
    }

    public static ArrayList<File> getWeiChatVideoFile(String dirPath) {
        ArrayList<File> fileDirs = new ArrayList<>();
        File fileDir = creatDir2SDCardFile(getSDCardPath() + dirPath);
        if (fileDir.exists() && fileDir.length() > 0) {
            //遍历文件夹
            File[] files = fileDir.listFiles();
            if (files != null && files.length > 0) {
                ArrayList<File> longNameFile = getLongNameFile(files);
                //判断文件是否为空
                if (longNameFile != null && longNameFile.size() > 0) {
                    //遍历获取
                    for (File file : longNameFile) {
                        File videoFile = new File(file, "video");
                        if (videoFile.exists() && videoFile.length() > 0) {
                            fileDirs.add(videoFile);
                        }
                    }
                }
            }
        }
        return fileDirs;
    }


    /**
     * 获取数组最值
     */
    private static ArrayList<File> getLongNameFile(File[] files) {
        ArrayList<File> fileDirs = new ArrayList<>();
        for (File file : files) {
            if (file.exists() && file.length() > 0 && file.getName().length() > 20) {
                fileDirs.add(file);
            }
        }
        return fileDirs;
    }


    public static boolean deleteFile(final File dir) {
        if (dir == null) return false;
        // dir doesn't exist then return true
        if (!dir.exists()) return true;
        // dir isn't a directory then return false
        if (!dir.isDirectory()) return false;
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!file.delete()) return false;
                } else if (file.isDirectory()) {
                    if (!deleteFile(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    // 删除指定文件夹文件
    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    public static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

    public static String getDefaultDir(Context context) {
        String var1 = null;
        if (!"mounted".equals(Environment.getExternalStorageState()) && Environment.isExternalStorageRemovable()) {
            File var4 = context.getFilesDir();
            if (var4 != null) {
                var1 = var4.getPath();
            }

            return var1;
        } else {
            String var2 = context.getExternalMediaDirs()[0].getAbsolutePath() + File.separator + "TXUGC";
            File var3 = new File(var2);
            if (!var3.exists()) {
                var3.mkdir();
            }

            return var2;
        }
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    /**
     * 获取文件名
     */
    private static String getFileName(String filePath) {
        int index = 0;
        String tempName = "";
        if ((index = filePath.lastIndexOf("/")) != -1) {
            // 如果有后缀名才
            tempName = filePath.substring(index + 1);
        }
        return tempName.contains(".") ? tempName : "";
    }

    /**
     * 获取文件目录路径
     *
     * @param filePath
     * @return
     */
    private static String getFileDir(String filePath) {
        if (getSDCardPath() != null) {
            if (filePath.startsWith(getSDCardPath())) {
                return filePath.replace(getFileName(filePath), "");
            }
        }
        return getSDCardPath() + filePath.replace(getFileName(filePath), "");
    }


    /**
     * 获取SD卡路径
     *
     * @return /sdcard/
     */
    public static String getSDCardPath() {
        if (sdCardIsExit()) {
            return Environment.getExternalStorageDirectory().toString() + "/";
        }
        return null;
    }

    /**
     * 判断SD卡是否存在
     *
     * @return
     */
    public static boolean sdCardIsExit() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
