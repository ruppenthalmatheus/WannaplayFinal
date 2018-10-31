package com.example.matheus.wannaplay;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.matheus.wannaplay.Utilities.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ConstraintLayout mHeaderConstraint;
    private ImageView mHeaderBackgroundImg;
    private CircleImageView mPhotoImg;
    private CheckBox mProfileVocalBtn, mProfileGuitarBtn, mProfileBassBtn, mProfileDrumsBtn, mProfileOthersBtn;
    private EditText mProfileAbout;
    private TextView mProfileName;
    private Switch mSpotifySwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the profile_preferences layout into a View
        View profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        //Loading components
        mHeaderBackgroundImg = profileView.findViewById(R.id.profileHeaderBackgroundImg);
        mPhotoImg = profileView.findViewById(R.id.profilePhotoImg);
        mProfileName = profileView.findViewById(R.id.profileNameTxt);
        mProfileAbout = profileView.findViewById(R.id.profileAboutTxt);
        mProfileVocalBtn = profileView.findViewById(R.id.profileVocalCheckbox);
        mProfileGuitarBtn = profileView.findViewById(R.id.profileGuitarCheckbox);
        mProfileBassBtn = profileView.findViewById(R.id.profileBassCheckbox);
        mProfileDrumsBtn = profileView.findViewById(R.id.profileDrumCheckbox);
        mProfileOthersBtn = profileView.findViewById(R.id.profileOtherCheckbox);
        mSpotifySwitch = profileView.findViewById(R.id.profileSpotifySwitch);

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
                        mProfileName.setText(getUserFirstName((String) document.get(t.getKEY_NAME())));
                        mProfileAbout.setText(document.get(t.getKEY_ABOUT()).toString());
                        mProfileVocalBtn.setChecked(document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_VOCAL()));
                        mProfileGuitarBtn.setChecked(document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_GUITAR()));
                        mProfileBassBtn.setChecked(document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_BASS()));
                        mProfileDrumsBtn.setChecked(document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_DRUMS()));
                        mProfileOthersBtn.setChecked(document.getBoolean(t.getKEY_SKILLS() + "." + t.getKEY_OTHERS()));
                        Log.d("OK", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ERROR", "No such document");
                    }
                } else {
                    Log.d("ERROR", "get failed with ", task.getException());
                }
            }
        });
    }

    private void updateProfile() {

        final Tags t = new Tags();

        final boolean isVocalist = mProfileVocalBtn.isChecked();
        final boolean isGuitarist = mProfileGuitarBtn.isChecked();
        final boolean isBassist = mProfileBassBtn.isChecked();
        final boolean isDrummer = mProfileDrumsBtn.isChecked();
        final boolean isOthers = mProfileOthersBtn.isChecked();
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
    public void onStop() {
        updateProfile();
        super.onStop();
    }

    private String getUserFirstName(String pName) {
        String userFirstName = pName.substring(0, pName.indexOf(" "));
        return userFirstName;
    }
}
