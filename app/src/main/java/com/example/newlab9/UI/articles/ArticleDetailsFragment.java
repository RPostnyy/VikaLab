package com.example.newlab9.UI.articles;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.newlab9.Models.Article;
import com.example.newlab9.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ArticleDetailsFragment extends Fragment {

    private static final String TAG = "ArticleDetailsFragment";
    private TextView articleTitle, articleContent, articleDate;
    private ImageView articleImage;
    private Button buttonEditArticle, buttonDeleteArticle;
    private String articleId;
    private boolean isAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_details, container, false);

        articleTitle = view.findViewById(R.id.articleTitle);
        articleImage = view.findViewById(R.id.articleImage);
        articleContent = view.findViewById(R.id.articleContent);
        buttonEditArticle = view.findViewById(R.id.buttonEditArticle);
        buttonDeleteArticle = view.findViewById(R.id.buttonDeleteArticle);
        articleDate = view.findViewById(R.id.articleDate);

        if (getArguments() != null) {
            articleId = getArguments().getString("articleId");
            isAdmin = getArguments().getBoolean("isAdmin");
            loadArticleDetails(articleId);
        } else {
            Log.e(TAG, "No article ID found in arguments");
        }

        if (isAdmin) {
            buttonEditArticle.setOnClickListener(v -> {
                Fragment editArticleFragment = new EditArticleFragment();
                Bundle bundle = new Bundle();
                bundle.putString("articleId", articleId);
                editArticleFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editArticleFragment)
                        .addToBackStack(null)
                        .commit();
            });

            buttonDeleteArticle.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("Удалить статью")
                        .setMessage("Вы уверены, что хотите удалить эту статью?")
                        .setPositiveButton("Да", (dialog, which) -> deleteArticle())
                        .setNegativeButton("Нет", null)
                        .show();
            });
        } else {
            buttonEditArticle.setVisibility(View.GONE);
            buttonDeleteArticle.setVisibility(View.GONE);
        }

        return view;
    }

    private void loadArticleDetails(String articleId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Articles").child(articleId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Article article = dataSnapshot.getValue(Article.class);
                if (article != null) {
                    articleTitle.setText(article.getTitle());
                    articleContent.setText(article.getContent());
                    Glide.with(getContext()).load(article.getImageUrl()).into(articleImage);
                    articleDate.setText(article.getCreationDate()); // Устанавливаем дату создания
                    Log.d(TAG, "Article details loaded successfully");
                } else {
                    Log.e(TAG, "Article not found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load article details", databaseError.toException());
            }
        });
    }

    private void deleteArticle() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Articles").child(articleId);
        ref.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Статья удалена", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Ошибка при удалении статьи", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to delete article", task.getException());
            }
        });
    }
}
