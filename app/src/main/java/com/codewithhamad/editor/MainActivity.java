package com.codewithhamad.editor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.codewithhamad.editor.databinding.ActivityMainBinding;
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    int IMAGE_REQUEST_CODE= 45;
    int CAMERA_REQUEST_CODE= 14;
    int EDITED_IMAGE_RESULT_CODE= 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // admob initialization
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // loading ads
        AdRequest adRequest= new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        getSupportActionBar().hide();

        binding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });

        binding.cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA}, 32);
                }
                else{
                    Intent cameraIntent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST_CODE){
            if(data.getData() != null){
                Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                dsPhotoEditorIntent.setData(data.getData());

                // directory for edited images
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "picaso");

                int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
                dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
                startActivityForResult(dsPhotoEditorIntent, EDITED_IMAGE_RESULT_CODE);
            }
        }

        if(requestCode == EDITED_IMAGE_RESULT_CODE){
            if(data.getData()!=null) {
                Toast.makeText(this, "Image saved to gallery.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }
        }

        if(requestCode == CAMERA_REQUEST_CODE){
            Bitmap cameraPhoto= (Bitmap) data.getExtras().get("data");
            Uri uri= getImageUri(cameraPhoto);

            // navigating to edit activity after capturing image from camera
            Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
            dsPhotoEditorIntent.setData(uri);

            // directory for edited images
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "picaso");

            int[] toolsToHide = {DsPhotoEditorActivity.TOOL_ORIENTATION, DsPhotoEditorActivity.TOOL_CROP};
            dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);
            startActivityForResult(dsPhotoEditorIntent, EDITED_IMAGE_RESULT_CODE);
        }
    }

    public Uri getImageUri(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path= MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", "Desc");
        return Uri.parse(path);
    }
}