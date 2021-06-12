package com.example.grapfood.activity.chefFood_fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.adapter_defFood.ChefHomeAdapter;
import com.example.grapfood.activity.bottomnavigation.ChefFoodPanel_BottomNavigation;
import com.example.grapfood.activity.model.ModelProduct;
import com.example.grapfood.activity.model.UpdateDishModel;
import com.example.grapfood.activity.object.Chef;
import com.example.grapfood.activity.object.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChefHomeFragment  extends Fragment {

    private RelativeLayout productRL,allproductRL;
    RecyclerView recyclerView;
    private List<UpdateDishModel> updateDishModelList;
    private EditText searchProductEt;
    private ImageButton filterProductBtn;
    private TextView filterProductTv;

    DatabaseReference dataa;
    private String State,City,Area;
    FirebaseAuth firebaseAuth;
    List<ModelProduct> list;
    private ArrayList<ModelProduct> productList;
    private AdapterProductChef adapterProductChef;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chef_home,null);
        setHasOptionsMenu(true);


        firebaseAuth = FirebaseAuth.getInstance();

        allproductRL =(RelativeLayout) v.findViewById(R.id.allproductRL);
        productRL =(RelativeLayout) v.findViewById(R.id.productRL);

        searchProductEt =(EditText) v.findViewById(R.id.searchProductEt);

        //search

        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                progressDialog = new ProgressDialog(getContext());
//                progressDialog.setTitle("Tình hình");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.setMessage("Đang tìm kiếm chờ xíu ...");
//                progressDialog.setIndeterminate(false);
//                progressDialog.setMax(100);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                progressDialog.setCancelable(true);
//                progressDialog.show();
                try{
                    filter(s.toString());
                    Toast.makeText(getContext(), "Đang tìm kiếm", Toast.LENGTH_SHORT).show();
//                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        filterProductTv =(TextView) v.findViewById(R.id.filterProductTv);

        filterProductBtn =(ImageButton) v.findViewById(R.id.filterProductBtn);

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn thể loại")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("Tất cả")){
                                    //load all
                                    loadAllProduct();
                                }
                                else {
                                    //load filtered
                                    loadFilteredProduct(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        loadAllProduct();
        recyclerView =(RecyclerView) v.findViewById(R.id.Recycle_menu);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapterProductChef = new AdapterProductChef(getContext(),productList);
        recyclerView.setAdapter(adapterProductChef);
//        updateDishModelList = new ArrayList<>();
//        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        dataa = FirebaseDatabase.getInstance().getReference("Chef").child(userid);
//        dataa.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Chef cheff = snapshot.getValue(Chef.class);
//                State = cheff.getState();
//                City = cheff.getCity();
//                Area = cheff.getArea();
//                chefDishes();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


        return v;
    }
    private void filter(String text) {
        ArrayList<ModelProduct> filteredList = new ArrayList<>();
        for (ModelProduct item : productList) {
            if (item.getProductTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapterProductChef.filterList(filteredList);
    }
    private void loadFilteredProduct(final String selected) {
        productList = new ArrayList<>();

        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chef");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();

                            //if selected category mathes product category then add in list
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }


                        }
                        //setup adapter
                        adapterProductChef  =new AdapterProductChef(getContext(),productList);
                        //set adapter
                        recyclerView.setAdapter(adapterProductChef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProduct() {
        productList = new ArrayList<>();

        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chef");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductChef  =new AdapterProductChef(getContext(),productList);
                        //set adapter
                        recyclerView.setAdapter(adapterProductChef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void chefDishes() {
//
//        String useridd = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("FoodDetails").child(State).child(City).child(Area).child(useridd);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                updateDishModelList.clear();
//                for(DataSnapshot snapshot1:snapshot.getChildren()){
//                    UpdateDishModel updateDishModel = snapshot1.getValue(UpdateDishModel.class);
//                    updateDishModelList.add(updateDishModel);
//                }
//                adapter = new ChefHomeAdapter(getContext(),updateDishModelList);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

//        int idd = item.getItemId();
//        if(idd == R.id.LOGOUT){
//            Logout();
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

//    private void Logout() {
//
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(getActivity(), MainMenu.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//    }
}