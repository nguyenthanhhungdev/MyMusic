package com.example.mymusic.Fragment;

import android.os.Bundle;
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

import com.example.mymusic.R;

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
        otherInforTextView = view.findViewById(R.id.otherInforTextView);
        progressBar = view.findViewById(R.id.thanhProgress);
        imageView = view.findViewById(R.id.imageViewPlaying);
    }


}
