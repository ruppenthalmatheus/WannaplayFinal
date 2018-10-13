package com.example.matheus.wannaplay;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {

    private TextView welcomeMessage;
    private TextView displayDate;
    private DatePickerDialog datePickerDialog;
    private FirebaseAuth firebaseAuth;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = firebaseAuth.getInstance();

        String userName = firebaseAuth.getCurrentUser().getDisplayName();
        String userFirstName = userName.substring(0, userName.indexOf(" "));

        welcomeMessage = findViewById(R.id.welcome_message);
        welcomeMessage.setText("Olá " + userFirstName + "!");

        finishButton = findViewById(R.id.finish_button);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (getApplicationContext(), MainActivity.class);
                finish();
                startActivity(intent);
            }
        });


        displayDate = findViewById(R.id.date_select);
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
                        },year,month,day);
                datePickerDialog.show();
            }
        });

    }
}
