package com.jgasteiz.comics_android.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Utils {

    private static String STR_SEPARATOR = "__,__";

    public static String convertArrayToString(ArrayList<String> array){
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            str.append(array.get(i));
            // Do not append comma at the end of last element
            if(i < array.size() - 1){
                str.append(STR_SEPARATOR);
            }
        }
        return str.toString();
    }
    public static ArrayList<String> convertStringToArray(String str){
        String[] pages = str.split(STR_SEPARATOR);
        ArrayList<String> pageList = new ArrayList<>();
        Collections.addAll(pageList, pages);
        return pageList;
    }

    public static boolean isNetworkAvailable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
