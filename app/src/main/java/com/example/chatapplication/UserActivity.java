package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.chatapplication.Adapters.ViewPagerAdapter;
import com.example.chatapplication.AddFriend.AcceptRequest;
import com.example.chatapplication.AddFriend.SendRequest;
import com.example.chatapplication.GroupCreation.CreateGroup;
import com.example.chatapplication.LoginSignUp.Login;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.Settings.Settings;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserActivity extends AppCompatActivity {
   FirebaseFirestore database;
   Users myProfile;
    private FirebaseAuth firebaseAuth;
    String TAG="task11";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // database object
        database = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }
    public void fillDetails()
    {
        database.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    myProfile = documentSnapshot.toObject(Users.class);
                    attachPageViewer();
                    addOnlineUpdate();
                } else {
                    Log.d("task43", "task failed");
                }
            }
        });

    }

    public void attachPageViewer()
    {
        ViewPager viewPager=(ViewPager)findViewById(R.id.viewPager);
        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabLayout);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(),myProfile);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.logged_in_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.notificationRequests: seeRequests();
                                            return true;

            case R.id.addFriend:addFriend();
                                return true;

            case R.id.menuSettings:
                settings();
                return true;

            case R.id.logOut:   signOut();
                                return true;

            case  R.id.addNewGroup: newGroup();
                                    return true;
            //default: return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newGroup(){
        Intent intent= new Intent(getApplicationContext(), CreateGroup.class);
        intent.putExtra("myProfile",myProfile);
        startActivity(intent);
    }

    private void settings(){
        Intent intent= new Intent(getApplicationContext(), Settings.class);
        intent.putExtra("myProfile",myProfile);
        startActivity(intent);
    }

    private void signOut(){
        GoogleSignInClient googleSignInClient= GoogleSignIn.getClient(UserActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    private void addFriend(){
        Intent intent= new Intent(getApplicationContext(),SendRequest.class);
        intent.putExtra("myProfile",myProfile);
        startActivity(intent);
      //  fillDetails();
    }

    private void  seeRequests(){
        Intent intent= new Intent(getApplicationContext(), AcceptRequest.class);
        intent.putExtra("myProfile",myProfile);
        startActivity(intent);
      //  fillDetails();
    }


    @Override
    protected void onStart() {
        super.onStart();
        fillDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",false) .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated on pause!");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document on pause", e);
            }
        });
    }

    public void  addOnlineUpdate()
    {
        database.collection("Status").document(myProfile.getUserId()).update("isOnline",true) .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error updating document", e);
            }
        });
    }
}



