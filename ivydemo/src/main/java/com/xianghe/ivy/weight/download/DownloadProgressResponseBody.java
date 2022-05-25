package com.xianghe.ivy.weight.download;


import com.xianghe.ivy.weight.download.breakpoint.DownloadProgressListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 作者：created by huangjiang on 2018/10/11  下午5:26
 * 邮箱：504512336@qq.com
 * 描述：下载的数据处理
 */
public class DownloadProgressResponseBody extends ResponseBody {
    private final ResponseBody responseBody;
    private BufferedSource bufferedSource;
    private int code;
    private DownloadProgressListener mDownloadProgressListener;

    public DownloadProgressResponseBody(ResponseBody responseBody, int code) {
        this.responseBody = responseBody;
        this.code = code;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
//                DownloadProgressEvent downloadProgressEvent = new DownloadProgressEvent();
//                downloadProgressEvent.setBytesRead(totalBytesRead);
//                downloadProgressEvent.setContentLength(responseBody.contentLength());
//                downloadProgressEvent.setDone(bytesRead == -1);
//                downloadProgressEvent.setCode(code);
                if (mDownloadProgressListener != null) {
                    mDownloadProgressListener.progress(totalBytesRead, responseBody.contentLength(), bytesRead == -1,code);
                }
                return bytesRead;
            }
        };
    }


    public DownloadProgressResponseBody setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.mDownloadProgressListener = downloadProgressListener;
        return this;
    }
}
