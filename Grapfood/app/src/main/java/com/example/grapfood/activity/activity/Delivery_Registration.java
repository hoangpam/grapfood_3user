package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
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
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;

import com.example.grapfood.BuildConfig;
import com.example.grapfood.R;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class Delivery_Registration extends AppCompatActivity implements LocationListener {

    final String TAG = "GPS";
    //permission constants
    //h???ng s??? quy???n
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    //h???ng s??? ch???n h??nh ???nh
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    //kh???i t???o d??nh cho GPS
    //?????nh v???
    //ch???n khoang v??ng
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
    //m???ng quy???n
    String[] locationPermission;
    String[] cameraPermission;
    String[] storagePermission;
    //image picked uri
    //ch???n h??nh ???nh

    private Uri image_uri;
    //h??nh ???nh ???? ch???n
    double latitude = 0.0, longitude = 0.0;
    LocationManager locationManager;
    //check no internet
    //ki???m tra c?? m???ng hay kh??ng
    private Boolean wifiConnected = false;
    private Boolean mobileConnected = false;


    ImageButton gpsBTN;
    CircularImageView profileIv;
    ImageButton btnBN;

    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;

    TextInputLayout Fname,Lname,Email,Pass,cpass,mobileno,houseno,area,
     States,Citys,compleaddress;
    Button signup, Emaill, Phone;
    CountryCodePicker Cpp;
    FirebaseAuth FAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    String fname,lname,emailid,password,confpassword,mobile,house,Area,statee,cityy,ImageURL,cpAddress;

    private String UID,MobileNo,FirstName,EmailId,ImageURL1,Timestamp;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery__registration);

        Fname = (TextInputLayout)findViewById(R.id.fname);
        Lname = (TextInputLayout)findViewById(R.id.lname);
        Email = (TextInputLayout)findViewById(R.id.Emailid);
        Pass = (TextInputLayout)findViewById(R.id.password);
        cpass = (TextInputLayout)findViewById(R.id.confirmpassword);
        mobileno = (TextInputLayout)findViewById(R.id.mobileno);
        houseno = (TextInputLayout)findViewById(R.id.Houseno);
        States = (TextInputLayout) findViewById(R.id.States2);
        Citys = (TextInputLayout) findViewById(R.id.Citys2);
        area = (TextInputLayout)findViewById(R.id.Areaa);
        compleaddress = (TextInputLayout) findViewById(R.id.completeAddress);


        Cpp = (CountryCodePicker)findViewById(R.id.ctrycode);

        signup = (Button)findViewById(R.id.Signupp);
        Emaill = (Button)findViewById(R.id.emaillid);
        Phone = (Button)findViewById(R.id.Phonenumber);

        gpsBTN = (ImageButton) findViewById(R.id.gpsBtn);

        profileIv = (CircularImageView) findViewById(R.id.profileIv);
        btnBN = (ImageButton) findViewById(R.id.backBN);

        //init permissions array
        //m???ng quy???n nh???p v??o

        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);


        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        intent = getIntent();

        UID = intent.getStringExtra("UID");
        MobileNo = intent.getStringExtra("MobileNo");
        FirstName = intent.getStringExtra("FirstName");
        EmailId = intent.getStringExtra("EmailId");
        ImageURL1 = intent.getStringExtra("ImageURL");
        Timestamp = intent.getStringExtra("Timestamp");

        //mouse click event
        //s??? ki???n click chu???t
        btnBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tr??? v??? ph??a tr?????c ????
                onBackPressed();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image
                //ch???n h??nh ???nh
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
                    emailid = Email.getEditText().getText().toString().trim();
                    mobile = mobileno.getEditText().getText().toString().trim();
                    password = Pass.getEditText().getText().toString().trim();
                    confpassword = cpass.getEditText().getText().toString().trim();
                    Area = area.getEditText().getText().toString().trim();
                    house = houseno.getEditText().getText().toString().trim();
                    ImageURL = String.valueOf(image_uri).trim();
                    cpAddress = compleaddress.getEditText().getText().toString().trim();
                    statee = States.getEditText().getText().toString().trim();
                    cityy = Citys.getEditText().getText().toString().trim();
                    String timestamp = "" + System.currentTimeMillis();

                    if (isValid() && image_uri == null ){

                        final ProgressDialog mDialog = new ProgressDialog(Delivery_Registration.this);
                        mDialog.setCancelable(false);
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setTitle("\uD83E\uDD84 T??nh h??nh m???ng y???u");
                        mDialog.setMessage("??ang ????ng k?? v?? t???i h??nh ?????i di???n l??n, vui l??ng ?????i......");
                        mDialog.show();

                        FAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    try{
                                        HashMap<String , String> hashMap1 = new HashMap<>();
                                        hashMap1.put("UID", "" + FAuth.getUid());
                                        hashMap1.put("MobileNo",mobile);
                                        hashMap1.put("FirstName",fname);
                                        hashMap1.put("LastName",lname);
                                        hashMap1.put("EmailId",emailid);
                                        hashMap1.put("City",cityy);
                                        hashMap1.put("Area",Area);
                                        hashMap1.put("Password",password);
                                        hashMap1.put("State",statee);
                                        hashMap1.put("ConfirmPassword",confpassword);
                                        hashMap1.put("House",house);
                                        hashMap1.put("Timestamp", "" + timestamp );//+" "+DateFormat.getTimeInstance().format(new Date())
                                        hashMap1.put("ImageURL", "");
                                        hashMap1.put("Latitude", "" + mCurrentLocation.getLatitude());
                                        hashMap1.put("Longitude", "" + mCurrentLocation.getLongitude());
                                        hashMap1.put("CompleteAddress", cpAddress);
                                        hashMap1.put("AccountType","DeliveryPerson");
                                        hashMap1.put("Online", "true");

                                        databaseReference
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                mDialog.dismiss();

                                                FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_Registration.this);
                                                            builder.setMessage("\uD83E\uDD84 B???n ???? ????ng k??! ?????m b???o x??c minh email c???a b???n");
                                                            builder.setCancelable(false);
                                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    dialog.dismiss();

                                                                    String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                                    Intent b = new Intent(Delivery_Registration.this,Delivery_VerifyPhone.class);
                                                                    b.putExtra("phonenumber",phonenumber);
                                                                    startActivity(b);

                                                                }
                                                            });
                                                            AlertDialog Alert = builder.create();
                                                            Alert.show();
                                                        }else{
                                                            mDialog.dismiss();
                                                            Toasty.error(Delivery_Registration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                            ReusableCodeForAll.ShowAlert(Delivery_Registration.this,"??ang b??? l???i n??",task.getException().getMessage());
                                                        }
                                                    }
                                                });

                                            }
                                        });
                                    }catch (Exception e){
                                        Erro();
                                        Toasty.warning(Delivery_Registration.this, "Vui l??ng nh???n v??o n??t GPS g??c ph???i b??n tr??n ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                                        mDialog.dismiss();
                                    }

                                }else{
                                    mDialog.dismiss();
                                    Toasty.error(Delivery_Registration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                    ReusableCodeForAll.ShowAlert(Delivery_Registration.this,"Error",task.getException().getMessage());

                                }
                            }
                        });
                    }
                    else if (isValid() && image_uri != null){


                        String filePathAndName = "profile_image/" + "" + FAuth.getUid();

                        final ProgressDialog progressDialog = new ProgressDialog(Delivery_Registration.this);
                        progressDialog.setTitle("\uD83E\uDD84 ??ang ????ng k?? v?? t???i h??nh ???nh ?????i di???n l??n, vui l??ng ?????i t??....");
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
                                            FAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                    if (task.isSuccessful()){
                                                        try {
                                                            HashMap<String , String> hashMap1 = new HashMap<>();
                                                            hashMap1.put("UID", "" + FAuth.getUid());
                                                            hashMap1.put("MobileNo",mobile);
                                                            hashMap1.put("FirstName",fname);
                                                            hashMap1.put("LastName",lname);
                                                            hashMap1.put("EmailId",emailid);
                                                            hashMap1.put("City",cityy);
                                                            hashMap1.put("Area",Area);
                                                            hashMap1.put("Password",password);
                                                            hashMap1.put("State",statee);
                                                            hashMap1.put("ConfirmPassword",confpassword);
                                                            hashMap1.put("House",house);
                                                            hashMap1.put("Timestamp", "" + timestamp);//+" "+DateFormat.getTimeInstance().format(new Date())
                                                            hashMap1.put("ImageURL", ""+downloadImageUri);
                                                            hashMap1.put("Latitude", "" + mCurrentLocation.getLatitude());
                                                            hashMap1.put("Longitude", "" + mCurrentLocation.getLongitude());
                                                            hashMap1.put("CompleteAddress", cpAddress);
                                                            hashMap1.put("AccountType","DeliveryPerson");
                                                            hashMap1.put("Online", "true");

                                                            databaseReference
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    progressDialog.dismiss();

                                                                    FAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if(task.isSuccessful()){
//                                                                                Context context = new ContextThemeWrapper(Delivery_Registration.this, R.style.AppTheme2);
//                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.MaterialAlertDialog_rounded);

                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(Delivery_Registration.this);
                                                                                builder.setMessage("\uD83E\uDD84 B???n ???? ????ng k??! ?????m b???o x??c minh Email c???a b???n");
                                                                                builder.setCancelable(false);
                                                                                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                                        dialog.dismiss();
                                                                                        String phonenumber = Cpp.getSelectedCountryCodeWithPlus() + mobile;
                                                                                        Intent b = new Intent(Delivery_Registration.this,VerifyPhone.class);
                                                                                        b.putExtra("phonenumber",phonenumber);
                                                                                        startActivity(b);

                                                                                    }
                                                                                });
                                                                                AlertDialog Alert = builder.create();
                                                                                Alert.show();
                                                                            }else{
                                                                                progressDialog.dismiss();
                                                                                Toasty.error(Delivery_Registration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                                                ReusableCodeForAll.ShowAlert(Delivery_Registration.this,"Error",task.getException().getMessage());
                                                                            }
                                                                        }
                                                                    });

                                                                }
                                                            });
                                                        }catch (Exception e){
                                                            Erro();
                                                            Toasty.warning(Delivery_Registration.this, "Vui l??ng nh???n v??o n??t GPS g??c ph???i b??n tr??n ..."+"\n"+e.getMessage(), Toast.LENGTH_LONG, true).show();
                                                            progressDialog.dismiss();
                                                        }

                                                    }else {
                                                        progressDialog.dismiss();
                                                        Toasty.error(Delivery_Registration.this, ""+task.getException().getMessage(), Toast.LENGTH_LONG, true).show();
                                                        ReusableCodeForAll.ShowAlert(Delivery_Registration.this, "??ang b??? l???i n??", task.getException().getMessage());
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
                                        Toast.makeText(Delivery_Registration.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setMessage("??ang t???i " + (int) progress + "%");
                                        progressDialog.setCanceledOnTouchOutside(false);
                                    }
                                });
                    }
                    else{
                        Toasty.error(Delivery_Registration.this, "Qu?? tr??nh ????ng k?? ???? th???t b???i!\nC?? th??? email ???? ???????c ????ng k??!\nHo???c b???n ???? ??i???n sai th??ng tin\nVui l??ng ki???m tra l???i k???t n???i m???ng v?? th??ng tin b??n tr??n .", Toast.LENGTH_SHORT, true).show();

//                    Toast.makeText(Delivery_Registration.this, "Qu?? tr??nh ????ng k?? ???? th???t b???i!\nC?? th??? email ???? ???????c ????ng k??!\nHo???c b???n ???? ??i???n sai th??ng tin\nVui l??ng ki???m tra l???i k???t n???i m???ng v?? th??ng tin b??n tr??n .", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Erro();
                    Toasty.warning(Delivery_Registration.this, "Vui l??ng nh???n v??o n??t GPS g??c ph???i b??n tr??n ..."+"\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                }

            }
        });

        Emaill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Delivery_Registration.this,Delivery_Login.class));
                finish();
            }
        });
        Phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Delivery_Registration.this,Delivery_Loginphone.class));
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
                    Log.d(TAG, "getAddress:  th??nh ph???" +" "+ city);
                    Log.d(TAG, "getAddress:  to??n ?????a ch???" +" "+ address);

                    Log.d(TAG, "getAddress:  qu???n"+ " "+ state);

                    Log.d(TAG, "getAddress:  s??? nh??"+ " "+ knownName);

                    //set addresses
                    //?????t d???a ch???
                    States.getEditText().setText(state);
                    Citys.getEditText().setText(city);
                    houseno.getEditText().setText(knownName);

                    compleaddress.getEditText().setText(address);
                }
            } catch (Exception e) {
                Toasty.error(this, "Hi???n t???i kh??ng truy c???p ???????c v??? tr?? c???a b???n"+ e.getMessage(), Toast.LENGTH_SHORT, true).show();

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
                        Log.i(TAG, "T???t c??? c??c c??i ?????t v??? tr?? ?????u h??i l??ng.");
                        //Started location updates!
                        Toasty.warning(getApplicationContext(), "???? b???t ?????u c???p nh???t v??? tr?? hi???n t???i c???a b???n!\nVui l??ng t??? ??i???n t??n ???????ng\n Ki???m tra l???i xem ???? ????ng ?????i ch??? ch??a!", Toast.LENGTH_LONG, true).show();

//                        Toast.makeText(getApplicationContext(), "???? b???t ?????u c???p nh???t v??? tr?? hi???n t???i c???a b???n!\nVui l??ng t??? ??i???n t??n ???????ng\n Ki???m tra l???i xem ???? ????ng ?????i ch??? ch??a", Toast.LENGTH_LONG).show();

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
                                Log.i(TAG, "C??i ?????t v??? tr?? kh??ng h??i l??ng. ??ang c??? g???ng n??ng c???p " +
                                        "C??i ?????t v??? tr??\n ");//location settings
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Delivery_Registration.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    //PendingIntent unable to execute request.
                                    Log.i(TAG, "PendingIntent kh??ng th??? th???c hi???n y??u c???u.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toasty.error(Delivery_Registration.this, ""+errorMessage+"\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();

//                                Toast.makeText(Delivery_Registration.this, errorMessage, Toast.LENGTH_LONG).show();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Delivery_Registration.this);
        alertDialog.setTitle("GPS kh??ng ???????c k??ch ho???t!");
        alertDialog.setMessage("B???n c?? mu???n b???t GPS kh??ng?");
        alertDialog.setPositiveButton("C??", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "C?? th??? nh???n ???????c v??? tr??");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS ???? ???????c b???t");
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
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER ???? ???????c b???t");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

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
                Log.d(TAG, "Kh??ng th??? nh???n ???????c v??? tr??");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
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

    private void Erro() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("?? k??a l???i");
        alerta.setMessage("Vui l??ng nh???n n??t m??u ????? GPS b??n ph???i ch??? ????ng K??");
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
//        findAddress();
            Log.d("latitude", "latitude--" + latitude);
            Log.d("longitude", "longitude--" + longitude);
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            try {
                if (latitude == 0 && longitude == 0) {
                    Toasty.warning(Delivery_Registration.this, "Vui l??ng nh???n n??t GPS c???a b??n ph???i ch??? ????NG K?? .... ", Toast.LENGTH_LONG, true).show();
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
                        //?????t d???a ch???

                        compleaddress.getEditText().setText(address + " " + city + " " + state);
                    }
                }

            } catch (Exception e) {
                Toasty.error(Delivery_Registration.this, "" + e.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Delivery_Registration.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    //ph???n c??i ?????t hi???n th??? khung ch???n h??nh ???nh
    //the settings show the picture selection frame
    /////
    private void showImagePickDialog() {
        //options to display in dialog
        //c??c t??y ch???n ????? hi???n th??? trong h???p tho???i
        String[] options = {"M??y ???nh", "B??? s??u t???p ho???c Kho ???nh"};//camara, gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vui l??ng ch???n h??nh ???nh ??? d???ng")//pick image
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            // m??y ???nh ???????c ch???n //camara clicked
                            if (checkCameraPermission()) {
                                //camera permissin allowed
                                pickFromCamera();
                            } else {
                                //not allowed, request
                                requestCameraPermission();
                            }
                        } else {
                            //b??? s??u t???p ???????c ch???n //galley clicked
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
    //ph???n n??t ch???n t??? b??? s??u t???p
    //button section select from gallery
    ///
    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//image
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }
    //ph???n n??t ch???n t??? camera
    //select button from camera
    ///
    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image Title");//Temp_Image Title
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image Description");//Temp_Image Description

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }
    //ph???n m?? hi???n th??? c?? ????ng Email hay Gmail hay kh??ng
    //Is the displayed code correct Email or Gmail?
    //
    String emailpattern = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    //ph???n ki???m tra gi?? tr??? ?????u v??o c???a c??c TextInputLayout c?? r???ng hay kh??ng
    //Checking the input value of the TextInputLayout is empty or not
    //

    public boolean isValid(){
        Email.setErrorEnabled(false);
        Email.setError("");
        Fname.setErrorEnabled(false);
        Fname.setError("");
        Lname.setErrorEnabled(false);
        Lname.setError("");
        Pass.setErrorEnabled(false);
        Pass.setError("");
        mobileno.setErrorEnabled(false);
        mobileno.setError("");
        cpass.setErrorEnabled(false);
        cpass.setError("");
        area.setErrorEnabled(false);
        area.setError("");
        houseno.setErrorEnabled(false);
        houseno.setError("");
        States.setErrorEnabled(false);
        States.setError("");
        Citys.setErrorEnabled(false);
        Citys.setError("");
        compleaddress.setErrorEnabled(false);
        compleaddress.setError("");

        boolean isValid=false,isValidcitys = false,isValidstate = false,isValidcompleteAd = false,isValidhouseno=false,isValidlname=false,isValidname=false,isValidemail=false,isValidpassword=false,isValidconfpassword=false,isValidmobilenum=false,isValidarea=false;
        if(TextUtils.isEmpty(fname)){
            Fname.setErrorEnabled(true);
            Fname.setError("Vui l??ng nh???p H???");
        }else{
            isValidname = true;
        }
        if(TextUtils.isEmpty(lname)){
            Lname.setErrorEnabled(true);
            Lname.setError("Vui l??ng nh???p T??n");
        }else{
            isValidlname = true;
        }
        if(TextUtils.isEmpty(emailid)){
            Email.setErrorEnabled(true);
            Email.setError("Vui l??ng nh???p Email");
        }else{
            if(emailid.matches(emailpattern)){
                isValidemail = true;
            }else{
                Email.setErrorEnabled(true);
                Email.setError("B???n ???? nh???p sai c?? ph??p Email");
            }
        }
        if(TextUtils.isEmpty(password)){
            Pass.setErrorEnabled(true);
            Pass.setError("Vui l??ng nh???p M???t Kh???u");
        }else{
            if(password.length()<8){
                Pass.setErrorEnabled(true);
                Pass.setError("M???t Kh???u c???a b???n b??? y???u");
            }else{
                isValidpassword = true;
            }
        }
        if(TextUtils.isEmpty(confpassword)){
            cpass.setErrorEnabled(true);
            cpass.setError("Vui l??ng nh???p l???i M???t Kh???u");
        }else{
            if(!password.equals(confpassword)){
                cpass.setErrorEnabled(true);
                cpass.setError("M???t Kh???u kh??ng kh???p");
            }else{
                isValidconfpassword = true;
            }
        }
        if(TextUtils.isEmpty(mobile)){
            mobileno.setErrorEnabled(true);
            mobileno.setError("Vui l??ng nh???p S??? ??i???n Tho???i");
        }else{
            if(mobile.length()<10){
                mobileno.setErrorEnabled(true);
                mobileno.setError("S??? ??i???n Tho???i di ?????ng kh??ng h???p l???");
            }else{
                isValidmobilenum = true;
            }
        }
        if(TextUtils.isEmpty(Area)){
            area.setErrorEnabled(true);
            area.setError("Vui l??ng nh???p T??n ???????ng");
        }else{
            isValidarea = true;
        }
        if(TextUtils.isEmpty(house)){
            houseno.setErrorEnabled(true);
            houseno.setError("Vui l??ng nh???p S??? Nh??");
        }else{
            isValidhouseno = true;
        }
        if(TextUtils.isEmpty(statee)){
            States.setErrorEnabled(true);
            States.setError("Vui l??ng nh???p Qu???n/ huy???n");
        }else{
            isValidstate = true;
        }
        if(TextUtils.isEmpty(cityy)){
            Citys.setErrorEnabled(true);
            Citys.setError("Vui l??ng nh???p Th??nh ph???");
        }else{
            isValidcitys = true;
        }
        if(TextUtils.isEmpty(cpAddress)){
            compleaddress.setErrorEnabled(true);
            compleaddress.setError("Vui l??ng nh???p Ho??n th??nh ?????y ????? ?????a ch???");
        }else{
            isValidcompleteAd = true;
        }
        isValid = (isValidcitys && isValidstate && isValidcompleteAd && isValidarea && isValidconfpassword && isValidpassword  && isValidemail && isValidmobilenum && isValidname && isValidhouseno && isValidlname) ? true : false;
        return isValid;


    }
    //ph???n ki???m tra ???? nh???n n??t GPS hay ch??a ????? x??c ?????nh ????ng v??? tr?? hi???n t???i
    // check whether the GPS button has been pressed or not to determine the current location
    //
    private boolean init()
    {
        boolean isValid=false,isValidLocation = false;
        double nullString = Double.parseDouble(null);
        if(mCurrentLocation.getLongitude() == nullString || mCurrentLocation.getLongitude() == nullString)
        {
            Toasty.warning(this, "Vui l??ng nh???n v??o n??t GPS g??c ph???i b??n tr??n ...", Toast.LENGTH_SHORT, true).show();

//            Toast.makeText(this, "Vui l??ng nh???n v??o n??t GPS g??c ph???i b??n tr??n ...", Toast.LENGTH_SHORT).show();
        }
        else{
            isValidLocation = true;
        }
        isValid = isValidLocation ? true : false;
        return isValid;
    }
    // h??m ki???m tra quy???n ?????a ch???
    // function to check address permissions
    //
    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermission, LOCATION_REQUEST_CODE);
    }
    //t??nh tr???ng k???t n???i wifi ????? d??ng app
    private void isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected()){
            wifiConnected = ni.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            if(wifiConnected)
            {
                Toasty.success(this, "B???n ???? k???t n???i th??nh c??ng ?????n t??? wifi!", Toast.LENGTH_SHORT, true).show();

            }
            else if(mobileConnected)
            {
                Toasty.success(this, "B???n ???? k???t n???i th??nh c??ng ?????n t??? m???ng d??? li???u di ?????ng c???a ??i???n tho???i!", Toast.LENGTH_SHORT, true).show();

            }
        }else {
            Toasty.error(this, "Hi???n t???i b???n kh??ng c?? k???t n???i m???ng.\nVui l??ng m??? wifi v?? d??? li???u di ?????ng.<!>", Toast.LENGTH_SHORT, true).show();

            showSettingsWifis();
        }
    }
    //m??? c??i ?????t ?????n ph???n wifi
    public void showSettingsWifis(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Delivery_Registration.this);
        alertDialog.setTitle("C??i ?????t");
        alertDialog.setMessage("B???t wifi ????? d??ng app! Chuy???n ?????n menu c??i ?????t?");//Enable Location Provider! Go to settings menu?
        alertDialog.setPositiveButton("??i t???i c??i ?????t",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        Delivery_Registration.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Hu???",
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
        //v??? tr?? ???????c ph??t hi???n
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
        //gps / v??? tr?? b??? v?? hi???u h??a
        //please turn on location
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        Toasty.warning(this, "Vui l??ng m??? v??? tr??....", Toast.LENGTH_SHORT, true).show();

//        Toast.makeText(this,"Vui l??ng m??? v??? tr??....",Toast.LENGTH_SHORT).show();
//        showSettingsAlert();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
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
                            showMessageOKCancel("C??c quy???n n??y l?? b???t bu???c ?????i v???i ???ng d???ng. Vui l??ng cho ph??p truy c???p.",
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
                    Log.d(TAG, "Kh??ng c?? quy???n b??? t??? ch???i.");
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
                        //s??? cho ph??p ???????c ph??p c???p quy???n
                        pickFromCamera();

                    }
                    else {
                        //permission denied
                        //s??? cho ph??p kh??ng ???????c ph??p c???p quy???n
                        //location permission is necessary
                        Toasty.success(this, "S??? cho ph??p m??y ???nh l?? c???n thi???t.....", Toast.LENGTH_SHORT, true).show();

//                        Toast.makeText(this,"S??? cho ph??p m??y ???nh l?? c???n thi???t.....",Toast.LENGTH_SHORT).show();
                    }
                }
            }break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0)
                {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted && image_uri!=null){
                        //permission allowed
                        //s??? cho ph??p ???????c ph??p c???p quy???n
                        pickFromGallery();
                    }
                    else {
                        //permission denied
                        //s??? cho ph??p kh??ng ???????c ph??p c???p quy???n
                        //location permission is necessary
                        Toasty.success(this, "S??? cho ph??p b??? nh??? l?? c???n thi???t....", Toast.LENGTH_SHORT, true).show();

//                        Toast.makeText(this,"S??? cho ph??p b??? nh??? l?? c???n thi???t.....",Toast.LENGTH_SHORT).show();
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
                Toasty.success(this, "???? ch???n h??nh th??nh c??ng!...", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(this, "???? ch???n h??nh th??nh c??ng!", Toast.LENGTH_SHORT).show();
                //set to imageview
                profileIv.setImageURI(image_uri);

            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                //set to imageview

                profileIv.setImageURI(image_uri);
                ((CircularImageView) findViewById(R.id.profileIv)).setImageURI(image_uri);
                Toasty.success(this, "???? ch???p h??nh th??nh c??ng!....", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(this, "???? ch???p h??nh th??nh c??ng!", Toast.LENGTH_SHORT).show();
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