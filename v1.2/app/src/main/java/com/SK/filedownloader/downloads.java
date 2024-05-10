package com.SK.filedownloader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.jetbrains.annotations.NotNull;

import java.net.URI;

public class downloads {
    public String _percent, _filename,_filename_saved,_extension, _url,_date,_type,_loc,_progress,_status;
    Uri _file_uri;
    int _size;
    long _download_id;

    public downloads(String FileName,String FileName_saved,String Percent,String Extension,String Url,Uri file_uri,long download_id,
                     String type,String progress,String status,String date,String loc,int size){
        _percent=Percent;_filename=FileName;_filename_saved=FileName_saved;_extension=Extension;_url=Url;_file_uri=file_uri;_date=date;_status=status;
        _type=type;_download_id=download_id;_progress=progress;_loc=loc;_size=size;
    }

    public String get_extension() {
        return _extension;
    }

    public String get_filename() {
        return _filename;
    }
    public String get_filename_saved() {
        return _filename_saved;
    }

    public String get_percent() {
        return _percent;
    }

    public String get_url() {return _url;}
    public String get_date() {return _date;}
    public String get_type() {return _type;}
    public String get_loc() {return _loc;}
    public String get_status() {return _status;}
    public int get_size() {return _size;}
    public Uri get_file_uri() {return _file_uri;}

    public long get_download_id() {return _download_id;}
}
