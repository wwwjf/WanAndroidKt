package com.xianghe.ivy.ui.module.record.model;

import java.io.Serializable;
import java.util.List;

public class MovieEditModel implements Serializable {

    private String videoFileName;                                                                   //视频的文件夹

    private List<MovieItemModel> movieItemModels;

    public MovieEditModel(String videoFileName, List<MovieItemModel> movieItemModels) {
        this.videoFileName = videoFileName;
        this.movieItemModels = movieItemModels;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public List<MovieItemModel> getMovieItemModels() {
        return movieItemModels;
    }

    public void setMovieItemModels(List<MovieItemModel> movieItemModels) {
        this.movieItemModels = movieItemModels;
    }
}
