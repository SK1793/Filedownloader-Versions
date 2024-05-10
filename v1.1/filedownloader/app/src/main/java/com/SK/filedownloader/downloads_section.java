package com.SK.filedownloader;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.view.View.VISIBLE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class downloads_section extends RecyclerView.Adapter<downloads_section.ViewHolder> {

    ArrayList<downloads> d_arr;
    Context context;
    long D_id;
    String filename,extension,path,filename_saved;
    private boolean loading = false;
    private int page = 1;
    String FIle_path;Boolean isDeleted=false;
    List res;
    DbDownloader db1;
    RecyclerView rv;
    public downloads_section(ArrayList<downloads> D_arr, Context context1){
        this.d_arr=D_arr;this.context=context1;
        db1=new DbDownloader(context1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.downloads_adapter2,parent,false);

//        switch (viewType){
//            case 0:
//                createViewHolder(parent,viewType).btnMore.setVisibility(View.VISIBLE);
//            case 1:
//                createViewHolder(parent,viewType).btnMore.setVisibility(View.GONE);
//
//        }

        return new ViewHolder(v,context).linkAdapter(this);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        long start = System.currentTimeMillis();
        custom_tools tools=new custom_tools(this.context);
        downloads item = d_arr.get(position);
        final downloads[] d_arr1 = new downloads[1];

        holder.btnPause.setEnabled(false);
        holder.btnResume.setEnabled(false);

        holder.btnDeleteYes.setBackgroundColor(Color.BLACK);
        holder.btnDeleteNo.setBackgroundColor(Color.BLACK);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

        try {
             d_arr1[0] = d_arr.get(position);

            holder.D_name.setText(d_arr1[0].get_filename());
            holder.D_name.setSelected(true);

            holder.D_ext.setText(d_arr1[0].get_extension());
            holder.btnPause.setEnabled(true);
            holder.btnResume.setEnabled(false);

            //get download id
            D_id= d_arr1[0].get_download_id();
//            Log.d(TAG, "Status: "+tools.Status(d_arr1[0].get_download_id(),this.context));
            filename_saved= d_arr1[0].get_filename_saved();

//            if(d_arr1[0].get_percent()!=null) {
//                holder.D_status.setText(d_arr1[0].get_status());
//            }else{
//                holder.D_status.setText("-");
//            }

            switch (d_arr1[0].get_type().toLowerCase()){
                case "audio":holder.ivFIle_icon.setImageResource(R.drawable.audio_file);
                    break;
                case "text":holder.ivFIle_icon.setImageResource(R.drawable.text_file);
                    break;
                case "video":holder.ivFIle_icon.setImageResource(R.drawable.video_file);
                    break;
                case "application":
                    if(d_arr1[0].get_extension().contains("pdf")){
                        holder.ivFIle_icon.setImageResource(R.drawable.text_file);
                    }else if(d_arr1[0].get_extension().contains("apk")){
                        holder.ivFIle_icon.setImageResource(R.drawable.apk_file);
                    }else if(d_arr1[0].get_extension().contains("application")){
                        holder.ivFIle_icon.setImageResource(R.drawable.apk_file);
                    }else{
                        holder.ivFIle_icon.setImageResource(R.drawable.file);
                    }
                    break;

            }

            String Size=tools.byte_converter(d_arr1[0].get_size());

            holder.D_Url.setText("Url: "+ d_arr1[0].get_url());
            holder.D_date.setText("Date: "+ d_arr1[0].get_date());
            if(d_arr1[0].get_file_uri()!=null){
                holder.D_loc.setText("Location: "+d_arr1[0].get_file_uri());
            }else{
                holder.D_loc.setText("Location: "+ "Download/FileDownloader"+d_arr1[0].get_filename_saved());
            }
            holder.D_type.setText("Type: "+ d_arr1[0].get_type());
            holder.D_size.setText("Size: "+Size);
            holder.D_Size_adapter.setText(Size);
            holder.D_status.setText(tools.Percentage(d_arr1[0].get_download_id()));
            holder.D_ID.setText(d_arr1[0].get_download_id()+"");

//            Thread Status_thread=new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.d(TAG, "Download_section-Details-> Percent_str= "
//                            +tools.Percentage(d_arr1[0].get_download_id())+", Status= "+tools.Status(d_arr1[0].get_download_id(),context)+
//                            ", Percent= "+tools.progressbarPercent(d_arr1[0].get_download_id()));
//                }
//            });Status_thread.start();

             if (d_arr1[0].get_percent()!=null && !tools.Status(d_arr1[0].get_download_id(),context).contains("Failed") &&
                     tools.Status(d_arr1[0].get_download_id(),context).contains("Pending/Paused")) {
                 if (!d_arr1[0].get_percent().contains("100")) {
//                    while (!d_arr1[0].get_percent().contains("100")){

                        holder.D_status.setText(tools.Percentage(d_arr1[0].get_download_id()));
                        holder.D_progress.setText("Progress: "+tools.Status(d_arr1[0].get_download_id(),context));
                        holder.PbDProgress.setProgress(Math.abs(tools.progressbarPercent(d_arr1[0].get_download_id())));

//                    }
                     // Code to run on the UI thread

                 }
//                holder.D_progress.setText("Progress: "+ tools.Percentage(d_arr1[0].get_download_id()));
            }else if(tools.Status(d_arr1[0].get_download_id(),context).contains("Failed")){
                 holder.btnPause.setEnabled(false);
                 holder.btnResume.setEnabled(false);
                 holder.D_status.setText(tools.Percentage(d_arr1[0].get_download_id()));
                 holder.D_progress.setText("Progress: "+tools.Status(d_arr1[0].get_download_id(),context));
                 holder.PbDProgress.setProgress(Math.abs(tools.progressbarPercent(d_arr1[0].get_download_id())));
             }

             else if(tools.Status(d_arr1[0].get_download_id(),context).contains("Pending")){
                 holder.btnPause.setEnabled(false);
                 holder.btnResume.setEnabled(false);
                 holder.D_status.setText(tools.Percentage(d_arr1[0].get_download_id()));
                 holder.D_progress.setText("Progress: "+tools.Status(d_arr1[0].get_download_id(),context));
                 holder.PbDProgress.setProgress(Math.abs(tools.progressbarPercent(d_arr1[0].get_download_id())));

             }else if(tools.Status(d_arr1[0].get_download_id(),context).contains("Paused")){
                 holder.btnPause.setEnabled(false);
                 holder.btnResume.setEnabled(true);
                 holder.D_status.setText(tools.Percentage(d_arr1[0].get_download_id()));
                 holder.D_progress.setText("Progress: "+tools.Status(d_arr1[0].get_download_id(),context));
                 holder.PbDProgress.setProgress(Math.abs(tools.progressbarPercent(d_arr1[0].get_download_id())));

             }

             else if(tools.Status(d_arr1[0].get_download_id(),context).contains("Successfull")){
                 holder.btnPause.setEnabled(false);
                 holder.btnResume.setEnabled(false);
                 holder.D_status.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.complete1,0);
                 holder.D_status.setText(" ");
                 holder.D_progress.setText("Progress: Downloaded");
                 holder.PbDProgress.setVisibility(View.GONE);
                 holder.D_Size_adapter.setText(Size);
                 holder.D_type.setText("Type: "+ d_arr1[0].get_type());
                 holder.D_size.setText("Size: "+Size);
             }

             else
            {
                holder.D_status.setText("NA");
                holder.D_progress.setText("NA");
                if(tools.progressbarPercent(d_arr1[0].get_download_id())>0){

                    holder.PbDProgress.setProgress(Math.abs(tools.progressbarPercent(d_arr1[0].get_download_id())));
                }else{
                    holder.PbDProgress.setProgress(0);

                }

            }

            //update changes
//            notifyItemInserted(position);

        }catch (IndexOutOfBoundsException e){
            Log.d(String.valueOf(1254), "=>downloads_section- Exception: "+e);
            e.printStackTrace();
        }
            }
        },50);


        holder.btnOpen.setOnClickListener(v->{

            if(!tools.Status(d_arr1[0].get_download_id(),this.context).contains("Successfull")){
                Toast.makeText(this.context,"Download has not Completed",Toast.LENGTH_SHORT).show();
            }else {

                    d_arr1[0] = d_arr.get(position);
//                filename_saved=res.get(2).toString();
                    filename_saved = d_arr1[0].get_filename_saved();
                    path = tools.local_path(d_arr1[0].get_download_id());
//                    String type=tools.Media_type(d_arr1[0].get_url()).split("/")[0];
                try {
//To Convert File:// format to Content:// type for above api level 24
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Video.Media.DATA, Uri.parse(path).toString());
//                    Uri contentUri = context.getContentResolver().insert(
//                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
//                    Log.d(TAG, "Content: "+contentUri);

                    Uri uri = Uri.parse(path);
                    if(new File(uri.getPath()).exists()){

                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();
                        intent.setData(Uri.parse(tools.local_path(d_arr1[0].get_download_id())));
                        this.context.startActivity(intent);

                    }else{
                        Toast.makeText(this.context, " File Doesn't exist  "+d_arr1[0].get_filename(), Toast.LENGTH_SHORT).show();
                    }

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                } catch (Error e) {
                    Toast.makeText(this.context, "Error Finding File", Toast.LENGTH_SHORT).show();
                    path = Environment.getExternalStorageDirectory() + "/download";
                    Uri uri_ = Uri.parse(path);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(uri_, "*/*");
                    this.context.startActivity(intent);
                }
            }
        });
//
        holder.btnMore.setOnClickListener(view -> {
            String status=tools.Status(d_arr1[0].get_download_id(),context);
//            Log.d(TAG, "Status: "+status);
            String text_more=holder.btnMore.getText().toString();
            String text_less=holder.btnMore.getText().toString();

            if (text_more.contentEquals("More")){
                holder.Llayout.setVisibility(View.VISIBLE);
                holder.btnMore.setText("less");  holder.Llayout.requestLayout();

            } else if (text_less.contentEquals("less")) {
                holder.Llayout.setVisibility(View.GONE);
                holder.btnMore.setText("More");  holder.Llayout.requestLayout();
            }else {
                Toast.makeText(this.context,"Something is odd Try Again!",Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnResume.setOnClickListener(v->{
            int updatedRows = 0;

            ContentValues ResumeDownload = new ContentValues();
            ResumeDownload.put("control", 0); // Resume Control Value

            try {
                updatedRows = context
                        .getContentResolver()
                        .update(Uri.parse("downloads"),
                                ResumeDownload,
                                "title=?",
                                new String[]{ filename });
                if(updatedRows>0 && tools.Status(d_arr1[0].get_download_id(),context).contains("Download")) {
                    Toast.makeText(context, "Download Resumed!!!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v.getContext(), v, "Download Resumed", Snackbar.LENGTH_INDEFINITE).show();
                    holder.btnPause.setEnabled(true);
                    holder.btnResume.setEnabled(false);
                }else {
                    Toast.makeText(context, "couldn't Resume Download!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to Resume Download");
                Toast.makeText(context, "Failed to Resume Download", Toast.LENGTH_SHORT).show();
            }

        });

        holder.btnPause.setOnClickListener(v->{

            int updatedRows = 0;

            ContentValues pauseDownload = new ContentValues();
            pauseDownload.put("control", 1); // Pause Control Value

            try {
                updatedRows = context
                        .getContentResolver()
                        .update(Uri.parse("downloads"),
                                pauseDownload,
                                "title=?",
                                new String[]{ filename });
                if(updatedRows>0) {
                    Toast.makeText(context, "Download Paused!!!", Toast.LENGTH_SHORT).show();
                    Snackbar.make(v.getContext(), v, "Download Paused", Snackbar.LENGTH_INDEFINITE).show();
                    holder.btnPause.setEnabled(false);
                    holder.btnResume.setEnabled(true);
                }else {
                    Toast.makeText(context, "couldn't Pause Download!!!", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to Pause Download");
                Toast.makeText(context, "Failed to Pause Download", Toast.LENGTH_SHORT).show();
            }

//        Toast.makeText(this.context, "Download Paused", Toast.LENGTH_SHORT).show();
//            Snackbar.make(v.getContext(),v,"Download Paused",Snackbar.LENGTH_LONG).show();

        });

        holder.btnRemove.setOnClickListener(v->{
//            Log.d(TAG, "Position: "+position);

            int isvisible=holder.CVdelete.getVisibility();

            if (isvisible!=VISIBLE){
                holder.CVdelete.setVisibility(VISIBLE);
            }else{
                holder.CVdelete.setVisibility(View.GONE);

            }

//            ViewHolder vh = new ViewHolder(v, context);
//            vh.Remove_Item(holder.getAbsoluteAdapterPosition());
//            db1.deleteRow(String.valueOf(item.get_download_id()));
//            Toast.makeText(this.context, "Download removed: " + filename_saved, Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Download removed: " + filename_saved);
//
//                notifyItemChanged(holder.getAbsoluteAdapterPosition());
//                notifyItemRemoved(holder.getAbsoluteAdapterPosition());
//                notifyItemRangeRemoved(holder.getAbsoluteAdapterPosition(),1);
//                notifyDataSetChanged();
    });

        holder.btnDeleteNo.setOnClickListener(v->{
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL);

                // Here you have all the downloades list which are running, failed, pending
                // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
//                int removed=dm.remove(current_pos.get_download_id());
                try {
                    new Handler().post(()->{

                    db1.deleteRow(String.valueOf(item.get_download_id()));
                        this.d_arr.remove(position);
                        this.notifyItemRemoved(position);
                        this.notifyDataSetChanged();
                        this.notifyItemRangeChanged(position,this.getItemCount());
//                        adapter.remove((downloads) ArrList.get(position));
                    Toast.makeText(v.getContext(), "Download Removed from List", Toast.LENGTH_SHORT).show();
                        Snackbar.make(v.getContext(),v,"Loading",Snackbar.LENGTH_LONG).show();
                    });

                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Couldn't Remove Download", Toast.LENGTH_SHORT).show();
                }

                notifyDataSetChanged();

        });

        holder.btnDeleteYes.setOnClickListener(v->{

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL);

            // Here you have all the downloades list which are running, failed, pending
            // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
//                int removed=dm.remove(current_pos.get_download_id());
            FIle_path=item.get_file_uri().getPath();
            File file=new File(FIle_path);
            int Removed=0;
            try {
                Removed=dm.remove(item.get_download_id());
                if(Removed>0) {
                    if (file.exists()) {
                        new Handler().post(() -> {
                            isDeleted = file.delete();
                            if (isDeleted) {
                                db1.deleteRow(String.valueOf(item.get_download_id()));
                                this.d_arr.remove(position);
                                this.notifyItemRemoved(position);
                                this.notifyDataSetChanged();
                                this.notifyItemRangeChanged(position, this.getItemCount());
//                        adapter.remove((downloads) ArrList.get(position));
                                Toast.makeText(v.getContext(), "Download Removed from List", Toast.LENGTH_SHORT).show();
                                Snackbar.make(v.getContext(), v, "Loading", Snackbar.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(v.getContext(), "Couldn't Delete The File", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(v.getContext(), "File Doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(v.getContext(), "Couldn't remove Download", Toast.LENGTH_SHORT).show();
                }
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
                Toast.makeText(v.getContext(), "Couldn't Remove Download", Toast.LENGTH_SHORT).show();
            }

            notifyDataSetChanged();
        });

        final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                long downloadedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if (D_id == -1) {
                    Toast.makeText(context, "Failed to Download!Try Again...", Toast.LENGTH_SHORT).show();
                }

                if (d_arr1[0].get_download_id() == downloadedID) {
                    holder.btnPause.setEnabled(false);
                    holder.btnResume.setEnabled(false);
                }
            }
        };
//
//        Log.d(TAG, "=>downloads_section-Duration(onBindViewHolder): ");
//        Log.d(TAG, String.valueOf(System.currentTimeMillis()-start));
}

    @Override
    public int getItemCount() {
        return d_arr.size();
    }
//    private void loadMoreData(int index) {
//        // Simulate loading data from a network API
//        new Handler().postDelayed(() -> {
//            List<downloads> newItems = new ArrayList<>();
//            for (int i = page * 10 + 1; i <= (page + 1) * 10; i++) {
//                newItems.add(new downloads("Item " + i));
//            }
//
//            d_arr.addAll(newItems);
//            notifyItemRangeInserted(getItemCount(), newItems.size());
//
//            loading = false;
//            page++;
//        }, 2000);
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{
        LinearLayout Llayout;
        ImageButton btnOpen,btnPause,btnResume,btnRemove;
        ProgressBar Pbmore,PbDProgress;
        private downloads_section adapter;
        Button btnDeleteYes,btnDeleteNo;

        CardView CVdelete;
        private TextView D_ID,D_name,D_ext,D_status,D_progress,D_Url,D_date,D_loc,D_type,D_size,D_Size_adapter,btnMore;
        RecyclerView Rv;

        ImageView ivFIle_icon;
        SwipeRefreshLayout srLayout;
       public ViewHolder(@NonNull View itemView,Context context) {

            super(itemView);
            D_name=itemView.findViewById(R.id.tvFIlename2);
            D_ext=itemView.findViewById(R.id.tvExtension2);
            D_status=itemView.findViewById(R.id.tvDownloadPercent2);
            D_progress=itemView.findViewById(R.id.tvFileProgress2);
            D_Url=itemView.findViewById(R.id.tvFileUrl2);
            D_date=itemView.findViewById(R.id.tvFileDate2);
            D_loc=itemView.findViewById(R.id.tvFileLoc2);
            D_type=itemView.findViewById(R.id.tvFileType2);
            D_size=itemView.findViewById(R.id.tvFileSize2);
            D_ID=itemView.findViewById(R.id.tvDownloadID);
            Pbmore=itemView.findViewById(R.id.PBloadMore);
           PbDProgress=itemView.findViewById(R.id.PbDownloadProgress2);
           ivFIle_icon=itemView.findViewById(R.id.ivFile_icon2);

           D_Size_adapter=itemView.findViewById(R.id.tvFileSize_adapterView2);

           btnMore=itemView.findViewById(R.id.tvLinkMore2);
           btnOpen=itemView.findViewById(R.id.btnOpen2);
           btnPause=itemView.findViewById(R.id.btnPause2);
           btnResume=itemView.findViewById(R.id.btnResume2);
           btnRemove=itemView.findViewById(R.id.btnRemove2);
           Llayout=itemView.findViewById(R.id.llFileDetailsContainer2);

           CVdelete=itemView.findViewById(R.id.CVdeleteCard_section);
           btnDeleteYes=itemView.findViewById(R.id.btnYes_section);
           btnDeleteNo=itemView.findViewById(R.id.btnNo_section);

        }


       public ViewHolder linkAdapter(downloads_section adapter){
           this.adapter=adapter;
           return this;
       }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        }
    }
}
