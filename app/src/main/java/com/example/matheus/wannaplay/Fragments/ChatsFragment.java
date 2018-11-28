package com.example.matheus.wannaplay.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matheus.wannaplay.Adapters.ChatAdapter;
import com.example.matheus.wannaplay.Models.Chat;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class ChatsFragment extends Fragment {
    RecyclerView recyclerViewMessages;
    TextView messagesNoMessagesTxt;
    ImageView messagesEmptyImg;
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    Tags t = new Tags();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        recyclerViewMessages = view.findViewById(R.id.messagesRecyclerView);
        messagesNoMessagesTxt = view.findViewById(R.id.messagesNoMessagesTxt);
        messagesEmptyImg = view.findViewById(R.id.messagesEmptyImg);
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
        setChatsQuery();

    }

    public void setChatsQuery() {
        Query query = FirebaseFirestore.getInstance().collection(t.getKEY_CHAT())
                .whereArrayContains(t.getKEY_USERS(),userId)
                .orderBy(t.getKEY_LAST_MESSAGE_DATE(), Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setLifecycleOwner(this)
                .setQuery(query, Chat.class)
                .build();

        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null  && queryDocumentSnapshots.isEmpty()){
                    messagesNoMessagesTxt.setVisibility(View.VISIBLE);
                    recyclerViewMessages.setVisibility(View.INVISIBLE);
                    messagesEmptyImg.setVisibility(View.VISIBLE);
                } else {
                    messagesNoMessagesTxt.setVisibility(View.INVISIBLE);
                    recyclerViewMessages.setVisibility(View.VISIBLE);
                    messagesEmptyImg.setVisibility(View.INVISIBLE);
                }
            }
        });

        ChatAdapter chatAdapter = new ChatAdapter(options);
        recyclerViewMessages.setAdapter(chatAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
    }
}
