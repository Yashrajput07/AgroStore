package com.project.agrostore.payment.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.project.agrostore.databinding.TransactionItemLayoutBinding;
import com.project.agrostore.payment.model.PaymentModel;
import com.project.agrostore.utils.Constants;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    final TransactionItemLayoutBinding binding;

    public HistoryViewHolder(TransactionItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindHistoryData(PaymentModel paymentModel) {
        binding.tvTransTitle.setText(paymentModel.getProductName());
        binding.tvTransID.setText("Id: " + paymentModel.getPaymentID());
        binding.tvTransStatus.setText("Status: " + paymentModel.getPaymentStatus());
        binding.tvTransPrice.setText(" " + paymentModel.getProductPrice());
        Constants.bindImage(binding.ivHistoryProduct, paymentModel.getProductImage(), binding.ivHistoryProduct);
    }
}
