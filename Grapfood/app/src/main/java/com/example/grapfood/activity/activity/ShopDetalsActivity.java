package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.adapter_Customer.AdapterCartItem;
import com.example.grapfood.activity.adapter_Customer.AdapterProductUser;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.adapter_defFood.AdapterReview;
import com.example.grapfood.activity.model.ModelCartItem;
import com.example.grapfood.activity.model.ModelProduct;
import com.example.grapfood.activity.model.ModelReview;
import com.example.grapfood.activity.object.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetalsActivity extends AppCompatActivity {

    //declare ui views
    private ImageView shopIv;
    private TextView shopNameTv , phoneTv, emailTv, openClosedTv, deliveryFeeTv, addressTv,filterProductTv, cartCountTv;
    private ImageButton callBtn, mapBtn, cartBtn, backBtn, filterProductBtn, reviewBtn;
    private EditText searchProductEt;
    private RatingBar ratingBar;
    private RecyclerView Recycle_menu;

    private String shopUid;
    private String myLAtitude, myLongitude, myPhone;
    private String shopName, shopEmail, shopPhone,shopAddress,shopLAtitude, shopLongitude;
    public String deliveryFee;


    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    private ArrayList<ModelProduct> productsList;
    private AdapterProductUser adapterProductUser;

    //cart
    private ArrayList<ModelCartItem> cartItemsList;
    private AdapterCartItem adapterCartItem;

    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detals);

        //init ui views
        shopIv = findViewById(R.id.shopIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        phoneTv = findViewById(R.id.phoneTv);
        emailTv = findViewById(R.id.emailTv);
        openClosedTv = findViewById(R.id.openClosedTv);
        deliveryFeeTv = findViewById(R.id.deliveryFeeTv);
        addressTv = findViewById(R.id.addressTv);
        callBtn = findViewById(R.id.callBtn);
        mapBtn = findViewById(R.id.mapBtn);
        cartBtn = findViewById(R.id.cartBtn);
        backBtn = findViewById(R.id.backBtn);
        searchProductEt = findViewById(R.id.searchProductEt);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        cartCountTv = findViewById(R.id.cartCountTv);
        reviewBtn = findViewById(R.id.reviewBtn);
        ratingBar = findViewById(R.id.ratingBar);

        //init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tình hình mạng yếu vui lòng đợi...");
        progressDialog.setCanceledOnTouchOutside(false);


        Recycle_menu = findViewById(R.id.Recycle_menu);
        Recycle_menu.setHasFixedSize(true);
        Recycle_menu.setLayoutManager(new LinearLayoutManager(this));
        Recycle_menu.setItemAnimator(new DefaultItemAnimator());
//        adapterProductUser = new AdapterProductUser(this,productsList);
//        Recycle_menu.setAdapter(adapterProductUser);
        filterProductTv =findViewById(R.id.filterProductTv);
        //get uid of the shop from intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        loadReviews();//avg rating, set on ratingbar

        //declare it ti class level an initin onCreate
         easyDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Quantily",new String[]{"text","no null"}))
                .doneTableColumn();
        //each shop have its own product and orders so if user add items to cart and go back and open cart int different shop then cart should be different
        //so delete cart data whenever user open this activity
        try {
            deleteCartData();//before it
        }catch (Exception e){
//            Toasty.error(this, "\n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
        }

        cartCount();
        //search

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{

                    adapterProductUser.getFilter().filter(s.toString());
                    Toasty.info(ShopDetalsActivity.this, "Đang tìm kiếm", Toast.LENGTH_SHORT, true).show();

                }catch (Exception e){
                    e.printStackTrace();
//                    Toasty.error(ShopDetalsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go previous activity
                onBackPressed();
            }
        });

        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show cart dialog
                showCartDialog();
//                try {
//                    showCartDialog();
//                }catch (Exception e){
//                    Toasty.error(ShopDetalsActivity.this, "Lỗi kìa\n"+e.getMessage(), Toast.LENGTH_LONG,true).show();
//                }

            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShopDetalsActivity.this);
                builder.setTitle("Chọn thể loại")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("Tất cả")){
                                    //load all
                                    loadShopProducts();
                                }
                                else {
                                    //load filtered
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        //handle reviewsBtn click, open reviews activity

        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass shop uid to show its show
                Intent intent = new Intent(ShopDetalsActivity.this,ShopReviewsActivity.class);
                intent.putExtra("shopUid",shopUid);
                startActivity(intent);
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews() {

        DatabaseReference ref  =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding data into it
                        try{
                            ratingSum=0;
                            for (DataSnapshot ds: snapshot.getChildren()){
                                float rating = Float.parseFloat(""+ds.child("rattings").getValue()); // e.g 4.3
                                ratingSum = ratingSum + rating; //for avg rating , add(addition of) all ratings, later will divide it by number of reviews


                            }


                            long numberOfReviews = snapshot.getChildrenCount();
                            float avgRating = ratingSum/numberOfReviews;

                            ratingBar.setRating(avgRating);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
//                            Toasty.error(ShopDetalsActivity.this, "Đang lỗi\n"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void deleteCartData() {
        easyDB.deleteAllDataFromTable();//delete all records from cart
    }

    public void cartCount(){
        //keep it public so we can access in adapter
        //get cart count
        int count = easyDB.getAllData().getCount();
        if(count<=0){
            //no item in cart hide cart count textview
            cartCountTv.setVisibility(View.GONE);
        }else {
            //have items in cart,show cart count textview and set count
            cartCountTv.setVisibility(View.VISIBLE);
            cartCountTv.setText(""+count);//coucatnate with string, because we cant set integer in textview
        }
    }

    public double allTotalPrice = 0.00;
    //need to access these views in adapter so making public
    public TextView sTotalTv,dFeeTv, allTotalPriceTv;

    private void showCartDialog() {

        //init list
        cartItemsList = new ArrayList<>();

        //inflare cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart,null);
        //init views
        TextView shopNameTv = view.findViewById(R.id.shopNameTv);
        RecyclerView cartItemRv = view.findViewById(R.id.cartItemRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        dFeeTv = view.findViewById(R.id.dFeeTv);
        allTotalPriceTv = view.findViewById(R.id.totalTv);
        Button checkoutBtn = view.findViewById(R.id.checkoutBtn);

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);

        shopNameTv.setText(shopName);

//        EasyDB easyDB = EasyDB.init(this,"ITEMS_DB")
//                .setTableName("ITEMS_TABLE")
//                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
//                .addColumn(new Column("Item_PID",new String[]{"text","no null"}))
//                .addColumn(new Column("Item_Name",new String[]{"text","no null"}))
//                .addColumn(new Column("Item_Price_Each",new String[]{"text","no null"}))
//                .addColumn(new Column("Item_Price",new String[]{"text","no null"}))
//                .addColumn(new Column("Item_Quantily",new String[]{"text","no null"}))
//                .doneTableColumn();

        //get all records from db
        Cursor res = easyDB.getAllData();
        while (res.moveToNext()){
            String id = res.getString(1);
            String pId = res.getString(2);
            String name = res.getString(3);
            String price = res.getString(4);
            String cost = res.getString(5);
            String quantity = res.getString(6);

            allTotalPrice = allTotalPrice + Double.parseDouble(cost);

            ModelCartItem modelCartItem = new ModelCartItem(""+id,
                    ""+pId,
                    ""+name,
                    ""+price,
                    ""+cost,
                    ""+quantity);

            cartItemsList.add(modelCartItem);

        }
        //setup adapter
        adapterCartItem = new AdapterCartItem(this,cartItemsList);
        //set to recylerview
        cartItemRv.setAdapter(adapterCartItem);

        dFeeTv.setText("đ"+deliveryFee);
        String tt = String.format("%.2f",allTotalPrice).replaceAll(",",".");
        sTotalTv.setText("đ"+tt);
        allTotalPriceTv.setText("đ"+(allTotalPrice+Double.parseDouble(deliveryFee.replace("đ",""))));

        //show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        //reset total price on dialog dimiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.00;
            }
        });

        //place order
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //first validate delivery address
                if(myLAtitude.equals("")||myLAtitude.equals("null")|| myLongitude.equals("null")||myLongitude.equals("")){
                    //user didn't enter address in profile
                    Toasty.error(ShopDetalsActivity.this, "Vui lòng nhập địa chỉ của bạn vào hồ sơ của bạn trước khi đặt hàng", Toast.LENGTH_SHORT,true).show();
                    return;//don't procede further
                }
                if(myPhone.equals("")||myPhone.equals("null")){
                    //user didn't enter phone in profile
                    Toasty.error(ShopDetalsActivity.this, "Vui lòng nhập số diện thoại của bạn vào hồ sơ của bạn trước khi đặt hàng", Toast.LENGTH_SHORT,true).show();
                    return;//don't procede further
                }
                if(cartItemsList.size() == 0){
                    //cart list is empty
                    Toast.makeText(ShopDetalsActivity.this, "Không có mặt hàng nào trong giỏ hàng", Toast.LENGTH_SHORT).show();
                    return; //don't proceed further
                }

                submitOrder();
            }
        });

    }

    private void submitOrder() {
        //show progress dialog
        progressDialog.setMessage("Đang đặt hàng...");
        progressDialog.show();

        //for order id and order time
        final String timestamp = ""+System.currentTimeMillis();

        String cost  = allTotalPriceTv.getText().toString().trim().replace("đ","");//remove đ if contains

        //add latitude, longitude of user to each order | delete previous orders from firebase or add manually to them

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderId",""+timestamp);
        hashMap.put("orderTime",""+timestamp);
        hashMap.put("orderStatus","Đang thực thi"); //In Progress/Completed/Cancelled
        hashMap.put("orderCost",""+cost);
        hashMap.put("deliveryFee",""+deliveryFee);
        hashMap.put("orderBy",""+firebaseAuth.getUid());
        hashMap.put("orderTo",""+shopUid);
        hashMap.put("latitude",""+myLAtitude);
        hashMap.put("longitude",""+myLongitude);

        //add to db
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(shopUid).child("Orders");
        ref.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //order info added now add order items
                        for ( int i =0; i<cartItemsList.size(); i++){
                            String pId = cartItemsList.get(i).getpId();
                            String id = cartItemsList.get(i).getpId();
                            String cost = cartItemsList.get(i).getCost();
                            String name = cartItemsList.get(i).getName();
                            String price = cartItemsList.get(i).getPrice();
                            String quantity = cartItemsList.get(i).getQuantity();

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("pId",  pId);
                            hashMap1.put("name", name);
                            hashMap1.put("cost", cost);
                            hashMap1.put("price", price);
                            hashMap1.put("quantity", quantity);

                            ref.child(timestamp).child("Items").child(pId).setValue(hashMap1);

                        }
                        progressDialog.dismiss();
                        Toasty.success(ShopDetalsActivity.this, "Đã đặt hàng thành công", Toast.LENGTH_SHORT,true).show();


                        //after placing order open order details page
                        //open order details, we need to keys there, orderId, orderTo
                        Intent intent = new Intent(ShopDetalsActivity.this, OrderDetailsUsersActivity.class);
                        intent.putExtra("orderTo",shopUid);
                        intent.putExtra("orderId",timestamp);
                        startActivity(intent);//no get these values through intent on OrderDetailUserActivity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed placing order
                        progressDialog.dismiss();
                        Toasty.error(ShopDetalsActivity.this, "Lỗi kìa\n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                    }
                });

    }

    private void openMap() {
        //saddr means soruce address
        //daddr means destination address
        String address = "https://map.google.com/maps?sadd="+myLAtitude+","+ myLongitude +"&dadd="+ shopLAtitude +","+shopLongitude;
        Intent intent =new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        startActivity(intent);
    }

    private void dialPhone() {

        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(shopPhone))));
        Toasty.info(this, ""+shopPhone, Toast.LENGTH_SHORT, true).show();

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
                            myPhone = ""+ds.child("MobileNo").getValue();
                            String email = ""+ds.child("EmailId").getValue();
                            String profile = ""+ds.child("ImageURL").getValue();
                            String city = ""+ds.child("City").getValue();
                            myLAtitude = ""+ds.child("Latitude").getValue();
                            myLongitude = ""+ds.child("Longitude").getValue();


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fname =""+snapshot.child("FirstName").getValue();
                String name = ""+snapshot.child("LastName").getValue();
                shopName =""+snapshot.child("NameShop").getValue();
                shopPhone = ""+snapshot.child("MobileNo").getValue();
                shopEmail = ""+snapshot.child("EmailId").getValue();
                shopAddress = ""+snapshot.child("CompleteAddress").getValue();
                shopLAtitude =""+snapshot.child("Latitude").getValue();
                shopLongitude = ""+snapshot.child("Longitude").getValue();
                deliveryFee = ""+ snapshot.child("DeliveryFree").getValue();
                String profileImage = ""+ snapshot.child("ImageURL").getValue();
                String shopOpen = "" + snapshot.child("ImageURL").getValue();

                //set data
                shopNameTv.setText(shopName);
                emailTv.setText(shopEmail);
                deliveryFeeTv.setText("Giao hàng:"+"đ"+deliveryFee);
                addressTv.setText(shopAddress);
                phoneTv.setText(shopPhone);
                if(shopOpen.equals("true")){
                    openClosedTv.setText("Mở cửa");
                }else {
                    openClosedTv.setText("Đóng cửa");
                }
                try {
                    if (profileImage.isEmpty()) {
                        shopIv.setImageResource(R.drawable.ic_shop);
                    } else{
                        Picasso.get().load(profileImage).into(shopIv);
                    }
//                    Picasso.get().load(profileImage).into(shopIv);
                }catch (Exception e){
                    Picasso.get().load(profileImage).placeholder(R.drawable.ic_shop);
//                    Toasty.error(ShopDetalsActivity.this, "Lỗi kìa\n"+""+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadShopProducts() {
        //init list
        productsList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(shopUid).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding items
                        productsList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productsList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductUser = new AdapterProductUser(ShopDetalsActivity.this, productsList);
                        //set adapter
                        Recycle_menu.setAdapter(adapterProductUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}