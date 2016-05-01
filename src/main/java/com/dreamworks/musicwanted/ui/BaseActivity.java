package com.dreamworks.musicwanted.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamworks.musicwanted.network.ExecutorHelper;
import com.dreamworks.musicwanted.network.KoalaHttpUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        KoalaHttpUtils.getInstance().cancelRequest(this.getClass().getSimpleName());
    }
}
