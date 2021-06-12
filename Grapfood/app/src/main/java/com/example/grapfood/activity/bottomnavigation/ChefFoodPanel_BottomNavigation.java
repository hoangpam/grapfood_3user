package com.example.grapfood.activity.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.chefFoodPanel.AddProductActivity;
import com.example.grapfood.activity.chefFoodPanel.ProfileEditChef;
import com.example.grapfood.activity.chefFood_fragment.ChefHomeFragment;
import com.example.grapfood.activity.chefFood_fragment.ChefOrderFragment;
import com.example.grapfood.activity.chefFood_fragment.ChefPendingOrderFragment;
import com.example.grapfood.activity.chefFood_fragment.ChefProfileFragment;
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

import java.util.HashMap;
import java.util.UUID;

public class ChefFoodPanel_BottomNavigation extends AppCompatActivity  {

    Fragment fragmenthientai;
    FrameLayout layoutHost;


    private TextView nameTv, shopnameTv, emailTv, tabProductsTv, tabOrderTv;
    private ImageButton logoutbtn, editProfileBtn, addProductBtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    CircularImageView profileIv;
    String RandomUID;

    //    Context context = new ContextThemeWrapper(ChefFoodPanel_BottomNavigation.this, R.style.AppTheme2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chef_food_panel__bottom_navigation);


        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.chef_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // attaching bottom sheet behaviour - hide / show on scroll
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        layoutHost = findViewById(R.id.frame_container);
        fragmenthientai = new ChefHomeFragment();
        loadFragment(fragmenthientai);


        nameTv = (TextView) findViewById(R.id.nameTv);
        shopnameTv = (TextView) findViewById(R.id.ShopNameTv);
        emailTv = (TextView) findViewById(R.id.EmailTv);
        tabProductsTv = (TextView) findViewById(R.id.tabProductsTv);
        tabOrderTv = (TextView) findViewById(R.id.tabOrderTv);

        logoutbtn = (ImageButton) findViewById(R.id.logoutBTN);
        editProfileBtn = (ImageButton) findViewById(R.id.editProfileBtn);
        addProductBtn = (ImageButton) findViewById(R.id.addProductBTN);
        profileIv = (CircularImageView) findViewById(R.id.profileIv);
//        progressDialog = new ProgressDialog(context,R.style.MaterialAlertDialog_rounded);

        progressDialog = new ProgressDialog(ChefFoodPanel_BottomNavigation.this);
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
                //open edit profile activity
                startActivity(new Intent(ChefFoodPanel_BottomNavigation.this, ProfileEditChef.class));

            }
        });

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open edit add product activity(fragment)
                startActivity(new Intent(ChefFoodPanel_BottomNavigation.this, AddProductActivity.class));
            }
        });

        tabProductsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load product
                showProductUI();
                fragmenthientai = new ChefHomeFragment();
                loadFragment(fragmenthientai);
            }
        });

        tabOrderTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //load orders
                showOrdersUI();
                fragmenthientai = new ChefOrderFragment();
                loadFragment(fragmenthientai);
            }
        });

    }

    private void showProductUI() {
        //show products ui and hide orders ui
        tabProductsTv.setTextColor(getResources().getColor(R.color.black));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabOrderTv.setTextColor(getResources().getColor(R.color.white));
        tabOrderTv.setBackgroundResource(R.drawable.shape_rect03);
    }

    private void showOrdersUI() {
        //show orders ui and hide products ui
        tabProductsTv.setTextColor(getResources().getColor(R.color.white));
        tabProductsTv.setBackgroundResource(R.drawable.shape_rect03);

        tabOrderTv.setTextColor(getResources().getColor(R.color.black));
        tabOrderTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.chefHome :
                    fragment=new ChefHomeFragment();
                    break;
                case R.id.tabProductsTv :
                    fragment=new ChefHomeFragment();
                    break;
                case R.id.chefPendingOrders:
                    fragment=new ChefPendingOrderFragment();
                    break;
                case R.id.chef_Orders :
                    fragment=new ChefOrderFragment();
                    break;
                case R.id.tabOrderTv:
                    fragment=new ChefOrderFragment();
                    break;
                case R.id.chef_Profile:
                    fragment=new ChefProfileFragment();
                    break;
            }
            return loadcheffragment(fragment);
        }
    };



    private boolean loadcheffragment(Fragment fragment) {

        if (fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,fragment).commit();
            return true;
        }
        return false;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(ChefFoodPanel_BottomNavigation.this, MainMenu.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chef");
        ref.orderByChild("UID").equalTo(firebaseAuth.getUid())
                .addValueEventListener( new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fname =""+ds.child("FirstName").getValue();
                            String name = ""+ds.child("LastName").getValue();
                            String nameshop = ""+ds.child("NameShop").getValue();
                            String email = ""+ ds.child("EmailId").getValue();
                            String profile = ""+ds.child("ImageURL").getValue();

                            nameTv.setText(""+fname+" "+name);
                            shopnameTv.setText(""+nameshop);
                            emailTv.setText(""+email);

                            try {
                                Picasso.get().load(profile).placeholder(R.drawable.ic_camera_24).into(profileIv);
                            }catch (Exception e)
                            {
                                profileIv.setImageResource(R.drawable.ic_camera_24);
                            }
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
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chef");
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
                        Toast.makeText(ChefFoodPanel_BottomNavigation.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}