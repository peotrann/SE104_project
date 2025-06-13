package com.example.SE104_DoAn;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        holder.tvUsername.setText(message.getUsername());
        holder.tvMessage.setText(message.getMessage());
        if (message.getTimestamp() != null) {
            holder.tvTimestamp.setText(dateFormat.format(message.getTimestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages != null ? chatMessages.size() : 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername;
        TextView tvMessage;
        TextView tvTimestamp;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}