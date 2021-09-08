package com.example.grapfood.activity.deliveryFoodPanel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.bottomnavigation.CustomerFoofPanel_BottomNavigation;
import com.example.grapfood.activity.bottomnavigation.DeliveryFoodPanel_BottomNavigation;
import com.example.grapfood.activity.chefFoodPanel.ProfileEditChef;
import com.example.grapfood.activity.customerFoodPanel.ProfileEditCus;
import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class ProfileEditDelivery extends AppCompatActivity implements LocationListener {

    private ImageButton backBtn;
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
    LocationManager locationManager;
    //permission arrays
    //mảng quyền
    String[] locationPermission;

    String[] cameraPermission;
    String[] storagePermission;
    double latitude = 0.0, longitude = 0.0;
    private Uri image_uri;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseAuth firebaseAuth;
    CircularImageView profileIv;
    EditText fistname,lastname,email,mobino,houseno,area,state,city,completeAd;
    String fn,ln,nphone,Email,Houseno,Area,State,City,CompleteAd;
    Button btnsua;
    CountryCodePicker Cpp;
    //check no internet
    //kiểm tra có mạng hay không
    private Boolean wifiConnected = false;
    private Boolean mobileConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit_delivery);

        backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileEditDelivery.this, DeliveryFoodPanel_BottomNavigation.class));
                finish();
            }
        });

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
        profileIv = (CircularImageView) findViewById(R.id.profileIv);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //dành cho truy cập firebase
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                //chọn hình ảnh
                showImagePickDialog();

            }
        });

        fistname = (EditText)findViewById(R.id.Firstname);
        lastname = (EditText)findViewById(R.id.Lastname);
        email = (EditText) findViewById(R.id.Email);
        mobino=(EditText) findViewById(R.id.Mobileno);
        Cpp = (CountryCodePicker) findViewById(R.id.CountryCode);
        houseno = (EditText) findViewById(R.id.houseNo);
        area =(EditText) findViewById(R.id.edArea);
        state = (EditText) findViewById(R.id.edStates);
        city = (EditText) findViewById(R.id.edCitys);
        completeAd = (EditText) findViewById(R.id.edcompleteAddress);
        Toast.makeText(this, "Vui lòng nhấn nút trên chữ GPS", Toast.LENGTH_LONG).show();
        btnsua = (Button) findViewById(R.id.bt_thaydoithongtin);
        //truy cập thông tin
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        btnsua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin update profile
                inputData();
            }
        });
        ButterKnife.bind(this);
        initLocation();
        restoreValuesFromBundle(savedInstanceState);
        isOnline();
    }
    //viết sự kiện cho cập nhật lai hình ảnh hiện tại
    private void inputData() {
        //input data
        fn = fistname.getText().toString().trim();
        ln = lastname.getText().toString().trim();

        nphone = mobino.getText().toString().trim();
        Email = email.getText().toString().trim();

        Houseno = houseno.getText().toString().trim();
        Area = area.getText().toString().trim();
        State = state.getText().toString().trim();
        City = city.getText().toString().trim();
        CompleteAd = completeAd.getText().toString().trim();



        updateProfile();


    }

    private void Erro() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("Ơ kìa lỗi");
        alerta.setMessage("Vui lòng nhấn nút màu đỏ GPS bên phải chữ Đăng Ký");
        alerta.setPositiveButton("OK", null);
        alerta.show();
    }

    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileEditDelivery.this);
        progressDialog.setMessage("Đang cập nhật lại cho bạn...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(image_uri == null ){
            try {
                //update without image

                //setup date to update
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("MobileNo",""+nphone);
                hashMap.put("FirstName",""+fn);
                hashMap.put("LastName",""+ln);
                hashMap.put("EmailId",""+Email);
                hashMap.put("City",""+City);
                hashMap.put("Area",""+Area);
                hashMap.put("State",""+State);
                hashMap.put("House",""+Houseno);
                hashMap.put("CompleteAddress",""+CompleteAd);
                hashMap.put("Latitude", "" + mCurrentLocation.getLatitude());
                hashMap.put("Longitude", "" + mCurrentLocation.getLongitude());


                //update to db
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chef");
                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //update
                                progressDialog.dismiss();
                                Toast.makeText(ProfileEditDelivery.this, "Cập nhật thành công y...", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed to update
                                Toast.makeText(ProfileEditDelivery.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }catch (Exception e){
                Erro();
                Toasty.warning(ProfileEditDelivery.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                progressDialog.dismiss();
            }

        }
        else{
            try {
                //update with image

                //*******Upload image first*************/
                String filePathAndName = "profile_image/" + "" + firebaseAuth.getUid();
                //get storage reference
                StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
                storageReference.putFile(image_uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                //image uploaded get url of uploaded image
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());
                                Uri downloadImageUri = uriTask.getResult();

                                if(uriTask.isSuccessful()){
                                    //image url received,now update db
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("MobileNo",""+nphone);
                                    hashMap.put("FirstName",""+fn);
                                    hashMap.put("LastName",""+ln);
                                    hashMap.put("EmailId",""+Email);
                                    hashMap.put("City",""+City);
                                    hashMap.put("Area",""+Area);
                                    hashMap.put("State",""+State);
                                    hashMap.put("House",""+Houseno);
                                    hashMap.put("CompleteAddress",""+CompleteAd);
                                    hashMap.put("Latitude", "" + mCurrentLocation.getLatitude());
                                    hashMap.put("Longitude", "" + mCurrentLocation.getLongitude());
                                    hashMap.put("ImageURL",""+downloadImageUri);
                                    //update to db
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chef");
                                    ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //update
                                                    progressDialog.dismiss();
                                                    Toast.makeText(ProfileEditDelivery.this, "Cập nhật thành công y...", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //failed to update
                                                    Toast.makeText(ProfileEditDelivery.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileEditDelivery.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }catch (Exception e){
                Erro();
                Toasty.warning(ProfileEditDelivery.this, "Vui lòng nhấn vào nút GPS góc phải bên trên ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                progressDialog.dismiss();
            }

        }
    }
    //viết sự kiện cho load hình ảnh của user
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(getApplicationContext(), MainMenu.class));
            finish();
        }
        else{
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //load user info, and set to views
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chef");
        ref.orderByChild("UID").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String FirstName = ""+ds.child("FirstName").getValue();
                            String LastName = ""+ds.child("LastName").getValue();
                            String MobileNo = ""+ds.child("MobileNo").getValue();
                            String EmailId = ""+ds.child("EmailId").getValue();
                            String Area = ""+ds.child("Area").getValue();
                            String State = ""+ds.child("State").getValue();
                            String House = ""+ds.child("House").getValue();
                            String City = ""+ds.child("City").getValue();
                            String ImageURL = ""+ds.child("ImageURL").getValue();
                            latitude = Double.parseDouble(""+ds.child("Latitude").getValue());
                            longitude = Double.parseDouble(""+ds.child("Longitude").getValue());
                            String Timestamp = ""+ds.child("Timestamp").getValue();
                            String Online = ""+ds.child("Online").getValue();
                            String CAdress = ""+ds.child("CompleteAddress").getValue();


                            fistname.setText(FirstName);
                            lastname.setText(LastName);

                            email.setText(EmailId);
                            mobino.setText(MobileNo);

                            area.setText(Area);
                            state.setText(State);
                            houseno.setText(House);
                            city.setText(City);
                            completeAd.setText(CAdress);

                            try {
                                Picasso.get().load(ImageURL).placeholder(R.drawable.ic_camera_24).into(profileIv);
                            }catch (Exception e ){
                                profileIv.setImageResource(R.drawable.ic_camera_24);
                                Toast.makeText(ProfileEditDelivery.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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

    //danh mục chọn vị trí hiện tại

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

                    String city1 = addresses.get(0).getAdminArea();
                    String knownName = addresses.get(0).getFeatureName();
                    String state1 = addresses.get(0).getSubAdminArea();
                    Log.d(TAG, "getAddress:  thành phố" +" "+ city);
                    Log.d(TAG, "getAddress:  toàn địa chỉ" +" "+ address);

                    Log.d(TAG, "getAddress:  quận"+ " "+ state);

                    Log.d(TAG, "getAddress:  số nhà"+ " "+ knownName);

                    //set addresses
                    //đặt dịa chỉ
                    state.setText(state1);
                    city.setText(city1);
                    houseno.setText(knownName);

                    completeAd.setText(address);
                }
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getApplicationContext(), "Đã bắt đầu cập nhật vị trí hiện tại của bạn!\nVui lòng tự điền tên đường\n Kiểm tra lại xem đã đúng đại chỉ chưa", Toast.LENGTH_LONG).show();

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
                                    rae.startResolutionForResult(ProfileEditDelivery.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    //PendingIntent unable to execute request.
                                    Log.i(TAG, "PendingIntent không thể thực hiện yêu cầu.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(ProfileEditDelivery.this, errorMessage, Toast.LENGTH_LONG).show();
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

    public void showSettingsAlert() {
//        Context context = new ContextThemeWrapper(ProfileEditDelivery.this, R.style.AppTheme2);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,R.style.MaterialAlertDialog_rounded);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileEditDelivery.this);
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
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,  this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null){
                            updateUI(loc);
                        }
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER đã được bật");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES,  this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) {
                            updateUI(loc);
                        }
                    }
                } else {
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

    private void updateUI(@NotNull Location location) {
        Log.d(TAG, "updateUI");
//        tvLatitude.setText(Double.toString(loc.getLatitude()));
//        tvLongitude.setText(Double.toString(loc.getLongitude()));
//        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        findAddress();
        Log.d("latitude", "latitude--" + latitude);
        Log.d("longitude", "longitude--" + longitude);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
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
                //            state1.getEditText().setText(state);
                //            city1.getEditText().setText(city);
                //            area.getEditText().setText(knownName);
                //            houseno.getEditText().setText(postalCode);
                //            delivery.getEditText().setText(country);
                //            compleaddress.getEditText().setText(address);
                completeAd.setText(address + " " + city + " " + state + " " + country);
            }
//
//
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
//        Context context = new ContextThemeWrapper(ProfileEditDelivery.this, R.style.AppTheme2);
//        new AlertDialog.Builder(context,R.style.MaterialAlertDialog_rounded)

        new AlertDialog.Builder(ProfileEditDelivery.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    String emailpattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    private void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            wifiConnected = ni.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Toast.makeText(this, "Bạn đã kết nối thành công đến wifi", Toast.LENGTH_SHORT).show();
            } else if (mobileConnected) {
                Toast.makeText(this, "Bạn đã kết nối thành công đến điện thoại", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Hiện tại bạn không có kết nối", Toast.LENGTH_SHORT).show();
            showSettingsWifis();
        }
    }

    //mở cài đặt đến phần wifi

    public void showSettingsWifis() {
//        Context context = new ContextThemeWrapper(ProfileEditDelivery.this, R.style.AppTheme2);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//                context,R.style.MaterialAlertDialog_rounded);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ProfileEditDelivery.this);
        alertDialog.setTitle("Cài đặt");
        alertDialog.setMessage("Bật wifi để dùng app! Chuyển đến menu cài đặt?");//Enable Location Provider! Go to settings menu?
        alertDialog.setPositiveButton("Đi tới cài đặt",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        ProfileEditDelivery.this.startActivity(intent);
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
//                if(grantResults.length > 0)
//                {
//                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    if(locationAccepted){
//                        //permission allowed
//                        //sự cho phép được phép cấp quyền
////                            detectLocation();
//                            startLocationUpdates();
//
//                    }
//                    else {
//                        //permission denied
//                        //sự cho phép không được phép cấp quyền
//                        //location permission is necessary
//                        Toast.makeText(this,"Sự cho phép vị trí là cần thiết.....",Toast.LENGTH_SHORT).show();
//                    }
//                }

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
                Toast.makeText(this, "Đã chọn hình thành công!", Toast.LENGTH_SHORT).show();
                //set to imageview
                profileIv.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview

                profileIv.setImageURI(image_uri);
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toast.makeText(this, "Đã chụp hình thành công!", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        Toast.makeText(this,"Vui lòng mở vị trí....",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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