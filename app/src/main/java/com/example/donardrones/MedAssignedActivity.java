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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import static android.view.View.GONE;

public class MedAssignedActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView name;
    private TextView phone;
    private TextView address;
    private Button call_medassist;
    private Button blood_collected;
    private String id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_assigned);
        name = findViewById(R.id.med_donor_name);
        phone = findViewById(R.id.med_donor_phone);
        address = findViewById(R.id.med_donor_location);
        call_medassist = findViewById(R.id.med_call_donor);
        blood_collected = findViewById(R.id.med_blood_collected);

        db.collection("blood_request").whereEqualTo("medassist",Util.getCurrentUser()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getDocuments().isEmpty()){
                        Request request = new Request(task.getResult().getDocuments().get(0).getData());
                            id= task.getResult().getDocuments().get(0).getId();
                            address.setText(request.getDonor_location().getAddress());
                            name.setText(request.getDonor().getUser_name());
                            phone.setText(request.getDonor().getPhone());

                    }
                }
            }
        });

        call_medassist.setOnClickListener(this);
        blood_collected.setOnClickListener(this);
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
            case R.id.med_blood_collected:
                Util.getDeny_request_ids().add(id);
                Intent i = new Intent(MedAssignedActivity.this, MedMainActivity.class);
                startActivity(i);
                break;
            case R.id.med_call_donor :
                if(!phone.getText().toString().isEmpty()){
                    dialPhoneNumber(phone.getText().toString());
                }
                break;
        }
    }
}
