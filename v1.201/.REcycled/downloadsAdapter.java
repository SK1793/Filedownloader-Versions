package com.SK.filedownloader;

import static android.view.View.VISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.SK.filedownloader.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class downloadsAdapter extends ArrayAdapter<downloads>{

    String Filename, Extension, Percent, _Url, path, File_full;
    Uri file_uri;
    FileReader fr;
    ImageView file_icon;
    DocumentsProvider docP;
    DownloadManager dm;
    long d_id;
    int Pause_updatedRows, Resume_updatedRows = 0, pgr = 0,item_position=-1;

    ProgressBar PbPercentage;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String c_date = sdf.format(c.getTime());
    custom_tools tools;
    ImageButton resume,remove,pause,open;
    TextView filename ,percent , extension, more , url , loc , date, progress , size , file_size, type , resumable ;
    LinearLayout LLCardView;
    ArrayList ArrList;
    downloadsAdapter adapter;

    static long Static_Did;static downloadsAdapter Static_adapter1;static TextView Static_Percent;static ProgressBar Static_pbProgress;
    static custom_tools Static_Tools;

    public downloadsAdapter(@NonNull Context context, ArrayList<downloads> arrList) {
        super(context, 0, arrList);
        this.ArrList = arrList;

    }

    @Override
    public int getPosition(@Nullable downloads item) {
        item_position=super.getPosition(item);
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @SuppressLint({"Range", "ResourceAsColor"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Initialization Starts
        ProgressLoader pgloader=new ProgressLoader();

        adapter = new downloadsAdapter(this.getContext(), ArrList);
        Static_adapter1=adapter;
        adapter.setNotifyOnChange(true);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_downloads_adapter, parent, false);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

//        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Objects.requireNonNull(Looper.myLooper()));

        downloads current_pos = getItem(position);
        tools = new custom_tools(this.getContext());
        Static_Tools=tools;
        d_id=current_pos.get_download_id();

        //get activity variables
         filename = convertView.findViewById(R.id.tvFIlename);
                percent = convertView.findViewById(R.id.tvDownloadPercent);
                Static_Percent=percent;
                extension = convertView.findViewById(R.id.tvExtension);
                more = convertView.findViewById(R.id.tvLinkMore);
                url = convertView.findViewById(R.id.tvFileUrl);
                loc = convertView.findViewById(R.id.tvFileLoc);
                date = convertView.findViewById(R.id.tvFileDate);
                progress = convertView.findViewById(R.id.tvFileProgress);
                size = convertView.findViewById(R.id.tvFileSize);
                file_size = convertView.findViewById(R.id.tvFileSize_adapterView);
                type = convertView.findViewById(R.id.tvFileType);

                resume = convertView.findViewById(R.id.btnResume);
                remove = convertView.findViewById(R.id.btnRemove);
                pause = convertView.findViewById(R.id.btnPause);
                open = convertView.findViewById(R.id.btnOpen);
                resumable=convertView.findViewById(R.id.tvResummable);
        file_icon = convertView.findViewById(R.id.ivFile);

        Button btnDeleteYes = convertView.findViewById(R.id.btnYes);
        Button btnDeleteNo = convertView.findViewById(R.id.btnNo);

         PbPercentage = convertView.findViewById(R.id.PbDownloadProgress);
        Static_pbProgress=PbPercentage;
        CardView CVdelete = convertView.findViewById(R.id.CVdeleteCard);

        LinearLayout layoutUrl = convertView.findViewById(R.id.llFileDetailsContainer);

        //Initialization Ends

         dm = (DownloadManager) this.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

//        pDialog.setTitle("Download List");
//        pDialog.setMessage("Loading List");

        if (current_pos == null) {
//            Log.d(TAG, "=>downloads_adapter- There are no values in Array for adding into Adapter Item ");
//            Toast.makeText(this.getContext(), "There are no values Added!please try again.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "No Data to Display", Toast.LENGTH_SHORT).show();
        } else {


            //Set Update on Change in whole Dataset
//            adapter.registerDataSetObserver(new DataSetObserver() {
//                @Override
//                public void onChanged() {
//                    super.onChanged();
////                    prg.start();
//                    adapter.notifyDataSetChanged();
//                }
//
//                @Override
//                public void onInvalidated() {
//                    super.onInvalidated();
//                }
//            });
                                    file_size.setText(tools.byte_converter(tools.File_size(current_pos.get_url())));
                                    filename.setText(current_pos.get_filename());
                                    filename.setSelected(true);
                                    extension.setText(current_pos.get_extension());
                                    url.setText("URL: ".concat(current_pos.get_url()));
                                    if ((current_pos.get_loc() != null)) {
                                        loc.setText("Location: ".concat(current_pos.get_loc()));
                                    }
                                    date.setText("Date: ".concat(current_pos.get_date()));
                                    if(tools.IsResumable(current_pos.get_url())){
                                        resumable.setText("Resumable: Yes");
                                    }else {
                                        resumable.setText("Resumable: No");
                                    }

                                    String Size = tools.byte_converter(current_pos.get_size());

                //                    file_size.setText(Size);
                                    size.setText("Size: ".concat(Size + ""));

                                    type.setText("Type: ".concat(current_pos.get_type()));


                                    if (tools.progressbarPercent(current_pos.get_download_id()) == 100) {
                                        percent.setText(" ");
                                    }
                                    file_icon.setImageResource(R.drawable.file);

                                    switch (current_pos.get_type().toLowerCase()) {
                                        case "audio":
                                            file_icon.setImageResource(R.drawable.audio_file);
                                            break;
                                        case "text":
                                            file_icon.setImageResource(R.drawable.text_file);
                                            break;
                                        case "video":
                                            file_icon.setImageResource(R.drawable.video_file);
                                            break;
                                        case "image":
                                            file_icon.setImageResource(R.drawable.image_file);
                                            break;
                                        case "application":
                                            if (current_pos.get_extension().contains("pdf")) {
                                                file_icon.setImageResource(R.drawable.text_file);
                                            } else if (current_pos.get_extension().contains("mp3") || current_pos.get_extension().contains("aac")
                                                    || current_pos.get_extension().contains("wav") || current_pos.get_extension().contains("wma") || current_pos.get_extension().contains("aiff")
                                                    || current_pos.get_extension().contains("sbc") || current_pos.get_extension().contains("flac")) {
                                                file_icon.setImageResource(R.drawable.audio_file);
                                            } else if (current_pos.get_extension().contains("mp4") || current_pos.get_extension().contains("3gp") || current_pos.get_extension().contains("h.")
                                                    || current_pos.get_extension().contains("av") || current_pos.get_extension().contains("vp9") || current_pos.get_extension().contains("avc")) {
                                                file_icon.setImageResource(R.drawable.video_file);
                                            } else if (current_pos.get_extension().contains("apk")) {
                                                file_icon.setImageResource(R.drawable.apk_file);
                                            }else if(current_pos.get_extension().toLowerCase().contains("JPEG") || current_pos.get_extension().toLowerCase().contains("JPG")
                                                    || current_pos.get_extension().toLowerCase().contains("TIFF") || current_pos.get_extension().toLowerCase().contains("PNG00")
                                                    || current_pos.get_extension().toLowerCase().contains("SVG") || current_pos.get_extension().toLowerCase().contains("HEIF")
                                                    || current_pos.get_extension().toLowerCase().contains("JPEG") || current_pos.get_extension().toLowerCase().contains("JPEG")){
                                                file_icon.setImageResource(R.drawable.image_file);
                                            } else {
                                                file_icon.setImageResource(R.drawable.file);
                                            }
                                            break;

                                    }


                        switch (tools.Status(current_pos.get_download_id(), downloadsAdapter.this.getContext())){
                            case "Download":
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        //                            pgloader.execute(current_pos.get_download_id());
                                        progress.setText("Status: Downloading");
//                                percent.setText(tools.progressbarPercent(current_pos.get_download_id())+"");
//                                PbPercentage.setProgress(tools.progressbarPercent(current_pos.get_download_id()));
                                        percent.setText(tools.Percentage(current_pos.get_download_id()));
                                        resume.setEnabled(false);
                                        pause.setEnabled(true);
                                        adapter.notifyDataSetChanged();
                                    }});
                                break;

                            case "Successfull":
            //                            file_size.setText(tools.byte_converter(tools.Progress(current_pos.get_download_id()))
            //                                    +"/"+tools.byte_converter(tools.File_size(current_pos.get_url())));
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.setText("Status: Successfull");
                                        percent.setText("");
                                        percent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.complete, 0);
                                        PbPercentage.setVisibility(View.GONE);
                                        resume.setEnabled(false);
                                        pause.setEnabled(false);
                                        adapter.notifyDataSetChanged();
                                    }});

                                    break;


                            case "Failed":
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
            //                            file_size.setText(tools.byte_converter(tools.Progress(current_pos.get_download_id()))
            //                                    +"/"+tools.byte_converter(tools.File_size(current_pos.get_url())));
                                progress.setText("Status: Failed");
                                percent.setText("");
                                percent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.incomplete, 0);
                                resume.setEnabled(false);
                                pause.setEnabled(false);
                                adapter.notifyDataSetChanged();
                                    }});

                                    break;


                            case "Pending":
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //                            pgloader.execute(current_pos.get_download_id());
                                        //                            file_size.setText(tools.byte_converter(tools.Progress(current_pos.get_download_id()))
                                        //                                    +"/"+tools.byte_converter(tools.File_size(current_pos.get_url())));
                                        progress.setText("Status: Pending");
                                        percent.setText("");
                                        PbPercentage.setProgress(0);
                                        //                    AnimationDrawable hourglass_anim=(AnimationDrawable) getDrawable(this.getContext(),R.drawable.hourglass);
                                        percent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hourglass, 0);
                                        resume.setEnabled(false);
                                        pause.setEnabled(false);
                                        adapter.notifyDataSetChanged();
                                    }});
                                    break;

                            case "Paused":
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //                            file_size.setText(tools.byte_converter(tools.Progress(current_pos.get_download_id()))
                                        //                                    +"/"+tools.byte_converter(tools.File_size(current_pos.get_url())));
                                        //                            pgloader.execute(current_pos.get_download_id());
                                        progress.setText("Status: paused");
                                        //                    AnimationDrawable hourglass_anim=(AnimationDrawable) getDrawable(this.getContext(),R.drawable.hourglass);
                                        percent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hourglass, 0);
                                        resume.setEnabled(true);
                                        pause.setEnabled(false);
                                        adapter.notifyDataSetChanged();
                                    }});
                                    break;
                        }
//            file_size.setText(tools.byte_converter(tools.Progress(current_pos.get_download_id()))
//                    +"/"+tools.byte_converter(tools.File_size(current_pos.get_url())));
                    file_size.setText(tools.byte_converter(tools.File_size(current_pos.get_url())));
                    final boolean[] isFinished={};

            final int[] prg = {0};

                                new ProgressLoader().execute(d_id);

//                                final long total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                                final long downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                                progress1[0] = (int) ((downloaded * 100L) / total);

//                            percent.setText(tools.Percentage(current_pos.get_download_id())+"");

//
//                            PbPercentage.setProgress(Math.abs(tools.progressbarPercent(current_pos.get_download_id())));
//                            adapter.notifyDataSetChanged();



        }


        //on press more
        more.setOnClickListener(view -> {

            String text_more = more.getText().toString();
            String text_less = more.getText().toString();

            if (text_more.contentEquals("More")) {
                layoutUrl.setVisibility(VISIBLE);
                more.setText("less");

            } else {
                layoutUrl.setVisibility(View.GONE);
                more.setText("More");
            }
        });

        open.setOnClickListener(v -> {
        Executor exec1= Executors.newSingleThreadExecutor();
//            Log.d(TAG, "Download ID: "+current_pos.get_download_id());

            exec1.execute(() ->{
                if (!tools.Status(current_pos.get_download_id(), getContext()).contains("Successfull")) {
                    Toast.makeText(getContext(), "Download has not Completed", Toast.LENGTH_SHORT).show();
                } else {
                    try {

                        path = tools.local_path(current_pos.get_download_id());

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                        Uri uri = Uri.parse(path);
//                    Log.d(TAG, "Uri(Open): " + uri);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        int flagActivityNewTask = intent.FLAG_ACTIVITY_NEW_TASK;
                        intent.setFlags(flagActivityNewTask);
//                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();
                        intent.setData(uri);

                        getContext().startActivity(intent);
                    }
                    catch (Error e) {

                        Toast.makeText(getContext(), "couldn't find file", Toast.LENGTH_SHORT).show();
                        path = Environment.getExternalStorageDirectory() + "/download";
                        Uri uri_ = Uri.parse(path);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(uri_, "*/*");
                        getContext().startActivity(intent);
                    }
                }
                    });

        });

        resume.setOnClickListener(v -> {
            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 0); // Pause Control Value

            try {
                Resume_updatedRows = getContext()
                        .getContentResolver()
                        .update(Uri.parse("content://downloads/my_downloads/" + current_pos.get_download_id() + ""),
                                pauseDownload,
                                "title=?",
                                new String[]{current_pos.get_filename()});
                if (Resume_updatedRows > 0 && tools.Status(current_pos.get_download_id(), getContext()).toLowerCase().contains("downl")) {

                    resume.setEnabled(false);
                    pause.setEnabled(true);

                    Toast.makeText(getContext(), "Download Resumed!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Couldn't Resume Download", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                Log.e(TAG, "Failed to  Resume Download: " + current_pos.get_filename());
            }
            adapter.notifyDataSetChanged();
        });

        pause.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {

            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 1); // Pause Control Value

            try {
                Pause_updatedRows = getContext()
                        .getContentResolver()
                        .update(Uri.parse("content://downloads/my_downloads/" + current_pos.get_download_id() + ""),
                                pauseDownload,
                                "title=?",
                                new String[]{current_pos.get_filename()});
                if (Pause_updatedRows > 0) {

                    resume.setEnabled(true);
                    pause.setEnabled(false);

                    Toast.makeText(getContext(), "Download Paused!!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "Couldn't Pause Download", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to  Pause Download: " + current_pos.get_filename());
            }
            adapter.notifyDataSetChanged();
                }
            }).start();
        });

        remove.setOnClickListener(v -> {
            int isvisible = CVdelete.getVisibility();

            if (isvisible != VISIBLE) {
                CVdelete.setVisibility(VISIBLE);
            } else {
                CVdelete.setVisibility(View.GONE);

            }

//        int removed=dm.remove(current_pos.get_download_id());
        });

        btnDeleteYes.setOnClickListener(v -> {


            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_PENDING | DownloadManager.STATUS_RUNNING | DownloadManager.STATUS_SUCCESSFUL | DownloadManager.STATUS_PAUSED);

            Cursor c = dm.query(query);
            if (c.moveToNext()) {
                // Here you have all the downloades list which are running, failed, pending
                // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
                int removed = dm.remove(current_pos.get_download_id());

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

//                Uri uri = Uri.parse("file://" + file.get
//               AbsolutePath());

//                Log.d(TAG, "Uri: " + current_pos.get_file_uri().getPath());
                File file = new File(current_pos.get_file_uri().getPath());
                Boolean isDeleted = false;
//                ContentResolver cr=getContext().getContentResolver();
//                int deletedRows=0;

                if (removed > 0) {
                    Snackbar.make(v, "Deleting file", Snackbar.LENGTH_SHORT).show();

                    try {
                        if (file.exists()) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                isDeleted = file.delete();
//                                deletedRows = cr.delete(current_pos.get_file_uri(), null);
                                if (isDeleted) {
                                    ArrList.remove(getPosition(current_pos));
//                    adapter.remove(adapter.getItem(position));
                                    adapter.notifyDataSetChanged();
                                    CVdelete.setVisibility(View.GONE);
//                                    Log.d(TAG, "File Deleted");
                                    Toast.makeText(getContext(), "File deleted", Toast.LENGTH_SHORT).show();

                                } else {
//                                    Log.d(TAG, "File not Deleted");
                                    Toast.makeText(getContext(), "File not deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "File not found!", Toast.LENGTH_SHORT).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
//                        Log.d(TAG, "Error caused while Deleting File");
                        Toast.makeText(getContext(), "Error caused while Deleting File", Toast.LENGTH_SHORT).show();
                    }

//                    Log.d(TAG, "position: "+position);
                    Toast.makeText(getContext(), "Download Removed: " + removed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Couldn't remove Download ", Toast.LENGTH_SHORT).show();
                }
            }


            adapter.notifyDataSetChanged();
        });

        btnDeleteNo.setOnClickListener(v -> {
//            DownloadManager.Query query = new DownloadManager.Query();
//            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL);
//
//            Cursor c = dm.query(query);
//            if(c.moveToNext()) {
            // Here you have all the downloades list which are running, failed, pending
            // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
//                int removed=dm.remove(current_pos.get_download_id());

            try {
                ArrList.remove(position);
//                        adapter.remove((downloads) ArrList.get(position));
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Download Removed", Toast.LENGTH_SHORT).show();

            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Couldn't Remove Download", Toast.LENGTH_SHORT).show();
            }
//                adapter.remove(adapter.getItem(position));

//            }
            CVdelete.setVisibility(View.GONE);

//            adapter.notifyDataSetChanged();
        });

        adapter.notifyDataSetChanged();

        return convertView;

    }

    public class ProgressLoader extends AsyncTask<Long, Integer, Void> {
        int i=0,progress1=0;
        int prg = 0;
        final long[] total={0};

        Intent intent=new Intent();


        @SuppressLint("Range")
        @Override
        protected Void doInBackground(Long... val) {
//            Log.d(TAG, "doInBackground: id: "+val[0]);
//            Log.d(TAG, "doInBackground: Percent: "+tools.progressbarBytes(val[0]));

            prg=tools.progressbarPercent(d_id);

            for(i=prg;i<100;i++){

                try {
                    Thread.sleep(500);
                    publishProgress(tools.progressbarBytes(d_id));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
//            Log.d(TAG, "doInBackground: Progress: "+tools.progressbarBytes(d_id));

             return null;
        }

        @SuppressLint("Range")
        @Override
        protected void onProgressUpdate(Integer... val) {
            super.onProgressUpdate(val);

            percent.setText(val[0]+"");
            PbPercentage.setProgress(val[0]);
            adapter.notifyDataSetChanged();
        }

        @SuppressLint("Range")
        @Override
        protected void onPostExecute(Void values) {
            super.onPostExecute(values);
            percent.setText("");
            resume.setEnabled(false);
            pause.setEnabled(false);

            PbPercentage.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();

        }
    }

//    private void LoadFragmentz(Fragment fragment){
//        FragmentManager fm= fragment.getParentFragmentManager();
//        FragmentTransaction ft=fm.beginTransaction();
//        ft.replace(R.id.FrameL1,fragment);
//        ft.commit();
//
//    }


}



