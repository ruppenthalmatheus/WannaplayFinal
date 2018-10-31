package com.example.matheus.wannaplay;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheus.wannaplay.Utilities.Tags;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private String mMusicianPhotoUrl;
    private int mMusicianAge;
    private double mMusicianLatitude, mMusicianLongitude;
    private boolean mMusicianVocal, mMusicianGuitar, mMusicianBass, mMusicianDrums, mMusicianOthers;
    private Calendar calendar;
    private TextView displayDate;
    private String userName, userKey;
    private CheckBox mVocalBtn, mGuitarBtn, mBassBtn, mDrumsBtn, mOthersBtn;
    private DatePickerDialog datePickerDialog;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleApiClient googleApiClient;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

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

        mVocalBtn = findViewById(R.id.registerVocalCheckbox);
        mGuitarBtn = findViewById(R.id.registerGuitarCheckbox);
        mBassBtn = findViewById(R.id.registerBassCheckbox);
        mDrumsBtn = findViewById(R.id.registerDrumCheckbox);
        mOthersBtn = findViewById(R.id.registerOtherCheckbox);

        Button finishButton = findViewById(R.id.registerFinishBtn);
        displayDate = findViewById(R.id.date_select);

        //Changes the Finish Button background if the device language is Portuguese
        if (Locale.getDefault().getDisplayLanguage().equals("português")) {
            finishButton.setBackgroundResource(R.drawable.btn_finish_br);
        }

        userName = firebaseAuth.getCurrentUser().getDisplayName();
        userKey = firebaseAuth.getCurrentUser().getUid();
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
                    saveMusician();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(intent);
                }
            }
        });

    }

    /*private int calculateAge(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Calendar today = Calendar.getInstance();

        int age = today.get((Calendar.YEAR) - c.get(Calendar.YEAR));

        if (today.get(Calendar.DAY_OF_MONTH) < c.get(Calendar.DAY_OF_MONTH)) {
            age--;
        }

        return age;
    }*/

    private void saveMusician() {

        final Tags t = new Tags();

        final boolean isVocalist = mVocalBtn.isChecked();
        final boolean isGuitarist = mGuitarBtn.isChecked();
        final boolean isBassist = mBassBtn.isChecked();
        final boolean isDrummer = mDrumsBtn.isChecked();
        final boolean isOthers = mOthersBtn.isChecked();
        calendar = Calendar.getInstance();
        //int age = calculateAge(calendar.getTimeInMillis());
        String photoUrl = String.valueOf(firebaseAuth.getCurrentUser().getPhotoUrl());

        Map<String, Object> musician = new HashMap<>();
        musician.put(t.getKEY_NAME(), userName);
        musician.put(t.getKEY_ABOUT(), "");
        musician.put(t.getKEY_EMAIL(), firebaseAuth.getCurrentUser().getEmail());
        musician.put(t.getKEY_PHOTO(), photoUrl+"?type=large");
        musician.put(t.getKEY_AGE(), 28);
        musician.put(t.getKEY_LATITUDE(), mMusicianLatitude);
        musician.put(t.getKEY_LONGITUDE(), mMusicianLongitude);

        firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(userKey)
                .set(musician)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("OK", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error writing document", e);
                    }
                });

        Map<String, Object> skills = new HashMap<>();
        skills.put(t.getKEY_VOCAL(), isVocalist);
        skills.put(t.getKEY_GUITAR(), isGuitarist);
        skills.put(t.getKEY_BASS(), isBassist);
        skills.put(t.getKEY_DRUMS(), isDrummer);
        skills.put(t.getKEY_OTHERS(), isOthers);

        DocumentReference musicianReference = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(userKey);
        musicianReference.
                update(t.getKEY_SKILLS(), skills)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("OK", "Skills added succesfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Error adding skills", e);
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
