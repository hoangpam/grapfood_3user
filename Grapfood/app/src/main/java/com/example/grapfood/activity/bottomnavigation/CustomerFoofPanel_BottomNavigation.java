package com.example.grapfood.activity.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.adapter_Customer.AdapterShop;
import com.example.grapfood.activity.customerFoodPanel.ProfileEditCus;
import com.example.grapfood.activity.customerFood_fagment.CustomerCartFragmnet;
import com.example.grapfood.activity.customerFood_fagment.CustomerHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerOrdersFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerProfileFragment;
import com.example.grapfood.activity.model.ModelShop;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class CustomerFoofPanel_BottomNavigation extends AppCompatActivity {
    Fragment fragmenthientai;
    FrameLayout layoutHost;
    private ProgressDialog progressDialog;
    private TextView nameTv ,emailTv ,phoneTv, tabShopsTv, tabOrderTv;
    private CircularImageView profileIv;
    private ImageButton logoutbtn,editProfileBtn;;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelShop> shopsList;
    private AdapterShop adapterShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_foof_panel__bottom_navigation);

//        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
//        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        // attaching bottom sheet behaviour - hide / show on scroll
//        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
//        layoutParams.setBehavior(new BottomNavigationBehavior());
        layoutHost = findViewById(R.id.fragment_container);
        fragmenthientai = new CustomerHomeFragment();
        loadFragment(fragmenthientai);

        nameTv = (TextView) findViewById(R.id.nameTv);
        logoutbtn = (ImageButton) findViewById(R.id.logoutBTN);
        editProfileBtn = (ImageButton) findViewById(R.id.editProfileBtn);
        phoneTv = (TextView) findViewById(R.id.PhoneTv);
        emailTv = (TextView) findViewById(R.id.EmailTv);
        tabShopsTv = (TextView) findViewById(R.id.tabShopsTv);
        tabOrderTv = (TextView) findViewById(R.id.tabOrderTv);

        logoutbtn = (ImageButton) findViewById(R.id.logoutBTN);
        editProfileBtn = (ImageButton) findViewById(R.id.editProfileBtn);

        profileIv = (CircularImageView) findViewById(R.id.profileIv);

        progressDialog = new ProgressDialog(CustomerFoofPanel_BottomNavigation.this);
        progressDialog.setTitle("Tình hình mạng yếu");
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

        tabShopsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load product
                showShopUI();
                fragmenthientai = new CustomerHomeFragment();
                loadFragment(fragmenthientai);
            }
        });

        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showOrdersUI();
                fragmenthientai = new CustomerOrdersFragment();
                loadFragment(fragmenthientai);
            }
        });


    }

    private void showShopUI() {
        //show products ui and hide orders ui
        tabShopsTv.setTextColor(getResources().getColor(R.color.black));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrderTv.setTextColor(getResources().getColor(R.color.white));
        tabOrderTv.setBackgroundResource(R.drawable.shape_rect03);
    }

    private void showOrdersUI(){
        //show orders ui and hide products ui
        tabShopsTv.setTextColor(getResources().getColor(R.color.white));
        tabShopsTv.setBackgroundResource(R.drawable.shape_rect03);

        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
        tabOrderTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
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
    };

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("UID").equalTo(firebaseAuth.getUid())
                .addValueEventListener( new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fname =""+ds.child("FirstName").getValue();
                            String name = ""+ds.child("LastName").getValue();
                            String phone = ""+ds.child("MobileNo").getValue();
                            String email = ""+ds.child("EmailId").getValue();
                            String profile = ""+ds.child("ImageURL").getValue();
                            String city =""+ds.child("City").getValue();
                            String accountType = ""+ds.child("AccountType").getValue();
                            String state = ""+ds.child("State").getValue();

                            nameTv.setText(""+fname+" "+name);
                            emailTv.setText(email);
                            phoneTv.setText(phone);

                            try {
                                Picasso.get().load(profile).placeholder(R.drawable.ic_camera_24).into(profileIv);
                            }catch (Exception e)
                            {
                                profileIv.setImageResource(R.drawable.ic_camera_24);
                            }
                            loadShops(city);
                            loadShops(accountType);
                            loadShops(state);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadShops(final String myCity) {
        //init list
        shopsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("AccountType").equalTo("Chef")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding
                        shopsList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopCity = ""+ds.child("City").getValue();

                            //show only user city shops
                            if(shopCity.equals(myCity)){
                                shopsList.add(modelShop);
                            }

                            //if you want to display all shops, skip the if statement and add this
                            //shopsList.add(modelsShop)
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
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
                    }
                });
    }
}