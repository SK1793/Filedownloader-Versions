package com.SK.filedownloader;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class downloadsAdapterCustom extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_downloads_adapter);

        ImageButton resume= findViewById(R.id.btnResume);
        ImageButton pause=findViewById(R.id.btnPause);
        ImageButton remove=findViewById(R.id.btnRemove);
        TextView more=findViewById(R.id.tvLinkMore);
        LinearLayout layoutUrl=findViewById(R.id.llFileDetailsContainer);
        
        resume.setBackgroundColor(Color.parseColor("#49d16d"));
        pause.setBackgroundColor(Color.parseColor("#d1ba24"));
        remove.setBackgroundColor(Color.parseColor("#d9473f"));

        //Hide extra details
        layoutUrl.setVisibility(View.GONE);

        more.setOnClickListener(view -> {
            if (more.getText().toString().equals("more")){
                layoutUrl.setVisibility(View.VISIBLE);
            }else{
                layoutUrl.setVisibility(View.GONE);
            }
        });


    }
}
