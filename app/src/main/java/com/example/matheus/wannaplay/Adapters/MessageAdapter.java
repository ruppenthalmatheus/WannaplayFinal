package com.example.matheus.wannaplay.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.matheus.wannaplay.Models.Message;
import com.example.matheus.wannaplay.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message,MessageAdapter.MessageViewHolder> {
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        Context context = holder.itemView.getContext();
        boolean isMyMessage = Objects.equals(model.getWriterUserId(), userId);

        if (isMyMessage) {
            holder.rootMessage.setBackgroundResource(R.drawable.chat2);
            ((FrameLayout.LayoutParams) holder.rootMessage.getLayoutParams()).gravity = Gravity.END;
            holder.tvMessage.setTextColor(Color.WHITE);
        } else {
            holder.rootMessage.setBackgroundResource(R.drawable.chat1);
            ((FrameLayout.LayoutParams) holder.rootMessage.getLayoutParams()).gravity = Gravity.START;
            holder.tvMessage.setTextColor(Color.GRAY);
        }

        holder.rootMessage.setPadding(
                dpToPx(isMyMessage? 8 : 16 ,context),
                dpToPx(8,context),
                dpToPx(isMyMessage? 16 : 8 ,context),
                dpToPx(8,context)
        );

        holder.tvMessage.setText(model.getText());

        CharSequence relativeDateTimeString = DateUtils.getRelativeDateTimeString(
                context,
                model.getDate().getTime(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL);

        holder.tvTime.setText(relativeDateTimeString);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView tvMessage;
        TextView tvTime;
        LinearLayout rootMessage;
        public MessageViewHolder(View itemView) {
            super(itemView);
            rootMessage = itemView.findViewById(R.id.rootMessage);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    private int dpToPx(int dp, Context context){
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
