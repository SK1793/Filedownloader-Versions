package com.SK.filedownloader;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DbDownloader extends SQLiteOpenHelper {
    public static final int Db_Version=1;
    List<String> res = new ArrayList<>();
    public static String DBName="DownloaderDb",TableName="Downloads",D_Id_Col="D_id",Id_col="id",FIlename_Col_="Name",
            FIlename_saved_Col_="filename_saved",FileUri_Col="Uri",D_URL="D_url",FIleExt_Col="Extension",FIle_create_date="Created_date",d_status="status",
            File_Size="size";


    public DbDownloader(@Nullable Context context) {

        super(context, DBName, null, Db_Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Q="CREATE TABLE IF NOT EXISTS  "+TableName+" ("
                +Id_col+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +D_Id_Col+" INTEGER,"
                +FIlename_Col_+" TEXT,"+FIlename_saved_Col_+" TEXT,"
                +FIleExt_Col+" TEXT,"
                +D_URL+" TEXT,"
                +FileUri_Col+" TEXT,"
                +FIle_create_date+" TEXT,"
                +File_Size+" INTEGER,"
                +d_status+" TEXT)";

        db.execSQL(Q);

    }

    public void AddDownload(long D_id, String FileName,String Filename_saved, String FileExt, String FileUri,String url, String created_date,int size,String status){
        SQLiteDatabase db1= getWritableDatabase();
        ContentValues cv1=new ContentValues();

        cv1.put(D_Id_Col,D_id);
        cv1.put(FIlename_Col_,FileName);
        cv1.put(FIlename_saved_Col_,Filename_saved);
        cv1.put(FIleExt_Col, FileExt);
        cv1.put(D_URL, url);
        cv1.put(FileUri_Col, FileUri);
        cv1.put(FIle_create_date, created_date);
        cv1.put(File_Size, size);
        cv1.put(d_status, status);


        db1.insertOrThrow(TableName,null,cv1);

        db1.close();
    }

    public List fetchColById(String id) {

        SQLiteDatabase database=this.getWritableDatabase();
        String whereClause = "D_id = ?";
        String[] whereArgs = {id};
        Cursor crsr = database.query(TableName, null, whereClause, whereArgs, null, null, null);

        if(crsr.moveToFirst()){
            do {
                int Col_Did_index = crsr.getColumnIndex("D_id");
                long D_Id = crsr.getLong(Col_Did_index);
                int Col_FileName_index = crsr.getColumnIndex("Name");
                String Filename1 = crsr.getString(Col_FileName_index);
                int Col_FileName_saved_index = crsr.getColumnIndex("filename_saved");
                String Filename1_saved = crsr.getString(Col_FileName_saved_index);
                int Col_FileExt_index = crsr.getColumnIndex("Extension");
                String FileExt1 = crsr.getString(Col_FileExt_index);
                int D_URL_index = crsr.getColumnIndex("D_url");
                String FileUrl1 = crsr.getString(D_URL_index);
                int Col_FileUri_index = crsr.getColumnIndex("Uri");
                String FileUri1 = crsr.getString(Col_FileUri_index);
                int Col_Status_index = crsr.getColumnIndex("status");
                String status = crsr.getString(Col_Status_index);
                int Col_createdDate_index = crsr.getColumnIndex("Created_date");
                String CreatedDate = crsr.getString(Col_createdDate_index);
                int Col_size_index = crsr.getColumnIndex("size");
                int Size = crsr.getInt(Col_size_index);
               
                res.add(String.valueOf(D_Id));res.add(Filename1);res.add(Filename1_saved);res.add(FileExt1);res.add(FileUrl1);res.add(FileUri1);
                res.add(status);res.add(CreatedDate);res.add(String.valueOf(Size));

            }while (crsr.moveToNext());
        }
        crsr.close();

        return res;
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    public ArrayList<downloads> fetchToArray(Context context){
        custom_tools Custom_tools=new custom_tools(context);
        SQLiteDatabase db1 = this.getReadableDatabase();

        Cursor crsr= db1.rawQuery("SELECT * FROM Downloads",null);
        ArrayList<downloads> dArray=new ArrayList<>();

       if(crsr.moveToFirst()){
           do {
               int Col_Did_index = crsr.getColumnIndex("D_id");
               long D_Id = crsr.getLong(Col_Did_index);
               int Col_FileName_index = crsr.getColumnIndex("Name");
               String Filename1 = crsr.getString(Col_FileName_index);
               int Col_FileExt_index = crsr.getColumnIndex("Extension");
               int Col_FileName_saved_index = crsr.getColumnIndex("filename_saved");
               String Filename1_saved = crsr.getString(Col_FileName_saved_index);
               String FileExt1 = crsr.getString(Col_FileExt_index);
               int D_URL_index = crsr.getColumnIndex("D_url");
               String FileUrl1 = crsr.getString(D_URL_index);
               int Col_FileUri_index = crsr.getColumnIndex("Uri");
               String FileUri1 = crsr.getString(Col_FileUri_index);
               int Col_Status_index = crsr.getColumnIndex("status");
               String status = crsr.getString(Col_Status_index);
               int Col_createdDate_index = crsr.getColumnIndex("Created_date");
               String CreatedDate = crsr.getString(Col_createdDate_index);
               int Col_size_index = crsr.getColumnIndex("size");
               int Size = crsr.getInt(Col_size_index);

//               int Size=Custom_tools.File_size(D_Id);

               dArray.add(new downloads(Filename1,Filename1_saved, Custom_tools.Percentage(D_Id),FileExt1
                       ,FileUrl1,Uri.parse(FileUri1),D_Id,FileExt1,
                       status,status,CreatedDate,FileUri1,Size));

           }while (crsr.moveToNext());
       }
       crsr.close();
       return dArray;
    }
    public static Cursor fetchAllRows(SQLiteDatabase db){

        String q = "SELECT * FROM "+TableName;
        Cursor c=db.rawQuery(q,null);
        return c;
    }

    public void deleteRow(String value)
    {
        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TableName+ " WHERE "+D_Id_Col+"='"+value+"'");
        db.delete(TableName,"D_id=?",new String[]{value});
        db.close();
    }

    public void insertRow(long D_id, String FileName,String Filename_saved, String FileExt, String FileUri,String url, String created_date,int size,String status)
    {
        ContentValues cv1=new ContentValues();

        cv1.put(D_Id_Col,D_id);
        cv1.put(FIlename_Col_,FileName);
        cv1.put(FIlename_saved_Col_,Filename_saved);
        cv1.put(FIleExt_Col, FileExt);
        cv1.put(D_URL, url);
        cv1.put(FileUri_Col, FileUri);
        cv1.put(FIle_create_date, created_date);
        cv1.put(File_Size, size);
        cv1.put(d_status, status);

        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("DELETE FROM " + TableName+ " WHERE "+D_Id_Col+"='"+value+"'");
        db.insert(TableName,"",cv1);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS "+TableName);
    onCreate(db);
    }
}
