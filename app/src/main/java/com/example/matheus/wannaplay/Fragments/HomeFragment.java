package com.example.matheus.wannaplay.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.matheus.wannaplay.Models.Musician;
import com.example.matheus.wannaplay.Adapters.MusicianAdapter;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    Tags t = new Tags();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference musiciansRef = firebaseFirestore.collection(t.getKEY_MUSICIANS());
    private View homeView;
    private String mCurrentUserId;
    Query lessCurrentUser;

    private MusicianAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Turns the fragment_preferences layout into a View
        homeView = inflater.inflate(R.layout.fragment_grid_home, container, false);

        mCurrentUserId = firebaseAuth.getCurrentUser().getUid();

        setUpRecyclerView();
        adapter.startListening();


        return homeView;
    }

    private void setUpRecyclerView() {

        Query query = musiciansRef.orderBy(t.getKEY_DATE(), Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Musician> options = new FirestoreRecyclerOptions.Builder<Musician>()
                .setQuery(query, Musician.class)
                .build();

        adapter = new MusicianAdapter(options);

        RecyclerView recyclerView = homeView.findViewById(R.id.homeGridRecycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 2));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
