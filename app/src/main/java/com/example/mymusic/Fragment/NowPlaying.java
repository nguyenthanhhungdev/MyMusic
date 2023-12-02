package com.example.mymusic.Fragment;

import static com.example.mymusic.Activity.MainActivity.musicFiles;
import static com.example.mymusic.Adapter.MusicAdapter.getMediaPlayer;
import static com.example.mymusic.Adapter.MusicAdapter.setMediaPlayer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.mymusic.Adapter.MusicAdapter;
import com.example.mymusic.File.MusicFiles;
import com.example.mymusic.R;

import java.io.IOException;

public class NowPlaying extends Fragment {
    private TextView songNameTextView, otherInforTextView;
    private ImageView imageView;
    private ImageButton imageButtonPlay;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        songNameTextView = view.findViewById(R.id.songNamePlaying);
        songNameTextView.setSelected(true);
        otherInforTextView = view.findViewById(R.id.otherInforTextView);
        otherInforTextView.setSelected(true);
        progressBar = view.findViewById(R.id.thanhProgress);
        imageView = view.findViewById(R.id.imageViewPlaying);
        imageButtonPlay = view.findViewById(R.id.imageButtonPlay);
        relativeLayout = view.findViewById(R.id.relativeLayoutNowPlaying);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onResume() {
        super.onResume();
        initComponent();

        imageButtonPlay.setOnClickListener(e -> {
            if (MusicAdapter.getMediaPlayer().isPlaying()) {
                imageButtonPlay.setImageResource(R.drawable.baseline_play_arrow);
                MusicAdapter.getMediaPlayer().pause();
            } else {
                imageButtonPlay.setImageResource(R.drawable.baseline_pause);
                MusicAdapter.getMediaPlayer().start();
            }
        });
        Handler handler = new Handler();
        this.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (MusicAdapter.getMediaPlayer() != null) {
                    int currentPostion = MusicAdapter.getMediaPlayer().getCurrentPosition();
                    progressBar.setProgress(currentPostion);
                }
                handler.postDelayed(this, 100);
            }
        });


        songNameTextView.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float endX = event.getX();
                        float endY = event.getY();
                        float deltaX = endX - startX;
                        float deltaY = endY - startY;

                        if (Math.abs(deltaX) > Math.abs(deltaY)) {
                            // Xử lý sự kiện vuốt ngang ở đây
                            getMediaPlayer().stop();
                            getMediaPlayer().reset();
                            getMediaPlayer().release();
                            int newPosition;
                            if (deltaX > 0) { // Vuốt sang phải
                                newPosition = MusicAdapter.getPlayingPosition() - 1;
                            } else {
                                // Vuốt sang trái
                                newPosition = MusicAdapter.getPlayingPosition() + 1;
                            }
                            playNew(newPosition);
                            return true; // Đã xử lý sự kiện vuốt ngang
                        }
                        break;
                }
                return false; // Không xử lý sự kiện vuốt ngang
            }
        });
    }

    private void initComponent() {
        String title = musicFiles.get(MusicAdapter.getPlayingPosition()).getTitle();
        String album = musicFiles.get(MusicAdapter.getPlayingPosition()).getAlbum();
        String artist = musicFiles.get(MusicAdapter.getPlayingPosition()).getArtist();
        String otherInfor = artist.equals("<unknown>") ? album : artist;
        songNameTextView.setText(title);
        otherInforTextView.setText(otherInfor);

        byte[] image;
        try {
            image = getImage(musicFiles.get(MusicAdapter.getPlayingPosition()).getPath());
            if (image != null) {
                Glide.with(requireContext()).asBitmap()
                        .load(image)
                        .into(imageView);
            } else {
                Glide.with(requireContext())
                        .load(R.drawable.img)
                        .into(imageView);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int durationMax = MusicAdapter.getMediaPlayer().getDuration();
        progressBar.setMax(durationMax);

        assert image != null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        Palette.from(bitmap).generate(palette -> {
            assert palette != null;
            Palette.Swatch swatch = palette.getDominantSwatch();
            if (swatch != null){
                GradientDrawable gradientDrawable1 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[]{swatch.getRgb(), swatch.getRgb()});
                relativeLayout.setBackground(gradientDrawable1);
            }
        });
    }

    private void playNew(int newPosition) {
        MusicFiles musicFile = musicFiles.get(newPosition);
        Uri uri = Uri.parse(musicFile.getPath());
        setMediaPlayer(MediaPlayer.create(getContext(), uri));
        MusicAdapter.setPlayingPosition(newPosition);
        getMediaPlayer().start();
        initComponent();
    }

    public byte[] getImage(String path) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();
        retriever.close();
        return image;
    }
}
