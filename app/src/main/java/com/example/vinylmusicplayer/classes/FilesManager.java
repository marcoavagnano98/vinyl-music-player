package com.example.vinylmusicplayer.classes;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class FilesManager {


    private String[] goodExtensions;

    private Map<String, File> mapAudioFiles;
    private final long START_INDEX = 1000;
    RandomString randomString=new RandomString();


    public FilesManager() {
        goodExtensions = new String[]{".mp3",".opus", ".ogg", ".wav", ".pcm",".3gp",".mp4", ".m4a",".aac",".amr",".flac",".mkv"};
        mapAudioFiles = new HashMap<>();
    }

    public Set<String> allSongsInFolder(String folder) {
        File directory = new File(Environment.getExternalStorageDirectory(), folder);
        FileFilter fileFilter = pathname -> {
            String fileName = pathname.getName();
            for (String extension : goodExtensions) {
                if (fileName.endsWith(extension)) {
                    return true;
                }
            }
            return false;
        };
        if (mapAudioFiles.isEmpty() && directory.exists() && directory.canRead()) {
            fillMap(directory.listFiles(fileFilter));
        }
        // Map<String, File> sortByNamef=sortByName();
        return mapAudioFiles.keySet();
    }

    public String truncateExtension(String name) {
        int lastOcc = name.lastIndexOf('.');
        if (lastOcc != -1) {
            name = name.substring(0, lastOcc);
        }
        return name;
    }

    private void fillMap(File[] files) {
        for (int i = 0; i < files.length; i++) {
            String path=files[i].getAbsolutePath();
            mapAudioFiles.put(path, files[i]);
        }
    }

    public File getAudioFIleById(String id) {
        return mapAudioFiles.get(id);
    }

    public static void deleteFile(Uri uri, Context context) {
        String path=uri.getPath();
        File file= new File(uri.getPath());
        if (file.exists()) {
            boolean deleted=file.delete();
          //  new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyFolder").delete();
//            contentResolver.delete(Uri.parse(path), null, null);

        }else{
            Toast.makeText(context, path.concat(" non esiste!!"),Toast.LENGTH_LONG).show();
        }
    }

}
