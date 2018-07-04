package com.kanpurlive.ghufya.kanpur_live;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;

public class ItemThreadActivity extends AppCompatActivity {


    @BindView(R.id.activity_item_thread_recycler)
    EmptyStateRecyclerView shopsRecycler;
    @BindView(R.id.activity_item_thread_empty_view)
    TextView emptyView;
    @BindView(R.id.add_item)
    FloatingActionButton addItem;
    @BindView(R.id.toolbar_shop)
    Toolbar toolbar;

    private FirebaseUser owner;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDatabase;
    ItemsAdapter adapter;
    @State
    String userUid;
    int position=-1;
    private Shop shopUser;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        adapter.startListening();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        adapter.stopListening();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_thread);
        ButterKnife.bind(this);
        //setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            userUid = getIntent().getStringExtra(Constants.USER_ID_EXTRA);
        }
        shopUser = null;
        toolbar.setLogo(R.drawable.ic_indian_rupee);
        setSupportActionBar(toolbar);

        position = getIntent().getIntExtra(Constants.ITEM_POSITION_EXTRA,-1);
        mDatabase = FirebaseFirestore.getInstance();

        loadUserDetails();
        initializeFirebaseAuthListener();
        initializeUsersRecycler();

    }
    private void loadUserDetails() {
        DocumentReference docRef = mDatabase.collection("shops").document(userUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                shopUser = documentSnapshot.toObject(Shop.class);
                toolbar.setTitle(shopUser.getDisplayName());
                //initializeMessagesRecycler();
            }
        });
    }
    private void initializeUsersRecycler() {
        Query query = mDatabase.collection("items").document(userUid).collection("itemCollection");
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query, Item.class)
                .build();
        adapter = new ItemsAdapter(options, this);
        shopsRecycler.setAdapter(adapter);
       /* RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };*/
        LinearLayoutManagerWithSmoothScroller linearLayoutManager = new LinearLayoutManagerWithSmoothScroller(this);

        shopsRecycler.setLayoutManager(linearLayoutManager);
        if(position!=-1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    shopsRecycler.smoothScrollToPosition(position);
                    // yourList.scrollToPosition(position);
                }
            }, 200);
           //shopsRecycler.smoothScrollToPosition(position);
           /* smoothScroller.setTargetPosition(position);
            linearLayoutManager.startSmoothScroll(smoothScroller);*/
        }

        shopsRecycler.setEmptyView(emptyView);
    }
    private void initializeFirebaseAuthListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                owner = firebaseAuth.getCurrentUser();
                if (owner != null) {
                    //addUserToDatabase(owner);
                    Log.d("@@@@", "home:signed_in:" + owner.getUid());
                } else {
                    Log.d("@@@@", "home:signed_out");
                    Intent login = new Intent(ItemThreadActivity.this, MainActivity.class);
                    startActivity(login);
                    finish();
                }
            }
        };
    }

    private void addUserToDatabase(FirebaseUser firebaseUser) {
        Item user = new Item(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getUid(),
                firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString()
        );

        String instanceId = FirebaseInstanceId.getInstance().getToken();
        if (instanceId != null) {
            user.setInstanceId(instanceId);
        }
        mDatabase.collection("shops")
                .document(user.getUid()).set(user);
    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onUserSelected(Item item) {
        final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        long timestamp = new Date().getTime();
        long dayTimestamp = getDayTimestamp(timestamp);
        FileModel fileModel = new FileModel("img", item, name, "");
        Intent thread = new Intent(this, ChatThreadActivity.class);

        String ownerUid = owner.getUid();
        String userUid = item.getUid();
        Message message =
                new Message(timestamp, -timestamp, dayTimestamp, "", ownerUid, userUid, fileModel);
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

        thread.putExtra(Constants.USER_ID_EXTRA, item.getUid());
        thread.putExtra(Constants.PHOTO_URL_EXTRA, item.getPhotoUrl());
        thread.putExtra(Constants.Desc_EXTRA, item.getDescription() + " Price - " + item.getPrice());
        startActivity(thread);
    }

    @OnClick(R.id.add_item)
    public void addNewItem() {
        Intent thread = new Intent(this, AddItemActivity.class);
        startActivity(thread);
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
}
