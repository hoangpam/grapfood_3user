package com.example.grapfood.activity.chefFoodPanel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.example.grapfood.activity.bottomnavigation.ChefFoodPanel_BottomNavigation;
import com.example.grapfood.activity.object.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private CircularImageView productIconIv;
    String[] cameraPermission;
    String[] storagePermission;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri image_uri;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    //hằng số chọn hình ảnh
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    private EditText titleEt,descriptionEt,quantilyEt,priceEt,discountpriceEt,discountNoteEt;
    private String productTitle, productDesciptions, productCategory,productQuanlity,originalPrice,discountPrice,discountNote;
    private boolean discountAvailable = false;
    private TextView categoryTv;
    private SwitchCompat discountSwitch;
    private Button addProductBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AddProductActivity.this, ChefFoodPanel_BottomNavigation.class));
                finish();
            }
        });
        productIconIv = (CircularImageView) findViewById(R.id.profileIv);
        //dành cho truy cập firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        productIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                //chọn hình ảnh
                showImagePickDialog();

            }
        });
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};



        titleEt = (EditText) findViewById(R.id.titleEt);
        descriptionEt = (EditText) findViewById(R.id.descriptionEt);
        quantilyEt = (EditText) findViewById(R.id.quantilyEt);
        categoryTv = (TextView) findViewById(R.id.categoryTv);
        categoryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick diolog
                categoryDialog();
            }
        });

        priceEt = (EditText) findViewById(R.id.priceEt);
        discountSwitch = (SwitchCompat) findViewById(R.id.discountSwitch);
        //if discountSwitch is checked: show discountPriceEt, discountNoteEt | if discountSwitch is no checked hide discountPriceEt, discountNoteEt
        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //checked, show discountPriceEt, discountNoteEt
                    discountpriceEt.setVisibility(View.VISIBLE);
                    discountNoteEt.setVisibility(View.VISIBLE);
                }
                else {
                    //unchecked, hide discountPriceEt, discountNoteEt
                    discountpriceEt.setVisibility(View.GONE);
                    discountNoteEt.setVisibility(View.GONE);
                }
            }
        });
        discountpriceEt = (EditText) findViewById(R.id.discountpriceEt);
        discountNoteEt = (EditText) findViewById(R.id.discountNoteEt);

        firebaseAuth = FirebaseAuth.getInstance();

        //setup progressdialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang load Vui lòng chờ...");
        progressDialog.setCanceledOnTouchOutside(false);


        addProductBtn = (Button) findViewById(R.id.addProductBTN);
        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /****Flow:
                ** 1>Input data
                 * 2>Validate data
                 * 3>Add data to db
                 *********/
                inputData();
            }
        });
    }

    private void inputData()
    {
        //1> Input data
        productTitle = titleEt.getText().toString().trim();
        productDesciptions = descriptionEt.getText().toString().trim();
        productCategory = categoryTv.getText().toString().trim();
        productQuanlity = quantilyEt.getText().toString().trim();
        originalPrice = priceEt.getText().toString().trim();
        discountAvailable = discountSwitch.isChecked();//checked true or false
        titleEt.setError("");
        descriptionEt.setError("");
        categoryTv.setError("");
        quantilyEt.setError("");
        priceEt.setError("");
        //2> Validate data
        if(TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Tên sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
            titleEt.setError("Tên sản phẩm là cần thiết ..");
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(productDesciptions)){
            Toast.makeText(this, "Mô tả chi tiết sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
            descriptionEt.setError("Mô tả chi tiết sản phẩm là cần thiết ..");
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Thể sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
            categoryTv.setError("Thể sản phẩm là cần thiết ..");
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(productQuanlity)){
            Toast.makeText(this, "Định lượng sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
            quantilyEt.setError("Định lượng sản phẩm là cần thiết ..");
            return;//don't proceed further
        }
        if(TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Giá sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
            priceEt.setError("Giá sản phẩm là cần thiết ..");
            return;//don't proceed further
        }
        if(discountAvailable){
            //product is with discount
            discountPrice = discountpriceEt.getText().toString().trim();
            discountNote = discountNoteEt.getText().toString().trim();
            discountpriceEt.setError("");
            discountNoteEt.setError("");
            if(TextUtils.isEmpty(discountPrice)){
                Toast.makeText(this, "Số tiền giảm giá sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
                discountpriceEt.setError("Số tiền giảm giá sản phẩm là cần thiết ..");
                return;//don't proceed further
            }
            if(TextUtils.isEmpty(discountNote)){
                Toast.makeText(this, "Tên và mô tả giảm giá sản phẩm là cần thiết ..", Toast.LENGTH_SHORT).show();
                discountNoteEt.setError("Tên và mô tả giảm giá sản phẩm là cần thiết ..");
                return;//don't proceed further
            }
        }
        else{
            //product is without discount
            discountPrice = "0";
            discountNote = "";
        }
        addProduct();
    }

    private void addProduct() {
        //3> Add data to db
        progressDialog.setMessage("Tiến hành thêm sản phẩm ...");
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();
        if(image_uri == null) {
            //upload without image

            //setup data to upload
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("productId",""+timestamp);
            hashMap.put("productTitle",""+productTitle);
            hashMap.put("productDesciptions",""+productDesciptions);
            hashMap.put("productCategory",""+productCategory);
            hashMap.put("productQuanlity",""+productQuanlity);
            hashMap.put("productIcon",""); //no image, set empty
            hashMap.put("originalPrice",""+originalPrice);
            hashMap.put("discountPrice",""+discountPrice);
            hashMap.put("discountNote",""+discountNote);
            hashMap.put("discountAvailable",""+discountAvailable);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("uid",""+firebaseAuth.getUid());

            //add to db
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chef");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //added to db
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, "Đã thêm sản phẩm thành công...", Toast.LENGTH_SHORT).show();
                            clearData();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed adding to db
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{
            //upload with image

            //first upload image to storage

            //name and path of image to be uploaded
            String filePathAndName = "product_image/"+""+timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image upload
                            //get url of uploaded image
                            Task<Uri> uriTask  = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            Uri downloadImageUri = uriTask.getResult();
                            if(uriTask.isSuccessful()){
                                //uri of image received, upload

                                //setup data to upload
                                HashMap<String ,Object> hashMap = new HashMap<>();
                                hashMap.put("productId",""+timestamp);
                                hashMap.put("productTitle",""+productTitle);
                                hashMap.put("productDesciptions",""+productDesciptions);
                                hashMap.put("productCategory",""+productCategory);
                                hashMap.put("productQuanlity",""+productQuanlity);
                                hashMap.put("productIcon",""+downloadImageUri);
                                hashMap.put("originalPrice",""+originalPrice);
                                hashMap.put("discountPrice",""+discountPrice);
                                hashMap.put("discountNote",""+discountNote);
                                hashMap.put("discountAvailable",""+discountAvailable);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("uid",""+firebaseAuth.getUid());

                                //add to db
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chef");
                                reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //added to db
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, "Đã thêm sản phẩm thành công...", Toast.LENGTH_SHORT).show();
                                                clearData();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //failed adding to db
                                                progressDialog.dismiss();
                                                Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploading image
                            progressDialog.dismiss();
                            Toast.makeText(AddProductActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearData(){
        //clear data after uploading product
        titleEt.setText("");
        descriptionEt.setText("");
        categoryTv.setText("");
        quantilyEt.setText("");
        priceEt.setText("");
        discountpriceEt.setText("");
        discountNoteEt.setText("");
        titleEt.setError("");
        descriptionEt.setError("");
        categoryTv.setError("");
        quantilyEt.setError("");
        priceEt.setError("");
        discountpriceEt.setError("");
        discountNoteEt.setError("");
        productIconIv.setImageResource(R.drawable.ic_shop);
        image_uri = null;
    }

    private void categoryDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thể loại sản phẩm")
                .setItems(Constants.productCategory, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //get picked category
                        String category = Constants.productCategory[which];
                        //set picked category
                        categoryTv.setText(category);
                    }
                })
                .show();
    }
    //danh mục chọn hình ảnh
    private void showImagePickDialog() {
        //options to display in dialog
        //các tùy chọn để hiển thị trong hộp thoại
        String[] options = {"Máy ảnh", "Bộ sưu tập hoặc Kho ảnh"};//camara, gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(image_uri!=null&& cameraAccepted && storageAccepted){
                        //permission allowed
                        //sự cho phép được phép cấp quyền
                        pickFromCamera();

                    }
                    else {
                        //permission denied
                        //sự cho phép không được phép cấp quyền
                        //location permission is necessary
                        Toast.makeText(this,"Sự cho phép máy ảnh là cần thiết.....",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0)
                {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted && image_uri!=null){
                        //permission allowed
                        //sự cho phép được phép cấp quyền
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        //sự cho phép không được phép cấp quyền
                        //location permission is necessary
                        Toast.makeText(this,"Sự cho phép bộ nhớ là cần thiết.....",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                image_uri = data.getData();
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toast.makeText(this, "Đã chọn hình thành công!", Toast.LENGTH_SHORT).show();
                //set to imageview
                productIconIv.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview

                productIconIv.setImageURI(image_uri);
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toast.makeText(this, "Đã chụp hình thành công!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}