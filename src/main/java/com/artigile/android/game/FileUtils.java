package com.artigile.android.game;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ivanbahdanau
 */
public class FileUtils {

    private FileUtils() {
    }


    public static File getOutputMediaFile() {
        File mediaStorageDir = getMagicMazeVideosDir();
        if (mediaStorageDir == null) return null;
        // Create a media file name
        File mediaFile;
        int i = 1;

        do {
            String filePath = mediaStorageDir.getPath() + File.separator + "/" + SimpleDateFormat.getDateInstance().format(new Date()) + "-" + i + ".mpeg";
            filePath = filePath.replace(" ", "_").replace(",", "_").replace(":", "_");
            mediaFile = new File(filePath);
            i++;
        }
        while (mediaFile.exists());
        return mediaFile;
    }


    public static File getMagicMazeVideosDir() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "MagicMaze");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MagicMaze", "failed to create directory");
                return null;
            }
        }
        return mediaStorageDir;
    }

}