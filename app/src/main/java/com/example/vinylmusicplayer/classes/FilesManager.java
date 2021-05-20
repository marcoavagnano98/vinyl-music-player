package com.example.vinylmusicplayer.classes;

import android.os.Environment;

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


    public FilesManager() {
        goodExtensions = new String[]{".mp3", ".ogg", ".wav", ".pcm"};
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
            long id = START_INDEX + i;
            mapAudioFiles.put(String.valueOf(id), files[i]);
        }
    }

    public File getAudioFIleById(String id) {
        return mapAudioFiles.get(id);
    }
}
