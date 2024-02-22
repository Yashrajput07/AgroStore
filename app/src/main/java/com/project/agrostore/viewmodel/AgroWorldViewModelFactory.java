package com.project.agrostore.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.project.agrostore.ui.repository.agrostoreRepositoryImpl;

public class agrostoreViewModelFactory implements ViewModelProvider.Factory {
    private agrostoreRepositoryImpl agrostoreRepository;
    private Context context;

    public agrostoreViewModelFactory(agrostoreRepositoryImpl agrostoreRepository, Context context) {
        this.agrostoreRepository = agrostoreRepository;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        return (T) new AgroViewModel(agrostoreRepository, context);
    }
}
