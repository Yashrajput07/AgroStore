package com.project.agrostore.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.agrostore.BuildConfig;
import com.project.agrostore.R;
import com.project.agrostore.db.PreferenceHelper;
import com.project.agrostore.ui.activity.SignInActivity;
import com.project.agrostore.ui.activity.DashboardActivity;
import com.project.agrostore.ui.activity.UserProfileActivity;

import java.util.Locale;

public class Constants {
    public static final int REQUEST_CODE = 99;
    public static final int GPS_REQUEST_CODE = 999;
    public static final int LOGOUT_REQUEST_CODE = 129;
    public static final String ALARM_ID = "AlarmID";
    public static final String ENGLISH_KEY = "EnglishLanguage";
    public static final String HINDI_KEY = "HindiLanguage";
    public static final String BASE_URL_SHEET_DB = "https://sheetdb.io/api/v1/";
    public static final String BASE_URL_WEATHER = "https://api.openweathermap.org/data/2.5/";
    public static final String ENGLISH_NEWS_WEB_URL = "https://krishijagran.com/news";
    public static final String HINDI_NEWS_WEB_URL = "https://hindi.krishijagran.com/news";


    public static void printLog(String message) {
        Log.d("AgroStore User", message);
    }

    public static void identifyUser(FirebaseUser user, Context context) {
        if (user.getEmail() != null) {
            if (user.getEmail().equals("vivek@gmail.com") || user.getEmail().equals("") || user.getEmail().equals("")) {
                //Manufacture user
                Intent manufacturerIntent = new Intent(context, UserProfileActivity.class);
                manufacturerIntent.putExtra("manufacturerUser", "manufacturer");
                context.startActivity(manufacturerIntent);
            } else if (user.getEmail().equals("dj@gmail.com") || user.getEmail().equals("") || user.getEmail().equals("")) {
                //transport user
                Intent transportIntent = new Intent(context, UserProfileActivity.class);
                transportIntent.putExtra("transportUser", "transport");
                context.startActivity(transportIntent);
            } else {
                //Farmer
                Intent intent = new Intent(context, DashboardActivity.class);
                context.startActivity(intent);
            }
        }
    }

    public static void setAppLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);
        context.createConfigurationContext(config);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void bindImage(View view, String url, ImageView imageView) {
        Glide.with(view).load(url).into(imageView);
    }

    public static boolean contactValidation(String contact) {
        return contact.length() == 10;
    }

    public static void logoutAlertMessage(Activity context, FirebaseAuth auth) {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.logout)).setIcon(R.drawable.app_icon4).setMessage(context.getString(R.string.logout_alert_message)).setCancelable(true).setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
        }).setPositiveButton(android.R.string.yes, (arg0, arg1) -> {

            auth.signOut();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(context.getString(R.string.default_web_client_id)).requestEmail().build();
            GoogleSignIn.getClient(context, gso).revokeAccess();
            Intent intent = new Intent(context, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivityForResult(intent, Constants.LOGOUT_REQUEST_CODE);
            context.finish();
        }).create().show();
    }

    public static String plainStringEmail(String email) {
        String userName = email.replaceAll("\\.", "");
        userName.replaceAll("@", "");
        userName.replaceAll("#", "");
        userName.replaceAll("$", "");
        return userName;
    }

    public static void adminEmailContact(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"digitech4574@gmail.com", "mr.nakroz.77@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enter your subject here..");
        context.startActivity(Intent.createChooser(emailIntent, "Email via..."));
    }

    public static boolean selectedLanguage(Context context) {
        PreferenceHelper helper = PreferenceHelper.getInstance(context);
        return helper.getData(HINDI_KEY);
    }
}