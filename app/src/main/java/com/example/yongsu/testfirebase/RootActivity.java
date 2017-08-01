package com.example.yongsu.testfirebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RootActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;
    TextView tvUid;
    Button btnDB, btnStorage;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

        tvUid  = (TextView)findViewById(R.id.tvUid);
        mAuth = FirebaseAuth.getInstance();
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        String uid = pref.getString("UID", "");
        tvUid.setText("User:" + mAuth.getCurrentUser().getEmail());

        ((Button)findViewById(R.id.btnDB)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RootActivity.this, DatabaseActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        ((Button)findViewById(R.id.btnStorage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RootActivity.this, StorageActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        ((Button)findViewById(R.id.btnUpDown)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RootActivity.this, UpLoadActivity.class);
                startActivityForResult(intent, 2);
            }
        });
        ((Button)findViewById(R.id.btnUserList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RootActivity.this, UserlistActivity.class);
                startActivityForResult(intent, 3);
            }
        });
        ((Button)findViewById(R.id.btnAlarm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(RootActivity.this, AlarmActivity.class);
                startActivityForResult(intent, 4);
            }
        });

        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
        super.onBackPressed();
    }
}
