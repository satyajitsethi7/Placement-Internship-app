package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences pref=getSharedPreferences("login", MODE_PRIVATE);
                Boolean check=pref.getBoolean("flag",false);

                Intent inext;
                if(check){
//                    Log.d("F", String.valueOf(check));
                    inext=new Intent(SplashActivity.this,LoginAndSignup.class);
                }else{
//                    Log.d("D", "Ok");
                    inext=new Intent(SplashActivity.this,MainActivity.class);
//                    inext.putExtra("direct_to_login", true);
                }
                startActivity(inext);
                finish();
            }
        },2000);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}