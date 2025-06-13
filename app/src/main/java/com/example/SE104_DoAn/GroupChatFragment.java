package com.example.SE104_DoAn;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class GroupChatFragment extends Fragment {

    private static final String TAG = "GroupChatFragment";

    private RecyclerView rvChatMessages;
    private EditText etMessageInput;
    private Button btnSendMessage;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private CollectionReference messagesRef;

    private String groupId;
    private String groupName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString("GROUP_ID");
            groupName = getArguments().getString("GROUP_NAME");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);

        if (groupId == null) {
            Toast.makeText(getContext(), "Lỗi: Không tìm thấy group chat.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // SỬA LỖI: Xóa hoặc comment out dòng code gây crash.
        // Dòng này sẽ gây lỗi nếu theme của bạn là NoActionBar.
        /*
        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(groupName);
        }
        */

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        messagesRef = db.collection("Group").document(groupId).collection("Messages");

        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etMessageInput = view.findViewById(R.id.etMessageInput);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);

        setupRecyclerView();
        loadMessages();

        btnSendMessage.setOnClickListener(v -> sendMessage());

        return view;
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void loadMessages() {
        messagesRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener(getActivity(), (snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Lỗi khi lắng nghe tin nhắn.", error);
                        return;
                    }
                    if (snapshots == null) return;

                    chatMessages.clear();
                    chatMessages.addAll(snapshots.toObjects(ChatMessage.class));
                    chatAdapter.notifyDataSetChanged();
                    if (!chatMessages.isEmpty()) {
                        rvChatMessages.scrollToPosition(chatMessages.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String messageText = etMessageInput.getText().toString().trim();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (messageText.isEmpty() || currentUser == null) {
            return;
        }

        String username = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : currentUser.getEmail().split("@")[0];
        ChatMessage message = new ChatMessage(currentUser.getUid(), username, messageText);

        messagesRef.add(message)
                .addOnSuccessListener(documentReference -> etMessageInput.setText(""))
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gửi tin nhắn thất bại.", Toast.LENGTH_SHORT).show());
    }
}