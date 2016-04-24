package com.dreamworks.musicwanted.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamworks.musicwanted.R;
import com.dreamworks.musicwanted.model.MyModel;
import com.dreamworks.musicwanted.network.ExecutorHelper;
import com.dreamworks.musicwanted.network.KoalaHttpType;
import com.dreamworks.musicwanted.network.KoalaTaskListener;
import com.dreamworks.musicwanted.network.LogicTask;
import com.dreamworks.musicwanted.network.RequestEntry;
import com.dreamworks.musicwanted.network.RequestHandler;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ExecutorHelper.getInstance().execute(new LogicTask(HomeActivity.this.getClass().getName()) {
            @Override
            public void execute() {
                MyModel myModel = RequestHandler.generalRequestSync(listener, new RequestEntry("https://www.baidu.com/", HomeActivity.this.getClass().getName(), KoalaHttpType.GET));
                if (listener != null) {
                    listener.onResponseResult(myModel);
                }

            }
        });
    }

    private KoalaTaskListener<MyModel> listener = new KoalaTaskListener<MyModel>() {
        @Override
        public void onResponse(MyModel result) {
            Log.e("傻逼", result.msg);

        }
    };
}
