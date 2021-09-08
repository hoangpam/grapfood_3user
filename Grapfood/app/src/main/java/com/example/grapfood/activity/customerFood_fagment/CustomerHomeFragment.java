package com.example.grapfood.activity.customerFood_fagment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.adapter_Customer.AdapterProductUser;
import com.example.grapfood.activity.adapter_Customer.AdapterShop;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.bottomnavigation.CustomerFoofPanel_BottomNavigation;
import com.example.grapfood.activity.model.ModelProduct;
import com.example.grapfood.activity.model.ModelShop;
import com.example.grapfood.activity.object.Constants;
import com.example.grapfood.activity.object.FoodDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

//implements SwipeRefreshLayout.OnRefreshListener
public class CustomerHomeFragment extends Fragment {

    RecyclerView recyclerView;
    FirebaseAuth firebaseAuth;

    private AdapterShop adapterShop;
    ArrayList<ModelShop> shopsList;

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private EditText searchProductEt;
    private ImageButton filterProductBtn,voiceProductBtn;
    private TextView filterProductTv;
    private TextToSpeech tts;

    TextToSpeech.OnInitListener listener ;

    int count = 0;
    SpeechRecognizer speechRecognizer;
//    String ID;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_customerhome, null);

        firebaseAuth = FirebaseAuth.getInstance();
        searchProductEt =(EditText) v.findViewById(R.id.searchProductEt);
        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQUEST_CODE_SPEECH_INPUT);

        //search


        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
                    adapterShop.getFilter().filter(s.toString());
                    tts = new TextToSpeech(getContext(), listener );
                    Toasty.info(getContext(), "Đang tìm kiếm cửa hàng bạn muốn vui lòng chờ \n\uD83E\uDD69", Toast.LENGTH_SHORT, true).show();

                }catch (Exception e){
                    e.printStackTrace();
//                    Toasty.info(getContext(), "\uD83E\uDD80\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recyclerView = v.findViewById(R.id.recycle_menu);
        recyclerView.setHasFixedSize(true);
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.move);
        recyclerView.startAnimation(animation);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        filterProductTv =(TextView) v.findViewById(R.id.filterProductTv);

        filterProductBtn =(ImageButton) v.findViewById(R.id.filterProductBtn);

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn thể loại cửa hàng")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                filterProductTv.setText(selected);
                                if(selected.equals("Tất cả cửa hàng")){
                                    //load all
                                    loadAllShop();
                                }
                                else {
                                    //load filtered
                                    loadShops(selected);
                                }
                            }
                        })
                        .show();
            }
        });

        voiceProductBtn =(ImageButton) v.findViewById(R.id.voiceProductBtn);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        voiceProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickusevoice();
                if(count==0)
                {
                    //start listening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    speak();
                    count=1;
                }
                else{
                    //stop listening
                    speechRecognizer.stopListening();
                    count =0;
                }
            }
        });
        speakvoice();
        loadAllShop();
        checkUser();

        return v;
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(getActivity(), MainMenu.class));

        }
        else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("UID").equalTo(firebaseAuth.getUid())
                .addValueEventListener( new ValueEventListener(){

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String fname =""+ds.child("FirstName").getValue();
                            String name = ""+ds.child("LastName").getValue();
                            String phone = ""+ds.child("MobileNo").getValue();
                            String email = ""+ds.child("EmailId").getValue();
                            String profile = ""+ds.child("ImageURL").getValue();
                            String accountType = ""+ds.child("AccountType").getValue();
                            String city =""+ds.child("City").getValue();
                            String state = ""+ds.child("State").getValue();

                            loadShops(city);
                            loadShops(state);
                            loadShops(accountType);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }
    private void loadShops(final String myCity) {
        //init list
        shopsList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("AccountType").equalTo("Chef")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clear list before adding
                        shopsList.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);

                            String shopCity = ""+ds.child("City").getValue();

                            //show only user city shops
                            if(shopCity.equals(myCity)){
                                shopsList.add(modelShop);
                            }

                            //if you want to display all shops, skip the if statement and add this
                            shopsList.add(modelShop);

                        }
                        //setup adapter
                        adapterShop  =new AdapterShop(getContext(),shopsList);
                        //set adapter
                        recyclerView.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void loadAllShop() {
        shopsList = new ArrayList<>();

        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list

                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelShop modelShop = ds.getValue(ModelShop.class);
                            shopsList.add(modelShop);
                        }
                        //setup adapter
                        adapterShop  =new AdapterShop(getContext(),shopsList);
                        //set adapter
                        recyclerView.setAdapter(adapterShop);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
    private void speak() {
        //intent to show speech to text didlog

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Tìm kiếm đồ ăn thức uống yêu thích ở đây");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_to_text_info));
        //start intent
        try {
            //in there was no error
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        }catch (Exception e){
            //if there was some error
            //get message of error
//            Toasty.error(getContext(), "Lỗi kìa sữa gấp\n"+e.getMessage(), Toast.LENGTH_SHORT,true).show();
        }
    }
    @Override
    public  void onActivityResult(int requestCode,int resultCode,@androidx.annotation.Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(requestCode == RESULT_OK && null!=data ){
//                    //get text array from voice intent
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    //set to text
//                    searchProductEt.setText(result.get(0));

                    String toSpeak = searchProductEt.getText().toString();
                    Toasty.success(getContext(), toSpeak,Toast.LENGTH_SHORT,true).show();
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                else
                {
                    searchProductEt.setText("Chuyển đổi thành chữ bị lỗi");
                    // missing data, install it
//                    Intent installIntent = new Intent();
//                    installIntent.setAction(
//                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                    startActivity(installIntent);
                }
                break;
            }
        }
    }

    public void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void clickusevoice()
    {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

    }
    public void speakvoice()
    {
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);

                searchProductEt.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1)
        {
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toasty.success(getContext(), "Bạn đã cấp quyền truy câp giọng nói",Toast.LENGTH_SHORT,true).show();
            }
            else{
                Toasty.error(getContext(), "Bạn chưa cấp quyền truy câp giọng nói",Toast.LENGTH_SHORT,true).show();
            }
        }
    }
}
