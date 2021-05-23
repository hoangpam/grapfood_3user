package com.example.grapfood.activity.adapter_defFood;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grapfood.R;
import com.example.grapfood.activity.activity.Food_details;
import com.example.grapfood.activity.model.UpdateDishModel;
import com.example.grapfood.activity.object.FoodDetails;
import com.google.firebase.database.DatabaseReference;
import android.content.Context;


import java.util.List;

public class CustomerHomeAdapter extends RecyclerView.Adapter<CustomerHomeAdapter.ViewHolder> {

    private Context mContext;
    private List<FoodDetails> foodDetailsList;


    public CustomerHomeAdapter(Context mContext , List<FoodDetails>foodDetails){

        this.foodDetailsList = foodDetails;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public CustomerHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemgoimon,parent,false);
        return new CustomerHomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerHomeAdapter.ViewHolder holder, int position) {

        final FoodDetails foodDetails = foodDetailsList.get(position);
        Glide.with(mContext).load(foodDetails.getImageURL()).into(holder.imageView);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Food_details.class);
                intent.putExtra("key",foodDetails.getChefId());
                mContext.startActivity(intent);
            }
        });
        holder.Dishname.setText(foodDetails.getDishes());
        foodDetails.getRandomUID();
        foodDetails.getChefId();
        holder.Price.setText("Giá: "+foodDetails.getPrice()+" VND");

    }

    @Override
    public int getItemCount() {
        return foodDetailsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView Dishname,Price;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative_doan);
            imageView = itemView.findViewById(R.id.imgDoAn);
            Dishname = itemView.findViewById(R.id.tvTen);
            Price = itemView.findViewById(R.id.txtgia1);
        }
    }
}
