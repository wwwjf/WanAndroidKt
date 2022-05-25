package com.xianghe.ivy.model.request;

public class MediaReportRequest {
    public static final int TYPE_PORNOGRAPHY = 0;           // 色情低俗
    public static final int TYPE_POLITICALLY_SENSITIVE = 1; // 政治敏感词
    public static final int TYPE_ILLEGAL_CRIME = 2;         // 违法犯罪
    public static final int TYPE_PIRACY = 3;                // 侵犯版权
    public static final int TYPE_INSULT = 4;                // 侮辱谩骂

    private final long mediaId;
    private final int type;
    private String content;

    public MediaReportRequest(long mediaId, int type) {
        this.mediaId = mediaId;
        this.type = type;
    }

    public long getMediaId() {
        return mediaId;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
