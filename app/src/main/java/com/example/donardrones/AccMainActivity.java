package com.example.donardrones;

import androidx.appcompat.app.AppCompatActivity;

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
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acc_main);
        edit_blood_gp = findViewById(R.id.acc_blood_gp_input);
        edit_units = findViewById(R.id.acc_units_input);
        edit_location = findViewById(R.id.acc_location_input);
        submit_button = findViewById(R.id.acc_submit_button);
        submit_button.setOnClickListener(this);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.acc_submit_button :
                //TODO: get req data & "Location and submit request
                String address = edit_location.getText().toString();
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(address,
                        getApplicationContext(), new GeocoderHandler());
                Map<String,String> map = new HashMap<>();
                map.put("blood_gp",edit_blood_gp.getText().toString());
                db.collection("request").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(AccMainActivity.this,"Request submitted",Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }
    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            Toast.makeText(AccMainActivity.this,locationAddress,Toast.LENGTH_SHORT).show();
            //latLongTV.setText(locationAddress);
        }
    }
}
