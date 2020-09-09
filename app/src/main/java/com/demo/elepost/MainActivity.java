package com.demo.elepost;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
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
    private Spinner mSn_area;
    private TextView mTv_result;
    private String returnResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "Created!");

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mEt_point = findViewById(R.id.point);
        mBt_search = findViewById(R.id.btn_search);

        final String[] area = new String[1];
        final String[] point = new String[1];
        final StringBuffer postInfo = new StringBuffer();
        final String url = "https://wx.ecnu.edu.cn/CorpWeChat/card/dogetEle.html";

        mSn_area = findViewById(R.id.areaSpinner);

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
                    Connection resultHtml = Jsoup.connect(url);
                    resultHtml.data("area", area[0]);
                    resultHtml.data("point",point[0]);
                    Document resultDocument = null;
                    try {
                        resultDocument = resultHtml.post();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Element resultEle = resultDocument.selectFirst("h3");
                    returnResult = resultEle.text();
                    System.out.println("====result====>"+returnResult);
                    postInfo.delete(0,postInfo.length());
                    mTv_result = findViewById(R.id.mTv_result);
                    mTv_result.setText(returnResult);
                }
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
