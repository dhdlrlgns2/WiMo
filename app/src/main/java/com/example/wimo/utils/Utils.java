package com.example.wimo.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.wimo.PrivacyInfoDB;
import com.example.wimo.data.MessageInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    public static String timeToDate(long timeInMillis) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        Date mDate = new Date(timeInMillis);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        return mFormat.format(mDate);
    }

    public static void showDialogFragment(FragmentManager fm, String TAG, DialogFragment dialogFragment) {
        if (fm != null) {
            Fragment prevFragment = fm.findFragmentByTag(TAG);
            if (prevFragment!=null) {
                FragmentTransaction tr = fm.beginTransaction();
                tr.remove(prevFragment);
                tr.commit();
            }
            dialogFragment.show(fm, TAG);
        }
    }

    public static void showDialog(Context context, String title, EditText input, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("확인", listener);
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
