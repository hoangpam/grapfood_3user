package com.example.grapfood.activity.chefFood_fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

import com.example.grapfood.R;
import com.example.grapfood.activity.adapter_defFood.AdapterProductChef;
import com.example.grapfood.activity.data.LanguageDetailsChecker;
import com.example.grapfood.activity.model.ModelProduct;
import com.example.grapfood.activity.object.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class ChefHomeFragment  extends Fragment implements TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private RelativeLayout productRL,allproductRL;
    RecyclerView recyclerView;
    private EditText searchProductEt;
    private ImageButton filterProductBtn,voiceProductBtn;
    private TextView filterProductTv;

    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    DatabaseReference dataa;
    private String State,City,Area;
    FirebaseAuth firebaseAuth;
    List<ModelProduct> list;
    private ArrayList<ModelProduct> productList;
    private AdapterProductChef adapterProductChef;
    ProgressDialog progressDialog;
    TextToSpeech.OnInitListener listener ;
    int count = 0;
    SpeechRecognizer speechRecognizer;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chef_home,null);
        setHasOptionsMenu(true);

        firebaseAuth = FirebaseAuth.getInstance();

        allproductRL =(RelativeLayout) v.findViewById(R.id.allproductRL);
        productRL =(RelativeLayout) v.findViewById(R.id.productRL);

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
//                    filter(s.toString());
                    adapterProductChef.getFilter().filter(s);
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


        filterProductTv =(TextView) v.findViewById(R.id.filterProductTv);

        filterProductBtn =(ImageButton) v.findViewById(R.id.filterProductBtn);

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Chọn thể loại đồ ăn")
                        .setItems(Constants.productCategory1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategory1[which];
                                String [] option = {

                                        "Đặc sản",//
                                        "Ăn vặt",//
                                        "Bún/Phở" ,//
                                        "Đồ Uống",//
                                        "Thức ăn nhanh",//
                                        "Sức Khỏe",//
                                        "Cơm"//
                                };

                                filterProductTv.setText(selected);
                                if(selected.equals("Tất cả đồ ăn")){
                                    //load all
                                    loadAllProduct();
                                    adapterProductChef.getFilter().filter("");
                                }
                                else {
                                    //load filtered
                                    loadFilteredProduct(selected);
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


        loadAllProduct();
        recyclerView =(RecyclerView) v.findViewById(R.id.Recycle_menu);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapterProductChef = new AdapterProductChef(getContext(),productList);
        recyclerView.setAdapter(adapterProductChef);


        return v;
    }

    private void speak() {
        //intent to show speech to text didlog

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Tìm kiếm đồ ăn thức uống yêu thích ở đây");
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

    private void filter(String text) {
        productList = new ArrayList<>();
        for (ModelProduct item : list) {
            if (item.getProductTitle().toUpperCase().contains(text.toUpperCase())) {
                productList.add(item);
            }
        }
        adapterProductChef.filterList(productList);
    }

    private void loadFilteredProduct(final String selected) {
        productList = new ArrayList<>();

        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String productCategory = ""+ds.child("productCategory").getValue();
                            String productTitle = ""+ds.child("productTitle").getValue();
                            //if selected category mathes product category then add in list
                            if(selected.equals(productCategory)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }
                            if(selected.equals(productTitle)){
                                ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                                productList.add(modelProduct);
                            }

                        }
                        //setup adapter
                        adapterProductChef  =new AdapterProductChef(getContext(),productList);
                        //set adapter
                        recyclerView.setAdapter(adapterProductChef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadAllProduct() {
        productList = new ArrayList<>();

        //get all product
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(firebaseAuth.getUid()).child("Products")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //before getting reset list
                        productList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
                            productList.add(modelProduct);
                        }
                        //setup adapter
                        adapterProductChef  =new AdapterProductChef(getContext(),productList);
                        //set adapter
                        recyclerView.setAdapter(adapterProductChef);
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

