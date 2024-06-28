package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.*;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView cityName;
    Button search;
    TextView show;
    String url;

    class getWeather extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls){
            StringBuilder result = new StringBuilder();
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line = "";
                while((line = reader.readLine()) !=null){
                    result.append(line).append("\n");
                }
                return result.toString();
            }catch (Exception exception){
                exception.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject mainObject = jsonObject.getJSONObject("main");

                // Obtendo os valores individuais
                double tempCelsius = mainObject.getDouble("temp");
                double feelsLikeCelsius = mainObject.getDouble("feels_like");
                double tempMinCelsius = mainObject.getDouble("temp_min");
                double tempMaxCelsius = mainObject.getDouble("temp_max");
                int humidity = mainObject.getInt("humidity");
                int pressure = mainObject.getInt("pressure");
                int seaLevel = mainObject.has("sea_level") ? mainObject.getInt("sea_level") : -1; // Verifica se existe
                int grndLevel = mainObject.has("grnd_level") ? mainObject.getInt("grnd_level") : -1; // Verifica se existe

                // Formatando as strings com as unidades apropriadas
                String weatherInfo = "Temperatura: " + tempCelsius + "°C\n" +
                        "Sensação térmica: " + feelsLikeCelsius + "°C\n" +
                        "Temperatura Min: " + tempMinCelsius + "°C\n" +
                        "Temperatura Max: " + tempMaxCelsius + "°C\n" +
                        "Umidade: " + humidity + "%\n" +
                        "Pressão: " + pressure + " hPa\n";

                if (seaLevel != -1) {
                    weatherInfo += "Pressão ao mar: " + seaLevel + " hPa\n";
                }
                if (grndLevel != -1) {
                    weatherInfo += "Pressão ao Solo: " + grndLevel + " hPa\n";
                }

                show.setText(weatherInfo);
                show.setVisibility(View.VISIBLE);
            }catch (Exception exception){
                exception.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = findViewById(R.id.cityName);
        search = findViewById(R.id.search);
        show = findViewById(R.id.weather);

        final String[] temp={""};

        if (show.getText().toString().isEmpty()){
            show.setVisibility(View.INVISIBLE);
        }


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Button CLicked!", Toast.LENGTH_SHORT).show();
                String city = cityName.getText().toString();
                try {
                    if(city != null){
                        url = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&lang=pt_br&appid=71aa47c4f54efabc8962837fbc35f131&units=metric";
                    }else{
                        Toast.makeText(MainActivity.this, "Digite a cidade aqui!", Toast.LENGTH_SHORT).show();
                    }
                    getWeather task = new getWeather();
                    temp[0] = task.execute(url).get();
                }catch(ExecutionException exception){
                    exception.printStackTrace();
                }catch(InterruptedException exception){
                    exception.printStackTrace();
                }
                if(temp[0] == null){
                    show.setText("Sem informações!");
                }
            }
        });
    }
}