package io.taptalk.TapTalk.API.Interceptor;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import io.taptalk.TapTalk.API.ResponseBody.TAPDownloadProgressResponseBody;
import io.taptalk.TapTalk.Interface.TapTalkDownloadProgressInterface;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPDownloadRequestInterceptor implements Interceptor {
    public static final String TAG = TAPDownloadRequestInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        return chain.proceed(original).newBuilder()
                .body(new TAPDownloadProgressResponseBody(chain.proceed(original).body(),
                        original.header("localID"),
                        new TapTalkDownloadProgressInterface() {
                            @Override
                            public void update(int percentage, String localID) {
                                TAPFileDownloadManager.getInstance().addDownloadProgressMap(localID, percentage);
                                Intent intent = new Intent(DownloadProgressLoading);
                                intent.putExtra(DownloadLocalID, localID);
                                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                            }

                            @Override
                            public void finish(String localID) {
                                Intent intent = new Intent(DownloadFinish);
                                intent.putExtra(DownloadLocalID, localID);
                                LocalBroadcastManager.getInstance(appContext).sendBroadcast(intent);
                            }
                        })).build();
    }
}
