package com.kanpurlive.ghufya.kanpur_live;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kanpurlive.ghufya.kanpur_live.R;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.tooltip.Tooltip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

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

    private final int PICK_IMAGE_REQUEST = 71;
    private final int GALLERY = 1, CAMERA = 2;

    private static final String IMAGE_DIRECTORY = "/klive";
    boolean isColorAdded, isPhoto1Added, isPhoto2Added, isPhoto3Added;
    String photoName=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        isColorAdded=false;
        photoName=null;
        chatButton.setVisibility(View.INVISIBLE);
        colorItem.setBackgroundResource(R.drawable.ic_color_pick);
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
        priceItem.setText("Rs: "+priceInput.getText()+"/-");
    }
    @OnTextChanged(R.id.size_input)
    public void setSizeItem(){
        // Toast.makeText(this, "sdssd", Toast.LENGTH_LONG).show();
        sizeItem.setText("Size:  "+sizeInput.getText());
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
                    //sendFileFirebase(storageRef,contentURI);
                    Toast.makeText(this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);
                    setPhotoFlags();

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

    private void setPhotoFlags() {
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
        Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ShopsThread.class));
        finish();
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
}