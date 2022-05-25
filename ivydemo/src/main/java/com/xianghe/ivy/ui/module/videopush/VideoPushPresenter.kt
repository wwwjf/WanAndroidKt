package com.xianghe.ivy.ui.module.videopush

import android.text.TextUtils
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.xianghe.ivy.R
import com.xianghe.ivy.app.IvyApp
import com.xianghe.ivy.app.greendao.GreenDaoManager
import com.xianghe.ivy.entity.dao.VideoPushCacheDao
import com.xianghe.ivy.entity.db.TagsCategory
import com.xianghe.ivy.entity.db.UploadTaskCache
import com.xianghe.ivy.entity.db.VideoPushCache
import com.xianghe.ivy.http.scheduler.SchedulerProvider
import com.xianghe.ivy.manager.UserInfoManager
import com.xianghe.ivy.mvp.loadPager.BaseLoadingPresenter
import com.xianghe.ivy.utils.IvyUtils
import com.xianghe.ivy.utils.KLog
import com.xianghe.ivy.utils.NetworkUtil
import java.io.File
import java.util.*

/**
 * @Author: ycl
 * @Date: 2018/10/24 17:12
 * @Desc:
 */
class VideoPushPresenter : BaseLoadingPresenter<VideoPushContract.View>(), VideoPushContract.Presenter {

    private val mModel by lazy { VideoPushModel() }
    private val mDao by lazy { GreenDaoManager.getInstance().tagsCategoryDao }
//    private var mUploadManager: UploadManager? = null


    override fun cancleAllRequest() {
//        mUploadManager?.cancel()
        removeAll()
    }

    override fun getMediaUpload(videoPath: String, title: String, description: String,
                                director: String, player: String, location: String,
                                privince: String, city: String, private: String,
                                tags: String,isScreenRecord: Int, is_participate_activity: Int) {

        // ================== by LuWei 只做最小改动 - Start ==================
        val uid = UserInfoManager.getUid()
        saveVideoCache(uid, director, player)
        // ================== by LuWei 只做最小改动 - Start  ==================

        // 1. 获取视频第一帧图片，并上传第一帧图片
        val imagePath = if (view()?.getFirstFilPicPath() != null && File(view()?.getFirstFilPicPath()).exists()) {
            view()?.getFirstFilPicPath()
        } else {
            IvyUtils.getVideoFrame(IvyApp.getInstance().applicationContext, videoPath, 2)
        }
        if (imagePath == null) {
            view()?.onError(IvyApp.getInstance().getString(R.string.common_media_cover_failed))
            return
        }
        /*mUploadManager = UploadManager.Builder().setFilePath(imagePath)
                .setType(UploadManager.uploadType.COVER)
                .setListener(object : UploadManager.uploadListener {
                    override fun onSuccessUpload(mCache: UploadTaskCache?, path: String?) {
                        if (path != null) {
                            // 子线程
                            upload(videoPath, title, description,
                                    director, player, location,
                                    privince, city, path,
                                    private, tags, imagePath,
                                    isScreenRecord, is_participate_activity)
                        }
                    }

                    override fun onErrorUpload(mCache: UploadTaskCache?, t: Throwable?) {
                        KLog.d("" + t)
                        view()?.onError(t)
                    }

                    override fun onProgressUpload(mCache: UploadTaskCache?, currentOffset: Long, totelLength: Long) {
                        KLog.i("currentOffset:" + currentOffset + "totelLength: " + totelLength)
                    }
                }).build()
                .start()*/
    }

    // 2 . 获取到第一帧图片，上传视频信息到后台
    fun upload(videoPath: String, title: String, description: String,
               director: String, player: String, location: String,
               privince: String, city: String, path: String,
               private: String, tags: String, imagePath: String,
               isScreenRecord:Int, is_participate_activity:Int) {
        val duration = java.lang.Long.valueOf(IvyUtils.getMediaDuration(videoPath)) / 1000
        add(mModel.getMediaUpload(title, description, director, player,
                location, privince, city, path, duration.toString(), private, tags,isScreenRecord,is_participate_activity)
                .subscribe({
                    if (it.data != null) {
                        if (!NetworkUtil.isNetworkConnected(IvyApp.getInstance().applicationContext)) {
                            view()?.onError(IvyApp.getInstance().getString(R.string.common_network_error))
                        } else {
                            view()?.onStartVideoUpload("${it.data.media_id}", videoPath, imagePath, title,
                                    description,director,player,location)
                        }
                    } else {
                        view()?.onError(IvyApp.getInstance().getString(R.string.common_upload_failed))
                    }
                }, {
                    KLog.d("" + it)
                    view()?.onError(it)
                })
        )
    }

    override fun getMovieTags() {
        showLoading()
        val cache = null;
//        val cache = Observable.create<BaseResponse<List<TagsCategory>>> {
//            val list = mDao.queryBuilder().list()
//            KLog.i(list.size)
//            val response: BaseResponse<List<TagsCategory>> = BaseResponse<List<TagsCategory>>()
//            response.run {
//                data = list
//                status = if (list == null || list.isEmpty()) 0 else 1
//            }
//            it.onNext(response)
//        }
        val network = mModel.getMovieTags(1)
        add(mModel.getMovieTags(1)
                .subscribeOn(SchedulerProvider.io())
                .firstElement() // 收到一个有效事件就停止
                .observeOn(SchedulerProvider.ui())
                .subscribe({
                    KLog.i(it)
                    clossLoading()
                    val data = it.data
                    if (data != null) {
                        //insertDataToDao(data)
                        view()?.onMovieTagSuccess(data)
                    } else {
                        view()?.onMovieTagSuccess(Collections.emptyList())
                    }
                }, {
                    KLog.i(it)
                    clossLoading()
                    view()?.onMovieTagSuccess(Collections.emptyList())
                }))
    }

    private fun insertDataToDao(data: List<TagsCategory>) {
        if (mDao != null) {
            val size = mDao.queryBuilder().list().size
            KLog.i(size)
            if (size > 0) {
                mDao.deleteAll()
            }
            data.forEach {
                mDao.insert(it)
            }
        }
    }

    private fun saveVideoCache(uid: Long, director: String, player: String) {
        val cache = loadVideoCache(uid) ?: return;

        cache.uid = uid
        cache.director = director
        cache.player = player

        val videoPushCacheDao = GreenDaoManager.getInstance().videoPushCacheDao
        if (videoPushCacheDao != null) {
            if (cache.id == null) {
                videoPushCacheDao.insert(cache);

            } else {
                videoPushCacheDao.update(cache);

            }
        }
    }

    @Nullable
    private fun loadVideoCache(uid: Long): VideoPushCache? {
        var cache: VideoPushCache? = null

        val dao = GreenDaoManager.getInstance().videoPushCacheDao ?: return cache;

        val caches = dao.queryBuilder().where(VideoPushCacheDao.Properties.Uid.eq(uid))?.list()

        if (caches == null || caches.size <= 0) {
            cache = VideoPushCache();
        } else {
            cache = caches.get(0);
        }
        return cache;
    }

    @NonNull
    fun getCacheDirectors(uid: Long): List<String> {
        var directors = ArrayList<String>()

        val cache = loadVideoCache(uid)
        if (cache != null && !TextUtils.isEmpty(cache.director)) {
            val directorArray = cache.director.split(',')
            for (director in directorArray) {
                directors.add(director)
            }
        }

        return directors;
    }

    @NonNull
    fun getCacheActors(uid: Long): List<String> {
        var actors = ArrayList<String>()

        val cache = loadVideoCache(uid)
        if (cache != null && !TextUtils.isEmpty(cache.player)) {
            val actorsArray = cache.player.split(',')
            for (actor in actorsArray) {
                actors.add(actor)
            }
        }

        return actors;
    }
}
