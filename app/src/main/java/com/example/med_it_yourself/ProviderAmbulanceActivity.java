package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProviderAmbulanceActivity extends AppCompatActivity {

    private Button mAssistAmbulance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_ambulance);
        mAssistAmbulance = (Button) findViewById(R.id.amb_assist);

        mAssistAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProviderAmbulanceActivity.this, ProviderAmbulanceMap.class);
                startActivity(i);
            }
        });
    }
}