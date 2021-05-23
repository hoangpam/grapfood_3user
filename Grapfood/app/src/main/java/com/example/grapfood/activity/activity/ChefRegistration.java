package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;


import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;


import com.example.grapfood.activity.chefFoodPanel.chef_postDish;
import com.example.grapfood.activity.chefFood_fragment.ChefHomeFragment;
import com.example.grapfood.activity.object.Chef;
import com.example.grapfood.activity.object.FoodDetails;
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
import com.google.protobuf.DescriptorProtos;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ChefRegistration extends AppCompatActivity implements LocationListener {
    String[] TPHCM = {"Quận 1", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 8", "Quận 10", "Quận 11", "Quận Tân Phú", "Quận Tân Bình", "Quận Bình Tân", "Quận Phú Nhuận", "Quận Gò Vấp", "Quận 9", "Quận 2", "Quận Thủ Đức"};
    String[] TPHN = {"Quận Hoàn Kiếm", "Quận Đống Đa", "Quận Ba Đình", "Quận Hoàng Mai", "Quận Thanh Xuân", "Quận Long Biên", "Quận Nam Từ Liêm", "Quận Bắc Từ Liêm", "Quận Tây Hồ", "Quận Cầu Giấy", "Quận Hà Đông"};
    String[] TPTN = {"Huyện Châu Thành", "Huyện Hoà Thành", "Huyện Bến Cầu", "Huyện Trảng Bàn", "Huyện Tân Châu", "Huyện Dương Minh Châu"};

    //permission constants
    //hằng số quyền
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constants
    //hằng số chọn hình ảnh
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permission arrays
    //mảng quyền
     String[] locationPermission;
     String[] cameraPermission;
     String[] storagePermission;
    //image picked uri
    //chọn hình ảnh
    Uri mcropimageuri;
    private Uri image_uri;
    //hình ảnh đã chọn
    double latitude, longitude;
    LocationManager locationManager;
    ImageButton gpsBTN;
    CircularImageView profileIv;
    ImageButton btnBN;
    TextInputLayout Fname;
    TextInputLayout Lname;
    TextInputLayout NameShop;
    TextInputLayout Email;
    TextInputLayout Pass;
    TextInputLayout cpass;
    TextInputLayout mobileno;
    TextInputLayout houseno;
    TextInputLayout area;
    TextInputLayout state1;
    TextInputLayout city1;
    TextInputLayout compleaddress;

    Button signup, Emaill, Phone;
    CountryCodePicker Cpp;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    FirebaseAuth FAuth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String fname, lname, emailid, password, confpassword, mobile, house, Area, statee, cpAddress, cityy, nameshop,RandomUID,ImageURL;
    String role = "Chef";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_registration);
        Fname = (TextInputLayout) findViewById(R.id.Firstname);
        Lname = (TextInputLayout) findViewById(R.id.Lastname);
        NameShop = (TextInputLayout) findViewById(R.id.NameShop);
        Email = (TextInputLayout) findViewById(R.id.Email);
        Pass = (TextInputLayout) findViewById(R.id.Pwd);
        cpass = (TextInputLayout) findViewById(R.id.Cpass);
        mobileno = (TextInputLayout) findViewById(R.id.Mobileno);
        houseno = (TextInputLayout) findViewById(R.id.houseNo);
        state1 = (TextInputLayout) findViewById(R.id.States);
        city1 = (TextInputLayout) findViewById(R.id.Citys);
        compleaddress = (TextInputLayout) findViewById(R.id.completeAddress);
        area = (TextInputLayout) findViewById(R.id.Area);

        gpsBTN = (ImageButton) findViewById(R.id.gpsBtn);

        profileIv = (CircularImageView) findViewById(R.id.profileIv);

        signup = (Button) findViewById(R.id.Signup);
        Emaill = (Button) findViewById(R.id.email);
        Phone = (Button) findViewById(R.id.phone);

        Cpp = (CountryCodePicker) findViewById(R.id.CountryCode);
        btnBN = (ImageButton) findViewById(R.id.backBN);
        //init permissions array
        //mảng quyền nhập vào

        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

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
        gpsBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //detect current location
                //phát hiện vị trí hiện tại

                if (checkLocationPermission()) {
                    //already allowed
                    //sẵn sàng cấp quyền
                    detectLocation();

                } else {
                    //not allowed, request
                    //không được phép, yêu cầu
                    requestLocationPermission();
                    Toast.makeText(ChefRegistration.this, "Lỗi nè quỷ hà", Toast.LENGTH_SHORT).show();
                }
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

        databaseReference = firebaseDatabase.getInstance().getReference("Chef");
        FAuth = FirebaseAuth.getInstance();


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (isValid()) {
                    uploadImage();

                    final ProgressDialog mDialog = new ProgressDialog(ChefRegistration.this);

                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Đang đăng ký, vui lòng đợi......");
                    mDialog.setIndeterminate(false);
                    mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mDialog.setProgress(0);
                    mDialog.show();

                    FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference("User").child(useridd);
                                final HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("Role", role);
                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        HashMap<String, String> hashMap1 = new HashMap<>();

                                        hashMap1.put("MobileNo", mobile);
                                        hashMap1.put("FirstName", fname);
                                        hashMap1.put("LastName", lname);
                                        hashMap1.put("Name Shop", nameshop);
                                        hashMap1.put("Email Id", emailid);
                                        hashMap1.put("City", cityy);
                                        hashMap1.put("Area", Area);//phường xã
                                        hashMap1.put("Password", password);
                                        hashMap1.put("Complete Address", cpAddress);
                                        hashMap1.put("State", statee);//Quận huyện
                                        hashMap1.put("Confirm Password", confpassword);
                                        hashMap1.put("House", house);//Số đường
                                        hashMap1.put("ImageURL",String.valueOf(image_uri));

                                        firebaseDatabase.getInstance().getReference("Chef")
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
                                                            builder.setMessage("Bạn đã đăng ký! Đảm bảo xác minh email của bạn");
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
                                                            ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                                                        }
                                                    }
                                                });

                                            }
                                        });

                                    }
                                });
                            } else {

                                mDialog.dismiss();
                                ReusableCodeForAll.ShowAlert(ChefRegistration.this, "Đang bị lỗi nè", task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(ChefRegistration.this, "Quá trình đăng ký đã thất bại!\nCó thể email đã được đăng ký!\nHoặc bạn đã điền sai thông tin\nVui lòng kiểm tra lại kết nối mạng và thông tin bên trên .", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //cập nhật ảnh lên firebase storage

    private void uploadImage() {

        if(image_uri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(ChefRegistration.this);
            progressDialog.setTitle("Đang tải hình ảnh lên.....");
            progressDialog.show();
            //hàm khởi tạo ngẫu nhiệu key trong phần child firebase
            RandomUID = UUID.randomUUID().toString();

            FAuth = FirebaseAuth.getInstance();
            FirebaseStorage ref1 = FirebaseStorage.getInstance();

            FAuth.signOut();
            ref = ref1.getReference(RandomUID);
            ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Chef info = new Chef(Area,cityy,cpAddress,confpassword,emailid,fname,house,String.valueOf(uri),lname,mobile,nameshop,password,statee);
                            firebaseDatabase.getInstance().getReference("Chef").child(RandomUID)
                                    .setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    progressDialog.dismiss();
                                    Toast.makeText(ChefRegistration.this, "Hình đại diện được đăng thành công!", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ChefRegistration.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Đang tải " + (int) progress + "%");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
            });
        }
//        }else
//        {
//            Toast.makeText(chef_postDish.this,"Đang lỗi đại ca",Toast.LENGTH_SHORT).show();
//        }

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

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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

        boolean isValid = false, isValidhouseno = false, isValidlname = false, isValidname = false, isValidnameshop = false, isValidemail = false, isValidpassword = false, isValidconfpassword = false, isValidmobilenum = false,isValidarea = false,isValidcity = false,isValidstate = false;
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
//        if(TextUtils.isEmpty(Pincode)){
//            pincode.setErrorEnabled(true);
//            pincode.setError("Nhập vào Pincode");
//        }else{
//            isValidpincode = true;
//        }
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
            isValidstate = true;
        }
        isValid = (  isValidarea && isValidcity && isValidstate && isValidconfpassword && isValidpassword   && isValidemail && isValidmobilenum && isValidname && isValidhouseno && isValidlname && isValidnameshop) ? true : false;
        return isValid;


    }

    // hàm kiểm tra quyền địa chỉ
//    private boolean checkLocationPermission() {
//        boolean result = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION) ==
//                (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Kiểm tra cấp quyền địa chỉ")
                        .setMessage("Lỗi nè đại ca")
                        .setPositiveButton("Oke", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ChefRegistration.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermission, LOCATION_REQUEST_CODE);
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

    private void findAddress() {
        //find hourse no,address,country,state city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitude,longitude,1);


            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String countryName = addresses.get(0).getCountryName();

            //set addresses
            //đặt dịa chỉ
            state1.getEditText().setText(state);
            city1.getEditText().setText(city);
            area.getEditText().setText(countryName);
            compleaddress.getEditText().setText(address);

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void detectLocation() {
        //please wait
        Toast.makeText(this, "Vui lòng chờ....", Toast.LENGTH_LONG).show();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        //location detected
        //vị trí được phát hiện
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        findAddress();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //gps/location disabled
        //gps / vị trí bị vô hiệu hóa
        //please turn on location
        Toast.makeText(this,"Vui lòng mở vị trí....",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length > 0)
                {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(locationAccepted){
                        //permission allowed
                        //sự cho phép được phép cấp quyền
                        detectLocation();
                    }
                    else {
                        //permission denied
                        //sự cho phép không được phép cấp quyền
                        //location permission is necessary
                        Toast.makeText(this,"Sự cho phép vị trí là cần thiết.....",Toast.LENGTH_SHORT).show();
                    }
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


}