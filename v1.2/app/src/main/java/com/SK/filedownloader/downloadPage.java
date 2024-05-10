package com.SK.filedownloader;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.SK.filedownloader.R;

import java.util.ArrayList;

public class downloadPage extends AppCompatActivity {

    RelativeLayout RLcurrent,RLall;
    ProgressBar PBcurrent,PBall;
    TextView CurrentFetchErr,AllFetchAErr,btnCurrentView,btnAllView;
    DbDownloader dbDataClass;
    ArrayList<downloads> D_arr,D_arrCurrent;
    RecyclerView RVCurrent,RVAll;
    downloads_section Downloads_Section_obj,Downloads_Section_objCurrent;

    custom_tools tools;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);

        context=this.getApplicationContext();

        D_arr= new ArrayList<>();
        D_arrCurrent= new ArrayList<>();

        RLcurrent = findViewById(R.id.RL_Current);
        RLall = findViewById(R.id.RLAll);
        PBcurrent = findViewById(R.id.PBar_CurrentD);
        PBall = findViewById(R.id.PBar_AllD);
        CurrentFetchErr = findViewById(R.id.TvFetchErrorCurrent);
        AllFetchAErr = findViewById(R.id.TvFetchErrorAll);
        btnAllView=findViewById(R.id.TvbtnViewAll);
        btnCurrentView=findViewById(R.id.TvbtnViewCurrent);
//        srLayout=findViewById(R.id.SwipeRLAll);

        //Let RecycleLayout be invisible
        RLcurrent.setVisibility(View.GONE);
        RLall.setVisibility(View.GONE);
        CurrentFetchErr.setVisibility(View.GONE);
        AllFetchAErr.setVisibility(View.GONE);

        tools=new custom_tools(context);

        dbDataClass = new DbDownloader(context);
//        db1_read = dbDataClass.getReadableDatabase();
//        db1_write = this.openOrCreateDatabase("DownloaderDb", Context.MODE_PRIVATE, null);

        D_arr=dbDataClass.fetchToArray(downloadPage.this);
        Downloads_Section_obj =new downloads_section(D_arr,context);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "D_arr : "+D_arr.get(1)._download_id);
//                Log.d(TAG, "D_arr length: "+ Downloads_Section_obj.getItemCount());
//            }
//        }).start();

        RVAll=findViewById(R.id.idRVAll);
        RVCurrent=findViewById(R.id.idRVCurrent);

        LinearLayoutManager LLManager=
                new LinearLayoutManager(downloadPage.this,RecyclerView.VERTICAL,true);

        if(!(D_arr.size()>0)){
//            Log.d(TAG, "=>DownloadPage-There were no records from 'D_arr' to Add");
            Toast.makeText(this, "No records found!", Toast.LENGTH_SHORT).show();
            AllFetchAErr.setVisibility(View.VISIBLE);
            return;
        }
        for(int i=0;i<D_arr.size();i++){
            if(!(tools.Status( D_arr.get(i).get_download_id(),context).contains("Successfull")) ||
                    (!tools.Status(D_arr.get(i).get_download_id(),context).contains("Failed"))){
                D_arrCurrent.add(D_arr.get(i));
            }

        }

            RVAll.setLayoutManager(LLManager);

        PBall.setVisibility(View.GONE);
        RVAll.setAdapter(Downloads_Section_obj);

        if(D_arrCurrent.size()>0){
            Downloads_Section_objCurrent =new downloads_section(D_arrCurrent,context);
        RVCurrent.setLayoutManager(LLManager);
        RVCurrent.setAdapter(Downloads_Section_objCurrent);
        }

        //Show And Hide Lists with a Tap on "-" or "+"
        btnCurrentView.setOnClickListener(v->{
            if(btnCurrentView.getText().toString().contains("+")){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                RLcurrent.setVisibility(View.VISIBLE);
                btnCurrentView.setText("-");
            }else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                RLcurrent.setVisibility(View.GONE);
                btnCurrentView.setText("+");

            }
        });
        btnAllView.setOnClickListener(v->{
            if(btnAllView.getText().toString().contains("+")){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                RLall.setVisibility(View.VISIBLE);
                btnAllView.setText("-");
            }else{
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                RLall.setVisibility(View.GONE);
                btnAllView.setText("+");

            }
        });

//        PBall.setVisibility(View.GONE);
//        RLall.setVisibility(View.VISIBLE);
//        PBcurrent.setVisibility(View.GONE);
//        RLcurrent.setVisibility(View.VISIBLE);

    }
}