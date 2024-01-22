package com.chacha.igexperiments;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileHelper {

    private final String TEMP_DIR_NAME = "TempAPKs";
    private String tempFilePath;

    public FileHelper() {
    }

    public String getTempFilePath() {
        return tempFilePath;
    }

    public void handleSelectedApkFile(Context context, Uri selectedFileUri) {
        this.tempFilePath = copyFileToTempDir(context, selectedFileUri);

        if (tempFilePath != null) {
            Toast.makeText(context, "Copied file to: " + tempFilePath, Toast.LENGTH_LONG).show();
            // Continue with your logic, using tempFilePath
        } else {
            Toast.makeText(context, "Failed to copy file to temp directory", Toast.LENGTH_SHORT).show();
        }
    }

    private String copyFileToTempDir(Context context, Uri sourceUri) {
        try {
            File tempDir = new File(context.getCacheDir(), TEMP_DIR_NAME);
            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            String fileName = getFileNameFromUri(context, sourceUri);
            String tempFilePath = tempDir.getAbsolutePath() + File.separator + fileName;

            try (InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
                 OutputStream outputStream = new FileOutputStream(tempFilePath)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return tempFilePath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getFileNameFromUri(Context context, Uri uri) {
        String fileName = null;
        String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            fileName = new File(uri.getPath()).getName();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                    fileName = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (fileName == null) {
            fileName = "temp_file";
        }

        return fileName;
    }
}
