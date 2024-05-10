package com.SK.filedownloader;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class FIle_properties_customize {

    EditText filename,extension,path;
    Button update;
    ImageButton Opener;
    int Folder_select_code;
    Uri file_uri;
    LinearLayout layout1;
    LayoutInflater inflator1 ;

    protected void popup_window(View view) {

        Folder_select_code = 1111;
        inflator1 = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);

        View popup =inflator1.inflate(R.layout.file_properties_customize,null);
        layout1=popup.findViewById(R.id.LL_props_custom);
        filename=popup.findViewById(R.id.ETFilename);
        extension=popup.findViewById(R.id.ETExtension);
        update=popup.findViewById(R.id.BtnUpdate);

        int width =LinearLayout.LayoutParams.MATCH_PARENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;

        final PopupWindow window1=new PopupWindow(popup,width,height,true);

        window1.showAtLocation(view, Gravity.CENTER,0,height/2);
        window1.setElevation(10);
        window1.setFocusable(true);
        window1.setAnimationStyle(R.style.Pop_up_window_Animation);
        window1.update();

        update.setOnClickListener(v -> {
            Intent i1=new Intent(this.layout1.getContext(), MainActivity.class);
            i1.putExtra("filename",filename.getText().toString());
            i1.putExtra("extension",extension.getText().toString());
            startActivity(this.layout1.getContext(), i1,null);
            window1.showAsDropDown(v);
            window1.dismiss();
        });
    }

}
