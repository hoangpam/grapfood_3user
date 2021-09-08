package com.example.grapfood.activity.chefFood_fragment;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.grapfood.R;
import com.example.grapfood.activity.activity.MainMenu;
import com.example.grapfood.activity.adapter_Customer.AdapterOrderUser;
import com.example.grapfood.activity.adapter_Customer.AdapterShop;
import com.example.grapfood.activity.adapter_defFood.AdapterOrderShop;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.model.ModelOrderShop;
import com.example.grapfood.activity.model.ModelOrderUser;
import com.example.grapfood.activity.model.ModelShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

public class ChefOrderFragment extends Fragment implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    FirebaseAuth firebaseAuth;

    private AdapterShop adapterShop;
    ArrayList<ModelShop> shopsList;
    private ArrayList<ModelOrderShop> orderShopArrayList;
    private AdapterOrderShop adapterOrderShop;
    private EditText searchProductEt;
    TextToSpeech.OnInitListener listener ;
    private TextView filterOrderTv;
    private ImageButton filterOrderBtn,voiceProductBtn;
    RecyclerView recyclerView;
    int count = 0;
    SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chef_orders,null);
        setHasOptionsMenu(true);
        firebaseAuth = FirebaseAuth.getInstance();

        searchProductEt =(EditText) v.findViewById(R.id.searchProductEt);
        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQUEST_CODE_SPEECH_INPUT);
        checkUser();
        filterOrderBtn = v.findViewById(R.id.filterOrderBtn);
        filterOrderTv = v.findViewById(R.id.filterOrderTv);
        filterOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //options to dispaly in dialog
                String [] options = {"Tất cả","Đang thực thi","Hoàn thành","Bị huỷ bỏ"};
                //dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Lọc đơn đặt hàng:")
                        .setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //handle item clicks
                                if(which==0){
                                 //All clicked
                                    filterOrderTv.setText("Hiển thị tất cả sản phẩm khách đặt");
                                    adapterOrderShop.getFilter().filter("");//show all orders
                                }
                                else{
                                    String optionClicked = options[which];
                                    filterOrderTv.setText("Hiển thị sản phẩm " + optionClicked + " đã đặt" );//r.g. Showing Completed Orders
                                    adapterOrderShop.getFilter().filter(optionClicked);
                                }
                            }
                        })
                        .show();
            }
        });
        voiceProductBtn =(ImageButton) v.findViewById(R.id.voiceProductBtn);



        voiceProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
                final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                clickusevoice();
                if(count==0)
                {
                    //start listening
                    speechRecognizer.startListening(speechRecognizerIntent);
                    speak();
                    speakvoice();
                    count=1;
                }
                else{
                    //stop listening
                    speechRecognizer.stopListening();
                    count =0;
                }

            }
        });
        searchProductEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try{
//                    filter(s.toString());
                    adapterOrderShop.getFilter().filter(s);
                    tts = new TextToSpeech(getContext(), listener );
                    Toasty.info(getContext(), "Đang tìm kiếm thứ bạn muốn vui lòng chờ \uD83E\uDD69", Toast.LENGTH_SHORT, true).show();

                }catch (Exception e){
                    e.printStackTrace();
//                    Toasty.info(getContext(), "\uD83E\uDD80\n"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loadAllsOrders();
        recyclerView =(RecyclerView) v.findViewById(R.id.ordersRv);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapterOrderShop = new AdapterOrderShop(getContext(),orderShopArrayList);
        recyclerView.setAdapter(adapterOrderShop);

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


                            //load only those shops that are in the city of users
                            loadShops(city);
                            loadShops(state);

                            loadAllsOrders();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toasty.error(getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT, true).show();
                    }
                });

    }

    private void loadAllsOrders() {
        //init order list
        orderShopArrayList = new ArrayList<>();

        //get orders
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderShopArrayList.clear();
                for (DataSnapshot ds:  snapshot.getChildren()){

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("Users");
                    ref.child(firebaseAuth.getUid()).child("Orders")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        orderShopArrayList.clear();
                                        for (DataSnapshot ds: snapshot.getChildren()){

                                            ModelOrderShop modelOrderShop = ds.getValue(ModelOrderShop.class);

                                            //add to list
                                            orderShopArrayList.add(modelOrderShop);
                                        }
                                        //setup adapter
                                        adapterOrderShop = new AdapterOrderShop(getContext(),orderShopArrayList);
                                        //set to recyclerview
                                        recyclerView.setAdapter(adapterOrderShop);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
                            String shopState = ""+ds.child("State").getValue();
                            //show only user city shops
                            if(shopCity.equals(myCity)){
                                shopsList.add(modelShop);
                            }
                            if(shopCity.equals(shopState)){
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
    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub
        if(status != TextToSpeech.ERROR) {
            tts.setLanguage(Locale.US);
        }
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            // tts.setPitch(5); // set pitch level

            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                Log.e("TTS", "Language is not supported");
                Toasty.error(getContext(), "Ngôn ngữ không được hỗ trợ", Toast.LENGTH_SHORT,true).show();
            } else {
                voiceProductBtn.setEnabled(true);
                speak();
            }

        } else {
            Log.e("TTS", "Initilization Failed");
            Toasty.error(getContext(), "Lỗi kìa sữa gấp\n"+ Log.e("TTS", "Initilization Failed"), Toast.LENGTH_SHORT,true).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.logout,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);
    }



    @Override
    public  void onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(requestCode == RESULT_OK && null!=data ){
//                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    //set to text
                    searchProductEt.setText(result.get(0));
                    String tex = searchProductEt.getText().toString().trim();
                    String toSpeak = tex;
                    Toasty.success(getContext(), toSpeak,Toast.LENGTH_SHORT,true).show();
                    tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                else{
                    searchProductEt.setText("Chuyển đổi thành chữ bị lỗi");
                }
//                else
//                {
//                    searchProductEt.setText("Không tìm thấy thứ bạn cần tìm");
//                    // missing data, install it
//                }
                break;
            }
        }
    }
    private void speak() {
        //intent to show speech to text didlog

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Tìm kiếm đon đặt hàng");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE_SPEECH_INPUT);
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
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toasty.success(getContext(), "Bạn đã cấp quyền truy câp giọng nói",Toast.LENGTH_SHORT,true).show();
            }
            else{
                Toasty.error(getContext(), "Bạn chưa cấp quyền truy câp giọng nói",Toast.LENGTH_SHORT,true).show();
            }
        }
    }
}
