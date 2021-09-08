package com.example.grapfood.activity.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.view.ContextThemeWrapper;

import com.example.grapfood.R;

public class ReusableCodeForAll {
    public static void ShowAlert(Context context,String title,String message){
        Context context1 = new ContextThemeWrapper(context, R.style.AppTheme2);
        AlertDialog.Builder builder = new AlertDialog.Builder(context1,R.style.MaterialAlertDialog_rounded);

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setTitle(title).setMessage(message).show();
    }


}
