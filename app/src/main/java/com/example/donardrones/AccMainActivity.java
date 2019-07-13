package com.example.donardrones;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.HashMap;
import java.util.Map;

public class AccMainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edit_blood_gp;
    private EditText edit_units;
    private EditText edit_location;
    private Button submit_button;
    private boolean gotlocation=false;
    private String address="";
    private double longitude=-1;
    private double latitude=-1;
    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_main);
        edit_blood_gp = findViewById(R.id.acc_blood_gp_input);
        edit_units = findViewById(R.id.acc_units_input);
        edit_location = findViewById(R.id.acc_location_input);
        submit_button = findViewById(R.id.acc_submit_button);
        submit_button.setOnClickListener(this);

        //Added in donor and medassist activities
        //FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.acc_submit_button :

                String add = edit_location.getText().toString();
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(add, getApplicationContext(), new GeocoderHandler());


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
            Toast.makeText(AccMainActivity.this,address,Toast.LENGTH_SHORT).show();
            if(latitude!=-1 && longitude!=-1) {
                User_Location user_location = new User_Location(address, longitude, latitude);
                Request request = new Request();
                request.setBlood_group(edit_blood_gp.getText().toString());
                request.setAcceptor(Util.getCurrentUser());
                request.setActive("yes");
                request.setUnits(edit_units.getText().toString());
                request.setAcceptor_location(user_location);

                Map<String,String> m =  new HashMap<>();
                m.put("blood_group",edit_blood_gp.getText().toString());
                db.collection("blood_request").add(request).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AccMainActivity.this, "Request submitted" + documentReference.getId(), Toast.LENGTH_SHORT).show();

                    }
                });
                db.collection("request").add(m).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AccMainActivity.this, "Request submitted" + documentReference.getId(), Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(AccMainActivity.this, AccReqAcceptedActivity.class);
                        startActivity(i);
                    }
                });
            }
        }
    }
}
