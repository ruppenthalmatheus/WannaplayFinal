package com.example.matheus.wannaplay.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.matheus.wannaplay.Adapters.ChatAdapter;
import com.example.matheus.wannaplay.Models.Chat;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ChatsFragment extends Fragment {
    RecyclerView rvChats;
    TextView tvNoMessages;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Query chatsQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        rvChats = view.findViewById(R.id.messagesRecyclerView);
        tvNoMessages = view.findViewById(R.id.messagesNoMessagesTxt);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        setChatsQuery();

    }

    public void setChatsQuery() {
        Query query = FirebaseFirestore.getInstance().collection("chats")
                .whereArrayContains("users",userId)
                .orderBy("lastMessageDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setLifecycleOwner(this)
                .setQuery(query, Chat.class)
                .build();

        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null  && queryDocumentSnapshots.isEmpty()){
                    tvNoMessages.setVisibility(View.VISIBLE);
                    rvChats.setVisibility(View.INVISIBLE);
                } else {
                    tvNoMessages.setVisibility(View.INVISIBLE);
                    rvChats.setVisibility(View.VISIBLE);
                }
            }
        });

        ChatAdapter chatAdapter = new ChatAdapter(options);
        rvChats.setAdapter(chatAdapter);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
}
