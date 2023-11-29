package com.example.mymusic.Fragment;

import static com.example.mymusic.Activity.MainActivity.musicFiles;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.mymusic.Adapter.MusicAdapter;
import com.example.mymusic.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MySongFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MySongFragment extends Fragment {
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private ImageButton button;
    public static View fragmentContainer;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static FragmentManager fragmentManager;

    public MySongFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MySongFragment newInstance(String param1, String param2) {
        MySongFragment fragment = new MySongFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        Nạp giao diện
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recyclerViewSong);
        recyclerView.setHasFixedSize(true);

        if (musicFiles.size() > 1) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        fragmentContainer = view.findViewById(R.id.fragment_container_view);
        fragmentManager = getChildFragmentManager();

    }

    public static void popUpNowPlying() {
        NowPlaying fragment = new NowPlaying();

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Thêm hoặc thay thế Fragment trong container (FrameLayout) trong layout của Activity
        transaction.replace(R.id.fragment_container_view, fragment); // R.id.fragment_container là ID của FrameLayout

        transaction.addToBackStack(null);
        // Commit giao dịch
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}