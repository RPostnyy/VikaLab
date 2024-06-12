package com.example.newlab9.UI.articles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.newlab9.Models.Article;
import com.example.newlab9.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class EditArticleFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "EditArticleFragment";

    private EditText editTextTitle, editTextContent;
    private ImageView imageView;
    private Button buttonChooseImage, buttonSave;
    private Uri imageUri;
    private String articleId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_article, container, false);

        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        imageView = view.findViewById(R.id.imageView);
        buttonChooseImage = view.findViewById(R.id.buttonChooseImage);
        buttonSave = view.findViewById(R.id.buttonSave);

        if (getArguments() != null) {
            articleId = getArguments().getString("articleId");
            loadArticleDetails(articleId);
        }

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonSave.setOnClickListener(v -> saveArticle());

        return view;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(imageView);
        }
    }

    private void loadArticleDetails(String articleId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Articles").child(articleId);
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Article article = task.getResult().getValue(Article.class);
                if (article != null) {
                    editTextTitle.setText(article.getTitle());
                    editTextContent.setText(article.getContent());
                    Glide.with(getContext()).load(article.getImageUrl()).into(imageView);
                }
            } else {
                Log.e(TAG, "Failed to load article details", task.getException());
            }
        });
    }

    private void saveArticle() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Articles").child(articleId);
        ref.child("title").setValue(title);
        ref.child("content").setValue(content);

        if (imageUri != null) {
            uploadImageAndSaveArticle(ref);
        } else {
            ref.child("creationDate").setValue(getCurrentDate()); // Устанавливаем дату создания
            Toast.makeText(getContext(), "Статья сохранена", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void uploadImageAndSaveArticle(DatabaseReference ref) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("article_images").child(articleId + ".jpg");
        storageRef.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                storageRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
                    if (uriTask.isSuccessful()) {
                        String imageUrl = uriTask.getResult().toString();
                        ref.child("imageUrl").setValue(imageUrl).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                ref.child("creationDate").setValue(getCurrentDate()); // Устанавливаем дату создания
                                Toast.makeText(getContext(), "Статья сохранена", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack();
                            } else {
                                Log.e(TAG, "Failed to save article", saveTask.getException());
                            }
                        });
                    } else {
                        Log.e(TAG, "Failed to get download URL", uriTask.getException());
                    }
                });
            } else {
                Log.e(TAG, "Image upload failed", task.getException());
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
