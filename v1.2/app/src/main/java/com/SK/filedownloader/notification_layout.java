package com.SK.filedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.SK.filedownloader.R;

public class notification_layout extends AppCompatActivity {
    int Progress;
    DownloadManager dm;
    long id;
    custom_tools tools;
    TextView TVfilename,TVprogress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_layout);

         tools=new custom_tools(this);
        Handler handler = new Handler();


        TVfilename=findViewById(R.id.TVNotificationFilename);
        TVprogress=findViewById(R.id.TVNotificationProgress);
        String Filename=getIntent().getStringExtra("Filename");
         id=getIntent().getLongExtra("D_id",0);

         dm= (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);

        TVfilename.setText(Filename);

//        ProgressLoader progressLoader=new ProgressLoader();
//            progressLoader.execute();
        //set Values to TextViews

    }

    public class ProgressLoader extends AsyncTask<Long,Integer,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(Long... longs) {

            Progress=(int)(tools.progressbarPercent(id));

            return Progress;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            TVprogress.setText(values+"%");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void onPostExecute(Integer val) {
            super.onPostExecute(val);
            TVprogress.setText(val+"%");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}