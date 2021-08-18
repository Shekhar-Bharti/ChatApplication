package com.example.chatapplication.LoginSignUp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.chatapplication.R;
import com.example.chatapplication.UserActivity;
import com.example.chatapplication.detailsFirstTime.FirstLogIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private static final String TAG = "Login";
    private FirebaseAuth firebaseAuth;
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN= 111;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private float alpha=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        tabLayout= findViewById(R.id.tabLayout);
        viewPager= findViewById(R.id.viewPagerLogin);
        signInButton= findViewById(R.id.signInButton);
        signInButton.setSize(signInButton.SIZE_ICON_ONLY);
        relativeLayout= findViewById(R.id.progressBarLogin);

        tabLayout.addTab(tabLayout.newTab().setText("Login"));
        tabLayout.addTab(tabLayout.newTab().setText("SignUp"));
        tabLayout.setTabGravity(tabLayout.GRAVITY_FILL);


        final LoginAdapter adapter= new LoginAdapter(getSupportFragmentManager(),this,tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        animate();

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressStart();
                signIn();
            }
        });
    }

    private void animate(){

        signInButton.setTranslationY(300);
        tabLayout.setTranslationY(300);

        signInButton.setAlpha(alpha);
        tabLayout.setAlpha(alpha);

        signInButton.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();
        tabLayout.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
    }

    private void signIn() {
        mGoogleSignInClient.signOut();
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
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            //Toast.makeText(Login.this,account.getDisplayName(),Toast.LENGTH_LONG).show();
            AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
            firebaseAuth.signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    FirebaseFirestore.getInstance().collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if(!document.exists()) {
                                    startActivity(new Intent(getApplicationContext(), FirstLogIn.class));
                                    finish();
                                }else {
                                    startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                    finish();
                                }
                            } else {
                                startActivity(new Intent(getApplicationContext(), UserActivity.class));
                                finish();
                                Log.d("task33", "Failed with: ", task.getException());
                            }
                        }
                    });
                    Toast.makeText(getApplicationContext(),"Successfully Logged in",Toast.LENGTH_LONG).show();
                }
            });
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            progressStop();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    public void progressStart(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        relativeLayout.setVisibility(View.VISIBLE);
    }

    public void progressStop(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        relativeLayout.setVisibility(View.INVISIBLE);
    }
}