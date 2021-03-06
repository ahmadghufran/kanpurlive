package com.kanpurlive.ghufya.kanpur_live;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.Query;

/**
 * Created by Ghufya on 28/03/2018.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/*
 * Created by Mahmoud on 3/13/2017.
 */

class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 0;
    private static final int VIEW_TYPE_SENT_WITH_DATE = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_RECEIVED_WITH_DATE = 3;
    private static final int VIEW_TYPE_SENT_WITH_IMAGE = 4;
    private static final int VIEW_TYPE_RECIEVED_WITH_IMAGE = 5;

    private final String ownerUid;
    private final Context context;
    private ArrayList<Integer> selectedPositions;

    MessagesAdapter(Context context, String ownerUid, FirestoreRecyclerOptions<Message> ref) {
        super(ref);
        this.context = context;
        this.ownerUid = ownerUid;
        selectedPositions = new ArrayList<>();
    }
    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.setMessage(model);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = getItem(position);
        if (message.getFile() != null){
            if (message.getFile().getType().equals("img") && message.getFrom().equals(ownerUid)){
                return VIEW_TYPE_SENT_WITH_IMAGE;
            }else {
                return VIEW_TYPE_RECIEVED_WITH_IMAGE;
            }
        } else if (message.getFrom().equals(ownerUid)) {
            if (position == getItemCount() - 1 || selectedPositions.contains(position) ||
                    getItem(position + 1).getDayTimestamp() != message.getDayTimestamp()) {
                return VIEW_TYPE_SENT_WITH_DATE;
            } else {
                return VIEW_TYPE_SENT;
            }
        } else {
            if (position == getItemCount() - 1 || selectedPositions.contains(position) ||
                    getItem(position + 1).getDayTimestamp() != message.getDayTimestamp()) {
                return VIEW_TYPE_RECEIVED_WITH_DATE;
            } else {
                return VIEW_TYPE_RECEIVED;
            }
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_SENT:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                break;
            case VIEW_TYPE_SENT_WITH_DATE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                break;
            case VIEW_TYPE_RECEIVED:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                break;
            case VIEW_TYPE_RECEIVED_WITH_DATE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                break;
            case VIEW_TYPE_SENT_WITH_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_left_img, parent, false);
                break;
            case VIEW_TYPE_RECIEVED_WITH_IMAGE:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_left_img, parent, false);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
        }
        return new MessageViewHolder(itemView);
    }



    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Nullable
        @BindView(R.id.item_message_date_text_view)
        TextView itemMessageDateTextView;
        @Nullable
        @BindView(R.id.item_message_body_text_view)
        TextView itemMessageBodyTextView;
        @Nullable
        @BindView(R.id.item_message_parent)
        LinearLayout itemMessageParent;
        @Nullable
        @BindView(R.id.img_chat)
        ImageView ivChatPhoto;

        MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //itemMessageBodyTextView.setOnClickListener(this);
            //itemMessageBodyTextView.setOnLongClickListener(this);
        }

        void setMessage(Message message) {
            int viewType = MessagesAdapter.this.getItemViewType(getLayoutPosition());
            if (message.getFile()==null) {
                itemMessageBodyTextView.setText(message.getBody());
                boolean shouldHideDate = viewType == VIEW_TYPE_SENT || viewType == VIEW_TYPE_RECEIVED;
                itemMessageDateTextView.setVisibility(shouldHideDate ? GONE : VISIBLE);
                if (!shouldHideDate) {
                    itemMessageDateTextView.setText(getDatePretty(message.getTimestamp(), true));
                }
            }
            if (message.getFile() != null) {
                //viewHolder.tvIsLocation(View.GONE);
                setIvChatPhoto(message.getFile().getItem().getPhotoUrl1());
            }
        }

        private void setIvChatPhoto(String url_file) {
            if (ivChatPhoto == null) return;
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.fitCenter().centerCrop();
            Glide.with(ivChatPhoto.getContext()).load(url_file)
                    .apply(requestOptions)
                    .into(ivChatPhoto);
            ivChatPhoto.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //if (selectedPositions.contains(getLayoutPosition())) {
            //    selectedPositions.remove(Integer.valueOf(getLayoutPosition()));
            //    setDateVisibility(GONE);
            //} else {
            //    selectedPositions.add(getLayoutPosition());
            //    setDateVisibility(VISIBLE);
            //}
        }

        private void setDateVisibility(int visibility) {
            TransitionManager.beginDelayedTransition(itemMessageParent);
            itemMessageDateTextView.setVisibility(visibility);
        }
        @Optional
        @OnLongClick(R.id.item_message_body_text_view)
        @Override
        public boolean onLongClick(View v) {
            Message message = getItem(getLayoutPosition());
            if (message != null) {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(
                        context.getString(R.string.clipboard_title_copied_message),
                        message.getBody());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, R.string.message_message_copied, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        @Optional
        @OnClick( R.id.item_box)
        public void itemClicked(View v){
            EventBus.getDefault().post(getSnapshots().get(getLayoutPosition()).getFile().getItem());
            //Toast.makeText(context, "char button....", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDatePretty(long timestamp, boolean showTimeOfDay) {
        DateTime yesterdayDT = new DateTime(DateTime.now().getMillis() - 1000 * 60 * 60 * 24);
        yesterdayDT = yesterdayDT.withTime(0, 0, 0, 0);
        Interval today = new Interval(DateTime.now().withTimeAtStartOfDay(), Days.ONE);
        Interval yesterday = new Interval(yesterdayDT, Days.ONE);
        DateTimeFormatter timeFormatter = DateTimeFormat.shortTime();
        DateTimeFormatter dateFormatter = DateTimeFormat.shortDate();
        if (today.contains(timestamp)) {
            if (showTimeOfDay) {
                return timeFormatter.print(timestamp);
            } else {
                return context.getString(R.string.today);
            }
        } else if (yesterday.contains(timestamp)) {
            return context.getString(R.string.yesterday);
        } else {
            return dateFormatter.print(timestamp);
        }
    }
}
