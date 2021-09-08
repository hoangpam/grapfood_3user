package com.example.grapfood.activity.adapter_Customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.ShopDetalsActivity;
import com.example.grapfood.activity.model.ModelCartItem;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem> {

    private Context context;
    private ArrayList<ModelCartItem> cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_cartitem.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem,parent,false);
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCartItem holder, int position) {

        //get data
        ModelCartItem modelCartItem = cartItems.get(position);

        String id = modelCartItem.getId();
        String getpID = modelCartItem.getpId();
        String title = modelCartItem.getName();
        String cost = modelCartItem.getCost();
        String price = modelCartItem.getPrice();
        String quantity = modelCartItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(""+title);
        holder.itemPriceTv.setText(""+cost);
        holder.itemQuantityTv.setText("["+quantity+"]");//e.g.[3]
        holder.itemPriceEachTv.setText(""+price);



        //hamdle remove click listener, delete item from cart
        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //will create table if not exists, but in that case will must exit
                EasyDB easyDB = EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_PID",new String[]{"text","no null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text","no null"}))
                        .addColumn(new Column("Item_Price_Each",new String[]{"text","no null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text","no null"}))
                        .addColumn(new Column("Item_Quantily",new String[]{"text","no null"}))
                        .doneTableColumn();

                easyDB.deleteRow(1,id);//column Number 1 is Item_Id
                Toasty.success(context, "Đã huỷ khỏi giỏ hàng...", Toast.LENGTH_SHORT,true).show();


                //refresh list
                cartItems.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();


                DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
                symbols.setDecimalSeparator('.');

                DecimalFormat format = new DecimalFormat();
                format.setDecimalFormatSymbols(symbols);
                format.setMaximumFractionDigits(2);

                double tx = Double.parseDouble(((ShopDetalsActivity)context).allTotalPriceTv.getText().toString().trim().replace("đ",""));
                double totalPrice = tx - Double.parseDouble(cost.replace("đ",""));
                double deliveryFee = Double.parseDouble(((ShopDetalsActivity)context).deliveryFee.replace("đ",""));
                String tt = String.format("%.2f",totalPrice);
                String ttt = String.format("%.2f",deliveryFee);
                double sTotalPrice = Double.parseDouble(tt.replaceAll(",",".")) - Double.parseDouble(ttt.replaceAll(",","."));
                ((ShopDetalsActivity)context).allTotalPrice = 0.00;
                ((ShopDetalsActivity)context).sTotalTv.setText("đ"+String.format("%.2f",sTotalPrice));

                ((ShopDetalsActivity)context).allTotalPriceTv.setText("đ"+String.format("%.2f",Double.parseDouble(tt.replaceAll(",","."))));

                //after removing item from cart, update cart count
                ((ShopDetalsActivity)context).cartCount();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();  //return number of records
    }

    //view holder class
    class HolderCartItem extends RecyclerView.ViewHolder {

        private TextView itemTitleTv,itemPriceTv,itemPriceEachTv,itemQuantityTv,itemRemoveTv;

        //ui view of row_cartietms.xml
        public HolderCartItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);

        }
    }

}
