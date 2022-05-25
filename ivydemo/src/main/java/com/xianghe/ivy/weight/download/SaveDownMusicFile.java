package com.xianghe.ivy.weight.download;

import com.blankj.utilcode.util.EncryptUtils;
import com.xianghe.ivy.ui.media.config.MediaConfig;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 作者：created by huangjiang on 2018/9/19  15:10
 * 邮箱：504512336@qq.com
 * 描述：
 */
public class SaveDownMusicFile {

    public static SaveDownMusicFile sDownMusicFile;
    private final String filePath;

    private SaveDownMusicFile() {
        File file = FileUtils.createTempFile(MediaConfig.AUDIO_DIR_DOWNLOAD_MUSIC, MediaConfig.AUDIO_DOWNMUSICLIST);
        if (file == null) {
            this.filePath = null;
        } else {
            this.filePath = file.getAbsolutePath();
        }
        KLog.i("filePath = " + filePath);
    }

    public static String getKey(String musicName, String musicId) {
        return EncryptUtils.encryptMD5ToString(musicId + "|" + musicName);
    }

    public static SaveDownMusicFile getInstance() {
        if (sDownMusicFile == null) {
            synchronized (SaveDownMusicFile.class) {
                if (sDownMusicFile == null) {
                    sDownMusicFile = new SaveDownMusicFile();
                }
            }
        }
        return sDownMusicFile;
    }


    public boolean hasFile() {
        return filePath != null;
    }

    public boolean saveMusicToFile(String key, String musicPath) {
        if (!hasFile()) return false;
        KLog.i(filePath);

        String content = key + ":" + musicPath;
        File file = new File(filePath);
        if (file.exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(filePath, true);
                content = ":" + content;
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            FileOutputStream fileOutput = null;
            try {
                fileOutput = new FileOutputStream(filePath);
                fileOutput.write(content.getBytes());
                fileOutput.flush();
                fileOutput.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fileOutput != null) {
                        fileOutput.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * @Date 创建时间: 2018/9/28
     * @Description 描述：读取本地文件列表，是否有下载此音乐
     */
    public String readMusicFile(String key) {
        if (!hasFile()) return null;
        KLog.i(filePath);

        String musicPath = "";
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];

            int len;
            while ((len = fis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            String result = new String(bos.toByteArray());

            KLog.i(result);
//            ToastUtils.showShort("读取数据成功!" + result);
            String[] str = result.split(":");
            for (int i = 0; i < str.length; i++) {
                if (key.equals(str[i])) {
                    musicPath = str[i + 1];
                    KLog.i(musicPath);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return musicPath;
    }

//    /**
//     * 将集合写入sd卡
//     *
//     * @param fileName 文件名
//     * @param list     集合
//     * @return true 保存成功
//     */
//    public boolean writeListIntoSDcard(String fileId, String musicPath) {
//        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            List<String>
////            File sdCardDir = Environment.getExternalStorageDirectory();//获取sd卡目录
//            File sdFile = new File(filePath);
//            try {
//                FileOutputStream fos = new FileOutputStream(sdFile);
//                ObjectOutputStream oos = new ObjectOutputStream(fos);
//                oos.writeObject(list);//写入
//                fos.close();
//                oos.close();
//                return true;
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return false;
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return false;
//            }
//        } else {
//            return false;
//        }
//
//    }
//
//
//    /**
//     * 读取sd卡对象
//     * @param fileName 文件名
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public List<String> readListFromSdCard(String fileName){
//        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){  //检测sd卡是否存在
//            List<String> list;
//            File sdCardDir = Environment.getExternalStorageDirectory();
//            File sdFile = new File(sdCardDir,fileName);
//            try {
//                FileInputStream fis = new FileInputStream(sdFile);
//                ObjectInputStream ois = new ObjectInputStream(fis);
//                list = (List<String>) ois.readObject();
//                fis.close();
//                ois.close();
//                return list;
//            } catch (StreamCorruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return null;
//            } catch (OptionalDataException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return null;
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return null;
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return null;
//            } catch (ClassNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//                return null;
//            }
//        }
//        else {
//            return null;
//        }
//    }

}
