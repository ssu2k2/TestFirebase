package com.example.yongsu.testfirebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private FirebaseAuth mAuth;
    ProgressDialog progDialog ;
    Context context;

    EditText edtId, edtPw;
    Button btnLogin, btnCreate;
    TextView tvResult;
    SharedPreferences pref ;
    private FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                Log.d(TAG, "onAuthStateChanged:Sign in :" + user.getUid());
                saveUid(user.getUid());
            } else {
                Log.d(TAG, "onAuthStateChanged:Sign out");
            }
        }
    };
    private void saveUid(String uid){
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UID", uid);
        editor.commit();
    }
    private void moveToRoot(){
        Intent intent = new Intent(MainActivity.this, RootActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private boolean isValidPasswd(String target){
        Pattern p = Pattern.compile("(^.*(?=.{6,100})(?=.*[0-9])(?=.*[a-zA-Z]).*$)");
        Matcher m = p.matcher(target);
        if(m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")){
            return true;
        }else {
            return false;
        }
    }
    private boolean isValidEmail(String target) {
        if (target == null || TextUtils.isEmpty(target)){
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        progDialog = new ProgressDialog(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            Log.d(TAG, "Current User:" + mAuth.getCurrentUser().getEmail());
            moveToRoot();
        } else {
            Log.d(TAG, "Log out State");
        }
        edtId = (EditText)findViewById(R.id.edtId);
        edtPw = (EditText)findViewById(R.id.edtPw);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnCreate= (Button)findViewById(R.id.btnCreate);
        tvResult = (TextView)findViewById(R.id.tvResult);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edtId.getText().toString().trim();
                String pw = edtPw.getText().toString().trim();
                if(isValidEmail(id) && isValidPasswd(pw)){
                    createAccount(id, pw);
                }else {
                    Toast.makeText(MainActivity.this, "Check the Input Data", Toast.LENGTH_SHORT).show();
                };;
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = edtId.getText().toString().trim();
                String pw = edtPw.getText().toString().trim();
                if(isValidEmail(id) && isValidPasswd(pw)){
                    signin(id, pw);
                }else {
                    Toast.makeText(MainActivity.this, "Check the Input Data", Toast.LENGTH_SHORT).show();
                };
            }
        });
    }
    private void signout(){
        FirebaseAuth.getInstance().signOut();
    }
    private void signin(String email, String password){
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Log in ....");
        // show dialog
        progDialog.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        progDialog.dismiss();
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed" +  task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        } else {
                            tvResult.setText("Log in  Complete");
                            moveToRoot();
                        }
                    }
                });
    }
    private void createAccount(String email, String password) {
        if(!isValidEmail(email)){
            Log.e(TAG, "createAccount: email is not valid ");
            Toast.makeText(MainActivity.this, "Email is not valid",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidPasswd(password)){
            Log.e(TAG, "createAccount: password is not valid ");
            Toast.makeText(context, "Password is not valid",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Create User  ....");
        // show dialog
        progDialog.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        progDialog.dismiss();
                       if (!task.isSuccessful()) {
                            Toast.makeText(context, "Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            tvResult.setText("Account Create Complete");
                           moveToRoot();
                        }
                    }
                });
    }
}
