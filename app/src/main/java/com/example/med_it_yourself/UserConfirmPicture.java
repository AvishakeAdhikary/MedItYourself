package com.example.med_it_yourself;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserConfirmPicture extends AppCompatActivity {

    private ImageView image;
    private Button confirm;
    private File imgFile;
    private Uri fileUri;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_picture_user);
        confirm = (Button) findViewById(R.id.confirm_image);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String filename = getIntent().getStringExtra("filename");

        image = (ImageView) findViewById(R.id.view_image);

        imgFile = new  File(filename);

        fileUri = Uri.fromFile(imgFile);

        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(myBitmap);

        }

            confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("UserOrders").child(userID);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), fileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = filePath.putBytes(data);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                        return;
                    }
                });
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                        Map newImage = new HashMap();
                        newImage.put("OrderImageUrl", downloadUrl.toString());
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);
                        databaseReference.updateChildren(newImage);
                        finish();
                        return;
                    }
                });
            }
        });
    }

}