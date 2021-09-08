package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;


import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import com.example.grapfood.BuildConfig;
import com.example.grapfood.R;


import com.example.grapfood.activity.chefFoodPanel.ProfileEditChef;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.DescriptorProtos;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class ChefRegistration extends AppCompatActivity implements LocationListener {

    final String TAG = "GPS";
    //permission constants
    //hằng số quyền
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    //hằng số chọn hình ảnh
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //khởi tạo dành cho GPS
    //định vị
    //chọn khoang vùng
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    // location last updated time
    private String mLastUpdateTime;
    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 600;


    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    //permission arrays
    //mảng quyền
    String[] locationPermission;
    String[] cameraPermission;
    String[] storagePermission;
    //image picked uri
    //chọn hình ảnh

    private Uri image_uri;
    //hình ảnh đã chọn
    double latitude = 0.0, longitude = 0.0;

    LocationManager locationManager;
    //check no internet
    //kiểm tra có mạng hay không
    private Boolean wifiConnected = false;
    private Boolean mobileConnected = false;

    ImageButton gpsBTN;
    CircularImageView profileIv;
    ImageButton btnBN;
    TextInputLayout Fname, Lname, NameShop, Email, Pass,
            cpass, mobileno, houseno, area,
            state1, city1, compleaddress, delivery;
    Button signup, Emaill, Phone;
    CountryCodePicker Cpp;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    FirebaseAuth FAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    String fname, lname, emailid, password, confpassword,
            mobile, house, Area, statee, cpAddress,
            cityy, nameshop, ImageURL, Delivery;
    EditText FirstnameEt,EmailEt,MobilenoEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_registration);

        //chọn các vùng đã khởi tạo trong layout
        Fname = (TextInputLayout) findViewById(R.id.Firstname);
        FirstnameEt =  findViewById(R.id.FirstnameEt);
        Lname = (TextInputLayout) findViewById(R.id.Lastname);
        NameShop = (TextInputLayout) findViewById(R.id.NameShop);
        Email = (TextInputLayout) findViewById(R.id.Email);
        EmailEt =  findViewById(R.id.EmailEt);
        Pass = (TextInputLayout) findViewById(R.id.Pwd);
        cpass = (TextInputLayout) findViewById(R.id.Cpass);
        mobileno = (TextInputLayout) findViewById(R.id.Mobileno);
        MobilenoEt =  findViewById(R.id.MobilenoEt);
        houseno = (TextInputLayout) findViewById(R.id.houseNo);
        state1 = (TextInputLayout) findViewById(R.id.States);
        city1 = (TextInputLayout) findViewById(R.id.Citys);
        compleaddress = (TextInputLayout) findViewById(R.id.completeAddress);
        area = (TextInputLayout) findViewById(R.id.Area);
        delivery = (TextInputLayout) findViewById(R.id.deliveryfree);
        gpsBTN = (ImageButton) findViewById(R.id.gpsBtn);


        profileIv = (CircularImageView) findViewById(R.id.profileIv);

        signup = (Button) findViewById(R.id.Signup);
        Emaill = (Button) findViewById(R.id.email);
        Phone = (Button) findViewById(R.id.phone);
        // nút sổ xuống chọn sdt
        Cpp = (CountryCodePicker) findViewById(R.id.CountryCode);

        btnBN = (ImageButton) findViewById(R.id.backBN);
        //init permissions array
        //mảng quyền nhập vào
        //dành cho vị trí
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //dành cho truy cập camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //dành cho truy cập firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();




        //mouse click event
        //sự kiện click chuột
        btnBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //trả về phía trước đó
                onBackPressed();
            }
        });


        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                //chọn hình ảnh
                showImagePickDialog();

            }
        });


        ButterKnife.bind(this);
        initLocation();
        restoreValuesFromBundle(savedInstanceState);


        FAuth = FirebaseAuth.getInstance();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    fname = Fname.getEditText().getText().toString().trim();
                    lname = Lname.getEditText().getText().toString().trim();
                    nameshop = NameShop.getEditText().getText().toString().trim();
                    emailid = Email.getEditText().getText().toString().trim();
                    mobile = mobileno.getEditText().getText().toString().trim();
                    password = Pass.getEditText().getText().toString().trim();
                    confpassword = cpass.getEditText().getText().toString().trim();
                    Area = area.getEditText().getText().toString().trim();
                    house = houseno.getEditText().getText().toString().trim();
                    statee = state1.getEditText().getText().toString().trim();
                    cityy = city1.getEditText().getText().toString().trim();
                    cpAddress = compleaddress.getEditText().getText().toString().trim();
                    ImageURL = String.valueOf(image_uri).trim();
                    Delivery = delivery.getEditText().getText().toString().trim();
                    String timestamp = "" + System.currentTimeMillis();
                    DatabaseReference DanhMuc = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DanhMuc");

                    //cập nhật ảnh lên firebase storage nếu hình ảnh là null
                    if (isValid() && image_uri == null ) {
                        try {
                            if(!init(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())){
                                return;
                            }
                        }catch (Exception e){
                            Toasty.warning(ChefRegistration.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ...", Toast.LENGTH_SHORT, true).show();
                        }

                        final ProgressDialog mDialog = new ProgressDialog(ChefRegistration.this);
                        mDialog.setCancelable(false);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setTitle("\uD83E\uDD84 Tình hình mạng yếu");
                        mDialog.setMessage("Đang đăng ký và tải hình đại diện lên, vui lòng đợi tí......");
                        mDialog.setIndeterminate(false);
                        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mDialog.setProgress(0);
                        mDialog.show();

                        FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    try {
                                        latitude = mCurrentLocation.getLatitude();
                                        longitude = mCurrentLocation.getLongitude();
                                        HashMap<String, String> hashMap1 = new HashMap<>();
                                        hashMap1.put("UID", "" + FAuth.getUid());
                                        hashMap1.put("MobileNo", mobile);
                                        hashMap1.put("DeliveryFree", Delivery);
                                        hashMap1.put("FirstName", fname);
                                        hashMap1.put("LastName", lname);
                                        hashMap1.put("NameShop", nameshop);
                                        hashMap1.put("EmailId", emailid);
                                        hashMap1.put("City", cityy);
                                        hashMap1.put("Area", Area);//phường xã
                                        hashMap1.put("Password", password);
                                        hashMap1.put("CompleteAddress", cpAddress);
                                        hashMap1.put("State", statee);//Quận huyện
                                        hashMap1.put("ConfirmPassword", confpassword);
                                        hashMap1.put("House", house);//Số đường
                                        hashMap1.put("ImageURL", "");
                                        hashMap1.put("Latitude", "" + latitude);
                                        hashMap1.put("Longitude", "" + longitude);
                                        hashMap1.put("Timestamp", "" + timestamp);//+DateFormat.getTimeInstance().format(new Date())
                                        hashMap1.put("Online", "true");
                                        hashMap1.put("ShopOpen", "true");
                                        hashMap1.put("AccountType", "Chef");

                                        databaseReference
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDialog.dismiss();

                                                FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(ChefRegistration.this);
                                                            builder.setMessage("\uD83E\uDD84 Bạn đã đăng ký! Đảm bảo xác minh Email của bạn trong hòm thư");
                                                            builder.setCancelable(false);
                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                    String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                                    Intent b = new Intent(ChefRegistration.this, ChefVerifyPhone.class);
                                                                    b.putExtra("phonenumber", phonenumber);
                                                                    startActivity(b);
                                                                }
                                                            });
                                                            AlertDialog Alert = builder.create();
                                                            Alert.show();
                                                        } else {
                                                            mDialog.dismiss();
                                                            Toasty.error(ChefRegistration.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                            ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                        DanhMuc.child("-MiiJTfsEPcgklkpm4RV").child("mName").setValue("Bún/Phở");
                                        DanhMuc.child("-MiiJiqPT4X3kuXq00qa").child("mName").setValue("Ăn Vặt");
                                        DanhMuc.child("-MiiJrcM8xTVUua1rnII").child("mName").setValue("Đồ Uống");
                                        DanhMuc.child("-MiiK-b2YhzfxzfpUDyb").child("mName").setValue("Sức Khỏe");
                                        DanhMuc.child("-MiiK8fXXkqhjsMloVw0").child("mName").setValue("Cơm");
                                        DanhMuc.child("-MiiKJdrYPfuHnsYDfLU").child("mName").setValue("Đặc Sản");
                                        DanhMuc.child("-MiiKl8Xr0GD4N4zeSEd").child("mName").setValue("Thức ăn nhanh");

                                    }catch (Exception e){
                                        Erro();
                                        Toasty.warning(ChefRegistration.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                                        mDialog.dismiss();
                                    }
                                } else{

                                    mDialog.dismiss();
                                    Toasty.error(ChefRegistration.this, "" + task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                    ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                                }
                            }
                        });
                    }
                    //cập nhật ảnh lên firebase storage nếu hình ảnh không bị null
                    else if (isValid() && image_uri != null ) {

                        if(!init(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude())){
                            return;
                        }

                        String filePathAndName = "profile_image/" + "" + FAuth.getUid();

                        final ProgressDialog progressDialog = new ProgressDialog(ChefRegistration.this);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.setIndeterminate(false);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        progressDialog.setProgress(0);
                        progressDialog.setTitle("\uD83E\uDD84 Tình hình mạng yếu");
                        progressDialog.setMessage("Đang đăng ký và tải hình ảnh đại diện lên, vui lòng đợi tí....");
                        progressDialog.show();

                        //update image
                        ref = FirebaseStorage.getInstance().getReference(filePathAndName);
                        ref.putFile(image_uri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //get url of uploated image
                                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                        while (!uriTask.isSuccessful()) ;
                                        Uri downloadImageUri = uriTask.getResult();

                                        if (uriTask.isSuccessful()) {

                                            FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()) {
                                                        try{
                                                            HashMap<String, String> hashMap1 = new HashMap<>();
                                                            hashMap1.put("UID", "" + FAuth.getUid());
                                                            hashMap1.put("MobileNo", mobile);
                                                            hashMap1.put("DeliveryFree", Delivery);
                                                            hashMap1.put("FirstName", fname);
                                                            hashMap1.put("LastName", lname);
                                                            hashMap1.put("NameShop", nameshop);
                                                            hashMap1.put("EmailId", emailid);
                                                            hashMap1.put("City", cityy);
                                                            hashMap1.put("Area", Area);//phường xã
                                                            hashMap1.put("Password", password);
                                                            hashMap1.put("CompleteAddress", cpAddress);
                                                            hashMap1.put("State", statee);//Quận huyện
                                                            hashMap1.put("ConfirmPassword", confpassword);
                                                            hashMap1.put("House", house);//Số đường
                                                            hashMap1.put("ImageURL", "" + downloadImageUri);//url of uploadted image
                                                            hashMap1.put("Latitude", "" + mCurrentLocation.getLatitude());
                                                            hashMap1.put("Longitude", "" + mCurrentLocation.getLongitude());
                                                            hashMap1.put("Timestamp", "" + timestamp);//+" "+DateFormat.getTimeInstance().format(new Date())
                                                            hashMap1.put("Online", "true");
                                                            hashMap1.put("ShopOpen", "true");
                                                            hashMap1.put("AccountType","Chef");

                                                            databaseReference
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressDialog.dismiss();

                                                                    FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

//                                                                                Context context = new ContextThemeWrapper(ChefRegistration.this, R.style.AppTheme2);
//                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.MaterialAlertDialog_rounded);

                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(ChefRegistration.this);
                                                                                builder.setMessage("\uD83E\uDD84 Bạn đã đăng ký! Đảm bảo xác minh email của bạn");
                                                                                builder.setCancelable(false);
                                                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                                        dialog.dismiss();
                                                                                        String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                                                        Intent b = new Intent(ChefRegistration.this, ChefVerifyPhone.class);
                                                                                        b.putExtra("phonenumber", phonenumber);
                                                                                        startActivity(b);
                                                                                    }
                                                                                });
                                                                                AlertDialog Alert = builder.create();
                                                                                Alert.show();
                                                                            } else {
                                                                                progressDialog.dismiss();
                                                                                Toasty.error(ChefRegistration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                                                ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            });
                                                            DanhMuc.child("-MiiJTfsEPcgklkpm4RV").child("mName").setValue("Bún/Phở");
                                                            DanhMuc.child("-MiiJiqPT4X3kuXq00qa").child("mName").setValue("Ăn Vặt");
                                                            DanhMuc.child("-MiiJrcM8xTVUua1rnII").child("mName").setValue("Đồ Uống");
                                                            DanhMuc.child("-MiiK-b2YhzfxzfpUDyb").child("mName").setValue("Sức Khỏe");
                                                            DanhMuc.child("-MiiK8fXXkqhjsMloVw0").child("mName").setValue("Cơm");
                                                            DanhMuc.child("-MiiKJdrYPfuHnsYDfLU").child("mName").setValue("Đặc Sản");
                                                            DanhMuc.child("-MiiKl8Xr0GD4N4zeSEd").child("mName").setValue("Thức ăn nhanh");
                                                        }catch (Exception e){
                                                            Erro();
                                                            Toasty.warning(ChefRegistration.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                                                            progressDialog.dismiss();
                                                        }

                                                    } else {

                                                        progressDialog.dismiss();
                                                        Toasty.error(ChefRegistration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                        ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                                                    }
                                                }
                                            });
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toasty.error(ChefRegistration.this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
//                                    Toast.makeText(ChefRegistration.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage("Đang tải " + (int) progress + "%");
                                        progressDialog.setCanceledOnTouchOutside(false);
                                    }
                                });
                    } else {
                        Toasty.warning(ChefRegistration.this, "Quá trình đăng ký đã thất bại!\nCó thể email đã được đăng ký!\nHoặc bạn đã điền sai thông tin\nVui lòng kiểm tra lại kết nối mạng và thông tin bên trên .", Toast.LENGTH_SHORT, true).show();
                    }
                }catch (Exception e){
                    Erro();
                    Toasty.warning(ChefRegistration.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                }
            }
        });
        Emaill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChefRegistration.this, Login.class));
                finish();
            }
        });
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChefRegistration.this, Loginphone.class));
                finish();
            }
        });
        isOnline();

    }


    private void initLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                updateLocationUI();
            }
        };
        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }
        /**
         * Update the UI displaying the location data
         * and toggling the buttons
         */
    private void updateLocationUI() {

            Log.d(TAG, "updateUI");

            if (mCurrentLocation != null) {
                Log.d("latitude", "latitude--" + mCurrentLocation.getLatitude());
                Log.d("longitude", "longitude--" + mCurrentLocation.getLongitude());
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());
                try {

                        addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);
                        if (addresses != null && addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0);

                            String city = addresses.get(0).getAdminArea();
                            String knownName = addresses.get(0).getFeatureName();
                            String state = addresses.get(0).getSubAdminArea();
                            Log.d(TAG, "getAddress:  thành phố" + " " + city);
                            Log.d(TAG, "getAddress:  toàn địa chỉ" + " " + address);

                            Log.d(TAG, "getAddress:  quận" + " " + state);

                            Log.d(TAG, "getAddress:  số nhà" + " " + knownName);

                            //set addresses
                            //đặt dịa chỉ
                            state1.getEditText().setText(state);
                            city1.getEditText().setText(city);
                            houseno.getEditText().setText(knownName);

                            compleaddress.getEditText().setText(address);
                        }
                } catch (Exception e) {
                    Toasty.error(this, "Hiện tại không truy cập được vị trí của bạn"+"\n" + e.getMessage(), Toast.LENGTH_SHORT, true).show();

                }
            }

        }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        //All location settings are satisfied.
                        Log.i(TAG, "Tất cả các cài đặt vị trí đều hài lòng.");
                        //Started location updates!
                        Toasty.warning(getApplicationContext(), "Đã bắt đầu cập nhật vị trí hiện tại của bạn!\nVui lòng tự điền tên đường\n Kiểm tra lại xem đã đúng đại chỉ chưa", Toast.LENGTH_LONG, true).show();

//                        Toast.makeText(getApplicationContext(), "Đã bắt đầu cập nhật vị trí hiện tại của bạn!\nVui lòng tự điền tên đường\n Kiểm tra lại xem đã đúng đại chỉ chưa", Toast.LENGTH_LONG).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                //Location settings are not satisfied. Attempting to upgrade
                                Log.i(TAG, "Cài đặt vị trí không hài lòng. Đang cố gắng nâng cấp " +
                                        "Cài đặt vị trí\n ");//location settings
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(ChefRegistration.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    //PendingIntent unable to execute request.
                                    Log.i(TAG, "PendingIntent không thể thực hiện yêu cầu.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toasty.error(ChefRegistration.this, ""+errorMessage+"\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();

//                                Toast.makeText(ChefRegistration.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        updateLocationUI();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);

    }

    @OnClick(R.id.gpsBtn)
    public void startLocationButtonClick() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
//                            openSettings();
                            showSettingsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }

        updateLocationUI();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChefRegistration.this);
        alertDialog.setTitle("GPS không được kích hoạt!");
        alertDialog.setMessage("Bạn có muốn bật GPS không?");
        alertDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Có thể nhận được vị trí");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS đã được bật");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null){
                            updateUI(loc);
                        }
                    }
                    else{
                        Toasty.warning(ChefRegistration.this, "Vui lòng nhấn nút GPS của bên phải chữ ĐĂNG KÝ .... ", Toast.LENGTH_LONG, true).show();
                        Erro();
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER đã được bật");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) {
                            updateUI(loc);
                        }
                    }else {
                        Toasty.warning(ChefRegistration.this, "Vui lòng nhấn nút GPS của bên phải chữ ĐĂNG KÝ .... ", Toast.LENGTH_LONG, true).show();
                        Erro();
                    }
                } else {
                    Toasty.warning(ChefRegistration.this, "Vui lòng nhấn nút GPS của bên phải chữ ĐĂNG KÝ .... ", Toast.LENGTH_LONG, true).show();
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);

                }
            } else {
                Log.d(TAG, "Không thể nhận được vị trí");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

//    private void getLastLocation() {
//        try {
//            Criteria criteria = new Criteria();
//            String provider = locationManager.getBestProvider(criteria, false);
//            Location location = locationManager.getLastKnownLocation(provider);
//            Log.d(TAG, provider);
//            Log.d(TAG, location == null ? "KHÔNG có vị trí cuối cùng" : location.toString());
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }

    @NotNull
    private ArrayList findUnAskedPermissions(@NotNull ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void Erro() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Ơ kìa lỗi");
        alerta.setMessage("Vui lòng nhấn nút màu đỏ GPS bên phải chữ Đăng Ký");
        alerta.setPositiveButton("OK", null);
        alerta.show();
    }

    private void updateUI(@NotNull Location location) {
        if(location == null)
        {
            Erro();
        }
        else {
            Log.d(TAG, "updateUI");

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.d("latitude", "latitude--" + latitude);
            Log.d("longitude", "longitude--" + longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                if (latitude == 0 && longitude == 0) {
                    Toasty.warning(ChefRegistration.this, "Vui lòng nhấn nút GPS của bên phải chữ ĐĂNG KÝ .... ", Toast.LENGTH_LONG, true).show();
                } else {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                    if (addresses != null && addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        String city = addresses.get(0).getLocality();
                        String state = addresses.get(0).getAdminArea();
                        String country = addresses.get(0).getCountryName();
                        String postalCode = addresses.get(0).getPostalCode();
                        String knownName = addresses.get(0).getFeatureName();
                        Log.d(TAG, "getAddress:  address" + address);
                        Log.d(TAG, "getAddress:  city" + city);
                        Log.d(TAG, "getAddress:  state" + state);
                        Log.d(TAG, "getAddress:  postalCode" + postalCode);
                        Log.d(TAG, "getAddress:  knownName" + knownName);

                        //set addresses
                        //đặt dịa chỉ
                        compleaddress.getEditText().setText(address + " " + city + " " + state);
                    }
                }
            } catch (Exception e) {
                Toasty.error(this, ""+ e.getMessage(), Toast.LENGTH_SHORT, true).show();
            }

        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ChefRegistration.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

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

    String emailpattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public boolean isValid() {
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        NameShop.setErrorEnabled(false);
        NameShop.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        area.setErrorEnabled(false);
        area.setError("");
        city1.setErrorEnabled(false);
        city1.setError("");
        state1.setErrorEnabled(false);
        state1.setError("");
        houseno.setErrorEnabled(false);
        houseno.setError("");
        compleaddress.setErrorEnabled(false);
        compleaddress.setError("");
//        pincode.setErrorEnabled(false);
//        pincode.setError("");

        boolean isValid = false, isValidhouseno = false, isValidlname = false,
                isValidname = false, isValidnameshop = false,
                isValidemail = false, isValidpassword = false,
                isValidconfpassword = false, isValidmobilenum = false,
                isValidarea = false, isValidcity = false,
                isValidstate = false, isValidcompleteAd = false;
        if (TextUtils.isEmpty(fname)) {
            Fname.setErrorEnabled(true);
            Fname.setError("Bạn chưa nhập họ");
        } else {
            isValidname = true;
        }
        if (TextUtils.isEmpty(lname)) {
            Lname.setErrorEnabled(true);
            Lname.setError("Bạn chưa nhập tên");
        } else {
            isValidlname = true;
        }
        if (TextUtils.isEmpty(nameshop)) {
            NameShop.setErrorEnabled(true);
            NameShop.setError("Bạn chưa nhập tên cửa hàng");
        } else {
            isValidnameshop = true;
        }
        if (TextUtils.isEmpty(emailid)) {
            Email.setErrorEnabled(true);
            Email.setError("Email là cần thiết");
        } else {
            if (emailid.matches(emailpattern)) {
                isValidemail = true;
            } else {
                Email.setErrorEnabled(true);
                Email.setError("Nhập vào email hợp lệ");
            }
        }
        if (TextUtils.isEmpty(password)) {
            Pass.setErrorEnabled(true);
            Pass.setError("Nhập vào mật khẩu");
        } else {
            if (password.length() < 8) {
                Pass.setErrorEnabled(true);
                Pass.setError("Mật khẩu yếu");
            } else {
                isValidpassword = true;
            }
        }
        if (TextUtils.isEmpty(confpassword)) {
            cpass.setErrorEnabled(true);
            cpass.setError("Nhập lại mật khẩu");
        } else {
            if (!password.equals(confpassword)) {
                cpass.setErrorEnabled(true);
                cpass.setError("Mật khẩu không khớp");
            } else {
                isValidconfpassword = true;
            }
        }
        if (TextUtils.isEmpty(mobile)) {
            mobileno.setErrorEnabled(true);
            mobileno.setError("Số điện thoại cần thiết");
        } else {
            if (mobile.length() < 10) {
                mobileno.setErrorEnabled(true);
                mobileno.setError("Nhập lại số điện thoại");
            } else {
                isValidmobilenum = true;
            }
        }
        if (TextUtils.isEmpty(Area)) {
            area.setErrorEnabled(true);
            area.setError("Tên đường không được để trống");
        } else {
            isValidarea = true;
        }

        if (TextUtils.isEmpty(house)) {
            houseno.setErrorEnabled(true);
            houseno.setError("Địa chỉ không được để trống");
        } else {
            isValidhouseno = true;
        }
        if (TextUtils.isEmpty(cityy)) {
            city1.setErrorEnabled(true);
            city1.setError("Bạn chưa nhập thành phố đang ở");
        } else {
            isValidcity = true;
        }
        if (TextUtils.isEmpty(statee)) {
            state1.setErrorEnabled(true);
            state1.setError("Bạn chưa nhập quận/ huyện đang ở");
        } else {
            isValidstate = true;
        }
        if (TextUtils.isEmpty(cpAddress)) {
            compleaddress.setErrorEnabled(true);
            compleaddress.setError("Bạn chưa nhập hoàn chỉnh địa chỉ");
        } else {
            isValidcompleteAd = true;
        }

        isValid = (isValidcompleteAd && isValidarea && isValidcity && isValidstate && isValidconfpassword && isValidpassword && isValidemail && isValidmobilenum && isValidname && isValidhouseno && isValidlname && isValidnameshop) ? true : false;
        return isValid;


    }
    public static double unboxed(Double v) {
        return v == null ? 0 : v.doubleValue();
    }
    private boolean init(double getLatitude,double getLongitude)
    {
        boolean isValid=false,isValidLocation = true;
        double nullString = Double.parseDouble("null");
        if(mCurrentLocation.getLatitude() == nullString || mCurrentLocation.getLongitude()  == nullString)
        {
            Toasty.warning(this, "Vui lòng nhấn vào nút GPS góc phải bên trên ...", Toast.LENGTH_SHORT, true).show();

        }
        else{
            isValidLocation = false;
        }
        isValid = isValidLocation ? true : false;
        return isValid;
    }

    // hàm kiểm tra quyền địa chỉ

    //tình trạng kết nối wifi để dùng app
    private void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            wifiConnected = ni.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
//                Toasty.success(this, "Bạn đã kết nối thành công đến từ wifi!", Toast.LENGTH_SHORT, true).show();

            } else if (mobileConnected) {
//                Toasty.success(this, "Bạn đã kết nối thành công đến từ mạng dữ liệu di động của điện thoại!", Toast.LENGTH_SHORT, true).show();

            }
        } else {
            Toasty.error(this, "Hiện tại bạn không có kết nối mạng.\nVui lòng mở wifi và dữ liệu di động.<!>", Toast.LENGTH_SHORT, true).show();

            showSettingsWifis();
        }
    }

    //mở cài đặt đến phần wifi
    public void showSettingsWifis() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ChefRegistration.this);
        alertDialog.setTitle("Cài đặt");
        alertDialog.setMessage("Bật wifi để dùng app! Chuyển đến menu cài đặt?");//Enable Location Provider! Go to settings menu?
        alertDialog.setPositiveButton("Đi tới cài đặt",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        ChefRegistration.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Huỷ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
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


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        //vị trí được phát hiện

        Log.d(TAG, "onLocationChanged");
        updateUI(location);

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("TAG", "onStatusChanged: " + provider);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disabled
        //gps / vị trí bị vô hiệu hóa
        //please turn on location
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        Toasty.warning(this, "Vui lòng mở vị trí....", Toast.LENGTH_SHORT, true).show();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:{
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("Các quyền này là bắt buộc đối với ứng dụng. Vui lòng cho phép truy cập.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsRejected.toArray(
                                                new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } else {
                    //No rejected permissions.
                    Log.d(TAG, "Không có quyền bị từ chối.");
                    canGetLocation = true;
                    getLocation();
                }
            }break;
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
                        Toasty.success(this, "Sự cho phép máy ảnh là cần thiết.....!", Toast.LENGTH_SHORT, true).show();

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
                        Toasty.success(this, "Sự cho phép bộ nhớ là cần thiết.....!", Toast.LENGTH_SHORT, true).show();

//                        Toast.makeText(this,"Sự cho phép bộ nhớ là cần thiết.....",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                //get picked image
                image_uri = data.getData();
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toasty.success(this, "Đã chọn hình thành công!!", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(this, "Đã chọn hình thành công!", Toast.LENGTH_SHORT).show();
                //set to imageview
                profileIv.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview

                profileIv.setImageURI(image_uri);
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toasty.success(this, "Đã chụp hình thành công!!", Toast.LENGTH_SHORT, true).show();
//                Toast.makeText(this, "Đã chụp hình thành công!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates();
        }

        updateLocationUI();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates

        }
    }

}