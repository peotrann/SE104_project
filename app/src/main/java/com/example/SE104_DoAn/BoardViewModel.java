package com.example.SE104_DoAn;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardViewModel extends ViewModel {
    private final MutableLiveData<List<String>> listTitles = new MutableLiveData<>();
    private final MutableLiveData<List<TaskList>> taskLists = new MutableLiveData<>();
    private final MutableLiveData<String> addListStatus = new MutableLiveData<>();
    private final DatabaseReference mDatabase;
    private final FirebaseUser currentUser;
    private final Map<Integer, String> taskListKeys = new HashMap<>();

    public BoardViewModel() {
        mDatabase = FirebaseDatabase.getInstance("https://se104-doan-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        List<String> initialTitles = new ArrayList<>();
        initialTitles.add(null);
        listTitles.setValue(initialTitles);
        taskLists.setValue(new ArrayList<>());

        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {
        if (currentUser == null) {
            Log.w("BoardViewModel", "User not logged in, cannot load data");
            addListStatus.setValue("Lỗi: Vui lòng đăng nhập để tải dữ liệu");
            return;
        }

        Log.d("BoardViewModel", "Loading data for user: " + currentUser.getUid());
        mDatabase.child("users").child(currentUser.getUid()).child("taskLists")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> titles = new ArrayList<>();
                        List<TaskList> lists = new ArrayList<>();
                        taskListKeys.clear();
                        int index = 0;

                        for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                            String title = taskSnapshot.child("title").getValue(String.class);
                            String creator = taskSnapshot.child("creator").getValue(String.class);
                            Map<String, String> members = (Map<String, String>) taskSnapshot.child("members").getValue();
                            List<Card> cards = new ArrayList<>();
                            for (DataSnapshot cardSnapshot : taskSnapshot.child("cards").getChildren()) {
                                Card card = cardSnapshot.getValue(Card.class);
                                if (card != null) {
                                    cards.add(card);
                                }
                            }

                            TaskList taskList = new TaskList();
                            taskList.setCards(cards != null ? cards : new ArrayList<>());
                            taskList.setCreator(creator != null ? creator : "");
                            taskList.setMembers(members != null ? members : new HashMap<>());
                            titles.add(title);
                            lists.add(taskList);
                            taskListKeys.put(index++, taskSnapshot.getKey());
                        }

                        if (titles.isEmpty()) {
                            titles.add(null);
                        } else if (titles.get(titles.size() - 1) != null) {
                            titles.add(null);
                        }

                        Log.d("BoardViewModel", "Loaded " + titles.size() + " titles from Firebase");
                        listTitles.setValue(titles);
                        taskLists.setValue(lists);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BoardViewModel", "loadDataFromFirebase failed: " + error.getMessage());
                        addListStatus.setValue("Lỗi khi tải dữ liệu: " + error.getMessage());
                    }
                });
    }

    public LiveData<List<String>> getListTitles() {
        return listTitles;
    }

    public LiveData<List<TaskList>> getTaskLists() {
        return taskLists;
    }

    public LiveData<String> getAddListStatus() {
        return addListStatus;
    }

    public void addNewList(String title) {
        Log.d("BoardViewModel", "Attempting to add new list: " + title);
        if (currentUser == null) {
            Log.e("BoardViewModel", "User not logged in, cannot add new list");
            addListStatus.setValue("Lỗi: Vui lòng đăng nhập trước khi thêm danh sách");
            return;
        }

        Log.d("BoardViewModel", "User UID: " + currentUser.getUid());
        DatabaseReference newListRef = mDatabase.child("users").child(currentUser.getUid()).child("taskLists").push();
        TaskList taskList = new TaskList();
        taskList.setCreator(currentUser.getUid());
        Map<String, String> members = new HashMap<>();
        members.put(currentUser.getUid(), "leader");
        taskList.setMembers(members);

        Map<String, Object> taskListValues = new HashMap<>();
        taskListValues.put("title", title);
        taskListValues.put("creator", currentUser.getUid());
        taskListValues.put("members", taskList.getMembers());
        taskListValues.put("cards", taskList.getCards());

        newListRef.setValue(taskListValues)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BoardViewModel", "Successfully added list to Firebase: " + title);
                    addListStatus.setValue("Danh sách " + title + " đã được thêm thành công");
                })
                .addOnFailureListener(e -> {
                    Log.e("BoardViewModel", "Failed to add new list to Firebase: " + e.getMessage());
                    addListStatus.setValue("Lỗi: Không thể thêm danh sách. " + e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("BoardViewModel", "Task incomplete: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    } else {
                        Log.d("BoardViewModel", "Task completed");
                    }
                });
    }

    public void addTaskToList(int listPosition, String taskTitle) {
        Log.d("BoardViewModel", "Attempting to add task: " + taskTitle + " to list at position: " + listPosition);
        if (currentUser == null) {
            Log.e("BoardViewModel", "User not logged in, cannot add task");
            addListStatus.setValue("Lỗi: Vui lòng đăng nhập trước khi thêm task");
            return;
        }

        String taskListKey = taskListKeys.get(listPosition);
        if (taskListKey == null) {
            Log.e("BoardViewModel", "Invalid list position: " + listPosition);
            addListStatus.setValue("Lỗi: Vị trí danh sách không hợp lệ");
            return;
        }

        DatabaseReference taskListRef = mDatabase.child("users").child(currentUser.getUid()).child("taskLists").child(taskListKey).child("cards").push();
        Card newCard = new Card();
        newCard.setTitle(taskTitle);
        newCard.setCreator(currentUser.getUid());

        taskListRef.setValue(newCard)
                .addOnSuccessListener(aVoid -> {
                    Log.d("BoardViewModel", "Successfully added task to Firebase: " + taskTitle);
                    addListStatus.setValue("Task " + taskTitle + " đã được thêm thành công");
                    loadDataFromFirebase(); // Cập nhật lại dữ liệu
                })
                .addOnFailureListener(e -> {
                    Log.e("BoardViewModel", "Failed to add task to Firebase: " + e.getMessage());
                    addListStatus.setValue("Lỗi: Không thể thêm task. " + e.getMessage());
                })
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("BoardViewModel", "Task incomplete: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    } else {
                        Log.d("BoardViewModel", "Task completed");
                    }
                });
    }

    // Thêm phương thức để cập nhật card
    public void updateCardInList(int listPosition, int cardPosition, Card updatedCard) {
        Log.d("BoardViewModel", "Attempting to update card at listPosition: " + listPosition + ", cardPosition: " + cardPosition);
        if (currentUser == null) {
            Log.e("BoardViewModel", "User not logged in, cannot update card");
            addListStatus.setValue("Lỗi: Vui lòng đăng nhập trước khi cập nhật card");
            return;
        }

        String taskListKey = taskListKeys.get(listPosition);
        if (taskListKey == null) {
            Log.e("BoardViewModel", "Invalid list position: " + listPosition);
            addListStatus.setValue("Lỗi: Vị trí danh sách không hợp lệ");
            return;
        }

        // Lấy danh sách cards từ Firebase để tìm key của card
        mDatabase.child("users").child(currentUser.getUid()).child("taskLists").child(taskListKey).child("cards")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentPosition = 0;
                        for (DataSnapshot cardSnapshot : snapshot.getChildren()) {
                            if (currentPosition == cardPosition) {
                                String cardKey = cardSnapshot.getKey();
                                DatabaseReference cardRef = mDatabase.child("users").child(currentUser.getUid()).child("taskLists").child(taskListKey).child("cards").child(cardKey);
                                cardRef.setValue(updatedCard)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("BoardViewModel", "Successfully updated card: " + updatedCard.getTitle());
                                            addListStatus.setValue("Card " + updatedCard.getTitle() + " đã được cập nhật thành công");
                                            loadDataFromFirebase(); // Cập nhật lại dữ liệu
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("BoardViewModel", "Failed to update card: " + e.getMessage());
                                            addListStatus.setValue("Lỗi: Không thể cập nhật card. " + e.getMessage());
                                        });
                                break;
                            }
                            currentPosition++;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("BoardViewModel", "updateCardInList failed: " + error.getMessage());
                        addListStatus.setValue("Lỗi khi cập nhật card: " + error.getMessage());
                    }
                });
    }

    public void updateTaskLists(List<TaskList> updatedTaskLists) {
        List<TaskList> newTaskLists = new ArrayList<>();
        for (TaskList taskList : updatedTaskLists) {
            TaskList newTaskList = new TaskList();
            newTaskList.setCards(new ArrayList<>(taskList.getCards()));
            newTaskList.setCreator(taskList.getCreator());
            newTaskList.setMembers(new HashMap<>(taskList.getMembers()));
            newTaskLists.add(newTaskList);
        }
        taskLists.setValue(newTaskLists);
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabase;
    }

    public String getTaskListKey(int position) {
        return taskListKeys.get(position);
    }
}