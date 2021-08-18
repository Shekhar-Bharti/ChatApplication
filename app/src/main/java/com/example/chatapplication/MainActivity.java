package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.chatapplication.LoginSignUp.Login;
import com.example.chatapplication.detailsFirstTime.FirstLogIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Document;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth= FirebaseAuth.getInstance();
        progressBar= findViewById(R.id.progressBarMain);
    }


    @Override
    protected void onStart() {
        super.onStart();

        progressStart();
        if(firebaseAuth.getCurrentUser()!=null){
            checkForDocuments();
        }else{
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        }
    }

    void checkForDocuments(){
        FirebaseFirestore firestore= FirebaseFirestore.getInstance();
        DocumentReference reference= firestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid());
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        startActivity(new Intent(getApplicationContext(), UserActivity.class));
                        finish();
                    } else {
                        firebaseAuth.signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                    }
                }else{
                    signOut();
                    Toast.makeText(getApplicationContext(),"Error Logging In",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Login.class));
                    finish();
                }
            }
        });
    }

    private void signOut(){
        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void progressStart(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void progressStop(){
        progressBar.setVisibility(View.INVISIBLE);
    }

}