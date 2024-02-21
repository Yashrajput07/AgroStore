package com.project.agrostore.articles.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.articles.listener.FlowerClickListener;
import com.project.agrostore.articles.model.FlowersResponse;
import com.project.agrostore.articles.viewholder.ArticleListViewHolder;
import com.project.agrostore.databinding.ArticleItemLayoutBinding;

import java.util.ArrayList;

public class FlowersAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {
    private final ArrayList<FlowersResponse> flowersResponseArrayList;
    private final FlowerClickListener listener;

    public FlowersAdapter(ArrayList<FlowersResponse> flowersResponseArrayList, FlowerClickListener listener) {
        this.flowersResponseArrayList = flowersResponseArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArticleListViewHolder(ArticleItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int position) {
        FlowersResponse response = flowersResponseArrayList.get(position);
        holder.bindFlowersData(response, listener);
    }

    @Override
    public int getItemCount() {
        return flowersResponseArrayList.size();
    }
}
