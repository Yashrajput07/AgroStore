package com.project.agrostore.articles.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.articles.listener.CropsClickListener;
import com.project.agrostore.articles.listener.ExpandClickListener;
import com.project.agrostore.articles.listener.FlowerClickListener;
import com.project.agrostore.articles.listener.FruitsClickListener;
import com.project.agrostore.articles.model.CropsResponse;
import com.project.agrostore.articles.model.FlowersResponse;
import com.project.agrostore.articles.model.FruitsResponse;
import com.project.agrostore.articles.model.HowToExpandResponse;
import com.project.agrostore.databinding.ArticleItemLayoutBinding;
import com.project.agrostore.utils.Constants;

public class ArticleListViewHolder extends RecyclerView.ViewHolder {

    private final ArticleItemLayoutBinding binding;

    public ArticleListViewHolder(ArticleItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindFlowersData(FlowersResponse responses, FlowerClickListener listener) {

        binding.tvTitleArticleItem.setText(responses.getTitle());
        binding.tvSeasonArticleItem.setText(responses.getSeason());
        Constants.bindImage(binding.getRoot(), responses.getImageLink(), binding.ivArticleItem);
        binding.crdArticleItem.setOnClickListener(v -> {
            listener.onFlowersClick(responses);
        });
    }

    public void bindCropsData(CropsResponse responses, CropsClickListener listener) {
        binding.tvTitleArticleItem.setText(responses.getTitle());
        binding.tvSeasonArticleItem.setText(responses.getSeason());
        Constants.bindImage(binding.getRoot(), responses.getImageLink(), binding.ivArticleItem);
        binding.crdArticleItem.setOnClickListener(v -> {
            listener.onCropsClick(responses);
        });
    }

    public void bindHowToExpandData(HowToExpandResponse responses, ExpandClickListener listener) {
        binding.tvTitleArticleItem.setText(responses.getCropName());
        binding.tvSeasonArticleItem.setText(responses.getSeason());
        Constants.bindImage(binding.getRoot(), responses.getImageLink(), binding.ivArticleItem);
        binding.crdArticleItem.setOnClickListener(v -> {
            listener.onExpandItemClick(responses);
        });
    }

    public void bindFruitsData(FruitsResponse responses, FruitsClickListener listener) {
        binding.tvTitleArticleItem.setText(responses.getTitle());
        binding.tvSeasonArticleItem.setText(responses.getSeason());
        Constants.bindImage(binding.getRoot(), responses.getImageLink(), binding.ivArticleItem);
        binding.crdArticleItem.setOnClickListener(v -> {
            listener.onFruitClick(responses);
        });
    }

}
