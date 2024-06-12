package com.example.newlab9.UI.articles;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newlab9.Models.Article;
import com.example.newlab9.R;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private static final String TAG = "ArticleAdapter";
    private List<Article> articleList;
    private Context context;
    private OnArticleClickListener onArticleClickListener;

    public ArticleAdapter(List<Article> articleList, Context context, OnArticleClickListener onArticleClickListener) {
        this.articleList = articleList;
        this.context = context;
        this.onArticleClickListener = onArticleClickListener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.articleTitle.setText(article.getTitle());
        Glide.with(context).load(article.getImageUrl()).into(holder.articleImage);

        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "Article clicked: " + article.getTitle());
            onArticleClickListener.onArticleClick(article);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView articleTitle;
        ImageView articleImage;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            articleTitle = itemView.findViewById(R.id.articleTitle);
            articleImage = itemView.findViewById(R.id.articleImage);
        }
    }

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }
}
