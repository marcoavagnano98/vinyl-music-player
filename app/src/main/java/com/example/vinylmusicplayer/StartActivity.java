package com.example.vinylmusicplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;

import com.example.vinylmusicplayer.fragments.HomeFragment;
import com.example.vinylmusicplayer.fragments.StartFragment;

public class StartActivity extends AppCompatActivity {
    String TAG = "StartFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        findViewById(R.id.chooseFolder).setOnClickListener(l -> {
            getFolder();
        });
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri = result.getData().getData();
                    String folder = uri.getPath().split(":")[1];
                    Intent intent=new Intent(this, MainActivity.class);
                    intent.putExtra("folder", folder);
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                }
            });

    public void getFolder() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        someActivityResultLauncher.launch(intent);
    }
}