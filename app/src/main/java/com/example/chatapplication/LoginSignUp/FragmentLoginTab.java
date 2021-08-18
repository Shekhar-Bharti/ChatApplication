package com.example.chatapplication.LoginSignUp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.UserActivity;
import com.example.chatapplication.detailsFirstTime.FirstLogIn;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class FragmentLoginTab extends Fragment {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewForgotPassword;
    private Button buttonLogin;
    private float alpha=0;
    private FirebaseAuth firebaseAuth;
    private RelativeLayout relativeLayout;
    private FirebaseFirestore database;
    private Users myProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_login,container,false);

        editTextEmail= root.findViewById(R.id.editTextLoginEmail);
        editTextPassword= root.findViewById(R.id.editTextLoginPassword);
        textViewForgotPassword= root.findViewById(R.id.textViewForgotPassword);
        buttonLogin= root.findViewById(R.id.buttonLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        relativeLayout= root.findViewById(R.id.progressBarLoginFragment);
        database= FirebaseFirestore.getInstance();

        animate();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword(v);
            }
        });


        return root;
    }

    private void animate(){

        textViewForgotPassword.setTranslationX(800);
        buttonLogin.setTranslationX(800);

        editTextEmail.setAlpha(alpha);
        editTextPassword.setAlpha(alpha);
        textViewForgotPassword.setAlpha(alpha);
        buttonLogin.setAlpha(alpha);

        editTextEmail.animate().alpha(1).setDuration(800).setStartDelay(300).start();
        editTextPassword.animate().alpha(1).setDuration(800).setStartDelay(500).start();
        textViewForgotPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        buttonLogin.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();
    }

    private void login(){
        String email, password;
        email= editTextEmail.getText().toString().trim();
        password= editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("Email Required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required");
            return;
        }
        progressStart();
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        database.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if(!document.exists()) {
                                        startActivity(new Intent(getContext(), FirstLogIn.class));
                                        getActivity().finish();
                                    }else {
                                        startActivity(new Intent(getActivity(), UserActivity.class));
                                        getActivity().finish();
                                    }
                                } else {
                                    startActivity(new Intent(getActivity(), UserActivity.class));
                                    getActivity().finish();
                                    Log.d("task33", "Failed with: ", task.getException());
                                }
                            }
                        });
                        Toast.makeText(getActivity().getApplicationContext(),"Successfully Logged in",Toast.LENGTH_LONG).show();

                    }else{
                        try {
                            verificationDialogBox(firebaseAuth.getCurrentUser(), "Send", getActivity()).show();
                            firebaseAuth.signOut();
                        } catch (Exception e) {
                            Toast.makeText(getContext(),"AlertBox:"+e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    Toast.makeText(getActivity(),"Email/Password Incorrect",Toast.LENGTH_LONG).show();
                }
            }
        });
        progressStop();
    }

    private void forgotPassword(View v){
        final EditText resetMail = new EditText(v.getContext());

        final AlertDialog.Builder alertDialogForgotPassword= new AlertDialog.Builder(v.getContext());
        alertDialogForgotPassword.setTitle("Forgot Password?").setMessage("Enter Email:").setView(resetMail);

        alertDialogForgotPassword.setPositiveButton("Send Reset Link", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mail= resetMail.getText().toString();
                if(mail.isEmpty()){
                    return;
                }

                firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"Link has been Send to the Email",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Link not Send: "+e.getMessage() ,Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).setNegativeButton("Go Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //close
            }
        }).show();
    }

    public AlertDialog.Builder verificationDialogBox(FirebaseUser firebaseUser, String positiveButtonString, Context context) throws Exception{
        final AlertDialog.Builder alertDialogVerification= new AlertDialog.Builder(context);
        alertDialogVerification.setTitle("Verify your Email")
                .setMessage("Send Link to verify email")
                .setPositiveButton(positiveButtonString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Verification Link Send", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Login: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });
        return alertDialogVerification;
    }


    public void progressStart(){
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void progressStop(){
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        relativeLayout.setVisibility(View.INVISIBLE);
    }
}
