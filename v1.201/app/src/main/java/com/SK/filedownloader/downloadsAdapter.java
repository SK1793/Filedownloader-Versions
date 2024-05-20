package com.SK.filedownloader;

import static android.view.View.VISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class downloadsAdapter extends ArrayAdapter<downloads>  {

    String Filename,Extension,Percent,_Url,path,File_full;
    Uri file_uri;
    FileReader fr;
    ImageView file_icon;
    DocumentsProvider docP;
    long d_id;
    int Pause_updatedRows,Resume_updatedRows = 0;


    ProgressDialog pDialog;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String c_date = sdf.format(c.getTime());
    custom_tools tools;

    LinearLayout LLCardView;
    ArrayList ArrList;
    downloadsAdapter adapter;

    public downloadsAdapter(@NonNull Context context, ArrayList<downloads> arrList) {
        super(context,0, arrList);
        this.ArrList=arrList;

    }

    @Override
    public int getPosition(@Nullable downloads item) {
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

        adapter=new downloadsAdapter(this.getContext(),ArrList);

        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.activity_downloads_adapter,parent,false);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        downloads current_pos=getItem(position);
        tools=new custom_tools(this.getContext());

        //get activity variables
        TextView filename=convertView.findViewById(R.id.tvFIlename),
                percent=convertView.findViewById(R.id.tvDownloadPercent),
                extension=convertView.findViewById(R.id.tvExtension),
                more =convertView.findViewById(R.id.tvLinkMore),
                url =convertView.findViewById(R.id.tvFileUrl),
                loc =convertView.findViewById(R.id.tvFileLoc),
                date =convertView.findViewById(R.id.tvFileDate),
                progress =convertView.findViewById(R.id.tvFileProgress),
                size =convertView.findViewById(R.id.tvFileSize),
                file_size =convertView.findViewById(R.id.tvFileSize_adapterView),
                type =convertView.findViewById(R.id.tvFileType);

        ImageButton resume =convertView.findViewById(R.id.btnResume),
                remove=convertView.findViewById(R.id.btnRemove),
                pause =convertView.findViewById(R.id.btnPause),
                open =convertView.findViewById(R.id.btnOpen);
        file_icon=convertView.findViewById(R.id.ivFile);
//        resume.setBackgroundColor(Color.parseColor("#49d16d"));
//        pause.setBackgroundColor(Color.parseColor("#d1ba24"));
//        remove.setBackgroundColor(Color.parseColor("#d9473f"));

        Button btnDeleteYes=convertView.findViewById(R.id.btnYes);
        Button btnDeleteNo=convertView.findViewById(R.id.btnNo);

        ProgressBar PbPercentage=convertView.findViewById(R.id.PbDownloadProgress);
        CardView CVdelete=convertView.findViewById(R.id.CVdeleteCard);

        LinearLayout layoutUrl=convertView.findViewById(R.id.llFileDetailsContainer);

        //Initialization Ends

        DownloadManager dm = (DownloadManager) this.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

//        pDialog.setTitle("Download List");
//        pDialog.setMessage("Loading List");

        if(current_pos==null){
            Log.d(TAG, "=>downloads_adapter- There are no values in Array for adding into Adapter Item ");
//            Toast.makeText(this.getContext(), "There are no values Added!please try again.", Toast.LENGTH_SHORT).show();
            Toast.makeText(getContext(), "No Data to Display", Toast.LENGTH_SHORT).show();
        }else {

            Executor executor1 = Executors.newFixedThreadPool(10); // Creates an executor with 10 threads

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

            final int[] D_progress = {0};
            if(tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("succes")
             || tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("down") ||
                    tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("pause") ||
                    tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("failed") ||
                    tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("pend")){
                setIcon(current_pos);
            }

            executor.execute(() -> {
                //Background work here

                do{
                    D_progress[0]=tools.progressbarPercent(current_pos.get_download_id());

                    handler.postDelayed(() -> {
                        PbPercentage.setProgress(0);
                        //UI Thread work here

                        if(D_progress[0]==100){
                            percent.setText(" ");
                            percent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.complete,0,0,0);
                        }else {
                            percent.setText(D_progress[0]+"%");

                        }

                        PbPercentage.setProgress(Math.abs(D_progress[0]));
//                        percent.setText(Math.abs(D_progress[0])+"");
                        adapter.notifyDataSetChanged();

                    },50);

                    if(tools.Status(current_pos.get_download_id(),getContext()).toLowerCase().contains("succes")){

//                        PbPercentage.setVisibility(View.GONE);
//                        percent.setText(" ");
//                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.complete1,0);
                        pause.setEnabled(false);
                        resume.setEnabled(false);

                    }
                }

                while(tools.Status(current_pos.get_download_id(),getContext()).contains("Download") ||
                        tools.Status(current_pos.get_download_id(),getContext()).contains("Success"));
            });


            Log.d(TAG, "Type: "+current_pos.get_type().toLowerCase());
            Log.d(TAG, "Extension: "+current_pos.get_extension().toLowerCase());


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    filename.setText(current_pos.get_filename());
                    filename.setSelected(true);
                    extension.setText(current_pos.get_extension());
                    url.setText("URL: ".concat(current_pos.get_url()));
                    if((current_pos.get_loc()!=null)){
                        loc.setText("Location: ".concat(current_pos.get_loc()));
                    }
                    date.setText("Date: ".concat(current_pos.get_date()));

                    String Size=tools.byte_converter(current_pos.get_size());

                    file_size.setText(Size);
                    size.setText("Size: ".concat(Size+""));

                    type.setText("Type: ".concat(current_pos.get_type()));
                    if(D_progress[0]==100){
                        percent.setText(" ");
                    }else {
                        percent.setText(D_progress[0]+"%");

                    }
                    file_icon.setImageResource(R.drawable.file);

                    progress.setText("Progress: ".concat(tools.Status(current_pos.get_download_id(),getContext())));

//                    Cursor cursor1 = dm.query(new DownloadManager.Query().setFilterById(current_pos.get_download_id()));

//                    while (tools.Status(current_pos.get_download_id(),getContext()).contains("Downloading")){
//                        h.postDelayed(run1,500);
//                        Log.d(TAG, "ProgressBar_value: "+ PbPercentage.getProgress());
//                        Log.d(TAG, "ProgressBar(input): "+d_progress[0]);
//                    }

                    if(tools.Status(current_pos.get_download_id(),getContext()).contains("Downloading")
                    ) {

                        progress.setText("Status: Downloading");
                        setIcon(current_pos);
                        percent.setText(tools.Percentage(current_pos.get_download_id()));
                        resume.setEnabled(false);
                        pause.setEnabled(true);
                        adapter.notifyDataSetChanged();
                    }
                    else if (tools.Status(current_pos.get_download_id(),getContext()).contains("Successfull") ||
                            tools.Status(current_pos.get_download_id(),getContext()).contains("N/A")){
                        progress.setText("Status: Successfull");
                        setIcon(current_pos);
                        percent.setText(" ");
                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.complete,0);
                        PbPercentage.setVisibility(View.GONE);
                        resume.setEnabled(false);
                        pause.setEnabled(false);
                    }
                    else if (tools.Status(current_pos.get_download_id(),getContext()).contains("Pending")){
                        progress.setText("Status: Pending");
                        setIcon(current_pos);
//                    AnimationDrawable hourglass_anim=(AnimationDrawable) getDrawable(this.getContext(),R.drawable.hourglass);
                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.hourglass,0);
                        resume.setEnabled(false);
                        pause.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                    else if (tools.Status(current_pos.get_download_id(),getContext()).contains("Pause")){
                        progress.setText("Status: paused");
                        setIcon(current_pos);
//                    AnimationDrawable hourglass_anim=(AnimationDrawable) getDrawable(this.getContext(),R.drawable.hourglass);
                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.hourglass,0);
                        resume.setEnabled(true);
                        pause.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                    else if (tools.Status(current_pos.get_download_id(),getContext()).contains("Failed")){
                        progress.setText("Status: Failed");
                        setIcon(current_pos);
                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.incomplete,0);
                        resume.setEnabled(false);
                        pause.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                    else{
                        progress.setText(" Error while Downloading ("+tools.Status(current_pos.get_download_id(),getContext())+")");
                        percent.setText("Status: Error Try again");
                        percent.setText("");
                        setIcon(current_pos);
                        percent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.hourglass,0);
//                        PbPercentage.setBackgroundColor(Color.red(200));
                        resume.setEnabled(false);
                        pause.setEnabled(false);
                        adapter.notifyDataSetChanged();
                    }
                }
            },50);

        }

        //on press more
        more.setOnClickListener(view -> {

            String text_more=more.getText().toString();
            String text_less=more.getText().toString();

            if (text_more.contentEquals("More")){
                layoutUrl.setVisibility(VISIBLE);
                more.setText("less");

            } else  {
                layoutUrl.setVisibility(View.GONE);
                more.setText("More");
            }

//            DownloadManager.Query query = new DownloadManager.Query().setFilterById(current_pos.get_download_id());
//            Cursor c = dm.query(query);
//            if (c.getCount() != 0) {
//                while (c.moveToNext()) {
//                    Log.i("Download File Info", "-------------Starts--------------");
//                    Log.i("Download File Info", "Public URL: " + c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_URI)));
//                    Log.i("Download File Info", "Title/Filename " + c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)));
//                    Log.i("Download File Info", "Local URI " + c.getString(c.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI)));
//                    Log.i("Download File Info", "Status " + c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)));
//                    Log.i("Download File Info", "Reason " + c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON)));
//                    Log.i("Download File Info", "DownloadID " + c.getLong(c.getColumnIndexOrThrow(DownloadManager.COLUMN_ID)));
//                    Log.i("Download File Info", "**********************************");
//                }
//            }
        });

        open.setOnClickListener(v->{

//            Log.d(TAG, "Download ID: "+current_pos.get_download_id());

            if(!tools.Status(current_pos.get_download_id(),this.getContext()).contains("Successfull")){
                Toast.makeText(this.getContext(),"Download has not Completed",Toast.LENGTH_SHORT).show();
            }else{
                try {

                    path = tools.local_path(current_pos.get_download_id());

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                    Uri uri = Uri.parse(path);
                    Log.d(TAG, "Uri(Open): "+uri);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    int flagActivityNewTask = intent.FLAG_ACTIVITY_NEW_TASK;
                    intent.setFlags(flagActivityNewTask);
//                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();
                    intent.setData(uri);

                    getContext().startActivity(intent);
                }catch (Error e){

                    Toast.makeText(this.getContext(), "couldn't find file", Toast.LENGTH_SHORT).show();
                    path = Environment.getExternalStorageDirectory() + "/download";
                    Uri uri_=Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri_, "*/*");
                    this.getContext().startActivity(intent);
                }
            }
        });

        resume.setOnClickListener(v->{
            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 0); // Pause Control Value

            try {
                Resume_updatedRows = getContext()
                        .getContentResolver()
                        .update(Uri.parse("content://downloads/my_downloads/"+current_pos.get_download_id()+""),
                                pauseDownload,
                                "title=?",
                                new String[]{ current_pos.get_filename() });
                if(Resume_updatedRows>0 && tools.Status(current_pos.get_download_id(),getContext()).contains("Downloading")){

                    resume.setEnabled(false);
                    pause.setEnabled(true);

                    Toast.makeText(getContext(), "Download Resumed!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(v.getContext(), "Couldn't Resume Download", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to  Resume Download: "+current_pos.get_filename());
            }
            adapter.notifyDataSetChanged();
        });

        pause.setOnClickListener(v->{
            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 1); // Pause Control Value

            try {
                Pause_updatedRows = getContext()
                        .getContentResolver()
                        .update(Uri.parse("content://downloads/my_downloads/"+current_pos.get_download_id()+""),
                                pauseDownload,
                                "title=?",
                                new String[]{ current_pos.get_filename() });
                if(Pause_updatedRows>0){

                    resume.setEnabled(true);
                    pause.setEnabled(false);

                    Toast.makeText(getContext(), "Download Paused!!!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(v.getContext(), "Couldn't Pause Download", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to  Pause Download: "+current_pos.get_filename());
            }
            adapter.notifyDataSetChanged();
        });

        remove.setOnClickListener(v->{
            int isvisible=CVdelete.getVisibility();

            if (isvisible!=VISIBLE){
                CVdelete.setVisibility(VISIBLE);
            }else{
                CVdelete.setVisibility(View.GONE);

            }

//        int removed=dm.remove(current_pos.get_download_id());
        });

        btnDeleteYes.setOnClickListener(v->{
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_PAUSED);

            Cursor c = dm.query(query);
            if(c.moveToNext()) {
                // Here you have all the downloades list which are running, failed, pending
                // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
                int removed=dm.remove(current_pos.get_download_id());

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

//                Uri uri = Uri.parse("file://" + file.get
//               AbsolutePath());

                Log.d(TAG, "Uri: "+current_pos.get_file_uri().getPath());
                File file=new File(current_pos.get_file_uri().getPath());
                Boolean isDeleted=false;
//                ContentResolver cr=getContext().getContentResolver();
//                int deletedRows=0;

                if(removed>0){
                    Snackbar.make(v,"Deleting file",Snackbar.LENGTH_SHORT).show();

                    try{
                        if(file.exists()) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                isDeleted = file.delete();
//                                deletedRows = cr.delete(current_pos.get_file_uri(), null);
                                if (isDeleted) {
                                    ArrList.remove(getPosition(current_pos));
//                    adapter.remove(adapter.
//                    getItem(position));
                                    adapter.notifyDataSetChanged();
                                    CVdelete.setVisibility(View.GONE);
                                    Log.d(TAG, "File Deleted");
                                    Toast.makeText(getContext(), "File deleted", Toast.LENGTH_SHORT).show();

                                } else {
                                    Log.d(TAG, "File not Deleted");
                                    Toast.makeText(getContext(), "File not deleted", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            Toast.makeText(this.getContext(), "File not found!",Toast.LENGTH_SHORT).show();

                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        Log.d(TAG, "Error caused while Deleting File");
                        Toast.makeText(this.getContext(), "Error caused while Deleting File", Toast.LENGTH_SHORT).show();
                    }

//                    Log.d(TAG, "position: "+position);
                    Toast.makeText(this.getContext(), "Download Removed: "+removed, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this.getContext(), "Couldn't remove Download ", Toast.LENGTH_SHORT).show();
                }
            }

            adapter.notifyDataSetChanged();
        });

        btnDeleteNo.setOnClickListener(v->{
//            DownloadManager.Query query = new DownloadManager.Query();
//            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL);
//
//            Cursor c = dm.query(query);
//            if(c.moveToNext()) {
            // Here you have all the downloades list which are running, failed, pending
            // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
//                int removed=dm.remove(current_pos.get_download_id());
            try{
                ArrList.remove(position);
//                        adapter.remove((downloads) ArrList.get(position));
                adapter.notifyDataSetChanged();
                Toast.makeText(this.getContext(), "Download Removed", Toast.LENGTH_SHORT).show();

            }catch (UnsupportedOperationException e){
                e.printStackTrace();
                Toast.makeText(this.getContext(), "Couldn't Remove Download", Toast.LENGTH_SHORT).show();
            }
//                adapter.remove(adapter.getItem(position));

//            }
            CVdelete.setVisibility(View.GONE);
//            adapter.notifyDataSetChanged();
        });

        adapter.notifyDataSetChanged();

        return convertView;

    }

   public void setIcon(downloads pos){

       if(pos.get_type().toLowerCase().contains("audio")){
           file_icon.setImageResource(R.drawable.audio_file);
       }else if (pos.get_type().toLowerCase().contains("text")) {
           file_icon.setImageResource(R.drawable.text_file);
       }else if (pos.get_type().toLowerCase().contains("video")) {
           file_icon.setImageResource(R.drawable.video_file);
       }else if (pos.get_type().toLowerCase().contains("application"))
       {
           if (pos.get_extension().toLowerCase().contains("pdf")) {
               file_icon.setImageResource(R.drawable.text_file);
           } else if (pos.get_extension().toLowerCase().contains("mp3") || pos.get_extension().toLowerCase().contains("aac")
                   || pos.get_extension().toLowerCase().contains("wav") || pos.get_extension().toLowerCase().contains("wma") || pos.get_extension().toLowerCase().contains("aiff")
                   || pos.get_extension().toLowerCase().contains("sbc") || pos.get_extension().toLowerCase().contains("flac")) {
               file_icon.setImageResource(R.drawable.audio_file);
           } else if (pos.get_extension().toLowerCase().contains("mp4") || pos.get_extension().toLowerCase().contains("3gp") || pos.get_extension().toLowerCase().contains("h.")
                   || pos.get_extension().toLowerCase().contains("av") || pos.get_extension().toLowerCase().contains("vp9") || pos.get_extension().toLowerCase().contains("avc")) {
               file_icon.setImageResource(R.drawable.video_file);
           } else if (pos.get_extension().toLowerCase().contains("apk")) {
               file_icon.setImageResource(R.drawable.apk_file);
           } else if(pos.get_extension().toLowerCase().contains("jpeg") || pos.get_extension().toLowerCase().contains("jpg")
                   || pos.get_extension().toLowerCase().contains("tiff") || pos.get_extension().toLowerCase().contains("png")
                   || pos.get_extension().toLowerCase().contains("svg") || pos.get_extension().toLowerCase().contains("heif")){
               file_icon.setImageResource(R.drawable.image_file);
           } else {
               file_icon.setImageResource(R.drawable.file);
           }
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




