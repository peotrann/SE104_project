package com.example.SE104_DoAn;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class BoardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_ADD = 1;
    public static final int REQUEST_CODE_UPDATE_CARD = 100;

    private Context context;
    private List<String> listTitles;
    private List<TaskList> taskLists;
    private OnAddListListener onAddListListener;
    private OnAddTaskListener onAddTaskListener;
    private BoardViewModel boardViewModel;
    private FirebaseUser currentUser;

    public BoardListAdapter(Context context, List<String> listTitles, List<TaskList> taskLists, OnAddListListener onAddListListener, BoardViewModel boardViewModel) {
        this.context = context;
        this.listTitles = listTitles;
        this.taskLists = taskLists;
        this.onAddListListener = onAddListListener;
        this.boardViewModel = boardViewModel;
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("BoardListAdapter", "Initialized with listTitles size: " + (listTitles != null ? listTitles.size() : 0));
    }

    public void setListTitles(List<String> newListTitles) {
        this.listTitles = newListTitles;
        Log.d("BoardListAdapter", "setListTitles with size: " + (newListTitles != null ? newListTitles.size() : 0));
        notifyDataSetChanged();
    }

    public void setTaskLists(List<TaskList> newTaskLists) {
        this.taskLists = newTaskLists;
        notifyDataSetChanged();
    }

    public void setOnAddTaskListener(OnAddTaskListener listener) {
        this.onAddTaskListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (listTitles != null && position < listTitles.size() && listTitles.get(position) != null) {
            return TYPE_LIST;
        }
        return TYPE_ADD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_LIST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
            return new ListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_list, parent, false);
            return new AddListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ListViewHolder) {
            ListViewHolder listViewHolder = (ListViewHolder) holder;
            String title = listTitles.get(position);
            TaskList taskList = taskLists.get(position);

            listViewHolder.tvListTitle.setText(title);
            listViewHolder.cardRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            CardAdapter cardAdapter = new CardAdapter(taskList.getCards(), (card, cardPosition) -> {
                Intent intent = new Intent(context, CardDetailActivity.class);
                intent.putExtra("card", card);
                intent.putExtra("position", cardPosition);
                intent.putExtra("listPosition", position);
                ((BoardFragment) onAddListListener).startActivityForResult(intent, REQUEST_CODE_UPDATE_CARD);
            });
            listViewHolder.cardRecyclerView.setAdapter(cardAdapter);

            listViewHolder.tvAddCard.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Thêm task mới");

                final android.widget.EditText input = new android.widget.EditText(context);
                input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Thêm", (dialog, which) -> {
                    String cardTitle = input.getText().toString().trim();
                    if (!cardTitle.isEmpty()) {
                        if (onAddTaskListener != null) {
                            onAddTaskListener.onAddTask(position, cardTitle);
                        }
                    }
                });

                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        } else if (holder instanceof AddListViewHolder) {
            AddListViewHolder addListViewHolder = (AddListViewHolder) holder;
            addListViewHolder.btnAddList.setOnClickListener(v -> {
                if (onAddListListener != null) {
                    onAddListListener.onAddList();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listTitles != null ? listTitles.size() : 1;
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvListTitle;
        RecyclerView cardRecyclerView;
        TextView tvAddCard;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListTitle = itemView.findViewById(R.id.listTitle);
            cardRecyclerView = itemView.findViewById(R.id.cardRecyclerView);
            tvAddCard = itemView.findViewById(R.id.tvAddCard);
        }
    }

    static class AddListViewHolder extends RecyclerView.ViewHolder {
        Button btnAddList;

        public AddListViewHolder(@NonNull View itemView) {
            super(itemView);
            btnAddList = itemView.findViewById(R.id.btnAddList);
        }
    }

    // Định nghĩa interface OnAddListListener
    public interface OnAddListListener {
        void onAddList();
    }

    // Interface OnAddTaskListener (đã có)
    public interface OnAddTaskListener {
        void onAddTask(int listPosition, String taskTitle);
    }
}