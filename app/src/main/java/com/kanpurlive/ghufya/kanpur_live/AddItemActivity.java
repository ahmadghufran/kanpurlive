package com.kanpurlive.ghufya.kanpur_live;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kanpurlive.ghufya.kanpur_live.R;
import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerDialog;
import com.skydoves.colorpickerpreference.ColorPickerView;

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
    @BindView(R.id.color_selector)
    ImageButton colorSelector;
    @BindView(R.id.desc_item)
    TextView descItem;

    ImageView imageView;
    private final int PICK_IMAGE_REQUEST = 71;
    private final int GALLERY = 1, CAMERA = 2;

    private static final String IMAGE_DIRECTORY = "/klive";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Icepick.restoreInstanceState(this, savedInstanceState);
        ButterKnife.bind(this);
        showColorDialog();
    }

    private void showColorDialog() {
        ColorPickerDialog.Builder builder = new ColorPickerDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setTitle("ColorPicker Dialog");
        builder.setPreferenceName("MyColorPickerDialog");
        //builder.setFlagView(new CustomFlag(this, R.layout.layout_flag));
        builder.setPositiveButton(getString(R.string.app_name), new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                TextView textView = findViewById(R.id.textView);
                //textView.setText("#" + colorEnvelope.getHtmlCode());

                //LinearLayout linearLayout = findViewById(R.id.linearLayout);
                colorSelector.setBackgroundColor(colorEnvelope.getColor());
            }
        });
        builder.setNegativeButton(getString(R.string.app_name), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void displayLoadingState() {

    }
    @OnClick(R.id.color_selector)
    public void colorSelector(){
        Toast.makeText(this, "color dialog", Toast.LENGTH_LONG).show();
        showPictureDialog();
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
        showPictureDialog();
    }
    @OnClick(R.id.item_user_image_view2)
    public void addNewItemImage2(){
        imageView=itemItemImageView2;
        Toast.makeText(this, "sdssd222", Toast.LENGTH_LONG).show();
        showPictureDialog();
    }
    @OnClick(R.id.item_user_image_view3)
    public void addNewItemImage3(){
        imageView=itemItemImageView3;
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
}
