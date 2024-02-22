package com.project.agrostore.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.project.agrostore.BuildConfig;
import com.project.agrostore.R;
import com.project.agrostore.databinding.ActivityWeatherForecastBinding;
import com.project.agrostore.ui.repository.AgroStoreRepositoryImpl;
import com.project.agrostore.utils.Constants;
import com.project.agrostore.utils.CustomMultiColorProgressBar;
import com.project.agrostore.utils.Permissions;
import com.project.agrostore.viewmodel.AgroViewModel;
import com.project.agrostore.viewmodel.AgroStoreViewModelFactory;
import com.project.agrostore.weather.adapter.WeatherForecastAdapter;
import com.project.agrostore.weather.listener.WeatherForecastListener;
import com.project.agrostore.weather.model.weatherlist.ListItem;

import java.util.ArrayList;

public class WeatherForecastActivity extends AppCompatActivity implements WeatherForecastListener {
    private final ArrayList<ListItem> forecastItemArrayList = new ArrayList<>();
    private ActivityWeatherForecastBinding binding;
    private CustomMultiColorProgressBar progressBar;
    private AgroViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weather_forecast);
        initializeAgroStoreViewModel();
        progressBar = new CustomMultiColorProgressBar(this, getString(R.string.loader_message));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.forecast_weather);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0.0);
        double lon = intent.getDoubleExtra("lon", 0.0);
        if (lat != 0.0 && lon != 0.0 && Permissions.checkConnection(this)) {
            callForecastApiService(lat, lon);
        }
    }

    private void callForecastApiService(double lat, double lon) {
        progressBar.showProgressBar();
        viewModel.performWeatherForecastRequest(lat, lon, BuildConfig.API_KEY);
        viewModel.observeWeatherDateResourceLiveData.observe(this, weatherDatesResponseResource -> {
            switch (weatherDatesResponseResource.status) {
                case ERROR:
                    progressBar.hideProgressBar();
                    binding.recyclerViewForecast.setVisibility(View.GONE);
                    binding.tvForecastNoDataFound.setVisibility(View.VISIBLE);
                    binding.tvForecastNoDataFound.setText(weatherDatesResponseResource.message);
                    break;
                case LOADING:
                    break;
                case SUCCESS:
                    progressBar.hideProgressBar();
                    if (weatherDatesResponseResource.data != null) {
                        forecastItemArrayList.clear();
                        forecastItemArrayList.addAll(weatherDatesResponseResource.data.getList());
                        setUpRecyclerView();
                    } else {
                        binding.recyclerViewForecast.setVisibility(View.GONE);
                        binding.tvForecastNoDataFound.setVisibility(View.VISIBLE);
                        binding.tvForecastNoDataFound.setText(weatherDatesResponseResource.message);
                    }
            }
        });
    }

    private void setUpRecyclerView() {
        WeatherForecastAdapter forecastAdapter = new WeatherForecastAdapter(forecastItemArrayList, this);
        binding.recyclerViewForecast.setAdapter(forecastAdapter);
        binding.recyclerViewForecast.setLayoutManager(new GridLayoutManager(WeatherForecastActivity.this, 2));
    }

    public void initializeAgroStoreViewModel() {
        AgroStoreRepositoryImpl agroStoreRepository = new AgroStoreRepositoryImpl();
        viewModel = ViewModelProviders.of(this, new AgroStoreViewModelFactory(agroStoreRepository, this)).get(AgroViewModel.class);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onForecastWeatherCardClick(String description) {
        Constants.showToast(this, description);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}