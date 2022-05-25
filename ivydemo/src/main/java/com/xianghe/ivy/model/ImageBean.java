package com.xianghe.ivy.model;

import android.media.ExifInterface;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class ImageBean implements Serializable {
    public static final int NORMAL = 1;
    public static final int QUICK = 2;
    public static final int FADE = 3;
    public static final int HORIZONTALSCROLLING = 4;
    public static final int VERTICALSCROLLING = 5;
    public static final int GRAY = 6;

    private ArrayList<ImageInfo> imagePathList;
    private String outputPath;
    private String outputH264Path;
    private int outputType;
    private ImageBean imageBean;

    public ImageBean(){
        imageBean = this;
        imagePathList = new ArrayList<>();
    }

    public ImageBean setOutputPath(String outputPath){
        this.outputPath = outputPath;
        this.outputH264Path = outputPath.substring(0,outputPath.lastIndexOf("/")) + "cloutput.h264";
        return imageBean;
    }

    public ImageBean setOutputType(int outputType){
        this.outputType = outputType;
        return imageBean;
    }

    public ImageBean setImagePathList(ArrayList<String> list){
        this.imagePathList.clear();
        for(String s : list){
            int rotate = 0;
            rotate = getExifOrientation(s);
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setImagePath(s);
            imageInfo.setImageRotate(rotate);
            imagePathList.add(imageInfo);
        }
        return imageBean;
    }

    public ImageBean addImagePath(ImageInfo imagePath){
        imagePathList.add(imagePath);
        return imageBean;
    }

    public ImageBean addImagePath(int index,ImageInfo imagePath){
        imagePathList.add(index,imagePath);
        return imageBean;
    }

    public ImageBean removeImagePath(int index){
        imagePathList.remove(index);
        return imageBean;
    }

    public ImageBean removeImagePath(String imagePath){
        imagePathList.remove(imagePath);
        return imageBean;
    }

    public ImageBean clearImagePathList(){
        imagePathList.clear();
        return imageBean;
    }

    public int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        }
        return degree;
    }
}
