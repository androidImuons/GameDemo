package com.imuons.saddaadda.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.imuons.saddaadda.R;
import com.imuons.saddaadda.Utils.AppCommon;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(2000);
                    String token = AppCommon.getInstance(SplashActivity.this).getToken();
                    //SharedPreferenceUtils.getAccesstoken(SplashScreen.this);

                    if (token == null || token.isEmpty()) {
                        startActivity(new Intent(SplashActivity.this, SelectionPage.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    }
                    finish();


                } catch (Exception e) {

                }
            }
        };
        thread.start();

    }
}
