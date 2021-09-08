package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.adapter_Customer.AdapterOrderedItem;
import com.example.grapfood.activity.model.ModelOrderedItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class OrderDetailsUsersActivity extends AppCompatActivity {

    private String orderTo, orderId;

    //ui view
    private ImageButton backBtn,writeReviewBtn;
    private TextView orderIdTv,dateTv,orderStatusTv,shopNameTv,totalItemsTv,amountTv,addressTv;
    private RecyclerView itemRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelOrderedItem> orderedItemsArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_users);

        //init views
        backBtn = findViewById(R.id.backBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        shopNameTv = findViewById(R.id.shopNameTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemRv = findViewById(R.id.itemRv);
        writeReviewBtn = findViewById(R.id.writeReviewBtn);

        Intent intent = getIntent();
        orderTo = intent.getStringExtra("orderTo");//orderTo coutains uid of the shop where we placed order
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopInfo();
        loadOrderDetail();
        loadOrderItems();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle writeReviewBtn click, start write review activity
        writeReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OrderDetailsUsersActivity.this, WriteReviewActivity.class);
                intent1.putExtra("shopUid",orderTo); //to write review to a shop we have must uid of shop

                startActivity(intent1);
            }
        });
    }

    private void loadOrderItems() {
        //init list
        orderedItemsArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        orderedItemsArrayList.clear();//before loading items clear list
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderedItem modelOrderedItem = ds.getValue(ModelOrderedItem.class);
                            //add to list
                            orderedItemsArrayList.add(modelOrderedItem);
                        }
                        //all items added to list
                        //setup adapter
                        adapterOrderedItem = new AdapterOrderedItem(OrderDetailsUsersActivity.this,orderedItemsArrayList);
                        //set adapter
                        itemRv.setAdapter(adapterOrderedItem);

                        //set items count
                        totalItemsTv.setText(""+snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadOrderDetail() {
        //load order detail
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get data
                        String orderBy = ""+ snapshot.child("orderBy").getValue();
                        String orderCost = ""+snapshot.child("orderCost").getValue();
                        String orderId = ""+snapshot.child("orderId").getValue();
                        String orderStatus = ""+snapshot.child("orderStatus").getValue();
                        String orderTime = ""+snapshot.child("orderTime").getValue();
                        String orderTo = ""+snapshot.child("orderTo").getValue();
                        String deliveryFee = ""+snapshot.child("deliveryFee").getValue();
                        String latitude = ""+snapshot.child("latitude").getValue();
                        String longitude = ""+snapshot.child("longitude").getValue();


                        //conver timestamp to
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(Long.parseLong(orderTime));
                        String formateDate = DateFormat.format("dd/MM/yyyy hh:mm a",calendar).toString();

                        if(orderStatusTv.equals("Đang thực thi")){
                            orderStatusTv.setTextColor(OrderDetailsUsersActivity.this.getResources().getColor(R.color.colorPrimary));
                        }
                        else if(orderStatusTv.equals("Hoàn thành")){
                            orderStatusTv.setTextColor(OrderDetailsUsersActivity.this.getResources().getColor(R.color.green));
                        }
                        else if(orderStatusTv.equals("Bị huỷ bỏ")){
                            orderStatusTv.setTextColor(OrderDetailsUsersActivity.this.getResources().getColor(R.color.Red));
                        }

                        //set data
                        orderIdTv.setText(orderId);
                        orderStatusTv.setText(orderStatus);
                        amountTv.setText("đ"+orderCost+"[Bao gồm cả giao hàng đ"+deliveryFee+"]");
                        dateTv.setText(formateDate);

                        findAddress(latitude,longitude);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        //find address, country,state,city
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat,lon,1);

            String address = addresses.get(0).getAddressLine(0);//compleaddress

            addressTv.setText(address);
        }catch (Exception e){
//            Toasty.error(this, "Lỗi kìa"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
        }
    }

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(orderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String shopName = ""+snapshot.child("NameShop").getValue();
                        shopNameTv.setText(shopName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}