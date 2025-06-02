package com.example.SE104_DoAn;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BoardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_LIST = 0;
    private static final int TYPE_ADD = 1;

    private List<String> listTitles;
    private List<TaskList> taskLists;
    private Context context;
    private OnAddListListener listener;

    public BoardListAdapter(Context context, List<String> listTitles, List<TaskList> taskLists, OnAddListListener listener) {
        this.context = context;
        this.listTitles = listTitles;
        this.taskLists = taskLists;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return (listTitles.get(position) == null) ? TYPE_ADD : TYPE_LIST;
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
        if (getItemViewType(position) == TYPE_LIST) {
            ListViewHolder viewHolder = (ListViewHolder) holder;
            String title = listTitles.get(position);
            TaskList taskList = taskLists.get(position);

            viewHolder.listTitle.setText(title);
            viewHolder.cardRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            viewHolder.cardRecyclerView.setAdapter(new CardAdapter(taskList.getCards(), context));

            // Thay setOnClickListener cho Button bằng TextView
            viewHolder.tvAddCard.setOnClickListener(v -> showAddCardDialog(taskList));
        } else {
            AddListViewHolder viewHolder = (AddListViewHolder) holder;
            viewHolder.btnAddList.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAddList();
                }
            });
        }
    }

    private void showAddCardDialog(TaskList taskList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thêm task mới");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String cardTitle = input.getText().toString();
            if (!cardTitle.isEmpty()) {
                taskList.addCard(new Card(cardTitle));
                notifyItemChanged(taskLists.indexOf(taskList));
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return listTitles.size();
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView listTitle;
        RecyclerView cardRecyclerView;
        TextView tvAddCard; // Thay Button bằng TextView

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            listTitle = itemView.findViewById(R.id.listTitle);
            cardRecyclerView = itemView.findViewById(R.id.cardRecyclerView);
            tvAddCard = itemView.findViewById(R.id.tvAddCard); // Thay btnAddCard bằng tvAddCard
        }
    }

    public static class AddListViewHolder extends RecyclerView.ViewHolder {
        Button btnAddList;

        public AddListViewHolder(View itemView) {
            super(itemView);
            btnAddList = itemView.findViewById(R.id.btnAddList);
        }
    }
}