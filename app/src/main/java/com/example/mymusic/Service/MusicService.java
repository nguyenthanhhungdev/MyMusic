package com.example.mymusic.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.mymusic.MainActivity;
import com.example.mymusic.MusicAdapter;

import java.security.Provider;
import java.util.List;
import java.util.Map;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startForeground() {
        // Before starting the service as foreground check that the app has the
        // appropriate runtime permissions. In this case, verify that the user
        // has granted the CAMERA permission.
        int filePermission =
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO);
        if (filePermission == PackageManager.PERMISSION_DENIED) {
            // Without read file permissions the service cannot run in the
            // foreground. Consider informing user or updating your app UI if
            // visible.
            stopSelf();
            return;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = MusicAdapter.getMediaPlayer();
    }
}