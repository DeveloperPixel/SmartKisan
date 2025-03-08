package com.example.optimizer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.optimizer.nointernet.WeatherDataParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cz.msebera.android.httpclient.Header;

public class weathercheck extends AppCompatActivity {

    EditText cityname;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    TextView cityCountryTextView, minTempTextView, maxTempTextView, weatherTypeTextView, forecastTextView;
    ImageView weatherIcon;
    ImageButton backButton, searchButton;
    final String API_key = "ae715b713173cc9c7bad9dc91b4d858c";
    private double latitude;
    private double longitude;

    private LinearLayout weatherContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weathercheck);

        cityname = findViewById(R.id.editTextText);
        backButton = findViewById(R.id.backButton);
        searchButton = findViewById(R.id.search_btn);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityCountryTextView = findViewById(R.id.cityCountryTextView);
        minTempTextView = findViewById(R.id.minTempTextView);
        maxTempTextView = findViewById(R.id.maxTempTextView);
        weatherTypeTextView = findViewById(R.id.weatherTypeTextView);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        if (latitude != 0 && longitude != 0) {
            getWeatherByCoordinates(latitude, longitude);
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityname.getText().toString();
                getcitycoordinates(city);
            }
        });


    }

    private void getcitycoordinates(String city) {
        String Geo_link = "http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=1&appid=" + API_key;

        if (city == null || city.trim().isEmpty()) {
            Log.d("TAG", "City name is empty");
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Geo_link, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Handle JSONArray response
                Log.d("WeatherAPIResponse", response.toString());

                if (response.length() > 0) {
                    try {
                        JSONObject cityData = response.getJSONObject(0); // Get the first city data object
                        String cityName = cityData.optString("name");
                        double lat = cityData.optDouble("lat");
                        double lon = cityData.optDouble("lon");
                        String country = cityData.optString("country");
                        String state = cityData.optString("state");

                        getWeatherByCoordinates(lat, lon);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Tag", "Error parsing JSON data");
                    }
                } else {
                    Log.d("Tag", "No data found for the city");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Tag", "Failure in API call: " + responseString);
            }
        });
    }


    private void getWeatherByCoordinates(double lat, double lon) {
        String currentApiUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_key;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(currentApiUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("WeatherCheck", "Weather response: " + response.toString());

                    JSONArray weatherArray = response.getJSONArray("weather");
                    JSONObject weatherObject = weatherArray.getJSONObject(0);
                    String weatherType = weatherObject.getString("description");

                    JSONObject mainObject = response.getJSONObject("main");
                    double tempMinKelvin = mainObject.getDouble("temp_min");
                    double tempMaxKelvin = mainObject.getDouble("temp_max");

                    // Convert from Kelvin to Celsius
                    double tempMinCelsius = tempMinKelvin - 273.15;
                    double tempMaxCelsius = tempMaxKelvin - 273.15;

                    String cityName = response.getString("name");
                    String countryName = response.getJSONObject("sys").getString("country");

                    cityCountryTextView.setText(String.format("%s, %s", cityName, countryName));
                    minTempTextView.setText(String.format("Min: %.1f°C", tempMinCelsius));
                    maxTempTextView.setText(String.format("Max: %.1f°C", tempMaxCelsius));
                    weatherTypeTextView.setText(weatherType);

                    String iconCode = weatherObject.getString("icon");
                    String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                    //Glide.with(weathercheck.this).load(iconUrl).into(weatherIcon);
                    processWeatherData(lat, lon);
                } catch (Exception e) {
                    Log.e("WeatherCheck", "Error processing the weather data: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("WeatherCheck", "Weather API call failed: " + responseString);
                forecastTextView.setText("Failed to retrieve data");
            }
        });
    }

    private void processWeatherData(double lat, double lon) {
        Log.d("process data", "start: ");
        String Forecast_API_URL = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + API_key;


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Forecast_API_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.d("process data", "onsuccess: ");
                    JSONArray list = response.getJSONArray("list");
                    StringBuilder dailyForecastSummary = new StringBuilder();
                    List<WeatherDataParser.WeatherEntry> entries = WeatherDataParser.parseWeatherData(dailyForecastSummary);
                    ScrollView scrollView = findViewById(R.id.scrollView);
                    weatherContainer = findViewById(R.id.weather_container);
                    LayoutInflater inflater = LayoutInflater.from(weathercheck.this);


                    // Use a TreeMap to automatically sort dates
                    TreeMap<String, DailyForecast> dailyForecastMap = new TreeMap<>();

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);
                        String dateTime = item.getString("dt_txt");
                        String dateOnly = dateTime.split(" ")[0]; // Get the date part only

                        // Extract weather description
                        JSONArray weatherArray = item.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);
                        String weatherDescription = weatherObject.getString("description");

                        // Extract rain data
                        JSONObject rainObject = item.optJSONObject("rain");
                        double rainAmount = rainObject != null ? rainObject.optDouble("3h", 0.0) : 0.0;

                        // Aggregate weather data by date
                        if (!dailyForecastMap.containsKey(dateOnly)) {
                            dailyForecastMap.put(dateOnly, new DailyForecast(weatherDescription, rainAmount));
                        } else {
                            DailyForecast dailyForecast = dailyForecastMap.get(dateOnly);
                            dailyForecast.addDescription(weatherDescription);
                            dailyForecast.addRainAmount(rainAmount);
                        }
                    }

                    // Build daily forecast summary
                    for (Map.Entry<String, DailyForecast> entry : dailyForecastMap.entrySet()) {
                        String date = entry.getKey();
                        DailyForecast dailyForecast = entry.getValue();

                        dailyForecastSummary.append(String.format("Date: %s\nWeather: %s\nRain: %.2f mm\n\n", date, dailyForecast.getWeatherDescription(), dailyForecast.getTotalRainAmount()));
                    }
                    Log.d("ForecastAPIResponse", "data aa gya");
                    Log.d("process data", String.valueOf(dailyForecastSummary));

                    for (WeatherDataParser.WeatherEntry entry : entries) {
                        View weatherView = inflater.inflate(R.layout.weather_entry, weatherContainer, false);

                        TextView dateView = weatherView.findViewById(R.id.weather_date);
                        TextView descriptionView = weatherView.findViewById(R.id.weather_description);
                        TextView rainView = weatherView.findViewById(R.id.rainfall_amount);

                        dateView.setText("Date: " + entry.date);
                        descriptionView.setText("Weather: " + entry.weather);
                        rainView.setText("Rain: " + entry.rain);

                        weatherContainer.addView(weatherView);
                    }



                } catch (Exception e) {
                    Log.e("ForecastAPIResponse", "Error processing the forecast data", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                runOnUiThread(() -> forecastTextView.setText("Failed to retrieve data"));
            }
        });
    }


    private static class DailyForecast {
        private StringBuilder weatherDescriptions = new StringBuilder();
        private double totalRainAmount = 0.0;

        public DailyForecast(String initialDescription, double initialRainAmount) {
            this.weatherDescriptions.append(initialDescription);
            this.totalRainAmount += initialRainAmount;
        }

        public void addDescription(String description) {
            weatherDescriptions.append(", ").append(description);
        }

        public void addRainAmount(double rainAmount) {
            this.totalRainAmount += rainAmount;
        }

        public String getWeatherDescription() {
            return weatherDescriptions.toString();
        }

        public double getTotalRainAmount() {
            return totalRainAmount;
        }
    }

}

