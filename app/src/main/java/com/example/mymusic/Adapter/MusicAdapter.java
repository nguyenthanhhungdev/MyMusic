package com.example.mymusic.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymusic.Activity.PlayActivity;
import com.example.mymusic.Fragment.MySongFragment;
import com.example.mymusic.Holder.MusicViewHolder;
import com.example.mymusic.File.MusicFiles;
import com.example.mymusic.R;

import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicViewHolder> {
    private final Context context; // biến để truy cập tới các dữ liệu của giao diện gọi tới
    private final ArrayList<MusicFiles> musicFiles;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static int playingPosition = -1; // position của bài hát đang phát
    private boolean isPlaying;
    private Uri uri;
    private MusicViewHolder oldViewHolder;

    public MusicAdapter(Context context, ArrayList<MusicFiles> musicFiles) {
        this.context = context;
        this.musicFiles = musicFiles;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_item, parent, false);
        oldViewHolder = new MusicViewHolder(view);
        return new MusicViewHolder(view);
    }

    //    Gán dữ liệu từ thành phần giao diện vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        int currentPosition = position;

        String title = musicFiles.get(position).getTitle();
        holder.getNameTextView().setText(title);

        String s_position = String.valueOf(position);
        holder.getNumTextView().setText(s_position);

//        holder.getButton().setTag(R.drawable.baseline_play_arrow);
        byte[] image;
        try {
            image = getSongImage(musicFiles.get(position).getPath());
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

        String album = musicFiles.get(position).getAlbum();
        String artist = musicFiles.get(position).getArtist();

        if (artist.equals("<unknown>")) {
            holder.getOtherTextView().setText(album);
        } else {
            holder.getOtherTextView().setText(artist);
        }

        String duration = milliSecondsToTimer(Long.parseLong(musicFiles.get(position).getDuration()));
        holder.getDurationTextView().setText(duration);

        playThreadBtn(holder, position);

        holder.itemView.setOnClickListener(e -> {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra("position", position);
            intent.putExtra("playing_position", playingPosition);
            context.startActivity(intent);
            /**
             * Sau khi truyền playingPosition vào trong intent thì ta mới cập nhật lại playingPosition
             * Vì khi cập nhật trước khi truyền sẽ gây hệ thống hiểu lầm là đang chơi 1 bài vì playingPosition = currentPosition nên không khởi tạo mediaPlayer cho bài mới
             * Truyền sau giúp ta xác định lại bài hát khi chơi ở ngoài màn hình chính
             * */
            playingPosition = currentPosition;
            holder.getButton().setImageResource(R.drawable.baseline_pause);
            MySongFragment.popUpNowPlying();
        });

    }

    private void playThreadBtn(MusicViewHolder holder, int position) {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                holder.getButton().setOnClickListener(e -> playPauseBtnClicked(holder, position));
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked(MusicViewHolder holder, int currentPosition) {
        uri = Uri.parse(musicFiles.get(currentPosition).getPath());

        // Kiểm tra xem có bài hát nào đang phát không
        isPlaying = mediaPlayer.isPlaying();

         try {
            if (isPlaying) {
                // Nếu có bài hát đang phát và khác bài hát muốn chơi hiện tại
                if (playingPosition != currentPosition) {
                    // Dừng bài hát đang phát
                    mediaPlayer.stop();
//                mediaPlayer.release();
                    mediaPlayer.reset();
//                Chơi bài hat mới
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
        } catch (IOException e) {
             Toast.makeText(context,"Lỗi không phát được bài hát", Toast.LENGTH_LONG);
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
//        Lưu lại vị trí bài hát đang phát
        playingPosition = currentPosition;

        /**
         * Khi nhấn vào button thì Sẽ thực hiện gọi transaction để thêm vao fragment mysong
         * Sử dụng hàm static vì dẽ không cần phải gọi tạo mới 1 instance của mysong (hàm popUp phải nằm trong mysong mới có thể sử dụng
         * FragmentManager)
         *
         * */
        MySongFragment.popUpNowPlying();
    }

    private void pauseMusic(MediaPlayer mediaPlayer) {
        mediaPlayer.pause();
        isPlaying = mediaPlayer.isPlaying();
    }

    private void playMusic(MediaPlayer mediaPlayer) throws IOException {
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

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setMediaPlayer(MediaPlayer mediaPlayer) {
        MusicAdapter.mediaPlayer = mediaPlayer;
    }

    public static int getPlayingPosition() {
        return playingPosition;
    }

    public static void setPlayingPosition(int playingPosition) {
        MusicAdapter.playingPosition = playingPosition;
    }
}
