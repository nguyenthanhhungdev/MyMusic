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
    private MediaPlayer mediaPlayer;
    private MyViewHolder myViewHolder;

    public MusicAdapter(Context context, ArrayList<MusicFiles> musicFiles) {
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        myViewHolder = new MyViewHolder(view);
        return new MyViewHolder(view);
    }

    //    Gán dữ liệu từ thành phần giao diện vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Uri uri = Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer = MediaPlayer.create(context, uri);

        String title = musicFiles.get(position).getTitle();
        holder.getNameTextView().setText(title);

        String s_position = String.valueOf(position);
        holder.getNumTextView().setText(s_position);

        holder.getButton().setTag(R.drawable.baseline_play_arrow);
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

        playThreadBtn(holder);

        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("position", position);
            context.startActivity(intent);
        });
    }

    private void playThreadBtn(MyViewHolder holder) {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                holder.getButton().setOnClickListener(e -> playPauseBtnClicked(holder));
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked(MyViewHolder holder) {
        if (mediaPlayer.isPlaying()) {
                if (Integer.parseInt(myViewHolder.getButton().getTag().toString()) == R.drawable.baseline_pause){
                    myViewHolder.getButton().setImageResource(R.drawable.baseline_play_arrow);
                    myViewHolder.getButton().setTag(R.drawable.baseline_play_arrow);
                }
            mediaPlayer.pause();
        } else {
            holder.getButton().setImageResource(R.drawable.baseline_pause);
            holder.getButton().setTag(R.drawable.baseline_pause);
            mediaPlayer.start();
        }
        myViewHolder = holder;
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
