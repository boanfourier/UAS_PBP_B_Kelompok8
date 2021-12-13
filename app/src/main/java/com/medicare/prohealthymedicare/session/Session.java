package com.medicare.prohealthymedicare.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {

    static final String KEY_USER_TEREGISTER = "user", KEY_PASS_TEREGISTER = "pass";
    static final String KEY_USERNAME_SEDANG_LOGIN = "Username_logged_in";
    static final String KEY_STATUS_SEDANG_LOGIN = "Status_logged_in";
    static final String KEY_ISLOGIN = "Login";

    private static SharedPreferences getSharedPreference(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setIsLogin(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(KEY_ISLOGIN, tipe);
        editor.apply();
    }


    public static String getIsLogin(Context context){
        return getSharedPreference(context).getString(KEY_ISLOGIN,"false");
    }




    public static void setIsFirstname(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("firstname", tipe);
        editor.apply();
    }
    public static String getIsFirstname(Context context){
        return getSharedPreference(context).getString("firstname","");
    }

    public static void setIsLastName(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("lastname", tipe);
        editor.apply();
    }
    public static String getIsLastName(Context context){
        return getSharedPreference(context).getString("lastname","");
    }



    public static void setIsPassword(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("password", tipe);
        editor.apply();
    }

    public static String getIsPassword(Context context){
        return getSharedPreference(context).getString("password","");
    }


    public static void setIsNotelp(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("notelp", tipe);
        editor.apply();
    }

    public static String getIsNotelp(Context context){
        return getSharedPreference(context).getString("notelp","");
    }


    public static void setIsFoto(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("foto", tipe);
        editor.apply();
    }

    public static String getIsFoto(Context context){
        return getSharedPreference(context).getString("foto","");
    }

    public static void setIsUsername(Context context, String tipe){
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString("username", tipe);
        editor.apply();
    }

    public static String getIsUsername(Context context){
        return getSharedPreference(context).getString("username","");
    }

    public static void clearLoggedInUser(Context context) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove(KEY_USERNAME_SEDANG_LOGIN);
        editor.remove(KEY_STATUS_SEDANG_LOGIN);
        editor.apply();
    }
}
