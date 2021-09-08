package com.example.grapfood.activity.adapter_Customer;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.circularimageview.CircularImageView;
import com.example.grapfood.R;
import com.example.grapfood.activity.FilterProductUser.FilterProductUser;
import com.example.grapfood.activity.activity.ShopDetalsActivity;
import com.example.grapfood.activity.model.ModelProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUser> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflare layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_user,parent,false);
        return new HolderProductUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUser holder, int position) {
        //get data
        ModelProduct modelProduct = productsList.get(position);
        String discountAvailable = modelProduct.getDiscountAvailable();
        String discountNote= modelProduct.getDiscountNote();
        String discountPrice= modelProduct.getDiscountPrice();
        String productCategory = modelProduct.getProductCategory();
        String originalPrice = modelProduct.getOriginalPrice();
        String productDesciptions = modelProduct.getProductDesciptions();
        String productTitle = modelProduct.getProductTitle();
        String productQuanlity = modelProduct.getProductQuanlity();
        String productId = modelProduct.getProductId();
        String timestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.discountNoteTv.setText(discountNote);
        holder.descriptionTv.setText(productDesciptions);
        holder.originalPriceTv.setText("đ"+originalPrice);
        holder.discountPriceTv.setText("đ"+discountPrice );
        if(discountAvailable.equals("true")){
            //product is on discount
            holder.discountPriceTv.setVisibility(View.VISIBLE);
            holder.discountNoteTv.setVisibility(View.VISIBLE);
            holder.originalPriceTv.setPaintFlags(holder.originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            //product is not on discount
            holder.discountPriceTv.setVisibility(View.GONE);
            holder.discountNoteTv.setVisibility(View.GONE);
            holder.originalPriceTv.setPaintFlags(0);
        }
        try{
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_add).into(holder.productIconIv);
        }catch (Exception e){
            holder.productIconIv.setImageResource(R.drawable.ic_add);
            Toasty.error(context, "This is an error toast.\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
        }

        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product to cart
                showQuantityDialog(modelProduct);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
            }
        });


    }
    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view  = LayoutInflater.from(context).inflate(R.layout.dialog_quantity,null);
        //init layout views
        CircularImageView productIv  =view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView discountNoteTv = view.findViewById(R.id.discountNoteTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView priceDiscountedTv = view.findViewById(R.id.priceDiscountedTv);
        TextView finalPriceTv = view.findViewById(R.id.finalPriceTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView quantilyTv = view.findViewById(R.id.quantilyTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        String productID = modelProduct.getProductId();
        String title = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuanlity();
        String description = modelProduct.getProductDesciptions();
        String discountNote = modelProduct.getDiscountNote();
        String image = modelProduct.getProductIcon();

        final String price;
        if(modelProduct.getDiscountAvailable().equals("true")){
            //product have discount
            price = modelProduct.getDiscountPrice();
            discountNoteTv.setVisibility(View.VISIBLE);
            originalPriceTv.setPaintFlags(originalPriceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else {
            //product don't have discount
            discountNoteTv.setVisibility(View.GONE);
            priceDiscountedTv.setVisibility(View.GONE);
            price = modelProduct.getOriginalPrice();
        }

        cost = Double.parseDouble(price.replaceAll("đ",""));
        finalCost = Double.parseDouble(price.replaceAll("đ",""));

        quantity = 1;

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        //set  data
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_shop).into(productIv);
        }catch (Exception e){
            Picasso.get().load(image).placeholder(R.drawable.ic_shop);
            Toasty.error(context, ""+e.getMessage(), Toast.LENGTH_SHORT,true).show();
        }

        titleTv.setText(title);
        pQuantityTv.setText(productQuantity);
        descriptionTv.setText(description);
        quantilyTv.setText(""+quantity);
        originalPriceTv.setText("đ"+modelProduct.getOriginalPrice());
        priceDiscountedTv.setText("đ"+modelProduct.getDiscountPrice());
        finalPriceTv.setText("đ"+finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        //increase quantily of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finalPriceTv.setText("đ"+finalCost);
                quantilyTv.setText(""+quantity);
            }
        });

        //decrease quantily of the product, only if quanlity is > 1

        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity > 1){
                    finalCost = finalCost - cost;
                    quantity --;

                    finalPriceTv.setText("đ"+finalCost);
                    quantilyTv.setText(""+quantity);
                }
            }
        });
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = price;
                String totalPrice = finalPriceTv.getText().toString().trim().replace("đ","");
                String quantity = quantilyTv.getText().toString().trim();

                //add to db(SQLite)
                addToCart(productID,title,priceEach,totalPrice,quantity);

                dialog.dismiss();
            }
        });
    }


    private int itemId = 1;
    private void addToCart(String productID, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","no null"}))
                .addColumn(new Column("Item_Quantily",new String[]{"text","no null"}))
                .doneTableColumn();

        Boolean b = easyDB.addData("Item_Id",itemId)
                .addData("Item_PID",productID)
                .addData("Item_Name",title)
                .addData("Item_Price_Each",priceEach)
                .addData("Item_Price",price)
                .addData("Item_Quantily",quantity)
                .doneDataAdding();

        Toasty.success(context, "Đã thêm vào giỏ hàng...", Toast.LENGTH_SHORT).show();


        //update cart count
        ((ShopDetalsActivity)context).cartCount();
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new FilterProductUser(this,filterList);
        }
        return filter;
    }

    class HolderProductUser extends RecyclerView.ViewHolder{

        //ui views
        private ImageView productIconIv,nextIv;
        private TextView discountNoteTv,titleTv,descriptionTv,addToCartTv,discountPriceTv,originalPriceTv;

        public HolderProductUser(@NonNull View item){
            super(item);

            //init ui views
            productIconIv = item.findViewById(R.id.productIconIv);
            discountNoteTv = item.findViewById(R.id.discountNoteTv);
            titleTv = item.findViewById(R.id.titleTv);
            addToCartTv = item.findViewById(R.id.addToCartTv);
            discountPriceTv = item.findViewById(R.id.discountPriceTv);
            originalPriceTv = item.findViewById(R.id.originalPriceTv);
            descriptionTv = item.findViewById(R.id.descriptionTv);
            nextIv = item.findViewById(R.id.nextIv);
        }
    }

}
