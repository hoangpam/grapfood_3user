package com.example.grapfood.activity.FilterProductChef;

import android.widget.Filter;

import com.example.grapfood.activity.adapter_defFood.AdapterOrderShop;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.model.ModelOrderShop;
import com.example.grapfood.activity.model.ModelProduct;

import java.util.ArrayList;

public class FilterOrderShop extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderShop> filterList,productsList;


    public FilterOrderShop(AdapterOrderShop adapter, ArrayList<ModelOrderShop> productsList){
        this.adapter = adapter;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
//        List<ModelProduct> filteredList = new ArrayList<>();
        //validate data for search query
        if(constraint != null && constraint.length() >0) {
            //search filed not empty, searching something, perform search

            //change to upper case, to make insensitive
            constraint = constraint.toString().toUpperCase();
            //store our filtered list
            ArrayList<ModelOrderShop> filterModels  =new ArrayList<>();
            for (int i =0; i <filterModels.size();i++){
                //check, search by title and category
                if(filterModels.get(i).getOrderStatus().toUpperCase().contains(constraint)){
                    //add filtered data to list
                    filterModels.add(filterModels.get(i));

                }
            }

            results.count = filterModels.size();
            results.values= filterModels;
        }
        else{
            //search filed empty,not searching, return original/all/complete list
            results.count = filterList.size();
            results.values= filterList;
//            results.count = productsList.size();
//            results.values = productsList;
        }



        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
//        modelProductList.clear();
//        modelProductList.addAll((List) results.values);
        adapter.orderShopArrayList = (ArrayList<ModelOrderShop>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
