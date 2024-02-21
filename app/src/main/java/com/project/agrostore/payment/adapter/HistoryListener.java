package com.project.agrostore.payment.adapter;

import com.project.agrostore.payment.model.PaymentModel;

public interface HistoryListener {
    void onTransactionRemovedClick(PaymentModel paymentModel);
}
