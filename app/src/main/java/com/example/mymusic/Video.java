package com.example.mymusic;

import android.net.Uri;

public class Video {
    private Uri path;
    private String name;
    private int duration;
    private int size;

    public Video(Uri uri, String name, int duration, int size) {
        this.path = uri;
        this.name = name;
        this.duration = duration;
        this.size = size;
    }

    public Uri getPath() {
        return path;
    }

    public void setPath(Uri path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
