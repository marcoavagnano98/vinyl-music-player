package com.example.vinylmusicplayer.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class NetworkHelper {
    public static Drawable drawableFromUrl(String url) throws IOException {
        Bitmap x;
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.connect();
        InputStream input = connection.getInputStream();
        x = BitmapFactory.decodeStream(input);
        return new BitmapDrawable(Resources.getSystem(), x);
    }
//    private boolean isNetworkAvailable(Context context){
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (cm == null) return false;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            NetworkCapabilities cap = cm.getNetworkCapabilities(cm.getActiveNetwork());
//            if (cap == null) return false;
//            return cap.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Network[] networks = cm.getAllNetworks();
//            for (Network n: networks) {
//                NetworkInfo nInfo = cm.getNetworkInfo(n);
//                if (nInfo != null && nInfo.isConnected()) return true;
//            }
//        } else {
//            NetworkInfo[] networks = cm.getAllNetworkInfo();
//            for (NetworkInfo nInfo: networks) {
//                if (nInfo != null && nInfo.isConnected()) return true;
//            }
//        }
//
//        return false;
//    }
//    public static synchronized void checkInternetConnection(ConnectivityCallback callback,AppExecutors appExecutors) {
//        activity.appExecutors.getNetworkIO().execute(() -> {
//            if (isNetworkAvailable()) {
//                try {
//                    HttpsURLConnection urlc = (HttpsURLConnection)
//                            new URL("https://clients3.google.com/generate_204").openConnection();
//                    urlc.setRequestProperty("User-Agent", "Android");
//                    urlc.setRequestProperty("Connection", "close");
//                    urlc.setConnectTimeout(1000);
//                    urlc.connect();
//                    boolean isConnected = urlc.getResponseCode() == 204 && urlc.getContentLength() == 0;
//                    postCallback(callback, isConnected);
//                } catch (Exception e) {
//                    postCallback(callback, false);
//                }
//            } else {
//                postCallback(callback, false);
//            }
//        });
//    }
//
//    public interface ConnectivityCallback {
//        void onDetected(boolean isConnected);
//    }

}
