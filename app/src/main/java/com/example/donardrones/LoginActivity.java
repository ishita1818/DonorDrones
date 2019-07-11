package com.example.donardrones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.donardrones.Util;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 100;
    private EditText name_input;
    private EditText phone_input;
    private GoogleSignInClient mGoogleSignInClient;
    private static int type=-1;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activtiy);
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton acceptor_button = findViewById(R.id.sign_in_button_acceptor);
        SignInButton donor_button = findViewById(R.id.sign_in_button_donor);
        SignInButton med_button = findViewById(R.id.sign_in_button_med);
        name_input= findViewById(R.id.name_input);
        phone_input=findViewById(R.id.phone_input);
        acceptor_button.setOnClickListener(this);
        donor_button.setOnClickListener(this);
        med_button.setOnClickListener(this);
        setGoogleButtonText(acceptor_button,"Acceptor Sign In");
        setGoogleButtonText(donor_button,"Donor Sign In");
        setGoogleButtonText(med_button,"MedAssist Sign In");

    }
    protected void setGoogleButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button_acceptor:
                type=0;
                signIn();
                Toast.makeText(this, "Signed in as Acceptor", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_in_button_donor:
                type=1;
                signIn();
                Toast.makeText(this, "Signed in as Donor", Toast.LENGTH_SHORT).show();
                break;
            case R.id.sign_in_button_med:
                type = 2;
                signIn();
                Toast.makeText(this, "Signed in as MedAssist", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            final GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            if(name_input.getText().toString().matches("")){
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show();
            }else if(phone_input.getText().toString().matches("")){
                Toast.makeText(this,"Please enter your phone number",Toast.LENGTH_SHORT).show();
            }
            else {
                final User user = new User(name_input.getText().toString(),account.getEmail(),phone_input.getText().toString(),type);
                //Map<String,User> m = new HashMap<>();
                //m.put(account.getEmail(),user);
                db.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.v(TAG,"Added into users!");
                    }
                });
                db.collection(Util.getUserType(type)).add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(LoginActivity.this,"Added with id :"+ documentReference.getId(),Toast.LENGTH_SHORT).show();
                        Util.setCurrentUser(user);
                        updateUI(account);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });

                Toast.makeText(this, account.getEmail(), Toast.LENGTH_SHORT).show();

            }

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null) {
            db.collection("users").whereEqualTo("email",account.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    User u = new User(queryDocumentSnapshots.getDocuments().get(0).getData());
                    type = u.getType();
                    Util.setCurrentUser(u);
                    updateUI(account);
                }
            });
        }else
            Toast.makeText(LoginActivity.this,"Error",Toast.LENGTH_SHORT).show();
    }

    private void updateUI(GoogleSignInAccount account) {
        if(account!=null){
            Intent i;
            switch (type){
                case 0:
                    i=new Intent(LoginActivity.this, AccMainActivity.class);
                    startActivity(i);
                    break;
                case 1 :
                    i=new Intent(LoginActivity.this, DonarMainActivity.class);
                    startActivity(i);
                    break;
                case 2:
                    i=new Intent(LoginActivity.this, MedMainActivity.class);
                    startActivity(i);
                    break;
            }

        }
    }




}
