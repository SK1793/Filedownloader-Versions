package com.SK.filedownloader;

import static android.content.ContentValues.TAG;

import static com.SK.filedownloader.DbDownloader.fetchAllRows;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.RecyclerView;

import com.SK.filedownloader.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.HttpRetryException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    int FOLDERPICKER_CODE = 1111;
    private EditText url, ext, path;
    ListView lvDownloads, lvDownloads_Section_all, lvDownloads_Section_current;
    RecyclerView RcAll, RcCurrent;
    String title, ext2, extension,type, geturl, final_title, extension_Edited, title_Edited, mime1, size, AltPath, Filename, PrevUrl = " ";
    ArrayList<downloads> arr = new ArrayList<downloads>();
    ArrayList<downloads> arr_all = new ArrayList<downloads>();
    Uri uri_file, fle_uri, Custom_uri;
    Cursor cursor, Fetcher;
    LinearLayout ll1;
    long D_id;
    Button download, btn_allDownloads;
    ImageButton select_dir, Props_Custom_button;
    Intent i2;
    private DbDownloader DbDownloader1;
    SQLiteDatabase db1_write, db1_read;
    String[] permissions, Extension_split,title_split;
    DownloadManager.Request request;

    ProgressBar PbDownloadLoading;


    int Some_code = 1793, Size = 0, progress = 0, totalBytes = 0;
    custom_tools CustomTools;
    boolean doubleBackPressed, d_started = false, delete_option;
    File direct;

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable resetBackPressedRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackPressed = false;
        }
    };

    ExecutorService executor = Executors.newSingleThreadExecutor();

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AdView ad=findViewById(R.id.AdView_MainActivity);
        AdRequest adReq=new AdRequest.Builder()
                .build();
        ad.loadAd(adReq);

        MobileAds.initialize(this,initializationStatus -> {

        });

        StrictMode.ThreadPolicy gfgPolicy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);


        url = findViewById(R.id.url);
        lvDownloads = findViewById(R.id.lvDownloads);
        ll1 = findViewById(R.id.LL1);
        download = findViewById(R.id.download);
        btn_allDownloads = findViewById(R.id.btnAllDownloads);
        select_dir = findViewById(R.id.IB_select_direct);
        path = findViewById(R.id.ETPath);
        Props_Custom_button = findViewById(R.id.IB_Popup_Props);
        PbDownloadLoading = findViewById(R.id.PbDownloadLoading);

        DbDownloader1 = new DbDownloader(MainActivity.this);
        CustomTools = new custom_tools(MainActivity.this);

        Extension_split = new String[2];

        //Start the On Download complete function
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        //File Reader
        db1_write = this.openOrCreateDatabase("DownloaderDb", Context.MODE_PRIVATE, null);
        db1_read = this.DbDownloader1.getWritableDatabase();

//        Log.d(TAG, "Pre Rows Count "+Fetcher.getCount());
//        Log.d(TAG, "Pre Columns Count "+ Arrays.toString(Fetcher.getColumnNames()));
//        Thread FetchAlldata = new Thread(() -> Fetcher = fetchAllRows(db1_read));
//        Log.d(TAG, "Brand: " + Build.BRAND);//OnePlus for me
//        Log.d(TAG, "Model: " + Build.MODEL);//DN2101
//        Log.d(TAG, "Product: " + Build.PRODUCT);//DN2101IND

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.POST_NOTIFICATIONS
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        // List of permissions to request that are not granted
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        // Request permissions
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    Some_code);
        } else {
            // All permissions are already granted, proceed with your logic
        }

        //Initializing and declaring Download Manager
        DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        executor.execute(() -> {

            direct = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/FileDownloader");
            boolean dir = false;
            if (!direct.exists()) {
                try {
                    dir = direct.mkdir();
                } catch (SecurityException e) {
                    e.printStackTrace();
//                    Log.d(TAG, "-->mainActivity> Error creating directory : " + e);
                }
                if (!dir) {
//                    Log.d(TAG, "-->mainActivity> Couldn't create Directory :");

                }
            }

//        Log.d(TAG, "Directory_: "+ direct);
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");

//        Thread completed = new Thread(()->{
//            registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//        });

        download.setOnClickListener(v -> {

            //init Calender for File Name to be Unique with each sec.
            Calendar c = Calendar.getInstance();
            String c_date = sdf.format(c.getTime());


            geturl = url.getText().toString();
            if (geturl.length() < 1) {
                Toast.makeText(this, "Please enter Url!", Toast.LENGTH_LONG).show();
                url.setError("please Enter URL.");
                return;
            }
            String full_title = URLUtil.guessFileName((geturl), null, null);
            String[] title_split = full_title.split("\\.");
            if (title_split[0].length() > 1) {

                title = title_split[0];
            } else {

                title = "DownloadFile";
            }
            //Properties-custom
            title_Edited = "";
            extension_Edited = "";
            i2 = getIntent();

            if (i2.getStringExtra("filename") != null) {
                title_Edited = i2.getStringExtra("filename");
            }

            if (i2.getStringExtra("extension") != null) {
                extension_Edited = i2.getStringExtra("extension");
            }
//            Goto the Function which handles the Download Operatuon

            PbDownloadLoading.setVisibility(View.VISIBLE);
            PbDownloadLoading.setProgress(10);
            AddDownload(dm, v, c_date);

        });

        btn_allDownloads.setOnClickListener(v -> {
            Intent ActivityAlldownloads = new Intent(this, downloads_page2.class);


//            Intent send_data=new Intent();
            ActivityAlldownloads.putExtra("filename", this.final_title);
            ActivityAlldownloads.putExtra("url", this.geturl);
            ActivityAlldownloads.putExtra("D_id", this.D_id);
            ActivityAlldownloads.putExtra("extension", extension);
            startActivity(ActivityAlldownloads);
        });
        //Directory Selction for Downloading FIle
        select_dir.setOnClickListener(v -> {

            if (Build.BRAND.contains("Samsung") || Build.BRAND.contains("SamSung")) {
                Intent intent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
                intent.putExtra("CONTENT_TYPE", "*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

                // Start the file picker activity and wait for the result.
                startActivityForResult(intent, FOLDERPICKER_CODE);
            }

        });

        Props_Custom_button.setOnClickListener(v -> {


            PrevUrl = url.getText().toString();
            FIle_properties_customize popup1 = new FIle_properties_customize();
            popup1.popup_window(v);
        });

        // ATTENTION: This was auto-generated to handle app links.

        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkAction != null) {
            url.setText(String.valueOf(appLinkData));
        } else {
//            Toast.makeText(this, "Link not Found! or Retry!!!", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackPressed) {
            super.onBackPressed();
            super.onDestroy();// Exit the app
        } else {
            doubleBackPressed = true;
            Toast.makeText(this, "Press back again to exit",
                    Toast.LENGTH_SHORT).show();

            handler.postDelayed(resetBackPressedRunnable,
                    2000);
        }
    }

    final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (D_id == -1) {
                Toast.makeText(context, "Failed to Download!Try Again...", Toast.LENGTH_SHORT).show();
            }

            if (D_id == downloadedID) {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            }
        }
    };


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Checking the result status
        if (requestCode == FOLDERPICKER_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Custom_uri = data.getData();
//            Log.d(TAG, "Dialog_Uri: "+Custom_uri.toString());
            assert Custom_uri != null;
            path.setText(Custom_uri.getPath());

//            Log.d(TAG, "Custom_uri: " + Custom_uri);
//            Log.d(TAG, "Custom_uri(getPath): " + Custom_uri.getPath());
//            Log.d(TAG, "Custom_uri(getPath->toString): " + Custom_uri.getPath().toString());
//            ll1.refreshDrawableState();
//            ll1.invalidate();
        }

    }

    @SuppressLint("Range")
    public void AddDownload(DownloadManager dm, View v, String c_date) {
        String ext1;
        NotificationLoader notificationLoader = new NotificationLoader();
        d_started = false;

        try {

                    Uri uri = Uri.parse(geturl);
                    try {
                        request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

                        request.allowScanningByMediaScanner();
                        request.setDescription("Downloading File...");
//                request.setVisibleInDownloadsUi(true);
                        request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(geturl));
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    } catch (Exception e) {
                        url.setError("Link invalid!");
                        Toast.makeText(MainActivity.this, "Some errors caused!check URL again", Toast.LENGTH_SHORT).show();
                        PbDownloadLoading.setProgress(0);

                        System.exit(0);
                    }

                    String state = Environment.getExternalStorageState();

                    mime1 = CustomTools.Media_type(geturl);
                    Size = CustomTools.File_size(geturl);
                    size = CustomTools.byte_converter(Size);
                    Filename = CustomTools.File_name(geturl);
//                    CustomTools.http_results(geturl);

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG, "size(URLConnection): " + size);
//                            Log.d(TAG, "Mime_type2: " + mime1);
//                            Log.d(TAG, "Filename: " + Filename);
//                            Log.d(TAG, "Custom_Directory_: " + Custom_uri);
//                        }
//                    }).start();

                    if (!(Filename == null)) {

                        if (Filename.length() > 3) {

                            if (Filename.contains(".")) {

                                 title_split = Filename.split("\\.");
                                title = title_split[0];
                            }
                        }
                    }

                    if (mime1 == null) {
                        Extension_split[0] = "*";
                        Extension_split[1] = " ";

                    } else {
                        Extension_split = mime1.split("/");
                    }
                    //Custom Properties
                    if (!title_Edited.isEmpty()) {
                        title = title_Edited;
                    }

                    if (!extension_Edited.isEmpty()) {
                        extension = extension_Edited;
                    } else {
                        extension = Extension_split[1];
                    }
                    type=Extension_split[0];

//                    Log.d(TAG, "Extension: "+Extension_split[1]);
//                    Log.d(TAG, "Type: "+type);

                    if (extension.contains("octet") && title_split[1].length()>0){
                        extension=title_split[1];
                    }

                    final_title = title + "_" + c_date + "." + extension;
                    if (Custom_uri != null) {
                        String[] Dir_split = Custom_uri.getPath().split(":");
//                        Log.d(TAG, "Custom_uri(Dir_split[1]) : " + Dir_split[1]);
//                        Log.d(TAG, "Custom_uri(Custom_uri.getPath()) : " + Custom_uri.getPath());

//                    Uri destinationUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()
//                            +"/Downloads", final_title));
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            AltPath = "file:///storage/emulated/0/" + Dir_split[1] + "/" + final_title;
                            request.setDestinationInExternalPublicDir(Dir_split[1], final_title);
                        } else {
                            Toast.makeText(MainActivity.this, "Can't Save in custom Folder for Android P and Later Versions(Saved in Default folder)", Toast.LENGTH_LONG).show();
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "FileDownloader/" + final_title);
                        }
                        Custom_uri = null;
                        path.setText("");
                    } else {
                        if (direct.exists()) {
//                    request.setDestinationInExternalFilesDir(getApplicationContext(),Environment.DIRECTORY_DOWNLOADS+"FileDownloader", final_title)
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "FileDownloader/" + final_title);
                        } else {
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, final_title);
                        }
                    }
                    request.setTitle(title);
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, final_title);


            new Handler().post(new Runnable() {
                @Override
                public void run() {

                            try {
                                //Download ID
                                D_id = dm.enqueue(request);
                                Thread.sleep(50);
                            } catch (Exception e) {
                                e.printStackTrace();
//                                Log.d(TAG, "Error+ " + e);
                                Toast.makeText(MainActivity.this, "Error while Requesting Download", Toast.LENGTH_SHORT).show();
                            }
//                            Log.d(TAG, "Download Started (Enqueue)!");

                            Cursor cursor1 = dm.query(new DownloadManager.Query().setFilterById(D_id));

                            PbDownloadLoading.setProgress(55);

                            if (cursor1.moveToFirst()) {

                                    progress = cursor1.getInt(cursor1.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    int totalSizeIndex = cursor1.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                                    totalBytes = (int) cursor1.getLong(totalSizeIndex);

//                                    Log.d(TAG, "inside Switch !");
//                                    Log.d(TAG, "status :"+CustomTools.Status(D_id, getApplicationContext()).toLowerCase());

                                Log.d(TAG, "Status: "+CustomTools.Status(D_id, getApplicationContext()).toLowerCase());
                                    switch (CustomTools.Status(D_id, getApplicationContext()).toLowerCase()) {
                                        case "download":
//                                            Log.d(TAG, "inside download !");

                                            Snackbar.make(v, "Download Started!", Snackbar.LENGTH_SHORT).show();
//                                            Log.d(TAG, "Download Started!");

                                            //Adapter Setting for current downloads

//                DbDownloader1.AddDownload(420,"filename", "extension", "some_uri", "some_url", 1234567);
                                            String perce_tage = CustomTools.Percentage(D_id);
                                            String[] Downloaded_path = {""};
                                            if (AltPath == null) {
                                                Downloaded_path[0] = CustomTools.local_path(D_id);
                                            } else {
                                                Downloaded_path[0] = AltPath;
                                            }
                                            executor.execute(() -> {
                                                Log.d(TAG, "D_id: " + D_id + ", title: " + title + " ,extension: " + extension +
                                                        " ,fle_uri: " + Downloaded_path[0] + ",geturl: " + geturl + " ,Date: " + CustomTools.date_now() + " ,size: " + Size + " ,percent: " + perce_tage + " ,status: " + CustomTools.Status(D_id, getApplicationContext()));
                                            });

                                            String file_url = "path";
                                            file_url = CustomTools.local_path(D_id);

                                            DbDownloader1.AddDownload(D_id, title, final_title, extension, Downloaded_path[0], geturl, CustomTools.date_now(), Size, CustomTools.Status(D_id, getApplicationContext()));
//                ArrayList<downloads> some_list=DbDownloader1.fetchToArray(MainActivity.this);

                                            arr.add(new downloads(title, final_title, perce_tage, extension, geturl, Uri.parse(Downloaded_path[0]), D_id,
                                                    type, CustomTools.Status(D_id, getApplicationContext()), CustomTools.Status(D_id, getApplicationContext()), CustomTools.date_now(), Downloaded_path[0], Size));
                                            Collections.reverse(arr);

                                            downloadsAdapter adapter = new downloadsAdapter(getApplicationContext(), arr);
//                downloads_adapter2 adapter1 = new downloads_adapter2(this, arr);

                                            //layout selvDownloadstAdapter to list
//                .setAdapter(adapter);
                runOnUiThread(new Runnable() {
                   @Override
                      public void run() {
                       PbDownloadLoading.setProgress(100);
                       try {
                           Thread.sleep(50);
                       } catch (InterruptedException e) {
                           throw new RuntimeException(e);
                       }
                       PbDownloadLoading.setVisibility(View.GONE);
                       lvDownloads.setAdapter(adapter);
                       adapter.notifyDataSetChanged();
                       ll1.requestLayout();

//                lvDownloads_Section_current.setAdapter(adapter);\
                   }});
                                            Toast.makeText(getApplicationContext(), "Download Started", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "Successfull":
                                            Toast.makeText(getApplicationContext(), "Download Already Exist!", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "Failed":
                                            Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "Pending":
                                            Toast.makeText(getApplicationContext(), "Download Pending", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "Paused":
                                            Toast.makeText(getApplicationContext(), "Download Paused", Toast.LENGTH_SHORT).show();
                                            break;
//                                    default:
////                                        Log.d(TAG, "Something wrong while checking Status of Download");
//                                        Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//                                        throw new IllegalStateException();
                                    }
//                                    executor.execute(() -> {
//                                        notificationLoader.execute(D_id);
//
//
//                                    });


                                    //Set Notification
                                }
                                cursor1.close();

               }
                });

        } catch (Exception e) {
            Toast.makeText(this, "Error try again, " + e, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Error: " + e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
//        Log.d(TAG, "App Status:Restarted");
        super.onRestart();
        if (PrevUrl.length() > 2) {
            url.setText(PrevUrl);
        }

    }

    @Override
    protected void onResume() {
//        Log.d(TAG, "App Status:Resume");
//        completed1.start();
        super.onResume();
    }

    @Override
    public void onPause() {
//        Log.d(TAG, "App Status:Paused");
        super.onPause();
//
    }

    @Override
    protected void onUserLeaveHint() {
//        Log.d(TAG, "App Status:User Leaving");
//        Toast.makeText(this, "You are leaving the App!Bye.", Toast.LENGTH_SHORT).show();
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
//        Log.d(TAG, "App Status:Destroyed");
        unregisterReceiver(onDownloadComplete);
        super.onDestroy();
        //On destroying App the onComplete download function is ended
    }

    public class NotificationLoader extends AsyncTask<Long, Long, Void> {
        String channel_ID = "CHANNEL_ID_NOTIFICATION";
        Intent i;
        NotificationCompat.Builder builder;
        NotificationManager NotificationManager;

        @Override
        protected Void doInBackground(Long... val) {

            i = new Intent(MainActivity.this, notification_layout.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("Filename", Filename);
            i.putExtra("D_id", val[0]);

            publishProgress(val);
            return null;
        }

        @Override
        protected void onProgressUpdate(Long... val) {
            super.onProgressUpdate(val);
            NotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            builder =
                    new NotificationCompat.Builder(MainActivity.this, channel_ID);
            builder.setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(CustomTools.Status(val[0], MainActivity.this))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setContentText(Filename + " | " + CustomTools.Status(val[0],getApplicationContext()))
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setProgress(100,0,true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

            PendingIntent Pintent = PendingIntent.getActivity(MainActivity.this, 0, i, PendingIntent.FLAG_MUTABLE);

            builder.setContentIntent(Pintent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel Nchannel = NotificationManager.getNotificationChannel(channel_ID);
                if (Nchannel == null) {
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    Nchannel = new NotificationChannel(channel_ID, "Status: " + CustomTools.Status(val[0], MainActivity.this), importance);
                    Nchannel.setLightColor(Color.BLUE);
                    Nchannel.enableVibration(true);
                    NotificationManager.createNotificationChannel(Nchannel);
                }
            }
            NotificationManager.notify(1, builder.build());

        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            NotificationManager.notify(1, builder.build());

        }
    }

}