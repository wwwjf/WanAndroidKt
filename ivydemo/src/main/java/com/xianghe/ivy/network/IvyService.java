package com.xianghe.ivy.network;

import com.google.gson.JsonElement;
import com.xianghe.ivy.model.response.BaseResponse;
import com.xianghe.ivy.ui.module.welcom.PushBean;

import java.util.Map;

import clojure.lang.Obj;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface IvyService {


//    /**
//     * 验证手机号是否注册
//     *
//     * @param mobileNumber
//     * @return
//     */
//    @POST("verify_number")
//    Call<BaseResponse> verifyMobile(@Query("mobile") String mobileNumber);
//
//    /**
//     * 发送手机验证码
//     *
//     * @param mobileNumber
//     * @return
//     */
//    @POST("Login_registerSms")
//    Call<BaseResponse> sendSms(@Query("mobile") String mobileNumber);
//
//    /**
//     * 用户注册
//     *
//     * @param mobileNumber       手机号
//     * @param password           密码（密码必须为6-16位字母数字符号组合）
//     * @param smsCode            短信验证码
//     * @param openid             微信openid
//     * @param mobile_unique_code mobile_unique_code 设备标识
//     * @return
//     */
//    @POST("login_register")
//    Call<BaseResponse> register(@Query("mobile") String mobileNumber,
//                                @Query("password") String password,
//                                @Query("smscode") String smsCode,
//                                @Query("openid") String openid,
//                                @Query("mobile_unique_code") String mobile_unique_code);
//
//    /**
//     * 验证码登录
//     *
//     * @param mobileNumber
//     * @param smsCode
//     * @param openid       非必选
//     * @return
//     */
//    @POST("verify_login")
//    Call<BaseResponse> verifyLogin(@Query("mobile") String mobileNumber,
//                                   @Query("smscode") String smsCode,
//                                   @Query("openid") String openid,
//                                   @Query("mobile_unique_code") String mobile_unique_code);
//
//
//    /**
//     * 微信登录
//     *
//     * @param params
//     * @return
//     */
//    @POST("App_Oauth_weixin")
//    Call<BaseResponse> weChatLogin(@QueryMap HashMap<String, String> params);
//
//    /**
//     * 微信登录
//     *
//     * @param params
//     * @return
//     */
//    @POST("App_Oauth_weixin")
//    Call<BaseResponse> newWeChatLogin(@QueryMap HashMap<String, String> params);
//
//    /**
//     * 忘记密码
//     *
//     * @param mobileNumber
//     * @param smsCode
//     * @param password
//     * @return
//     */
//    @POST("login_forgetPwd")
//    Call<BaseResponse> forgetPwd(@Query("mobile") String mobileNumber,
//                                 @Query("smscode") String smsCode,
//                                 @Query("password") String password);
//
//
//    /**
//     * 指定分类数据视频列表
//     *
//     * @return
//     */
    @POST("index_categoryList")
    Call<BaseResponse> movieList(@Query("type") String type, @Query("page") int page);

//    /**
//     * 单个视频详细信息
//     *
//     * @param id
//     * @return
//     */
//    @POST("index_mediaDetail")
//    Call<BaseResponse> mediaDetail(@Query("id") int id);
//
//    /**
//     * 用户主页
//     *
//     * @param uid
//     * @return
//     */
//    @POST("user_homePage")
//    Call<BaseResponse> userHomePage(@Query("uid") long uid);
//
//    /**
//     * 用户主页影片列表
//     *
//     * @param uid
//     * @param page
//     * @return
//     */
//    @POST("user_mediaList")
//    Call<BaseResponse> userMediaList(@Query("uid") long uid, @Query("page") int page);
//
//
//    /**
//     * 用户主页动态列表
//     *
//     * @param uid
//     * @param page
//     * @return
//     */
//    @POST("user_dynamicList")
//    Call<BaseResponse> userDynamicList(@Query("uid") long uid, @Query("page") int page);
//
//    /**
//     * 版本检测
//     *
//     * @return
//     */
//    @POST("system_index")
//    Call<BaseResponse> checkVersion();
//
//
//    /**
//     * 关注
//     *
//     * @param friendId
//     * @return
//     */
//    @POST("friend_friendAdd")
//    Call<BaseResponse> addFriend(@Query("friend_id") String friendId);
//
//    /**
//     * 取消关注
//     *
//     * @param friendId
//     * @return
//     */
//    @POST("friend_frienddel")
//    Call<BaseResponse> delFriend(@Query("friend_id") String friendId);
//
//    /**
//     * 收藏视频
//     *
//     * @param mediaId
//     * @return
//     */
//    @POST("collect_media")
//    Call<BaseResponse> collectMedia(@Query("media_id") int mediaId);
//
//    /**
//     * 取消收藏视频
//     *
//     * @param mediaId
//     * @return
//     */
//    @POST("del_collect_media")
//    Call<BaseResponse> delCollectMedia(@Query("media_id") int mediaId);
//
//    /**
//     * 评论视频 一级评论
//     *
//     * @param mediaId 视频id
//     * @param content
//     * @return
//     */
//    @POST("index_addMediaComment")
//    Call<BaseResponse> addMediaComment(@Query("id") int mediaId, @Query("content") String content);
//
//
//    /**
//     * 回复视频下的评论 二级评论
//     *
//     * @param commentId 评论id
//     * @param content
//     * @return
//     */
//    @POST("index_addMediaReply")
//    Call<BaseResponse> addMediaReply(@Query("id") int commentId, @Query("content") String content);
//
//    /**
//     * 上传影片信息，获取上传到OSS的影片id
//     *
//     * @return
//     */
//    @POST("media_mediaUpload")
//    Call<BaseResponse> mediaUpload(@QueryMap Map<String, String> params);
//
//    /**
//     * 获取上传token
//     *
//     * @return
//     */
//    @POST("index_getToken")
//    Call<BaseResponse> getUploadToken();
//
//    /**
//     * 上传图片
//     *
//     * @return
//     */
//    @Multipart
//    @POST("index_imgUpload")
//    Call<BaseResponse> uploadImage(@Part() MultipartBody.Part params);
//
//    /**
//     * 上传文件
//     *
//     * @param file
//     * @return
//     */
//    @POST("index_videoUpload")
//    @Multipart
//    Call<BaseResponse> uploadFile(@Part("video_file") RequestBody file, @Part MultipartBody.Part file2);
//
//    /**
//     * 点赞影片
//     *
//     * @param mediaId
//     * @return
//     */
//    @POST("media_praise")
//    Call<BaseResponse> likeMedia(@Query("media_id") int mediaId);
//
//    /**
//     * 取消点赞影片
//     *
//     * @param mediaId
//     * @return
//     */
//    @POST("media_cancelpraise")
//    Call<BaseResponse> unLikeMedia(@Query("media_id") int mediaId);
//
//    /**
//     * 影片评论列表
//     *
//     * @param id
//     * @param page
//     * @return
//     */
//    @POST("index_mediaComments")
//    Call<BaseResponse> mediaCommentList(@Query("id") int id, @Query("page") int page);
//
//    /**
//     * 好友列表
//     *
//     * @param type 1：我的推荐好友列表，2：我关注过的好友列表，3:我的粉丝列表,默认为1
//     * @param page
//     * @return
//     */
//    @POST("friend_friendList")
//    Call<BaseResponse> friendList(@Query("type") int type, @Query("page") int page);
//
//
//    /**
//     * 收藏列表
//     *
//     * @param page
//     * @return
//     */
//    @POST("collect_media_list")
//    Call<BaseResponse> collectionList(@Query("page") int page);
//
//    /**
//     * 视频信息举报
//     *
//     * @param mediaId
//     * @param id
//     * @param content
//     * @return
//     */
//    @POST("index_MediaReport")
//    Call<BaseResponse> mediaReport(@Query("media_id") String mediaId,
//                                   @Query("id") String id,
//                                   @Query("content") String content);
//
//
//    /**
//     * 删除评论/回复
//     *
//     * @param commentId
//     * @return
//     */
//    @POST("media_delComment")
//    Call<BaseResponse> mediaDelComment(@Query("comment_id") int commentId);
//
//    /**
//     * 获取评论回复列表
//     *
//     * @param id
//     * @param page
//     * @return
//     */
//    @POST("index_mediaCommentDetail")
//    Call<BaseResponse> mediaReplyList(@Query("id") int id, @Query("page") int page);
//
//    /**
//     * 删除视频
//     *
//     * @param mediaId
//     * @return
//     */
//    @POST("media_delMedia")
//    Call<BaseResponse> mediaDelete(@Query("media_id") int mediaId);
//
//    /**
//     * 获取所有背景墙
//     *
//     * @return
//     */
//    @POST("get_all_bg_picture")
//    Call<BaseResponse> getAllBg();
//
//
//    /**
//     * 设置用户背景图片
//     *
//     * @param userId
//     * @param pictureId
//     * @return
//     */
//    @POST("set_user_picture")
//    Call<BaseResponse> setUserBg(@Query("user_id") long userId, @Query("picture_id") String pictureId);
//
//
//    /**
//     * 个人信息
//     *
//     * @return
//     */
//    @POST("user_baseInfo")
//    Call<BaseResponse> getUserInfo();
//
//    /**
//     * 修改昵称
//     *
//     * @return
//     */
//    @POST("user_editNicknameSubmit")
//    Call<BaseResponse> updateNickName(@Query("nick_name") String nickName);
//
//    /**
//     * 修改性别
//     *
//     * @param sex
//     * @return
//     */
//    @POST("user_editSexSubmit")
//    Call<BaseResponse> updateUserSex(@Query("sex") int sex);
//
//    /**
//     * 更新头像
//     *
//     * @param imgUrl
//     * @return
//     */
//    @POST("user_editAvatarSubmit")
//    Call<BaseResponse> updateAvatar(@Query("img_file") String imgUrl);
//
//    /**
//     * 亲友圈好友列表(不分页)
//     *
//     * @return
//     */
//    @POST("friendList")
//    Call<BaseResponse> relativeList();
//
//    /**
//     * 亲友圈好友申请列表
//     *
//     * @param page
//     * @return
//     */
//    @POST("friendApplyList")
//    Call<BaseResponse> relativeApplyList(@Query("page") int page);
//
//    /**
//     * 搜索用户
//     *
//     * @param uid
//     * @return
//     */
//    @POST("friendSearch")
//    Call<BaseResponse> friendSearch(@Query("uid") String uid);
//
//    /**
//     * 申请添加亲友圈
//     *
//     * @param uid
//     * @return
//     */
//    @POST("friendApply")
//    Call<BaseResponse> friendApply(@Query("uid") long uid);
//
//    /**
//     * 删除亲友
//     *
//     * @param uid
//     * @return
//     */
//    @POST("deleteFriend")
//    Call<BaseResponse> delRelativeFriend(@Query("uid") long uid);
//
//    /**
//     * 改变好友申请状态
//     *
//     * @param id     申请id
//     * @param status 1:同意申请 2:拒绝申请 其他状态无效
//     * @return
//     */
//    @POST("changeApplyStatus")
//    Call<BaseResponse> changeRelativeStatus(@Query("id") long id, @Query("status") int status);
//
//    /**
//     * 删除亲友申请
//     *
//     * @param id
//     * @return
//     */
//    @POST("deleteApply")
//    Call<BaseResponse> deleteRelativeApply(@Query("id") long id);
//
//    /**
//     * 通讯录检测
//     *
//     * @return
//     */
//    @POST("addressBook")
//    Call<BaseResponse> addressBookCheck();
//
//    /**
//     * 发送邀请短信
//     *
//     * @param mobile
//     * @return
//     */
//    @POST("sendMessage")
//    Call<BaseResponse> sendInviteMessage(@Query("mobile") String mobile);
//
//    /**
//     * 群发邀请短信
//     *
//     * @param mobiles 手机号（英文逗号分隔，例如：13851124415,15321564188）
//     * @return
//     */
//    @POST("groupSendMessage")
//    Call<BaseResponse> sendInviteMessageList(@Query("mobile") String mobiles);
//
//    /**
//     * 用户登录栏目标签列表
//     *
//     * @return
//     */
//    @POST("tags_name")
//    Call<BaseResponse> getTagsName();
//
//    /**
//     * 设置用户标签
//     *
//     * @param tagsId 用户选择栏目对应ID,多选用，隔开如（1,2,3）
//     * @return
//     */
//    @POST("choice_tags")
//    Call<BaseResponse> tagsChoice(@Query("tags_name") String tagsId);
//
//    /**
//     * 修改手机号
//     *
//     * @param mobile
//     * @param code   验证码
//     * @return
//     */
//    @POST("user_editMobileSubmit")
//    Call<BaseResponse> editMobile(@Query("mobile") String mobile, @Query("code") String code);
//
//    /**
//     * 退出登录
//     *
//     * @return
//     */
//    @POST("logout")
//    Call<BaseResponse> logOut();
//
//    /**
//     * 极光推送消息设置
//     *
//     * @param registration_id
//     * @return
//     */
//    @POST("setRegistrationId")
//    Call<BaseResponse> setRegistrationId(@Query("registration_id") String registration_id,
//                                         @Query("mobile_unique_code") String mobile_unique_code);
//
//    /**
//     * 修改手机号
//     *
//     * @param type 音乐类型 0：热门音乐 1：慢音乐
//     * @return
//     */
//    @POST("musicList")
//    Call<BaseResponse> getMusic(@Query("type") int type);
//
//    /**
//     * 修改手机号
//     *
//     * @param type 音乐类型 0：热门音乐 1：慢音乐
//     * @return
//     */
//    @POST("userAction")
//    Call<BaseResponse> setuserAction(@Query("media_id") int mediaId,
//                                     @Query("action") int action,
//                                     @Query("duration") int duration);

    /*推送请求*/
    @POST()
    Observable<PushBean> postPushWay(@Url String url, @QueryMap Map<String, Object> params);


    @FormUrlEncoded
    @POST("{url}")
    Observable<JsonElement> postFieldMap(@Path("url") String url, @FieldMap Map<String, Object> params);


    // ------------------------  netWorkApi start -----------------
    @POST("{sourceUrl}")
    Observable<JsonElement> postMap(@Path(value = "sourceUrl", encoded = true) String sourceUrl,
                                    @QueryMap Map<String, Object> params);

    @POST()
    Observable<JsonElement> postMapByFullUrl(@Url String url,
                                             @QueryMap Map<String, Object> params);

    @GET("{sourceUrl}")
    Observable<JsonElement> get(@Path(value = "sourceUrl", encoded = true) String sourceUrl,
                                @QueryMap Map<String, Object> params);

    @GET()
    Observable<JsonElement> getByFullUrl(@Url String url,
                                         @QueryMap Map<String, Object> params);

    @GET()
    Observable<ResponseBody> getDownLoad(@Url String url);

    // ------------------------  netWorkApi end -----------------

    // -------------  针对老代码封装mvc返回Call的统一调度  start -------------
    @POST("{sourceUrl}")
    Call<BaseResponse> postMapMVC(@Path(value = "sourceUrl", encoded = true) String sourceUrl,
                                 @QueryMap Map<String, Object> params);

    @POST()
    Call<BaseResponse> postMapByFullUrlMVC(@Url String url,
                                          @QueryMap Map<String, Object> params);

    @Multipart
    @POST()
    Observable<JsonElement> postMultipart(@Url String path, @PartMap Map<String, RequestBody> map, @Part MultipartBody.Part file);

    @Multipart
    @POST()
    Observable<JsonElement> postMultipart(@Url String path, @PartMap Map<String, RequestBody> map);


    @Multipart
    @POST("{sourceUrl}")
    Observable<JsonElement> postMultipartByFullUrl(@Path(value = "sourceUrl", encoded = true) String sourceUrl,@PartMap Map<String, RequestBody> map,@Part MultipartBody.Part file);


    @Multipart
    @POST("{sourceUrl}")
    Observable<JsonElement> postMultipartByFullUrl(@Path(value = "sourceUrl", encoded = true) String sourceUrl,@PartMap Map<String, RequestBody> map);
    // -------------  针对老代码封装mvc返回Call的统一调度  end -------------
}
