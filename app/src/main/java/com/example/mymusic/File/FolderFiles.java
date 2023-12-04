package com.example.mymusic.File;

import java.io.File;

public class FolderFiles {
    File file;
    String numberOfSong;

    public FolderFiles(File file, String numberOfSong) {
        this.file = file;
        this.numberOfSong = numberOfSong;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNumberOfSong() {
        return numberOfSong;
    }

    public void setNumberOfSong(String numberOfSong) {
        this.numberOfSong = numberOfSong;
    }
}
