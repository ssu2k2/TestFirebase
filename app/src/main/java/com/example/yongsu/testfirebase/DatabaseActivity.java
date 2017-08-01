package com.example.yongsu.testfirebase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ListView lvList;
    EditText edtInput;
    Button btnSend;
    String user;
    ArrayList<String> alChatData;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        alChatData = new ArrayList<>();
        lvList = (ListView)findViewById(R.id.lvList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alChatData);
        lvList.setAdapter(adapter);

        edtInput = (EditText)findViewById(R.id.edtInput);
        btnSend = (Button)findViewById(R.id.btnSend);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser().getEmail();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = edtInput.getText().toString().trim();
                if(input.length() > 0){
                    ChatData c = new ChatData(user, input);
                    databaseReference.child("message").push().setValue(c);
                    edtInput.setText("");
                }
            }
        });
        databaseReference.child("message").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData c = dataSnapshot.getValue(ChatData.class);
                adapter.add(c.userName + " " + c.message);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
