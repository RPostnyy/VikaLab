package com.example.newlab9;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.newlab9.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private Uri avatarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.enterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        binding.btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open gallery to select an image
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        binding.btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open camera to take a photo
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 2);
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.enterLogin.getText().toString(), binding.enterPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        saveUserInfo();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                binding.enterDate.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateInputs() {
        String name = binding.enterName.getText().toString().trim();
        String secondName = binding.enterSecondName.getText().toString().trim();
        String email = binding.enterLogin.getText().toString().trim();
        String password = binding.enterPassword.getText().toString().trim();
        String repeatPassword = binding.enterRepeatPassword.getText().toString().trim();
        String dateOfBirth = binding.enterDate.getText().toString().trim();

        if (name.isEmpty() || secondName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty() || dateOfBirth.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!name.matches("[a-zA-Zа-яА-Я]+")) {
            Toast.makeText(this, "Имя должно содержать только буквы", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!secondName.matches("[a-zA-Zа-яА-Я]+")) {
            Toast.makeText(this, "Фамилия должна содержать только буквы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Введите корректный email", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isDateOfBirthValid(dateOfBirth)) {
            Toast.makeText(this, "Дата рождения не может быть в будущем", Toast.LENGTH_SHORT).show();
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

    private void saveUserInfo() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String, String> userInfo = new HashMap<>();
        userInfo.put("email", binding.enterLogin.getText().toString());
        userInfo.put("userName", binding.enterName.getText().toString());
        userInfo.put("userSecondName", binding.enterSecondName.getText().toString());
        userInfo.put("dateOfBD", binding.enterDate.getText().toString());
        userInfo.put("role", "user"); // Добавьте роль пользователя. Админа назначаете вручную через Firebase Console.

        if (avatarUri != null) {
            uploadAvatar(uid, userInfo);
        } else {
            saveToDatabase(uid, userInfo);
        }
    }


    private void uploadAvatar(String uid, HashMap<String, String> userInfo) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("avatars/" + UUID.randomUUID().toString());
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), avatarUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    userInfo.put("avatarUrl", task.getResult().toString());
                                    saveToDatabase(uid, userInfo);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Ошибка получения URL аватарки", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка загрузки аватарки", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToDatabase(String uid, HashMap<String, String> userInfo) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                avatarUri = data.getData();
                binding.avatarImageView.setImageURI(avatarUri);
            } else if (requestCode == 2) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                avatarUri = getImageUri(imageBitmap);
                binding.avatarImageView.setImageBitmap(imageBitmap);
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
