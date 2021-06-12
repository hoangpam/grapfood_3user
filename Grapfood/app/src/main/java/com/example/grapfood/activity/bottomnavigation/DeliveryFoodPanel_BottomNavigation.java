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
import com.example.grapfood.activity.chefFood_fragment.ChefHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerCartFragmnet;
import com.example.grapfood.activity.customerFood_fagment.CustomerHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerOrdersFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerProfileFragment;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryPendingOrderFragment;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryProfile;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryShipOrders;
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

public class DeliveryFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Fragment fragmenthientai;
    FrameLayout layoutHost;

    private TextView nameTv;
    private ImageButton logoutbtn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
//    Context context = new ContextThemeWrapper(DeliveryFoodPanel_BottomNavigation .this, R.style.AppTheme2);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_food_panel__bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_devivery);
        navigationView.setOnNavigationItemSelectedListener(this);
        layoutHost = findViewById(R.id.fragment_container);
        fragmenthientai = new DeliveryProfile();
        loadFragment(fragmenthientai);

        nameTv = (TextView) findViewById(R.id.nameTv);
        logoutbtn = (ImageButton) findViewById(R.id.logoutBTN);

//        progressDialog = new ProgressDialog(context,R.style.MaterialAlertDialog_rounded);

        progressDialog = new ProgressDialog(DeliveryFoodPanel_BottomNavigation.this);
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


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.pendingorders:
                fragment=new DeliveryPendingOrderFragment();
                break;
        }
        switch (item.getItemId()){
            case R.id.shiporders:
                fragment=new DeliveryShipOrders();
                break;
        }
        switch (item.getItemId()){
            case R.id.delivery_profile:
                fragment=new DeliveryProfile();
                break;
        }
        return loadcheffragment(fragment);

    }

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
            startActivity(new Intent(DeliveryFoodPanel_BottomNavigation.this, MainMenu.class));
            finish();
        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DeliveryPerson");
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

    private void makeMeOffline() {
        //after logging in make user online
        progressDialog.setMessage("Đang đang xuất khỏi hệ thống...");
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Online", "false");


        //update value to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("DeliveryPerson");
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
                        Toast.makeText(DeliveryFoodPanel_BottomNavigation.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}