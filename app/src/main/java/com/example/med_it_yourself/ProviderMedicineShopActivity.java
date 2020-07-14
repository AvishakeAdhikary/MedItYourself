package com.example.med_it_yourself;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class ProviderMedicineShopActivity extends AppCompatActivity {

    private Button mConfirmOrderButton;
    private ImageView mOrderImageView;
    String orderUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_medicene_shop);
        mOrderImageView = (ImageView) findViewById(R.id.provider_order_image);

        mConfirmOrderButton = (Button) findViewById(R.id.confirm_order);

        mConfirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProviderMedicineShopActivity.this, ProviderConfirmPicture.class);
                i.putExtra("filename",orderUrl);
                startActivity(i);
                finish();
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Orders");

        Query queryUid = ref.orderByKey().limitToFirst(1);
        queryUid.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datas : dataSnapshot.getChildren()) {
                    if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                    {
                        Map<String,Object> map = (Map<String, Object>) datas.getValue();
                        if(map.get("OrderImageUrl")!=null){
                            orderUrl = map.get("OrderImageUrl").toString();
                            Glide.with(getApplication()).load(orderUrl).into(mOrderImageView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}