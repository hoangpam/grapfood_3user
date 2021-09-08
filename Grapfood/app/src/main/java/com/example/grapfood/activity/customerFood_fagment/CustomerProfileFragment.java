package com.example.grapfood.activity.customerFood_fagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.activity.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.annotation.Nullable;

public class CustomerProfileFragment  extends Fragment implements View.OnClickListener {

    RelativeLayout rlDangXuat;

    ImageButton btnsetting;
    TextView ten;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerprofile, null);


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }
}
