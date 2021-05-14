package com.example.grapfood.activity.deliveryFood_fagment;

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

public class DeliveryProfile extends Fragment implements View.OnClickListener {

    RelativeLayout rlDangXuat;

    ImageButton btnsetting;
    TextView ten;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_deliveryprofile, null);
        getActivity().setTitle("Hồ sơ");
        rlDangXuat = v.findViewById(R.id.it_logout);
        rlDangXuat.setOnClickListener(this);

        btnsetting =v.findViewById(R.id.imb_setting);
        btnsetting.setOnClickListener(this);
        ten =v.findViewById(R.id.tv_tenHienThi);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.it_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), MainMenu.class));
                break;
            case R.id.imb_setting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.imv_avatar:
                break;
        }
    }
}
