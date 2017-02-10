package com.jahanzaib.fixit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class FrontSplash extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.front_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                /* Create an Intent that will start the Menu-Activity. */

                Intent mainIntent = new Intent(FrontSplash.this, LoginActivity.class);
                FrontSplash.this.startActivity(mainIntent);
                FrontSplash.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
