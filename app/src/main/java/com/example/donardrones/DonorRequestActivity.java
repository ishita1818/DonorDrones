package com.example.donardrones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class DonorRequestActivity extends AppCompatActivity {

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private TextView blood_gp ;
    private TextView location;
    private EditText your_location;
    private Button accept;
    private Button deny;
    private String id="";
    private String address;
    private double longitude;
    private double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_request);
        blood_gp = findViewById(R.id.donor_request_blood_gp);
        location = findViewById(R.id.donor_request_location);
        your_location = findViewById(R.id.donor_request_edit_location);
        accept = findViewById(R.id.donor_request_accept);
        deny =  findViewById(R.id.donor_request_deny);

        db.collection("blood_request").whereEqualTo("active","yes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot doc :queryDocumentSnapshots){
                        if( Util.getDeny_request_ids().isEmpty() || Util.getDeny_request_ids().indexOf(doc.getId())==1){
                            id= doc.getId();
                            Request request = new Request(doc.getData());
                            blood_gp.setText(request.getBlood_group());
                            location.setText(request.getAcceptor_location().getAddress());
                        }
                    }
                }
            }
        });
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String add = your_location.getText().toString();
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(add, getApplicationContext(), new GeocoderHandler());

            }
        });
        deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!id.isEmpty()) {
                    Util.getDeny_request_ids().add(id);
                    Intent i = new Intent(DonorRequestActivity.this, DonorMainActivity.class);
                    startActivity(i);
                }
            }
        });
    }


    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    address = bundle.getString("address");
                    longitude= bundle.getDouble("longitude");
                    latitude= bundle.getDouble("latitude");
                    break;
                default:
                    address = null;
            }
            Toast.makeText(DonorRequestActivity.this,address,Toast.LENGTH_SHORT).show();
            if(latitude!=-1 && longitude!=-1) {
                User_Location user_location = new User_Location(address, longitude, latitude);

                Map<String,Object> m= new HashMap<>();
                m.put("donor_location",user_location);
                m.put("donor",Util.getCurrentUser());
                db.collection("blood_request").document(id).update(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DonorRequestActivity.this,"Thanks for accepting request!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(DonorRequestActivity.this, MedRequestActivity.class);
                        startActivity(i);
                    }
                });

            }
        }
    }
}
