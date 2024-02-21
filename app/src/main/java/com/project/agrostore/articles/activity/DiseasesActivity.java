package com.project.agrostoreapp.articles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.project.agrostoreapp.R;
import com.project.agrostoreapp.articles.adapter.DiseaseAdapter;
import com.project.agrostoreapp.articles.listener.DiseasesListener;
import com.project.agrostoreapp.articles.model.DiseasesResponse;
import com.project.agrostoreapp.databinding.ActivityDiseasesBinding;
import com.project.agrostoreapp.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostoreapp.utils.Constants;
import com.project.agrostoreapp.utils.Permissions;
import com.project.agrostoreapp.viewmodel.AgroViewModel;
import com.project.agrostoreapp.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;

public class DiseasesActivity extends AppCompatActivity implements DiseasesListener {
    private final ArrayList<DiseasesResponse> diseasesResponseArrayList = new ArrayList<>();
    ActivityDiseasesBinding binding;
    private AgroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_diseases);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.diseases));
        initializeagrostoreViewModel();
        if (Permissions.checkConnection(this)) {
            getDiseasesListFromAPI();
        }
    }

    private void getDiseasesListFromAPI() {
        binding.shimmer.startShimmer();
        viewModel.getDiseasesResponseLivedata();
        viewModel.observeDiseaseResponseLivedata.observe(this, resource -> {
            switch (resource.status) {
                case ERROR:
                    binding.shimmer.stopShimmer();
                    binding.shimmer.setVisibility(View.GONE);
                    binding.rvDiseases.setVisibility(View.GONE);
                    binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                    binding.tvNoDataFoundErr.setText(resource.message);
                    break;
                case LOADING:
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        diseasesResponseArrayList.clear();
                        diseasesResponseArrayList.addAll(resource.data);
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.rvDiseases.setVisibility(View.VISIBLE);
                        setRecyclerView();
                    } else {
                        binding.tvNoDataFoundErr.setVisibility(View.VISIBLE);
                        binding.tvNoDataFoundErr.setText(R.string.no_data_found);
                    }
                    break;
            }
        });
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        viewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, this)).get(AgroViewModel.class);
    }

    private void setRecyclerView() {
        DiseaseAdapter diseaseAdapter = new DiseaseAdapter(diseasesResponseArrayList, this);
        binding.rvDiseases.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvDiseases.setAdapter(diseaseAdapter);
    }

    @Override
    public void onDiseaseItemClick(DiseasesResponse response) {
        Intent intent = new Intent(this, DiseasesDetailsActivity.class);
        intent.putExtra("diseasesResponse", response);
        intent.putExtra("isDiseasesResponse", true);
        startActivityForResult(intent, Constants.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Permissions.checkConnection(this)) {
            getDiseasesListFromAPI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}