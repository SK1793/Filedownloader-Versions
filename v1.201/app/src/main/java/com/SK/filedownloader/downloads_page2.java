package com.SK.filedownloader;

import static java.lang.Thread.sleep;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.erkutaras.showcaseview.ShowcaseManager;

public class downloads_page2 extends AppCompatActivity {

    DbDownloader dbDownloader1;
    RecyclerView Rvadapter,RvadapterCurrent;
    downloads_section downloads_section_obj, downloads_section_objCurrent;
    SwipeRefreshLayout srLayout,srLayoutCurrent;

    Context context;

    TextView btnCurrentView,btnAllView;
    LinearLayout LLCurrent,LLAll;
    custom_tools tools;

    Executor executor;
    AllListLoaderTask allListLoaderTask=new AllListLoaderTask();
    CurrentListLoaderTask currentListLoaderTask=new CurrentListLoaderTask();

    ArrayList<downloads>  D_arr = new ArrayList<>();
    ArrayList<downloads> D_arrCurrent = new ArrayList<>();
    ArrayList<downloads> arr = new ArrayList<>();
    int ShowcaseNumber=0;
    boolean isFirstTimeUser;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads_page2);

        SharedPreferences mPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
//        // Check if we need to display our OnboardingSupportFragment
//        if (!sharedPreferences.getBoolean(
//                MyOnboardingSupportFragment.COMPLETED_ONBOARDING_PREF_NAME, false)) {
//            // The user hasn't seen the OnboardingSupportFragment yet, so show it
////            startActivity(new Intent(this, OnboardingActivity.class));
//        }
        context= this.getApplicationContext();

        executor=Executors.newSingleThreadExecutor();

executor.execute(()->{

        boolean firstTime = mPreferences.getBoolean("firstTime", true);
        if (firstTime) {
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            isFirstTimeUser=true;
        } else {
            isFirstTimeUser = false;
        }
});

        // initializing our all variables.

        dbDownloader1 = new DbDownloader(downloads_page2.this);
        // getting our course array
        // list from db handler class.
        D_arr = dbDownloader1.fetchToArray(downloads_page2.this);
        Rvadapter = findViewById(R.id.idRVDownloads);
        RvadapterCurrent = findViewById(R.id.idRVDownloadsCurrent);
        srLayout=findViewById(R.id.SwipeRLAll2);
        srLayoutCurrent=findViewById(R.id.SwipeRLCurrent2);
        btnAllView=findViewById(R.id.TvbtnViewAll);
        btnCurrentView=findViewById(R.id.TvbtnViewCurrent);
        LLAll=findViewById(R.id.LLCardListAll);
        LLCurrent=findViewById(R.id.LLCardListCurrent);
        tools=new custom_tools(this.getApplicationContext());

        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(downloads_page2.this, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManagerCurrent =
                new LinearLayoutManager(downloads_page2.this, RecyclerView.VERTICAL, false);
        Rvadapter.setLayoutManager(linearLayoutManager);
        RvadapterCurrent.setLayoutManager(linearLayoutManagerCurrent);

        LLAll.setVisibility(View.GONE);

        //Initaialize DownloadManager Object

   //Load all List Section
       allListLoaderTask.execute();
  //Load Current List Section
       currentListLoaderTask.execute();

        // on below line passing our array list to our adapter class.

        srLayout.setOnRefreshListener(()->{
            downloads_section_obj.notifyDataSetChanged();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            srLayout.setRefreshing(false);
        });

        srLayoutCurrent.setOnRefreshListener(()->{
            downloads_section_objCurrent.notifyDataSetChanged();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            srLayoutCurrent.setRefreshing(false);
        });

        // setting layout manager for our recycler view.

        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(simpleCallback);
                itemTouchHelper.attachToRecyclerView(Rvadapter);

        // setting our adapter to recycler view.

        //Show And Hide Lists with a Tap on "-" or "+"
        btnCurrentView.setOnClickListener(v->{
            if(btnCurrentView.getText().toString().contains("+")){

                btnCurrentView.setText("-");
                LLCurrent.setVisibility(View.VISIBLE);
            }else{

                btnCurrentView.setText("+");
                LLCurrent.setVisibility(View.GONE);

            }
        });

        btnAllView.setOnClickListener(v->{

            if(btnAllView.getText().toString().contains("+")){

                btnAllView.setText("-");
                LLAll.setVisibility(View.VISIBLE);
            }else{
                btnAllView.setText("+");
                LLAll.setVisibility(View.GONE);

            }
        });

    }

    ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }
//        final DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        downloads deletedListItem=null;

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View view=viewHolder.itemView;
            if(isFirstTimeUser){
                ShowCaseView(view);
                isFirstTimeUser=false;
            }

            Drawable icon= ContextCompat.getDrawable(context,R.drawable.delete);
            ColorDrawable bg;
            int iconRight=0,iconLeft=0,iconHeight= Objects.requireNonNull( icon).getIntrinsicHeight(),
                    iconWidth=icon.getIntrinsicWidth(),cellHeight=view.getBottom()-view.getTop(),
                    iconTop=view.getTop() + (cellHeight - iconHeight) / 2,
            iconBottom=iconTop+iconHeight, margin= convertDpToPx(context,32);

            if(dX>0){   //Right Swipe
                icon=ContextCompat.getDrawable(context,R.drawable.open);
                bg=new ColorDrawable(Color.WHITE);
                bg.setBounds(0,view.getTop(), (int) (view.getLeft()+dX),view.getBottom());
                iconLeft=margin;
                iconRight=margin+iconWidth;
            }else{     //Left Swipe

                icon= ContextCompat.getDrawable(context,R.drawable.delete);
                bg= new ColorDrawable(Color.RED);
                bg.setBounds((int) (view.getRight()+dX),view.getTop(), view.getRight(),view.getBottom());
                iconLeft=view.getRight()- margin -iconWidth;
                iconRight=view.getRight()-margin;
            }
            bg.draw(c);

            icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
            icon.draw(c);
            super.onChildDraw(c, recyclerView, viewHolder, dX/4, dY, actionState, isCurrentlyActive);
        }

        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position=viewHolder.getAdapterPosition();
            File DeletedFile = null;int Removed;
            boolean[] isDeleted = {false};

            switch (direction){
                case ItemTouchHelper.LEFT:

//                    deletedListItem=arr.get(position);

//                    dbDownloader1.deleteRow(String.valueOf(arr.get(viewHolder.getAbsoluteAdapterPosition()).get_download_id()));
                    deletedListItem=arr.remove(position);


                    try {
                        File file=new File(deletedListItem.get_file_uri().getPath());
                        DeletedFile=new File(deletedListItem.get_file_uri().getPath());
                            if (file.exists()) {
                                new Handler().post(() -> {
                                    isDeleted[0] = file.delete();
                                    if (isDeleted[0]) {
                                        dbDownloader1.deleteRow(String.valueOf(arr.get(position).get_download_id()));
//                        adapter.remove((downloads) ArrList.get(position));
                                        Toast.makeText(viewHolder.itemView.getContext(), "Download Removed from List", Toast.LENGTH_SHORT).show();
//                                        Snackbar.make(getApplicationContext(), viewHolder.itemView, "Loading", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(viewHolder.itemView.getContext(), "Couldn't Delete The File", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(viewHolder.itemView.getContext(), "File Doesn't exist", Toast.LENGTH_SHORT).show();
                            }

                    } catch (UnsupportedOperationException e) {
                        e.printStackTrace();
                        Toast.makeText(viewHolder.itemView.getContext(), "Couldn't Remove Download", Toast.LENGTH_SHORT).show();
                    }


                    File finalDeletedFile = DeletedFile;
                    Snackbar.make(Rvadapter,"Removed item",Snackbar.LENGTH_SHORT)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dbDownloader1.insertRow(deletedListItem.get_download_id(),deletedListItem.get_filename(),deletedListItem.get_filename_saved(),deletedListItem.get_extension(),
                                            deletedListItem.get_file_uri().getPath(),deletedListItem.get_url(),deletedListItem.get_date(),deletedListItem.get_size(),deletedListItem.get_status());
                                    FileOutputStream outputStream=null;
                                    try{
                                        ContentResolver cr=context.getContentResolver();
                                        cr.openOutputStream(deletedListItem.get_file_uri(),"rw");
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    arr.add(position,deletedListItem);

                                    }
                            }).show();

                    getMainExecutor().execute(()->{
                        downloads_section_obj = new downloads_section(arr, downloads_page2.this);
                        Rvadapter.scrollToPosition(position-1);
                        Rvadapter.setAdapter(downloads_section_obj);
                        downloads_section_obj.notifyDataSetChanged();
                    });
                    viewHolder.itemView.invalidate(viewHolder.itemView.getLeft(),viewHolder.itemView.getTop(),0,viewHolder.itemView.getBottom());

                    break;

                case ItemTouchHelper.RIGHT:
                    Uri path;

                    path=arr.get(position).get_file_uri();
                    if(new File(Objects.requireNonNull(path.getPath())).exists()){

                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Toast.makeText(this.getContext(), "File Path: "+ uri_file, Toast.LENGTH_SHORT).show();
                        intent.setData(path);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }else{
                        Toast.makeText(context, " File Doesn't exist  "+arr.get(position).get_filename(), Toast.LENGTH_SHORT).show();
                    }
                    viewHolder.itemView.invalidate(0,viewHolder.itemView.getTop(),viewHolder.itemView.getRight(),viewHolder.itemView.getBottom());

                    break;
            }
        }

    };

    public static int convertDpToPx(Context context, float dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    public class CurrentListLoaderTask extends AsyncTask<ArrayList<downloads>,downloads_section ,downloads_section>{
            ProgressDialog progressDialog;
        int i;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(downloads_page2.this);
//            progressDialog.setMessage("Loading...");
//            progressDialog.setTitle("Downloads List");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            progressDialog.show();
//            progressDialog.setCancelable(false);
            ;
        }

        @Override
        protected downloads_section doInBackground(ArrayList<downloads>... list) {
            for(i=0;i<D_arr.size();i++){
                downloads dwnlds=D_arr.get(i);

                if(tools.Status(dwnlds.get_download_id(),getApplicationContext()).contains("Downloading") ||
                        tools.Status(dwnlds.get_download_id(),getApplicationContext()).contains("Paused")
                        || tools.Status(dwnlds.get_download_id(),getApplicationContext()).contains("Pending")){
                    D_arrCurrent.add(new downloads(dwnlds._filename,dwnlds._filename_saved,dwnlds._percent,dwnlds._extension,dwnlds._url,
                            dwnlds._file_uri,dwnlds._download_id,dwnlds._type,dwnlds._progress,dwnlds._status,dwnlds._date,dwnlds._loc,dwnlds._size));
//                    RvadapterCurrent.scrollToPosition(D_arrCurrent.size()-1);

//                Log.d("tag", "Current Size: "+D_arrCurrent.size());
                }
                //Reverse The ArrayList for getting last item at first
                Collections.reverse(D_arrCurrent);
                downloads_section_objCurrent = new downloads_section(D_arrCurrent, downloads_page2.this);
//            Log.d("tag", "All Size: "+D_arr.size());

            }
            return downloads_section_objCurrent;
        }

        @Override
        protected void onPostExecute(downloads_section val) {
            super.onPostExecute(val);
            RvadapterCurrent.setAdapter(val);
//            progressDialog.dismiss();
        }
    }
    public class AllListLoaderTask extends AsyncTask<ArrayList<downloads>,downloads ,Void>{
            ProgressDialog progressDialog;
        int i;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(downloads_page2.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setTitle("Downloads List");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(true);
            progressDialog.setContentView(btnAllView.getRootView());
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(ArrayList<downloads>... arrayLists) {
            for(i=0;i<D_arr.size();i++){
                downloads dwnlds=D_arr.get(i);

                arr.add(new downloads(dwnlds._filename,dwnlds._filename_saved,dwnlds._percent,dwnlds._extension,dwnlds._url,
                        dwnlds._file_uri,dwnlds._download_id,dwnlds._type,dwnlds._progress,dwnlds._status,dwnlds._date,dwnlds._loc,dwnlds._size));
//Reverse the ArrayList for getting recent item at first
                Collections.reverse(arr);
//            Log.d("tag", "All Size: "+D_arr.size());
                publishProgress(D_arr.get(i));

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(downloads... val) {
            super.onProgressUpdate(val);
            arr.add(new downloads(val[0]._filename,val[0]._filename_saved,val[0]._percent,val[0]._extension,val[0]._url,
                    val[0]._file_uri,val[0]._download_id,val[0]._type,val[0]._progress,val[0]._status,val[0]._date,val[0]._loc,val[0]._size));
//Reverse the ArrayList for getting recent item at first
            Collections.reverse(arr);
            downloads_section_obj = new downloads_section(arr, downloads_page2.this);
            Rvadapter.scrollToPosition(D_arr.size());
            Rvadapter.setAdapter(downloads_section_obj);
        }

        @Override
        protected void onPostExecute(Void val) {
            super.onPostExecute(val);
            progressDialog.dismiss();
//            Rvadapter.setAdapter(val);
//            progressDialog.dismiss();
        }
    }

    public void ShowCaseView(View v){
        ShowcaseNumber=1;
        ShowcaseManager.Builder builder=new ShowcaseManager.Builder();
        builder
                .context(downloads_page2.this)
                .key("key").developerMode(true)
                .view(v)
                .descriptionTitle("Info for Swipe Operations on a Card")
                .descriptionText("Swipe the card Left to Delete the File \n Swipe Right to Open the File")
                .buttonText("ok")
                .buttonVisibility(true)
                .cancelButtonColor(Color.RED)
                .cancelButtonVisibility(true).add().build().show()
                ;
    }


}