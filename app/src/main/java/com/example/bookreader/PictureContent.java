package com.example.bookreader;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureContent {

    static final List<PictureItem> ITEMS = new ArrayList<>();

    public static void loadSavedImages(File dir) {
        ITEMS.clear();
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    loadImage(file);
                }
            }
        }
    }

    private static String getDateFromUri(Uri uri){
        String[] split = uri.getPath().split("_");
        String fileName = split[1];
        int length = fileName.length();
        fileName = fileName.substring(0,4) + "-" + fileName.substring(4,6)+ "-" + fileName.substring(6,length);
        return fileName;
    }

    public static void loadImage(File file) {
        PictureItem newItem = new PictureItem();
        newItem.uri = Uri.fromFile(file);
        newItem.date = getDateFromUri(newItem.uri);
        addItem(newItem);
    }

    private static void addItem(PictureItem item) {
        ITEMS.add(0, item);
    }

}
