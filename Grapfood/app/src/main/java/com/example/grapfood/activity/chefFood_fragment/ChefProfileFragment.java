package com.example.grapfood.activity.chefFood_fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.chefFoodPanel.chef_postDish;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ChefProfileFragment extends Fragment implements View.OnClickListener {
    //khai bao biến

    ImageView profileIv;
    ImageButton btnsetting;
    Button postDish;
    ConstraintLayout backimg;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    //hằng số chọn hình ảnh
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private Uri image_uri;

    String[] cameraPermission;
    String[] storagePermission;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chef_prifile,null);
        backimg = (ConstraintLayout)v.findViewById(R.id.back1);
        //natural movement
        //chuyển động tự nhiên
        AnimationDrawable animationDrawable = new AnimationDrawable();
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.bg2),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img2),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img4),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img5),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img6),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img7),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img8),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.bg3),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img9),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img10),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img11),3000);
        animationDrawable.addFrame(getResources().getDrawable(R.drawable.img11),3000);
        animationDrawable.setOneShot(false);
        animationDrawable.setEnterFadeDuration(850);
        animationDrawable.setExitFadeDuration(1600);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        backimg.setBackgroundDrawable(animationDrawable);
        animationDrawable.start();
//        profileIv = (ImageView) v.findViewById(R.id.imv_avatar);
        postDish =  (Button)v.findViewById(R.id.post_dish);

        postDish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), chef_postDish.class));
            }
        });

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //dành cho truy cập firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        profileIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //display image in imageview
//                //hiển thị hình ảnh trong imageview
////                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
////                        Manifest.permission.READ_EXTERNAL_STORAGE);
////
////                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
////                    startGallery();
////                } else {
////                    ActivityCompat.requestPermissions(getActivity(),
////                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
////                            2000);
////                }
//                showImagePickDialog();
//            }
//        });
        return v;
    }
    private void showImagePickDialog() {
        //options to display in dialog
        //các tùy chọn để hiển thị trong hộp thoại
        String[] options = {"Máy ảnh", "Bộ sưu tập hoặc Kho ảnh"};//camara, gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Vui lòng chọn hình ảnh ở dạng")//pick image
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // máy ảnh được chọn //camara clicked
                            if (checkCameraPermission()) {
                                //camera permissin allowed
                                pickFromCamera();
                            } else {
                                //not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //bộ sưu tập được chọn //galley clicked
                            if (checkStoragePermission()) {
                                //storage permissin allowed
                                pickFromGallery();
                            } else {
                                //not allowed, request
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//image
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");//Temp_Image Title
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");//Temp_Image Description

        image_uri = getActivity().getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), cameraPermission, CAMERA_REQUEST_CODE);
    }
    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(getActivity(), storagePermission, STORAGE_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //super method removed
//        if(resultCode == RESULT_OK) {
//            if(requestCode == 1000){
//                Uri returnUri = data.getData();
//                Bitmap bitmapImage = null;
//                try {
//                    bitmapImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                profileIv.setImageBitmap(bitmapImage);
//            }
//        }
        //Uri returnUri;
        //returnUri = data.getData();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.post_dish:
                break;
        }
    }
}
