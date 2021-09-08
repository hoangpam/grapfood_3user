package com.example.grapfood.activity.customerFood_fagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.adapter_Customer.AdapterOrderUser;
import com.example.grapfood.activity.adapter_Customer.AdapterShop;
import com.example.grapfood.activity.model.ModelOrderUser;
import com.example.grapfood.activity.model.ModelShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

public class CustomerOrdersFragment extends Fragment {

    RecyclerView ordersRv;
    FirebaseAuth firebaseAuth;
    SwipeRefreshLayout swipeRefreshLayout;
    private AdapterShop adapterShop;
    ArrayList<ModelShop> shopsList;
    private ArrayList<ModelOrderUser> orderUsersList;
    private AdapterOrderUser adapterOrderUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerorders,null);
        firebaseAuth = FirebaseAuth.getInstance();

        ordersRv = v.findViewById(R.id.ordersRv);
        ordersRv.setHasFixedSize(true);
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.move);
        ordersRv.startAnimation(animation);
        ordersRv.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersRv.setItemAnimator(new DefaultItemAnimator());
        checkUser();

        return v;
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(getActivity(), MainMenu.class));

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
                            String accountType = ""+ds.child("AccountType").getValue();
                            String city =""+ds.child("City").getValue();
                            String state = ""+ds.child("State").getValue();


                            //load only those shops that are in the city of users
                            loadShops(city);
                            loadShops(state);

                            loadOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                });

    }

    private void loadOrders() {
        //init order list
        orderUsersList = new ArrayList<>();

        //get orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderUsersList.clear();
                for (DataSnapshot ds:  snapshot.getChildren()){
                    String uid =""+ds.getRef().getKey();

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users").child(uid).child("Orders");
                    ref.orderByChild("orderBy").equalTo(firebaseAuth.getUid())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot ds: snapshot.getChildren()){

                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);

                                            //add to list
                                            orderUsersList.add(modelOrderUser);
                                        }
                                        //setup adapter
                                        adapterOrderUser = new AdapterOrderUser(getContext(),orderUsersList);
                                        //set to recyclerview
                                        ordersRv.setAdapter(adapterOrderUser);
                                    }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
                            String shopState = ""+ds.child("State").getValue();
                            //show only user city shops
                            if(shopCity.equals(myCity)){
                                shopsList.add(modelShop);
                            }
                            if(shopCity.equals(shopState)){
                                shopsList.add(modelShop);
                            }

                            //if you want to display all shops, skip the if statement and add this
                            shopsList.add(modelShop);

                        }
                        //setup adapter
                        adapterShop  =new AdapterShop(getContext(),shopsList);
                        //set adapter
                        ordersRv.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}
