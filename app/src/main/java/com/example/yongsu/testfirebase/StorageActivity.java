package com.example.yongsu.testfirebase;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class StorageActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    ImageView ivImage;
    Button btnPre, btnNext;
    TextView tvPage;
    ProgressDialog progDialog ;
    int index = 1;
    String folder = "images";
    String fileName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        progDialog = new ProgressDialog(StorageActivity.this);
        ivImage = (ImageView)findViewById(R.id.ivImage);
        tvPage =(TextView)findViewById(R.id.tvPage);
        btnNext = (Button)findViewById(R.id.btnNext);
        btnPre = (Button)findViewById(R.id.btnPre);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        fileName = String.format("p0%d.png", index);
        String storagePath = folder + "/" + fileName;
        LoadFile(storagePath);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index < 5){
                    index ++;
                    fileName = String.format("p0%d.png", index);
                    String storagePath = folder + "/" + fileName;
                    LoadFile(storagePath);
                    tvPage.setText("Page:" + index);
                }
            }
        });
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(index > 1){
                    index --;
                    fileName = String.format("p0%d.png", index);
                    String storagePath = folder + "/" + fileName;
                    LoadFile(storagePath);
                    tvPage.setText("Page:" + index);
                }

            }
        });
    }
    private void LoadFile(String storagePath){
        StorageReference imageRef = mStorageRef.child(storagePath);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Loading ....");
        // show dialog
        progDialog.show();
        try {
            final File imageFile = File.createTempFile("images", "png");
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                    ivImage.setImageBitmap(bitmap);
                    Toast.makeText(StorageActivity.this, "Load Image Complete", Toast.LENGTH_SHORT).show();
                    progDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(StorageActivity.this, "Fail to Download", Toast.LENGTH_SHORT).show();
                    progDialog.dismiss();
                }
            });
        }catch (Exception e){

        }
    }
}
