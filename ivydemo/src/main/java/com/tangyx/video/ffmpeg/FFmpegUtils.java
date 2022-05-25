package com.tangyx.video.ffmpeg;

import android.text.TextUtils;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.ui.module.record.model.MovieItemModel;
import com.xianghe.ivy.ui.module.videocall.service.FileUtil;
import com.xianghe.ivy.utils.FileUtill;
import com.xianghe.ivy.utils.FileUtils;
import com.xianghe.ivy.utils.IvyUtils;
import com.xianghe.ivy.utils.KLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 *  备注：
 *      1> 如果没有添加音乐，添加录音，就设置其音量=0，后续跟居音量是否==0 判断有无添加音乐，录音
 *      2> DELETE_TEMP 参数控制，默认debug不删除中间视频处理文件，便于分析，release删除
 *      3> 所有的音频处理，都是先处理录音在处理音乐，单线程流程执行，一个执行完成再执行另一个，没有并行执行（测试过并行处理会挂）
 *
 *  处理视频start：
 *       一. audioAndVideoSynthesis -->
                1. size ==1,单个视频，
                    判断有无音频轨道的视频，是根据是否是图片合成视频的类型判断
                        a.有音频轨道（不是图片合成视频），执行提取视频->提取音频，获取到原声的音视频，后续对其音量处理，音频合成，裁剪等
                        b.无音频轨道（是图片合成视频），
                                如果没有添加音乐，录音，直接返回无声视频
                                有添加音乐，录音，先判断有无录音，执行录音音量修改
                2. size !=1, 多个视频，执行多段视频合成一个视频
                    判断有无本地视频(本地视频格式，分辨率多样，所有需要转码，自己拍摄的视频格式一致，直接合成即可)：
                        a.有本地视频，执行本地视频转ts，在执行合成视频视频命令
                        b.无本地视频，全部是自己拍摄的视频，直接合成视频，不需要转码

        二. 视频提取阶段完成，开始音频处理->
                1.  先判断有无录音，有录音，修改录音文件音量，执行完继续判断背景音乐流程
                2.  有无背景音乐，有背景音乐，先对背景音乐执行裁剪，再修改音量，
                        视频长度<=背景音乐的长度,音频阶段处理完成
                        视频长度>背景音乐的长度，需要再执行多个音乐的合成，就是后面音乐需要重复
                3.  背景音乐处理ok ->执行录音，视频原声，背景音乐合成(composeAudio)
                4.  所有的音频文件合成一个，在执行原声视频与音频文件合成end

 */
public class FFmpegUtils {

    private String targetPath = null;//存诸路径
    private OnFFmpegOutputListener listener = null;

    // 默认音量
   /* private final int defaultVideoVol = 50;
    private final int defaultRecordVol = 100;
    private final int defaultMusicVol = 50;*/

    private double videoDuration = 0L;//视频时长
    private int videoVol = 0;//视频音量
    private String recordPath = null;//录音路径
    private int recordVol = 0;//录音音量
    private String musicPath = null;//音乐路径
    private int musicVol = 0;//音乐音量
    private double musicStartTime = 0L;//音乐剪切开始时间
    private double musicSecond = 0L;//音乐剪切时长
    private boolean isPicToMovie = false;// 是否是图片合成视频


    private ArrayList<String> originalBgPathList = null; // 音乐的集合，可能包含有：背景音乐，录音，音乐
    private String originalVideoPath = null;//音视频分离后视频路径
//    private boolean DELETE_TEMP = !BuildConfig.DEBUG;// 是否删除合成中的临时文件


    public FFmpegUtils(String filePath) {
        targetPath = filePath;
        KLog.i("filePath: " + filePath);
        FileUtill.creatDir2SDCard(targetPath); // 重新创建一次，保证创建成功
    }


    private void release() {
        recordPath = null;
        musicPath = null;
        originalVideoPath = null;
        originalBgPathList.clear();
    }

    /**
     * 合成
     */
    public void audioAndVideoSynthesis(List<MovieItemModel> videoList,//多视频集合
                                       int videoVol,//视频原声音量
                                       double videoDuration,//视频时长
                                       String RecordPath,//录音路径
                                       int RecordVol,//录音音量
                                       String MusicPath,//音乐路径
                                       int MusicVol,//音乐音量
                                       double startTime,//音乐剪切开始位置
                                       double second,//音乐剪切长度
                                       boolean isPicToMovie, // 是否是图片合成视频
                                       final OnFFmpegOutputListener listener) {
        KLog.e("audioAndVideoSynthesis", "videoVol = " + videoVol
                + " \nvideoDuration = " + videoDuration
                + " \nRecordPath = " + RecordPath
                + " \nRecordVol = " + RecordVol
                + " \nMusicPath = " + MusicPath
                + " \nMusicVol = " + MusicVol
                + " \nstartTime = " + startTime
                + " \nsecond = " + second
        );

        if (RecordPath == null || TextUtils.isEmpty(RecordPath)) {
            RecordVol = 0;
        }
        if (MusicPath == null || TextUtils.isEmpty(MusicPath)) {
            MusicVol = 0;
        }

        this.videoDuration = videoDuration;
        this.videoVol = videoVol;
        this.recordPath = RecordPath;
        this.recordVol = RecordVol;
        this.musicPath = MusicPath;
        this.musicVol = MusicVol;
        this.musicStartTime = startTime;
        if (videoDuration * 1000 > second) {
            this.musicSecond = second;
        } else {
            this.musicSecond = videoDuration * 1000;
        }
        this.isPicToMovie = isPicToMovie;
        this.listener = listener;
        this.originalBgPathList = new ArrayList<>();
        if (videoList.size() > 0) {
            if (videoList.size() == 1) {
                if (isPicToMovie) { // 图片合成视频
                    if (recordVol == 0 && musicVol == 0) {
                        release();
                        if (listener != null) {
                            listener.OnFFmpegOutputListener(videoList.get(0).getFilePath());
                        }
                    } else {
                        // 存在视频原音，但是音量怎么调节并不知道
                        originalVideoPath = videoList.get(0).getFilePath();

                        // 针对音频的修改
                        if (recordVol != 0) {//如果有录音并且录音音量不为0
                            recordVolumeHandle(recordPath, recordVol);
                        } else if (musicVol != 0) {//　如果只有音乐 并且音量不为0
                            audioClipOperation(musicPath, musicStartTime, musicSecond, musicVol);
                        } else {
                            // 音乐，录音，背景音乐都没有
                        }
                    }
                } else { // 不是图片合成视频
                    extractVideo(videoList.get(0).getFilePath());
                }
            } else {
                boolean hasLocalVideo = false;
                for (MovieItemModel itemModel : videoList) {
                    if (itemModel.isVideo_from()) {
                        hasLocalVideo = true;
                        break;
                    }
                }

                // 如果有本地的视频，就需要转码，再合成
                if (hasLocalVideo) {
                    // 拆分TS在concat一个视频
                    toTSConcatVideo(videoList);
                } else {
                    // 没有本地的视频只需要合成即可
                    // 多个视频，先合成一个视频
                    mererVideo(videoList);
                }
            }
        }
    }

    /**
     * 合并的视频
     */
    private void mererVideo(List<MovieItemModel> videoList) {
        KLog.e("onStart: ----------合并视频 start------------ ");
        final File inputfile = new File(targetPath, "input.txt");
        if (!inputfile.exists()) {
            try {
                inputfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.OnFFmpegOutputListener(null);
                }
                return;
            }
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(inputfile);
            for (int i = 0; i < videoList.size(); i++) {
                KLog.e("onStart:  i : " + i + " inputPath: " + videoList.get(i).getFilePath());
                out.write(("file " + "'" + videoList.get(i).getFilePath() + "'").getBytes());
                out.write("\n".getBytes());
            }
           /* for (MovieItemModel string : videoList) {
                KLog.e("onStart: inputPath i : " + string.getFilePath());
                out.write(("file " + "'" + string.getFilePath() + "'").getBytes());
                out.write("\n".getBytes());
            }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.OnFFmpegOutputListener(null);
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.OnFFmpegOutputListener(null);
            }
            return;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        final String mererVideoPath = targetPath + "/" + String.format("mererVideo-output-%s.mp4", System.currentTimeMillis() + "");

        String[] common = FFmpegCommands.composeVideo(inputfile.getAbsolutePath(), mererVideoPath);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
//                KLog.e("onStart: inputPath: " + inputfile.getAbsolutePath());
            }

            @Override
            public void onEnd(int result) {
                inputfile.delete();
                KLog.e("onEnd: outPath: " + mererVideoPath);
                KLog.e("onEnd: ----------合并视频 end------------ ");

                extractVideo(mererVideoPath);
            }
        });
    }


    /**
     * 提取视频里面的视频
     */
    private void extractVideo(final String srcPath) {
        final String outVideo = targetPath + "/" + String.format("extractVideo-output-%s.mp4", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.extractVideo(srcPath, outVideo);
        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------提取视频 start------------");
                KLog.e("onStart: inputPath: " + srcPath);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + outVideo);
                KLog.e("onEnd: ----------提取视频 end------------");

                if (videoVol == 0 && recordVol == 0 && musicVol == 0) {
                    if (listener != null) {
                        //如果是合成的视频 删除src视频
//                        if (DELETE_TEMP && (srcPath.contains("mererVideo-output-") || srcPath.contains("tstoVideo-output-"))) {
                        if ((srcPath.contains("mererVideo-output-") || srcPath.contains("tstoVideo-output-"))) {
                            FileUtils.deleteTempFilePath(srcPath);
                        }
                        listener.OnFFmpegOutputListener(outVideo);
                    }
                } else {
                    // 存在视频原音，但是音量怎么调节并不知道
                    originalVideoPath = outVideo;
                    extractAudio(srcPath);
                }

            }
        });
    }

    /**
     * 提取视频里面的音频
     */
    private void extractAudio(String AudioUrl) {
        final String outAudio = targetPath + "/" + String.format("extractAudio-output-%s.aac", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.extractAudio(AudioUrl, outAudio);
        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------提取音频 start------------");
                KLog.e("onStart: inputPath: " + AudioUrl);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + outAudio);
//                if (DELETE_TEMP)
//                    FileUtils.deleteTempFilePath(AudioUrl); // 音频提取之后就删除合成的视频
                KLog.e("onEnd: ----------提取音频 end------------");

                // 视频音量有调节，需要调节视频音量
                if (videoVol != 0) {
                    originalVolumeHandle(outAudio, videoVol);
                } else {
                    if (recordVol != 0) {//如果有录音并且录音音量不为0
                        recordVolumeHandle(recordPath, recordVol);
                    } else if (musicVol != 0) {//　如果只有音乐 并且音量不为0
                        audioClipOperation(musicPath, musicStartTime, musicSecond, musicVol);
                    } else {
                        // 音乐，录音，背景音乐都没有
                    }
                }

            }
        });
    }

    /**
     * 处理视频原音音量
     */
    private void originalVolumeHandle(final String originaPath, int originaVol) {

        final String originautUrl = targetPath + "/" + String.format("volume-output-%s.aac", System.currentTimeMillis() + "");
        final String[] common = FFmpegCommands.changeAudioOrMusicVol(originaPath, originaVol, originautUrl);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------处理原音音量 start------------");
                KLog.e("onStart: inputPath: " + originaPath + "  Vol: " + originaVol);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + originautUrl);
//                if (DELETE_TEMP)
                FileUtils.deleteTempFilePath(originaPath); // 删除处理音频之前的视频
                KLog.e("onEnd: ----------处理原音音量 end------------");
                File file = new File(originautUrl);
                long length = com.blankj.utilcode.util.FileUtils.getFileLength(file);
                if (length > 0)
                    originalBgPathList.add(originautUrl);

                if (recordVol != 0) {//如果有录音并且录音音量不为0
                    recordVolumeHandle(recordPath, recordVol);
                } else if (musicVol != 0) {//　如果只有音乐 并且音量不为0
                    audioClipOperation(musicPath, musicStartTime, musicSecond, musicVol);
                } else {
                    // 录音，音乐都没有，直接合成
                    composeVideo(originautUrl);
                }

            }
        });

    }

    /**
     * 处理录音音量
     */
    private void recordVolumeHandle(final String RecordPath, int RecordVol) {

        final String recordOutUrl = targetPath + "/" + String.format("record-output-%s.aac", System.currentTimeMillis() + "");
        final String[] common = FFmpegCommands.changeAudioOrMusicVol(RecordPath, RecordVol, recordOutUrl);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------处理录音音量 start------------");
                KLog.e("onStart: inputPath: " + RecordPath + "  Vol: " + RecordVol);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + recordOutUrl);
                KLog.e("onEnd: ----------处理录音音量 end------------");
                originalBgPathList.add(recordOutUrl);
                if (musicVol != 0) {
                    audioClipOperation(musicPath, musicStartTime, musicSecond, musicVol);
                } else {
                    composeAudio();//如果没有音乐，直接录音和视频原音合并
                }
            }
        });

    }


    /**
     * 裁剪音频
     */
    private void audioClipOperation(final String MusicPath, double startTime, double second, final int MusicVol) {
        final String audioOutUrl = targetPath + "/" + String.format("TrimAudio-output-%s.aac", System.currentTimeMillis() + "");

        String[] commands = FFmpegCommands.cutIntoMusic(MusicPath, startTime, second, audioOutUrl);
        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------裁剪音频 start ------------");
                KLog.e("onStart: inputPath: " + MusicPath + "  startTime: " + startTime + " duration: " + second);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + audioOutUrl);
                KLog.e("onEnd: ----------裁剪音频 end ------------");
                musicVolumeHandle(audioOutUrl, MusicVol);
            }
        });

    }

    /**
     * 处理音乐音量
     */
    private void musicVolumeHandle(final String MusicPath, int MusicVol) {

        final String musicOutUrl = targetPath + "/" + String.format("music-output-%s.aac", System.currentTimeMillis() + "");
        final String[] common = FFmpegCommands.changeAudioOrMusicVol(MusicPath, MusicVol, musicOutUrl);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------处理音量 start------------");
                KLog.e("onStart: inputPath: " + MusicPath + "  Vol: " + MusicVol);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + musicOutUrl);
//                if (DELETE_TEMP)
                FileUtils.deleteTempFilePath(MusicPath); // 裁剪之后，未处理音频的音频需要删除
                KLog.e("onEnd: ------------处理音量 end------------");

                if (videoDuration * 1000 <= musicSecond) {//如果视频时长比音乐剪切时长要短，直接合成
                    originalBgPathList.add(musicOutUrl);
                    composeAudio();//多个音频文件合成
                } else { // 视频时长大于音乐，音乐需要循环
                    cyclicMusic(musicOutUrl);
                }

            }
        });

    }

    /**
     * 多个音频合成
     */
    private void composeAudio() {
        final String composeAudioPath = targetPath + "/" + String.format("composeAudio-output-%s.aac", System.currentTimeMillis() + "");
        if (originalBgPathList.size() == 1) {//如果只有一个音频，不需要音乐合成，只需要音视频直接合成
            composeVideo(originalBgPathList.get(0));
        } else { // 多段音乐合成
            String[] common = FFmpegCommands.composeAudio(originalBgPathList, composeAudioPath);
            FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
                @Override
                public void onStart() {
                    KLog.e("onStart: ----------合成原声和背景音乐 start------------");
                    if (BuildConfig.DEBUG) {
                        for (int i = 0; i < originalBgPathList.size(); i++) {
                            KLog.e("onStart: : i " + i + " inputPath: " + originalBgPathList.get(i));
                        }
                    }
                }

                @Override
                public void onEnd(int result) {
                    KLog.e("onEnd: outPath: " + composeAudioPath);
                    KLog.e("onEnd: ----------合成原声和背景音乐 end------------");


                    composeVideo(composeAudioPath);
                }
            });
        }

    }

    /**
     * 视频和背景音乐合成
     *
     * @param bgMusicAndAudio
     */
    private void composeVideo(String bgMusicAndAudio) {
        final String composeVideoPath = targetPath + "/" + String.format("composeVideo-output-%s.mp4", System.currentTimeMillis() + "");

        String[] common = FFmpegCommands.composeVideoAndMusic(originalVideoPath, bgMusicAndAudio, composeVideoPath, videoDuration);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------视频和背景音乐合成 start------------");
                KLog.e("onStart: inputVideoPath: " + originalVideoPath + "  inputMusicPath: " + bgMusicAndAudio);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + composeVideoPath);
                KLog.e("onEnd: ----------视频和背景音乐合成 end------------");

//                if (DELETE_TEMP) {
//                    if (!isPicToMovie) FileUtils.deleteTempFilePath(originalVideoPath);
                FileUtils.deleteTempFilePath(bgMusicAndAudio);
                if (originalBgPathList != null && !originalBgPathList.isEmpty()) {
                    for (String path : originalBgPathList) {
                        FileUtils.deleteTempFilePath(path);
                    }
                }
//                }
                release();
                if (listener != null) {
                    listener.OnFFmpegOutputListener(composeVideoPath);
                }
            }
        });
    }

    /**
     * 循环音乐
     */
    private void cyclicMusic(String CutFile) {

        int index = (int) (videoDuration * 1000 / musicSecond);
        int Take = (int) (videoDuration * 1000 % musicSecond);
        int forCount = 0;
        if (index == 0 && Take > 0) {
            forCount = 1;
        } else if (index > 0 && Take == 0) {
            forCount = index;
        } else if (index > 0 && Take > 0) {
            forCount = index + 1;
        }

        String AudioPath = "concat:";
        for (int i = 0; i < forCount; i++) {
            AudioPath += CutFile;
            AudioPath += "|";
        }
        AudioPath = AudioPath.substring(0, AudioPath.length() - 1);
        final String cyclicMusicPath = targetPath + "/" + String.format("cyclicMusic-output-%s.aac", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.composeMoreAudio(AudioPath, cyclicMusicPath);
        String finalAudioPath = AudioPath;
        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------循环音乐 start------------");
                KLog.e("onStart: inputVideoPath: " + finalAudioPath);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + cyclicMusicPath);
//                if (DELETE_TEMP)
                FileUtils.deleteTempFilePath(CutFile); // 循环之前的音频需要删除
                KLog.e("onEnd: ----------循环音乐 end------------");
                cyclicMusicClipOperation(cyclicMusicPath); // 音乐太长，需要裁剪

            }
        });
    }


    /**
     * 循环音乐剪辑
     */
    private void cyclicMusicClipOperation(String inputfileUrl) {
        final String ouputPath = targetPath + "/" + String.format("cyclicMusicclipvideo-output-%s.mp4", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.cutIntoMusic(inputfileUrl, 0, this.videoDuration * 1000, ouputPath);
        FFmpegRun.execute(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------裁剪音频 start ------------");
                KLog.e("onStart: inputVideoPath: " + inputfileUrl);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + ouputPath);
//                if (DELETE_TEMP)
                FileUtils.deleteTempFilePath(inputfileUrl); // 太长的音乐裁剪之前的音乐需要删除
                KLog.e("onStart: ----------裁剪音频 end ------------");
                originalBgPathList.add(ouputPath);
                composeAudio();//多个音频文件合成
            }
        });
    }


    /**
     * 视频旋转
     */
    public void rotateVideo(String inputPath, int rotate, OnFFmpegOutputListener outputListener) {
        final String outputPath = targetPath + "/" + String.format("rotate-output-%s.mp4", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.videoRotationAngle(inputPath, rotate, outputPath);
        FFmpegRun.executeRx(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------转码 start ------------");
                KLog.e("onStart: inputPath: " + inputPath + " rotate: " + rotate);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + outputPath);
                KLog.e("onEnd: ----------转码 end ------------");
                outputListener.OnFFmpegOutputListener(outputPath);
            }
        });
    }

    public void addHeaderInfo(String filePath) {
        final String headerVideoPath = targetPath + "/" + String.format("addHeader-output-%s.mp4", System.currentTimeMillis() + "");
        String[] common = FFmpegCommands.addHeaderInfo(filePath, headerVideoPath);
        FFmpegRun.executeRx(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ---------- addHeaderInfo start ------------");
                KLog.e("onStart: inputPath: " + filePath);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + headerVideoPath);
                KLog.e("onEnd: ---------- addHeaderInfo end ------------");
                if (listener != null) {
                    listener.OnFFmpegOutputListener(headerVideoPath);
                }
            }
        });
    }

    public void videoClip(String inputfileUrl, String startTime, String durationTime) {
        if (inputfileUrl.endsWith(".ts")) {
            videoClipOperation2(inputfileUrl, startTime, durationTime);
        } else {
            videoClipOperation(inputfileUrl, startTime, durationTime);
        }
    }

    /**
     * 视频剪辑
     */
    private void videoClipOperation(String inputfileUrl, String startTime, String durationTime) {
        final String ouputPath = targetPath + "/" + String.format("clipvideo-output-%s.mp4", System.currentTimeMillis() + "");
        String[] commands = FFmpegCommands.cutIntoVideo(inputfileUrl, startTime, durationTime, ouputPath);
        FFmpegRun.executeRx(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------裁剪视频 start-------------");
                KLog.e("onStart: inputPath: " + inputfileUrl);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + ouputPath);
                KLog.e("onEnd: ----------裁剪视频 end------------");
                // 检测裁剪是否成功，如果失败就再次裁剪一次
                String duration = IvyUtils.getMediaDuration(ouputPath);
                if (duration != null) {
                    try {
                        long currentDuration = Long.valueOf(duration).longValue();
                        long sourceDuration = Long.valueOf(durationTime).longValue() + 1000;
                        KLog.e("onEnd:  currentDuration: " + currentDuration + " sourceDuration: " + sourceDuration);
                        if (currentDuration > sourceDuration) {// 有1s的误差，说明裁剪失败了，重新开始裁剪
                            videoClipOperation2(inputfileUrl, startTime, durationTime);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
//                TranscodingWatermarkVideo(ouputPath,waterMarkPath,waterMarkPath,waterMarkPath,"帧率：影响画面流畅度，与画面流畅度成正比：帧率越大，画面越流畅；帧率越小，画面越有跳动感。如果码率为变量，则帧率也会影",listener);
                if (listener != null) {
                    listener.OnFFmpegOutputListener(ouputPath);
                }
            }
        });
    }

    /**
     * 如果在上面视频裁剪失败，就重新解码，编码裁剪视频
     *
     * @param inputfileUrl
     * @param startTime
     * @param durationTime
     */
    private void videoClipOperation2(String inputfileUrl, String startTime, String durationTime) {
        String ouputPath;
        if (inputfileUrl.endsWith(".ts")) {
            ouputPath = targetPath + "/" + String.format("clipvideo-output-%s.ts", System.currentTimeMillis() + "");
        } else {
            ouputPath = targetPath + "/" + String.format("clipvideo-output-%s.mp4", System.currentTimeMillis() + "");
        }
        String[] commands = FFmpegCommands.cutIntoVideo2(inputfileUrl, startTime, durationTime, ouputPath);
        FFmpegRun.executeRx(commands, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ----------重新解码，编码裁剪视频 裁剪视频 start-------------");
                KLog.e("onStart: inputPath: " + inputfileUrl);
            }

            @Override
            public void onEnd(int result) {
                KLog.e("onEnd: outPath: " + ouputPath);
                KLog.e("onEnd: ----------重新解码，编码裁剪视频 裁剪视频 end------------");
//                TranscodingWatermarkVideo(ouputPath,waterMarkPath,waterMarkPath,waterMarkPath,"帧率：影响画面流畅度，与画面流畅度成正比：帧率越大，画面越流畅；帧率越小，画面越有跳动感。如果码率为变量，则帧率也会影",listener);
                if (listener != null) {
                    listener.OnFFmpegOutputListener(ouputPath);
                }
            }
        });
    }

    // ------------------------------------  视频拆分TS,在合成mp4  start -------------------------------------------

    private void toTSConcatVideo(List<MovieItemModel> videoList) {
        int length = videoList.size();
        MovieItemModel[] paths = new MovieItemModel[length];
        int maxBitrate = 0; // bitrate最大值
        for (int i = 0; i < length; i++) {
            paths[i] = videoList.get(i);
            int bitrate = IvyUtils.getVideoBitrate(videoList.get(i).getFilePath());
            videoList.get(i).setBitrate(bitrate);
            if (bitrate > maxBitrate) {
                maxBitrate = bitrate;
            }
        }
        StringBuilder builder = new StringBuilder("concat:");
        List<String> outPaths = new ArrayList<>();
        KLog.e("onStart: ---------- 拆分为TS start------------ ");
        int finalMaxBitrate = maxBitrate;
        Observable.fromArray(paths)
                .flatMap(new Function<MovieItemModel, ObservableSource<Integer>>() {
                    @Override
                    public ObservableSource<Integer> apply(MovieItemModel itemModel) throws Exception {
                        String inputPath = itemModel.getFilePath();
                        KLog.d("bitrate: " + itemModel.getBitrate() + " maxBitrate: " + finalMaxBitrate);
                        String outPath = null;
                        int result = -1;
                        if (inputPath.endsWith(".ts")) {// 旋转之前已经转ts文件了
                            outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
                            if (!FileUtils.copyTempFilePath(inputPath, outPath)) {
                                outPath = inputPath;
                            }
                        } else if (itemModel.isVideo_from()) {// 只要是本地的就直接解码，重新编码
                            outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
                            String[] common = FFmpegCommands.mp4ToTs(inputPath, outPath, true, finalMaxBitrate);// 如果是旋转过的视频，就需要解码再重新编码
                            result = FFmpegRun.run(common);
                        } else {
                            if (itemModel.getBitrate() == finalMaxBitrate) { // 如果bitrate不变化就直接转化，false都可以
                                outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
                                String[] common = FFmpegCommands.mp4ToTs(inputPath, outPath, itemModel.getRotate() != 0, finalMaxBitrate);// 如果是旋转过的视频，就需要解码再重新编码
                                result = FFmpegRun.run(common);
                                if (!IvyUtils.isOKVideo(outPath)) {// 如果视频转ts失败，就需要再次装换一次
//                                    if (DELETE_TEMP)
                                    FileUtils.deleteTempFilePath(outPath);
                                    KLog.e("onGoing: isOKVideo failed delete ts :" + outPath);
                                    outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
                                    String[] common_failed = FFmpegCommands.mp4ToTs(inputPath, outPath, true, finalMaxBitrate);
                                    result = FFmpegRun.run(common_failed);
                                }
                            } else {
                                outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
                                String[] common_failed = FFmpegCommands.mp4ToTs(inputPath, outPath, true, finalMaxBitrate);
                                result = FFmpegRun.run(common_failed);
                            }
                        }
                        KLog.e("onGoing: inputPath: " + inputPath + " outPath: " + outPath);
                        outPaths.add(outPath);
                        builder.append(outPath);
                        builder.append("|");
                        return Observable.just(result);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    KLog.e("onGoing: result: " + result);
                }, throwable -> {
                    if (listener != null) {
                        listener.OnFFmpegOutputListener(null);
                    }
                }, () -> {
                    KLog.e("onEnd: ---------- 拆分为TS end------------ ");
                    builder.deleteCharAt(builder.length() - 1);
                    concatTsVideoToMp4(builder.toString(), outPaths, finalMaxBitrate);
                });
    }

    /* private Disposable mp4ToTS(String inputPath) {
         final String outPath = targetPath + "/" + String.format("mp4tots-output-%s.ts", System.currentTimeMillis() + "");
         String[] common = FFmpegCommands.mp4ToTs(inputPath, outPath);
         return FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
             @Override
             public void onStart() {
                 KLog.e("onStart: ---------- 拆分为TS start------------ ");
                 KLog.e("onStart: inputPath: " + inputPath);
             }

             @Override
             public void onEnd(int result) {
                 KLog.e("onEnd: outPath: " + outPath);
                 KLog.e("onEnd: ---------- 拆分为TS end------------ ");
             }
         });
     }
 */
    private void concatTsVideoToMp4(String inputPath, List<String> outPaths, int MaxBitrate) {
        final String mererVideoPath = targetPath + "/" + String.format("tstoVideo-output-%s.mp4", System.currentTimeMillis() + "");
        String[] common = FFmpegCommands.concatTsVideoToMp4(inputPath, mererVideoPath, MaxBitrate);
        FFmpegRun.execute(common, new FFmpegRun.FFmpegRunListener() {
            @Override
            public void onStart() {
                KLog.e("onStart: ---------- TS合成MP4 start------------ ");
                KLog.e("onStart: inputPath: " + inputPath);
            }

            @Override
            public void onEnd(int result) {
                if (outPaths != null && !outPaths.isEmpty()) {
//                    if (DELETE_TEMP) {
                    for (String ts : outPaths) {
                        FileUtils.deleteTempFilePath(ts);
                        KLog.e("onGoing: delete ts :" + ts);
                    }
//                    }
                    outPaths.clear();
                }

                KLog.e("onEnd: outPath: " + mererVideoPath);
                KLog.e("onEnd: ---------- TS合成MP4 end------------ ");
                extractVideo(mererVideoPath);
            }
        });
    }

    // ------------------------------------  视频拆分TS,在合成mp4  end  -------------------------------------------


    public FFmpegUtils setOnFFmpegOutputListener(OnFFmpegOutputListener listener) {
        this.listener = listener;
        return this;
    }

    public interface OnFFmpegOutputListener {
        void OnFFmpegOutputListener(String outputFilePath);
    }
}
