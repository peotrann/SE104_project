package com.example.SE104_DoAn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment {

    private TextView tvAccountName, tvAccountEmail;
    private ImageButton btnAccountMenu;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_LOGGED_IN = "isLoggedIn";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Khởi tạo SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, requireActivity().MODE_PRIVATE);

        // Khởi tạo các view
        tvAccountName = view.findViewById(R.id.tvAccountName);
        tvAccountEmail = view.findViewById(R.id.tvAccountEmail);
        btnAccountMenu = view.findViewById(R.id.btnAccountMenu);

        // Hiển thị thông tin tài khoản (giả lập)
        tvAccountName.setText("Người dùng");
        tvAccountEmail.setText("user@example.com");

        // Thiết lập option menu
        btnAccountMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(requireContext(), btnAccountMenu);
            popupMenu.getMenu().add(0, 1, 0, "Thêm tài khoản");
            popupMenu.getMenu().add(0, 2, 0, "Đăng xuất");

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    // Xử lý Thêm tài khoản (giả lập)
                    Toast.makeText(requireContext(), "Chức năng Thêm tài khoản chưa được triển khai", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (item.getItemId() == 2) {
                    // Xử lý Đăng xuất
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(KEY_LOGGED_IN, false);
                    editor.apply();

                    Intent intent = new Intent(requireActivity(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });

        return view;
    }
}