package com.example.mymusic.Fragment;

import static com.example.mymusic.Activity.MainActivity.musicFiles;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.Adapter.MusicAdapter;
import com.example.mymusic.R;

import java.io.IOException;

public class NowPlaying extends Fragment {
    private TextView songNameTextView, otherInforTextView;
    private ImageView imageView;
    private ImageButton imageButtonPlay;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        return view;
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
    }

    @Override
    public void onResume() {
        super.onResume();
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
                Glide.with(getContext()).asBitmap()
                        .load(image)
                        .into(imageView);
            } else {
                Glide.with(getContext())
                        .load(R.drawable.img)
                        .into(imageView);
            }

            imageButtonPlay.setOnClickListener(e -> {
                if (MusicAdapter.getMediaPlayer().isPlaying()) {
                    imageButtonPlay.setImageResource(R.drawable.baseline_play_arrow);
                    MusicAdapter.getMediaPlayer().pause();
                } else {
                    imageButtonPlay.setImageResource(R.drawable.baseline_pause);
                    MusicAdapter.getMediaPlayer().start();
                }
            });

            int durationMax = MusicAdapter.getMediaPlayer().getDuration();
            progressBar.setMax(durationMax);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
