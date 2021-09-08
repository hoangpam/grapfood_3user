package com.example.grapfood.activity.data;

public interface ItemClickListener<T> {
    void onClickItem(int position, T item);
}
