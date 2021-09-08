package com.example.grapfood.activity.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.grapfood.R;
import com.example.grapfood.activity.bottomnavigation.ChefFoodPanel_BottomNavigation;
import com.example.grapfood.databinding.ActivityMainBinding;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static android.graphics.Typeface.BOLD_ITALIC;
import static com.example.grapfood.R.string.ban;


public class MainMenu extends AppCompatActivity {

    Button signinemail,signinphone,signup,googleSinginBtn,facebookSinginBtn;
    ImageView bgimage;
    private LoginButton loginButton;
    //view binding
    private ActivityMainBinding binding;
    private static final int RC_SIGN_IN = 100;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager mCallbackManager;

    FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private  static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private  static final String TAG1 = "Facebook_Authencation_TAG";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("Users");
    private Uri image_uri;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference ref;
    private AccessTokenTracker accessTokenTracker;

    double latitude = 0.0, longitude = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        final Animation zoomin = AnimationUtils.loadAnimation(this,R.anim.zoomin);
        final Animation zoomout = AnimationUtils.loadAnimation(this,R.anim.zoomout);

        bgimage=findViewById(R.id.back2);
        bgimage.setAnimation(zoomin);
        bgimage.setAnimation(zoomout);

        zoomout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(zoomin);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoomin.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bgimage.startAnimation(zoomout);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        signinemail=(Button)findViewById(R.id.SignwithEmail);
        signinphone=(Button)findViewById(R.id.SignwithPhone);
        signup=(Button)findViewById(R.id.Signup);

        facebookSinginBtn=(Button) findViewById(R.id.facebookSinginBtn);
//        loginButton = findViewById(R.id.login_button);
        facebookSinginBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Toasty.Config.getInstance()
                        .setToastTypeface(Typeface.createFromAsset(getAssets(), "font-times-new-roman.ttf"))
                        .allowQueue(false)
                        .apply();
                Toasty.custom(MainMenu.this, ban, getResources().getDrawable(R.drawable.ic_facebook),
                        android.R.color.black, android.R.color.holo_green_light, Toasty.LENGTH_SHORT, true, true).show();
//                Toasty.info(MainMenu.this, getFormattedMessage()).show();
//                Toasty.error(MainMenu.this, "Đang hoàn thiện chức năng", Toast.LENGTH_SHORT,true).show();
//                LoginManager.getInstance().logInWithReadPermissions(MainMenu.this, Arrays.asList("user_photos", "email", "public_profile", "user_posts"));
//                LoginManager.getInstance().logInWithPublishPermissions(MainMenu.this, Arrays.asList("publish_actions"));
//                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        Log.d(TAG1,"onSuccess"+ loginResult);
//                        handlerFacebookToken(loginResult.getAccessToken());
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Log.d(TAG1,"onCancel");
//                    }
//
//                    @Override
//                    public void onError(FacebookException error) {
//                        Log.d(TAG1,"onError"+error);
//                    }
//                });
//
//                authStateListener = new FirebaseAuth.AuthStateListener() {
//                    @Override
//                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                        FirebaseUser user = firebaseAuth.getCurrentUser();
//                        if(user != null){
//                            updateUI(user);
//                        }else {
//                            updateUI(null);
//                        }
//                    }
//                };
//
//                accessTokenTracker = new AccessTokenTracker() {
//                    @Override
//                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
//                        if(currentAccessToken == null){
//                            firebaseAuth.signOut();
//                        }
//                    }
//                };
//                LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
//                    @Override
//                    public void onCompleted(AccessToken accessToken) {
//                        Log.d(TAG1,"onSuccess"+ accessToken);
//                        handlerFacebookToken(accessToken);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        Log.d(TAG1,"onFailure");
//                    }
//
//                    @Override
//                    public void onError(Exception exception) {
//                        Log.d(TAG1,"onError"+exception);
//                    }
//                });
            }
        });
//        Handler mHandler = new Handler();
//        FacebookSdk.InitializeCallback initializeCallback = new FacebookSdk.InitializeCallback() {
//            @Override
//            public void onInitialized() {
//
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //UI Code Here
//                    }
//                });
//            }
//        };
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
//
//        mCallbackManager = CallbackManager.Factory.create();







        //configure the Google Signin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        googleSinginBtn = findViewById(R.id.googleSinginBtn);

        //Google SignInButton Click to begin Google SignIn
//        binding.getRoot().findViewById(R.id.googleSinginBtn)
        googleSinginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google sign in
                final ProgressDialog mDialog = new ProgressDialog(MainMenu.this);
                mDialog.setCancelable(false);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.setTitle("\uD83D\uDC37 \n Tình hình máy yếu vui lòng chờ");
                mDialog.setMessage("Đang mở thông báo ......");
                mDialog.show();
                Log.d(TAG, "onClick: begin Google SignIn && { Bắt đầu Đăng nhập Google }");
                Toasty.warning(MainMenu.this, "\uD83D\uDC37 \nTình hình máy yếu vui lòng chờ", Toast.LENGTH_LONG,true).show();
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });


        signinemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signemail = new Intent(MainMenu.this,ChooseOne.class);
                signemail.putExtra("Home","Email");
                startActivity(signemail);
                finish();
            }
        });

        signinphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signphone = new Intent(MainMenu.this,ChooseOne.class);
                signphone.putExtra("Home","Phone");
                startActivity(signphone);
                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(MainMenu.this,ChooseOne.class);
                signup.putExtra("Home","SignUp");
                startActivity(signup);
                finish();
            }
        });
    }

    private void handlerFacebookToken(AccessToken accessToken) {
        Log.d(TAG1,"handleFacebookToken"+accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainMenu.this, "Đã đang nhập thành công", Toast.LENGTH_SHORT).show();
                    Log.d(TAG1,"sign in with credential: successful");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                }else {
                    Log.d(TAG1,"sign in with credential: failure\n",task.getException());
                    Toasty.error(MainMenu.this, "Authencation Failed", Toast.LENGTH_SHORT,true).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null){
            Log.d(TAG1,"sign in whith user");
            Toasty.success(this, "tt", Toast.LENGTH_SHORT,true).show();
        }else {
            Log.d(TAG1,"errorf");
            Toasty.error(this, "Lỗi kìa", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {

        super.onStart();

//        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected  void onStop(){
        super.onStop();

//        if(authStateListener != null){
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

//        mCallbackManager.onActivityResult(requestCode,resultCode,data);

        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(....)
        if(requestCode == RC_SIGN_IN){
            Log.d(TAG,"onActivityResult: Google Signin intent result && { Kết quả ý định của Google Signin }");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //google sign in success, now auth with firebase
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }catch (Exception e){
                //failed google sign in
                Log.e(TAG,"onActivityResult: "+e.getMessage());
                ReusableCodeForAll.ShowAlert(this,"Lỗi kìa",""+e.getMessage());
            }
        }

    }

    private CharSequence getFormattedMessage() {
        final String prefix = "Formatted ";
        final String highlight = "bold italic";
        final String suffix = " text";
        SpannableStringBuilder ssb = new SpannableStringBuilder(prefix).append(highlight).append(suffix);
        int prefixLen = prefix.length();
        ssb.setSpan(new StyleSpan(BOLD_ITALIC),
                prefixLen, prefixLen + highlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private void Erro() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("\uD83D\uDC37 \n Ơ kìa lỗi");
        alerta.setMessage("Đường dẫn thất bại kiểm tra lại máy của bạn");
        alerta.setPositiveButton("OK", null);
        alerta.show();
    }
    public void UpdateDM(){

    }
    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG,"firebaseAuthWithGoogleAccount: begin firebase auth with google acccount && { Bắt đầu xác thực firebase với tài khoản google }");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

        final ProgressDialog mDialog = new ProgressDialog(MainMenu.this);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle("Tình hình mạng yếu");
        mDialog.setMessage("Đang đăng ký và tải hình đại diện lên, vui lòng đợi......");
        mDialog.show();

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG,"onSuccess: Logged In");
                        DatabaseReference DanhMuc = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("DanhMuc");
                        if(image_uri == null ){
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            String uid = firebaseUser.getUid();
                            String fname = firebaseUser.getDisplayName();
                            String emailid = firebaseUser.getEmail();
                            String phone =firebaseUser.getPhoneNumber();
                            String timestamp = ""+System.currentTimeMillis();


                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("UID", "" + uid);
                            hashMap1.put("MobileNo", ""+phone);
                            hashMap1.put("DeliveryFree", "1000");
                            hashMap1.put("LastName", "ABC");
                            hashMap1.put("City", "Hồ Chí Minh");
                            hashMap1.put("Area", "Tân Quý");//phường xã
                            hashMap1.put("Password", "123456789");
                            hashMap1.put("CompleteAddress", "123 Lê Quý Phường Tân Quý Quận Tân Phú Thành phố Hồ Chí Minh");
                            hashMap1.put("State", "Tân Phú");//Quận huyện
                            hashMap1.put("ConfirmPassword", "123456789");
                            hashMap1.put("House", "123");//Số đường
                            hashMap1.put("ImageURL", "");//url of uploadted image
                            hashMap1.put("Latitude", "0.0" );
                            hashMap1.put("Longitude", "0.0" );
                            hashMap1.put("Online", "true");
                            hashMap1.put("ShopOpen", "true");
                            hashMap1.put("AccountType","Chef");
                            hashMap1.put("FirstName", fname);
                            hashMap1.put("EmailId", emailid);
                            hashMap1.put("Timestamp", "" + timestamp);//+DateFormat.getTimeInstance().format(new Date())

                            databaseReference
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mDialog.dismiss();
                                    if(task.isSuccessful()){

                                        Toasty.success(MainMenu.this, "Đăng nhập thành công", Toast.LENGTH_SHORT,true).show();
                                        Intent Z = new Intent(MainMenu.this, ChefFoodPanel_BottomNavigation.class);

                                        startActivity(Z);
                                        finish();
                                    }
                                    else{
                                        mDialog.dismiss();
                                        ReusableCodeForAll.ShowAlert(MainMenu.this,"Kết nối của bạn đang bị lỗi",task.getException().getMessage());
                                    }

                                }
                            });




                            DanhMuc.child("-MiiJTfsEPcgklkpm4RV").child("mName").setValue("Bún/Phở");
                            DanhMuc.child("-MiiJiqPT4X3kuXq00qa").child("mName").setValue("Ăn Vặt");
                            DanhMuc.child("-MiiJrcM8xTVUua1rnII").child("mName").setValue("Đồ Uống");
                            DanhMuc.child("-MiiK-b2YhzfxzfpUDyb").child("mName").setValue("Sức Khỏe");
                            DanhMuc.child("-MiiK8fXXkqhjsMloVw0").child("mName").setValue("Cơm");
                            DanhMuc.child("-MiiKJdrYPfuHnsYDfLU").child("mName").setValue("Đặc Sản");
                            DanhMuc.child("-MiiKl8Xr0GD4N4zeSEd").child("mName").setValue("Thức ăn nhanh");
                        }else{
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                            String filePathAndName = "profile_image/" + "" + firebaseAuth.getUid();

                            image_uri = firebaseUser.getPhotoUrl();

                            final ProgressDialog progressDialog = new ProgressDialog(MainMenu.this);
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.setIndeterminate(false);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setProgress(0);
                            progressDialog.setTitle("Tình hình mạng yếu");
                            progressDialog.setMessage("Đang đăng ký và tải hình ảnh đại diện lên, vui lòng đợi tí....");
                            progressDialog.show();

                            //dành cho truy cập firebase
                            storage = FirebaseStorage.getInstance();
                            storageReference = storage.getReference();

                            ref = FirebaseStorage.getInstance().getReference(filePathAndName);

                            ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    Uri downloadImageUri = uriTask.getResult();

                                    String uid = firebaseUser.getUid();
                                    String fname = firebaseUser.getDisplayName();
                                    String emailid = firebaseUser.getEmail();
                                    String phone =firebaseUser.getPhoneNumber();
                                    String timestamp = ""+System.currentTimeMillis();

                                    HashMap<String, String> hashMap1 = new HashMap<>();
                                    hashMap1.put("UID", "" + uid);
                                    hashMap1.put("MobileNo", ""+phone);
                                    hashMap1.put("DeliveryFree", "1000");
                                    hashMap1.put("LastName", "ABC");
                                    hashMap1.put("City", "Hồ Chí Minh");
                                    hashMap1.put("Area", "Tân Quý");//phường xã
                                    hashMap1.put("Password", "123456789");
                                    hashMap1.put("CompleteAddress", "123 Lê Quý Phường Tân Quý Quận Tân Phú Thành phố Hồ Chí Minh");
                                    hashMap1.put("State", "Tân Phú");//Quận huyện
                                    hashMap1.put("ConfirmPassword", "123456789");
                                    hashMap1.put("House", "123");//Số đường
                                    hashMap1.put("Latitude", "0.0" );
                                    hashMap1.put("Longitude", "0.0" );
                                    hashMap1.put("Online", "true");
                                    hashMap1.put("ShopOpen", "true");
                                    hashMap1.put("AccountType","Chef");
                                    hashMap1.put("FirstName", fname);
                                    hashMap1.put("EmailId", emailid);
                                    hashMap1.put("ImageURL", ""+downloadImageUri);
                                    hashMap1.put("Timestamp", "" + timestamp);//+DateFormat.getTimeInstance().format(new Date())



                                    databaseReference
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(hashMap1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                progressDialog.dismiss();
                                                mDialog.dismiss();

                                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                                while (!uriTask.isSuccessful()) ;
                                                Uri downloadImageUri = uriTask.getResult();
                                                String image = ""+downloadImageUri;

                                                Toasty.success(MainMenu.this, "Đăng nhập thành công", Toast.LENGTH_SHORT,true).show();
                                                Intent Z = new Intent(MainMenu.this, ChefFoodPanel_BottomNavigation.class);
                                                startActivity(Z);
                                                finish();
                                            }
                                            else{
                                                progressDialog.dismiss();
                                                mDialog.dismiss();
                                                ReusableCodeForAll.ShowAlert(MainMenu.this,"Kết nối của bạn đang bị lỗi",task.getException().getMessage());
                                            }

                                        }
                                    });
                                    DanhMuc.child("-MiiJTfsEPcgklkpm4RV").child("mName").setValue("Bún/Phở");
                                    DanhMuc.child("-MiiJiqPT4X3kuXq00qa").child("mName").setValue("Ăn Vặt");
                                    DanhMuc.child("-MiiJrcM8xTVUua1rnII").child("mName").setValue("Đồ Uống");
                                    DanhMuc.child("-MiiK-b2YhzfxzfpUDyb").child("mName").setValue("Sức Khỏe");
                                    DanhMuc.child("-MiiK8fXXkqhjsMloVw0").child("mName").setValue("Cơm");
                                    DanhMuc.child("-MiiKJdrYPfuHnsYDfLU").child("mName").setValue("Đặc Sản");
                                    DanhMuc.child("-MiiKl8Xr0GD4N4zeSEd").child("mName").setValue("Thức ăn nhanh");
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            })
                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                                            progressDialog.setMessage("Đang tải " + (int) progress + "%");
                                            progressDialog.setCanceledOnTouchOutside(false);
                                        }
                                    });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed
                        Erro();
                        Log.e(TAG,"onFailure: Logged failed &&{ Ghi nhật ký không thành công }\n"+e.getMessage());
                        Toasty.error(MainMenu.this, "Đăng nhập không thành công\n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
                    }
                });



    }
}