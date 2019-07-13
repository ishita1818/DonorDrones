package com.example.donardrones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import javax.annotation.Nullable;

public class DonorMainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donar_main);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
        db.collection("blood_request").whereEqualTo("active","yes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null){
                    if(!queryDocumentSnapshots.isEmpty()){

                        if(!queryDocumentSnapshots.isEmpty()){
                            for(DocumentSnapshot doc :queryDocumentSnapshots){
                                if( Util.getDeny_request_ids().isEmpty() || Util.getDeny_request_ids().indexOf(doc.getId())==1){
                                    Intent i = new Intent(DonorMainActivity.this,DonorRequestActivity.class);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
}
