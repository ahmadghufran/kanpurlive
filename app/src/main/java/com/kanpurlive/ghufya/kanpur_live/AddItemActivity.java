package com.kanpurlive.ghufya.kanpur_live;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.Icepick;

public class AddItemActivity extends BaseActivity {


    @BindView(R.id.item_user_image_view)
    ImageView itemItemImageView;
    @BindView(R.id.item_user_image_view2)
    ImageView itemItemImageView2;
    @BindView(R.id.item_user_image_view3)
    ImageView itemItemImageView3;
    @BindView(R.id.size_input)
    EditText sizeInput;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.price_input)
    EditText priceInput;
   /* @BindView(R.id.color_selector)
    ImageButton colorSelector;*/
    @BindView(R.id.color_item)
    View colorItem;
    @BindView(R.id.desc_item)
    TextView descItem;
    @BindView(R.id.price_item)
    TextView priceItem;
    @BindView(R.id.size_item)
    TextView sizeItem;
    ImageView imageView;
    @BindView(R.id.size_input_layout)
    TextInputLayout size_input_layout;
    @BindView(R.id.descriptionLayout)
    TextInputLayout descriptionLayout;
    @BindView(R.id.price_input_layout)
    TextInputLayout priceInput_layout;
    @BindView(R.id.chat_button)
    Button chatButton;
    @BindView(R.id.loading)
    ProgressBar spinner;
    @BindView(R.id.grayed_layout)
    LinearLayout grayed_layout;
    @BindView(R.id.grayed_layout_small)
    LinearLayout grayed_layout_small;

    String urlStr=null;
    String downloadUrl=null;
    String colorHtml=null;

    private final int PICK_IMAGE_REQUEST = 71;
    private final int GALLERY = 1, CAMERA = 2;

    private static final String IMAGE_DIRECTORY = "/klive";
    boolean isColorAdded, isPhoto1Added, isPhoto2Added, isPhoto3Added;
    String photoName=null;
    String localPhoto1Url = null;
    String localPhoto2Url = null;
    String localPhoto3Url = null;
    StorageReference storageRef;
    private FirebaseFirestore mDatabase;
    Item item;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        count =0;
        isColorAdded=false;
        photoName=null;
        chatButton.setVisibility(View.INVISIBLE);
        colorItem.setBackgroundResource(R.drawable.ic_color_pick);

        storageRef  = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseFirestore.getInstance();

        localPhoto1Url = null;
        localPhoto2Url = null;
        localPhoto3Url = null;
        colorHtml=null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "nahi hai granted Saved!", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        123);

                return;
            }
        }
    }

    private void showColorDialog() {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("ColorPicker Dialog");
        builder.setPreferenceName("MyColorPickerDialog");
        //builder.setFlagView(new CustomFlag(this, R.layout.layout_flag));
        builder.setPositiveButton(getString(R.string.app_name), new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
              //  TextView textView = findViewById(R.id.textView);
                //textView.setText("#" + colorEnvelope.getHtmlCode());

                //LinearLayout linearLayout = findViewById(R.id.linearLayout);
                isColorAdded=true;
                colorHtml = colorEnvelope.getColorHtml();
                colorItem.setBackgroundColor(colorEnvelope.getColor());
            }
        });
        builder.setNegativeButton(getString(R.string.app_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                isColorAdded = false;
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void displayLoadingState() {

    }
    @OnTextChanged(R.id.price_input)
    public void setPriceItem(){
       // Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        priceItem.setText(priceInput.getText()+"/-");
    }
    @OnTextChanged(R.id.size_input)
    public void setSizeItem(){
        // Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        sizeItem.setText(sizeInput.getText());
    }
    @OnClick(R.id.color_item)
    public void colorSelector(){
        Toast.makeText(this, "color dialog", Toast.LENGTH_LONG).show();
        showColorDialog();
    }

    @OnTextChanged(R.id.description)
    public void setDescItem(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        descItem.setText(description.getText());
    }
    @OnClick(R.id.item_user_image_view)
    public void addNewItemImage1(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        imageView=itemItemImageView;
        photoName="firstFrame";
        showPictureDialog();
    }
    @OnClick(R.id.item_user_image_view2)
    public void addNewItemImage2(){
        imageView=itemItemImageView2;
        Toast.makeText(this, "sdssd222", Toast.LENGTH_LONG).show();
        photoName="secondFrame";
        showPictureDialog();
    }
    @OnClick(R.id.item_user_image_view3)
    public void addNewItemImage3(){
        imageView=itemItemImageView3;
        photoName="thirdFrame";
        Toast.makeText(this, "sdssd333", Toast.LENGTH_LONG).show();
        showPictureDialog();
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
                    imageView.setImageBitmap(bitmap);
                    setPhotoFlags();
                    urlStr = getRealPathFromURI(contentURI.toString());
                    setLocalImageUrl();

/*
                    String compressedFileUrl = decodeFile(urlStr,800,800);
                    uploadFileToStorage(Uri.fromFile(new File(compressedFileUrl)));*/
                    //sendFileFirebase(storageRef,contentURI);
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "granted Saved!", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    //decodeFile(urlStr,800,800);


                } else {
                    Toast.makeText(this, "permission nahi mili abhi !", Toast.LENGTH_SHORT).show();
                    finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private  boolean checkAndRequestPermissions() {
        //int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

       /* if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }*/
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }
            final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            String s = name+ ++count+".png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;
    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }
    private void setPhotoFlags() {
        switch(photoName){
            case "firstFrame": isPhoto1Added=true; break;
            case "secondFrame": isPhoto2Added=true; break;
            case "thirdFrame": isPhoto3Added=true; break;
            default: isPhoto1Added = isPhoto2Added = isPhoto3Added = false;
                photoName=null; break;
        }
    }
    private void setPhotoFlagsOnFail() {
        switch(photoName){
            case "firstFrame": isPhoto1Added=true; break;
            case "secondFrame": isPhoto2Added=true; break;
            case "thirdFrame": isPhoto3Added=true; break;
            default: isPhoto1Added = isPhoto2Added = isPhoto3Added = false;
                photoName=null; break;
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
    @OnClick(R.id.submit_item)
    public void submitShop(){
        Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();

        submitForm();
        //addItemToDatabase(); To Do
    }
    private void submitForm() {
        if (!validateDescription()) {
            return;
        }

        if (!validateSize()) {
            return;
        }

        if (!validatePrice()) {
            return;
        }

        if (!validateColor()) {
            return;
        }
        if (!validatePhoto1()) {
            return;
        }
        if (!validatePhoto2()) {
            return;
        }
        if (!validatePhoto3()) {
            return;
        }
        /*if (!validateShopPhoto()) {
            return;
        }
        if (!validateShopType()) {
            return;
        }*/
        spinner.setVisibility(View.VISIBLE);
        grayed_layout.setVisibility(View.VISIBLE);
        grayed_layout_small.setVisibility(View.VISIBLE);

        item = new Item(null, sizeInput.getText().toString(), description.getText().toString(), null, colorHtml,
                Integer.parseInt(priceInput.getText().toString()),0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        final ArrayList<Task<Uri>> tasks = new ArrayList<>();

        String compressedFileUrl = decodeFile(localPhoto1Url,800,800);

        String compressedFile2Url = decodeFile(localPhoto2Url,800,800);

        String compressedFile3Url = decodeFile(localPhoto3Url,800,800);

        tasks.add(uploadFileToStorage(Uri.fromFile(new File(compressedFileUrl)), "firstFrame"));

        tasks.add(uploadFileToStorage(Uri.fromFile(new File(compressedFile2Url)),"secondFrame"));

        tasks.add(uploadFileToStorage(Uri.fromFile(new File(compressedFile3Url)), "thirdFrame"));

        Tasks.whenAll(tasks).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("AddItemActivity", "ALL Images HAVE BEEN UPLOADED");
                addItemToDatabase();

            }
        });

    }
    private void addItemToDatabase() {

       /* String instanceId = FirebaseInstanceId.getInstance().getToken();
        if (instanceId != null) {
            user.setInstanceId(instanceId);
        }*/
        String ownerId = FirebaseAuth.getInstance().getUid();
        item.setUid(ownerId);
        mDatabase.collection("items")
                .document(ownerId)
                .collection("itemCollection")
                .add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AddItemActivity", "ALL TASKS HAVE BEEN UPLOADED");
                        //dialog.cancel();
                        spinner.setVisibility(View.GONE);
                        grayed_layout.setVisibility(View.GONE);
                        grayed_layout_small.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddItemActivity.this, ShopsThread.class));
                        finish();
                    }
                });
    }

    private boolean validatePhoto1() {
        if(!isPhoto1Added){
            addNewItemImage1();
            return false;
        }
        return true;
    }
    private boolean validatePhoto2() {
        if(!isPhoto2Added){
            addNewItemImage2();
            return false;
        }
        return true;
    }
    private boolean validatePhoto3() {
        if(!isPhoto3Added){
            addNewItemImage3();
            return false;
        }
        return true;
    }
    private boolean validateColor() {
        if(!isColorAdded){
            colorSelector();
            return false;
        }
        return true;
    }

    private boolean validatePrice() {
        String sizeInputStr = priceInput.getText().toString().trim();
        if (sizeInputStr.isEmpty() || !TextUtils.isDigitsOnly(sizeInputStr)) {
            priceInput_layout.setError(getString(R.string.err_msg_phone_number));
            requestFocus(priceInput);
            return false;
        } else {
            priceInput_layout.setErrorEnabled(false);
        }
    return true;
    }

    private boolean validateSize() {

        String sizeInputStr = sizeInput.getText().toString().trim();
        if (sizeInputStr.isEmpty() || !TextUtils.isDigitsOnly(sizeInputStr)) {
            size_input_layout.setError(getString(R.string.err_msg_phone_number));
            requestFocus(sizeInput);
            return false;
        } else {
            size_input_layout.setErrorEnabled(false);
        }
        return true;
    }
    private boolean validateDescription() {
        if (description.getText().toString().trim().isEmpty()) {
            descriptionLayout.setError(getString(R.string.err_msg_name));
            requestFocus(description);
            return false;
        } else {
            descriptionLayout.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

    }
    private Task<Uri> uploadFileToStorage(Uri contentURI, final String frameName) {

        final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        final StorageReference ref = storageRef.child("images/"+"sample_"+name+ ++count+".jpg");
        UploadTask uploadTask = ref.putFile(contentURI);
// Register observers to listen for when the download is done or if it fails
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "success.....!", Toast.LENGTH_SHORT).show();
                    setPhotoFlagsOnFail();
                    throw task.getException();
                }
                setPhotoFlags();
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {

            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddItemActivity.this, "success.2222....!", Toast.LENGTH_SHORT).show();

                    Uri downloadUri = task.getResult();
                    downloadUrl = downloadUri.toString();
                    photoName = frameName;
                    setPhotoFlags();
                    setDownloadUrl();
                    Toast.makeText(AddItemActivity.this, downloadUri.toString(), Toast.LENGTH_SHORT).show();

                } else {
                    // Handle failures
                    setPhotoFlagsOnFail();
                    // ...
                }
            }
        });
        return urlTask;
    }

    private void setDownloadUrl() {
        switch(photoName){
            case "firstFrame":  item.setPhotoUrl1(downloadUrl); break;
            case "secondFrame": item.setPhotoUrl2(downloadUrl); isPhoto2Added=true; break;
            case "thirdFrame": item.setPhotoUrl3(downloadUrl); isPhoto3Added=true; break;
            default: isPhoto1Added = isPhoto2Added = isPhoto3Added = false;
                photoName=null; break;
        }
    }

    private void setLocalImageUrl() {
        switch(photoName){
            case "firstFrame":  localPhoto1Url = urlStr; break;
            case "secondFrame": localPhoto2Url = urlStr; break;
            case "thirdFrame": localPhoto3Url = urlStr;  break;
            default: isPhoto1Added = isPhoto2Added = isPhoto3Added = false;
                photoName=null; break;
        }
    }

}