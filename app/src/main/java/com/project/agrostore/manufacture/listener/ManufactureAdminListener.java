package com.project.agrostore.manufacture.listener;

import com.project.agrostore.shopping.model.ProductModel;

public interface ManufactureAdminListener {
    void performOnCardClickAction(ProductModel productModel);

    void performEditAction(ProductModel productModel, int position);

    void performDeleteAction(ProductModel productModel, int position);
}
