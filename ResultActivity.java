package com.example.snehalsurendradesai.weatherforecast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;
import android.net.Uri;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private String result, city, state, degree, windUnit, degUnit, visibUnit, fbicon, fbsummary, fbcurtemp;
    private JSONObject jObj;

    public int returnImg(String icon)
    {
        if(icon.matches("clear-day")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/clear.png";
            return (R.drawable.clear);
        }
        else if(icon.matches("clear-night")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/clear_night.png";
            return (R.drawable.clear_night);
        }
        else if(icon.matches("rain")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/rain.png";
            return (R.drawable.rain);
        }
        else if(icon.matches("snow")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/snow.png";
            return (R.drawable.snow);
        }
        else if(icon.matches("sleet")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/sleet.png";
            return (R.drawable.sleet);
        }
        else if(icon.matches("wind")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/wind.png";
            return (R.drawable.wind);
        }
        else if(icon.matches("fog")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/fog.png";
            return (R.drawable.fog);
        }
        else if(icon.matches("cloudy")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/cloudy.png";
            return (R.drawable.cloudy);
        }
        else if(icon.matches("partly-cloudy-day")) {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_day.png";
            return (R.drawable.cloud_day);
        }
        else {
            fbicon = "http://cs-server.usc.edu:45678/hw/hw8/images/cloud_night.png";
            return (R.drawable.cloud_night);
        }
    }

    public void Map(View view)
    {
        String lat = "", lng = "";
        try {
            lat = jObj.getString("latitude");
            lng = jObj.getString("longitude");
        }
        catch(JSONException e)
        {

        }
        Intent mapIntent = new Intent(ResultActivity.this, MapActivity.class);
        Bundle extras = new Bundle();
        extras.putString("lat", lat);
        extras.putString("lng", lng);
        mapIntent.putExtras(extras);
        startActivity(mapIntent);
    }

    public void FB(View view)
    {
        callbackManager = CallbackManager.Factory.create();
        System.out.println(callbackManager);
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Toast.makeText(ResultActivity.this, "Facebook Post Successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(ResultActivity.this, "Post Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(ResultActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                exception.printStackTrace();
            }
        });



        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Current Weather in " + city + ", " + state)
                    .setContentDescription(fbsummary + ", " + fbcurtemp + degUnit)
                    .setContentUrl(Uri.parse("http://forecast.io"))
                    .setImageUrl(Uri.parse(fbicon))
                    .build();

            shareDialog.show(linkContent);

        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.content_result);
        getSupportActionBar().setTitle("Result Activity");
        Bundle extras = getIntent().getExtras();
        result = extras.getString("result");
        city = extras.getString("city");
        state = extras.getString("state");
        degree = extras.getString("degree");
        System.out.println(result);
        System.out.println(degree);

        try {
            jObj = new JSONObject(result);
            String curr = jObj.getString("currently");
            JSONObject currObj = new JSONObject(curr);

            fbicon = currObj.getString("icon");
            fbsummary = currObj.getString("summary");
            fbcurtemp = currObj.getString("temperature");

            String daily = jObj.getString("daily");
            JSONObject dailyObj = new JSONObject(daily);

            if(degree.matches("Fahrenheit")) {
                windUnit = "mph";
                degUnit = "°F";
                visibUnit = "mi";
            }
            else {
                windUnit = "m/s";
                degUnit = "°C";
                visibUnit = "km";
            }

            String icon = currObj.getString("icon");
            ImageView summaryIV = (ImageView) findViewById(R.id.summaryImg);
            summaryIV.setImageResource(returnImg(icon));

            TextView summaryTV = (TextView) findViewById(R.id.summaryText);
            summaryTV.setText(currObj.getString("summary") + " in " + city + ", " + state);

            TextView degreeTV = (TextView) findViewById(R.id.degreeText);
            degreeTV.setText(degUnit);

            int index = currObj.getString("temperature").indexOf(".");
            TextView tempTV = (TextView) findViewById(R.id.temp);
            tempTV.setText(currObj.getString("temperature").substring(0, index));

            JSONArray dailyData = new JSONArray(dailyObj.getString("data"));
            JSONObject dayObj = dailyData.getJSONObject(0);

            TextView lowHigh = (TextView) findViewById(R.id.highLowText);
            lowHigh.setText("L:" + Math.round(Float.parseFloat(dayObj.getString("temperatureMin"))) + "° | H:" + Math.round(Float.parseFloat(dayObj.getString("temperatureMax"))) + "°");

            float precipIntensity = Float.parseFloat(currObj.getString("precipIntensity"));
            TextView precipitation = (TextView) findViewById(R.id.precipitation);
            if(precipIntensity >= 0.4)
                precipitation.setText("Heavy");
            else if(precipIntensity >= 0.1)
                precipitation.setText("Moderate");
            else if(precipIntensity >= 0.017)
                precipitation.setText("Light");
            else if(precipIntensity >= 0.002)
                precipitation.setText("Very Light");
            else
                precipitation.setText("None");

            TextView rainTV = (TextView) findViewById(R.id.chofrain);
            rainTV.setText(Math.round(precipIntensity * 100) + "%");

            TextView windTV = (TextView) findViewById(R.id.windspd);
            windTV.setText(currObj.getString("windSpeed") + " " + windUnit);

            TextView dewTV = (TextView) findViewById(R.id.dewpt);
            dewTV.setText(Math.round(Float.parseFloat(currObj.getString("dewPoint"))) + degUnit);

            TextView humidTV = (TextView) findViewById(R.id.humidity);
            humidTV.setText(Math.round(Float.parseFloat(currObj.getString("humidity")) * 100) + "%");

            TextView visibTV = (TextView) findViewById(R.id.visibility);
            visibTV.setText(currObj.getString("visibility") + " " + visibUnit);

            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            Date sunrise = new Date(Long.parseLong(dayObj.getString("sunriseTime")) * 1000);
            System.out.println(dateFormat.format(sunrise));
            Date sunset = new Date(Long.parseLong(dayObj.getString("sunsetTime")) * 1000);
            System.out.println(dateFormat.format(sunset));

            TextView sunriseTV = (TextView) findViewById(R.id.sunrise);
            dateFormat.setTimeZone(TimeZone.getTimeZone(jObj.getString("timezone")));
            sunriseTV.setText(dateFormat.format(sunrise).toString());

            TextView sunsetTV = (TextView) findViewById(R.id.sunset);
            sunsetTV.setText(dateFormat.format(sunset).toString());
        }
        catch(JSONException e)
        {

        }
    }

    public void More(View view)
    {
        Intent moreIntent = new Intent(ResultActivity.this, DetailsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("result", result);
        extras.putString("city", city);
        extras.putString("state", state);
        extras.putString("degree", degUnit);
        moreIntent.putExtras(extras);
        startActivity(moreIntent);
    }
}
