package com.example.newlab9.UI.articles;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newlab9.Models.Article;
import com.example.newlab9.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArticlesFragment extends Fragment implements ArticleAdapter.OnArticleClickListener {

    private static final String TAG = "ArticlesFragment";
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    private boolean isAdmin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_articles, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        articleList = new ArrayList<>();
        articleAdapter = new ArticleAdapter(articleList, getContext(), this);
        recyclerView.setAdapter(articleAdapter);

        if (getArguments() != null) {
            isAdmin = getArguments().getBoolean("isAdmin");
        }

        loadArticles();

        return view;
    }

    private void loadArticles() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Articles");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Article article = snapshot.getValue(Article.class);
                    if (article != null) {
                        article.setId(snapshot.getKey());
                        articleList.add(article);
                    }
                }
                if (articleList.isEmpty()) {
                    Log.w(TAG, "No articles found in the database");
                } else {
                    Log.d(TAG, "Articles loaded successfully: " + articleList.size());
                }
                articleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load articles", databaseError.toException());
            }
        });
    }

    @Override
    public void onArticleClick(Article article) {
        Fragment articleDetailsFragment = new ArticleDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("articleId", article.getId());
        bundle.putBoolean("isAdmin", isAdmin); // Передаем информацию о роли
        articleDetailsFragment.setArguments(bundle);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, articleDetailsFragment)
                .addToBackStack(null)
                .commit();
    }
}
