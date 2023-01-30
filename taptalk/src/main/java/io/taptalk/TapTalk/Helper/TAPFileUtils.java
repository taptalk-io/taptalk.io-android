package io.taptalk.TapTalk.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.R;
import okio.BufferedSink;
import okio.Okio;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ClientErrorCodes.ERROR_CODE_OTHERS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadedFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.FILEPROVIDER_AUTHORITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Helper.TapTalk.appContext;

public class TAPFileUtils {

    private static final String DOCUMENTS_DIR = "documents";

    public static String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String encodedMessage) {
        byte[] imageBytes = Base64.decode(encodedMessage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    public static String encodeToBase64(Uri imageUri, int maxSize, Activity activity) {
        final InputStream imageStream;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(new File(imageUri.getPath()).getAbsolutePath(), options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;

            options.inJustDecodeBounds = false;
            if (imageHeight > imageWidth) {
                options.inSampleSize = imageHeight / 2000;
            } else {
                options.inSampleSize = imageWidth / 2000;
            }
            imageStream = TapTalk.appContext.getContentResolver().openInputStream(imageUri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, options);
            final Bitmap resizedImage = scaleBitmapDown(getCorrectedImage(selectedImage, imageUri, activity), maxSize);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedImage.compress(Bitmap.CompressFormat.WEBP, 100, byteArrayOutputStream);
            String encoded = "data:image/webp;base64," + Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            return encoded.replace("\n", "");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getImageOrientation(String imagePath) {
        ExifInterface exif;
        try {
            if (imagePath != null) {
                exif = new ExifInterface(imagePath);
            } else {
                return 0;
            }
        } catch (IOException e) {
            return 0;
        }
        return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    public static String getMimeTypeFromUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().getType(uri);
        } catch (Exception e) {
            return "";
        }
    }

    public static String getFilePath(final Context context, final Uri uri) {
        // DocumentProvider
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//                return getDataColumn(context, contentUri, null, null);

                String id = DocumentsContract.getDocumentId(uri);

                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4);
                }
                id = id.replaceAll("[^\\d.]", "");


                String[] contentUriPrefixesToTry = new String[]{
                        "content://downloads/public_downloads",
                        "content://downloads/my_downloads",
                        "content://downloads/all_downloads"
                };

                for (String contentUriPrefix : contentUriPrefixesToTry) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse(contentUriPrefix), Long.valueOf(id));
                    try {
                        String path = getDataColumn(context, contentUri, null, null);
                        if (path != null) {
                            return path;
                        }
                    } catch (Exception e) {
                    }
                }

                // Path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
                String fileName = getFileName(context, uri);
                File cacheDir = getDocumentCacheDir(context);
                File file = generateFileName(fileName, cacheDir);
                String destinationPath = null;
                if (file != null) {
                    destinationPath = file.getAbsolutePath();
                    saveFileFromUri(context, uri, destinationPath);
                }
                return destinationPath;
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
            // Google Drive
            else if (isGoogleDriveUri(uri)) {
                try {
                    return saveFileIntoExternalStorageByUri(context, uri).getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            } else if (isFileProviderUri(uri)) {
                // FIXME: 23 January 2019
                for (String instanceKey : TapTalk.getInstanceKeys()) {
                    String path = TAPFileDownloadManager.getInstance(instanceKey).getFileProviderPath(uri);
                    if (null != path && !path.isEmpty()) {
                        return path;
                    }
                }
                return null;
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }

    private static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private static Bitmap getCorrectedImage(Bitmap image, Uri imageUri, Activity activity) {
        ExifInterface exif;
        try {
            if (getFilePath(activity, imageUri) != null) {
                exif = new ExifInterface(getFilePath(activity, imageUri));
            } else {
                return rotateBitmap(image, 0);
            }
        } catch (IOException e) {
            return rotateBitmap(image, 0);
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        return rotateBitmap(image, orientation);
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    private static boolean isFileProviderUri(Uri uri) {
        return FILEPROVIDER_AUTHORITY.equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        final String column = "_data";
        final String[] projection = {
                column
        };

        try (Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
//                final int index = cursor.getColumnIndexOrThrow(column);
                final int index = cursor.getColumnIndex(column);
                if (index >= 0) {
                    return cursor.getString(index);
                } else {
                    // column '_data' does not exist
                    File file = createTemporaryCachedFile(context, uri);
                    String destinationPath = "";
                    if (file != null) {
                        destinationPath = file.getAbsolutePath();
                    }
                    return destinationPath;
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            // Path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
            String fileName = getFileName(context, uri);
            File cacheDir = getShareCacheDir(context);
            File file = new File(cacheDir, fileName);
            String destinationPath = null;
            if (file != null) {
                destinationPath = file.getAbsolutePath();
                saveFileFromUri(context, uri, destinationPath);
            }

            return destinationPath;
        }
        return null;
    }

    public  static File getDocumentCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), DOCUMENTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getShareCacheDir(@NonNull Context context) {
        File dir = new File(context.getCacheDir(), "share");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    @Nullable
    private static File generateFileName(@Nullable String name, File directory) {
        if (name == null) {
            return null;
        }

        File file = new File(directory, name);

        if (file.exists()) {
            String fileName = name;
            String extension = "";
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex);
                extension = name.substring(dotIndex);
            }

            int index = 0;

            while (file.exists()) {
                index++;
                name = fileName + '(' + index + ')' + extension;
                file = new File(directory, name);
            }
        }

        try {
            if (!file.createNewFile()) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }

        return file;
    }

    private static void saveFileFromUri(Context context, Uri uri, String destinationPath) {
        InputStream is = null;
        BufferedOutputStream bos = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            bos = new BufferedOutputStream(new FileOutputStream(destinationPath, false));
            byte[] buf = new byte[1024];
            is.read(buf);
            do {
                bos.write(buf);
            } while (is.read(buf) != -1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static File saveFileIntoExternalStorageByUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        int originalSize = inputStream.available();

        BufferedInputStream bis;
        BufferedOutputStream bos;
        String fileName = getFileName(context, uri);
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + TapTalk.appContext.getString(R.string.app_name) + "/Videos");
        dir.mkdirs();
        File file = new File(dir, fileName);
        bis = new BufferedInputStream(inputStream);
        bos = new BufferedOutputStream(new FileOutputStream(file, false));

        byte[] buf = new byte[originalSize];
        bis.read(buf);
        do {
            bos.write(buf);
        } while (bis.read(buf) != -1);

        bos.flush();
        bos.close();
        bis.close();

        return file;
    }

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(Math.max(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME), 0));
                    if (result == null || result.isEmpty()) {
                        result = cursor.getString(Math.max(cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE), 0));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = uri.getPath();
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static File renameDuplicateFile(File file) {
        while (file.exists() && !file.isDirectory()) {
            String path = file.getAbsolutePath();
            StringBuilder sb = new StringBuilder(path);
            try {
                // File name already contains duplicate number (2), (3), etc.
                int duplicateNumberStartIndex = sb.lastIndexOf("(");
                int duplicateNumberEndIndex = sb.lastIndexOf(")");
                int duplicateNumber = Integer.valueOf(sb.substring(duplicateNumberStartIndex + 1, duplicateNumberEndIndex)) + 1;
                sb.replace(duplicateNumberStartIndex, duplicateNumberEndIndex + 1, "(" + duplicateNumber + ")");
            } catch (Exception e) {
                sb.insert(sb.lastIndexOf("."), " (2)");
            }
            file = new File(sb.toString());
        }
        return file;
    }

    public static Uri parseFileUri(Uri uri) {
        String uriString = uri.toString();
        if (uriString.startsWith("file://")) {
            Uri fileUri = Uri.fromFile(new File(uriString.substring(7)));
            return Uri.parse(String.valueOf(fileUri));
        }
        return uri;
    }

    public static File createTemporaryCachedFile(Context context, Uri uri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
            InputStream input = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            File tempFile = new File(context.getCacheDir(), TAPFileUtils.getFileName(context, uri));
            FileOutputStream output = new FileOutputStream(tempFile);

            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
