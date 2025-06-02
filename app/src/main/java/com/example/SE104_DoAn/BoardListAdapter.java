package com.example.SE104_DoAn;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BoardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_ADD = 1;
    private static final int REQUEST_CODE_UPDATE_CARD = 100;

    private Context context;
    private List<String> listTitles;
    private List<TaskList> taskLists;
    private OnAddListListener onAddListListener;

    public BoardListAdapter(Context context, List<String> listTitles, List<TaskList> taskLists, OnAddListListener onAddListListener) {
        this.context = context;
        this.listTitles = listTitles;
        this.taskLists = taskLists;
        this.onAddListListener = onAddListListener;
    }

    public void setListTitles(List<String> newListTitles) {
        this.listTitles = newListTitles;
    }

    public void setTaskLists(List<TaskList> newTaskLists) {
        this.taskLists = newTaskLists;
    }

    @Override
    public int getItemViewType(int position) {
        return listTitles.get(position) == null ? TYPE_ADD : TYPE_LIST;
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
                ((BoardFragment) onAddListListener).startActivityForResult(intent, REQUEST_CODE_UPDATE_CARD);
            });
            listViewHolder.cardRecyclerView.setAdapter(cardAdapter);

            listViewHolder.tvAddCard.setOnClickListener(v -> {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("Thêm thẻ mới");

                final android.widget.EditText input = new android.widget.EditText(context);
                input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Thêm", (dialog, which) -> {
                    String cardTitle = input.getText().toString();
                    if (!cardTitle.isEmpty()) {
                        Card card = new Card(cardTitle);
                        taskList.addCard(card);
                        cardAdapter.notifyItemInserted(taskList.getCards().size() - 1);
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
        return listTitles != null ? listTitles.size() : 0;
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
}