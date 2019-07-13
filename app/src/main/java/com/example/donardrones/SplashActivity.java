package com.example.donardrones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT=3000;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int type =-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            db.collection("users").whereEqualTo("email",account.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()) {
                        User u = new User(queryDocumentSnapshots.getDocuments().get(0).getData());
                        type = u.getType();
                        Util.setCurrentUser(u);
                        updateUI(account);
                    }
                }
            });
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    //Intent is used to switch from one activity to another.

                    startActivity(i);
                    //invoke the SecondActivity.

                    finish();
                    //the current activity will get finished.
                }
            }, SPLASH_SCREEN_TIME_OUT);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account!=null){
            Intent i;
            switch (type){
                case 0:
                    db.collection("blood_request").whereEqualTo("acceptor",Util.getCurrentUser()).whereEqualTo("active","yes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                                Intent intent = new Intent(SplashActivity.this, AccReqAcceptedActivity.class);
                                startActivity(intent);
                            }else{
                                Intent i=new Intent(SplashActivity.this, AccMainActivity.class);
                                startActivity(i);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent i=new Intent(SplashActivity.this, AccMainActivity.class);
                            startActivity(i);
                        }
                    });

                    break;
                case 1 :
                    db.collection("blood_request").whereEqualTo("donor",Util.getCurrentUser()).whereEqualTo("active","yes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.getDocuments().isEmpty()) {
                                Intent intent = new Intent(SplashActivity.this, MedRequestActivity.class);
                                startActivity(intent);
                            }else{
                                Intent i=new Intent(SplashActivity.this, DonorMainActivity.class);
                                startActivity(i);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Intent i=new Intent(SplashActivity.this, DonorMainActivity.class);
                            startActivity(i);
                        }
                    });
                    break;
                case 2:
                    i=new Intent(SplashActivity.this, MedMainActivity.class);
                    startActivity(i);
                    break;
            }

        }
    }
}
