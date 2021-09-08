package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.adapter_defFood.AdapterReview;
import com.example.grapfood.activity.model.ModelOrderUser;
import com.example.grapfood.activity.model.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ShopReviewsActivity extends AppCompatActivity {

    //ui views
    private ImageButton backBtn;
    private ImageView profileIv;
    private TextView shopNameTv,ratingTv;
    private RatingBar ratingBar;
    private RecyclerView reviewRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelReview> reviewArrayList; //will contain list of all reviews

    private AdapterReview adapterReview;

    private String shopUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_reviews);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        ratingTv = findViewById(R.id.ratingTv);
        reviewRv = findViewById(R.id.reviewRv);

        //get shop uid from intent
        shopUid = getIntent().getStringExtra("shopUid");

        firebaseAuth = FirebaseAuth.getInstance();
        loadShopDetails();//for shop name, image
        loadReviews();//for reviews list,avg rating

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//go previous activity
            }
        });
    }

    private float ratingSum = 0;
    private void loadReviews() {
        //init list
        reviewArrayList = new ArrayList<>();


        DatabaseReference ref  =FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding data into it
                        try{

                            reviewArrayList.clear();
                            ratingSum=0;
                            for (DataSnapshot ds: snapshot.getChildren()){
                                float rating = Float.parseFloat(""+ds.child("rattings").getValue()); // e.g 4.3
                                ratingSum = ratingSum + rating; //for avg rating , add(addition of) all ratings, later will divide it by number of reviews

                                ModelReview modelReview = ds.getValue(ModelReview.class);
                                reviewArrayList.add(modelReview);
                            }
                            //setup adapter
                            adapterReview = new AdapterReview(ShopReviewsActivity.this, reviewArrayList);
                            //set to recyclerview
                            reviewRv.setAdapter(adapterReview);

                            long numberOfReviews = snapshot.getChildrenCount();
                            float avgRating = ratingSum/numberOfReviews;

                            ratingTv.setText(String.format("%.2f",avgRating + "[" + numberOfReviews +"]"));//e.g. 4.8 [10]
                            ratingBar.setRating(avgRating);
                        }
                        catch (Exception e)
                        {
                            Log.e(""+e.getMessage(),"");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadShopDetails() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = ""+ snapshot.child("NameShop").getValue();
                        String profileImage = ""+ snapshot.child("ImageURL").getValue();

                        //set data
                        shopNameTv.setText(name);
                        try{
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_user_hh).into(profileIv);
                        }catch (Exception e){
                            //if anything goes wrong setting image (exception occurs), set default image
                            Picasso.get().load(profileImage).placeholder(R.drawable.ic_add);
                            Toasty.warning(ShopReviewsActivity.this, "Cập nhật hình của bạn để xem và \n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}