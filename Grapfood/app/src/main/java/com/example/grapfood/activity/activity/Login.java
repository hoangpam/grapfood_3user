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
import com.example.grapfood.activity.model.ModelShop;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

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
            //g???ch ch??n b??n d?????i
            Forgotpassword.setPaintFlags(Forgotpassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            signup.setPaintFlags(signup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            progressDialog = new ProgressDialog(this);
//            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GREEN));
            progressDialog.getWindow().setIcon(R.drawable.common_google_signin_btn_icon_dark);

            progressDialog.setTitle("\uD83D\uDC37 \n T??nh h??nh m???ng y???u");
            progressDialog.setCanceledOnTouchOutside(false);


            btnBN = (ImageButton) findViewById(R.id.backBN);
            //mouse click event
            //s??? ki???n click chu???t
            btnBN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tr??? v??? ph??a tr?????c ????
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
                        mDialog.setTitle("\uD83D\uDC37 T??nh h??nh m???ng y???u");
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.setCancelable(false);
                        mDialog.setMessage("??ang ????ng nh???p Vui l??ng ?????i.......");
                        mDialog.show();

                        Fauth.signInWithEmailAndPassword(emailid,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){
                                    mDialog.dismiss();

                                    if(Fauth.getCurrentUser().isEmailVerified()){
                                        mDialog.dismiss();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Online","true");


                                        //update value to db
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                        ref.child(Fauth.getUid()).updateChildren(hashMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toasty.success(Login.this, "B???n(KH) ??ang online!", Toast.LENGTH_SHORT, true).show();

//                                                        Toast.makeText(Cheflogin.this, "Shop ??ang online", Toast.LENGTH_SHORT).show();
                                                        mDialog.dismiss();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        mDialog.dismiss();
                                                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                        Toasty.success(Login.this, "\uD83D\uDC37 \nXin ch??c m???ng! B???n ???? ????ng nh???p th??nh c??ng!", Toast.LENGTH_SHORT, true).show();

//                                        Toast.makeText(Login.this, "Xin ch??c m???ng! B???n ???? ????ng nh???p th??nh c??ng", Toast.LENGTH_SHORT).show();
                                        Intent Z = new Intent(Login.this, CustomerFoofPanel_BottomNavigation.class);
                                        startActivity(Z);
                                        finish();

                                    }else{
                                        ReusableCodeForAll.ShowAlert(Login.this,"X??c minh kh??ng ho??n th??nh","B???n ch??a x??c minh Email c???a m??nh. Vui l??ng ki???m tra Email");

                                    }
                                }else{
                                    mDialog.dismiss();
                                    ReusableCodeForAll.ShowAlert(Login.this,"K???t n???i c???a b???n ??ang b??? l???i",task.getException().getMessage());
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
            Toasty.error(this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();

//            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private void showchoiseForgotPassDialog()
    {
        //options to display in dialog
        //c??c t??y ch???n ????? hi???n th??? trong h???p tho???i
        String[] options = {"Qua Email", "Qua s??? ??i???n tho???i"};//camara, gallery
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Vui l??ng ch???n c??ch th???c ????? l???y l???i m???t kh???u")//pick image
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
        builder.setTitle("Qu??n m???t kh???u");
        builder.setMessage("Nh???p m?? b???o m???t c???a b???n");
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark);


        LayoutInflater inflater = this.getLayoutInflater();
        View forgotPassView = inflater.inflate(R.layout.activity_forgot_password, null);

        builder.setView(forgotPassView);
        builder.setIcon(R.drawable.common_google_signin_btn_icon_dark);
        final EditText edPhone = forgotPassView.findViewById(R.id.edtPhone);

        Email = edPhone.getText().toString().trim();

        builder.setPositiveButton("X??c nh???n", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!Patterns.EMAIL_ADDRESS.matcher(Email).matches())
                {
//                    Toast.makeText(Login.this, "C??ch th???c nh???p Email c???a b???n b??? sai", Toast.LENGTH_SHORT).show();
                    Toasty.error(Login.this, "C??ch th???c nh???p Email c???a b???n b??? sai", Toast.LENGTH_SHORT, true).show();

                    return;
                }
                progressDialog.setMessage("??ang g???i m?? ?????i m???t kh???u sang Email c???a b???n\nVui l??ng ki???m tra h??m th?? Email ???? g???i ch??a\nN???u ch??a th?? b???n h??y ch??? v??i ph??t ????? h??? th???ng ??ang trong ti???n tr??nh g???i cho b???n..");
                progressDialog.show();

                Fauth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    //instructions sent
                                    //h?????ng d???n ???????c g???i ????? reset l???i password c???a b???n
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    //Password reset instructions sent to your email
//                                    Toast.makeText(Login.this, "???? g???i link ?????t l???i m???t kh???u ?????n Email c???a b???n", Toast.LENGTH_SHORT).show();
                                    Toasty.success(Login.this, "???? g???i link ?????t l???i m???t kh???u ?????n Email c???a b???n!", Toast.LENGTH_SHORT, true).show();

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed sending instructions
                                //kh??ng g???i ???????c h?????ng d???n ????? reset l???i password c???a b???n
                                progressDialog.dismiss();
                                dialog.dismiss();
                                ReusableCodeForAll.ShowAlert(Login.this,"L???i k??a","Ch??a c?? t??i kho???n m?? ????i qu??n v???i ch??? kh??ng");
                                Toasty.error(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                            }
                        });


            }
        });
        builder.setNegativeButton("Hu???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    //qu??n m???t kh???u theo h??nh th???c s??? ??i???n tho???i
    private void showForgotPassDialog() {

        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(Login.this);
        builder.setTitle("Qu??n m???t kh???u");
        builder.setMessage("Nh???p m?? b???o m???t c???a b???n");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgotPassView = inflater.inflate(R.layout.forgot_password_layout,null);

        builder.setView(forgotPassView);
        builder.setIcon(R.drawable.ic_security_black_24dp);
        final EditText edPhone = forgotPassView.findViewById(R.id.edtPhone);


        builder.setPositiveButton("X??c nh???n", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Fauth.verifyPasswordResetCode(edPhone.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                //instructions sent
                                //h?????ng d???n ???????c g???i ????? reset l???i password c???a b???n
                                dialog.dismiss();
                                //Password reset instructions sent to your email
                                FirebaseUser user = Fauth.getCurrentUser();
                                if(user != null){
                                    table_User.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            ModelShop user = dataSnapshot.child(edPhone.getText().toString()).getValue(ModelShop.class);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else {
                                    ReusableCodeForAll.ShowAlert(Login.this,"L???i k??a","Ch??a c?? t??i kho???n m?? ????i qu??n v???i ch??? kh??ng");
                                }
//                                Toast.makeText(Cheflogin.this, "???? g???i m?? code ?????t l???i m???t kh???u ?????n s??? "+" " +" c???a b???n", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }
        });
        builder.setNegativeButton("Hu???", new DialogInterface.OnClickListener() {
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
            email.setError("Email b???t bu???c nh???p");
        }else{
            if(emailid.matches(emailpattern)){
                isvalidemail=true;
            }else{
                email.setErrorEnabled(true);
                email.setError("?????a ch??? Email kh??ng h???p l???");
            }
        }
        if(TextUtils.isEmpty(pwd)){

            pass.setErrorEnabled(true);
            pass.setError("M???t kh???u b???t bu???c nh???p");
        }else{
            isvalidpassword=true;
        }
        isvalid=(isvalidemail && isvalidpassword)?true:false;
        return isvalid;
    }

}