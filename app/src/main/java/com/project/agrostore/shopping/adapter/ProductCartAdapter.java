package com.project.agrostore.shopping.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.databinding.CartItemLayoutBinding;
import com.project.agrostore.shopping.listener.ItemCartActionListener;
import com.project.agrostore.shopping.model.ProductModel;
import com.project.agrostore.shopping.viewholder.CartViewHolder;

import java.util.List;

public class ProductCartAdapter extends RecyclerView.Adapter<CartViewHolder> {
    private final List<ProductModel> productModelArrayList;
    private final ItemCartActionListener listener;

    public ProductCartAdapter(List<ProductModel> productModelList, ItemCartActionListener listener) {
        this.productModelArrayList = productModelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartViewHolder(CartItemLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);
        holder.bindItemData(productModel, listener);
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }
}
