package com.example.studyhotspot.Boundary;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.studyhotspot.R;
import com.example.studyhotspot.Control.CreateSession;
import com.example.studyhotspot.Control.ActivityPageMain;
import com.example.studyhotspot.Control.URLReader;
import com.example.studyhotspot.Control.FindFriend;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * LocationInformationActivity gets the information about the location selected on the map using
 * the google api with the weather of the location using weather global api.Information such as
 * address, phoneNUmber, openStatus, hours, price and weather has been displayed
 */

public class LocationInformationActivity extends AppCompatActivity {


    private TextView locationName;
    private FloatingActionButton createSession;
    private FloatingActionButton directions;
    private ImageView back;
    private TextView address;
    private TextView phoneNumber;
    private TextView openStatus;
    private TextView hours;
    private TextView price;
    private RatingBar ratingBar;
    private TextView ratingNum;
    private TextView tempView;
    private TextView condDesc;
    private ImageView condIcon;
    private TextView time;
    private SeekBar timeBar;
    private BottomAppBar bottomAppBar;


    String name = null;
    String coord = null;
    String rawPlaceID = null;

    String userID;
    String currentUser;
    String userEmail;

    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            name = extras.getString("Name");
            coord = extras.getString("Coord");
            rawPlaceID = extras.getString("PlaceID");

            currentUser = extras.getString("currentUser");
            userID = extras.getString("currentUID");
            userEmail = extras.getString("userEmail");
        }

        setUpTopBar();
        setUpBottomAppBar();
        setUpInformationPage();

    }

    private void setUpTopBar(){
        directions = findViewById(R.id.directions);
        back = findViewById(R.id.back_button);
        locationName = findViewById(R.id.location_name);

        locationName.setText(name);

        StringBuilder sb = new StringBuilder();
        sb.append("http://maps.google.com/maps?daddr=");
        sb.append(name);
        String url = sb.toString();


        directions.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    private void setUpInformationPage(){
        //ids
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone);
        openStatus = findViewById(R.id.openStatus);
        hours = findViewById(R.id.hours);
        price = findViewById(R.id.price);
        ratingBar = findViewById(R.id.ratingBar);
        ratingNum = findViewById(R.id.ratingNum);
        condDesc = findViewById(R.id.condDescr);
        tempView = findViewById(R.id.temp);
        condIcon = findViewById(R.id.condIcon);
        time = findViewById(R.id.time);
        timeBar = findViewById(R.id.timeBar);

        //obtain + display details

        String placeID = null;
        int begin = rawPlaceID.indexOf(":");
        int end = rawPlaceID.indexOf("}",begin);
        placeID = rawPlaceID.substring(begin+2,end-1);

        StringBuilder sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/place/details/json?place_id=");
        sb.append(placeID);
        sb.append("&fields=formatted_address,rating,formatted_phone_number,opening_hours," +
                "website,price_level&key=AIzaSyCo7BtlsuOVcER0l-THnPurg5v1RjBXXtU");

        System.out.println(sb.toString());

        //ADDRESS

        JSONObject result = null;
        try {
            jsonObject = URLReader.URL2JSON(sb.toString());
            result = jsonObject.getJSONObject("result");
            address.setText(result.getString("formatted_address"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //PHONE

        try {
            phoneNumber.setText(result.getString("formatted_phone_number"));
        } catch (JSONException e) {
            phoneNumber.setText("Unavailable");
        }

        //PRICE

        try {
            String pLevel = "";
            for (int i = 0; i < result.getInt("price_level"); i++) {
                pLevel = pLevel + "$";
            }
            price.setText(pLevel);
        } catch (JSONException e){
            price.setText("Unavailable");
        }

        //RATING
        try {
            ratingBar.setRating((float) result.getDouble("rating"));
            ratingNum.setText("("+result.get("rating")+")");
        } catch (JSONException e){
            ratingBar.setRating(0);
            ratingNum.setText("(Unavailable)");
        }

        //OPENING HOURS

        try {
            JSONObject open = result.getJSONObject("opening_hours");
            System.out.println(open.get("open_now"));
            if ((boolean)open.get("open_now")){
                openStatus.setText("OPEN");
                openStatus.setTextColor(Color.parseColor("#006400"));
            }
            else{
                openStatus.setText("CLOSED");
                openStatus.setTextColor(Color.parseColor("#800000"));
            }

            JSONArray weekdays = (JSONArray) open.get("weekday_text");
            sb = new StringBuilder();

            for (int i = 0; i < weekdays.length(); i++){
                sb.append("- ");
                sb.append(weekdays.get(i));
                sb.append("\n");
            }

            hours.setText(sb.toString());

        } catch (JSONException e) {
            openStatus.setText("Opening Hours Unavailable");
            e.printStackTrace();
        }

        //WEATHER STARTS HERE

        JSONObject jsonObject = null;
        JSONArray weatherJSON = null;

        try {
            jsonObject = URLReader.URL2JSON("https://api.openweathermap.org/data/2.5/forecast?id=1880251&APPID=8b209724831a07af211a052c5e87e404");
            weatherJSON = jsonObject.getJSONArray("list");
            System.out.println(weatherJSON);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray finalWeatherJSON = weatherJSON;

        //Initialize timeBar
        try {
            JSONObject cur = finalWeatherJSON.getJSONObject(0);

            //Find and Set Temperature
            int temperature = (cur.getJSONObject("main").getInt("temp")-273);
            tempView.setText(""+temperature+" °C");

            //Find and Set Condition
            String cond = cur.getJSONArray("weather").getJSONObject(0).getString("description");
            condDesc.setText(cond);

            //Set Icon based on Condition
            if (cond.contains("thunder")){
                condIcon.setImageResource(R.mipmap.thunderstorm);
            }
            else if (cond.contains("cloud")){
                condIcon.setImageResource(R.mipmap.cloud);
            }
            else if (cond.contains("rain")){
                condIcon.setImageResource(R.mipmap.rain);
            }
            else if (cond.contains("clear")){
                condIcon.setImageResource(R.mipmap.clear);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Set timeBar listener
        timeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                //set Timing
                if (progress == 0){
                    time.setText("Now:");
                }
                else if (progress == 1){
                    time.setText("In 1 hour:");
                }
                else{
                    time.setText("In "+progress+" hours:");
                }

                try {
                    JSONObject cur = finalWeatherJSON.getJSONObject(progress);

                    //Find and Set Temperature
                    int temperature = (cur.getJSONObject("main").getInt("temp")-273);
                    tempView.setText(""+temperature+" °C");

                    //Find and Set Condition
                    String cond = cur.getJSONArray("weather").getJSONObject(0).getString("description");
                    condDesc.setText(cond);

                    //Set Icon based on Condition
                    if (cond.contains("thunder")){
                        condIcon.setImageResource(R.mipmap.thunderstorm);
                    }
                    else if (cond.contains("cloud")){
                        condIcon.setImageResource(R.mipmap.cloud);
                    }
                    else if (cond.contains("rain")){
                        condIcon.setImageResource(R.mipmap.rain);
                    }
                    else if (cond.contains("clear")){
                        condIcon.setImageResource(R.mipmap.clear);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bottomAppBar);
        createSession = findViewById(R.id.createSession);

        //click event over Bottom bar menu item
        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String title = item.getTitle().toString();
                if (title.contentEquals("Friends")) {
                    Intent intent = new Intent(LocationInformationActivity.this, FindFriend.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Activities")) {
                    Intent intent = new Intent(LocationInformationActivity.this, ActivityPageMain.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else if (title.contentEquals("Settings")) {
                    Intent intent = new Intent(LocationInformationActivity.this, Logout.class);
                    intent.putExtra("currentUser", currentUser);
                    intent.putExtra("currentUID", userID);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                }

                return false;
            }
        });

        createSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocationInformationActivity.this, CreateSession.class);
                intent.putExtra("Name", name);
                intent.putExtra("currentUser", currentUser);
                intent.putExtra("currentUID", userID);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });
    }
}
