package com.example.donardrones;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AcceptorRequstActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String id="";
    private TextView blood_gp;
    private TextView location;
    private EditText your_location;
    private Button accept;
    private Button deny;
    private String address;
    private double longitude;
    private double latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptor_requst);
        blood_gp = findViewById(R.id.med_acc_blood_gp);
        location = findViewById(R.id.med_acc_location);
        your_location = findViewById(R.id.med_address);
        accept = findViewById(R.id.med_accept_buton);
        deny = findViewById(R.id.med_deny_button);

        db.collection("blood_request").whereEqualTo("active","yes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(!task.getResult().getDocuments().isEmpty()){
                        for(DocumentSnapshot doc :task.getResult().getDocuments()){
                            if( Util.getDeny_request_ids().isEmpty() || Util.getDeny_request_ids().indexOf(doc.getId())==1){
                                id= doc.getId();
                                Request request = new Request(doc.getData());
                                blood_gp.setText(request.getBlood_group());
                                location.setText(request.getAcceptor_location().getAddress());
                            }
                        }
                    }
                }
            }
        });

        accept.setOnClickListener(this);
        deny.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.med_accept_buton:
                String add = your_location.getText().toString();
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(add, getApplicationContext(), new GeocoderHandler());
                break;

            case R.id.med_deny_button:
                Util.getDeny_request_ids().add(id);
                Intent i = new Intent(AcceptorRequstActivity.this,MedMainActivity.class);
                startActivity(i);
                break;
        }
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
            Toast.makeText(AcceptorRequstActivity.this,address,Toast.LENGTH_SHORT).show();
            if(latitude!=-1 && longitude!=-1) {
                User_Location user_location = new User_Location(address, longitude, latitude);

                Map<String,Object> m= new HashMap<>();
                m.put("medassist_location",user_location);
                m.put("medassist",Util.getCurrentUser());
                db.collection("blood_request").document(id).update(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AcceptorRequstActivity.this,"Thanks for accepting request!",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(AcceptorRequstActivity.this, MedAssignedActivity.class);
                        startActivity(i);
                    }
                });

            }
        }
    }
}
