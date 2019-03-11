package io.taptalk.TapTalk.API.ResponseBody;

import java.io.IOException;

import javax.annotation.Nullable;

import io.taptalk.TapTalk.Interface.TapTalkDownloadProgressInterface;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class TAPDownloadProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final TapTalkDownloadProgressInterface progressListener;
    private final String localID;
    private BufferedSource bufferedSource;

    public TAPDownloadProgressResponseBody(ResponseBody responseBody, String localID, TapTalkDownloadProgressInterface progressListener) {
        this.responseBody = responseBody;
        this.progressListener = progressListener;
        this.localID = localID;
    }

    @Nullable
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

                if (bytesRead != -1) {
                    int percentage = (int) (100 * totalBytesRead / responseBody.contentLength());
                    progressListener.update(percentage, localID);
                } else progressListener.finish(localID);
                return bytesRead;
            }
        };
    }
}
