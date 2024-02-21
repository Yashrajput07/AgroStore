package com.project.agrostore.articles.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.articles.listener.InsectControlListener;
import com.project.agrostore.articles.model.InsectControlResponse;
import com.project.agrostore.articles.viewholder.DiseasesViewHolder;
import com.project.agrostore.databinding.ArticleItemLayoutBinding;

import java.util.List;

public class InsectControlAdapter extends RecyclerView.Adapter<DiseasesViewHolder> {
    private final List<InsectControlResponse> insectControlResponseList;
    private final InsectControlListener listener;

    public InsectControlAdapter(List<InsectControlResponse> insectControlResponseList, InsectControlListener listener) {
        this.insectControlResponseList = insectControlResponseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DiseasesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DiseasesViewHolder(ArticleItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiseasesViewHolder holder, int position) {
        InsectControlResponse response = insectControlResponseList.get(position);
        holder.bindInsectControlData(response, listener);
    }

    @Override
    public int getItemCount() {
        return insectControlResponseList.size();
    }
}
