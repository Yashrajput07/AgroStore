package com.project.agrostore.articles.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.articles.listener.CropsClickListener;
import com.project.agrostore.articles.model.CropsResponse;
import com.project.agrostore.articles.viewholder.ArticleListViewHolder;
import com.project.agrostore.databinding.ArticleItemLayoutBinding;

import java.util.ArrayList;

public class CropsAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {
    private final ArrayList<CropsResponse> cropsResponseArrayList;
    private final CropsClickListener listener;

    public CropsAdapter(ArrayList<CropsResponse> cropsResponseArrayList, CropsClickListener listener) {
        this.cropsResponseArrayList = cropsResponseArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleListViewHolder(ArticleItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int position) {
        CropsResponse cropsResponses = cropsResponseArrayList.get(position);
        holder.bindCropsData(cropsResponses, listener);
    }

    @Override
    public int getItemCount() {
        return cropsResponseArrayList.size();
    }
}
