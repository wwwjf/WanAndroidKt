package com.xianghe.ivy.ui.module.videopush

import com.luckongo.tthd.net.mapper.RespMapper
import com.xianghe.ivy.constant.URL
import com.xianghe.ivy.entity.db.TagsCategory
import com.xianghe.ivy.http.request.NetworkRequest
import com.xianghe.ivy.model.response.BaseResponse
import com.xianghe.ivy.model.response.MediaUploadResponse
import io.reactivex.Observable

/**
 * @Author:  ycl
 * @Date:  2018/10/24 17:36
 * @Desc:
 */
class VideoPushModel : VideoPushContract.Model {
    override fun getMediaUpload(title: String, description: String,
                                director: String, player: String,
                                location: String,
                                privince: String, city: String,
                                media: String, length: String,
                                private: String, tags: String,isScreenRecord:Int, is_participate_activity:Int): Observable<BaseResponse<MediaUploadResponse>> {
        return NetworkRequest.postMap(
                URL.MEDIA_MEDIAUPLOAD, mapOf(
                "title" to title,
                "description" to description,
                "director" to director,
                "player" to player,
                "screenwriter" to "",
                "staring" to "",
                "director" to "",
                "location" to location,
                "province" to privince,
                "city" to city,
                "media" to media,
                "length" to length,
                "private" to private,
                "owner" to "1",
                "tags" to tags,
                "is_screen_recording" to isScreenRecord,
                "is_participate_activity" to is_participate_activity
        ),
                respMapper = RespMapper())
    }

    /**
     * type 0-升序，1-降序（录屏第一个）
     */
    override fun getMovieTags(type:Int): Observable<BaseResponse<List<TagsCategory>>> {
        return NetworkRequest.postMap(
                URL.TAGS_NAME, mapOf("type" to type),
                respMapper = RespMapper())
    }
}