package com.example.grapfood.activity.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.chefFoodPanel.ProfileEditChef;
import com.example.grapfood.activity.customerFoodPanel.ProfileEditCus;
import com.example.grapfood.activity.customerFood_fagment.CustomerCartFragmnet;
import com.example.grapfood.activity.customerFood_fagment.CustomerHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerOrdersFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerProfileFragment;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryProfile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CustomerFoofPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    Fragment fragmenthientai;
    FrameLayout layoutHost;
    private ProgressDialog progressDialog;
    private TextView nameTv;
    private ImageButton logoutbtn,editProfileBtn;;

    private FirebaseAuth firebaseAuth;
//    Context context = new ContextThemeWrapper(CustomerFoofPanel_BottomNavigation.this, R.style.AppTheme2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_foof_panel__bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        layoutHost = findViewById(R.id.fragment_container);
        fragmenthientai = new CustomerHomeFragment();
        loadFragment(fragmenthientai);

        nameTv = (TextView) findViewById(R.id.nameTv);
        logoutbtn = (ImageButton) findViewById(R.id.logoutBTN);
        editProfileBtn = (ImageButton) findViewById(R.id.editProfileBtn);

//        progressDialog = new ProgressDialog(context,R.style.MaterialAlertDialog_rounded);

        progressDialog = new ProgressDialog(CustomerFoofPanel_BottomNavigation.this);
        progressDialog.setTitle("Tình hình");
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make offline
                //sign out
                //go to login activity
                makeMeOffline();
            }
        });
        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CustomerFoofPanel_BottomNavigation.this, ProfileEditCus.class));
                finish();
            }
        });

    }
    //hiển thị các dòng menu nằm phía dưới
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.cust_Home:
                fragment=new CustomerHomeFragment();
                break;
        }
        switch (item.getItemId()){
            case R.id.cart:
                fragment=new CustomerCartFragmnet();
                break;
        }
        switch (item.getItemId()){
            case R.id.cust_profile:
                fragment=new CustomerProfileFragment();
                break;
        }
        switch (item.getItemId()){
            case R.id.Cust_order:
                fragment=new CustomerOrdersFragment();
                break;
        }
        switch (item.getItemId()){
            case R.id.track:
                fragment=new CustomerCartFragmnet();
                break;
        }
        return loadcheffragment(fragment);

    }
    //hàm load các fragment
    private boolean loadcheffragment(Fragment fragment) {

        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
        return false;
    }
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
    //đổ dữ liệu cho textview người dùng
    //toolbar
    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(CustomerFoofPanel_BottomNavigation.this, MainMenu.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer");
        ref.orderByChild("UID").equalTo(firebaseAuth.getUid())
                .addValueEventListener( new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fname =""+ds.child("FirstName").getValue();
                            String name = ""+ds.child("LastName").getValue();


                            nameTv.setText(""+fname+" "+name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void makeMeOffline()
    {
        //after logging in make user online
        progressDialog.setMessage("Đang đang xuất khỏi hệ thống...");
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online","false");


        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Customer");
        ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CustomerFoofPanel_BottomNavigation.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}