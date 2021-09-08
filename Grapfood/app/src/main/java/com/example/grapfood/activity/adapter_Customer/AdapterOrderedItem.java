package com.example.grapfood.activity.adapter_Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapfood.R;
import com.example.grapfood.activity.model.ModelCartItem;
import com.example.grapfood.activity.model.ModelOrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelOrderedItem> cartItemsArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderedItem> cartItemsArrayList) {
        this.context = context;
        this.cartItemsArrayList = cartItemsArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       //inflate layout
       View view = LayoutInflater.from(context).inflate(R.layout.row_orderditem,parent,false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {


        //get data
        ModelOrderedItem modelOrderedItem = cartItemsArrayList.get(position);
        String getpId  = modelOrderedItem.getpId();
        String name  = modelOrderedItem.getName();
        String cost  = modelOrderedItem.getCost();
        String price  = modelOrderedItem.getPrice();
        String quantity  = modelOrderedItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceTv.setText("đ"+cost);
        holder.itemPriceEachTv.setText("đ"+price);
        holder.itemQuantityTv.setText("["+quantity+"]");



    }

    @Override
    public int getItemCount() {
        return cartItemsArrayList.size();//return list size
    }

    //view holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //views of row_ordereditem.xml
        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv;
        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);


        }
    }
}
