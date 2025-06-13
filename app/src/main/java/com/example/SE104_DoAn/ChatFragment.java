package com.example.SE104_DoAn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private RecyclerView rvChatLobby;
    private ChatLobbyAdapter adapter;
    private BoardViewModel boardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChatLobby = view.findViewById(R.id.rvChatLobby);
        boardViewModel = new ViewModelProvider(requireActivity()).get(BoardViewModel.class);

        setupRecyclerView();
        observeViewModel();

        return view;
    }

    private void setupRecyclerView() {
        adapter = new ChatLobbyAdapter(new ArrayList<>(), group -> {
            // Xử lý sự kiện khi người dùng nhấn vào một group
            Bundle args = new Bundle();
            args.putString("GROUP_ID", group.getGroup_id());
            args.putString("GROUP_NAME", group.getName());

            // Chuyển sang GroupChatFragment bằng Navigation Component
            NavHostFragment.findNavController(this).navigate(R.id.action_chatFragment_to_groupChatFragment, args);
        });
        rvChatLobby.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatLobby.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Lắng nghe danh sách group từ BoardViewModel
        boardViewModel.getGroups().observe(getViewLifecycleOwner(), groups -> {
            if (groups != null) {
                adapter.updateGroups(groups);
            }
        });
    }
}