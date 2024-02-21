package com.project.agrostore.taskmanager.listener;

import com.project.agrostore.db.FarmerModel;

public interface OnItemClickListener {
    void markTaskCompleted(FarmerModel model);

    void onDeleteClick(FarmerModel model);
}