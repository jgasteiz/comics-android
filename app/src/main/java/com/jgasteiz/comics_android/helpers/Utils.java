package com.jgasteiz.comics_android.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {

    private static String STR_SEPARATOR = "__,__";

    public static String convertArrayToString(ArrayList<String> array){
        String str = "";
        for (int i = 0; i < array.size(); i++) {
            str = str + array.get(i);
            // Do not append comma at the end of last element
            if(i < array.size() - 1){
                str = str + STR_SEPARATOR;
            }
        }
        return str;
    }
    public static ArrayList<String> convertStringToArray(String str){
        String[] pages = str.split(STR_SEPARATOR);
        ArrayList<String> pageList = new ArrayList<>();
        for (String page : pages) {
            pageList.add(page);
        }
        return pageList;
    }

    public static boolean isNetworkAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
