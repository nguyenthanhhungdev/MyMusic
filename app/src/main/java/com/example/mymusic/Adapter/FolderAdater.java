package com.example.mymusic.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymusic.Activity.MainActivity;
import com.example.mymusic.File.FolderFiles;
import com.example.mymusic.Holder.FolderViewHolder;
import com.example.mymusic.R;

import java.io.File;
import java.util.ArrayList;

public class FolderAdater extends RecyclerView.Adapter<FolderViewHolder> {
    private Context context;
    private ArrayList<FolderFiles> folders;
    public FolderAdater(Context context, ArrayList<FolderFiles> folders) {
        this.context = context;
        this.folders = folders;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        String name = folders.get(position).getFile().getName();
        String path = folders.get(position).getFile().getPath();
        String number = folders.get(position).getNumberOfSong();
        holder.getFolderName().setText(name);
        holder.getFolderPath().setText(path);
        holder.getNumberOf().setText(String.format("%s song", number));
    }

    private String getStringUntilMatch(String input, String pattern) {
        int index = input.indexOf(pattern);
        if (index != -1) {
            return input.substring(0, index);
        }
        return input;
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


}
