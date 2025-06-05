package com.example.SE104_DoAn;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements BoardListAdapter.OnAddListListener {

    private RecyclerView boardRecyclerView;
    private BoardListAdapter boardListAdapter;
    private BoardViewModel boardViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        boardViewModel = new ViewModelProvider(this).get(BoardViewModel.class);

        boardRecyclerView = view.findViewById(R.id.boardRecyclerView);
        if (boardRecyclerView == null) {
            throw new IllegalStateException("RecyclerView not found in fragment_board.xml");
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        boardRecyclerView.setLayoutManager(layoutManager);

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.list_spacing);
        boardRecyclerView.addItemDecoration(new ListItemDecoration(spacingInPixels));

        boardListAdapter = new BoardListAdapter(getContext(), new ArrayList<>(), new ArrayList<>(), this, boardViewModel);
        boardRecyclerView.setAdapter(boardListAdapter);

        // Thêm OnAddTaskListener
        boardListAdapter.setOnAddTaskListener((listPosition, taskTitle) -> {
            boardViewModel.addTaskToList(listPosition, taskTitle);
        });

        boardViewModel.getListTitles().observe(getViewLifecycleOwner(), titles -> {
            if (titles != null) {
                Log.d("BoardFragment", "Titles updated: " + titles.size());
                boardListAdapter.setListTitles(titles);
                boardListAdapter.notifyDataSetChanged();
            } else {
                Log.w("BoardFragment", "Titles is null, initializing with default");
                List<String> defaultTitles = new ArrayList<>();
                defaultTitles.add(null);
                boardListAdapter.setListTitles(defaultTitles);
            }
        });

        boardViewModel.getTaskLists().observe(getViewLifecycleOwner(), taskLists -> {
            if (taskLists != null) {
                boardListAdapter.setTaskLists(taskLists);
                boardListAdapter.notifyDataSetChanged();
            } else {
                Log.w("BoardFragment", "Task lists is null, initializing with default");
                boardListAdapter.setTaskLists(new ArrayList<>());
            }
        });

        boardViewModel.getAddListStatus().observe(getViewLifecycleOwner(), status -> {
            if (status != null) {
                Toast.makeText(getContext(), status, Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BoardListAdapter.REQUEST_CODE_UPDATE_CARD && resultCode == RESULT_OK && data != null) {
            Card updatedCard = data.getParcelableExtra("updatedCard");
            int position = data.getIntExtra("position", -1);
            int listPosition = data.getIntExtra("listPosition", -1);

            if (updatedCard != null && position != -1 && listPosition != -1) {
                boardViewModel.updateCardInList(listPosition, position, updatedCard);
            }
        }
    }

    @Override
    public void onAddList() {
        showAddListDialog();
    }

    private void showAddListDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Thêm nhóm mới");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String listTitle = input.getText().toString().trim();
            if (!listTitle.isEmpty()) {
                boardViewModel.addNewList(listTitle);
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập tiêu đề danh sách", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}