package com.project.agrostore.manufacture.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.databinding.ProductItemLayoutBinding;
import com.project.agrostore.shopping.listener.OnProductListener;
import com.project.agrostore.shopping.model.ProductModel;
import com.project.agrostore.utils.Constants;

public class ProductViewHolder extends RecyclerView.ViewHolder {
    final ProductItemLayoutBinding binding;

    public ProductViewHolder(ProductItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void setData(ProductModel productModel, OnProductListener clickListener) {
        binding.tvProductName.setText(productModel.getTitle());
        binding.tvProductPrice.setText("₹" + productModel.getPrice());
        Constants.bindImage(
                binding.ivProductImage,
                productModel.getImageUrl(),
                binding.ivProductImage
        );
        binding.crdProductView.setOnClickListener(v -> clickListener.onProductClick(productModel));

    }
}