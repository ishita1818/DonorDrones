package com.example.donardrones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import static android.view.View.GONE;

public class MedRequestActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView medassist_name;
    private TextView medassist_phone;
    private TextView medassist_address;
    private LinearLayout req_accepted;
    private LinearLayout req_pending;
    private Button call_medassist;
    private Button blood_donated;
    private String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_request);

        medassist_name = findViewById(R.id.donor_medassist_name);
        medassist_address = findViewById(R.id.donor_medassist_address);
        medassist_phone = findViewById(R.id.donor_medassist_phone);
        req_pending = findViewById(R.id.donor_layout_req_pending);
        req_accepted = findViewById(R.id.donor_layout_req_accepted);

        call_medassist = findViewById(R.id.donor_button_call_medassist);
        blood_donated = findViewById(R.id.donor_button_blood_received);

        db.collection("blood_request").whereEqualTo("donor",Util.getCurrentUser()).whereEqualTo("actie","yes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                        if(!documentSnapshot.getData().isEmpty()) {
                            Request request = new Request(documentSnapshot.getData());
                            if (!request.getMedassist().getEmail().isEmpty()) {
                                id= documentSnapshot.getId();
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

        db.collection("blood_request").whereEqualTo("donor",Util.getCurrentUser()).whereEqualTo("active","yes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e==null){
                    if(!queryDocumentSnapshots.isEmpty()){
                        for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            if(!documentSnapshot.getData().isEmpty()) {
                                Toast.makeText(MedRequestActivity.this, "MedAssist details have been updated!", Toast.LENGTH_SHORT).show();
                                Request request = new Request(documentSnapshot.getData());
                                if (!request.getMedassist().getEmail().isEmpty()) {
                                    id= documentSnapshot.getId();
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
            }
        });
        call_medassist.setOnClickListener(this);
        blood_donated.setOnClickListener(this);
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
        switch (view.getId()){
            case R.id.donor_button_blood_received:
                if(!id.isEmpty()){
                    Util.getDeny_request_ids().add(id);
                    Intent i = new Intent(MedRequestActivity.this,DonorMainActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.donor_button_call_medassist:
                if(!medassist_phone.getText().toString().isEmpty()){
                    dialPhoneNumber(medassist_phone.getText().toString());
                }
                break;
        }
    }
}
