package com.example.donardrones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import static android.view.View.GONE;

public class AccReqAcceptedActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView medassist_name;
    private TextView medassist_phone;
    private TextView medassist_address;
    private LinearLayout req_accepted;
    private LinearLayout req_pending;
    private Button call_medassist;
    private Button blood_received;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_req_accepted);

        medassist_address = findViewById(R.id.acc_medassist_address);
        medassist_name = findViewById(R.id.acc_medassist_name);
        medassist_phone = findViewById(R.id.acc_medassist_phone);

        req_accepted = findViewById(R.id.acc_layout_req_accepted);
        req_pending = findViewById(R.id.acc_layout_req_pending);
        call_medassist = findViewById(R.id.acc_button_call_medassist);
        blood_received = findViewById(R.id.acc_button_blood_received);
        call_medassist.setOnClickListener(this);
        blood_received.setOnClickListener(this);
        db.collection("blood_request").whereEqualTo("acceptor",Util.getCurrentUser()).whereEqualTo("active","yes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        if(!documentSnapshot.getData().isEmpty()) {
                            Request request = new Request(documentSnapshot.getData());
                            if (!request.getMedassist().getEmail().isEmpty()) {
                                req_pending.setVisibility(GONE);
                                req_accepted.setVisibility(View.VISIBLE);
                                medassist_address.setText(request.getMedassist_location().getAddress());
                                medassist_name.setText(request.getMedassist().getUser_name());
                                medassist_phone.setText(request.getMedassist().getPhone());
                            }else{
                                req_accepted.setVisibility(GONE);
                                req_pending.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
        db.collection("blood_request").whereEqualTo("acceptor",Util.getCurrentUser()).whereEqualTo("active","yes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        if(!documentSnapshot.getData().isEmpty()) {
                            Toast.makeText(AccReqAcceptedActivity.this, "MedAssist details have been updated!", Toast.LENGTH_SHORT).show();
                            Request request = new Request(documentSnapshot.getData());
                            if (!request.getMedassist().getEmail().isEmpty()) {
                                req_pending.setVisibility(GONE);
                                req_accepted.setVisibility(View.VISIBLE);
                                medassist_address.setText(request.getMedassist_location().getAddress());
                                medassist_name.setText(request.getMedassist().getUser_name());
                                medassist_phone.setText(request.getMedassist().getPhone());
                            }
                        }
                    }
                }
            }
        });

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.acc_button_blood_received :

                final CollectionReference itemsRef = db.collection("blood_request");
                itemsRef.whereEqualTo("acceptor",Util.getCurrentUser()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if(task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()) {
                                itemsRef.document(document.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AccReqAcceptedActivity.this,"Thanks for using DonorDrones! Take Care :)",Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(AccReqAcceptedActivity.this,AccMainActivity.class);
                                        startActivity(i);
                                    }
                                });

                            }
                        }
                    }
                });

                break;

            case R.id.acc_button_call_medassist :
                if(!medassist_phone.getText().toString().isEmpty()){
                    dialPhoneNumber(medassist_phone.getText().toString());
                }
                break;
        }
    }
}
