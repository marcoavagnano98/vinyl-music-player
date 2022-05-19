package com.example.vinylmusicplayer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vinylmusicplayer.MainActivity;
import com.example.vinylmusicplayer.R;
import com.example.vinylmusicplayer.StartActivity;

import java.io.File;

public class StartFragment extends Fragment {
    View divider;
    TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start, container, false);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Uri uri = result.getData().getData();
                    String folder = uri.getPath().split(":")[1];
                    Intent intent=new Intent(getActivity().getApplicationContext(), MainActivity.class);
                    intent.putExtra("folder", folder);
                    startActivity(intent);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView=view.findViewById(R.id.header1);
        divider=view.findViewById(R.id.divider);
        ViewGroup.LayoutParams params=textView.getLayoutParams();
        int length=params.width;
        params=divider.getLayoutParams();
        params.width=length;
        divider.setLayoutParams(params);
        view.findViewById(R.id.chooseFolder).setOnClickListener(l -> {
            getFolder();
        });
    }
}
