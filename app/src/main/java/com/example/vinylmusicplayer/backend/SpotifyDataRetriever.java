package com.example.vinylmusicplayer.backend;

import android.app.Activity;

import android.app.Application;
import android.content.Context;

import android.content.SharedPreferences;
import android.util.Log;


import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.adapters.ViewPagerAdapter;
import com.example.vinylmusicplayer.helpers.DateHelper;
import com.example.vinylmusicplayer.helpers.VolleyRequestHelper;
import com.google.gson.JsonObject;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class SpotifyDataRetriever {
    //
    private final String CLIENT_ID = "117f89e1bef040d5922b254fe6101ce1";
    private final String REDIRECT_URI = "http://bestemmiolo.azurewebsites.net/genera";
    private String token;
    private final String refreshToken = "AQBGZO6KLefGe2qPAg7xwuWilr6W5AQic_cZrXftHZRI1XZCCAHuDZ1G6oG27QRT3Ma5yKgGpUbLzBkNOnBbqIj_Ivl6AW9SBk-2YuDjazaUCOCcMHqHA9HuSN6dn7Ic8HE";
    private final String API_REQUEST_URL = "https://api.spotify.com/v1/";
    private Date accessTokenRequestDate;
    private final int EXPIRE_TOKEN_SECONDS = 3600;
    private List<String> artists;
    private boolean emptySearch = false;
    Activity activity;
    // private SpotifyAppRemote mSpotifyAppRemote;

    List<String> infoAboutSongs;

    public interface LoadDataListener {
        void onDataLoaded(String[] data, int index);
    }

    private LoadDataListener listener;

    public SpotifyDataRetriever(Activity activity, LoadDataListener listener) {
        this.activity = activity;
        this.listener = listener;

        infoAboutSongs = new ArrayList<>();
        //   this.adapter=adapter;
        artists = new ArrayList<>();

        setInitialTokenInfo();
        //COLLEGAMENTO ALL'APP SPOTIFY SUL DISPOSITIVO, INUTILE MOMENTANEAMENTE
//        ConnectionParams connectionParams =
//                new ConnectionParams.Builder(CLIENT_ID)
//                        .setRedirectUri(REDIRECT_URI)
//                        .showAuthView(true)
//                        .build();
//        SpotifyAppRemote.connect(context, connectionParams,
//                new Connector.ConnectionListener() {
//
//                    @Override
//                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
//                        //mSpotifyAppRemote = spotifyAppRemote;
//                    }
//
//                    @Override
//                    public void onFailure(Throwable throwable) {
//                        Log.e("MainActivity", throwable.getMessage(), throwable);
//
//                        // Something went wrong when attempting to connect! Handle errors here
//                    }
//                });
    }

    public void setInitialTokenInfo() {
        final int numberOfSeconds = -EXPIRE_TOKEN_SECONDS;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, numberOfSeconds);
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        Long timeData = sharedPref.getLong(activity.getString(R.string.access_token_data), calendar.getTime().getTime());
        accessTokenRequestDate = DateHelper.getDataFromTime(timeData);
        token = sharedPref.getString(activity.getString(R.string.access_token), "");
    }

    public void query(String songId,String q, int index) {
        Thread t = new Thread(() -> {
            try {
                Log.d("LocalVB", token + " " + accessTokenRequestDate);
                if (DateHelper.addSeconds(accessTokenRequestDate, EXPIRE_TOKEN_SECONDS).before(DateHelper.getCurrentDateTime()) || token.compareTo("") == 0) {
                    Log.d("REFRESHO", "CIAONNE");
                    refreshToken();
                    SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    accessTokenRequestDate = DateHelper.getCurrentDateTime();
                    editor.putLong(activity.getString(R.string.access_token_data), accessTokenRequestDate.getTime());
                    editor.putString(activity.getString(R.string.access_token), token);
                    editor.apply();
                    Log.d("Date", accessTokenRequestDate.toString());
                }
                String[] data = getSongData(songId,q);
                Log.d("JSON2", data[0] + "   " + index);
                listener.onDataLoaded(data, index);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        t.start();

    }

    private void refreshToken() throws JSONException {
        String authUrl = "https://accounts.spotify.com/api/token";
        String body = "grant_type=refresh_token&refresh_token=".concat(refreshToken);
        JSONObject response = VolleyRequestHelper.postRequest(authUrl, body,
                Collections.singletonMap("Authorization", "Basic MTE3Zjg5ZTFiZWYwNDBkNTkyMmIyNTRmZTYxMDFjZTE6ODNhZGY4OGQ1Mjk1NGFlNGE5ODg0NGMwYzNlNWUwM2Y="), activity.getApplicationContext());
        if (response.length() != 0) {
            Log.d("JSON1", response.toString());
            token = response.getString("access_token");
        }
    }

    //[0]=artist name, [1]=album_cover_image
    private String[] getSongData(String songId,String songTitle) throws JSONException {

        String urlSearchArtist = API_REQUEST_URL.concat("search?q=").concat(songTitle).concat("&type=track");
        JSONObject response = VolleyRequestHelper.getRequest(urlSearchArtist,
                Collections.singletonMap("Authorization", "Bearer ".concat(token)), activity.getApplicationContext());

        if (response.getJSONObject("tracks").getString("total").compareTo("0") == 0) {
            Log.d("JSON2", "Various");
            emptySearch = true;
            return new String[]{"Various Artists", ""};
        }

        String artistName="";
        String imageUrl="";
        String albumName="";
   //     if (artistName.equals("Various Artists") && !emptySearch) {
            JSONArray listArtists = response.getJSONObject("tracks").getJSONArray("items");
            for (int i = 0; i < listArtists.length(); i++) {
                JSONArray artistArray= ((JSONObject)((JSONObject) listArtists.get(i)).get("album")).getJSONArray("artists");
                JSONObject artistNameApp = (JSONObject) ((JSONObject) ((JSONObject) listArtists.get(i)).get("album")).getJSONArray("artists").get(0);
                imageUrl=((JSONObject) ((JSONObject) listArtists.get(i)).get("album")).getJSONArray("images").getJSONObject(1).getString("url");
                albumName=((JSONObject)((JSONObject) listArtists.get(i)).get("album")).getString("name");
                artistName = artistNameApp.getString("name");
                if (artistName.equals("Various Artists") && i != listArtists.length() - 1) {
                    artistName = "";
                }else{
                    for (int j = 1; j < artistArray.length(); j++) {
                        String nextArtist = artistArray.getJSONObject(j).getString("name");
                        if (j == 1) {
                            artistName = artistName.concat(" feat. ".concat(nextArtist));
                        } else {
                            artistName = artistName.concat(", ".concat(nextArtist));
                        }
                    }

                    break;
                }
                Log.d("ds", "dfs");
            }
          //  Log.d("JSON2021", ((JSONObject) listArtists.get(1)).toString());
        //}
        if (artistName.equals("")) {
            artistName = "Various Artist";
        }

        return new String[]{artistName,albumName,imageUrl,songId};
    }

    private String findFirstValue(JSONObject object, String value) {
        String object2 = object.toString().replace("\"", "");
        int index = object2.indexOf(value) + value.length();
        int w = index;
        while (true) {
            if (object2.charAt(w) == ',') {
                break;
            }
            w++;
        }
        return object2.substring(index, w);
    }

}
