package com.project.agrostore.weather.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.agrostore.weather.listener.WeatherForecastListener;
import com.project.agrostore.R;
import com.project.agrostore.databinding.ForecastItemLayoutBinding;
import com.project.agrostore.weather.model.weatherlist.ListItem;

public class WeatherForecastViewHolder extends RecyclerView.ViewHolder {
    private final ForecastItemLayoutBinding binding;

    public WeatherForecastViewHolder(@NonNull ForecastItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindForecastData(ListItem item, WeatherForecastListener listener) {
        String iconUrl = "http://openweathermap.org/img/wn/" + item.getWeather().get(0).getIcon() + "@2x.png";
        String mainTemp = String.format("%.0f", (item.getMain().getTemp() + 0.01) - 273.15);
        String[] dates = item.getDtTxt().split(" ");
        binding.tvForecastDate.setText(dates[0] + "\n" + dates[1]);
        binding.tvForecastTemp.setText(mainTemp + "°C");
        binding.tvForecastStatus.setText(item.getWeather().get(0).getDescription());
        Glide.with(binding.ivForecastIcon).load(iconUrl).placeholder(R.drawable.weather).dontAnimate().into(binding.ivForecastIcon);

        binding.crdForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onForecastWeatherCardClick(item.getWeather().get(0).getDescription());
            }
        });

    }
}
