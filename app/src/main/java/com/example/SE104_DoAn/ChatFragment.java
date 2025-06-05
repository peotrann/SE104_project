package com.example.SE104_DoAn;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText etMessageInput;
    private Button btnSendMessage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private DatabaseReference chatRef;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Khởi tạo các view
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etMessageInput = view.findViewById(R.id.etMessageInput);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        // Khởi tạo Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatRef = FirebaseDatabase.getInstance("https://se104-doan-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("users").child(currentUser != null ? currentUser.getUid() : "default").child("chat");

        // Khởi tạo RecyclerView
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatAdapter);

        // Lấy dữ liệu tin nhắn từ Firebase
        loadChatMessages();

        // Xử lý gửi tin nhắn
        btnSendMessage.setOnClickListener(v -> {
            String messageText = etMessageInput.getText().toString().trim();
            if (!messageText.isEmpty() && currentUser != null) {
                sendMessage(messageText);
                etMessageInput.setText("");
            }
        });

        return view;
    }

    private void loadChatMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatMessages.clear();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    ChatMessage message = messageSnapshot.getValue(ChatMessage.class);
                    if (message != null) {
                        chatMessages.add(message);
                    }
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatFragment", "loadChatMessages failed: " + error.getMessage());
            }
        });
    }

    private void sendMessage(String messageText) {
        String userId = currentUser.getUid();
        String username = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Unknown";
        ChatMessage message = new ChatMessage(userId, username, messageText, System.currentTimeMillis());
        String messageKey = chatRef.push().getKey();
        if (messageKey != null) {
            chatRef.child(messageKey).setValue(message)
                    .addOnSuccessListener(aVoid -> Log.d("ChatFragment", "Message sent: " + messageText))
                    .addOnFailureListener(e -> Log.e("ChatFragment", "Failed to send message: " + e.getMessage()));
        }
    }
}