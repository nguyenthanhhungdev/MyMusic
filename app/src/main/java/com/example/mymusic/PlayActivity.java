package com.example.mymusic;

import static com.example.mymusic.MainActivity.musicFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.color.utilities.CorePalette;


public class PlayActivity extends AppCompatActivity {

    private ImageButton playButton, shuffleButton, previousButton, nextButton, repeatButton, backButton, menuButton;
    private TextView songNameTextView, artistTextView, durationPlayed, durationMax;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    private int position = -1;
    //    private static ArrayList<MusicFiles> listSongs;
    private static Uri uri;
    public static MediaPlayer mediaPlayer;
    private Thread nextPrevThread, playThread;
    private ImageView imageView;

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
        capNhatUI();
    }

    /**
     * runOnUiThread() là một phương thức trong các lớp Activity và View được sử dụng để đăng một tác vụ lên luồng chính.
     * Luồng chính là luồng chịu trách nhiệm cập nhật giao diện người dùng.
     *
     * Nếu bạn gọi runOnUiThread() từ một luồng không phải giao diện người dùng,
     * tác vụ sẽ được đăng lên luồng chính và sẽ được thực thi ngay khi luồng chính có sẵn.
     * Điều này có nghĩa là bạn có thể chắc chắn rằng giao diện người dùng sẽ được cập nhật một cách an toàn và nhất quán.
     * */

    private void capNhatUI() {
        PlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    durationPlayed.setText(formattedTimer(currentPosition));
                }

                if (String.valueOf(mediaPlayer.getDuration()/1000).equals(durationMax)) {
                    playButton.setImageResource(R.drawable.baseline_play_arrow);
                }
//                Sau 1 khoảng thời gian thì gửi luồng này tới luồng giao diện
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
        prevNextThreadBtn();
    }

    private void prevNextThreadBtn() {
        nextPrevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextButton.setOnClickListener(e -> nextBtnClicked());
                previousButton.setOnClickListener(e -> prevButtonClick());
            }
        };
        nextPrevThread.start();
    }

    private void nextBtnClicked() {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % (musicFiles.size() + 1));
            try {
                checkPosition(position);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getApplicationContext(), "Đã hết danh sách bài hát", Toast.LENGTH_SHORT).show();
            }
            MusicFiles musicFile = musicFiles.get(position);
            uri = Uri.parse(musicFile.getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songNameTextView.setText(musicFile.getTitle());
            artistTextView.setText(musicFile.getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            capNhatUI();
            playButton.setImageResource(R.drawable.baseline_play_arrow);
    }

    private void prevButtonClick() {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) % musicFiles.size());
            try {
                checkPosition(position);
            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getApplicationContext(), "Đã hết danh sách bài hát", Toast.LENGTH_SHORT).show();
                return;
            }
            MusicFiles musicFile = musicFiles.get(position);
            uri = Uri.parse(musicFile.getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            songNameTextView.setText(musicFile.getTitle());
            artistTextView.setText(musicFile.getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            capNhatUI();
            playButton.setImageResource(R.drawable.baseline_play_arrow);
            mediaPlayer.start();
    }

    private void checkPosition(int position) {
        if (position < 0 || position > musicFiles.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playButton.setOnClickListener(e -> playPauseBtnClicked());

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
        } else {
            playButton.setImageResource(R.drawable.baseline_pause);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        capNhatUI();
    }

    private String formattedTimer(int currentPosition) {
        String totalOut;
        String totalNew;
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
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
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
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(imageView);
            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null) {
                        ImageView viewGradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout relativeLayout = findViewById(R.id.playActivity);
                        viewGradient.setBackgroundResource(R.drawable.gradient_bg);
                        relativeLayout.setBackgroundResource(R.color.DenTrongSuot);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        viewGradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        relativeLayout.setBackground(gradientDrawable1);

                        songNameTextView.setTextColor(swatch.getTitleTextColor());
                        artistTextView.setTextColor(swatch.getBodyTextColor());
                    } else {
                        ImageView viewGradient = findViewById(R.id.imageViewGradient);
                        RelativeLayout relativeLayout = findViewById(R.id.playActivity);
                        viewGradient.setBackgroundResource(R.drawable.gradient_bg);
                        relativeLayout.setBackgroundResource(R.color.DenTrongSuot);
                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff00000, 0x0000000});
                        viewGradient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff00000, 0xff00000});
                        relativeLayout.setBackground(gradientDrawable1);

                        songNameTextView.setTextColor(Color.WHITE);
                        artistTextView.setTextColor(Color.DKGRAY);

                    }
                }
            });
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.img)
                    .into(imageView);
        }
    }

}