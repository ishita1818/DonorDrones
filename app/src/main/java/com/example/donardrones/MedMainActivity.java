package com.example.donardrones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.annotation.Nullable;

public class MedMainActivity extends AppCompatActivity {

    private FirebaseFirestore db =FirebaseFirestore.getInstance();
    private String id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_main);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        db.collection("blood_request").whereEqualTo("active","yes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null){
                    if(!queryDocumentSnapshots.getDocuments().isEmpty())
                    if( Util.getDeny_request_ids().isEmpty() || Util.getDeny_request_ids().indexOf(queryDocumentSnapshots.getDocuments().get(0).getId())==1){
                        id= queryDocumentSnapshots.getDocuments().get(0).getId();
                        Intent i = new Intent(MedMainActivity.this,AcceptorRequstActivity.class);
                        startActivity(i);
                    }
                }
            }
        });
    }
}
