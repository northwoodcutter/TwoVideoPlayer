package io.github.exoplayerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(LaunchActivity.this, MyActivity.class);
        startActivity(intent);
        finish();
    }
}
