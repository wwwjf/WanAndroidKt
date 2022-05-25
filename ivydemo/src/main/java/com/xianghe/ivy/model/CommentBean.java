package com.xianghe.ivy.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommentBean implements Serializable {

    /**
     * media_id : 36
     * next_page : 1
     * comments_total : 96
     * list : [{"id":"666","content":"测试评论1222222","created_at":"2018-11-12 10:40:04","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"665","content":"测试评论1222222","created_at":"2018-11-12 10:39:49","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"664","content":"测试评论1222222","created_at":"2018-11-12 10:39:28","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"663","content":"测试评论1222222","created_at":"2018-11-12 10:38:41","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"662","content":"测试评论1222222","created_at":"2018-11-12 10:37:42","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"661","content":"测试评论1222222","created_at":"2018-11-12 10:37:20","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"660","content":"测试评论1222222","created_at":"2018-11-12 10:34:58","uid":"8272565","name":"何颀帅不+1","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/f791/20181116/20181116944b5b4903b7fa8b55d47736df9d9dd7154233795588.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4nHBsKHhb1z2ainbEHZ3WQHNMV02Kzukzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zEVLMQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABFjNRo3BY9jnxpLHi7qBkbVJBGvcQZEyzeg99usbrB6W9XTJg3XPVD9SnwYjLPDA1srg824ypWDCc1I6wdiCc6Cau6MLBX4BGVXlpDz8jRBiy6dHP%2FkNlYSPp%2BepkVkUVZAh7vHUjxVNHPM%2FU%2FtAC3V64oEFkCA%2FEp98O%2FG38zPA%3D&OSSAccessKeyId=STS.NJrMx3hbRnjLngh9dR8JX7iEq&Expires=1542349793&Signature=iG0UOlWk6Koeyv4AmcPcMcCDpZI%3D","reply_uid":"8272516","reply_name":"vivi"},{"id":"659","content":"fggh","created_at":"2018-11-12 09:55:23","uid":"8272599","name":"神仙小武子","avatar":"http://ivy-test.oss-cn-shenzhen.aliyuncs.com/avatar/080e/20181115/20181115d99a7389b3195454e3f8f5831c2383051542261318885.png?security-token=CAISlQJ1q6Ft5B2yfSjIr4vieILCiOZ5x6iTcFXbh288fupdtofbpzz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zSEakQ%2Bs6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABPaH36%2BuEp1Jxyq6KkoWywliHBe7K%2Flb849Q%2BPYHrmII3mAxdmjYWnIeHQR4KYhpvO7L457J6QlsaGqCoJAl5hT0l9jfBvCKks2LXZvI9GuaXwtEbQzHORNH%2BvaFu9c9Q2xs25xfyhFjOahybcr03ivfikTnQhbLTMbAv24EhEz0%3D&OSSAccessKeyId=STS.NHW38ve9XpjxrsjcoireqYBpE&Expires=1542349423&Signature=awB2ji%2FqTmmjMzwSjcGoEUXfoJw%3D","reply_uid":"8272564","reply_name":"www_jf建烽"},{"id":"658","content":"gghhhj","created_at":"2018-11-12 09:52:29","uid":"8272564","name":"www_jf建烽","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEJroYJLnOrSOD1G2Rq2qIzsbD2bRtKQSAVY7AnHdyA4t17kLbXvfNKeIK4DJyIQm1qGlhOuYTibzeQ/132","reply_uid":"8272565","reply_name":"何颀帅不+1"},{"id":"657","content":"gdhddhdhdh","created_at":"2018-11-12 09:51:36","uid":"8272564","name":"www_jf建烽","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/PiajxSqBRaEJroYJLnOrSOD1G2Rq2qIzsbD2bRtKQSAVY7AnHdyA4t17kLbXvfNKeIK4DJyIQm1qGlhOuYTibzeQ/132","reply_uid":"8272565","reply_name":"何颀帅不+1"}]
     */

    @SerializedName("media_id")
    private long mMediaId;
    @SerializedName("next_page")
    private int mNextPage;
    @SerializedName("comments_total")
    private int mCommentsTotal;
    @SerializedName("list")
    private List<CommentItem> mList = new ArrayList<>();

    public long getMediaId() {
        return mMediaId;
    }

    public void setMediaId(long mediaId) {
        mMediaId = mediaId;
    }

    public int getNextPage() {
        return mNextPage;
    }

    public void setNextPage(int nextPage) {
        mNextPage = nextPage;
    }

    public int getCommentsTotal() {
        return mCommentsTotal;
    }

    public void setCommentsTotal(int commentsTotal) {
        mCommentsTotal = commentsTotal;
    }

    public List<CommentItem> getList() {
        // gson 似乎是反射赋值, private List<CommentItem> mList = new ArrayList<>() 和 setList() 做了null处理还是没用.
        // 只能在这里处理了
        if (mList == null) {
            mList = new ArrayList<>();
        }
        return mList;
    }

    public void setList(List<CommentItem> list) {
        if (list == null) {
            mList.clear();
        } else {
            mList = list;
        }
    }

    public CommentItem findItemById(long commentId) {
        CommentItem result = null;
        if (mList != null) {
            for (CommentItem commentItem : mList) {
                if (commentItem.getId() == commentId) {
                    result = commentItem;
                    break;
                }
            }
        }
        return result;
    }
}