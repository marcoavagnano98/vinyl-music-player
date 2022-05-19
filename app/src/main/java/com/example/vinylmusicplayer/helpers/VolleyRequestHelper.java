package com.example.vinylmusicplayer.helpers;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.example.vinylmusicplayer.classes.VolleySingleton;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class VolleyRequestHelper {
    static Queue<JSONObject> queue = new LinkedList<>();
   public static JSONObject getRequest(String URL,Map<String, String> headers, Context context){
       RequestFuture<JSONObject> future = RequestFuture.newFuture();
       VolleySingleton.getInstance(context).addToRequestQueue(new JsonObjectRequest(Request.Method.GET, URL, null, future, future) {
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               return headers;
           }
       });
       try {
           JSONObject object = future.get(30,TimeUnit.SECONDS);
           Log.d("JSON2",object.toString());
           return object;
       } catch (InterruptedException | ExecutionException | TimeoutException e) {
           Throwable cause = e.getCause();
           if (cause instanceof ServerError) {
               ServerError error = (ServerError) cause;
               Log.e("TAG", "Server error: " + new String(error.networkResponse.data));
           }
           Log.e("TAG", "Cannot sync checkin.", e);
       }
       return new JSONObject();
   }
    public static JSONObject postRequest(String URL, String body, Map<String, String> headers, Context context) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();

        // final JSONObject[] object = new JSONObject[1];
        VolleySingleton.getInstance(context).addToRequestQueue(new JsonObjectRequest(Request.Method.POST, URL, null, future, future) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
            @Override
            public byte[] getBody() {
                return body.getBytes();
            }
        });
        try {
            JSONObject object = future.get(30,TimeUnit.SECONDS);
            Log.d("JSON1",object.toString() );
            return object;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ServerError) {
                ServerError error = (ServerError) cause;
                Log.e("TAG", "Server error: " + new String(error.networkResponse.data));
            }
            Log.e("TAG", "Cannot sync checkin.", e);
        }
        return new JSONObject();
    }
}
