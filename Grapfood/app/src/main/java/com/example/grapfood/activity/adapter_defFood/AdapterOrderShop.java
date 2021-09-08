package com.example.grapfood.activity.adapter_defFood;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapfood.R;
import com.example.grapfood.activity.FilterProductChef.FilterOrderShop;
import com.example.grapfood.activity.activity.OrderDetailsUsersActivity;
import com.example.grapfood.activity.model.ModelOrderShop;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderShop extends RecyclerView.Adapter<AdapterOrderShop.HolderOrderShop> implements Filterable {

    private Context context;
    public ArrayList<ModelOrderShop> orderShopArrayList,filterList;
    private FilterOrderShop filter;

    public AdapterOrderShop(Context context, ArrayList<ModelOrderShop> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
        this.filterList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_chef,parent,false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {
        //get data at postion
        ModelOrderShop modelOrderShop = orderShopArrayList.get(position);
        String orderId = modelOrderShop.getOrderId();
        String orderBy = modelOrderShop.getOrderBy();
        String orderCost = modelOrderShop.getOrderCost();
        String orderStatus = modelOrderShop.getOrderStatus();
        String orderTime = modelOrderShop.getOrderTime();
        String orderTo = modelOrderShop.getOrderTo();

        //load user/buyer info
        loadUserInfo(modelOrderShop,holder);
        //set data
        holder.amountTv.setText("Giá tiền đ:"+orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("Giao hàng ID:"+orderId);
        //change order status text color
        if(orderStatus.equals("Đang thực thi")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        else if(orderStatus.equals("Hoàn thành")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        }
        else if(orderStatus.equals("Bị huỷ bỏ")){
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.Red));
        }

        //conver timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));
        String formatedDate = DateFormat.format("dd/MM/yyyy",calendar).toString(); //e.g . 17/06/2021

        holder.dateTv.setText(formatedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details, we need to keys there, orderId, orderTo
                Intent intent = new Intent(context, OrderDetailsUsersActivity.class);
                intent.putExtra("orderTo",orderTo);
                intent.putExtra("orderId",orderId);
                context.startActivity(intent);//no get these values through intent on OrderDetailUserActivity
            }
        });
    }

    private void loadUserInfo(ModelOrderShop modelOrderShop, HolderOrderShop holder) {
        //to load email of the user/buyer: modelOrderShop.getOrderBy() contains uid of that user/buyer
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(modelOrderShop.getOrderBy())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String email = ""+snapshot.child("EmailId").getValue();
                        holder.emailTv.setText(email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return orderShopArrayList.size(); //return size of list/ number of record
    }

    @Override
    public Filter getFilter() {
        if(filter == null)
        {
            filter = new FilterOrderShop(this,filterList);
        }
        return filter;
    }

    //view holder class for you row_oder_chef.xml
    class HolderOrderShop extends RecyclerView.ViewHolder{

        //ui views of row_order_chef.xml
        private TextView orderIdTv, dateTv, emailTv, amountTv, statusTv;

        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);


            //init ui views
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.orderdateTv);
            emailTv = itemView.findViewById(R.id.emailTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
