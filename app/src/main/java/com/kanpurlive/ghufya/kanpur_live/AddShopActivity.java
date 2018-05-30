package com.kanpurlive.ghufya.kanpur_live;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class AddShopActivity extends AppCompatActivity {

    @BindView(R.id.item_shop_image_view)
    ImageView itemShopImageView;
    @BindView(R.id.shop_name_text_view)
    TextView shop_name_text_view;
    @BindView(R.id.shop_address_text_view)
    TextView shop_address_text_view;

    @BindView(R.id.shopName)
    EditText shopName;
    @BindView(R.id.phoneNumber)
    EditText phoneNumber;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.city)
    EditText city;
    String downloadUrl;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int GALLERY = 1, CAMERA = 2;
    private FirebaseFirestore mDatabase;
    private static final String IMAGE_DIRECTORY = "/klive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);
        ButterKnife.bind(this);
        mDatabase = FirebaseFirestore.getInstance();
    }

    @OnTextChanged(R.id.shopName)
    public void setShopName(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        shop_name_text_view.setText(shopName.getText());
    }
    @OnTextChanged(R.id.address)
    public void setAddress(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        shop_address_text_view.setText(address.getText());
    }
    @OnClick(R.id.item_shop_image_view)
    public void addNewItemImage1(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        showPictureDialog();
    }
    @OnClick(R.id.submit_shop)
    public void submitShop(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        addUserToDatabase();
    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    //sendFileFirebase(storageRef,contentURI);
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    //itemShopImageView.setImageBitmap(bitmap);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.placeholder_user)
                            .centerCrop()
                            .dontAnimate();
                    //.bitmapTransform(new CropCircleTransformation(context));

                    Glide.with(this)
                            .load(bitmap)
                            .apply(requestOptions)
                            .into(itemShopImageView);

                    //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));

                    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                    final StorageReference ref = storageRef.child("images/"+"sample.jpg");
                    UploadTask uploadTask = ref.putFile(contentURI);

// Register observers to listen for when the download is done or if it fails
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Toast.makeText(AddShopActivity.this, "success.....!", Toast.LENGTH_SHORT).show();
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL

                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(AddShopActivity.this, "success.2222....!", Toast.LENGTH_SHORT).show();

                                Uri downloadUri = task.getResult();
                                downloadUrl = downloadUri.toString();
                                Toast.makeText(AddShopActivity.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                  // downloadUrl= urlTask.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            itemShopImageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void addUserToDatabase() {
       Shop newShop = new Shop(
                shopName.getText().toString(),
                address.getText().toString(),
                phoneNumber.getText().toString(),
                downloadUrl
        );

       /* String instanceId = FirebaseInstanceId.getInstance().getToken();
        if (instanceId != null) {
            user.setInstanceId(instanceId);
        }*/
        mDatabase.collection("shops")
                .add(newShop);
    }
}
