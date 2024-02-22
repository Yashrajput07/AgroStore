package com.project.agrostore.ui.repository;

import static com.project.agrostore.utils.Constants.BASE_URL_SHEET_DB;
import static com.project.agrostore.utils.Constants.BASE_URL_WEATHER;

import com.project.agrostore.articles.model.CropsResponse;
import com.project.agrostore.articles.model.DiseasesResponse;
import com.project.agrostore.articles.model.FlowersResponse;
import com.project.agrostore.articles.model.FruitsResponse;
import com.project.agrostore.articles.model.HowToExpandResponse;
import com.project.agrostore.articles.model.InsectControlResponse;
import com.project.agrostore.network.APIService;
import com.project.agrostore.network.Network;
import com.project.agrostore.weather.model.weather_data.WeatherResponse;
import com.project.agrostore.weather.model.weatherlist.WeatherDatesResponse;

import java.util.List;

import retrofit2.Call;

public class agrostoreRepositoryImpl implements APIService {
    APIService apiService = Network.getInstance(BASE_URL_SHEET_DB);

    @Override
    public Call<WeatherResponse> getWeatherData(Double lat, Double lon, String apiKey) {
        apiService = Network.getInstance(BASE_URL_WEATHER);
        return apiService.getWeatherData(lat, lon, apiKey);
    }

    @Override
    public Call<WeatherDatesResponse> getWeatherForecastData(Double lat, Double lon, String apiKey) {
        apiService = Network.getInstance(BASE_URL_WEATHER);
        return apiService.getWeatherForecastData(lat, lon, apiKey);
    }

    @Override
    public Call<List<DiseasesResponse>> getDiseasesList() {
        return apiService.getDiseasesList();
    }


    @Override
    public Call<List<DiseasesResponse>> getLocalizedDiseasesList() {
        return apiService.getLocalizedDiseasesList();
    }

    @Override
    public Call<List<FruitsResponse>> getFruitsFromDB() {
        return apiService.getFruitsFromDB();
    }

    @Override
    public Call<List<FruitsResponse>> getLocalizedFruitsList() {
        return apiService.getLocalizedFruitsList();
    }

    @Override
    public Call<List<FlowersResponse>> getFlowersList() {
        return apiService.getFlowersList();
    }

    @Override
    public Call<List<FlowersResponse>> getLocalizedFlowersList() {
        return apiService.getLocalizedFlowersList();
    }

    @Override
    public Call<List<CropsResponse>> getListOfCrops() {
        return apiService.getListOfCrops();
    }

    @Override
    public Call<List<CropsResponse>> getLocalizedCropsList() {
        return apiService.getLocalizedCropsList();
    }

    @Override
    public Call<List<HowToExpandResponse>> getListOfHowToExpandData() {
        return apiService.getListOfHowToExpandData();
    }

    @Override
    public Call<List<HowToExpandResponse>> getLocalizedHowToExpandData() {
        return apiService.getLocalizedHowToExpandData();
    }

    @Override
    public Call<List<InsectControlResponse>> getInsectAndControlList() {
        return apiService.getInsectAndControlList();
    }

    @Override
    public Call<List<InsectControlResponse>> getLocalizedInsectAndControlList() {
        return apiService.getLocalizedInsectAndControlList();
    }
}
