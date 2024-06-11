package com.example.newlab9.UI.home;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.newlab9.LoginActivity;
import com.example.newlab9.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private EditText edChangeName, edChangeSurname, edChangeDate, edNewPassword, edRepeatPassword;
    private ImageView ivChangeAvatar;
    private Button btnSaveChanges, btnChangeAvatar, btnExit;
    private Uri avatarUri;
    private String avatarUrl;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        edChangeName = view.findViewById(R.id.edChangeName);
        edChangeSurname = view.findViewById(R.id.edChangeSurname);
        edChangeDate = view.findViewById(R.id.edChangeDate);
        edNewPassword = view.findViewById(R.id.edNewPassword);
        edRepeatPassword = view.findViewById(R.id.edRepeatPassword);
        ivChangeAvatar = view.findViewById(R.id.ivChangeAvatar);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnChangeAvatar = view.findViewById(R.id.btnChangeAvatar);
        btnExit = view.findViewById(R.id.btnExit);

        edChangeDate.setOnClickListener(v -> showDatePickerDialog());

        loadUserInfo();

        // Set onClick listeners
        btnSaveChanges.setOnClickListener(v -> saveChanges());
        btnChangeAvatar.setOnClickListener(v -> openGallery());
        btnExit.setOnClickListener(v -> signOut());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                edChangeDate.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadUserInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        reference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("userName").getValue(String.class);
                        String surname = dataSnapshot.child("userSecondName").getValue(String.class);
                        String dateOfBD = dataSnapshot.child("dateOfBD").getValue(String.class);
                        avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);

                        edChangeName.setText(name);
                        edChangeSurname.setText(surname);
                        edChangeDate.setText(dateOfBD);
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Picasso.get().load(avatarUrl).into(ivChangeAvatar);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Ошибка загрузки данных пользователя", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null) {
            avatarUri = data.getData();
            ivChangeAvatar.setImageURI(avatarUri);
        }
    }

    private void saveChanges() {
        if (!validateInputs()) {
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        Map<String, Object> updates = new HashMap<>();
        updates.put("userName", edChangeName.getText().toString());
        updates.put("userSecondName", edChangeSurname.getText().toString());
        updates.put("dateOfBD", edChangeDate.getText().toString());

        if (!edNewPassword.getText().toString().isEmpty()) {
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(edNewPassword.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Пароль успешно обновлен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Ошибка обновления пароля", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (avatarUri != null) {
            uploadAvatar(uid, updates);
        } else {
            reference.updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validateInputs() {
        String name = edChangeName.getText().toString().trim();
        String surname = edChangeSurname.getText().toString().trim();
        String dateOfBirth = edChangeDate.getText().toString().trim();
        String newPassword = edNewPassword.getText().toString().trim();
        String repeatPassword = edRepeatPassword.getText().toString().trim();

        if (name.isEmpty() || surname.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!name.matches("[a-zA-Zа-яА-Я]+")) {
            Toast.makeText(getActivity(), "Имя должно содержать только буквы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isDateOfBirthValid(dateOfBirth)) {
            Toast.makeText(getActivity(), "Дата рождения не может быть в будущем", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!newPassword.isEmpty() && !newPassword.equals(repeatPassword)) {
            Toast.makeText(getActivity(), "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isDateOfBirthValid(String dateOfBirth) {
        try {
            String[] parts = dateOfBirth.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]) - 1; // Month is 0-based in Calendar
            int day = Integer.parseInt(parts[2]);

            Calendar dob = Calendar.getInstance();
            dob.set(year, month, day);

            Calendar today = Calendar.getInstance();
            return !dob.after(today);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void uploadAvatar(String uid, Map<String, Object> updates) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars/" + uid);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), avatarUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> avatarTask) {
                    if (avatarTask.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> uriTask) {
                                if (uriTask.isSuccessful()) {
                                    updates.put("avatarUrl", uriTask.getResult().toString());
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                    reference.updateChildren(updates).addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Ошибка обновления данных", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Ошибка получения URL аватарки", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "Ошибка загрузки аватарки", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}
