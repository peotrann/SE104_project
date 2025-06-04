package com.example.SE104_DoAn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CardDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_UPDATE_CARD = 100;

    private EditText etCardTitle;
    private EditText etDescription;
    private LinearLayout llMembers;
    private Button btnAddMember;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private LinearLayout llAttachments;
    private Button btnAddAttachment;
    private Button btnSave;
    private Card card;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        // Khởi tạo các view
        etCardTitle = findViewById(R.id.etCardTitle);
        etDescription = findViewById(R.id.etDescription);
        llMembers = findViewById(R.id.llMembers);
        btnAddMember = findViewById(R.id.btnAddMember);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvEndDate = findViewById(R.id.tvEndDate);
        llAttachments = findViewById(R.id.llAttachments);
        btnAddAttachment = findViewById(R.id.btnAddAttachment);
        btnSave = findViewById(R.id.btnSave);

        // Nhận dữ liệu từ Intent
        card = getIntent().getParcelableExtra("card");
        int position = getIntent().getIntExtra("position", -1);
        int listPosition = getIntent().getIntExtra("listPosition", -1);

        if (card != null) {
            etCardTitle.setText(card.getTitle());
            etDescription.setText(card.getDescription());
            updateMembersList();
            updateAttachmentsList();
            updateDates();
        }

        // Xử lý thêm thành viên
        btnAddMember.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Thêm thành viên");

            final EditText input = new EditText(this);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String memberName = input.getText().toString();
                if (!memberName.isEmpty()) {
                    card.addMember(memberName);
                    updateMembersList();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Xử lý chọn ngày bắt đầu
        tvStartDate.setOnClickListener(v -> showDatePicker(true));

        // Xử lý chọn ngày kết thúc
        tvEndDate.setOnClickListener(v -> showDatePicker(false));

        // Xử lý thêm tệp đính kèm (giả lập)
        btnAddAttachment.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Thêm tệp đính kèm");

            final EditText input = new EditText(this);
            input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            input.setHint("Nhập tên tệp (giả lập)");
            builder.setView(input);

            builder.setPositiveButton("Thêm", (dialog, which) -> {
                String attachmentName = input.getText().toString();
                if (!attachmentName.isEmpty()) {
                    card.addAttachment(attachmentName);
                    updateAttachmentsList();
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        // Xử lý lưu các thay đổi
        btnSave.setOnClickListener(v -> {
            if (card != null) {
                card.setTitle(etCardTitle.getText().toString());
                card.setDescription(etDescription.getText().toString());

                Intent resultIntent = new Intent();
                resultIntent.putExtra("updatedCard", card);
                resultIntent.putExtra("position", position); // Truyền lại position
                resultIntent.putExtra("listPosition", listPosition); // Truyền lại listPosition
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void updateMembersList() {
        llMembers.removeAllViews();
        for (String member : card.getMembers()) {
            TextView tvMember = new TextView(this);
            tvMember.setText(member);
            tvMember.setPadding(0, 0, 0, 8);
            llMembers.addView(tvMember);
        }
    }

    private void updateAttachmentsList() {
        llAttachments.removeAllViews();
        for (String attachment : card.getAttachments()) {
            TextView tvAttachment = new TextView(this);
            tvAttachment.setText(attachment);
            tvAttachment.setPadding(0, 0, 0, 8);
            llAttachments.addView(tvAttachment);
        }
    }

    private void updateDates() {
        tvStartDate.setText(card.getStartDate() != null ? dateFormat.format(card.getStartDate()) : "Chưa đặt");
        tvEndDate.setText(card.getEndDate() != null ? dateFormat.format(card.getEndDate()) : "Chưa đặt");
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    Date selectedDate = calendar.getTime();
                    if (isStartDate) {
                        card.setStartDate(selectedDate);
                    } else {
                        card.setEndDate(selectedDate);
                    }
                    updateDates();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}