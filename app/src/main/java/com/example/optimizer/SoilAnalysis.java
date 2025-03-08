package com.example.optimizer;


import  androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;



public class SoilAnalysis extends AppCompatActivity {



    private static final String TAG = "Running";
    private static final String CHANNEL_ID ="My Channel" ;
    private static final int NOTIFICATION_ID=100;
    private Switch pump;
    String selectedItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_analysis);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView tempbox = findViewById(R.id.temperature);
        TextView tempinfo = findViewById(R.id.tempInfo);
        TextView humidbox = findViewById(R.id.humidity);
        TextView humidinfo = findViewById(R.id.humidInfo);
        TextView moistbox = findViewById(R.id.moisture);
        TextView moistinfo = findViewById(R.id.moistInfo);
        TextView Analyzed_info = findViewById(R.id.analyzed_info);
        TextView pump_status = findViewById(R.id.status);
        pump = findViewById(R.id.switch1);



        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.mainlogo, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap imageIcon = bitmapDrawable.getBitmap();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.mainlogo)
                .setContentTitle("Alert Notification")
//                .setContentText(getString(R.string.low_moisture_level))
                .setPriority(NotificationCompat.PRIORITY_LOW);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setLargeIcon(imageIcon)
                    .setSubText("New Messages")
                    .setChannelId(CHANNEL_ID);
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "New Channel", NotificationManager.IMPORTANCE_HIGH));
        } else {
            builder.setLargeIcon(imageIcon)
                    .setSubText("hydrate your crops");
        }



        Spinner mSpinner = findViewById(R.id.spinner_soil);
        List<String> mList = Arrays.asList("Sandy", "Loamy", "Clay");
        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list, mList);
        mArrayAdapter.setDropDownViewResource(R.layout.spinner_list);
        mSpinner.setAdapter(mArrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected item from the Spinner

                selectedItem = mList.get(position);
                // Log or use the selected item as needed
                Log.d(TAG, "Selected item: " + selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle case where nothing is selected
                Log.d(TAG, "Nothing selected");
            }
        });

        pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pump_status.setText("ON");
                    pump_status.setTextColor(getResources().getColor(R.color.on_green));
                } else {
                    pump_status.setText("OFF");
                    pump_status.setTextColor(getResources().getColor(R.color.off_red));
                }
            }
        });




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myDHT = database.getReference("User1").child("sensor");
        System.out.println(myDHT);

        myDHT.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot humidSnapshot = snapshot.child("DHT11").child("humidity");
                Integer humidityValue = humidSnapshot.getValue(Integer.class);
                humidbox.setText(humidityValue + "%");
                if (humidSnapshot.exists()) {
                    Log.w(TAG, "Humidity data found in Firebase");
                } else {
                    Log.w(TAG, "Humidity data not found in Firebase");
                }


                DataSnapshot tempSnapshot = snapshot.child("DHT11").child("temperature_celsius");
                Integer temperatureValue = tempSnapshot.getValue(Integer.class);
                tempbox.setText(temperatureValue + "°C");

                if (tempSnapshot.exists()) {
                    Log.w(TAG, "Temp data found in Firebase");
                } else {
                    Log.w(TAG, "Temp data not found in Firebase");
                }

                DataSnapshot moistSnapshot = snapshot.child("Soil_Moisture").child("moisture");
                Integer MoistureValue = moistSnapshot.getValue(Integer.class);
                moistbox.setText(String.valueOf(MoistureValue));

                if (moistSnapshot.exists()) {
                    Log.w(TAG, "mist data found in Firebase");
                } else {
                    Log.w(TAG, "mist data not found in Firebase");
                }



//                Integer temperatureValue = tempSnapshot.getValue(Integer.class);
//                tempbox.setText(temperatureValue + "°C");
//
//                Integer humidityValue = humidSnapshot.getValue(Integer.class);
//                humidbox.setText(humidityValue + "%");
//
//                Integer MoistureValue = moistSnapshot.getValue(Integer.class);
//                moistbox.setText(String.valueOf(MoistureValue)); // Convert integer to string before setting text



                if (selectedItem.equalsIgnoreCase("sandy")) {
                    //temperature
                    if (temperatureValue <= 20) {
                        tempinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_temperature_level));
                        builder.setContentTitle("Alert Temperature");
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if(temperatureValue >= 40){
                        tempinfo.setText("High");
                        builder.setContentTitle("Alert Temperature");
                        builder.setContentText(getString(R.string.high_temperature_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else{
                        tempinfo.setText("Normal");
                    }

                    //humidity
                    if (humidityValue <= 34) {
                        humidinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_humidity_level));
                        builder.setContentTitle("Alert Humidity");
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if (humidityValue >= 60) {
                        humidinfo.setText("High");
                        builder.setContentTitle("Alert Humidity");
                        builder.setContentText(getString(R.string.high_humidity_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    } else{
                        humidinfo.setText("Normal");
                    }

                    //moisture

                    if (MoistureValue >= 950) {
                        moistinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_moisture_level));
                        builder.setContentTitle("Alert Moisture");
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.low_moisture_level));
                        pump.setChecked(true);
                    }else if (MoistureValue <=  375) {
                        moistinfo.setText("High");
                        builder.setContentTitle("Alert Moisture");
                        builder.setContentText(getString(R.string.high_moisture_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.high_moisture_level));
                        pump.setChecked(false);
                    }else{
                        moistinfo.setText("Normal");
                        Analyzed_info.setText("Normal Conditions");
                        pump.setChecked(false);
                    }
                }




                else if (selectedItem.equalsIgnoreCase("loamy")) {
                    if (temperatureValue <= 20) {
                        tempinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_temperature_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if(temperatureValue >= 40){
                        tempinfo.setText("High");
                        builder.setContentText(getString(R.string.high_temperature_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else{
                        tempinfo.setText("Normal");
                    }

                    //humidity
                    if (humidityValue <= 39) {
                        humidinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_humidity_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if (humidityValue >= 60) {
                        humidinfo.setText("High");
                        builder.setContentText(getString(R.string.high_humidity_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    } else{
                        humidinfo.setText("Normal");
                    }

                    //moisture
                    if (MoistureValue >= 770) {
                        moistinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_moisture_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.low_moisture_level));
                        pump.setChecked(true);
                    }else if (MoistureValue <=  375) {
                        moistinfo.setText("High");
                        builder.setContentText(getString(R.string.high_moisture_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.high_moisture_level));
                        pump.setChecked(false);
                    }else{
                        moistinfo.setText("Normal");
                        Analyzed_info.setText("Normal Conditions");
                        pump.setChecked(false);
                    }

                }


                else if (selectedItem.equalsIgnoreCase("clay")) {
                    if (temperatureValue <= 20) {
                        tempinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_temperature_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if(temperatureValue >= 35){
                        tempinfo.setText("High");
                        builder.setContentText(getString(R.string.high_temperature_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else{
                        tempinfo.setText("Normal");
                    }

                    //humidity
                    if (humidityValue <= 40) {
                        humidinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_humidity_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }else if (humidityValue >= 60) {
                        humidinfo.setText("High");
                        builder.setContentText(getString(R.string.high_humidity_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    } else{
                        humidinfo.setText("Normal");
                    }

                    //moisture
                    if (MoistureValue >= 770) {
                        moistinfo.setText("Low");
                        builder.setContentText(getString(R.string.low_moisture_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.low_moisture_level));
                        pump.setChecked(true);

                    }else if (MoistureValue <=  375) {
                        moistinfo.setText("High");
                        builder.setContentText(getString(R.string.high_moisture_level));
                        Notification notification = builder.build();
                        notificationManager.notify(NOTIFICATION_ID, notification);
                        Analyzed_info.setText(getString(R.string.high_moisture_level));
                        pump.setChecked(false);
                    }else{
                        moistinfo.setText("Normal");
                        Analyzed_info.setText("Normal Conditions");
                        pump.setChecked(false);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }

        });

    }

}