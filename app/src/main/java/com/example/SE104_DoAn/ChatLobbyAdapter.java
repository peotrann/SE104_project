package com.example.SE104_DoAn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatLobbyAdapter extends RecyclerView.Adapter<ChatLobbyAdapter.ViewHolder> {

    private List<Group> groupList;
    private final OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public ChatLobbyAdapter(List<Group> groupList, OnGroupClickListener listener) {
        this.groupList = groupList;
        this.listener = listener;
    }

    public void updateGroups(List<Group> newGroupList) {
        this.groupList = newGroupList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_lobby, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.bind(group, listener);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName;
        TextView tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tvGroupName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }

        public void bind(final Group group, final OnGroupClickListener listener) {
            tvGroupName.setText(group.getName());
            // TODO: Bạn có thể thêm logic để hiển thị tin nhắn cuối cùng ở đây
            itemView.setOnClickListener(v -> listener.onGroupClick(group));
        }
    }
}