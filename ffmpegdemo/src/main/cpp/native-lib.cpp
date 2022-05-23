#include <jni.h>
#include <string>
#include <unistd.h>
#include "media/player/def_player/player.h"
#include "media/player/gl_player/gl_player.h"
#include "media/muxer/ff_repack.h"
#include "media/synthesizer/synthesizer.h"
extern "C"
JNIEXPORT
jstring JNICALL
Java_com_wwwjf_ffmpegdemo_FFmpegDemoActivity_helloFromJNI(JNIEnv *env,jobject){
    std::string hello = "hello from jni";
    return env->NewStringUTF(hello.c_str());
}


extern "C"{

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/jni.h>
JNIEXPORT jstring JNICALL
Java_com_wwwjf_ffmpegdemo_FFmpegDemoActivity_ffmpegInfo(JNIEnv *env, jobject  /* this */) {

    char info[40000] = {0};
    const char *c_temp = avcodec_configuration();
    /*while (c_temp != nullptr) {
        if (c_temp->decode != NULL) {
            sprintf(info, "%sdecode:", info);
        } else {
            sprintf(info, "%sencode:", info);
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                sprintf(info, "%s(video):", info);
                break;
            case AVMEDIA_TYPE_AUDIO:
                sprintf(info, "%s(audio):", info);
                break;
            default:
                sprintf(info, "%s(other):", info);
                break;
        }
        sprintf(info, "%s[%s]\n", info, c_temp->name);
        c_temp = c_temp->next;
    }*/

    return env->NewStringUTF(c_temp);
}
}
extern "C" {
    JNIEXPORT jint JNICALL
    Java_com_wwwjf_ffmpegdemo_PlayerActivity_createPlayer(JNIEnv *env, jobject thiz, jstring path,
                                                          jobject surface) {
        auto *player = new Player(env, path, surface);
        return (jint) player;
}
JNIEXPORT void JNICALL
Java_com_wwwjf_ffmpegdemo_PlayerActivity_play(JNIEnv *env, jobject thiz, jint player) {

    auto *p = (Player *) player;
    p->play();

}
JNIEXPORT void JNICALL
Java_com_wwwjf_ffmpegdemo_PlayerActivity_pause(JNIEnv *env, jobject thiz, jint player) {

    auto *p = (Player *) player;
    p->pause();
}
    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_PlayerActivity_stop(JNIEnv *env, jobject thiz, jint player) {

        auto *p = (Player *) player;
        p->stop();
    }

    JNIEXPORT jint JNICALL
    Java_com_wwwjf_ffmpegdemo_OpenGLPlayerActivity_createGLPlayer(JNIEnv *env, jobject thiz,
                                                                  jstring path, jobject surface) {

        GLPlayer *player = new GLPlayer(env, path);
        player->SetSurface(surface);
        return (jint) player;
    }
    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_OpenGLPlayerActivity_playOrPause(JNIEnv *env, jobject thiz,
                                                                  jint player) {

        GLPlayer *p = (GLPlayer *) player;
        p->PlayOrPause();
    }
    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_OpenGLPlayerActivity_stop(JNIEnv *env, jobject thiz,
                                                                  jint player) {

        GLPlayer *p = (GLPlayer *) player;
        p->Release();
    }
    JNIEXPORT jint JNICALL
    Java_com_wwwjf_ffmpegdemo_RepackActivity_createRepack(JNIEnv *env, jobject thiz, jstring src_path,
                                                          jstring dest_path) {
        FFRepack *repack = new FFRepack(env, src_path, dest_path);
        return (jint) repack;
    }
    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_RepackActivity_startRepack(JNIEnv *env, jobject thiz, jint repack) {
        FFRepack *ffRepack = (FFRepack *) repack;
        ffRepack->Start();
    }

    JNIEXPORT jint JNICALL
    Java_com_wwwjf_ffmpegdemo_FFEncodeActivity_initEncoder(JNIEnv *env, jobject thiz, jstring inPath, jstring outPath) {
        Synthesizer *synthesizer = new Synthesizer(env, inPath, outPath);
        return (jint)synthesizer;
    }

    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_FFEncodeActivity_startEncoder(JNIEnv *env, jobject thiz, jint synthesizer) {
        Synthesizer *s =  (Synthesizer *)synthesizer;
        s->Start();
    }

    JNIEXPORT void JNICALL
    Java_com_wwwjf_ffmpegdemo_FFEncodeActivity_releaseEncoder(JNIEnv *env, jobject thiz, jint synthesizer) {
        Synthesizer *s =  (Synthesizer *)synthesizer;
        delete s;
    }
}