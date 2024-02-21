package com.project.agrostore.articles.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.articles.listener.ExpandClickListener;
import com.project.agrostore.articles.model.HowToExpandResponse;
import com.project.agrostore.articles.viewholder.ArticleListViewHolder;
import com.project.agrostore.databinding.ArticleItemLayoutBinding;

import java.util.ArrayList;

public class HowToExpandAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {
    private final ArrayList<HowToExpandResponse> howToExpandResponseArrayList;
    private final ExpandClickListener listener;

    public HowToExpandAdapter(ArrayList<HowToExpandResponse> howToExpandResponseArrayList, ExpandClickListener listener) {
        this.howToExpandResponseArrayList = howToExpandResponseArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleListViewHolder(ArticleItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int position) {
        HowToExpandResponse cropsResponses = howToExpandResponseArrayList.get(position);
        holder.bindHowToExpandData(cropsResponses, listener);
    }

    @Override
    public int getItemCount() {
        return howToExpandResponseArrayList.size();
    }
}
