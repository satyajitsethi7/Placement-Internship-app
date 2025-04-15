package com.example.placementproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginAndSignup extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_and_signup);



        if(savedInstanceState==null){
            FragmentManager fm=getSupportFragmentManager();
            FragmentTransaction ft=fm.beginTransaction();
            ft.replace(R.id.loginandsignupmain, new LoginFragment());
            ft.commit();
            Log.d("LoginAndSigup", "Ok");
//            SharedPreferences pref=getSharedPreferences("login", MODE_PRIVATE);
//            SharedPreferences.Editor editor=pref.edit();
//            editor.putBoolean("flag", true);
//            editor.apply();

//            Intent iMain=new Intent(LoginAndSignup.this,MainActivity.class);
//            startActivity(iMain);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Fragment currentFreagment=getSupportFragmentManager().findFragmentById(R.id.main);
                if(currentFreagment instanceof SignupFragment)
                {
                    switchFragment(new LoginFragment());
                }else {
                    finish();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginandsignupmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void switchFragment(Fragment fragment)
    {
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.loginandsignupmain, fragment);
        ft.commit();
    }

}