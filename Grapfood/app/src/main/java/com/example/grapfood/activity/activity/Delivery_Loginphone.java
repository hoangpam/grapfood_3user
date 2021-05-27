package com.example.grapfood.activity.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Delivery_Loginphone extends AppCompatActivity {

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
        setContentView(R.layout.activity_delivery__loginphone);

        num = (EditText)findViewById(R.id.Dphonenumber);
        sendotp = (Button)findViewById(R.id.Sendotp);
        cpp=(CountryCodePicker)findViewById(R.id.countrycode);
        signinemail=(Button)findViewById(R.id.DbtnEmail);
        signup = (TextView)findViewById(R.id.Signupif);
        btnBN = (ImageButton) findViewById(R.id.backBN);
        //mouse click event
        //sự kiện click chuột
        btnBN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //trả về phía trước đó
                startActivity(new Intent(Delivery_Loginphone.this,MainMenu.class));
                finish();
            }
        });

        Fauth = FirebaseAuth.getInstance();

        sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number=num.getText().toString().trim();
                String Phonenum = cpp.getSelectedCountryCodeWithPlus()+number;
                Intent b = new Intent(Delivery_Loginphone.this,Delivery_sendotp.class);

                b.putExtra("phonenumber",Phonenum);
                startActivity(b);
                finish();

            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Delivery_Loginphone.this,Delivery_Registration.class));
                finish();
            }
        });
        signinemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Delivery_Loginphone.this,Delivery_Login.class));
                finish();
            }
        });


    }
}