package ru.ifmo.ctddev.FSSARecSys.utils;

/**
 * Created by Сергей on 10.07.2015.
 */
public class StringUtils {
    public static String[] StringToStrArr(String str){
        return str.split(" ");
    }

    public static String StrArrayToString(String [] strArr){
        String str = "";
        for (String s: strArr) {
            str += (s + " ");
        }
        return str;
    }
}
