package com.wwwjf.videodemo.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

public interface ICamera {
    /**
     * open the camera
     */
    void open(int cameraId);

    /**
     * set the preview texture
     */
    void setPreviewTexture(SurfaceTexture texture);

    /**
     * set the camera config
     */
    void setConfig(Config config);

    void setOnPreviewFrameCallback(PreviewFrameCallback callback);

    void preview();

    Camera.Size getPreviewSize();

    /**
     * close the camera
     */
    boolean close();

    class Config {
        public float rate = 1.778f; //宽高比
        public int minPreviewWidth;
        public int minPictureWidth;
    }

    interface PreviewFrameCallback {
        void onPreviewFrame(byte[] bytes, int width, int height);
    }
}