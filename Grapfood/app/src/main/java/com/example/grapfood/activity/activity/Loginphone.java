package com.example.grapfood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.grapfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Loginphone extends AppCompatActivity {

    EditText num;
    Button sendotp,signinemail;
    TextView signup;
    CountryCodePicker cpp;
    FirebaseAuth Fauth;
    String number;
    ImageButton btnBN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginphone);

        num = (EditText)findViewById(R.id.number);
        sendotp = (Button)findViewById(R.id.otp);
        cpp=(CountryCodePicker)findViewById(R.id.CountryCode);
        signinemail=(Button)findViewById(R.id.btnEmail);
        signup = (TextView)findViewById(R.id.acsignup);
        //hiển thị từ gạch dưới
        signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        btnBN = (ImageButton) findViewById(R.id.backBN);
        //mouse click event
        //sự kiện click chuột
        btnBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //trả về phía trước đó
                startActivity(new Intent(Loginphone.this,MainMenu.class));
                finish();
            }
        });
        Fauth = FirebaseAuth.getInstance();

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String numberphone = num.getText().toString();
                if(!isValid(numberphone))
                {
                    return;
                }

                number=num.getText().toString().trim();
                String Phonenum = cpp.getSelectedCountryCodeWithPlus()+number;
                Intent b = new Intent(Loginphone.this,sendotp.class);

                b.putExtra("phonenumber",Phonenum);
                startActivity(b);
                finish();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Loginphone.this,Registration.class));
                finish();
            }
        });
        signinemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Loginphone.this,Login.class));
                finish();
            }
        });
    }
    public boolean isValid(String phone) {
        boolean valid = true;
        if (phone.isEmpty()) {
            num.setError("Không để trống!");
            num.requestFocus();
            valid = false;
        } else if (phone.length() != 10) {
            num.setError("Số điện thoại phải từ 10 số trở lên!");
            num.requestFocus();
            valid = false;
        } else {
            num.setError(null);
        }
        return valid;
    }
}