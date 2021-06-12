package com.example.grapfood.activity.FilterProductChef;

import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.model.ModelProduct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FilterProduct extends Filter {
    private List<ModelProduct> modelProductList;
    private List<ModelProduct> modelProductFull;
    private AdapterProductChef adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProduct(AdapterProductChef adapter,ArrayList<ModelProduct> filterList){
        this.adapter = adapter;
        this.filterList = filterList;
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
            ArrayList<ModelProduct> filterModels  =new ArrayList<>();
            for (int i =0; i <filterModels.size();i++){
                //check, search by title and category
                if(filterModels.get(i).getProductTitle().toUpperCase().contains(constraint) ||
                        filterModels.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    //add filtered data to list
                    filterModels.add(filterModels.get(i));

                }
            }
//            String filterPattern = constraint.toString().toUpperCase().trim();
//
//            for (ModelProduct item: modelProductFull){
//
//                if(item.getProductTitle().toUpperCase().contains(filterPattern) || item.getProductTitle().toUpperCase().contains(filterPattern)){
//                        filteredList.add(item);
//                    }
//
//            }
//            FilterResults results1 = new FilterResults();
//            results1.values=filteredList;
            results.count = filterModels.size();
            results.values= filterModels;
        }
        else{
            //search filed empty,not searching, return original/all/complete list
            results.count = filterList.size();
            results.values= filterList;
//            filteredList.addAll(filterList);
        }



        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
//        modelProductList.clear();
//        modelProductList.addAll((List) results.values);
        adapter.productsList = (ArrayList<ModelProduct>) results.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
