package com.tangyx.video.ffmpeg;

import com.wwwjf.base.KLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tangyx
 * Date 2017/8/4
 * email tangyx@live.com
 */

public class FFmpegCommands {

    /**
     * 裁剪视频
     *
     * @param videoUrl
     * @param outUrl
     * @return
     */
    public static String[] cutIntoVideo(String videoUrl, String startTime, String durationTime, String outUrl) {
        KLog.d("cutIntoVideo: videoUrl " + videoUrl + " startTime " + startTime + " durationTime " + durationTime + " outUrl " + outUrl);

        String[] commands = new String[11];
        String start = generateTime(Double.valueOf(startTime));
        String time = generateTime(Double.valueOf(durationTime));

        KLog.d("cutIntoVideo: start " + start + " time " + time);

        commands[0] = "ffmpeg";
        commands[1] = "-ss";
        commands[2] = start;
        commands[3] = "-t";
        commands[4] = time;
        commands[5] = "-accurate_seek";
        commands[6] = "-i";
        commands[7] = videoUrl;
        commands[8] = "-codec";
        commands[9] = "copy";
        commands[10] = outUrl;

//        commands[0] = "ffmpeg";
//        commands[1] = "-ss";
//        commands[2] = start;
//        commands[3] = "-t";
//        commands[4] = time;
//        commands[5] = "-i";
//        commands[6] = videoUrl;
//        commands[7] = "-vcodec";
//        commands[8] = "copy";
//        commands[9] = "-acodec";
//        commands[10] = "copy";
//        commands[11] = outUrl;

        return commands;
    }

    /**
     * 裁剪视频( 重新编解码的方式)  针对ts视频裁剪也是使用此命令
     *
     * @param videoUrl
     * @param outUrl
     * @return
     */
    public static String[] cutIntoVideo2(String videoUrl, String startTime, String durationTime, String outUrl) {
        KLog.d("cutIntoVideo: videoUrl " + videoUrl + " startTime " + startTime + " durationTime " + durationTime + " outUrl " + outUrl);

        String[] commands = new String[12];
        String start = generateTime(Double.valueOf(startTime));
        String time = generateTime(Double.valueOf(durationTime));

        KLog.d("cutIntoVideo: start " + start + " time " + time);

        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = videoUrl;
        commands[3] = "-ss";
        commands[4] = start;
        commands[5] = "-t";
        commands[6] = time;
        commands[7] = "-c:v";
        commands[8] = "libx264";
        commands[9] = "-c:a";
        commands[10] = "aac";
        commands[11] = outUrl;

        return commands;
    }

    /**
     * 视频合成
     *
     * @param inputfileUrl
     * @param outUrl
     * @return
     */
    public static String[] composeVideo(String inputfileUrl, String outUrl) {
        String[] commands = new String[10];
        commands[0] = "ffmpeg";
        commands[1] = "-f";
        commands[2] = "concat";
        commands[3] = "-safe";
        commands[4] = "0";
        commands[5] = "-i";
        commands[6] = inputfileUrl;

        // 失效
//        commands[7] = "-s";
//        commands[8] = "720x1280";

        commands[7] = "-c";
        commands[8] = "copy";
        commands[9] = outUrl;

        return commands;
    }

    // ffmpeg -i MVI_7274.MOV -vcodec libx264 -preset fast -crf 20 -y -vf "scale=1920:-1" -acodec libmp3lame -ab 128k a.mp4
    public static String[] addHeaderInfo(String inputfileUrl, String outUrl) {
        String[] commands = new String[8];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = inputfileUrl;

    /*    commands[3] = "-vf";
        commands[4] = "scale=960:540";*/

     /*   commands[5] = "-qscale";
        commands[6] = "4";*/

        // -profile:v high -level 5.1
      /*  commands[5] = "-profile:v";
        commands[6] = "high";
        commands[5] = "-level";
        commands[6] = "4.1";*/

        // -preset:v slow  crf=18
//        commands[5] = "-preset";
//        commands[6] = "slow";
//        commands[5] = "-crf";
//        commands[5] = "20";


//        commands[3] = "-vcodec";
//        commands[4] = "libx264";
//        commands[5] = "-crf";
//        commands[6] = "20";
////        commands[7] = "-vf";
////        commands[8] = "scale=960:540";
//        commands[7] = "-movflags";
//        commands[8] = "faststart";
//        commands[9] = "-acodec";
//        commands[10] = "copy";
//        commands[11] = outUrl;


        commands[3] = "-c";
        commands[4] = "copy";
        commands[5] = "-movflags";
        commands[6] = "faststart";
        commands[7] = outUrl;

        return commands;
    }

    /**
     * 提取单独的音频
     *
     * @param videoUrl
     * @param outUrl
     * @return
     */
    public static String[] extractAudio(String videoUrl, String outUrl) {
        String[] commands = new String[8];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = videoUrl;
        commands[3] = "-vn";
        commands[4] = "-y";
        commands[5] = "-acodec";
        commands[6] = "copy";

        commands[7] = outUrl;
        return commands;
    }

    /**
     * 提取单独的视频，没有声音
     *
     * @param videoUrl
     * @param outUrl
     * @return
     */
    public static String[] extractVideo(String videoUrl, String outUrl) {
        String[] commands = new String[8];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = videoUrl;
        commands[3] = "-vcodec";
        commands[4] = "copy";
        commands[5] = "-an";
        commands[6] = "-y";
        commands[7] = outUrl;
        return commands;
    }

    /**
     * 裁剪音频
     */
    public static String[] cutIntoMusic(String musicUrl, double startTime, double second, String outUrl) {

        String start = generateTime(startTime);
        String time = generateTime(second);
        KLog.e("SLog", start + "---" + time + "---" + outUrl);
        String[] commands = new String[10];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = musicUrl;
        commands[3] = "-ss";
        commands[4] = start;
        commands[5] = "-t";
        commands[6] = time;
        commands[7] = "-acodec";
        commands[8] = "copy";
        commands[9] = outUrl;
        return commands;
    }

    /**
     * 音频合并
     *
     * @param audioList
     * @param outputUrl
     * @return
     */
    public static String[] composeAudio(List<String> audioList, String outputUrl) {

        ArrayList<String> _commands = new ArrayList<>();
        _commands.add("ffmpeg");
        for (int i = 0; i < audioList.size(); i++) {
            _commands.add("-i");
            _commands.add(audioList.get(i));
        }
        _commands.add("-filter_complex");
        _commands.add("amix=inputs=" + audioList.size() + ":duration=longest:dropout_transition=2");
        _commands.add("-strict");
        _commands.add("-2");
        _commands.add(outputUrl);
        String[] commands = new String[_commands.size()];
        for (int i = 0; i < _commands.size(); i++) {
            commands[i] = _commands.get(i);
        }
        return commands;
    }


//    /**
//     * 修改音频文件的音量
//     *
//     * @param audioOrMusicUrl
//     * @param vol
//     * @param outUrl
//     * @return
//     */
//    public static String[] changeAudioOrMusicVol(String audioOrMusicUrl, int vol, String outUrl) {
//        KLog.d("SLog", "audioOrMusicUrl:" + audioOrMusicUrl + "\nvol:" + vol + "\noutUrl:" + outUrl);
//        String[] commands = new String[6];
//        commands[0] = "ffmpeg";
//        commands[1] = "-i";
//        commands[2] = audioOrMusicUrl;
//        commands[3] = "-vol";
//        commands[4] = String.valueOf(vol);
////        commands[5] = "-acodec";
////        commands[6] = "copy";
//        commands[5] = outUrl;
//        return commands;
//    }

    /**
     * 修改音频文件的音量
     *
     * @param audioOrMusicUrl
     * @param vol
     * @param outUrl
     * @return
     */
    public static String[] changeAudioOrMusicVol(String audioOrMusicUrl, int vol, String outUrl) {
        KLog.d("SLog", "audioOrMusicUrl:" + audioOrMusicUrl + "\n0:" + vol + "\noutUrl:" + outUrl);
        String[] commands = new String[6];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = audioOrMusicUrl;
        commands[3] = "-af";
        commands[4] = "volume=" + (vol - 50) * 1f / 5 + "dB";
//        commands[5] = "-acodec";
//        commands[6] = "copy";
        commands[5] = outUrl;
        return commands;
    }


    /**
     * 多音频合并
     *
     * @param audio1
     * @param outputUrl
     * @return
     */
    public static String[] composeMoreAudio(String audio1, String outputUrl) {
        KLog.d("SLog", "audio1:" + audio1 + "\noutputUrl:" + outputUrl);
        String[] commands = new String[6];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-i";
        commands[2] = audio1;

        //覆盖输出
        commands[3] = "-acodec";
        commands[4] = "copy";

        commands[5] = outputUrl;
        return commands;
    }

    /**
     * 音频，视频合成
     *
     * @param videoUrl
     * @param musicOrAudio
     * @param outputUrl
     * @param second
     * @return
     */
    public static String[] composeVideoAndMusic(String videoUrl, String musicOrAudio, String outputUrl, double second) {
        KLog.d("SLog", "videoUrl:" + videoUrl + "\nmusicOrAudio:" + musicOrAudio + "\noutputUrl:" + outputUrl + "\nsecond:" + second);
        String[] commands = new String[14];
        commands[0] = "ffmpeg";
        commands[1] = "-ss";
        commands[2] = "0";
        commands[3] = "-t";
        commands[4] = String.valueOf(second);

        //输入
        commands[5] = "-i";
        commands[6] = videoUrl;
        //音乐
        commands[7] = "-i";
        commands[8] = musicOrAudio;

        //覆盖输出
        commands[9] = "-vcodec";
        commands[10] = "copy";
        commands[11] = "-acodec";
        commands[12] = "copy";
        //输出文件
        commands[13] = outputUrl;
        return commands;
    }

    /**
     * mp4转ts
     *
     * @param videoUrl
     * @param outPath
     * @return
     */
    public static String[] mp4ToTs(String videoUrl, String outPath, boolean CanDecode, int maxBitrate) {
        KLog.d("SLog", "videoUrl:" + videoUrl + "\noutPath:" + outPath + " \n CanDecode " + CanDecode+ "  maxBitrate " + maxBitrate);
        String[] commands = new String[12];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = videoUrl;
//        commands[3] = "-c";
//        commands[4] = "copy";
//        commands[5] = "-bsf:v";
//        commands[6] = "h264_mp4toannexb";
//        commands[7] = "-f";
//        commands[8] = "mpegts";
//        commands[9] = outPath;
      /*  if(flip){
            _commands.add("-vf");
            //hflip左右翻转，vflip上下翻转
            _commands.add("hflip");
        }*/
//        -preset：指定编码的配置。x264编码算法有很多可供配置的参数，不同的参数值会导致编码的速度大相径庭，甚至可能影响质量。为了免去用户了解算法，
// 然后手工配置参数的麻烦。x264提供了一些预设值，而这些预设值可以通过preset指定。这些预设值有包括：
// ultrafast，superfast，veryfast，faster，fast，medium，slow，slower，veryslow和placebo。ultrafast编码速度最快，但压缩率低，生成的文件更大，placebo则正好相反。x264所取的默认值为medium。需要说明的是，preset主要是影响编码的速度，并不会很大的影响编码出来的结果的质量。压缩高清电影时，我一般用slow或者slower，当你的机器性能很好时也可以使用veryslow，不过一般并不会带来很大的好处。
        commands[3] = "-preset";
        commands[4] = "ultrafast";
//        -crf：这是最重要的一个选项，用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18，1080p我没尝试过。个人觉得，一般情况下没有必要低于16。最好的办法是大家可以多尝试几个值，每个都压几分钟，看看最后的输出质量和文件大小，自己再按需选择。
//        commands[5] = "-crf";
//        commands[6] = "20";

        commands[5] = "-b:v";
        commands[6] = String.valueOf(maxBitrate == 0 ? 2 * 1024 * 1024 : maxBitrate);//2097152
//        commands[7] = "-s";
//        commands[8] = "720x1280";
        // 可能会异常，导致界面不出来，所以直接指定解码器直接解码，虽然耗时，但是有效
        // 旋转后的视频也需要此处理
        if (CanDecode) {
            commands[7] = "-c:v";
            commands[8] = "libx264";
            commands[9] = "-c:a";
            commands[10] = "aac";
            commands[11] = outPath;
        } else {
            commands[7] = "-acodec";
            commands[8] = "copy";
            commands[9] = "-vcodec";
            commands[10] = "copy";
            commands[11] = outPath;
        }
        return commands;
    }

    /**
     * ts拼接视频
     */
    // ffmpeg -i "concat:input1.ts|input2.ts|input3.ts" -c copy -bsf:a aac_adtstoasc -movflags +faststart output.mp4
// [ffmpeg, -i, "concat:/storage/emulated/0/xh_recorder/synth/mp4tots-output-1552114137120.ts|/storage/emulated/0/xh_recorder/synth/mp4tots-output-1552114137461.ts", -c, copy, -bsf:a, aac_adtstoasc, /storage/emulated/0/xh_recorder/synth/tstovd-output-1552114137498.mp4]
    public static String[] concatTsVideoToMp4(String concat, String _outPath, int MaxBitrate) {//-f concat -i list.txt -c copy concat.mp4
        KLog.d("SLog", "concat:" + concat + "\n_outPath:" + _outPath+ "  MaxBitrate:" + MaxBitrate);
        String[] commands = new String[12];
        commands[0] = "ffmpeg";
        commands[1] = "-i";
        commands[2] = concat;
//        commands[3] = "-c";
//        commands[4] = "copy";
//        commands[5] = "-bsf:a";
//        commands[6] = "aac_adtstoasc";
//        commands[7] = _outPath;

        commands[3] = "-b:v";
        commands[4] = String.valueOf(MaxBitrate==0?2 * 1024 * 1024:MaxBitrate);//2097152
        commands[5] = "-s";
        commands[6] = "720x1280";
        commands[7] = "-acodec";
        commands[8] = "copy";
        commands[9] = "-vcodec";
        commands[10] = "copy";
        commands[11] = _outPath;

        return commands;
    }

    /**
     * 使用ffmpeg命令行给视频添加水印
     *
     * @param srcFile   源文件
     * @param waterMark 水印文件路径
     * @return 添加水印后的文件
     */
    public static String[] addImageMark(String srcFile, String waterMark, String imageX, String imageY, String outputUrl) {
        KLog.d("SLog", "_filePath:" + srcFile + "\nwaterMark:" + waterMark + "\noutPath:" + outputUrl);


        String[] commands = new String[9];
        commands[0] = "ffmpeg";
        commands[1] = "-y";
        //输入
        commands[2] = "-i";
        commands[3] = srcFile;
        //音乐
        commands[4] = "-i";
        commands[5] = waterMark;
        commands[6] = "-filter_complex";
        commands[7] = "overlay=(W-w)/2:(H-h)/2";


        commands[8] = outputUrl;
        return commands;
    }

    /**
     * 使用ffmpeg命令行给视频添加水印
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件
     * @return 添加水印后的文件
     */
    public static String[] addWaterShow2SMark(String srcFile, String outputUrl) {
        KLog.w("SLog", "_filePath:" + srcFile + "\noutPath:" + outputUrl);
//        ffmpeg -y -i jiushu.mpg -acodec libfaac -b:a 30k -ar 44100 -r 15 -ac 2 -s 480x272 -vcodec libx264 -refs 2 -x264opts keyint=150:min-keyint=15 -vprofile baseline -level 20 -b:v 200k -vf "drawtext=fontfile=/mnt/hgfs/zm/simhei.ttf: text='来源：迅雷':x=100:y=x/dar:draw='if(gt(n,0),lt(n,250))':fontsize=24:fontcolor=yellow@0.5:shadowy=2"  drawtext.mp4
        String[] commands = new String[31];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-y";
        commands[2] = "-i";
        commands[3] = srcFile;
        //音乐
        commands[4] = "-acodec";
        commands[5] = "libfaac";
        commands[6] = "-b:a";
        commands[7] = "30k";

        commands[8] = "-ar";
        commands[9] = "44100";

        commands[10] = "-r";
        commands[11] = "15";

        commands[12] = "-ac";
        commands[13] = "2";

        commands[14] = "-s";
        commands[15] = "480x27";

        commands[16] = "-vcodec";
        commands[17] = "libx264";
        //输出文件
        commands[18] = "-refs";
        commands[19] = "2";


        commands[20] = "x264opts";
        commands[21] = "keyint=150:min-keyint=15";
        commands[22] = "-vprofile";
        commands[23] = "baseline";
        commands[24] = "-level";
        commands[25] = "20";
        commands[26] = "-b:v";
        commands[27] = "200k";
        commands[28] = "-vf";
        commands[29] = "drawtext=fontfile=arial.ttf:text='来源：迅雷':x=100:y=x/dar:draw='if(gt(n,0),lt(n,250))':fontsize=24:fontcolor=yellow@0.5:shadowy=2";

        commands[30] = outputUrl;
        return commands;
    }

    /**
     * 使用ffmpeg命令行进行转压视频
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] transformVideo(String srcFile, String outputUrl) {
        //指定目标视频的帧率、码率、分辨率
//        KLog.d("SLog", "_filePath:" + srcFile + "\noutPath:" + outputUrl);
        String[] commands = new String[18];
        commands[0] = "-threads";
        commands[1] = "3";
        commands[2] = "-i";
        commands[3] = srcFile;
        commands[4] = "-vcodec";
        commands[5] = "libx264";
//        -preset：指定编码的配置。x264编码算法有很多可供配置的参数，不同的参数值会导致编码的速度大相径庭，甚至可能影响质量。为了免去用户了解算法，然后手工配置参数的麻烦。x264提供了一些预设值，而这些预设值可以通过preset指定。这些预设值有包括：ultrafast，superfast，veryfast，faster，fast，medium，slow，slower，veryslow和placebo。ultrafast编码速度最快，但压缩率低，生成的文件更大，placebo则正好相反。x264所取的默认值为medium。需要说明的是，preset主要是影响编码的速度，并不会很大的影响编码出来的结果的质量。压缩高清电影时，我一般用slow或者slower，当你的机器性能很好时也可以使用veryslow，不过一般并不会带来很大的好处。
        commands[6] = "-preset";
        commands[7] = "fast";
//        -crf：这是最重要的一个选项，用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18，1080p我没尝试过。个人觉得，一般情况下没有必要低于16。最好的办法是大家可以多尝试几个值，每个都压几分钟，看看最后的输出质量和文件大小，自己再按需选择。
        commands[8] = "-crf";
        commands[9] = "20";
        commands[10] = "-y";
        commands[11] = "-vf";
        commands[12] = "scale=960:540";
        commands[13] = "-acodec";
        commands[14] = "copy";
        commands[15] = "-ab";
        commands[16] = "128k";

        //输出文件
        commands[17] = outputUrl;
        return commands;


    }

    /**
     * 使用ffmpeg旋转民视频
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] videoRotationAngle(String srcFile, int rotate, String outputUrl) {
        //指定目标视频的帧率、码率、分辨率

        //ffmpeg -y -i /Users/heqi/Desktop/Beyond.mp4  -vf transpose=2  -b 1024000 -s 320x180 -acodec copy -metadata:s:v:0 rotate=90 wxy_M2_cp.mp4
//        String transpose;
//        if (rotate/90 == 1){
//            transpose = "transpose=1";
//        } else if(rotate/90== 2){
//            transpose = "transpose=2";
//        } else {
//            transpose = "transpose=3";
//        }
        KLog.d("SLog", "_filePath:" + srcFile + "\noutPath:" + outputUrl);
        String tmRotate = "rotate=" + (rotate + 90);
        String[] commands = new String[11];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-y";
        commands[2] = "-i";
        commands[3] = srcFile;
////        //音乐
//        commands[4] = "-vf";
//        commands[5] = transpose;
//音乐
//        commands[5] = "-b";
//        commands[6] = "1024000";
//        commands[4] = "-profile:v";
//        commands[5] = "baseline";
//        commands[6] = "-level";
//        commands[7] = "3.0";
        commands[4] = "-g";
        commands[5] = "1";
        commands[6] = "-c";
        commands[7] = "copy";
        //-b:v 10M 表示视频码率为10Mbps   -b:a 128K 表示音频码率为 128Kbps，
        commands[8] = "-metadata:s:v";
        commands[9] = tmRotate;

        //输出文件7
        commands[10] = outputUrl;
        return commands;
    }

    /**
     * 使用ffmpeg转码
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] compressClipVideo(String srcFile, int width, int height, int x, int y, String outputUrl) {
        //指定目标视频的帧率、码率、分辨率

        //ffmpeg -threads 4 -y -i /Users/heqi/Desktop/wxy_M2_cp.mp4 -metadata:s:v rotate="0" -vf transpose=1,crop=220:220:50:150 -r 25 -acodec copy /Users/heqi/Desktop/wxy.mp4

        KLog.d("SLog", "_filePath:" + srcFile + "\noutPath:" + outputUrl + "\nwidth:" + width + "\nheight:" + height + "\nx:" + x + "\ny:" + y);

        String[] commands = new String[17];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-threads";
        commands[2] = "4";
        commands[3] = "-y";
        commands[4] = "-i";
        commands[5] = srcFile;
//        //音乐
        commands[6] = "-metadata:s:v";
        commands[7] = "rotate=\"0\"";
//音乐
//        commands[5] = "-b";
//        commands[6] = "1024000";

        commands[8] = "-vf";
        commands[9] = "crop=" + width + ":" + height + ":" + x + ":" + y;


        commands[10] = "-r";
        commands[11] = "25";
        //-b:v 10M 表示视频码率为10Mbps   -b:a 128K 表示音频码率为 128Kbps，
        commands[12] = "-b:v";
        commands[13] = "10M";
        commands[14] = "-acodec";
        commands[15] = "copy";

        //输出文件
        commands[16] = outputUrl;
        return commands;
    }

    /**
     * 使用ffmpeg剪切矩形范围
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] clipVideo(String srcFile, int width, int height, int x, int y, String outputUrl, String time) {
        //指定目标视频的帧率、码率、分辨率

        //ffmpeg -threads 4 -y -i /Users/heqi/Desktop/wxy_M2_cp.mp4 -metadata:s:v rotate="0" -vf transpose=1,crop=220:220:50:150 -r 25 -acodec copy /Users/heqi/Desktop/wxy.mp4

        KLog.d("SLog", "_filePath:" + srcFile + "\noutPath:" + outputUrl + "\nwidth:" + width + "\nheight:" + height + "\nx:" + x + "\ny:" + y);

        String[] commands = new String[15];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-y";
        commands[2] = "-i";
        commands[3] = srcFile;
        commands[4] = "-ss";
        commands[5] = "0";
        commands[6] = "-t";
        commands[7] = time;
        commands[8] = "-strict";
        commands[9] = "-2";
        commands[10] = "-vf";
        commands[11] = "crop=" + width + ":" + height + ":" + x + ":" + y;

        commands[12] = "-preset";
        commands[13] = "fast";
        //输出文件
        commands[14] = outputUrl;

        String str = Arrays.toString(commands);
        KLog.e("clipVideo", str.replace(" ", "\n"));
        return commands;
    }

    /**
     * 添加动态水印显示3秒
     *
     * @param srcFile   源文件
     * @param outputUrl 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] addDynamicWatermark(String srcFile, int time, String waterMarkPath, String outputUrl) {
        //指定目标视频的帧率、码率、分辨率

        //ffmpeg -threads 4 -y -i /Users/heqi/Desktop/wxy_M2_cp.mp4 -metadata:s:v rotate="0" -vf transpose=1,crop=220:220:50:150 -r 25 -acodec copy /Users/heqi/Desktop/wxy.mp4


        String[] commands = new String[11];
        commands[0] = "ffmpeg";
        //输入
        commands[1] = "-y";
        commands[2] = "-t";
        commands[3] = String.valueOf(time);
        commands[4] = "-i";
        commands[5] = srcFile;
        commands[6] = "-i";
        commands[7] = waterMarkPath;
        commands[8] = "-filter_complex";
        commands[9] = "overlay=x=if(lt(t\\,5)\\,(W-200)/2\\,-200 ):y=(H-200)/2";

        //输出文件
        commands[10] = outputUrl;
        String str = Arrays.toString(commands);
        return commands;
    }

    /**
     * 添加跑马灯
     *
     * @param srcFile    源文件
     * @param fontsPath  字体路径 加文字水印时必须
     * @param WMPath     右上角水印路径
     * @param DCPath     导演演员路径
     * @param INFOPath   位置和地理信息图片路径
     * @param edDescribe 跑马灯信息
     * @param outputUrl  输出文件
     * @return 转码后的文件
     */
    public static String[] addEntertainingDiversionsWatermark(String srcFile, String fontsPath, String WMPath, String DCPath, String INFOPath, String edDescribe, String outputUrl) {
        //指定目标视频的帧率、码率、分辨

        //右上角水印
        String waterMark = "[0:v][1:v]overlay=W-w-20:20[wm];";

        //导演演员
        String directorCast = "[wm][2:v]overlay=x=if(lt(t\\,5)\\,(W-w)/2\\,-w):y=(H-h)/2[dc];";

        //位置信息
//        String info = "[dc]drawtext=text='"+location+"':fontfile="+fontsPath+":fontcolor=white:fontsize=30:shadowx=2:shadowy=2:y=h-line_h-50:x=15[info];";
        String info = "[dc][3:v]overlay=15:H-h-50[info];";
        //描述跑马灯
//        String describe = "[info]drawtext=text='"+edDescribe+"':expansion=normal:fontfile="+fontsPath+":fontcolor=white:fontsize=35:shadowx=2:shadowy=2:y=h-line_h-10:x=w-(t-1)*w/15";

        String describe = "[info]drawtext=text='" + edDescribe + "':expansion=normal:fontfile=" + fontsPath + ":fontcolor=white:fontsize=35:shadowx=2:shadowy=2:y=h-line_h-10:x=w-mod(5*n\\,w+tw)";
        String strDrawtext = waterMark + directorCast + info + describe;

        String[] commands = new String[13];
        commands[0] = "-i";
        commands[1] = srcFile;   //原视频地址
        commands[2] = "-i";
        commands[3] = WMPath; //右上角水印
        commands[4] = "-i";
        commands[5] = DCPath; //导演
        commands[6] = "-i";
        commands[7] = INFOPath; //标题和地理位置
        commands[8] = "-filter_complex";
        commands[9] = strDrawtext;
        commands[10] = "-codec:a";  //音频选项,后加copy表示音频拷贝
        commands[11] = "copy";  //表示音频拷贝,无需处理音频时添加，可节省加水印时间
        commands[12] = outputUrl; //水印添加完成后的视频文件地址


        return commands;
    }


    private static String generateTime(double time) {

        int totalSeconds = (int) (time / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        int milliSeconds = (int) time % 1000;
        milliSeconds = milliSeconds / 100;
        return hours > 0 ? String.format("%d:%d:%d.%d", hours, minutes, seconds, milliSeconds) : String.format("%d:%d:%d.%d", hours, minutes, seconds, milliSeconds);
    }

}
