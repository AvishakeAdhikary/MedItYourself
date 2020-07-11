package com.example.med_it_yourself;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ProviderConfirmPicture extends AppCompatActivity {

    private ImageView image;
    private Button confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_confirm_picture);
        confirm = (Button) findViewById(R.id.confirm_image);

        String filename = getIntent().getStringExtra("filename");

        image = (ImageView) findViewById(R.id.view_image);

        File imgFile = new  File(filename);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(myBitmap);

        }

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProviderConfirmPicture.this,"Order Confirmed",Toast.LENGTH_LONG);
            }
        });
    }
}