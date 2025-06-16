package com.example.SE104_DoAn;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText etCardTitle, etDescription;
    private LinearLayout llMembersContainer;
    private Button btnSave, btnCancel;
    private TextView tvStartDate, tvEndDate;

    private LinearLayout llAttachments; // Thêm llAttachments
    private MaterialButton btnAddAttachment; // Thêm btnAddAttachment

    private Task task;
    private BoardViewModel viewModel;
    private FirebaseFirestore db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, hh:mm a", Locale.getDefault());
    private boolean isAdmin = false; // Biến cờ để lưu quyền admin
    private ActivityResultLauncher<String> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Khởi tạo launcher
        this.filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String fileName = getFileName(uri);
                        viewModel.addFileAttachment(task.getTask_id(), fileName, uri);
                    }
                });

        viewModel = new ViewModelProvider(this).get(BoardViewModel.class);
        db = FirebaseFirestore.getInstance();
        initViews();
        setupClickListeners();

        String taskId = getIntent().getStringExtra("taskId");

        if (taskId != null && !taskId.isEmpty()) {
            viewModel.getTaskById(taskId).observe(this, updatedTask -> {
                if (updatedTask != null) {
                    this.task = updatedTask;
                    bindDataToUI();
                    checkUserRole(); // Mỗi khi task cập nhật, kiểm tra lại vai trò
                }
            });
            observeAttachments(taskId);
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID của task.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        etCardTitle = findViewById(R.id.etCardTitle);
        etDescription = findViewById(R.id.etDescription);
        llMembersContainer = findViewById(R.id.llMembers);
        llAttachments = findViewById(R.id.llAttachments);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        // Ban đầu, đặt tất cả ở chế độ chỉ đọc
        setEditMode(false);

        // findViewById(R.id.btnAddMember).setVisibility(View.GONE);
        // findViewById(R.id.btnAddAttachment).setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveTask());
        btnCancel.setOnClickListener(v -> finish());
        findViewById(R.id.deadline_container).setOnClickListener(v -> {
            if (isAdmin) showDatePickerDialog();
            else Toast.makeText(this, "Bạn không có quyền chỉnh sửa.", Toast.LENGTH_SHORT).show();
        });

        btnAddAttachment.setOnClickListener(v -> {
            if (isAdmin) showAddAttachmentDialog();
            else Toast.makeText(this, "Chỉ admin mới có thể thêm tài liệu.", Toast.LENGTH_SHORT).show();
        });
    }

    private void bindDataToUI() {
        etCardTitle.setText(task.getTitle());
        etDescription.setText(task.getDescription());
        updateDates();
        updateMembersUI();
    }

    private void observeAttachments(String taskId) {
        viewModel.getDocumentsForTask(taskId).observe(this, documents -> {
            updateAttachmentsUI(documents);
        });
    }

    private void updateAttachmentsUI(List<Document> documents) {
        llAttachments.removeAllViews();
        if (documents == null || documents.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Chưa có tài liệu nào.");
            llAttachments.addView(tv);
        } else {
            for (Document doc : documents) {
                TextView linkView = new TextView(this);
                linkView.setText("🔗 " + doc.getName()); // Hiển thị tên file/link
                linkView.setPadding(0, 8, 0, 8);
                linkView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                linkView.setOnClickListener(v -> {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(doc.getFile_url()));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(this, "Không thể mở link.", Toast.LENGTH_SHORT).show();
                    }
                });
                llAttachments.addView(linkView);
            }
        }
    }

    private void showAddAttachmentDialog() {
        final CharSequence[] options = {"Thêm bằng Link", "Tải lên từ thiết bị", "Hủy"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn phương thức");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Thêm bằng Link")) {
                showAddLinkDialog();
            } else if (options[item].equals("Tải lên từ thiết bị")) {
                filePickerLauncher.launch("*/*");
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showAddLinkDialog() {
        Context context = this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);
        final EditText nameInput = new EditText(context);
        nameInput.setHint("Tên hiển thị (ví dụ: Tài liệu tham khảo)");
        layout.addView(nameInput);
        final EditText urlInput = new EditText(context);
        urlInput.setHint("Dán đường link (URL) vào đây");
        layout.addView(urlInput);
        new AlertDialog.Builder(this)
                .setTitle("Thêm tài liệu bằng Link")
                .setView(layout)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String url = urlInput.getText().toString().trim();
                    if (!name.isEmpty() && !url.isEmpty()) {
                        viewModel.addLinkAttachment(task.getTask_id(), name, url);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }

    private void checkUserRole() {
        if (task == null || task.getGroup_id() == null) return;
        viewModel.getCurrentUserRoleForGroup(task.getGroup_id()).observe(this, role -> {
            this.isAdmin = (role != null && role.equals("admin"));
            setEditMode(this.isAdmin);
        });
    }

    private void setEditMode(boolean isEditable) {
        etCardTitle.setEnabled(isEditable);
        etDescription.setEnabled(isEditable);
        tvEndDate.setClickable(isEditable);

        int visibility = isEditable ? View.VISIBLE : View.GONE;
        btnSave.setVisibility(visibility);
        btnCancel.setVisibility(visibility);

        updateMembersUI();
    }

    private void updateMembersUI() {
        llMembersContainer.removeAllViews();
        Button btnManageMembers = new Button(this);
        int memberCount = (task != null && task.getMembers() != null) ? task.getMembers().size() : 0;

        btnManageMembers.setText("Xem " + memberCount + " thành viên");
        if (isAdmin) {
            btnManageMembers.setText("Xem và quản lý " + memberCount + " thành viên");
        }

        btnManageMembers.setOnClickListener(v -> showMemberListDialog());
        llMembersContainer.addView(btnManageMembers);
    }

    private void showMemberListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Danh sách thành viên");

        Context context = this;
        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        LinearLayout memberListLayout = new LinearLayout(context);
        memberListLayout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(memberListLayout);

        dialogLayout.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f));

        if (isAdmin) {
            Button btnAddNewMember = new Button(context);
            btnAddNewMember.setText("Thêm thành viên mới");
            btnAddNewMember.setOnClickListener(v -> showAddUserByEmailDialog());
            dialogLayout.addView(btnAddNewMember);
        }

        builder.setView(dialogLayout);
        AlertDialog memberDialog = builder.create();

        List<String> memberIds = (task.getMembers() != null) ? task.getMembers() : new ArrayList<>();

        if (memberIds.isEmpty()) {
            TextView tvNoMembers = new TextView(context);
            tvNoMembers.setText("Chưa có thành viên nào.");
            memberListLayout.addView(tvNoMembers);
        } else {
            db.collection("User").whereIn(FieldPath.documentId(), memberIds).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        memberListLayout.removeAllViews();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            User user = document.toObject(User.class);
                            memberListLayout.addView(createMemberViewForDialog(user, document.getId()));
                        }
                    })
                    .addOnFailureListener(e -> {
                        memberListLayout.removeAllViews();
                        TextView errorView = new TextView(context);
                        errorView.setText("Lỗi khi tải danh sách thành viên.");
                        memberListLayout.addView(errorView);
                    });
        }
        memberDialog.show();
    }

    private View createMemberViewForDialog(User user, String userId) {
        LinearLayout memberLayout = new LinearLayout(this);
        memberLayout.setOrientation(LinearLayout.HORIZONTAL);
        memberLayout.setGravity(Gravity.CENTER_VERTICAL);
        memberLayout.setPadding(0, 8, 0, 8);

        TextView tvMemberName = new TextView(this);
        tvMemberName.setText(user != null ? (user.getUsername() != null ? user.getUsername() : user.getEmail()) : userId);
        tvMemberName.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        memberLayout.addView(tvMemberName);

        if (isAdmin) {
            ImageButton btnRemove = new ImageButton(this);
            btnRemove.setImageResource(android.R.drawable.ic_menu_delete);
            btnRemove.setBackground(null);
            btnRemove.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Xóa Thành viên")
                        .setMessage("Bạn có chắc muốn xóa thành viên này khỏi task?")
                        .setPositiveButton("Xóa", (dialog, which) -> viewModel.removeUserFromTask(task.getTask_id(), userId))
                        .setNegativeButton("Hủy", null)
                        .show();
            });
            memberLayout.addView(btnRemove);
        }
        return memberLayout;
    }

    private void showAddUserByEmailDialog() {
        final EditText input = new EditText(this);
        input.setHint("Nhập email người cần thêm");
        new AlertDialog.Builder(this)
                .setTitle("Thêm Thành viên vào Group")
                .setMessage("Lưu ý: Thêm thành viên vào group sẽ cho phép họ thấy tất cả các task trong group này.")
                .setView(input)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (!email.isEmpty()) {
                        viewModel.addUserToGroup(task.getGroup_id(), email);
                        viewModel.addUserToTask(task.getTask_id(), email);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void saveTask() {
        if (task != null) {
            task.setTitle(etCardTitle.getText().toString().trim());
            task.setDescription(etDescription.getText().toString().trim());
            viewModel.updateTask(task);
            Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateDates() {
        if (task != null && task.getCreated_at() != null) {
            tvStartDate.setText(dateFormat.format(task.getCreated_at()));
        } else {
            tvStartDate.setText("Chưa có");
        }
        if (task != null && task.getDeadline() != null) {
            tvEndDate.setText(dateFormat.format(task.getDeadline()));
        } else {
            tvEndDate.setText("Chưa đặt");
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (task.getDeadline() != null) {
            calendar.setTime(task.getDeadline());
        }
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            task.setDeadline(calendar.getTime());
            updateDates();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /*
    private void updateAttachmentsUI() {
        llAttachments.removeAllViews();
        TextView tvInfo = new TextView(this);
        tvInfo.setText("Chưa có tệp đính kèm.");
        llAttachments.addView(tvInfo);
    }
     */
}