package com.example.chatapplication.LoginSignUp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FragmentSignUpTab extends Fragment {
    private EditText editTextPassword, editTextEmail, editTextConfirmPassword;
    private Button buttonCreateAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private RelativeLayout relativeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_sign_up,container,false);

        editTextEmail= root.findViewById(R.id.editTextSignUpEmail);
        editTextPassword= root.findViewById(R.id.editTextSignUpPassword);
        buttonCreateAccount= root.findViewById(R.id.buttonSignUp);
        editTextConfirmPassword= root.findViewById(R.id.editTextSignUpConfirmPassword);
        relativeLayout= root.findViewById(R.id.progressBarSignUpFragment);

        firebaseAuth = FirebaseAuth.getInstance();

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(v);
            }
        });

        return root;
    }

    private void createAccount(View v){
        String userName, password, email;
        password= editTextPassword.getText().toString().trim();
        email= editTextEmail.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            editTextEmail.setError("email Required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required");
            return;
        }

        progressStart();

        try {
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Toast.makeText(getActivity(), "Successfully Registered", Toast.LENGTH_LONG).show();

                    firebaseUser = firebaseAuth.getCurrentUser();

                    try {
                        verificationDialogBox(firebaseUser, "Send", getActivity()).show();
                    }catch (Exception e){
                        Toast.makeText(getActivity(),"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    editTextEmail.setText("");
                    editTextConfirmPassword.setText("");
                    editTextPassword.setText("");
                    firebaseAuth.signOut();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e){
            String TAG ="Register";
            Toast.makeText(getActivity(),TAG+": Error: "+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        progressStop();
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
