package com.example.mymusic;

import static com.example.mymusic.MainActivity.musicFiles;
import static com.example.mymusic.MainActivity.repeatBoolean;
import static com.example.mymusic.MainActivity.shuffleBoolean;
import static com.example.mymusic.MusicAdapter.getMediaPlayer;
import static com.example.mymusic.MusicAdapter.setMediaPlayer;

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

import java.util.Random;


public class PlayActivity extends AppCompatActivity {

    private ImageButton playButton, shuffleButton, previousButton, nextButton, repeatButton, backButton, menuButton;
    private TextView songNameTextView, artistTextView, durationPlayed, durationMax;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    private int position = -1;
    //    private static ArrayList<MusicFiles> listSongs;
    private Uri uri;
    //    private Thread nextPrevThread, playThread;
    private ImageView imageView;
    private int durationTotal;
    private static int oldPosition;

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
                if (getMediaPlayer() != null && fromUser) {
                    getMediaPlayer().seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        backButton.setOnClickListener(e -> {
            onBackPressed();
        });
        capNhatUI();
    }

    /**
     * runOnUiThread() là một phương thức trong các lớp Activity và View được sử dụng để đăng một tác vụ lên luồng chính.
     * Luồng chính là luồng chịu trách nhiệm cập nhật giao diện người dùng.
     * <p>
     * Nếu bạn gọi runOnUiThread() từ một luồng không phải giao diện người dùng,
     * tác vụ sẽ được đăng lên luồng chính và sẽ được thực thi ngay khi luồng chính có sẵn.
     * Điều này có nghĩa là bạn có thể chắc chắn rằng giao diện người dùng sẽ được cập nhật một cách an toàn và nhất quán.
     */

    private void capNhatUI() {
        PlayActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (getMediaPlayer() != null) {
                    int currentPosition = getMediaPlayer().getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    durationPlayed.setText(formattedTimer(currentPosition));
                    if (currentPosition == durationTotal) {
                        nextBtnClicked();
                        if (!repeatBoolean && (position + 1) == musicFiles.size()) {
                            nextBtnClicked();
                            getMediaPlayer().stop();
                            playButton.setImageResource(R.drawable.baseline_play_arrow);
                        }
                    }
                }
//                Sau 1 khoảng thời gian thì gửi luồng này tới luồng giao diện
                handler.postDelayed(this, 1000);
            }
        });

        shuffleButton.setOnClickListener(e -> {
            if (shuffleBoolean) {
                shuffleBoolean = false;
                shuffleButton.setImageResource(R.drawable.baseline_shuffle_off);
            } else {
                shuffleBoolean = true;
                shuffleButton.setImageResource(R.drawable.baseline_shuffle_on_24);
            }
        });

        repeatButton.setOnClickListener(e -> {
            if (repeatBoolean) {
                repeatBoolean = false;
                repeatButton.setImageResource(R.drawable.baseline_repeat_off);
            } else {
                repeatBoolean = true;
                repeatButton.setImageResource(R.drawable.baseline_repeat_on);
            }
        });
    }


    /**
     * Khởi tạo các luồng trong onResume (khi người dùng có thể nhìn thấy giao diện)
     * Khi người dùng duyệt tới activity thì mới tạo ra các luồng
     * Dùng các luồng này để thuận tiện cho các tiến trình song song nhau với luồng giao diện chính
     */

    @Override
    protected void onResume() {
        super.onResume();
        playThreadBtn();
        prevNextThreadBtn();
    }

    private void prevNextThreadBtn() {
        if (ThreadPlay.nextPrevThread == null) {
            ThreadPlay.nextPrevThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    nextButton.setOnClickListener(e -> nextBtnClicked());
                    previousButton.setOnClickListener(e -> prevButtonClick());
                }
            };
            ThreadPlay.nextPrevThread.start();
        }
    }

    private void nextBtnClicked() {
        if (getMediaPlayer().isPlaying()) {
            next();
            playButton.setImageResource(R.drawable.baseline_pause);
            getMediaPlayer().start();
        } else {
            next();
            playButton.setImageResource(R.drawable.baseline_play_arrow);
        }
    }

    private void next() {
        getMediaPlayer().stop();
        getMediaPlayer().release();

        if (shuffleBoolean && !repeatBoolean) {
            position = getRandom(musicFiles.size() - 1);
        } else if (!shuffleBoolean && !repeatBoolean) {
            position = ((position + 1) % musicFiles.size());
        }
        try {
            checkPosition(position);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(), "Đã hết danh sách bài hát", Toast.LENGTH_SHORT).show();
        }

        MusicFiles musicFile = musicFiles.get(position);
        uri = Uri.parse(musicFile.getPath());
        setMediaPlayer(MediaPlayer.create(getApplicationContext(), uri));
        metaData(uri);
        songNameTextView.setText(musicFile.getTitle());
        artistTextView.setText(musicFile.getArtist());
        seekBar.setMax(getMediaPlayer().getDuration() / 1000);
        capNhatUI();
    }

    private int getRandom(int i) {
        // Tạo một đối tượng Random
        Random random = new Random();

        // Sử dụng nextInt để tạo số ngẫu nhiên từ 0 đến i (bao gồm cả 0 và i)
        int randomNumber = random.nextInt(i + 1);

        return randomNumber;
    }

    private void prevButtonClick() {
        if (getMediaPlayer().isPlaying()) {
            prev();
            playButton.setImageResource(R.drawable.baseline_pause);
            getMediaPlayer().start();
        } else {
            prev();
            playButton.setImageResource(R.drawable.baseline_play_arrow);
        }
    }

    private void prev() {
        getMediaPlayer().stop();
        getMediaPlayer().release();
        position = (position - 1) < 0 ? (musicFiles.size() - 1) : (position - 1);
        try {
            checkPosition(position);
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(getApplicationContext(), "Đã hết danh sách bài hát", Toast.LENGTH_SHORT).show();
            return;
        }
        MusicFiles musicFile = musicFiles.get(position);
        uri = Uri.parse(musicFile.getPath());
        setMediaPlayer(MediaPlayer.create(getApplicationContext(), uri));
        metaData(uri);
        songNameTextView.setText(musicFile.getTitle());
        artistTextView.setText(musicFile.getArtist());
        seekBar.setMax(getMediaPlayer().getDuration() / 1000);
        capNhatUI();
    }

    private void checkPosition(int position) {
        if (position < 0 || position > musicFiles.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private void playThreadBtn() {
        if (ThreadPlay.playThread == null) {
            ThreadPlay.playThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    playButton.setOnClickListener(e -> playPauseBtnClicked());

                }
            };
            ThreadPlay.playThread.start();
        }
    }

    /**
     * Đang play khi gọi tới activity_play,giao diện đang ở nút pause
     * khi người dùng nhấn vào nút pause thì sẽ chuyển sang nút play và dừng lại
     * sau đó tùy chỉnh lại thanh seekbar
     */
    private void playPauseBtnClicked() {
        if (getMediaPlayer().isPlaying()) {
            playButton.setImageResource(R.drawable.baseline_play_arrow);
            getMediaPlayer().pause();
        } else {
            playButton.setImageResource(R.drawable.baseline_pause);
            getMediaPlayer().start();
        }
        seekBar.setMax(getMediaPlayer().getDuration() / 1000);
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
        int currentPosition = getIntent().getIntExtra("playing_position", -1);
        if (musicFiles != null) {
            playButton.setImageResource(R.drawable.baseline_pause);
            uri = Uri.parse(musicFiles.get(position).getPath());
        }
        if ((getMediaPlayer() != null && currentPosition != position)/* || currentPosition != oldPosition*/) {
            getMediaPlayer().stop();
            getMediaPlayer().release();
        } else if (getMediaPlayer() == null) {
            setMediaPlayer(MediaPlayer.create(getApplicationContext(), uri));
        }
        getMediaPlayer().start();
        seekBar.setMax(getMediaPlayer().getDuration() / 1000);
        metaData(uri);
        oldPosition = currentPosition;
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
        durationTotal = Integer.parseInt(musicFiles.get(position).getDuration()) / 1000;
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
                    .load(R.drawable.img)
                    .into(imageView);
        }
    }

}