package com.example.grapfood.activity.deliveryFood_fagment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.grapfood.R;

import javax.annotation.Nullable;

public class DeliveryShipOrders extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deliveryshiporders, null);
        getActivity().setTitle("Giao hàng");
        return v;
    }
}
