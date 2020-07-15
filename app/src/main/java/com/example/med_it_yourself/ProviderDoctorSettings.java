package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProviderDoctorSettings extends AppCompatActivity {

    private EditText mNameField, mPhoneField, mEmailField;

    private Button mDoctorBack, mDoctorConfirm;

    private FirebaseAuth mAuth;
    private DatabaseReference mDriverDatabase;

    private String mUserID;
    private String mDoctorName;
    private String mDoctorPhone;
    private String mDoctorEmail;
    private String mDoctorService;

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_doctor_settings);


        mNameField = (EditText) findViewById(R.id.provider_doctor_name);
        mPhoneField = (EditText) findViewById(R.id.provider_doctor_phone);
        mEmailField = (EditText) findViewById(R.id.provider_doctor_email);


        mRadioGroup = (RadioGroup) findViewById(R.id.ProviderDoctorRadioGroup);

        mDoctorBack = (Button) findViewById(R.id.provider_doctor_back);
        mDoctorConfirm = (Button) findViewById(R.id.provider_doctor_confirm);


        mAuth = FirebaseAuth.getInstance();
        mUserID = mAuth.getCurrentUser().getUid();
        mDriverDatabase = FirebaseDatabase.getInstance().getReference().child("Doctors").child(mUserID);

        getUserInfo();

        mDoctorConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInformation();
                finish();
            }
        });

        mDoctorBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                return;
            }
        });
    }

    private void getUserInfo(){
        mDriverDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("DoctorName")!=null){
                        mDoctorName = map.get("DoctorName").toString();
                        mNameField.setText(mDoctorName);
                    }
                    if(map.get("DoctorPhone")!=null){
                        mDoctorPhone = map.get("DoctorPhone").toString();
                        mPhoneField.setText(mDoctorPhone);
                    }
                    if(map.get("DoctorEmail")!=null){
                        mDoctorEmail = map.get("DoctorEmail").toString();
                        mEmailField.setText(mDoctorEmail);
                    }
                    if(map.get("DoctorService")!=null){
                        mDoctorService = map.get("DoctorService").toString();
                        switch (mDoctorService){
                            case"General":
                                mRadioGroup.check(R.id.AmbR);
                                break;
                            case"Premium":
                                mRadioGroup.check(R.id.AmbPre);
                                break;
                            case"Luxury":
                                mRadioGroup.check(R.id.AmbLux);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void saveUserInformation() {
        mDoctorName = mNameField.getText().toString();
        mDoctorPhone = mPhoneField.getText().toString();
        mDoctorEmail = mEmailField.getText().toString();

        int selectId = mRadioGroup.getCheckedRadioButtonId();

        final RadioButton radioButton = (RadioButton) findViewById(selectId);

        if (radioButton.getText() == null) {
            return;
        }

        mDoctorService = radioButton.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("DoctorName", mDoctorName);
        userInfo.put("DoctorPhone", mDoctorPhone);
        userInfo.put("DoctorEmail", mDoctorEmail);
        userInfo.put("DoctorService", mDoctorService);
        mDriverDatabase.updateChildren(userInfo);
    }
}