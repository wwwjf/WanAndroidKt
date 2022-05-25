package com.xianghe.ivy.weight.download;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 作者：created by huangjiang on 2018/10/11  下午5:27
 * 邮箱：504512336@qq.com
 * 描述：API
 */
public interface DownloadService {
    // @Streaming 这个注解必须添加，否则文件全部写入内存，文件过大会造成内存溢出
    @Streaming
//    @GET("01a3bd5737f2e4fcc0c1939b4798b259b3c31247e/com.supercell.clashroyale.mi.apk")
//    @GET("http://ivy-test.oss-cn-shenzhen.aliyuncs.com/music/%E6%9C%80%E5%88%9D%E7%9A%84%E6%A2%A6%E6%83%B3%20-%20%E8%8C%83%E7%8E%AE%E7%90%AA.mp3?security-token=CAISlQJ1q6Ft5B2yfSjIr4n2LdLZmo1KgZGAM1Hi10QjPuJbqpeaizz2IHBNdHduAuwWtf80mW1X6foYlqJ4T55IQ1Dza8J148zJdIJd7c6T1fau5Jko1beXewHKeSOZsebWZ%2BLmNqS%2FHt6md1HDkAJq3LL%2Bbk%2FMdle5MJqP%2B%2FEFA9MMRVv6F3kkYu1bPQx%2FssQXGGLMPPK2SH7Qj3HXEVBjt3gb6wZ24r%2FtxdaHuFiMzg%2B46JdM%2BN%2BgesD7P5E3bMsuCofk5oEsKPqdihw3wgNR6aJ7gJZD%2FTr6pdyHCzFTmU7WarqKrIYzfFQiPfVnRPEY9uKPnPl5q%2FHVkJ%2Fs1xFOMOdaXiLSXom8x9HeH%2BekJluivi134JemGoABBjcVUpbGwwymCU5aOD%2F%2B6tJ0ckaemIETj86tJaoXdXTPJzRu%2FPTHHem5qHR7c9%2FWSwR%2FBuid1AYwWzxypUPm3aoqJFby030k9mx0Qp7TgNbmTmCu1nRdhY%2BAWlDadc0XYJe68BDtOnOwTjd1PDQuRjFXD9rWzMz%2F2LINOlUk%2FBw%3D&OSSAccessKeyId=STS.NJCfhmwRk6Sk1wS3Dv2mwER1i&Expires=1539252582&Signature=KXKjYXXOF9qLYQfnEY7LSja486s%3D")
    @GET
    Call<ResponseBody> downloadLargeFile(@Url String url);

    // @Streaming 这个注解必须添加，否则文件全部写入内存，文件过大会造成内存溢出
}
