package com.example.mymusic.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.R;

public class FolderViewHolder extends RecyclerView.ViewHolder {
    private TextView folderName, numberOf, folderPath;
    private ImageView imageView;

    public FolderViewHolder(@NonNull View itemView) {
        super(itemView);
        folderName = itemView.findViewById(R.id.folderName);
        numberOf = itemView.findViewById(R.id.numberOf);
        imageView = itemView.findViewById(R.id.folderIcon);
        folderPath = itemView.findViewById(R.id.folderPath);
    }

    public TextView getFolderName() {
        return folderName;
    }

    public void setFolderName(TextView folderName) {
        this.folderName = folderName;
    }

    public TextView getNumberOf() {
        return numberOf;
    }

    public void setNumberOf(TextView numberOf) {
        this.numberOf = numberOf;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public TextView getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(TextView folderPath) {
        this.folderPath = folderPath;
    }
}