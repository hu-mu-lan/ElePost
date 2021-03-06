package com.demo.elepost;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {

    private EditText mEt_point;
    private Button mBt_search;
    private Button mBt_history;
    private Spinner mSn_area;
    private TextView mTv_result;
    private String returnResult;

    private SharedPreferences sp;
    private Editor spEditor;

    private Context mContext;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "Created!");

        mContext = this;

        sp = this.getSharedPreferences("cacheXML", MODE_PRIVATE);
        spEditor = sp.edit();


//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        mEt_point = findViewById(R.id.point);
        mBt_search = findViewById(R.id.btn_search);

        final String[] area = new String[1];
        final String[] point = new String[1];
        final StringBuffer postInfo = new StringBuffer();
        final String url = "https://wx.ecnu.edu.cn/CorpWeChat/card/dogetEle.html";

        area[0] = sp.getString("area", "TP");
        point[0] = sp.getString("point", "");

        mEt_point.setText(point[0]);

        mSn_area = findViewById(R.id.areaSpinner);

        switch (area[0]) {
            case "TP":
                mSn_area.setSelection(0);
                break;
            case "zb":
                mSn_area.setSelection(1);
                break;
            case "mh":
                mSn_area.setSelection(2);
                break;
        }

        mSn_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("pos",String.valueOf(position));
                if(position == 0){
                    area[0] = "TP";
                }else if(position == 1){
                    area[0] = "zb";
                }else if(position == 2){
                    area[0] = "mh";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEt_point.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("point",s.toString());
                point[0] = s.toString();
                Log.d("point2=>",point[0]);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postInfo.append("area=").append(area[0]).append("&point=").append(point[0]);
                Log.d("test",postInfo.toString());

                if(point[0] == null || point[0].equals("")){
                    Toast.makeText(MainActivity.this,"请输入房间号！", LENGTH_SHORT).show();
                }else{
                    final Connection resultHtml = Jsoup.connect(url);
                    resultHtml.data("area", area[0]);
                    resultHtml.data("point",point[0]);

                    spEditor.putString("area", area[0]);
                    spEditor.putString("point", point[0]);
                    spEditor.apply();

                    final Document[] resultDocument = {null};

                    Thread postThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                resultDocument[0] = resultHtml.post();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(resultDocument[0] != null){
                                            Element resultEle = resultDocument[0].selectFirst("h3");
                                            returnResult = resultEle.text();
                                        }else {
                                            Toast.makeText(mContext,"请检查网络", LENGTH_SHORT).show();
                                        }
                                        postInfo.delete(0,postInfo.length());
                                        mTv_result = findViewById(R.id.mTv_result);
                                        mTv_result.setText(returnResult);
                                    }
                                });
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext,"请检查网络", LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    postThread.start();
                }
            }
        });

        mBt_history = findViewById(R.id.btn_history);
        mBt_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Caution!");
        builder.setMessage("Exit?");
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
