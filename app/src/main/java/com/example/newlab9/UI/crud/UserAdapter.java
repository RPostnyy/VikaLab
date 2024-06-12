package com.example.newlab9.UI.crud;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newlab9.Models.User;
import com.example.newlab9.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private String currentUserId;
    private Context context;

    private static final String TAG = "UserAdapter";

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            this.currentUserId = currentUser.getUid();
        }
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userEmail.setText(user.getLogin());
        holder.userRole.setText(user.getRole().equals("admin") ? "Админ" : "Пользователь");

        Log.d(TAG, "onBindViewHolder: position=" + position + ", user=" + user.getLogin() + ", role=" + user.getRole());

        // Проверка, является ли текущий пользователь тем, который отображается
        if (user.getUid().equals(currentUserId)) {
            holder.btnToggleRole.setEnabled(false);
            holder.btnToggleRole.setText(" ");
            holder.btnDeleteUser.setEnabled(false);
        } else {
            holder.btnToggleRole.setEnabled(true);
            holder.btnToggleRole.setText(user.getRole().equals("admin") ? "Сделать пользователем" : "Сделать админом");

            holder.btnToggleRole.setOnClickListener(v -> {
                // Переключение роли
                String newRole = user.getRole().equals("admin") ? "user" : "admin";
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("role");
                ref.setValue(newRole);
                user.setRole(newRole); // Обновляем локально для мгновенного отображения
                Log.d(TAG, "Role changed for user=" + user.getLogin() + " to " + newRole);
                notifyItemChanged(position); // Обновляем элемент списка
            });

            holder.btnDeleteUser.setEnabled(true);  // Ensure button is enabled for non-current users
            holder.btnDeleteUser.setOnClickListener(v -> {
                // Подтверждение удаления
                new AlertDialog.Builder(context)
                        .setTitle("Удалить")
                        .setMessage("Вы уверены, что хотите удалить аккаунт?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                            ref.removeValue();
                            userList.remove(position);
                            notifyItemRemoved(position);
                            Log.d(TAG, "User deleted: " + user.getLogin());
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView userEmail, userRole;
        Button btnToggleRole, btnDeleteUser;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            userRole = itemView.findViewById(R.id.userRole);
            btnToggleRole = itemView.findViewById(R.id.btnToggleRole);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
