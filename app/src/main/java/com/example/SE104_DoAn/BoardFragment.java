package com.example.SE104_DoAn;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements OnAddListListener {

    private RecyclerView boardRecyclerView;
    private BoardListAdapter boardListAdapter;
    private List<String> listTitles;
    private List<TaskList> taskLists;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_board, container, false);

        // Khởi tạo các view
        boardRecyclerView = view.findViewById(R.id.boardRecyclerView);
        if (boardRecyclerView == null) {
            throw new IllegalStateException("RecyclerView not found in fragment_board.xml");
        }

        // Khởi tạo danh sách tiêu đề và TaskList với phần tử null ở cuối
        listTitles = new ArrayList<>();
        taskLists = new ArrayList<>();
        listTitles.add(null); // Phần tử null để hiển thị nút "Add List"

        // Thiết lập RecyclerView với LinearLayoutManager ngang
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        boardRecyclerView.setLayoutManager(layoutManager);

        // Thêm ItemDecoration để tạo khoảng cách giữa các item
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.list_spacing);
        boardRecyclerView.addItemDecoration(new ListItemDecoration(spacingInPixels));

        // Khởi tạo và thiết lập adapter với listener
        boardListAdapter = new BoardListAdapter(getContext(), listTitles, taskLists, this);
        boardRecyclerView.setAdapter(boardListAdapter);

        return view;
    }

    // Phương thức thêm danh sách mới
    private void addNewList(String title) {
        if (!listTitles.contains(null)) {
            listTitles.add(null); // Đảm bảo phần tử null luôn tồn tại ở cuối
        }
        int insertPos = listTitles.size() - 1; // Chèn trước phần tử null
        listTitles.add(insertPos, title);
        taskLists.add(insertPos, new TaskList(title));
        boardListAdapter.notifyItemInserted(insertPos);
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
                addNewList(listTitle);
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}