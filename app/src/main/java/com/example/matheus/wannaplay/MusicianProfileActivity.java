package com.example.matheus.wannaplay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MusicianProfileActivity extends AppCompatActivity {

    ImageButton backButton;
    Button messageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        backButton = findViewById(R.id.musicianBackBtn);
        messageButton = findViewById(R.id.musicianSendMessageBtn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Back Button Action
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
