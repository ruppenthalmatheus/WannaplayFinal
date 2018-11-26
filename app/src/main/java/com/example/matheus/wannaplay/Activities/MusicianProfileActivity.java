package com.example.matheus.wannaplay.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MusicianProfileActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private ImageButton backButton;
    private Button messageButton;
    private CircleImageView mMusicianPhoto;
    private CheckBox mMusicianVocalBtn, mMusicianGuitarBtn, mMusicianBassBtn, mMusicianDrumsBtn, mMusicianOthersBtn;
    private TextView mMusicianName, mMusicianAboutTxt;
    private String musicianId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_profile);

        musicianId = getIntent().getStringExtra("user_id");

        backButton = findViewById(R.id.musicianBackBtn);
        messageButton = findViewById(R.id.musicianMessageBtn);
        mMusicianName = findViewById(R.id.musicianNameTxt);
        mMusicianPhoto = findViewById(R.id.musicianProfileImg);
        mMusicianVocalBtn = findViewById(R.id.musicianVocalCheckbox);
        mMusicianGuitarBtn = findViewById(R.id.musicianGuitarCheckbox);
        mMusicianBassBtn = findViewById(R.id.musicianBassCheckbox);
        mMusicianDrumsBtn = findViewById(R.id.musicianDrumCheckbox);
        mMusicianOthersBtn = findViewById(R.id.musicianOtherCheckbox);
        mMusicianAboutTxt = findViewById(R.id.musicianAboutTxt);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicianProfileActivity.this,ChatActivity.class);
                intent.putExtra(ChatActivity.TO_USER_ID_KEY, musicianId);
                startActivity(intent);
            }
        });

        getMusicianData();

    }

    private void getMusicianData() {
        final Tags t = new Tags();
        DocumentReference currentMusicianProfile = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(musicianId);
        currentMusicianProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        boolean isVocalist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_VOCAL());
                        mMusicianVocalBtn.setChecked(isVocalist);

                        boolean isGuitarist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_GUITAR());
                        mMusicianGuitarBtn.setChecked(isGuitarist);

                        boolean isBasist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_BASS());
                        mMusicianBassBtn.setChecked(isBasist);

                        boolean isDrummer = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_DRUMS());
                        mMusicianDrumsBtn.setChecked(isDrummer);

                        boolean isOthers = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_OTHERS());
                        mMusicianOthersBtn.setChecked(isOthers);

                        String profileName = getUserFirstName(document.getString(t.getKEY_NAME()));
                        mMusicianName.setText(profileName);

                        String profileAbout = document.getString(t.getKEY_ABOUT());
                        mMusicianAboutTxt.setText(profileAbout);

                        String userPhotoUrl = document.getString(t.getKEY_PHOTO());

                        try {
                            URL photoUrl = new URL(userPhotoUrl);
                            Glide.with(getApplicationContext()).load(photoUrl.toString()).into(mMusicianPhoto);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        Log.d("OK", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ERROR", "No such document");
                    }
                } else {
                    Log.d("ERROR", "Get failed with ", task.getException());
                }
            }
        });
    }

    private String getUserFirstName(String pName) {
        String userFirstName = pName.substring(0, pName.indexOf(" "));
        return userFirstName;
    }

}
