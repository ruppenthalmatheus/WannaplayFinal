package com.example.matheus.wannaplay;


import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String mMusicianName;
    private String mMusicianPhotoUrl;
    private int mMusicianAge;
    private double mMusicianLatitude;
    private double mMusicianLongitude;
    private boolean mMusicianVocal;
    private boolean mMusicianGuitar;
    private boolean mMusicianBass;
    private boolean mMusicianDrums;
    private boolean mMusicianOthers;
    private TextView displayDate;
    private DatePickerDialog datePickerDialog;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Button finishButton = findViewById(R.id.registerFinishBtn);
        displayDate = findViewById(R.id.date_select);

        //Changes the Finish Button background if the device language is Portuguese
        if (Locale.getDefault().getDisplayLanguage().equals("português")) {
            finishButton.setBackgroundResource(R.drawable.btn_finish_br);
        }

        String userName = firebaseAuth.getCurrentUser().getDisplayName();
        String userFirstName = userName.substring(0, userName.indexOf(" "));

        TextView welcomeMessage = findViewById(R.id.welcome_message);
        welcomeMessage.setText(getResources().getString(R.string.registerHello) + " " + userFirstName + "!");

        //DatePickerDialog Config
        displayDate.setOnClickListener(new View.OnClickListener() {

            int day;
            int month;
            int year;
            Calendar calendar;

            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                displayDate.setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        //Finish Button Config
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayDate.getText().toString().equals("")) {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registerRequiredBirthMessage), Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.dialogMessage), Toast.LENGTH_LONG).show();
        } else {
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMusicianLatitude = location.getLatitude();
                                mMusicianLongitude = location.getLongitude();
                            }
                        }
                    });
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("RegisterActivity", "Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("RegisterActivity", "Connection Failed: " + connectionResult.getErrorCode());
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }
}
