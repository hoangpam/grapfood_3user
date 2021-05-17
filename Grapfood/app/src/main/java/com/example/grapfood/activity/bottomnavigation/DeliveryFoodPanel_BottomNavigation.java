package com.example.grapfood.activity.bottomnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.grapfood.R;
import com.example.grapfood.activity.chefFood_fragment.ChefHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerCartFragmnet;
import com.example.grapfood.activity.customerFood_fagment.CustomerHomeFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerOrdersFragment;
import com.example.grapfood.activity.customerFood_fagment.CustomerProfileFragment;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryPendingOrderFragment;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryProfile;
import com.example.grapfood.activity.deliveryFood_fagment.DeliveryShipOrders;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DeliveryFoodPanel_BottomNavigation extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Fragment fragmenthientai;
    FrameLayout layoutHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_food_panel__bottom_navigation);
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_devivery);
        navigationView.setOnNavigationItemSelectedListener(this);
        layoutHost = findViewById(R.id.fragment_container);
        fragmenthientai = new DeliveryProfile();
        loadFragment(fragmenthientai);
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
}