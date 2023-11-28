package com.example.mymusic.Holder;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.R;

// Tạo một đối tượng đề giữ các TextView và ImageView
public class MyViewHolder extends RecyclerView.ViewHolder {
    private ImageView imageView;
    private TextView numTextView, nameTextView, otherTextView, durationTextView;
    private ImageButton button;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.imageView);
        numTextView = itemView.findViewById(R.id.numberText);
        nameTextView = itemView.findViewById(R.id.nameSongTextView);
        nameTextView.setMovementMethod(new ScrollingMovementMethod());
        nameTextView.setSelected(true);
        otherTextView = itemView.findViewById(R.id.otherTextView);
        otherTextView.setMovementMethod(new ScrollingMovementMethod());
        otherTextView.setSelected(true);
        durationTextView = itemView.findViewById(R.id.durationTextView);
        button = itemView.findViewById(R.id.playButton);
    }


    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getNumTextView() {
        return numTextView;
    }

    public void setNumTextView(TextView numTextView) {
        this.numTextView = numTextView;
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public void setNameTextView(TextView nameTextView) {
        this.nameTextView = nameTextView;
    }

    public TextView getOtherTextView() {
        return otherTextView;
    }

    public void setOtherTextView(TextView otherTextView) {
        this.otherTextView = otherTextView;
    }

    public TextView getDurationTextView() {
        return durationTextView;
    }

    public void setDurationTextView(TextView durationTextView) {
        this.durationTextView = durationTextView;
    }

    public ImageButton getButton() {
        return button;
    }

    public void setButton(ImageButton button) {
        this.button = button;
    }
}
