package com.xianghe.ivy.ui.module.record.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MovieClipModel implements Serializable {

    private String videoFileName;

    private List<MovieItemModel> movieItemModels;

    public MovieClipModel(String videoFileName, List<MovieItemModel> movieItemModels) {
        this.videoFileName = videoFileName;
        this.movieItemModels = movieItemModels;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public List<MovieItemModel> getMovieItemModel() {
        return movieItemModels;
    }

    public void setMovieItemModel(ArrayList<MovieItemModel> movieItemModel) {
        this.movieItemModels = movieItemModel;
    }
}
