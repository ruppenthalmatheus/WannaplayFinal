package com.example.matheus.wannaplay.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matheus.wannaplay.Models.Musician;
import com.example.matheus.wannaplay.Adapters.MusicianAdapter;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    Tags t = new Tags();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference musiciansRef = firebaseFirestore.collection(t.getKEY_MUSICIANS());
    TextView homeTitleTxt, homeFilter1, homeFilter2;
    ImageView homeStageImg;
    View homeView;

    private MusicianAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the fragment_preferences layout into a View
        homeView = inflater.inflate(R.layout.fragment_grid_home, container, false);
        homeTitleTxt = homeView.findViewById(R.id.homeGridTxt);
        homeFilter1 = homeView.findViewById(R.id.homeFilterResultTxt);
        homeFilter2 = homeView.findViewById(R.id.homeFilterResultTxt2);
        homeStageImg = homeView.findViewById(R.id.homeStageImg);


        firebaseAuth.getCurrentUser().getUid();

        return homeView;
    }

    private void setMusiciansQuery() {

        int mMinAge = Integer.valueOf(getMinAge());
        int mMaxAge = Integer.valueOf(getMaxAge());
        int mMaxDistance = Integer.valueOf(getMaxDistance());
        boolean mVocalBtnState = getVocalBtnState();
        boolean mGuitarBtnState = getGuitarBtnState();
        boolean mBassBtnState = getBassBtnState();
        boolean mDrumsBtnState = getDrumsBtnState();
        boolean mOthersBtnState = getOthersBtnState();

        if (mVocalBtnState && mBassBtnState && mGuitarBtnState && mDrumsBtnState && mOthersBtnState) {

            Query query = musiciansRef.whereLessThanOrEqualTo(t.getKEY_AGE(), mMaxAge)
                    .whereGreaterThanOrEqualTo(t.getKEY_AGE(), mMinAge)
                    .orderBy(t.getKEY_AGE(), Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Musician> options = new FirestoreRecyclerOptions.Builder<Musician>()
                    .setQuery(query, Musician.class)
                    .build();

            adapter = new MusicianAdapter(options);

            RecyclerView recyclerView = homeView.findViewById(R.id.homeGridRecycleView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
            recyclerView.setAdapter(adapter);

        } else {

            Query query = musiciansRef.whereLessThanOrEqualTo(t.getKEY_AGE(), mMaxAge)
                    .whereGreaterThanOrEqualTo(t.getKEY_AGE(), mMinAge)
                    .whereEqualTo(t.getKEY_SKILLS() + "." + t.getKEY_VOCAL(), mVocalBtnState)
                    .whereEqualTo(t.getKEY_SKILLS() + "." + t.getKEY_GUITAR(), mGuitarBtnState)
                    .whereEqualTo(t.getKEY_SKILLS() + "." + t.getKEY_BASS(), mBassBtnState)
                    .whereEqualTo(t.getKEY_SKILLS() + "." + t.getKEY_DRUMS(), mDrumsBtnState)
                    .whereEqualTo(t.getKEY_SKILLS() + "." + t.getKEY_OTHERS(), mOthersBtnState)
                    .orderBy(t.getKEY_AGE(), Query.Direction.ASCENDING);

            FirestoreRecyclerOptions<Musician> options = new FirestoreRecyclerOptions.Builder<Musician>()
                    .setQuery(query, Musician.class)
                    .build();

            query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots != null && queryDocumentSnapshots.isEmpty()){
                        homeTitleTxt.setVisibility(View.INVISIBLE);
                        homeStageImg.setVisibility(View.VISIBLE);
                        homeFilter1.setVisibility(View.VISIBLE);
                        homeFilter2.setVisibility(View.VISIBLE);
                    } else {
                        homeTitleTxt.setVisibility(View.VISIBLE);
                        homeStageImg.setVisibility(View.INVISIBLE);
                        homeFilter1.setVisibility(View.INVISIBLE);
                        homeFilter2.setVisibility(View.INVISIBLE);
                    }
                }
            });

            adapter = new MusicianAdapter(options);

            RecyclerView recyclerView = homeView.findViewById(R.id.homeGridRecycleView);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
            recyclerView.setAdapter(adapter);
        }

    }

    public String getMinAge() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return String.valueOf(preferences.getInt("minAge", 0));
    }

    public String getMaxAge() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return String.valueOf(preferences.getInt("maxAge", 60));
    }

    public String getMaxDistance() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return String.valueOf(preferences.getInt("distance", 200));
    }

    public Boolean getVocalBtnState() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("vocal", true);
    }

    public Boolean getGuitarBtnState() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("guitar", true);
    }

    public Boolean getBassBtnState() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("bass", true);
    }

    public Boolean getDrumsBtnState() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("drums", true);
    }

    public Boolean getOthersBtnState() {
        SharedPreferences preferences = getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("others", true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setMusiciansQuery();
        adapter.startListening();
    }
}
