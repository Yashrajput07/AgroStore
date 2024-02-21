package com.project.agrostore.shopping.listener;

import com.project.agrostore.shopping.model.ProductModel;

public interface ItemCartActionListener {
    void onIncreaseItemClick(ProductModel productModel, int quantity);

    void onDecreaseItemClick(ProductModel productModel, int quantity);

    void onRemovedItemClick(ProductModel productModel, int position);

}
