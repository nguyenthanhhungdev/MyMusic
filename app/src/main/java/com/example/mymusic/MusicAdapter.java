package com.example.mymusic;


import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private final Context context; // biến để truy cập tới các dữ liệu của giao diện gọi tới
    private final ArrayList<MusicFiles> musicFiles;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private MyViewHolder oldViewHolder;
    private int playingPosition = -1;
    private boolean isPlaying;

    public MusicAdapter(Context context, ArrayList<MusicFiles> musicFiles) {
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        oldViewHolder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    //    Gán dữ liệu từ thành phần giao diện vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String title = musicFiles.get(position).getTitle();
        holder.getNameTextView().setText(title);

        String s_position = String.valueOf(position);
        holder.getNumTextView().setText(s_position);

//        holder.getButton().setTag(R.drawable.baseline_play_arrow);
        try {
            byte[] image = getSongImage(musicFiles.get(position).getPath());
            if (image != null) {
                Glide.with(context).asBitmap()
                        .load(image)
                        .into(holder.getImageView());
            } else {
                Glide.with(context)
                        .load(R.drawable.img)
                        .into(holder.getImageView());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String path = musicFiles.get(position).getPath();
        String artist = musicFiles.get(position).getArtist();

        if (artist.equals("<unknown>")) {
            holder.getOtherTextView().setText(path);
        } else {
            holder.getOtherTextView().setText(artist);
        }

        String duration = milliSecondsToTimer(Long.parseLong(musicFiles.get(position).getDuration()));
        holder.getDurationTextView().setText(duration);

        playThreadBtn(holder, position);

        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });

    }

    private void playThreadBtn(MyViewHolder holder, int position) {
        ThreadPlay.playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                holder.getButton().setOnClickListener(e -> playPauseBtnClicked(holder, position));
            }
        };
        ThreadPlay.playThread.start();
    }

    private void playPauseBtnClicked(MyViewHolder holder, int currentPosition) {
        Uri uri = Uri.parse(musicFiles.get(currentPosition).getPath());

        // Kiểm tra xem có bài hát nào đang phát không
        isPlaying = mediaPlayer.isPlaying();

        if (isPlaying) {
            // Nếu có bài hát đang phát
            if (playingPosition != currentPosition) {
                // Dừng bài hát hiện tại
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(context, uri);
                playMusic(mediaPlayer);
            } else {
                // Dừng bài hát hiện tại nếu đang phát
                pauseMusic(mediaPlayer);
            }
        } else {
            // Nếu không có bài hát nào đang phát
            if (playingPosition != currentPosition) {
                // Nếu bài hát hiện tại khác với bài hát trước đó, tạo mới MediaPlayer
                mediaPlayer = MediaPlayer.create(context, uri);
            }
            playMusic(mediaPlayer);
        }

        // Lưu lại holder cũ
        if (isPlaying || playingPosition != currentPosition) {
            // Nếu có bài hát đang phát hoặc bài hát hiện tại khác với bài hát trước đó
            if (oldViewHolder != null) {
                oldViewHolder.getButton().setImageResource(R.drawable.baseline_play_arrow);
            }
            oldViewHolder = holder;
        }
//        Cập nhật giao diện
        holder.getButton().setImageResource(isPlaying ? R.drawable.baseline_pause : R.drawable.baseline_play_arrow);

        playingPosition = currentPosition;
    }

    private void pauseMusic(MediaPlayer mediaPlayer) {
        mediaPlayer.pause();
        isPlaying = mediaPlayer.isPlaying();
    }

    private void playMusic(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        isPlaying = mediaPlayer.isPlaying();
    }


    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public byte[] getSongImage(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] image = retriever.getEmbeddedPicture();
        retriever.release();
        retriever.close();
        return image;
    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString;
        String secondsString;

        // Chuyển đổi tổng số milliseconds thành số giây
        int seconds = (int) (milliseconds / 1000);

        // Tính số phút
        int minutes = seconds / 60;
        seconds = seconds % 60;

        // Định dạng chuỗi cho số giây và số phút
        secondsString = String.valueOf(seconds);
        if (seconds < 10) {
            secondsString = "0" + secondsString;
        }

        finalTimerString = minutes + ":" + secondsString;

        // Trả về chuỗi mm:ss
        return finalTimerString;
    }

}
