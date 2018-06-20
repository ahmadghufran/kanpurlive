package com.kanpurlive.ghufya.kanpur_live;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;

import static android.view.View.INVISIBLE;

public class ChatThreadActivity extends AppCompatActivity implements TextWatcher {
    private Shop user;
    private FirebaseUser owner;
    @State
    String userUid;
    @State
    boolean emptyInput;
    @BindView(R.id.activity_thread_send_fab)
    FloatingActionButton sendFab;
    @BindView(R.id.activity_thread_input_edit_text)
    TextInputEditText inputEditText;
    @BindView(R.id.activity_thread_empty_view)
    TextView emptyView;
    @BindView(R.id.activity_thread_progress)
    ProgressBar progress;

    @BindView(R.id.activity_thread_toolbar)
    Toolbar toolbar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    MessagesAdapter adapter;
    @BindView(R.id.activity_thread_messages_recycler)
    EmptyStateRecyclerView messagesRecycler;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mAuth.addAuthStateListener(mAuthListener);
       // adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        //adapter.stopListening();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_thread);
        ButterKnife.bind(this);
        //setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            userUid = getIntent().getStringExtra(Constants.USER_ID_EXTRA);
        }
        sendFab.requestFocus();
        mDatabase = FirebaseFirestore.getInstance();
        progress.setVisibility(INVISIBLE);
        loadUserDetails();
        initializeFirebaseAuthListener();
        initializeInteractionListeners();
    }

    private void loadUserDetails() {
        DocumentReference docRef = mDatabase.collection("shops").document(userUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                 user = documentSnapshot.toObject(Shop.class);
                initializeMessagesRecycler();
            }
        });
    }

    @OnClick(R.id.activity_thread_send_fab)
    public void onClick() {
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        if (user == null || owner == null) {
            Log.d("@@@@", "onSendClick: User:" + user + " Owner:" + owner);
            return;
        }
        long timestamp = new Date().getTime();
        long dayTimestamp = getDayTimestamp(timestamp);
        String body = inputEditText.getText().toString().trim();
        String ownerUid = owner.getUid();
        String userUid = user.getUid();
        Message message =
                new Message(timestamp, -timestamp, dayTimestamp, body, ownerUid, userUid,null);
        mDatabase
                .collection("notifications")
                .document("messages")
                .set(message);
        mDatabase
                .collection("messages")
                .document(userUid)
                .collection(ownerUid)
                .add(message);
        if (!userUid.equals(ownerUid)) {
            mDatabase
                    .collection("messages")
                    .document(ownerUid)
                    .collection(userUid)
                    .add(message);
        }
        inputEditText.setText("");
    }

    private long getDayTimestamp(long timestamp) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            return calendar.getTimeInMillis();
    }

    private void initializeInteractionListeners() {
        inputEditText.addTextChangedListener(this);
    }
    private void initializeFirebaseAuthListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                owner = firebaseAuth.getCurrentUser();
                if (owner  != null) {
                    initializeMessagesRecycler();
                    Log.d("@@@@", "home:signed_in:" + owner.getUid());
                } else {
                    Log.d("@@@@", "home:signed_out");
                    Log.d("@@@@", "thread:signed_out");
                    //Intent login = new Intent(ThreadActivity.this, LoginActivity.class);
                    //startActivity(login);
                    //finish()
                }
            }
        };
    }
    private void initializeMessagesRecycler() {
        if (user == null || owner == null) {
            Log.d("@@@@", "initializeMessagesRecycler: User:" + user + " Owner:" + owner);
            return;
        }
        Query messagesQuery = mDatabase
                .collection("messages")
                .document(owner.getUid())
                .collection(user.getUid())
                .orderBy("negatedTimestamp");
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions
                .Builder<Message>()
                .setLifecycleOwner(this)
                .setQuery(messagesQuery, Message.class)
                .build();
        adapter = new MessagesAdapter(ChatThreadActivity.this, owner.getUid(), options);
        messagesRecycler.setAdapter(null);
        messagesRecycler.setAdapter(adapter);
        messagesRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));
        messagesRecycler.setEmptyView(emptyView);
        messagesRecycler.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                messagesRecycler.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        emptyInput = s.toString().trim().isEmpty();
        displayInputState();
    }
    private void displayInputState() {
        //inputEditText.setEnabled(!isLoading);
        sendFab.setEnabled(!emptyInput );
        //sendFab.setImageResource(isLoading ? R.color.colorTransparent : R.drawable.ic_send);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserSelected(Item item) {
        Toast.makeText(this, "yyyyydddddd", Toast.LENGTH_LONG).show();
        Intent thread = new Intent(this, ItemThreadActivity.class);
        thread.putExtra(Constants.USER_ID_EXTRA, item.getUid());
        thread.putExtra(Constants.ITEM_POSITION_EXTRA, item.getPosition());
        startActivity(thread);
    }

}
