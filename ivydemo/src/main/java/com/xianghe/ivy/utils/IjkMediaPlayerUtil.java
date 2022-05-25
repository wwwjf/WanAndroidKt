package com.xianghe.ivy.utils;


import android.content.Context;


import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class IjkMediaPlayerUtil {
    public static String infoToString(int info) {
        switch (info) {
            case IMediaPlayer.MEDIA_INFO_UNKNOWN:               // 1
                return "MEDIA_INFO_UNKNOWN(1)";
            case IMediaPlayer.MEDIA_INFO_STARTED_AS_NEXT:       // 2
                return "MEDIA_INFO_STARTED_AS_NEXT(2)";
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: // 3
                return "MEDIA_INFO_VIDEO_RENDERING_START(3)";
            case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:   // 700
                return "MEDIA_INFO_VIDEO_TRACK_LAGGING(700)";
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:       // 701
                return "MEDIA_INFO_BUFFERING_START(701)";
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:         // 702
                return "MEDIA_INFO_BUFFERING_END(702)";
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:     // 703
                return "MEDIA_INFO_NETWORK_BANDWIDTH(703)";
            case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:      // 800
                return "MEDIA_INFO_BAD_INTERLEAVING(800)";
            case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:          // 801
                return "MEDIA_INFO_NOT_SEEKABLE(801)";
            case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:       // 802
                return "MEDIA_INFO_METADATA_UPDATE(802)";
            case IMediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR:      // 900
                return "MEDIA_INFO_TIMED_TEXT_ERROR(900)";
            case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:  // 901
                return "MEDIA_INFO_UNSUPPORTED_SUBTITLE(901)";
            case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:    // 902
                return "MEDIA_INFO_SUBTITLE_TIMED_OUT(902)";
            case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:        // 10001
                return "MEDIA_INFO_VIDEO_ROTATION_CHANGED(10001)";
            case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:         // 10002
                return "MEDIA_INFO_AUDIO_RENDERING_START(10002)";
            case IMediaPlayer.MEDIA_INFO_AUDIO_DECODED_START:           // 10003
                return "MEDIA_INFO_AUDIO_DECODED_START(10003)";
            case IMediaPlayer.MEDIA_INFO_VIDEO_DECODED_START:           // 10004
                return "MEDIA_INFO_VIDEO_DECODED_START(10004)";
            case IMediaPlayer.MEDIA_INFO_OPEN_INPUT:                    // 10005
                return "MEDIA_INFO_OPEN_INPUT(10005)";
            case IMediaPlayer.MEDIA_INFO_FIND_STREAM_INFO:              // 10006
                return "MEDIA_INFO_FIND_STREAM_INFO(10006)";
            case IMediaPlayer.MEDIA_INFO_COMPONENT_OPEN:                // 10007
                return "MEDIA_INFO_COMPONENT_OPEN(10007)";
            case IMediaPlayer.MEDIA_INFO_VIDEO_SEEK_RENDERING_START:    // 10008
                return "MEDIA_INFO_VIDEO_SEEK_RENDERING_START(10008)";
            case IMediaPlayer.MEDIA_INFO_AUDIO_SEEK_RENDERING_START:    // 10009
                return "MEDIA_INFO_AUDIO_SEEK_RENDERING_START(10009)";
            case IMediaPlayer.MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE:  // 10100
                return "MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE(10100)";
            default:
                return String.valueOf(info);
        }
    }

    public static String errorToString(int error) {
        switch (error) {
            case IMediaPlayer.MEDIA_ERROR_UNKNOWN:      // 1
                return "MEDIA_ERROR_UNKNOWN";
            case IMediaPlayer.MEDIA_ERROR_SERVER_DIED:  // 100
                return "MEDIA_ERROR_SERVER_DIED";
            case IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:   // 200
                return "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK";
            case IMediaPlayer.MEDIA_ERROR_IO:           // -1004
                return "MEDIA_ERROR_IO";
            case IMediaPlayer.MEDIA_ERROR_MALFORMED:    // -1007
                return "MEDIA_ERROR_MALFORMED";
            case IMediaPlayer.MEDIA_ERROR_UNSUPPORTED:  // -1010
                return "MEDIA_ERROR_UNSUPPORTED";
            case IMediaPlayer.MEDIA_ERROR_TIMED_OUT:    // -110
                return "MEDIA_ERROR_TIMED_OUT";
            default:
                return String.valueOf(error);
        }
    }

    public static String mediaInfoToString(MediaInfo mediaInfo) {
        if (mediaInfo == null) {
            return null;
        }

        return new StringBuffer()
                .append('\t').append("media player name : ").append(mediaInfo.mMediaPlayerName).append('\n')
                .append('\t').append("video decoder : ").append(mediaInfo.mVideoDecoder).append('\n')
                .append('\t').append("video decoder impl : ").append(mediaInfo.mVideoDecoderImpl).append('\n')
                .append('\t').append("audio decoder : ").append(mediaInfo.mAudioDecoder).append('\n')
                .append('\t').append("audio decoder impl : ").append(mediaInfo.mAudioDecoderImpl).append('\n')
                .append('\t').append("meta : ").append(mediaInfo.mMeta).append('\n')
                .toString();

    }

    public static String trackInfosToString(ITrackInfo[] trackInfos) {
        if (trackInfos == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer("[\n");
        for (ITrackInfo trackInfo : trackInfos) {
            sb.append("\t{\n")
                    .append('\t').append('\t').append("format: ").append(trackInfo.getFormat()).append('\n')
                    .append('\t').append('\t').append("language: ").append(trackInfo.getLanguage()).append('\n')
                    .append('\t').append('\t').append("track type: ").append(trackType2String(trackInfo.getTrackType())).append('\n')
                    .append('\t').append('\t').append("info inline: ").append(trackInfo.getInfoInline()).append('\n')
                    .append("\t}").append('\n');
        }
        return sb.append("]").toString();
    }

    public static String trackType2String(int trackType) {
        switch (trackType) {
            case ITrackInfo.MEDIA_TRACK_TYPE_UNKNOWN:
                return "MEDIA_TRACK_TYPE_UNKNOWN";

            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                return "MEDIA_TRACK_TYPE_VIDEO";

            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                return "MEDIA_TRACK_TYPE_AUDIO";

            case ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT:
                return "MEDIA_TRACK_TYPE_TIMEDTEXT";

            case ITrackInfo.MEDIA_TRACK_TYPE_SUBTITLE:
                return "MEDIA_TRACK_TYPE_SUBTITLE";

            case ITrackInfo.MEDIA_TRACK_TYPE_METADATA:
                return "MEDIA_TRACK_TYPE_METADATA";
        }
        return String.valueOf(trackType);
    }
}
