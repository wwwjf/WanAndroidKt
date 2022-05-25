package com.xianghe.ivy.constant;

import com.xianghe.ivy.BuildConfig;
import com.xianghe.ivy.app.URLS;

public interface Api {
    public final String BASE_URL = "http://pre.i-weiying.com/api/";

    int REQUEST_CODE = 101;

    /**
     * Url
     */
    public interface Route {
        /**
         * 首页
         */
        public interface Index {
            String CATEGORY_LIST = "index_categoryList";            // url 获取分类列表数据
            String MEDIA_PRAISE = "media_praise";                   // url 点赞
            String MEDIA_CANCEL_PRAISE = "media_cancelpraise";      // url 取消点赞

            String MEDIA_COMMENTS_LIST = "index_mediaCommentsList"; // url 获取视频评论列表
            String MEDIA_ADD_COMMENTS = "index_addMediaComments";   // url 添加视频评论
            String MEDIA_DEL_COMMENT = "media_delComment";          // url 删除视频评论
            String INDEX_GETTOKEN = "index_getToken";               // url 获取上传token
            String MEDIA_MEDIA_DETAIL = "media_mediaDetail";        // url 资讯详情（含评论）

            String INDEX_PUSHPLACE = "index_pushPlace";             // url 推送地理定位信息

            String INDEX_MEDIA_REPORT = "index_MediaReport";        // url 视频信息举报

            String INDEX_MEDIA_REPORT_TYPE = "index_MediaReporttype";        // url 视频举报的类型

            String INDEX_VERSION_CHECK = "system_index";            // url 版本检测
            String SHARE_QRCODE = "index_shareQRCode";              // url 获取分享二维码
            String MEDIA_FORWARDING = "media_forwarding";           // url 转发视频
            String BACKGROUND_MUSIC_LIST = "background_music_list"; // 背景音乐列表
            String MUSIC_COUNT = "music_count"; // 音乐应用统计
            String ACTIVITY_INDEX= "activity_index";                // 活动列表
            String ACTIVITY_RANKING = "index_activity_ranking";     // 活动排行榜
            String SHARE_DESCRIPTION = "activity_share_description"; // 有活动时分享到第三方时需要添加的文案
        }

        /**
         * 我的
         */
        public interface Mine {
            public interface Collection {
                String MEDIA = "collect_media";             // url 收藏视频
                String DEL_MEDIA = "del_collect_media";     // url 取消视频收藏
            }
        }

        /**
         * 用户信息
         */
        public interface User {
            /**
             * 用户信息
             */
            String HOMEPAGE = "user_homePage";
            /**
             * 用户上传影片列表
             */
            String MEDIALIST = "user_mediaList";
            /**
             * 用户动态列表
             */
            String DYNAMICLIST = "user_dynamicList";

            /**
             * 用户收藏列表
             */
            String COLLECTIONLIST = "collect_media_list";

            /**
             * type必传 2.关注列表，3.粉丝列表
             */
            String FRIENDLIST = "friend_friendList";

            /**
             * 举报用户
             */
            String USERREPORT = "user_UserReport";

            /**
             * 加入、解除黑名单
             * friend_uid 	拉黑用户id
             * status 1：加入黑名单， 2：解除黑名单
             */
            String FRIEND_BLACK = "index_FailFriend";

            /**
             * 黑名单列表
             */
            String BLACKLIST = "user_FailFriendList";
            /**
             * 用户信息
             */
            String USERINFO = "user_baseInfo";

            /**
             * 更新头像
             */
            String UPDATEUSERAVATAR = "user_editAvatarSubmit";

            /**
             * 修改昵称
             */
            String UPDATEUSERNICKNAME = "user_editNicknameSubmit";

            /**
             * 更新性别
             */
            String UPDATEUSERSEX = "user_editSexSubmit";

            /**
             * 更新手机号
             */
            String UPDATEUSERMOBILE = "user_editMobileSubmit";

            /**
             * 修改个人资料的生日、个性签名
             */
            String UPDATEBIRTHDAY_INFO = "user_editUserInfoSubmit";

            /**
             * 我的亲友列表
             */
            String RELATIVEMINELIST = "friendList";

            /**
             * 亲友申请列表
             */
            String RELATIVEAPPLYLIST = "friendApplyList";

            /**
             * 搜索用户
             */
            String SEARCHFRIEND = "friendSearch";

            /**
             * 改变亲友申请状态 status 1:同意申请 2:拒绝申请 其他状态无效
             */
            String CHANGEAPPLYSTATUS = "changeApplyStatus";

            /**
             * 删除亲友申请
             */
            String DELETEAPPLY = "deleteApply";

            /**
             * 通讯录检测  1:已邀请，但是还未注册 2:已是亲友 3：已邀请，被邀请用户已注册
             */
            String ADDRESSBOOK = "addressBook";

            /**
             * 通讯录邀请亲友圈：用户手机发送短信邀请成功的记录操作
             */
            String SENDAPPLY = "sendApply";

            /**
             * 关注用户
             */
            String FOLLOWUSER = "friend_friendAdd";

            /**
             * 取消关注用户
             */
            String UNFOLLOWUSER = "friend_frienddel";


            /**
             * 申请添加亲友圈
             */
            String APPLYRELATIVEFRIEND = "friendApply";

            /**
             * 删除亲友
             */
            String DELETERELATIVEFRIEND = "deleteFriend";

            /**
             * 删除影片
             */
            String DELETE_USERMOVIE = "media_delMedia";

            /**
             * 取消收藏影片
             */
            String UNCOLLECT_MOVIE = "del_collect_media";
            /**
             * 用户绑定支付宝、微信信息
             */
            String USER_PAYMENT_DETAIL = "user_payment_account_detail";

            /**
             * 视频邀请列表
             */
            String VIDEO_INVITATION_LIST = "video_invitation_list";

            /**
             * 删除视频邀请记录
             */
            String VIDEO_INVITATION_DEL = "video_invitation_del";

            /**
             * 可能认识的人
             */
            String FRIEND_KNOW_LIST ="friend_know_list";
        }

        /**
         * 登录
         */
        interface Login {
            /**
             * 验证手机号是否注册
             */
            String VERIFY_NUMBER = "verify_number";
            /**
             * 密码登录
             */
            String LOGIN = "login_login";

            /**
             * 发送手机验证码
             */
            String LOGIN_REGISTERSMS = "Login_registerSms";

            /**
             * 用户注册
             */
            String LOGIN_REGISTER = "login_register";

            /**
             * 通讯录绑定检测
             */
            String ADDRESSBOOK_CHECK = "addressbookCheck";
            /**
             * 通讯录绑定
             */
            String ADDRESS_BINDING = "addressbookBinding";

            /**
             * 验证码登录
             */
            String VERIFY_LOGIN = "verify_login";

            /**
             * 微信登录
             */
            String APP_OAUTH_WEIXIN = "App_Oauth_weixin";

            /**
             * Facebook登录
             */
            String APP_OAUTH_FACEBOOK = "Oauth_login_fb";

            /**
             * 忘记密码
             */
            String LOGIN_FORGETPWD = "login_forgetPwd";

            /**
             * 退出登录
             */
            String LOGOUT = "logout";

            /**
             * 版本检测
             */
            String CHECKVERSION = "system_index";

            /**
             * 密码重置
             */
            String PASSWORDRESET = "login_forgetPwd";

            /**
             * 用户登录栏目标签列表
             */
            String TAGSNAME = "tags_name";

            /**
             * 设置用户标签
             * tagsId 用户选择栏目对应ID,多选用，隔开如（1,2,3）
             */
            String CHOICETAGS = "choice_tags";

            /**
             * 获取微信userInfo
             */

            String GETOPENID = "https://api.weixin.qq.com/sns/oauth2/access_token";
            String GETUESRINFO = "https://api.weixin.qq.com/sns/userinfo";
        }

        /**
         * 搜索
         */
        interface Search {
            /**
             * 搜索用户信息、影片信息
             */
            String ESGET_LIST = "es_get_list";
        }
    }

    interface face {
        String CN_LICENSE_URL = "https://api-cn.faceplusplus.com/sdk/v3/auth";
        String FACE_URL = "https://api-cn.faceplusplus.com/cardpp/v1/ocridcard";
        String TEXT_API_KEY = "n0Al3HmIvdIP_4IfGKqv49EaNF06Hawd";
        String TEXT_API_SECRET = "BXNjQnZtbda3afHqKyY4fyzAIj4dnIRz";
        String RELEASE_API_KEY = "4663JfHu8O6aDaE-0c9mqvVSOCGfxIUQ";
        String RELEASE_API_SECRET = "hUpk3vYD20SW_UHHexF8vvicBo0_uons";
        String API_KEY = RELEASE_API_KEY;
        String API_SECRET = RELEASE_API_SECRET;
    }

    interface VIP {
        String MEMBERURL = URLS.BASE_URL_WAP + "/VIPCard?uid=%s&phone=%s&ticket=%s&flag=%s";
    }

    interface Identification {
        String SIGNATRUE_URL = URLS.BASE_URL_WAP + "/holder?phone=%s&ticket=%s&flag=%s";
        String STOCK_URL = URLS.BASE_URL_WAP + "/holderCard?phone=%s&ticket=%s&flag=%s";
        String STOCK_HOLD_URL = URLS.BASE_URL_WAP + "/delegate?phone=%s&ticket=%s&flag=%s";
        String IDENTIFICATION = "shareholder_identity";
        String HOLDERINFO = "shareholder_info";

        String STOCKINFO_DOWNLOAD = "index_download";

        /**
         * 股份协议
         */
        String TYPE_AGREEMENT = "shareholder_agreement";
        /**
         * 股份证书
         */
        String TYPE_CERTIFICATE = "shareholder_certificate";
        /**
         * 代持股协议
         */
        String TYPE_PROXY_AGREEMENT = "shareholder_proxy_agreement";
    }

    interface Invitation {
        String INVITATION = "invitation_UserDetails";
    }

    interface Member {
//        String MEMBER_PIC = "extension_members";
        String MEMBER_PIC = "user_detail";
        String UPLOAD_PIC = "index_fileUpload";
    }

    interface Push {
        String PUSHURL = "setRegistrationId";
        String HW_KEY = "HW";
    }

    public interface Action {
        String USER_ACTION = "userAction";
    }

    public interface Key {

        /**
         * 全局的 Key 可以在全局的拦截器中配置好
         */
        public interface Global {
            String CLIENT_SOURCE = "ClientSource";  // key 客户端类型
            String CLIENT_SYSTEM = "ClientSystem";  // key 客户系统
            String VERSION = "Version";             // key 客户端版本
            String MOBILE_UNIQUE_CODE = "mobile_unique_code";   // key 移动设备唯一标识
            String CLIENT_LANGUAGE = "language_code";//客户端语言
        }

        String CategoryType = "type";           // key 专辑类型
        String PAGE = "page";

        String MEDIA_ID = "media_id";           // key 视频id
        String START_COMMENT_ID = "start_comment_id";   // key 评论起始位置id，默认为0
        String TYPE = "type";                   // key 类型
        String ACTION = "action";               // key action
        String ACTION_TYPE = "action_type";     // key 行为     (默认为1：向下滑动 2：向上滑动)
        String PAGE_SIZE = "pagesize";          // key 每页条数  (默认10）
        String CONTENT = "content";             // key 评论/回复内容
        String COMMENT_ID = "comment_id";       // key 评论id
        String ID = "id";                       // key id
        String MEDIA_COMMENTS_ID = "media_comments_id"; // key id
        String FRIEND_ID = "friend_id";         // key id
        String UID = "uid";                     // key uid

        String recipient_uid = "recipient_uid";                     // key recipient_uid
        String status = "status";                                   // key status
        String room_id = "room_id";                                 // key room_id
        String duration_time = "duration_time";                     // key duration_time
        String DATE_FORMAT = "date_format";
        String ACTIVITY_TYPE = "activity_type";
    }


    public interface Value {


        /**
         * 分页起始页
         */
        int START_PAGE = 1;

        /**
         * 一页数据数量
         */
        int PAGE_SIZE = 10;

        /**
         * 客户端类型
         */
        public interface ClientSource {
            int UNKNOW = 0;     // value 最新
            int ANDROID = 1;    // value Android
        }

        public interface ClientLanguage {
            String ZH_CN = "zh-cn";
            String EN_US = "en-us";
        }

        /**
         * 专辑类型
         */
        public interface CategoryType {
            String NEW = "new";         // value 最新
            String HOT = "hot";         // value 热门
            String FOLLOW = "follow";   // value 关注
            String FRIEND = "friend";   // value 亲友圈
            String RECOMMEND = "recommend"; // value 推荐
        }

        public interface ActionType {
            int FORWARD = 1;    // value 向下滑动
            int BACKWARD = 2;   // value 向上滑动
        }
    }

    interface VideoCall{
        String video_chat = "video_chat";
        String modify_video_chat_status = "modify_video_chat_status";
    }

    //活动列表
    interface IActivityContent{
        String activity_type = "activity_type";
    }

    /**
     * 支付业务相关接口
     */
    interface IvyPay{
        /**
         * 加入会员费用
         */
        String member_fee = "member_fee";
        /**
         * 会员推荐人信息
         */
        String member_recommend_details = "member_recommend_details";

        String pay = "pay";


        /**
         * 修改推荐人信息
         */
        String update_member_recommend = "modify_member_recommend";

    }
}
