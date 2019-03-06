package io.taptalk.TapTalk.Interface;

import android.graphics.Bitmap;
import android.net.Uri;

public interface TapTalkDownloadInterface {
    void onImageDownloadProcessFinished(String localID, Bitmap bitmap);
    void onThumbnailDownloaded(String fileID, Bitmap bitmap);
    void onFileDownloadProcessFinished(String localID, Uri fileUri);
    void onDownloadFailed(String localID);
}
