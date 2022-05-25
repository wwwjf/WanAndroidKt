package com.xianghe.ivy.app;

import android.Manifest;
import android.os.Environment;

public class IvyConstants {

    /**
     * 万能签名（测试使用）
     */
    public static final String COMMONSIGNVALUE = "TY-8da23798c69b7bf706604f25c2c9549ae21593b6bdf4be64";
    public static final String signValue = "";



    //------h5页相关url end----------------

    /**
     * clear_login
     */
    public static final String SP_CLEAR_LOGIN = "clear_login";

    /**
     * 用户登录信息 sp文件名称
     */
    public static final String SP_USERINFO = "USER_INFO";

    /**
     * 用户是否登录
     */
    public static final String IS_LOGIN = "mIsLogin";

    /**
     * 环信是否登录
     */
    public static final String HX_LOGIN = "mHXLogin";
    /**
     * 环信userName
     */
    public static final String HX_USERNAME = "hx_userName";
    /**
     * 环信pwd
     */
    public static final String HX_PASSWORD = "hx_password";

    /**
     * 用户邀请码
     */
    public static final String INVITATION_CODE = "invitation_code";
    /**
     * 邀请者的邀请码
     */
    public static final String INVITER_INVITATION_CODE = "inviter_invitation_code";
    /**
     * 用户token
     */
    public static final String TICKET = "ticket";

    /**
     * 用户id
     */
    public static final String UID = "uid";

    /**
     * 用户选择的喜好类型
     */
    public static final String USER_TAGS = "user_tag";

    /**
     * 用户手机号
     */
    public static final String USER_MOBILE = "user_mobile";

    /**
     * 用户头像
     */
    public static final String USER_AVATAR = "user_avatar";

    /**
     * 是否会员
     */
    public static final String USER_IS_MEMBER = "user_is_member";

    /**
     * ticket过期时间 单位:秒（15天）
     */
    public static final int TIMEOUT_TICKET = 15 * 24 * 60 * 60;


    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    /**
     * 系统设置参数sp xml文件名
     */
    public static final String SP_SYSTEM_SETTING = "IVY_SETTINGS";

    /**
     * 流量自动播放开关
     */
    public static final String KEY_GRPS_PLAY = "KEY_GPRS_PLAY";

    /**
     * 极光 RegistrationID
     */
    public static final String REGISTRATION_ID = "RegistrationID";

    /**
     * 短信发送结果
     */
    public static final String SMS_ACTION = "com.xianghe.ivy.SMS.RESULT";

    /**
     * 手机号码长度
     */
    public static final int PHONE_NUMBER_LENGTH = 11;

    /**
     * 弹框弹出的拨打信息，回调到当前界面执行的再次拨打的请求
     */
    public static final int REQUEST_CODE_VIDEO_CALL = 15657;
    /**
     * 打开本地视频文件夹请求code
     */
    public static final int REQUEST_CODE_VIDEO_CHOOSE = 15656;
    /**
     * 打开本地视频文件夹请求code
     */
    public static final int REQUEST_CODE_S = 15664;


    public static final int REQUEST_CODE_20000 = 20000;

    public static final int REQUEST_CODE_20001 = 20001;

    public static final int REQUEST_CODE_20002 = 20002;

    /**
     * 收藏数、关注数、编辑资料、亲友数,黑名单,备注名变化
     */
    public static final int REQUEST_CODE_20003 = 20003;

    public static final int REQUEST_CODE_20004 = 20004;

    public static final int REQUEST_CODE_20005 = 20005;

    public static final int REQUEST_CODE_20006_ABOUT_MEMBER = 20006;
    public static final int REQUEST_CODE_20006_ABOUT_MARKET = 20007;
    public static final int REQUEST_CODE_20006_ABOUT_STOCK = 20008;

    /**
     * 我的收益、运营中心收益
     */
    public static final int REQUEST_CODE_20009_INCOME = 20009;


    /**
     * 返回后需要更新用户主页信息
     */
    public static final int REQUEST_CODE_USER_UPDATE = 15655;

    /**
     * 打开本地图片请求code
     */
    public static final int REQUEST_CODE_IMAGE_CHOOSE = 15654;
    /**
     * 视频聊天时完善资料进入个人资料页面返回
     */
    public static final int REQUEST_CODE_USER_UPDATE_VIDEO_CALL = 15653;
    /**
     * 调用裁剪图片
     */
    public static final int REQUEST_CODE_CROP = 15652;

    /**
     * 录屏请求code
     */
    public static final int REQUEST_CODE_SCREEN_RECORD = 15664;

    /**
     * 进入草稿箱请求code
     */
    public static final int REQUEST_CODE_MOVIE_CACHE = 15665;

    /**
     * 进入提现页请求code
     */
    public static final int REQUEST_CODE_WITHDRAWAL = 15666;

    /**
     * 视频拍摄页面
     */
    public static final int VIDEO_RECORD = 0;

    /**
     * 视频编辑页面
     */
    public static final int VIDEO_EDIT = 1;

    /**
     * 视频发布页面
     */
    public static final int VIDEO_PUSH = 2;

    /**
     * 打开相机权限
     */
    public static final String[] PERMISSIONS_RECORDING = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.WAKE_LOCK
    };

    /**
     * 打开联系人权限
     */
    public static final String[] PERMISSIONS_CONTACTS_SPLASH = new String[]{
            Manifest.permission.READ_PHONE_STATE};


    public static final String[] PERMISSIONS_PHONE_STATE = new String[]{
            Manifest.permission.READ_PHONE_STATE
    };

    /**
     * 打开联系人权限
     */
    public static final String[] PERMISSIONS_CONTACTS = new String[]{
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS};

    /**
     * 读取 SD 文件
     */
    public static final String[] PERMISSIONS_SD = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * 相机权限请求code
     */
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 15657;

    /**
     * 相机权限请求code
     */
    public static final int PERMISSION_REQUEST_CODE_ALERT = 1455;

    /**
     * 联系人权限code 进入App就申请
     */
    public static final int PERMISSION_REQUEST_CONTACTS_INIT = 15658;

    /**
     * 联系人权限code 进入通讯录列表
     */
    public static final String PERMISSION_REQUEST_CONTACTS_INVITE = "Contact";


    /**
     * 进入亲友圈，请求权限
     */
    public static final String PERMISSION_REQUEST_CONTACTS_RELATIVE_FRIEND = "relative_friend";

    /**
     * 定位权限code
     */
    public static final int PERMISSION_REQUEST_LOCATION = 15660;
    public static final int PERMISSION_REQUEST_LOCATION_PRE = 15661;

    /**
     * SD 读取权限code 进入App就申请
     */
    public static final int PERMISSION_REQUEST_SD_CARD = 15662;

    /**
     * 设备权限请求code
     */
    public static final int PERMISSION_REQUEST_PHONE_STATE = 15663;

    /**
     * 视频路径key
     */
    public static final String KEY_VIDEO_PATH = "Key_VideoPath";
    /**
     * 视频路径key
     */
    public static final String KEY_AUDIO_PATH = "Key_AudioPath";

    /**
     * 是否包含音频
     */
    public static final String KEY_ISCCONTAI_MUSIC = "Key_isContaiMusic";

    /**
     * 音乐名称
     */
    public static final String KEY_MUSIC_NAME = "Key_MusicName";

    /**
     * 旋转角度
     */
    public static final String KEY_ROTATION_ANGAL = "Key_RotationAngle";

    /**
     * 是否横屏
     */
    public static final String KEY_LANDSCAPE = "Key_Landscape";
    /**
     * 分页起始页
     */
    public static final int START_PAGE = 1;

    public static final String EVENTBUS_TAG = "EventBus_TAG_";


    /**
     * 作者：created by huangjiang on 2018/9/12  下午3:33
     * 邮箱：504512336@qq.com
     * 描述： EventBus 参数定义
     */
    public static final String CAMERA_RELEASE = "cameraRelease";//用于视频录制完成后的回调
    public static final String LOCAL_MUSIC_CALL = "localMusicCall";//用于本地音乐选中完成后的回调
    public static final String NETWORK_MUSIC_CALL = "networkMusicCall";//用于网络音乐选中完成后的回调
    public static final String NETWORK_MUSIC_SELECT = "musicselect";//音乐选择
    public static final String NETWORK_MUSIC_CANCEL_SELECT = "cancel_musicselect";//取消音乐选择
//    public static final String SOUND_RECORDING = "sounRecording";//用于在正录音点击返回时的回调

    public static final String LOCAL_MUSIC_EDITO_CALL = "localMusicEditorCall";//用于本地音乐选中完成后的回调--编缉页面
    public static final String NETWORK_MUSIC_EDITO_CALL = "networkMusicEditorCall";//用于网络音乐选中完成后的回调--编缉页面
    /**
     * 是否每次显示新手指引提示（调试使用）
     */
    public static final boolean ALWAYS_SHOW = false;
    /**
     * 联系电话
     */
    public static final String IVY_PHONE = "0755-22904978";

    //极光推送
    public interface iPUSH_TYPE {
        /**
         * 关注列表影片
         */
        int followList = 0;
        /**
         * 亲友圈影片列表
         */
        int relativeMovieList = 1;
        /**
         * 个人动态列表
         */
        int dynamicList = 2;
        /**
         * 亲友圈好友列表
         */
        int relativeList = 3;

        /**
         * 在其他设备登录的推送
         */
        int loginOtherDevice = 4;
    }

    public static final String SOUND_RECODING_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 保存Video编辑的目录名
     */
    public final static String JWZT_FOLDER_NAME = "/jwzt_recorder";
    /**
     * @Date 创建时间: 2018/9/20
     * @Description 描述： 存储下载音乐。及列表文件
     */
    public final static String MUSIC_FILE = SOUND_RECODING_PATH + "/download" + "/music";

    public final static String EVENTBUS_MESSAGE_SHARE_ACTION = "shareAction";
    //进度详情 ，
    public final static String EVENTBUS_MESSAGE_PROGRESS_UP = "progress_up";
    //进度详情 ，
    public final static String EVENTBUS_MESSAGE_PROGRESS_DOWN = "progress_down";
    //进度暂停
    public final static String EVENTBUS_MESSAGE_PROGRESS_STOP = "progressStop";

    //进度重新开始
    public final static String EVENTBUS_MESSAGE_PROGRESS_RESTART = "progressSeStart";
    //取消
    public final static String EVENTBUS_MESSAGE_PROGRESS_CANCEL = "progressCancel";
    //进度 返回
    public final static String EVENTBUS_MESSAGE_PROGRESS_BCAK = "progressBack";
    //进度 错误
    public final static String EVENTBUS_MESSAGE_PROGRESS_ERR = "progressErr";
    //进度 完成
    public final static String EVENTBUS_MESSAGE_PROGRESS_SUCCESS = "progressSuccess";
    //悬浮框  上传
    public final static String EVENTBUS_MESSAGE_UPLOAD = "upload_progress";
    //开始
    public final static String EVENTBUS_MESSAGE_UPLOAD_START = "upload_progress_start";
    //悬浮框  下载
    public final static String EVENTBUS_MESSAGE_DOWNLOAD = "download_progress";
    //悬浮框  隐藏
    public final static String EVENTBUS_MESSAGE_DISMISS = "progress_dismiss";

    //悬浮框  显示
    public final static String EVENTBUS_MESSAGE_SHOW = "progress_show";

    public final static String SAVE_OBJECT_NAME = "saveObjectName";
    public final static String SAVE_OBJECT_MOVIEUPLOAD_NAME = "saveObjecMovieUploadtName";


    // 上传需用的的参数
    public final static String ACCESSKEYID = "AccessKeyId";
    public final static String ACCESSKEYSECRET = "AccessKeySecret";
    public final static String ENDPOINT = "endpoint";
    public final static String BUCKETNAME = "bucketName";
    public final static String CALLBACKURL = "callbackUrl";
    public final static String CALLBACKBODY = "callbackBody";
    //获取OSS TOKEN
    public final static String SECURITYTOKEN = "SecurityToken";
    public final static String EXPIRES_IN = "expires_in";
    //上传目录
    public final static String AVATAR_PATH = "avatar";
    public final static String VIDEO_PATH = "video";
    public final static String CORER_PATH = "cover";
    public final static String MUSIC_PATH = "music";
    public final static String SHAREHOLDER_SIGN = "shareholder_sign";
    public final static String IDCARD_PATH = "idcard";
    public final static String MEMBER_AVATAR = "member_avatar";
    public final static String CDN_ENABLE = "cdn_enable";
    public final static String CDN_URL = "cdn_url";
    public final static String CDN_MASK = "cdn_mask";
    public final static String WEI_CHAT_DIR = "Tencent/MicroMsg";

    /**
     * 关于推广会员视频
     */
    public static final String url_member_intro = "http://ivystorage.oss-cn-shenzhen.aliyuncs.com/videos/appvideo/tuguanghuiyuan.mp4";

    /**
     * 关于股东视频
     */
    public static final String url_stock_intro = "http://ivystorage.oss-cn-shenzhen.aliyuncs.com/videos/appvideo/guquan.mp4";

    /**
     * 关于运营中心视频
     */
    public static final String url_market_intro = "http://ivystorage.oss-cn-shenzhen.aliyuncs.com/videos/appvideo/yunyingzhongxin.mp4";
//    public static final String url_market_intro = "https://interface.sina.cn/wap_api/video_location.d.html?cid=195763&table_id=258&did=hvhiqax5277701&vt=4&creator_id=&vid=214007756&video_id=288478875&r=v.sina.cn%2Fweibo_ugc%2F2019-04-26%2Fdetail-ihvhiqax5277701.d.html&wm=3049_0013&time=1557888811284&rd=0.38650219199551006";

    /**
     * 关闭页面广播
     */

public static final String close_activity_broadcast_receiver ="com.ivy.close.activity";

}
