package com.example.mymusic.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.example.mymusic.File.FolderFiles;
import com.example.mymusic.File.MusicFiles;
import com.example.mymusic.Fragment.MySongFragment;
import com.example.mymusic.R;
import com.example.mymusic.Fragment.FolderFragment;
import com.example.mymusic.Adapter.ViewPaperAdapter;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<MusicFiles> musicFiles;
    public static ArrayList<FolderFiles> folders = new ArrayList<>();
    static boolean shuffleBoolean = false, repeatBoolean = false;
    private static final int REQUESTCODE = 1;
    private final File rootDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath());

    public static FragmentManager fragmentManager;
    {
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestFileAccess();
    }

    public void initViewPaper() {
        ViewPager viewPager = findViewById(R.id.viewPaper);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayOut);
        ViewPaperAdapter viewPaperAdapter = new ViewPaperAdapter(getSupportFragmentManager());
        viewPaperAdapter.addFragment(new FolderFragment(), "Folder");
        viewPaperAdapter.addFragment(new MySongFragment(), "My Songs");
        viewPager.setAdapter(viewPaperAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void requestFileAccess() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Ứng dụng chưa được cấp quyền

            // Yêu cầu người dùng cấp quyền
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_AUDIO}, REQUESTCODE);
        } else {
            // Ứng dụng đã được cấp quyền
            // Thực hiện các tác vụ cần quyền
            musicFiles = getAllAudio(this);
            folders = getAllFiles(rootDirectory);
            initViewPaper();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUESTCODE) {
            // Xử lý kết quả của việc yêu cầu quyền

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Người dùng đã cấp quyền
                // Thực hiện các tác vụ cần quyền
                musicFiles = getAllAudio(this);
                folders = getAllFiles(rootDirectory);
                initViewPaper();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE);
            }
        }
    }

    public ArrayList<MusicFiles> getAllAudio(Context context) {
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] queryFeilds = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA, //path1
        };
        String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " DESC";
        Cursor cursor = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cursor = context.getContentResolver().query(uri, queryFeilds, null, null, sortOrder);
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String artist = cursor.getString(3);
                String path = cursor.getString(4);

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration);
                tempAudioList.add(musicFiles);
            }
            cursor.close();
        }
            return tempAudioList;
    }

    private ArrayList<FolderFiles> getAllFiles(File directory) {
        ArrayList<FolderFiles> audioFiles = new ArrayList<>();
        int numberOfFile = 0;
        // Kiểm tra nếu thư mục có thể đọc được
        if (directory.canRead()) {
            // Lấy danh sách các tệp và thư mục con trong thư mục hiện tại
            File[] files = directory.listFiles();

            if (files != null && files.length >= 1) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // Nếu là thư mục, gọi đệ quy để lấy tất cả các tệp âm thanh trong thư mục con
                        audioFiles.addAll(getAllFiles(file));
                    } else {
                        // Kiểm tra và lọc các tệp âm thanh
                        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {
                            /**
                             * Nếu là thư mục thì gọi đệ quy tới thư mục con
                             * Nếu không thì kiểm tra xem có phải là tệp âm thanh hay không
                             * Nếu phải thì:
                             * khi này directory là thư mục chứa tệp do gọi đệ quy
                             * Thêm directory vào trong danh sách
                             * */
                            numberOfFile++;
                        }
                    }
                }
                FolderFiles folderFiles = new FolderFiles(directory, String.valueOf(numberOfFile));
                audioFiles.add(folderFiles);
            }
        }

        return audioFiles;
    }
}
