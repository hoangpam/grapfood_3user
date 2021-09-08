package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class WriteReviewActivity extends AppCompatActivity {

    //ui views
    private ImageButton backBtn;
    private CircularImageView profileIv;
    private TextView shopNameTv;
    private RatingBar ratingBar;
    private FloatingActionButton submitBtn;
    private EditText reviewEt;

    private FirebaseAuth firebaseAuth;

    private String shopUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        //init ui views
        backBtn = findViewById(R.id.backBtn);
        profileIv = findViewById(R.id.profileIv);
        shopNameTv = findViewById(R.id.shopNameTv);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);

        firebaseAuth = FirebaseAuth.getInstance();




        //get shop uid from intent
        shopUid = getIntent().getStringExtra("shopUid");

        //load shop into: shop name, shop image
        loadShopInfo();


        //if user has written review to this shop, load it
        loadMyReview();
        //go back to previous activity
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //input data
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void loadShopInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //get shop info
                String shopName = ""+snapshot.child("NameShop").getValue();
                String shopImage = ""+snapshot.child("ImageURL").getValue();

                //set shop info to ui
                shopNameTv.setText(shopName);
                try {
                    Picasso.get().load(shopImage).placeholder(R.drawable.ic_shop).into(profileIv);
                }catch (Exception e){
                    profileIv.setImageResource(R.drawable.ic_add);
//                    Toasty.error(WriteReviewActivity.this, "Lỗi kìa sửa gấp\n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMyReview() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            //my review is available in shop

                            //get review details
                            String uid = ""+snapshot.child("uid").getValue();
                            String rattings = ""+snapshot.child("rattings").getValue();
                            String review = ""+snapshot.child("review").getValue();
                            String timestamp = ""+snapshot.child("timestamp").getValue();

                            //set review details to our ui
                            float myRatting = Float.parseFloat(rattings);
                            ratingBar.setRating(myRatting);
                            reviewEt.setText(review);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void inputData() {
        String rating = ""+ratingBar.getRating();
        String review = reviewEt.getText().toString().trim();

        //for time of review
        String timestamp = ""+System.currentTimeMillis();

        //setup data in hashmap
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+firebaseAuth.getUid());
        hashMap.put("rattings",""+rating); //e.g 4.6
        hashMap.put("review",""+review); //e.g Good service
        hashMap.put("timestamp",""+timestamp);

        //put to db: DB > Users > Ratings > ShopUid > Ratings
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(shopUid).child("Ratings").child(firebaseAuth.getUid()).updateChildren(hashMap)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //review added to db
                    Toasty.success(WriteReviewActivity.this, "Đánh giá được xuất bản thành công", Toast.LENGTH_SHORT,true).show();
                }
            })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed adding review
//                        Toasty.error(WriteReviewActivity.this, "Lỗi kìa \n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                    }
                });
    }
}