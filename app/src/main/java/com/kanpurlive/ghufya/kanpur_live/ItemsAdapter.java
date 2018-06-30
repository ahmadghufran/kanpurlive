package com.kanpurlive.ghufya.kanpur_live;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/*
 * Created by Mahmoud on 3/13/2017.
 */

class ItemsAdapter extends FirestoreRecyclerAdapter<Item, ItemsAdapter.ItemViewHolder> {

    // private final Context context;
    String[] list = new String[]{"212","sa"};
    private final Context context;
    ItemsAdapter(FirestoreRecyclerOptions<Item> options, Context context) {

        //super(Item.class, R.layout.item_user, ItemViewHolder.class, ref);
        super(options);
        this.context = context;
    }
  /*  ItemsAdapter(String[] li){
        super(li);
    }*/
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_in_shop, parent, false);
        int width = parent.getMeasuredWidth();
        itemView.setMinimumHeight(width+width/5);
        return new ItemViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Item model) {
        holder.setItem(model);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_user_image_view)
        ImageView itemItemImageView;
        @BindView(R.id.item_user_image_view2)
        ImageView itemItemImageView2;
        @BindView(R.id.item_user_image_view3)
        ImageView itemItemImageView3;
        @BindView(R.id.price_item)
        TextView priceItem;
        @BindView(R.id.size_item)
        TextView sizeItem;
        @BindView(R.id.desc_item)
        TextView descItem;
        @BindView(R.id.color_item)
        View colorItem;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //itemItemParent.setOnClickListener(this);
        }
        @Optional
        @OnClick( R.id.chat_button)
        public void chat(View v){

            Item  item = getSnapshots().get(getLayoutPosition());
            item.setPosition(getAdapterPosition());
            EventBus.getDefault().post(item);
            Toast.makeText(context, "char button....", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.item_user_parent:
                    EventBus.getDefault().post(getSnapshots().get(getLayoutPosition()).getUid());
                    Toast.makeText(v.getContext(), "parent ....", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.chat_button:
                    Toast.makeText(v.getContext(), "char button....", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        void setItem(Item item) {
            //itemFriendNameTextView.setText(user.getDisplayName());
            //itemFriendEmailTextView.setText(user.getEmail());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder_user)
                    .centerCrop()
                    .dontAnimate();
            //.bitmapTransform(new CropCircleTransformation(context));
            //itemItemImageView.getLayoutParams().height= itemItemImageView.getLayoutParams().width;
            colorItem.setBackgroundColor(Color.parseColor("#"+item.getColor()));
            sizeItem.setText(item.getSize());
            descItem.setText(item.getDescription());
            priceItem.setText("\u20B9"+item.getPrice());

            Glide.with(context)
                    .load(item.getPhotoUrl1())
                    .apply(requestOptions)
                    .into(itemItemImageView);
            Glide.with(context)
                    .load(item.getPhotoUrl2())
                    .apply(requestOptions)
                    .into(itemItemImageView2);
            Glide.with(context)
                    .load(item.getPhotoUrl3())
                    .apply(requestOptions)
                    .into(itemItemImageView3);
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

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {
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
    public ItemsAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
