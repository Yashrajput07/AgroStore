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
import com.project.agrostoreapp.articles.adapter.FlowersAdapter;
import com.project.agrostoreapp.articles.listener.FlowerClickListener;
import com.project.agrostoreapp.articles.model.FlowersResponse;
import com.project.agrostoreapp.databinding.ActivityFlowersBinding;
import com.project.agrostoreapp.ui.repository.agrostoreRepositoryImpl;
import com.project.agrostoreapp.utils.CustomMultiColorProgressBar;
import com.project.agrostoreapp.viewmodel.AgroViewModel;
import com.project.agrostoreapp.viewmodel.agrostoreViewModelFactory;

import java.util.ArrayList;

public class FlowersActivity extends AppCompatActivity implements FlowerClickListener {
    private final ArrayList<FlowersResponse> flowersResponsesList = new ArrayList<>();
    private ActivityFlowersBinding binding;
    private CustomMultiColorProgressBar progressBar;
    private AgroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_flowers);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.flowers));
        initializeagrostoreViewModel();
        progressBar = new CustomMultiColorProgressBar(this, getString(R.string.loader_message));
        getFlowersListFromApi();
    }

    private void getFlowersListFromApi() {
        progressBar.showProgressBar();
        viewModel.getFlowersResponseLivedata();
        viewModel.observeFlowersLiveData.observe(this, resource -> {
            switch (resource.status) {
                case ERROR:
                    progressBar.hideProgressBar();
                    binding.rvFlowers.setVisibility(View.GONE);
                    binding.tvNoFlowersDataFound.setVisibility(View.VISIBLE);
                    binding.tvNoFlowersDataFound.setText(resource.message);
                    break;
                case LOADING:
                    progressBar.showProgressBar();
                    break;
                case SUCCESS:
                    if (resource.data != null) {
                        flowersResponsesList.clear();
                        flowersResponsesList.addAll(resource.data);
                        progressBar.hideProgressBar();
                        binding.rvFlowers.setVisibility(View.VISIBLE);
                        setRecyclerView();
                    } else {
                        binding.tvNoFlowersDataFound.setVisibility(View.VISIBLE);
                        binding.tvNoFlowersDataFound.setText(getString(R.string.no_data_found));
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
        FlowersAdapter flowersAdapter = new FlowersAdapter(flowersResponsesList, this);
        binding.rvFlowers.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvFlowers.setAdapter(flowersAdapter);
    }

    @Override
    public void onFlowersClick(FlowersResponse response) {
        Intent intent = new Intent(FlowersActivity.this, ArticleDetailsActivity.class);
        intent.putExtra("flowerItemResponse", response);
        intent.putExtra("isFlowersResponse", true);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}