package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MediaReportTypeBean implements Serializable {


    @SerializedName("video_type")
    private List<VideoTypeBean> mVideoType;
    @SerializedName("people_type")
    private List<PeopleTypeBean> mPeopleType;

    public List<VideoTypeBean> getVideoType() {
        return mVideoType;
    }

    public void setVideoType(List<VideoTypeBean> videoType) {
        mVideoType = videoType;
    }

    public List<PeopleTypeBean> getPeopleType() {
        return mPeopleType;
    }

    public void setPeopleType(List<PeopleTypeBean> peopleType) {
        mPeopleType = peopleType;
    }

    public interface ReportType {
        public int getId();

        public int getTypeCode();

        public String getName();
    }

    @Override
    public String toString() {
        return "MediaReportTypeBean{" +
                "mVideoType=" + mVideoType +
                ", mPeopleType=" + mPeopleType +
                '}';
    }
}
