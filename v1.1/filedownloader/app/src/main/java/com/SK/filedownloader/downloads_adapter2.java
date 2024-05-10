package com.SK.filedownloader;

import static android.content.Context.DOWNLOAD_SERVICE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class downloads_adapter2 extends ArrayAdapter<downloads> {

    String Filename,Extension,Percent,_Url,path,File_full;
    Uri file_uri;
    FileReader fr;
    DocumentsProvider docP;
    long d_id;
   custom_tools Custom_Tools;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String c_date = sdf.format(c.getTime());



    public downloads_adapter2(@NonNull Context context, ArrayList<downloads> arrList) {
        super(context,0, arrList);

    }

    @SuppressLint("Range")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.downloads_adapter2,parent,false
            );
        }
        Custom_Tools=new custom_tools(this.getContext());

        downloads current_pos=getItem(position);

        //get activity variables
        TextView filename=convertView.findViewById(R.id.tvFIlename2);
        TextView percent=convertView.findViewById(R.id.tvDownloadPercent2);
        TextView extension=convertView.findViewById(R.id.tvExtension2);

        ImageButton resume = convertView.findViewById(R.id.btnResume2);
        ImageButton remove = convertView.findViewById(R.id.btnRemove2);
        ImageButton pause = convertView.findViewById(R.id.btnPause2);
        ImageButton open = convertView.findViewById(R.id.btnOpen2);
//        resume.setBackgroundColor(Color.parseColor("#49d16d"));
//        pause.setBackgroundColor(Color.parseColor("#d1ba24"));
//        remove.setBackgroundColor(Color.parseColor("#d9473f"));

        TextView more = convertView.findViewById(R.id.tvLinkMore2);
        TextView url = convertView.findViewById(R.id.tvFileUrl2);
        TextView loc = convertView.findViewById(R.id.tvFileLoc2);
        TextView date = convertView.findViewById(R.id.tvFileDate2);
        TextView progress = convertView.findViewById(R.id.tvFileProgress2);
        TextView size = convertView.findViewById(R.id.tvFileSize2);
        TextView file_size = convertView.findViewById(R.id.tvFileSize_adapterView2);
        TextView type = convertView.findViewById(R.id.tvFileType2);
        LinearLayout layoutUrl = convertView.findViewById(R.id.llFileDetailsContainer2);

        if(current_pos==null){
            Log.d(TAG, "=>downloads_adapter- There are no values in Array for adding into Adapter Item ");
//            Toast.makeText(this.getContext(), "There are no values Added!please try again.", Toast.LENGTH_SHORT).show();
        }

        //on press more
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
                Toast.makeText(this.getContext(),"Something is odd Try Again!",Toast.LENGTH_SHORT).show();
            }
        });

        open.setOnClickListener(v->{

            DownloadManager dm = (DownloadManager) getContext().getSystemService(DOWNLOAD_SERVICE);
            Uri uri_file= dm.getUriForDownloadedFile(d_id);
            String mime= dm.getMimeTypeForDownloadedFile(d_id);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Download ID: "+d_id);
                    Log.d(TAG, "uri : "+uri_file);
                    Log.d(TAG, "mime : "+mime);
                }
            }).start();


            try {

                path = "/storage/emulated/0/Download/"+File_full;

//                Uri uri = Uri.parse("file://" + file.getAbsolutePath());

                Uri uri = Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();

                if (mime!=null) {
                    intent.setDataAndTypeAndNormalize(uri, mime);
                } else {
                    intent.setDataAndTypeAndNormalize(uri, "*/*");
                }

                this.getContext().startActivity(intent);
            }catch (Error e){

                Toast.makeText(this.getContext(), "couldn't find file", Toast.LENGTH_SHORT).show();
                path = "/storage/emulated/0/Download";
                Uri uri_=Uri.parse(path);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(uri_, "*/*");
                this.getContext().startActivity(intent);
            }

        });

        resume.setOnClickListener(v->{
            DownloadManager dm = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

        });

        pause.setOnClickListener(v->{
            DownloadManager dm = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);

            Toast.makeText(this.getContext(), "Donwload paused", Toast.LENGTH_SHORT).show();
        });

        remove.setOnClickListener(v->{
            DownloadManager dm = (DownloadManager) this.getContext().getSystemService(DOWNLOAD_SERVICE);
            int removed=dm.remove(d_id);
            Toast.makeText(this.getContext(), "Download removed: "+removed, Toast.LENGTH_SHORT).show();
        });

        return convertView;

    }
}