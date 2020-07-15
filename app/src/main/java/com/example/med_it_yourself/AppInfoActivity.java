package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;

public class AppInfoActivity extends AppCompatActivity {
    private Button mGoBackButton;
    private MaterialCardView mCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        mGoBackButton = (Button) findViewById(R.id.app_info_back);
        mGoBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mCardView = (MaterialCardView) findViewById(R.id.creator_card);
        mCardView.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.linkedin.com/in/avhishek-adhikary-a96912129/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }
}