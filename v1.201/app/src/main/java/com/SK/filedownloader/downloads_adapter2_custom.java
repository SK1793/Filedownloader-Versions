package com.SK.filedownloader;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.SK.filedownloader.R;

public class downloads_adapter2_custom extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.downloads_adapter2);

        TextView filename=findViewById(R.id.tvFIlename2);
        TextView percent=findViewById(R.id.tvDownloadPercent2);
        TextView extension=findViewById(R.id.tvExtension2);

        ImageButton resume = findViewById(R.id.btnResume2);
//        ImageButton remove = findViewById(R.id.btnRemove2);
        ImageButton pause = findViewById(R.id.btnPause2);
//        ImageButton open = findViewById(R.id.btnOpen2);
//        resume.setBackgroundColor(Color.parseColor("#49d16d"));
//        pause.setBackgroundColor(Color.parseColor("#d1ba24"));
//        remove.setBackgroundColor(Color.parseColor("#d9473f"));

        TextView more = findViewById(R.id.tvLinkMore2);
        TextView url = findViewById(R.id.tvFileUrl2);
        TextView loc = findViewById(R.id.tvFileLoc2);
        TextView date = findViewById(R.id.tvFileDate2);
        TextView progress = findViewById(R.id.tvFileProgress2);
        TextView size = findViewById(R.id.tvFileSize2);
        TextView type = findViewById(R.id.tvFileType2);
        LinearLayout layoutUrl = findViewById(R.id.llFileDetailsContainer2);

        //Button Onclick Activities
        more.setOnClickListener(view -> {

            String text_more=more.getText().toString();
            String text_less=more.getText().toString();

            if (text_more.contentEquals("More")){
                layoutUrl.setVisibility(View.VISIBLE);
                more.setText("less");  layoutUrl.requestLayout();

            } else if (text_less.contentEquals("less")) {
                layoutUrl.setVisibility(View.GONE);
                more.setText("More");  layoutUrl.requestLayout();
            }else {
                Toast.makeText(this,"Something is odd Try Again!",Toast.LENGTH_SHORT).show();
            }
        });
        final String[] path = new String[1];

        Intent fetch_data=getIntent();
        String file_name=fetch_data.getStringExtra("final_title");
        String d_id=fetch_data.getStringExtra("D_id");
        String file_url=fetch_data.getStringExtra("geturl");
        String file_extension=fetch_data.getStringExtra("extension");

//        open.setOnClickListener(v->{
//
//            DownloadManager dm = (DownloadManager) this.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
//            Uri uri_file= dm.getUriForDownloadedFile(Long.parseLong(d_id));
//            String mime= dm.getMimeTypeForDownloadedFile(Long.parseLong(d_id));
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "Download ID: "+d_id);
//                    Log.d(TAG, "uri : "+uri_file);
//                    Log.d(TAG, "mime : "+mime);
//                }
//            }).start();
//
//
//            try {
//
//                path[0] = "/storage/emulated/0/Download/"+file_name;
//
////                Uri uri = Uri.parse("file://" + file.getAbsolutePath());
//
//                Uri uri = Uri.parse(path[0]);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
////                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();
//
//                if (mime!=null) {
//                    intent.setDataAndTypeAndNormalize(uri, mime);
//                } else {
//                    intent.setDataAndTypeAndNormalize(uri, "*/*");
//                }
//
//                this.startActivity(intent);
//            }catch (Error e){
//
//                Toast.makeText(this, "couldn't find file", Toast.LENGTH_SHORT).show();
//                path[0] = "/storage/emulated/0/Download";
//                Uri uri_=Uri.parse(path[0]);
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setDataAndType(uri_, "*/*");
//                this.startActivity(intent);
//            }
//
//        });

        resume.setOnClickListener(v->{

                DownloadManager dm = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);


        });

        pause.setOnClickListener(v->{
            DownloadManager dm = (DownloadManager)this.getSystemService(DOWNLOAD_SERVICE);

            Toast.makeText(this, "Donwload removed", Toast.LENGTH_SHORT).show();
        });

//        remove.setOnClickListener(v->{
//            DownloadManager dm = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
//            int removed=dm.remove(Long.parseLong(d_id));
//            Toast.makeText(this, "Download removed: "+removed, Toast.LENGTH_SHORT).show();
//        });


    }

}
