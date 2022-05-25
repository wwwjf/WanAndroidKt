package com.xianghe.ivy.ui.module.videopush

import com.xianghe.ivy.entity.db.TagsCategory
import com.xianghe.ivy.model.response.BaseResponse
import com.xianghe.ivy.model.response.MediaUploadResponse
import com.xianghe.ivy.mvp.loadPager.IBaseLoadingConctact
import io.reactivex.Observable

/**
 * @Author: ycl
 * @Date: 2018/10/25 9:47
 * @Desc:
 */
class VideoPushContract {
    interface Model : IBaseLoadingConctact.IBaseMode {
        fun getMediaUpload(title: String, description: String,
                           director: String, player: String,
                           location: String,
                           privince: String, city: String,
                           media: String, length: String,
                           private: String, tags: String,
                           isScreenRecord:Int,  is_participate_activity: Int

        ): Observable<BaseResponse<MediaUploadResponse>>

        /**
         * type 0-升序，1-降序（录屏第一个）
         */
        fun getMovieTags(type:Int): Observable<BaseResponse<List<TagsCategory>>>
    }

    interface View : IBaseLoadingConctact.IBaseView {
        fun onStartVideoUpload( mediaId: String ,videoPath: String,imagePath: String,title: String ,
                                description: String, director: String, player: String,location: String)
        fun onMovieTagSuccess(data: List<TagsCategory>)
        fun getFirstFilPicPath():String?
    }

    interface Presenter : IBaseLoadingConctact.IBasePresenter<VideoPushContract.View> {
        fun getMediaUpload(videoPath: String, title: String, description: String,
                           director: String, player: String, location: String,
                           province: String, city: String, privateType: String,
                           tag: String,isScreenRecord: Int, is_participate_activity: Int
        )
        fun getMovieTags()
        fun cancleAllRequest()
    }
}
