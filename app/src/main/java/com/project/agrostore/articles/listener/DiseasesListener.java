package com.project.agrostore.articles.listener;

import com.project.agrostore.articles.model.DiseasesResponse;

public interface DiseasesListener {
    void onDiseaseItemClick(DiseasesResponse response);
}
