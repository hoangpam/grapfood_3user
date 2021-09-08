package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.bottomnavigation.ChefFoodPanel_BottomNavigation;
import com.example.grapfood.activity.bottomnavigation.CustomerFoofPanel_BottomNavigation;
import com.example.grapfood.activity.bottomnavigation.DeliveryFoodPanel_BottomNavigation;
import com.example.grapfood.activity.chefFoodPanel.ProfileEditChef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import es.dmoral.toasty.Toasty;

import static com.example.grapfood.R.string.ban;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    TextView textView;
    FirebaseAuth Fauth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;
    //check no internet
    //kiểm tra có mạng hay không
    private Boolean wifiConnected = false;
    private Boolean mobileConnected = false;
//    Context context = new ContextThemeWrapper(MainActivity.this, R.style.AppTheme2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //make fullscreen
        //làm cho toàn màn hình
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView7);
//        set text movement after 1 period
        //thiết lập chuyển động chữ sau 1 khoảng thời gian
        imageView.animate().alpha(0f).setDuration(0);
        textView.animate().alpha(0f).setDuration(0);

        imageView.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.animate().alpha(1f).setDuration(800);

            }
        });
        isOnline();
//        store your account in your device
        //lưu trữ tài khoản trong máy
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!((Activity) MainActivity.this).isFinishing()) {

//                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                    try {
                        Fauth = FirebaseAuth.getInstance();
                        if (FirebaseApp.getApps(MainActivity.this).size() == 0) {
                            firebaseDatabase=FirebaseDatabase.getInstance();
                            firebaseDatabase.setPersistenceEnabled(true);
//                            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                        }
                        if (Fauth.getCurrentUser() != null) {
                            if (Fauth.getCurrentUser().isEmailVerified()) {
                                Fauth = FirebaseAuth.getInstance();
//                        progressDialog = new ProgressDialog(context,R.style.MaterialAlertDialog_rounded);

//                                databaseReference.onDisconnect().setValue(ServerValue.TIMESTAMP);
                                progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setTitle("Tình hình");
                                progressDialog.setMessage("Đang đăng nhập tài khoản đã tự lưu cho bạn\nHệ thống đang kết nối\nVui lòng chờ....");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid() + "/AccountType");
                                databaseReference.keepSynced(true);
                                databaseReference.child("User").child(FirebaseAuth.getInstance().getUid() + "/AccountType")
                                        .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("firebase", String.valueOf(task.getResult().getValue()));

                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                            builder.setMessage("Đăng nhập thành công");
                                            builder.setCancelable(false);
                                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();

                                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            String role = snapshot.getValue(String.class);
                                                            progressDialog.dismiss();
                                                            if (role.equals("Chef")) {
                                                                startActivity(new Intent(MainActivity.this, ChefFoodPanel_BottomNavigation.class));
                                                                finish();
                                                            }else
                                                            if (role.equals("Customer")) {
                                                                startActivity(new Intent(MainActivity.this, CustomerFoofPanel_BottomNavigation.class));
                                                                finish();
                                                            }else
                                                            if (role.equals("DeliveryPerson")) {
                                                                startActivity(new Intent(MainActivity.this, DeliveryFoodPanel_BottomNavigation.class));
                                                                finish();
                                                            }else
                                                            if(role.equals("")){
                                                                startActivity(new Intent(MainActivity.this,MainMenu.class));
                                                                finish();
                                                            }

                                                        }


                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            });
                                            AlertDialog Alert = builder.create();
                                            Alert.show();
                                        } else {
//                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                            Log.e("firebase", "Error getting data", task.getException());
                                            Toasty.custom(MainActivity.this, ban, getResources().getDrawable(R.drawable.ic_facebook),
                                                    android.R.color.black, android.R.color.holo_green_light, Toasty.LENGTH_SHORT, true, true).show();
                                            ReusableCodeForAll.ShowAlert(MainActivity.this,"Lỗi kìa",""+task.getException());
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                        builder.setMessage("Đăng nhập không thành công\nDo máy bạn bị lỗi\nHãy vào cài đặt xoá phần bộ nhớ cache\nNhấp vào nút Oke để thoát khỏi app \nVà chuyển tới website chỉ xoá cache trong máy điện thoại \nVà cài lại app để vào được");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                try {
                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("https://fptshop.com.vn/tin-tuc/thu-thuat/xoa-cache-va-data-cua-ung-dung-tren-android-6-0-de-tranh-day-bo-nho-va-gay-loi-39303" )));
                                                } catch (ActivityNotFoundException e) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("https://fptshop.com.vn/tin-tuc/thu-thuat/xoa-cache-va-data-cua-ung-dung-tren-android-6-0-de-tranh-day-bo-nho-va-gay-loi-39303" + getPackageName())));
                                                }
                                                finish();
                                            }
                                        });
                                        AlertDialog Alert = builder.create();
                                        Alert.show();
                                    }
                                });

                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setTitle("Tình hình");
                                builder.setMessage("Kiểm tra xem bạn đã xác minh Gmail và OTP của mình chưa, nếu không, vui lòng xác minh");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        dialog.dismiss();
                                        Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                Fauth.signOut();
                            }
                        } else {

                            Intent intent = new Intent(MainActivity.this, MainMenu.class);
                            startActivity(intent);
                            finish();
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        },3000);

    }
    //tình trạng kết nối wifi để dùng app
    private void isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnected()) {
            wifiConnected = ni.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = ni.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Toasty.success(this, "Bạn đã kết nối thành công đến từ wifi!", Toast.LENGTH_SHORT, true).show();

            } else if (mobileConnected) {
                Toasty.success(this, "Bạn đã kết nối thành công đến từ mạng dữ liệu di động của điện thoại!", Toast.LENGTH_SHORT, true).show();


            }
        } else {
            Toasty.error(this, "Hiện tại bạn không có kết nối mạng.\nVui lòng mở wifi và dữ liệu di động.<!>", Toast.LENGTH_SHORT, true).show();
            showSettingsWifis();
        }
    }
    //mở cài đặt đến phần wifi
    public void showSettingsWifis() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("Cài đặt");
        alertDialog.setMessage("Bật wifi để dùng app! Chuyển đến menu cài đặt?");//Enable Location Provider! Go to settings menu?
        alertDialog.setPositiveButton("Đi tới cài đặt",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_WIFI_SETTINGS);
                        MainActivity.this.startActivity(intent);
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
    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
}