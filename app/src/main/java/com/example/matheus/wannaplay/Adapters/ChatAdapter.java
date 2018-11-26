package com.example.matheus.wannaplay.Adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.matheus.wannaplay.Activities.ChatActivity;
import com.example.matheus.wannaplay.Models.Chat;
import com.example.matheus.wannaplay.Models.Musician;
import com.example.matheus.wannaplay.R;
import com.example.matheus.wannaplay.Utilities.Tags;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat,ChatAdapter.ChatViewHolder> {
    private Tags tags = new Tags();
    private CollectionReference usersReference = FirebaseFirestore.getInstance().collection(tags.getKEY_MUSICIANS());
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Chat model) {
        List<String> users = model.getUsers();
        final String otherUserId = Objects.equals(users.get(0), userId) ? users.get(1) : users.get(0);
        usersReference.document(otherUserId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Musician musician = documentSnapshot.toObject(Musician.class);
                holder.tvUsername.setText(musician.getName());
                Glide.with(holder.itemView).load(musician.getPhotoUrl()).into(holder.ivProfileImg);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),ChatActivity.class);
                        intent.putExtra(ChatActivity.TO_USER_ID_KEY, otherUserId);
                        v.getContext().startActivity(intent);
                    }
                });
            }
        });
        CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(holder.itemView.getContext(), model.getLastMessageDate().getTime(), DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS,DateUtils.FORMAT_ABBREV_ALL);
        holder.tvTime.setText(relativeDateTimeString);
        holder.tvLastMessage.setText(model.getLastMessage());

    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent,false);
        return new ChatViewHolder(view);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView tvUsername;
        TextView tvLastMessage;
        TextView tvTime;
        ImageView ivProfileImg;

        public ChatViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfileImg = itemView.findViewById(R.id.chatProfileImg);
        }
    }
}
