package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.bottomnavigation.CustomerFoofPanel_BottomNavigation;
import com.example.grapfood.activity.object.Chef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    TextInputLayout email,pass;
    Button Signin,Signinphone;
    TextView Forgotpassword , signup;
    FirebaseAuth Fauth;
    String emailid,pwd;
    DatabaseReference table_User;
    ImageButton btnBN;
    String Email;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try{

            email = (TextInputLayout)findViewById(R.id.Lemail);
            pass = (TextInputLayout)findViewById(R.id.Lpassword);
            Signin = (Button)findViewById(R.id.button4);
            signup = (TextView) findViewById(R.id.textView4);
            Forgotpassword = (TextView)findViewById(R.id.forgotpass);
            Signinphone = (Button)findViewById(R.id.btnphone);
            //gạch chân bên dưới
            Forgotpassword.setPaintFlags(Forgotpassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            progressDialog = new ProgressDialog(this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GREEN));
            progressDialog.getWindow().setIcon(R.drawable.common_google_signin_btn_icon_dark);

            progressDialog.setTitle("Tình hình");
            progressDialog.setCanceledOnTouchOutside(false);


            btnBN = (ImageButton) findViewById(R.id.backBN);
            //mouse click event
            //sự kiện click chuột
            btnBN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //trả về phía trước đó
                    startActivity(new Intent(Login.this,MainMenu.class));
                    finish();
                }
            });

            Fauth = FirebaseAuth.getInstance();

            Signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    emailid = email.getEditText().getText().toString().trim();
                    pwd = pass.getEditText().getText().toString().trim();

                    if(isValid()){

                        final ProgressDialog mDialog = new ProgressDialog(Login.this);
                        mDialog.setTitle("Tình hình");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage("Đang đăng nhập Vui lòng đợi.......");
                        mDialog.show();

                        Fauth.signInWithEmailAndPassword(emailid,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    mDialog.dismiss();

                                    if(Fauth.getCurrentUser().isEmailVerified()){
                                        mDialog.dismiss();
                                        Toast.makeText(Login.this, "Xin chúc mừng! Bạn đã đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                        Intent Z = new Intent(Login.this, CustomerFoofPanel_BottomNavigation.class);
                                        startActivity(Z);
                                        finish();

                                    }else{
                                        ReusableCodeForAll.ShowAlert(Login.this,"Xác minh không hoàn thành","Bạn chưa xác minh Email của mình. Vui lòng kiểm tra Email");

                                    }
                                }else{
                                    mDialog.dismiss();
                                    ReusableCodeForAll.ShowAlert(Login.this,"Kết nối của bạn đang bị lỗi",task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this,MainMenu.class));
                    finish();
                }
            });
            Forgotpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showchoiseForgotPassDialog();

                }
            });
            Signinphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Login.this,Loginphone.class));
                    finish();
                }
            });
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private void showchoiseForgotPassDialog()
    {
        //options to display in dialog
        //các tùy chọn để hiển thị trong hộp thoại
        String[] options = {"Qua Email", "Qua số điện thoại"};//camara, gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vui lòng chọn cách thức để lấy lại mật khẩu")//pick image
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {

                            showForgotPassDialogEmail();
                        } else {
                            showForgotPassDialog();
                        }
                    }
                })
                .show();
    }

    private  void showForgotPassDialogEmail() {
//        Context context = new ContextThemeWrapper(Login.this, R.style.AppTheme2);
//        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context,R.style.MaterialAlertDialog_rounded);

        final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập mã bảo mật của bạn");
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark);


        LayoutInflater inflater = this.getLayoutInflater();
        View forgotPassView = inflater.inflate(R.layout.activity_forgot_password, null);

        builder.setView(forgotPassView);
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark);
        final EditText edPhone = forgotPassView.findViewById(R.id.edtPhone);

        Email = edPhone.getText().toString().trim();

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
                    Toast.makeText(Login.this, "Cách thức nhập Email của bạn bị sai", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog.setMessage("Đang gửi mã đổi mật khẩu sang Email của bạn\nVui lòng kiểm tra hòm thư Email đã gửi chưa\nNếu chưa thì bạn hãy chờ vài phút để hệ thống đang trong tiến trình gửi cho bạn..");
                progressDialog.show();

                Fauth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    //instructions sent
                                    //hướng dẫn được gửi để reset lại password của bạn
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    //Password reset instructions sent to your email
                                    Toast.makeText(Login.this, "Đã gửi link đặt lại mật khẩu đến Email của bạn", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed sending instructions
                                //không gửi được hướng dẫn để reset lại password của bạn
                                progressDialog.dismiss();
                                dialog.dismiss();
                                ReusableCodeForAll.ShowAlert(Login.this,"Lỗi kìa","Chưa có tài khoản mà đòi quên với chả không");
                                Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //quên mật khẩu theo hình thức số điện thoại
    private void showForgotPassDialog() {
//        Context context = new ContextThemeWrapper(Login.this, R.style.AppTheme2);
//        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context,R.style.MaterialAlertDialog_rounded);

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Login.this);
        builder.setTitle("Quên mật khẩu");
        builder.setMessage("Nhập mã bảo mật của bạn");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgotPassView = inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgotPassView);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        final EditText edPhone = forgotPassView.findViewById(R.id.edtPhone);


        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Fauth.verifyPasswordResetCode(edPhone.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                //instructions sent
                                //hướng dẫn được gửi để reset lại password của bạn
                                dialog.dismiss();
                                //Password reset instructions sent to your email
                                FirebaseUser user = Fauth.getCurrentUser();
                                if(user != null){
                                    table_User.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Chef user = dataSnapshot.child(edPhone.getText().toString()).getValue(Chef.class);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else {
                                    ReusableCodeForAll.ShowAlert(Login.this,"Lỗi kìa","Chưa có tài khoản mà đòi quên với chả không");
                                }
//                                Toast.makeText(Cheflogin.this, "Đã gửi mã code đặt lại mật khẩu đến số "+" " +" của bạn", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
//                dialog.dismiss();
//                FirebaseUser user = Fauth.getCurrentUser();
//                if(user != null){
//                    table_User.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Chef user = dataSnapshot.child(edPhone.getText().toString()).getValue(Chef.class);
//                            if (user.getSecureCode().equals(edSecureCode.getText().toString())){
//                                Toast.makeText(Cheflogin.this, "Mật khẩu của bạn "+ user.getPassword(), Toast.LENGTH_SHORT).show();
//                            }else {
//                                Toast.makeText(Cheflogin.this, "Mã bảo mật sai !", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//
//                        }
//                    });
//                }else {
//                    ReusableCodeForAll.ShowAlert(Cheflogin.this,"Lỗi kìa","Chưa có tài khoản mà đòi quên với chả không");
//                }
            }
        });
        builder.setNegativeButton("Huỷ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    String emailpattern  = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

    public boolean isValid(){

        email.setErrorEnabled(false);
        email.setError("");
        pass.setErrorEnabled(false);
        pass.setError("");

        boolean isvalid=false,isvalidemail=false,isvalidpassword=false;
        if(TextUtils.isEmpty(emailid)){
            email.setErrorEnabled(true);
            email.setError("Email bắt buộc nhập");
        }else{
            if(emailid.matches(emailpattern)){
                isvalidemail=true;
            }else{
                email.setErrorEnabled(true);
                email.setError("Địa chỉ Email không hợp lệ");
            }
        }
        if(TextUtils.isEmpty(pwd)){

            pass.setErrorEnabled(true);
            pass.setError("Mật khẩu bắt buộc nhập");
        }else{
            isvalidpassword=true;
        }
        isvalid=(isvalidemail && isvalidpassword)?true:false;
        return isvalid;
    }

}