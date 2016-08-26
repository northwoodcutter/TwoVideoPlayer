package io.github.exoplayerapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import static android.view.Window.FEATURE_NO_TITLE;
import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;


public class MyActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        getWindow().setFlags(FLAG_FULLSCREEN,
                FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my);
    }

    private Handler mFirstHandler;
    private Handler mSecondHandler;

    public Handler getFirstHandler() {
        return mFirstHandler;
    }

    public void setFirstHandler(Handler mFirstHandler) {
        this.mFirstHandler = mFirstHandler;
    }

    public Handler getSecondHandler() {
        return mSecondHandler;
    }

    public void setSecondHandler(Handler mSecondHandler) {
        this.mSecondHandler = mSecondHandler;
    }
}
