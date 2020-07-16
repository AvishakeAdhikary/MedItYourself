package com.example.med_it_yourself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class UserDoctorActivity extends AppCompatActivity {

    private TextView mDocName,mDocPhone,mDocEmail,mDocUnavailableText;
    private ImageView mDocUnavailable;
    private DatabaseReference mDoctorAvailable;
    private Button mCallDoc,mEmailDoc,mReqDoc;
    private RadioGroup mRadioGroup;
    private LinearLayout mLinearLayout;
    private Map<String, Object> new_map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_user);

        mDocName = (TextView) findViewById(R.id.doc_name);
        mDocPhone = (TextView) findViewById(R.id.doc_phone);
        mDocEmail = (TextView) findViewById(R.id.doc_email);
        mDocUnavailableText = (TextView) findViewById(R.id.user_doctor_unavailable_text);
        mDocUnavailable = (ImageView) findViewById(R.id.user_doctor_image);
        mCallDoc = (Button) findViewById(R.id.call_doctor);
        mEmailDoc = (Button) findViewById(R.id.email_doctor);
        mLinearLayout = (LinearLayout) findViewById(R.id.final_layout);
        mReqDoc = (Button) findViewById(R.id.request_doctor);
        mReqDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDocUnavailable.setVisibility(View.VISIBLE);
                mDocUnavailableText.setVisibility(View.VISIBLE);

                mRadioGroup = (RadioGroup) findViewById(R.id.UserDoctorRadioGroup);
                mRadioGroup.check(R.id.DocG);
                mRadioGroup.setVisibility(View.GONE);
                mReqDoc.setVisibility(View.GONE);
                mDoctorAvailable = FirebaseDatabase.getInstance().getReference().child("DoctorsAvailable");
                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final RadioButton radioButton = findViewById(selectId);
                Query queryUid = mDoctorAvailable.child(radioButton.getText().toString()).orderByKey().limitToFirst(1);
                queryUid.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                String key = data.getKey();
                                String value = data.getValue().toString();
                                if(key!=null)
                                {
                                    mDocUnavailable.setVisibility(View.GONE);
                                    mDocUnavailableText.setVisibility(View.GONE);
                                    mLinearLayout.setVisibility(View.VISIBLE);
                                    DatabaseReference doctorReference = FirebaseDatabase.getInstance().getReference().child("Doctors").child(value);
                                    doctorReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            new_map = (Map<String, Object>) snapshot.getValue();
                                            if (new_map.get("DoctorName") != null) {
                                                    mDocName.setText("Doctor Name: "+new_map.get("DoctorName").toString());
                                                }
                                            if (new_map.get("DoctorPhone") != null) {
                                                mDocPhone.setText("Doctor Phone: "+new_map.get("DoctorPhone").toString());
                                            }
                                            if (new_map.get("DoctorEmail") != null) {
                                                mDocEmail.setText("Doctor Email: "+new_map.get("DoctorEmail").toString());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        mCallDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel" , new_map.get("DoctorPhone").toString(),null));
                startActivity(intent);
            }
        });

        mEmailDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", new_map.get("DoctorEmail").toString(), null));
                startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });

    }
}