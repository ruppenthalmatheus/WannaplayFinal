package com.example.matheus.wannaplay.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.matheus.wannaplay.R;

public class MusicianProfileActivity extends AppCompatActivity {

    ImageButton backButton;
    Button messageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        backButton = findViewById(R.id.musicianBackBtn);
        messageButton = findViewById(R.id.musicianMessageBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Message Button Action
            }
        });

    }
}