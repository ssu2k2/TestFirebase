package com.example.yongsu.testfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserlistActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;
    String user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getEmail();
        user = user.replace(".", "_");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        uInfo = new UserInfo();
        makeDummy();
        ((Button)findViewById(R.id.btnClick)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("user").child(user).push().setValue(uInfo);
            }
        });
    }
    UserInfo uInfo;
    private void makeDummy(){
        uInfo = new UserInfo();
        uInfo.name = "CHO";
        uInfo.age = 30;

    }
}
