package com.example.matheus.wannaplay.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matheus.wannaplay.Adapters.MessageAdapter;
import com.example.matheus.wannaplay.Models.Chat;
import com.example.matheus.wannaplay.Models.Message;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.util.Consumer;

import java.util.Date;

public class ChatActivity extends AppCompatActivity {
    public static final String CHAT_ID_KEY = "CHAT_ID";
    public static final String TO_USER_ID_KEY = "TO_USER_ID";
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String chatId;
    String toUserId;
    RecyclerView recyclerView;
    Button btnNewMessage;
    EditText etMessage;
    ImageView profileImage;
    TextView profileName;
    ImageButton btnBack;
    DocumentReference userReference;
    DocumentReference chatReference;
    CollectionReference messagesCollection;
    private Tags tags = new Tags();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chatRecyclerView);
        btnNewMessage = findViewById(R.id.chatSendBtn);
        etMessage = findViewById(R.id.chatMessageTxt);
        profileImage = findViewById(R.id.chatProfileImg);
        profileName = findViewById(R.id.chatProfileNameTxt);
        btnBack = findViewById(R.id.chatBackBtn);
        userReference = FirebaseFirestore.getInstance().collection(tags.getKEY_MUSICIANS()).document(userId);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true));



        toUserId = getIntent().getStringExtra(TO_USER_ID_KEY);

        FirebaseFirestore
                .getInstance()
                .collection(tags.getKEY_MUSICIANS())
                .document(toUserId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Glide.with(ChatActivity.this).load(documentSnapshot.getString(tags.getKEY_PHOTO())).into(profileImage);
                        profileName.setText(documentSnapshot.getString(tags.getKEY_NAME()));
                    }
                });

        if (getIntent().hasExtra(CHAT_ID_KEY)){
            chatId = getIntent().getStringExtra(CHAT_ID_KEY);
            chatReference = FirebaseFirestore.getInstance().collection("chats").document(chatId);
            messagesCollection = chatReference.collection("messages");
            setupRecyclerView();
        } else{
            findChatIdForUser(new Consumer<DocumentReference>() {
                @Override
                public void accept(DocumentReference documentReference) {
                    if (documentReference != null) {
                        chatReference = documentReference;
                        messagesCollection = chatReference.collection("messages");
                        setupRecyclerView();
                    }
                }
            });
        }


        btnNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendNewMessage();
            }
        });
    }

    private void setupRecyclerView(){
        FirestoreRecyclerOptions<Message> recyclerOptions =
                new FirestoreRecyclerOptions.Builder<Message>()
                        .setLifecycleOwner(this)
                        .setQuery(getMessagesQuery(),Message.class)
                        .build();

        recyclerView.setAdapter(new MessageAdapter(recyclerOptions));
    }


    private void findChatIdForUser(final Consumer<DocumentReference> documentReferenceConsumer){
        FirebaseFirestore.getInstance()
                .collection("chats")
                .whereEqualTo("user1Id",userId)
                .whereEqualTo("user2Id",toUserId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty())
                            documentReferenceConsumer.accept(queryDocumentSnapshots.getDocuments().get(0).getReference());
                        else {
                            FirebaseFirestore.getInstance()
                                    .collection("chats")
                                    .whereEqualTo("user2Id",userId)
                                    .whereEqualTo("user1Id",toUserId).get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if(!queryDocumentSnapshots.isEmpty())
                                                documentReferenceConsumer.accept(queryDocumentSnapshots.getDocuments().get(0).getReference());
                                            else {
                                                documentReferenceConsumer.accept(null);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void sendNewMessage() {
        if (TextUtils.isEmpty(etMessage.getText()))
            return;

        Message message = new Message(
                etMessage.getText().toString(),
                userId,
                new Date(System.currentTimeMillis())
        );

        if (chatReference == null) {
            if (chatId == null) {
                chatReference = FirebaseFirestore.getInstance().collection("chats").document();
                chatReference.set(new Chat(
                        userId,
                        toUserId,
                        message.getDate(),
                        message.getText())
                );
                chatId = chatReference.getId();
            } else {
                chatReference = FirebaseFirestore.getInstance().collection("chats").document(chatId);
            }
            messagesCollection = chatReference.collection("messages");
            setupRecyclerView();
        }
        chatReference.update("lastMessageDate", message.getDate());
        chatReference.update("lastMessage",message.getText());
        messagesCollection.add(message);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(0);
            }
        },500);
        etMessage.setText("");
    }


    public Query getMessagesQuery() {
        return messagesCollection.orderBy("date", Query.Direction.DESCENDING);
    }
}
