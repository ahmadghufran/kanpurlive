package com.kanpurlive.ghufya.kanpur_live;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Mahmoud on 3/13/2017.
 */

class ShowChatUserAdapter extends FirestoreRecyclerAdapter<Shop, ShowChatUserAdapter.ShopViewHolder> {

    // private final Context context;
    String[] list = new String[]{"212","sa"};
    private final Context context;
    ShowChatUserAdapter(FirestoreRecyclerOptions<Shop> options, Context context) {

        //super(Shop.class, R.layout.item_user, ShopViewHolder.class, ref);
        super(options);
        this.context = context;
    }
  /*  ShopsAdapter(String[] li){
        super(li);
    }*/
    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);

        return new ShopViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull ShopViewHolder holder, int position, @NonNull Shop model) {
        holder.setShop(model);
    }

    class ShopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_shop_image_view)
        ImageView itemShopImageView;
        @BindView(R.id.shop_name_text_view)
        TextView itemFriendNameTextView;
        @BindView(R.id.shop_address_text_view)
        TextView itemFriendEmailTextView;
        @BindView(R.id.item_user_parent)
        CardView itemShopParent;

        ShopViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemShopParent.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_user_parent:
                    EventBus.getDefault().post(getSnapshots().get(getLayoutPosition()).getUid());
                    break;
            }
        }

        void setShop(Shop user) {
            itemFriendNameTextView.setText(user.getDisplayName());
            itemFriendEmailTextView.setText(user.getEmail());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder_user)
                    .centerCrop()
                    .dontAnimate();
            //.bitmapTransform(new CropCircleTransformation(context));

            Glide.with(context)
                    .load(user.getPhotoUrl())
                    .apply(requestOptions)
                    .into(itemShopImageView);
        }
    }
}


/*
package com.google.firebase.quickstart.fcm;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> {
    private String[] mDataset;
    @BindView(R.id.item_friend_name_text_view)
    TextView itemFriendNameTextView;
    @BindView(R.id.item_friend_email_text_view)
    TextView itemFriendEmailTextView;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CardView mTextView;

        public ViewHolder(CardView v) {
            super(v);
            mTextView = v;

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ShopsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ShopsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        ViewHolder vh = new ViewHolder(v);
        ButterKnife.bind(this, v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
       // holder.mTextView.setText(mDataset[position]);
        itemFriendNameTextView.setText("aaa");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

}*/
