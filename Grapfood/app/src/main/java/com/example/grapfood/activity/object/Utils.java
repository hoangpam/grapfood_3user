package com.example.grapfood.activity.object;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.model.ModelProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static List<ModelProduct> DataCache =new ArrayList<>();

    public static String searchString = "";

    public static void show(Context c, String message){
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public static void openActivity(Context c,Class clazz){
        Intent intent = new Intent(c, clazz);
        c.startActivity(intent);
    }

    /**
     * This method will allow us send a serialized modelProduct objec  to a specified
     *  activity
     */
    public static void sendScientistToActivity(Context c, ModelProduct modelProduct,
                                               Class clazz){
        Intent i=new Intent(c,clazz);
        i.putExtra("SCIENTIST_KEY",modelProduct);
        c.startActivity(i);
    }

    /**
     * This method will allow us receive a serialized modelProduct, deserialize it and return it,.
     */
    public  static ModelProduct receiveScientist(Intent intent, Context c){
        try {
            return (ModelProduct) intent.getSerializableExtra("SCIENTIST_KEY");
        }catch (Exception e){
            e.printStackTrace();
            show(c,"RECEIVING-SCIENTIST ERROR: "+e.getMessage());
        }
        return null;
    }

    public static void showProgressBar(ProgressBar pb){
        pb.setVisibility(View.VISIBLE);
    }
    public static void hideProgressBar(ProgressBar pb){
        pb.setVisibility(View.GONE);
    }

    public static DatabaseReference getDatabaseRefence() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void search(final AppCompatActivity a, DatabaseReference db,
                              final ProgressBar pb,
                              AdapterProductChef adapter, String searchTerm) {
        if(searchTerm != null && searchTerm.length()>0){
            char[] letters=searchTerm.toCharArray();
            String firstLetter = String.valueOf(letters[0]).toUpperCase();
            String remainingLetters = searchTerm.substring(1);
            searchTerm=firstLetter+remainingLetters;
        }

        Utils.showProgressBar(pb);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


        Query firebaseSearchQuery = db.child("Chef").child(firebaseAuth.getUid()).child("Products").child("productId").orderByChild("productTitle").startAt(searchTerm)
                .endAt(searchTerm + "uf8ff");

        firebaseSearchQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataCache.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //Now get ModelProduct Objects and populate our arraylist.
                        ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                        modelProduct.setKey(ds.getKey());
                        DataCache.add(modelProduct);
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Utils.show(a,"No item found");
                }
                Utils.hideProgressBar(pb);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE CRUD", databaseError.getMessage());
                Utils.hideProgressBar(pb);
                Utils.show(a,databaseError.getMessage());
            }
        });
    }

    public static void select(final AppCompatActivity a, DatabaseReference db,
                              final ProgressBar pb,
                              final RecyclerView rv, AdapterProductChef adapter) {
        Utils.showProgressBar(pb);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        db.child("Chef").child(firebaseAuth.getUid()).child("Products").child("productId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataCache.clear();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //Now get ModelProduct Objects and populate our arraylist.
                        ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                        modelProduct.setKey(ds.getKey());
                        DataCache.add(modelProduct);
                    }

                    adapter.notifyDataSetChanged();
                }else {
                    //No more item found
                    //Không tìm thấy mặt hàng nào nữa
                    Utils.show(a,"Không tìm thấy mặt hàng nào nữa");
                }
                Utils.hideProgressBar(pb);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FIREBASE CRUD", databaseError.getMessage());
                Utils.hideProgressBar(pb);
                Utils.show(a,databaseError.getMessage());
            }
        });
    }


}
