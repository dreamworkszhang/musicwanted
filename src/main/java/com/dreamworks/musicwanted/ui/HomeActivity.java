package com.dreamworks.musicwanted.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dreamworks.musicwanted.R;

public class HomeActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.textview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PlayActivity.class));
            }
        });

    }

}
