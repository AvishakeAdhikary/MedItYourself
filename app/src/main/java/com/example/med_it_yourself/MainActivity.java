package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private Button mGetStartedButton;
    private ImageView mAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        mGetStartedButton = (Button) findViewById(R.id.get_started_button);
        mGetStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
            }
        });
        mAppInfo = (ImageView) findViewById(R.id.app_info);
        mAppInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MainActivity.this, AppInfoActivity.class));
                    }
                }
        );
    }
}