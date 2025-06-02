package com.example.SE104_DoAn;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements OnAddListListener {

    private static final int REQUEST_CODE_UPDATE_CARD = 100;

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

        // Khởi tạo adapter với dữ liệu ban đầu
        boardListAdapter = new BoardListAdapter(getContext(), new ArrayList<>(), new ArrayList<>(), this);
        boardRecyclerView.setAdapter(boardListAdapter);

        // Quan sát dữ liệu từ ViewModel
        boardViewModel.getListTitles().observe(getViewLifecycleOwner(), titles -> {
            boardListAdapter.setListTitles(titles);
            boardListAdapter.notifyDataSetChanged();
        });

        boardViewModel.getTaskLists().observe(getViewLifecycleOwner(), taskLists -> {
            boardListAdapter.setTaskLists(taskLists);
            boardListAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onAddList() {
        showAddListDialog();
    }

    private void showAddListDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setTitle("Thêm danh sách mới");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String listTitle = input.getText().toString();
            if (!listTitle.isEmpty()) {
                boardViewModel.addNewList(listTitle);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_UPDATE_CARD && resultCode == RESULT_OK && data != null) {
            Card updatedCard = data.getParcelableExtra("updatedCard");
            int position = data.getIntExtra("position", -1);
            int listPosition = -1;

            // Tìm danh sách chứa thẻ cần cập nhật
            List<TaskList> taskLists = boardViewModel.getTaskLists().getValue();
            if (taskLists != null) {
                for (int i = 0; i < taskLists.size(); i++) {
                    List<Card> cards = taskLists.get(i).getCards();
                    for (int j = 0; j < cards.size(); j++) {
                        if (j == position) {
                            listPosition = i;
                            cards.set(j, updatedCard);
                            break;
                        }
                    }
                    if (listPosition != -1) break;
                }
            }

            // Cập nhật danh sách trong ViewModel
            if (listPosition != -1) {
                boardViewModel.updateTaskLists(taskLists);
            }
        }
    }
}