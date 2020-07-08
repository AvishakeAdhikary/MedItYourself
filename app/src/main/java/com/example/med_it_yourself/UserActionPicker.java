package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class UserActionPicker extends AppCompatActivity {

    private Button mIAmbulance,mIDoctor,mIShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_picker_user);
        mIAmbulance = (Button) findViewById(R.id.i_ambulance);
        mIAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserActionPicker.this, UserAmbulanceActivity.class);
                startActivity(i);
            }
        });
        mIDoctor = (Button) findViewById(R.id.i_doctor);
        mIDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserActionPicker.this, UserDoctorActivity.class);
                startActivity(i);
            }
        });
        mIShop = (Button) findViewById(R.id.i_shop);
        mIShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserActionPicker.this, UserMediceneShopActivity.class);
                startActivity(i);
            }
        });
    }

}