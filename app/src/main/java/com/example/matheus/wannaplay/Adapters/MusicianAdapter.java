package com.example.matheus.wannaplay.Adapters;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matheus.wannaplay.Activities.LoginActivity;
import com.example.matheus.wannaplay.Activities.MainActivity;
import com.example.matheus.wannaplay.Activities.MusicianProfileActivity;
import com.example.matheus.wannaplay.Models.Musician;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import javax.annotation.Nullable;

public class MusicianAdapter extends FirestoreRecyclerAdapter<Musician, MusicianAdapter.MusicianHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Tags t = new Tags();
    private String mCurrentUserId;
    private static double mCurrentUserLatitude;
    private static double mCurrentUserLongitude;

    public MusicianAdapter(@NonNull FirestoreRecyclerOptions<Musician> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final MusicianHolder holder, final int position, @NonNull final Musician model) {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference musiciansRef = firebaseFirestore.collection(t.getKEY_MUSICIANS());
        mCurrentUserId = firebaseAuth.getCurrentUser().getUid();

        //if (getSnapshots().getSnapshot(position).getId().equals(mCurrentUserId)) {
            String mMusicianName = model.getName();
            String mMusicianFirstName = mMusicianName.substring(0, mMusicianName.indexOf(" "));
            holder.mMusicianDescription.setText(mMusicianFirstName + ", " + model.getAge());
            holder.mMusicianDistance.setText(String.valueOf(getCurrentDistance(model.getLatitude(), model.getLongitude())));
            holder.mMusicianPhoto.setScaleType((ImageView.ScaleType.CENTER_CROP));

            try {
                URL photoUrl = new URL(model.getPhotoUrl());
                Glide.with(holder.mMusicianPhoto.getContext()).load(photoUrl.toString()).into(holder.mMusicianPhoto);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        //}
    }

    @NonNull
    @Override
    public MusicianHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item_musician,
                parent, false);
        return new MusicianHolder(v);
    }

    class MusicianHolder extends RecyclerView.ViewHolder {

        TextView mMusicianDescription;
        ImageView mMusicianPhoto;
        TextView mMusicianDistance;

        public MusicianHolder(final View itemView) {
            super(itemView);

            mMusicianDescription = itemView.findViewById(R.id.cardViewDescriptionTxt);
            mMusicianDistance = itemView.findViewById(R.id.cardViewDistanceTxt);
            mMusicianPhoto = itemView.findViewById(R.id.cardViewPhotoImg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemView.getContext().startActivity(new Intent(itemView.getContext(), MusicianProfileActivity.class));

                }
            });

        }
    }

    public double getCurrentUserLatitude() {

        firebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        DocumentReference currentMusicianProfile = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(mCurrentUserId);
        currentMusicianProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mCurrentUserLatitude = document.getDouble(t.getKEY_LATITUDE());
                        Log.d("OK", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ERROR", "No such document");
                    }
                } else {
                    Log.d("ERROR", "Get failed with ", task.getException());
                }
            }
        });
        return mCurrentUserLatitude;
    }

    public double getmCurrentUserLongitude() {

        String mCurrentUserId = firebaseAuth.getCurrentUser().getUid();

        DocumentReference currentMusicianProfile = firebaseFirestore.collection(t.getKEY_MUSICIANS()).document(mCurrentUserId);
        currentMusicianProfile.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        mCurrentUserLongitude = document.getDouble(t.getKEY_LONGITUDE());
                        Log.d("OK", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("ERROR", "No such document");
                    }
                } else {
                    Log.d("ERROR", "Get failed with ", task.getException());
                }
            }
        });
        return mCurrentUserLongitude;
    }

    public float getCurrentDistance(double pMusicianLatitude, double pMusicianLongitude) {

        float distance = 0;

        Location currentUserLocation = new Location("currentUserLocation");
        currentUserLocation.setLatitude(getCurrentUserLatitude());
        currentUserLocation.setLongitude(getmCurrentUserLongitude());

        Location currentMusicianLocation = new Location("currentMusicianLocation");
        currentMusicianLocation.setLatitude(pMusicianLatitude);
        currentMusicianLocation.setLongitude(pMusicianLongitude);

        distance = currentUserLocation.distanceTo(currentMusicianLocation) / 1000;

        DecimalFormat decimalFormat = new DecimalFormat("#");

        return Float.valueOf(decimalFormat.format(distance));
    }


}
