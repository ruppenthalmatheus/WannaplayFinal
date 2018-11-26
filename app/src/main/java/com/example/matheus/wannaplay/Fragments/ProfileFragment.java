package com.example.matheus.wannaplay.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private CircleImageView mPhotoImg;
    private boolean isVocalist, isGuitarist, isBasist, isDrummer, isOthers;
    private CheckBox mProfileVocalBtn, mProfileGuitarBtn, mProfileBassBtn, mProfileDrumsBtn, mProfileOthersBtn;
    private EditText mProfileAbout;
    private TextView mProfileName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the profile_preferences layout into a View
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Loading components
        mPhotoImg = profileView.findViewById(R.id.profilePhotoImg);
        mProfileName = profileView.findViewById(R.id.profileNameTxt);
        mProfileAbout = profileView.findViewById(R.id.profileAboutTxt);
        mProfileVocalBtn = profileView.findViewById(R.id.profileVocalCheckbox);
        mProfileGuitarBtn = profileView.findViewById(R.id.profileGuitarCheckbox);
        mProfileBassBtn = profileView.findViewById(R.id.profileBassCheckbox);
        mProfileDrumsBtn = profileView.findViewById(R.id.profileDrumCheckbox);
        mProfileOthersBtn = profileView.findViewById(R.id.profileOtherCheckbox);

        getProfileData();

        return profileView;
    }

    private void getProfileData() {
        final Tags t = new Tags();
        String mCurrentUserId = firebaseAuth.getCurrentUser().getUid();
        DocumentReference currentMusicianProfile = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(mCurrentUserId);
        currentMusicianProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        SharedPreferences preferences = getContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();

                        boolean isVocalist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_VOCAL());
                        editor.putBoolean("vocal", isVocalist);
                        mProfileVocalBtn.setChecked(isVocalist);


                        boolean isGuitarist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_GUITAR());
                        editor.putBoolean("guitar", isGuitarist);
                        mProfileGuitarBtn.setChecked(isGuitarist);


                        boolean isBasist = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_BASS());
                        editor.putBoolean("bass", isBasist);
                        mProfileBassBtn.setChecked(isBasist);


                        boolean isDrummer = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_DRUMS());
                        editor.putBoolean("drums", isDrummer);
                        mProfileDrumsBtn.setChecked(isDrummer);


                        boolean isOthers = document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_OTHERS());
                        editor.putBoolean("others", isOthers);
                        mProfileOthersBtn.setChecked(isOthers);

                        String profileName = getUserFirstName(document.getString(t.getKEY_NAME()));
                        editor.putString("name", profileName);
                        mProfileName.setText(profileName);

                        String profileAbout = document.getString(t.getKEY_ABOUT());
                        editor.putString("about", profileAbout);
                        mProfileAbout.setText(profileAbout);

                        String userPhotoUrl = document.getString(t.getKEY_PHOTO());
                        editor.putString("photoURL", "");

                        try {
                            URL photoUrl = new URL(userPhotoUrl);
                            Glide.with(getContext()).load(photoUrl.toString()).into(mPhotoImg);
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

    private void updateProfile() {

        final Tags t = new Tags();

        final String userKey = firebaseAuth.getCurrentUser().getUid();

        Map<String, Object> musician = new HashMap<>();
       musician.put(t.getKEY_ABOUT(), mProfileAbout.getText());
        DocumentReference userProfileRef = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(userKey);
        userProfileRef.update(t.getKEY_ABOUT(), mProfileAbout.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("OK", "Profile description updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("ERROR", "Unable to update!", e);
                    }
                });

        boolean isVocalist = mProfileVocalBtn.isChecked();
        boolean isGuitarist = mProfileGuitarBtn.isChecked();
        boolean isBasist = mProfileBassBtn.isChecked();
        boolean isDrummer = mProfileDrumsBtn.isChecked();
        boolean isOthers = mProfileOthersBtn.isChecked();

        Map<String, Object> skills = new HashMap<>();
        skills.put(t.getKEY_VOCAL(), isVocalist);
        skills.put(t.getKEY_GUITAR(), isGuitarist);
        skills.put(t.getKEY_BASS(), isBasist);
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
    public void onStart() {

        SharedPreferences preferences = getContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);

        mProfileVocalBtn.setChecked(preferences.getBoolean("vocal", isVocalist));
        mProfileGuitarBtn.setChecked(preferences.getBoolean("guitar", isGuitarist));
        mProfileBassBtn.setChecked(preferences.getBoolean("bass", isBasist));
        mProfileDrumsBtn.setChecked(preferences.getBoolean("drums", isDrummer));
        mProfileOthersBtn.setChecked(preferences.getBoolean("others", isOthers));
        mProfileName.setText(String.valueOf(preferences.getString("name", "")));
        mProfileAbout.setText(String.valueOf(preferences.getString("about", "")));

        super.onStart();
    }

    @Override
    public void onStop() {

            updateProfile();

            boolean isVocalist = mProfileVocalBtn.isChecked();
            boolean isGuitarist = mProfileGuitarBtn.isChecked();
            boolean isBasist = mProfileBassBtn.isChecked();
            boolean isDrummer = mProfileDrumsBtn.isChecked();
            boolean isOthers = mProfileOthersBtn.isChecked();

            SharedPreferences preferences = getContext().getSharedPreferences("userProfile", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("vocal", isVocalist);
            editor.putBoolean("guitar", isGuitarist);
            editor.putBoolean("bass", isBasist);
            editor.putBoolean("drums", isDrummer);
            editor.putBoolean("others", isOthers);
            editor.putString("name", mProfileName.getText().toString());
            editor.putString("about", mProfileAbout.getText().toString());
            editor.apply();
            super.onStop();

    }

    private String getUserFirstName(String pName) {
        String userFirstName = pName.substring(0, pName.indexOf(" "));
        return userFirstName;
    }
}
