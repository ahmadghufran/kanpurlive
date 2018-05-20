package com.kanpurlive.ghufya.kanpur_live;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ItemThreadActivity extends AppCompatActivity {


    @BindView(R.id.activity_item_thread_recycler)
    EmptyStateRecyclerView shopsRecycler;
    @BindView(R.id.activity_item_thread_empty_view)
    TextView emptyView;
    @BindView(R.id.add_item)
    FloatingActionButton addItem;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore mDatabase;
    ItemsAdapter adapter;
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

        mDatabase = FirebaseFirestore.getInstance();

        initializeFirebaseAuthListener();
        initializeUsersRecycler();
    }

    private void initializeUsersRecycler() {
        Query query  = mDatabase.collection("shops");
        FirestoreRecyclerOptions<Item> options = new FirestoreRecyclerOptions.Builder<Item>()
                .setQuery(query , Item.class)
                .build();
        adapter = new ItemsAdapter(options, this);
        shopsRecycler.setAdapter(adapter);
        shopsRecycler.setLayoutManager(new LinearLayoutManager(this));
        shopsRecycler.setEmptyView(emptyView);
    }

    private void initializeFirebaseAuthListener() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    addUserToDatabase(user);
                    Log.d("@@@@", "home:signed_in:" + user.getUid());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserSelected(String id) {
        Intent thread = new Intent(this, MainActivity.class);
        thread.putExtra(Constants.USER_ID_EXTRA, id);
        startActivity(thread);
    }

    @OnClick(R.id.add_item)
    public void addNewItem(){
        Intent thread = new Intent(this, AddItemActivity.class);
        startActivity(thread);
    }
}
