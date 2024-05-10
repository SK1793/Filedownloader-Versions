package com.SK.filedownloader;

import static android.content.ContentValues.TAG;
import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class custom_tools {
    Context c;
    DownloadManager dm;

    
    public custom_tools(@Nullable Context context) {
//        super();
        this.c=context;
        dm = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    public void Load_data(ArrayList D_arr){

    }
    public String byte_converter(float size){
        final String[] result = {"N/A"};

      float _size[]={size};


        if(_size[0]>999.00 && _size[0]<999999.00){
            _size[0] = Float.parseFloat(decfor.format(_size[0] / 1024));
            result[0] =_size[0]+" KB";
        }
        else if(_size[0]>999999.00 && _size[0]<999999999.00){
            _size[0]=Float.parseFloat(decfor.format(_size[0]/1048576));
            result[0] =_size[0]+" MB";

        }
        else if(_size[0]>999999999.00){
            _size[0]=Float.parseFloat(decfor.format(_size[0]/1073741824));
            result[0] =_size[0]+" GB";

    }
        else{
            result[0] =size+"N/A";
        }


        return result[0];
    }
    public String date_now(){
        final String[] result = {""};



        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result[0] = sdf.format(c.getTime());

        return result[0];
    }
    public String Media_type( String Url) {
        String[] type = {""};

        try {
            URL obj = new URL(Url);
            URLConnection conn = obj.openConnection();
//            Map<String, List<String>> map = conn.getHeaderFields();
            type[0]= conn.getHeaderField("Content-Type");
        }catch (Exception e){
            Log.d(TAG, "Exception caused");
            e.printStackTrace();
        }

        return type[0];
    }
    public String local_path( long id) {
        String[] filePath = {""};

        try {
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(id);
            Cursor c = dm.query(query);
            if (c.getCount() != 0) {
                while (c.moveToNext()) {

                    filePath[0] = c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI));
                }
            }
        }catch (Exception e){
            Log.d(TAG, "Exception caused");
            e.printStackTrace();
        }

        return filePath[0];
    }
    public String File_name( String Url) {
        final String[] Name = {""};

            try {
                URL obj = new URL(Url);
                URLConnection conn = obj.openConnection();
//            Map<String, List<String>> map = conn.getHeaderFields();
                Name[0] = conn.getHeaderField("Content-Disposition");
                if (Name[0].length() > 3) {

                    Pattern quotes = Pattern.compile("\"([^\"]*)\"");
                    Matcher m = quotes.matcher(Name[0]);
                    while (m.find()) {
                        Name[0] = m.group(1)
                                .replace(':', ' ').replace('?', ' ').replace('+', ' ').replace('-', ' ')
                                .replace('^', ' ').replace('%', ' ').replace('#', ' ').replace('@', ' ')
                                .replace('!', ' ').replace('/', ' ').replace(',', ' ').replace('&', ' ')
                                .replace('$', ' ').replace('\\', ' ').replace(';', ' ').replaceAll("\\s", "");
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception caused");
                e.printStackTrace();
            }

        return Name[0];
    }
    public int File_size( String Url) {
        final int[] size = {0};

        try {
            URL obj = new URL(Url);
            URLConnection conn = obj.openConnection();

            size[0] = Integer.parseInt(conn.getHeaderField("Content-Length"));
        }catch (Exception e){
            Log.d(TAG, "Exception caused");
            e.printStackTrace();
        }

    return size[0];
    }
    public boolean IsResummable( String Url) {
        final String[] res = {""};
        boolean resummable[]={false};

        try {
            URL obj = new URL(Url);
            URLConnection conn = obj.openConnection();
//            Map<String, List<String>> map = conn.getHeaderFields();
            res[0] = conn.getHeaderField("Accept-Ranges");
        }catch (Exception e){
            Log.d(TAG, "Exception caused");
            e.printStackTrace();
        }

        if(res[0].contains("bytes")){
            resummable[0]=true;
        }else{
            resummable[0]=false;
        }

    return resummable[0];
    }
    public void http_results( String Url) {

                    URL obj = null;
                    try {
                        obj = new URL(Url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();

                    }
                    URLConnection conn = null;
                    try {
                        conn = obj.openConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
//        Map<String, List<String>> map = conn.getHeaderFields();

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//                        Log.d(TAG,"Key : " + entry.getKey()
//                                + " ,Value : " + entry.getValue());
//                    }
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }).start();
    }

    //Download percentage
    @SuppressLint("Range")
    public synchronized String Percentage( long D_id){
        final int[] percentage = {0};
        final int[] totalBytes = {0};
        final int[] progress = {0};

        try {
            Cursor cursor1 = dm.query(new DownloadManager.Query().setFilterById(D_id));
            if(cursor1.moveToFirst()) {
                progress[0] = cursor1.getInt(cursor1.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int totalSizeIndex = cursor1.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                totalBytes[0] = (int) cursor1.getLong(totalSizeIndex);
                if(progress[0] >-1){
                    percentage[0] =(progress[0] / totalBytes[0])*100;

                }else{
//                    Log.d(TAG, "=>Custom_tools-Download isn't Started");
                }

            }
            cursor1.close();
        }catch (Exception e){
            Log.d(TAG, "=>MainActivity-Progress_Error: ");
            e.printStackTrace();
        }
        String dwnld;
        return Math.abs(percentage[0]) +"";
    }

    @SuppressLint("Range")
    public synchronized int progressbarPercent(long D_Id){
        final int[] percentage = {0};
        final int[] totalBytes = {0};
        final int[] progress = {0};

            Cursor cursor1 = dm.query(new DownloadManager.Query().setFilterById(D_Id));
            if(cursor1.moveToFirst()) {
                progress[0] = cursor1.getInt(cursor1.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int totalSizeIndex = cursor1.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                totalBytes[0] = (int) cursor1.getLong(totalSizeIndex);

                        percentage[0] =(progress[0] * 100 / totalBytes[0]);
            }
            cursor1.close();

        return Math.abs(percentage[0]);
    }

    //Download Status
    public String Status( long Download_id1,Context c){
        View v=new View(c);

        String[] status1 = {"N/A"};

            Cursor cursor1 = dm.query(new DownloadManager.Query().setFilterById(Download_id1));

            if (cursor1.moveToNext()) {

                @SuppressLint("Range") int status = cursor1.getInt(cursor1.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (status == DownloadManager.STATUS_FAILED) {
                        status1[0] = "Failed";

                } else if (status == DownloadManager.STATUS_PENDING) {
                    status1[0] = "Pending";

                }else if (status == DownloadManager.STATUS_PAUSED) {

                    status1[0] = "Paused";

                } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    status1[0] = "Successfull";

                } else if (status == DownloadManager.STATUS_RUNNING) {

                    status1[0] = "Downloading";
                }

            }
        if(cursor1.getPosition()== cursor1.getCount()) {
            cursor1.close();
        }

        return status1[0];
    }
}
