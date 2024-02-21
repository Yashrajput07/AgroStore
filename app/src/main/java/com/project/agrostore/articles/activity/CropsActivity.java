package com.project.agrostoreapp.articles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.project.agrostoreapp.R;
import com.project.agrostoreapp.articles.adapter.CropsAdapter;
import com.project.agrostoreapp.articles.listener.CropsClickListener;
import com.project.agrostoreapp.articles.model.CropsResponse;
import com.project.agrostoreapp.databinding.ActivityCropsBinding;
import com.project.agrostoreapp.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostoreapp.utils.CustomMultiColorProgressBar;
import com.project.agrostoreapp.utils.Permissions;
import com.project.agrostoreapp.viewmodel.AgroViewModel;
import com.project.agrostoreapp.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;


public class CropsActivity extends AppCompatActivity implements CropsClickListener {
    private final ArrayList<CropsResponse> cropsResponseArrayList = new ArrayList<>();
    private ActivityCropsBinding binding;
    private AgroViewModel viewModel;
    private CustomMultiColorProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crops);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.crops));
        initializeagrostoreViewModel();
        progressBar = new CustomMultiColorProgressBar(this, getString(R.string.loader_message));
        if (Permissions.checkConnection(this)) {
            getCropsListFromApi();
        }
    }

    private void getCropsListFromApi() {
        progressBar.showProgressBar();
        viewModel.getCropsResponseLivedata();
        viewModel.observeCropsLiveData.observe(this, resource -> {
            switch (resource.status) {
                case ERROR:
                    progressBar.hideProgressBar();
                    binding.rvCrops.setVisibility(View.GONE);
                    binding.tvNoCropsDataFound.setVisibility(View.VISIBLE);
                    binding.tvNoCropsDataFound.setText(resource.message);
                    break;
                case LOADING:
                    progressBar.showProgressBar();
                    break;
                case SUCCESS:
                    progressBar.hideProgressBar();
                    if (resource.data != null) {
                        cropsResponseArrayList.clear();
                        cropsResponseArrayList.addAll(resource.data);
                        binding.rvCrops.setVisibility(View.VISIBLE);
                        setRecyclerView();
                    } else {
                        binding.rvCrops.setVisibility(View.GONE);
                        binding.tvNoCropsDataFound.setVisibility(View.VISIBLE);
                        binding.tvNoCropsDataFound.setText(R.string.no_data_found);
                    }
                    break;
            }
        });
    }

    private void setRecyclerView() {
        CropsAdapter cropsAdapter = new CropsAdapter(cropsResponseArrayList, this);
        binding.rvCrops.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvCrops.setAdapter(cropsAdapter);
    }

    @Override
    public void onCropsClick(CropsResponse response) {
        Intent intent = new Intent(CropsActivity.this, ArticleDetailsActivity.class);
        intent.putExtra("itemResponse", response);
        intent.putExtra("isCropResponse", true);
        startActivity(intent);
    }

    public void initializeagrostoreViewModel() {
        agrostoreRepositoryImpl agrostoreRepository = new agrostoreRepositoryImpl();
        viewModel = ViewModelProviders.of(this, new agrostoreViewModelFactory(agrostoreRepository, this)).get(AgroViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}