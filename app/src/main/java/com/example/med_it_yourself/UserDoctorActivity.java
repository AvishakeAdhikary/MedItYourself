package com.example.med_it_yourself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
                        for (DataSnapshot datas : snapshot.getChildren()) {
                            if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                                Map<String, Object> map = (Map<String, Object>) datas.getValue();
                                if (map.get("DoctorUid") != null) {
                                    mDoctorAvailable.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                mDocUnavailable.setVisibility(View.GONE);
                                                mDocUnavailableText.setVisibility(View.GONE);
                                                mLinearLayout.setVisibility(View.VISIBLE);
                                                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                                                if (map.get("DoctorName") != null) {
                                                    mDocName.setText(map.get("DoctorName").toString());
                                                }
                                                if (map.get("DoctorPhone") != null) {
                                                    mDocPhone.setText(map.get("DoctorPhone").toString());
                                                }
                                                if (map.get("DoctorEmail") != null) {
                                                    mDocEmail.setText(map.get("DoctorEmail").toString());
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
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
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mDocPhone.getText().toString()));
                startActivity(intent);
            }
        });
        mEmailDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mDocEmail.getText().toString(), null));
                startActivity(Intent.createChooser(intent, "Send email..."));
            }
        });
    }
}