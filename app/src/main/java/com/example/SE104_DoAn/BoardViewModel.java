package com.example.SE104_DoAn;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class BoardViewModel extends ViewModel {
    private MutableLiveData<List<String>> listTitles = new MutableLiveData<>();
    private MutableLiveData<List<TaskList>> taskLists = new MutableLiveData<>();

    public BoardViewModel() {
        // Khởi tạo dữ liệu với một phần tử null để hiển thị nút "Add List"
        List<String> initialTitles = new ArrayList<>();
        List<TaskList> initialTaskLists = new ArrayList<>();
        initialTitles.add(null); // Thêm phần tử null để hiển thị nút "Add List"
        listTitles.setValue(initialTitles);
        taskLists.setValue(initialTaskLists);
    }

    public LiveData<List<String>> getListTitles() {
        return listTitles;
    }

    public LiveData<List<TaskList>> getTaskLists() {
        return taskLists;
    }

    public void addNewList(String title) {
        List<String> currentTitles = listTitles.getValue();
        List<TaskList> currentTaskLists = taskLists.getValue();
        if (currentTitles != null && currentTaskLists != null) {
            // Xóa phần tử null (nút "Add List") trước khi thêm danh sách mới
            if (!currentTitles.isEmpty() && currentTitles.get(currentTitles.size() - 1) == null) {
                currentTitles.remove(currentTitles.size() - 1);
            }

            // Thêm danh sách mới
            currentTitles.add(title);
            currentTaskLists.add(new TaskList());

            // Thêm lại phần tử null ở cuối để hiển thị nút "Add List"
            currentTitles.add(null);

            listTitles.setValue(currentTitles);
            taskLists.setValue(currentTaskLists);
        }
    }

    public void updateTaskLists(List<TaskList> updatedTaskLists) {
        taskLists.setValue(updatedTaskLists);
    }
}