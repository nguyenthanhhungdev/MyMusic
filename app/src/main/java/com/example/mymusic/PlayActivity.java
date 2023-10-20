package com.example.mymusic;

import static com.example.mymusic.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private ImageButton playButton, shuffleButton, previousButton, nextButton, repeatButton, backButton, menuButton;
    private TextView songNameTextView, artistTextView, durationPlayed, durationMax;
    private ImageView imageView;
    private SeekBar seekBar;
    private Handler handler = new Handler();

    private int position = -1;
    //    private static ArrayList<MusicFiles> listSongs;
    private static Uri uri;
    public static MediaPlayer mediaPlayer;
    private Thread playThread, pauseThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initComponent();

        getIntentMethod();

        songNameTextView.setText(musicFiles.get(position).getTitle());
        artistTextView.setText(musicFiles.get(position).getArtist());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    durationPlayed.setText(formattedTimer(currentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });

    }

    /**
     * Khởi tạo các luồng trong onResume
     * Khi người dùng duyệt tới activity thì mới tạo ra các luồng
     * Dùng các luồng này để thuận tiện cho các tiến trình song song nhau với luồng giao diện chính
     * */

    @Override
    protected void onResume() {
        super.onResume();
        playThreadBtn();
        prevThreadBtn();
        nextThreadBtn();
    }

    private void nextThreadBtn() {
    }

    private void prevThreadBtn() {
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playButton.setOnClickListener(e -> {
                    playPauseBtnClicked();
                });
            }
        };
        playThread.start();
    }

  /**  Đang play khi gọi tới activity_play,giao diện đang ở nút pause
        khi người dùng nhấn vào nút pause thì sẽ chuyển sang nút play và dừng lại
        sau đó tùy chỉnh lại thanh seekbar
   */
    private void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying()) {
            playButton.setImageResource(R.drawable.baseline_play_arrow);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playButton.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(currentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTimer(int currentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(currentPosition % 60);
        String minutes = String.valueOf(currentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if ((seconds.length() == 1)) {
            return totalNew;
        }
        return totalOut;
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        if (musicFiles != null) {
            playButton.setImageResource(R.drawable.baseline_pause);
            uri = Uri.parse(musicFiles.get(position).getPath());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);
    }

    public void initComponent() {
        playButton = findViewById(R.id.playButton);
        shuffleButton = findViewById(R.id.shuffleButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        repeatButton = findViewById(R.id.repeatButton);

        songNameTextView = findViewById(R.id.songNameTextView);
        artistTextView = findViewById(R.id.artistTextView);
        imageView = findViewById(R.id.imageView);

        backButton = findViewById(R.id.backButton);
        menuButton = findViewById(R.id.menuButton);

        seekBar = findViewById(R.id.seekBar);
        durationPlayed = findViewById(R.id.durationPlay);
        durationMax = findViewById(R.id.durationTotal);

    }

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(musicFiles.get(position).getDuration()) / 1000;
        durationMax.setText(formattedTimer(durationTotal));
    }

    public void audioPlayer(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Không thể phát âm thành", Toast.LENGTH_SHORT).show();
        }
    }
}