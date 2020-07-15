package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ProviderDoctorActivity extends AppCompatActivity {

    private Button mSetupDoctorButton;
    private Switch mActiveDoctorSwitch;

    private FirebaseAuth mAuth;
    private DatabaseReference mDoctorDatabase;
    private DatabaseReference mAvailableDoctor,mDoctorService;

    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_doctor);
        mSetupDoctorButton = (Button) findViewById(R.id.provider_doctor_setup);
        mSetupDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProviderDoctorActivity.this, ProviderDoctorSettings.class);
                startActivity(i);
            }
        });
        mActiveDoctorSwitch = (Switch) findViewById(R.id.doc_active_switch);
        mActiveDoctorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b)
                {

                    mAuth = FirebaseAuth.getInstance();
                    mUserID = mAuth.getCurrentUser().getUid();
                    mDoctorDatabase = FirebaseDatabase.getInstance().getReference().child("Doctors").child(mUserID);
                    mDoctorService = FirebaseDatabase.getInstance().getReference().child("Doctors").child(mUserID).child("DoctorService");
                    mAvailableDoctor = FirebaseDatabase.getInstance().getReference().child("DoctorsAvailable");
                    if(mDoctorDatabase!=null) {
                        if (mDoctorService == null) {
                            Toast.makeText(getApplicationContext(), "Setup Doctor Account Properly First", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Map userInfo = new HashMap();
                            userInfo.put("DoctorUid", mUserID);
                            mAvailableDoctor.child(mDoctorService.toString()).updateChildren(userInfo);
                            Toast.makeText(getApplicationContext(), "Doctor Is Now Active", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        mActiveDoctorSwitch.setChecked(false);
                    }

                }
                else
                {
                    mAuth = FirebaseAuth.getInstance();
                    mUserID = mAuth.getCurrentUser().getUid();
                    mAvailableDoctor = FirebaseDatabase.getInstance().getReference().child("DoctorsAvailable").child(mUserID);
                    if(mAvailableDoctor!=null)
                    {
                        mAvailableDoctor.removeValue();
                    }
                    Toast.makeText(getApplicationContext(),"Doctor Is Now Inactive",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}