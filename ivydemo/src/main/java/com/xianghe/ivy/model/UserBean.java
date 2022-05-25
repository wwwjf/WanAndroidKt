package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 用户信息
 */
public class UserBean implements Serializable {

    private static final long serialVersionUID = -66092885774874290L;

    /**
     * id : 8272551
     * name : 白衬衫黄昏木吉他
     * real_name :
     * mobile :
     * avatar : http://thirdwx.qlogo.cn/mmopen/XxT9TiaJ1ibf3aV8icdK5nib1ENzRPEpKxYWKJ3fBdTTm1LIthFzIppDPEZkia6AP6HD6hxyu42pTAt5oxzam6zxby38gtktWr1bL/132
     * praise : 0
     * uid : 8272551
     * collection : 3
     * media_count : 0
     * fans_count : 0
     * follow_count : 0
     * background : http://ivystorage.oss-cn-shenzhen.aliyuncs.com/images/bg/bg1@2x.png
     * is_author_star : 0
     * star_status : 0
     */
    private long id;
    private String name;
    private String real_name;
    private int sex;
    private String mobile;
    private String avatar;
    private long praise;
    private long uid;
    private long collection;
    private long media_count;
    private long fans_count;
    private long follow_count;
//    private String background;
    /**
     * 是否关注
     */
    private int is_author_star;
    private int star_status;
    private int has_activity;

    private long friend_num;
    /**
     * 是否是亲友关系 0-不是，1-是,2-等待同意
     */
    private int is_friend;
    /**
     * 用户角色 1=股东用户 其他都是普通用户
     */
    private int role;

    /**
     * 推广会员 1=是 其他都不是
     */
    private int membership;

    /**
     * 当前用户已被加入黑名单：0 否   1 是
     */
    private int is_blacklist;

    /**
     * 生日
     */
    private String birthday;
    /**
     * 个性签名
     */
    private String signature;
    /**
     * 运营中心管理员 0-不是，1-是
     */
    private int operations_manager;

    /**
     * 运营中心负责人
     */
    private int is_affiliation_leader;

    /**
     * 备注名
     */
    private String remarkName;

   public int getHas_activity() {
        return has_activity;
    }

    public void setHas_activity(int has_activity) {
        this.has_activity = has_activity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getPraise() {
        return praise;
    }

    public void setPraise(long praise) {
        this.praise = praise;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getCollection() {
        return collection;
    }

    public void setCollection(long collection) {
        this.collection = collection;
    }

    public long getMedia_count() {
        return media_count;
    }

    public void setMedia_count(long media_count) {
        this.media_count = media_count;
    }

    public long getFans_count() {
        return fans_count;
    }

    public void setFans_count(long fans_count) {
        this.fans_count = fans_count;
    }

    public long getFollow_count() {
        return follow_count;
    }

    public void setFollow_count(long follow_count) {
        this.follow_count = follow_count;
    }

    /*public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }*/

    public int getIs_author_star() {
        return is_author_star;
    }

    public void setIs_author_star(int is_author_star) {
        this.is_author_star = is_author_star;
    }

    public int getStar_status() {
        return star_status;
    }

    public void setStar_status(int star_status) {
        this.star_status = star_status;
    }

    public long getFriend_num() {
        return friend_num;
    }

    public void setFriend_num(long friend_num) {
        this.friend_num = friend_num;
    }

    public int getIs_friend() {
        return is_friend;
    }

    public void setIs_friend(int is_friend) {
        this.is_friend = is_friend;
    }


    public int getRole() {
        return role;
    }

    public int getMembership() {
        return membership;
    }

    public void setMembership(int membership) {
        this.membership = membership;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getIs_blacklist() {
        return is_blacklist;
    }

    public void setIs_blacklist(int is_blacklist) {
        this.is_blacklist = is_blacklist;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getOperations_manager() {
        return operations_manager;
    }

    public void setOperations_manager(int operations_manager) {
        this.operations_manager = operations_manager;
    }

    public int getIs_affiliation_leader() {
        return is_affiliation_leader;
    }

    public void setIs_affiliation_leader(int is_affiliation_leader) {
        this.is_affiliation_leader = is_affiliation_leader;
    }

    public String getRemarkName() {
        return remarkName;
    }

    public void setRemarkName(String remarkName) {
        this.remarkName = remarkName;
    }
}
