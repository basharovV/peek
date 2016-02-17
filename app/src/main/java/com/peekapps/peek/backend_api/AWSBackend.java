package com.peekapps.peek.backend_api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSConfiguration;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.content.ContentItem;
import com.amazonaws.mobile.content.ContentProgressListener;
import com.amazonaws.mobile.content.UserFileManager;

import java.io.File;

/**
 * Created by Slav on 02/02/2016.
 */
public class AWSBackend implements BackendAdapter{

    private final static String S3BUCKET = AWSConfiguration.AMAZON_S3_USER_FILES_BUCKET;
    private final static String S3PREFIX = "public/";

    private static final String TAG = "AWSBackend";
    private static AWSBackend instance;

    private PhotoUploadProgressListener uploadProgressListener;
    private UserFileManager userFileManager;

    public static AWSBackend getInstance(Context context) {
        if (instance == null) {
            instance = new AWSBackend(context);
        }
        return instance;
    }

    private AWSBackend(Context context) {

        AWSMobileClient.defaultMobileClient()
                .createUserFileManager(S3BUCKET, S3PREFIX,
                        new UserFileManager.BuilderResultHandler() {
                            @Override
                            public void onComplete(final UserFileManager userFileManager) {
                                // Init
                                AWSBackend.this.userFileManager = userFileManager;
                                Log.d(TAG, "UserFileManager built");
                            }
                        });
        uploadProgressListener = new PhotoUploadProgressListener(context);
    }

    public void uploadFile(File file, final FileUploadCallback callback) {
        userFileManager.uploadContent(file, file.getPath(), new ContentProgressListener() {
            @Override
            public void onSuccess(ContentItem contentItem) {
                callback.onSuccess();
            }

            @Override
            public void onProgressUpdate(String filePath, boolean isWaiting, long bytesCurrent, long bytesTotal) {
                int percent = (int) (bytesCurrent / bytesTotal) * 100;
                callback.onProgress(percent);
            }

            @Override
            public void onError(String filePath, Exception ex) {
                callback.onError(ex.getMessage());
            }
        });
    }

    public File downloadFile(String fileID) {
        return null;
    }

    private class PhotoUploadProgressListener implements ContentProgressListener {

        private Context context;

        public PhotoUploadProgressListener(Context context) {
            this.context = context;
        }

        @Override
        public void onSuccess(ContentItem contentItem) {
            Toast.makeText(context, "Photo uploaded", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProgressUpdate(String filePath, boolean isWaiting, long bytesCurrent, long bytesTotal) {

        }

        @Override
        public void onError(String filePath, Exception ex) {

        }
    }
}
