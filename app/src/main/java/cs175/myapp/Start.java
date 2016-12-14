package cs175.myapp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class Start extends AppCompatActivity {

    LocationManager locationManager;
    Location location;
    String locationProvider;
    String city = "";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10;
    private static final String APP_ID = "159fdf1e3e166e74bb30bc53cd3db2f2";
    private boolean initial = true;
    private TextView best;
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        //get gps location on first time at screen
        if(initial) {
            checkLocation();
            String cityNoSpaces = city.replace(" ", "");
            String url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityNoSpaces + "&appid=" + APP_ID;
            new GetWeatherTask().execute(url);
        }

        best = (TextView)findViewById(R.id.best);
        SpannableString content = new SpannableString(getText(R.string.your_best));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        best.setText(content);
    }

    /*
   User has presssed start. Check orientation and launch version
    */
    public void startGame(){
        //get orientation value
        int orient = this.getResources().getConfiguration().orientation;

        //check which game activity to open
        if(orient == ORIENTATION_PORTRAIT){
            startActivity(new Intent(getApplicationContext(), gamePortrait.class));
        }
        else {
            startActivity(new Intent(getApplicationContext(), gameLandscape.class));
        }
    }

    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        private TextView textView;

        public GetWeatherTask() {
        }

        @Override
        protected String doInBackground(String... strings) {
            String weather = "UNDEFINED";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }

                JSONObject topLevel = new JSONObject(builder.toString());
                JSONArray main = topLevel.getJSONArray("weather");
                JSONObject mJsonObjectProperty = main.getJSONObject(0);
                weather = mJsonObjectProperty.getString("main");

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(String weather) {
            ///textView.setText("Current Weather: " + temp);
            if(initial) {
                Toast.makeText(getApplicationContext(), "Current forecast for " + city + " is " + weather,
                        Toast.LENGTH_LONG).show();
                backgroundWeather(weather);
                initial = false;
            }
        }
    }


        //pull location of user, city for weather forecast
        private void checkLocation(){
        //use location gps
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        locationProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        location = locationManager.getLastKnownLocation(locationProvider);
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        city = addresses.get(0).getLocality();
    }

    /*
    Set background image depending on current weather in user's city
     */
    public void backgroundWeather(String weather){
        //get current layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_start);

        //set background accoring to weather
        if(weather.contains("rain")){
            layout.setBackgroundResource(R.drawable.rain);
        }
        else if(weather.contains("snow")){
            layout.setBackgroundResource(R.drawable.snow);
        }
        else if(weather.contains("cloud")){
            layout.setBackgroundResource(R.drawable.cloud);
        }
        else{
            //clear backgrounf default
            layout.setBackgroundResource(R.drawable.clear);
        }
    }

    //disable back button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(event.getKeyCode()){
                case KeyEvent.KEYCODE_BACK:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}


