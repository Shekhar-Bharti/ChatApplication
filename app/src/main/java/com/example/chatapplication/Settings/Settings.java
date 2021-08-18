package com.example.chatapplication.Settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Settings extends AppCompatActivity {
    ImageView profilePic;
    ImageView iconCamera;
    ConstraintLayout profilePicLayout;
    FrameLayout frameLayout;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    Users currentUser;

    final float original = (float)1, increaseScale = (float)3;
    float traverseX, traverseY;
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        //getSupportFragmentManager().addOnBackStackChangedListener((FragmentManager.OnBackStackChangedListener) this);

        currentUser= (Users)getIntent().getSerializableExtra("myProfile");

        profilePic = findViewById(R.id.ImageViewSettingsProfilePic);
        iconCamera = findViewById(R.id.ImageViewCamera);
        profilePicLayout = findViewById(R.id.layout1);
        frameLayout= findViewById(R.id.fragmentSettings);

        firebaseStorage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference=firebaseStorage.getReference().child(currentUser.getDpMidLocation());

        setProfilePic(storageReference,profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag<1) {
                    flag++;
                    animate();
                    changeFragment();
                }
            }
        });
    }

    @GlideModule
    public class MyAppGlideModule extends AppGlideModule {

        @Override
        public void registerComponents(Context context, Glide glide, Registry registry) {
            // Register FirebaseImageLoader to handle StorageReference
            registry.append(StorageReference.class, InputStream.class,
                    new FirebaseImageLoader.Factory());
        }
    }

    void setProfilePic(StorageReference storageReference, ImageView profilePic){

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String imageURL = uri.toString();
                Glide.with(getApplicationContext())
                        .load(imageURL)
                        .placeholder(R.drawable.avatar)
                        .into(profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                profilePic.setImageResource(R.drawable.avatar);
            }
        });
    }

    private void animate(){

          traverseY= (float) (profilePicLayout.getHeight()*(increaseScale-1));

          ScaleAnimation scaleAnimation= new ScaleAnimation(original,increaseScale,original,increaseScale);
          scaleAnimation.setFillAfter(true);
          scaleAnimation.setDuration(200);

          iconCamera.setVisibility(View.VISIBLE);

          profilePicLayout.startAnimation(scaleAnimation);
          traverseX= (float) ((frameLayout.getWidth()/(increaseScale*2))-(profilePicLayout.getWidth()/2));
          profilePicLayout.animate().translationX(traverseX).setDuration(200).start();
          frameLayout.animate().translationY(traverseY).setDuration(200).start();
    }

    private void changeFragment(){
        getSupportActionBar().setTitle("Profile");

        try{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentSettings,new FragmentSettingsUpdate(currentUser))
                    .addToBackStack("MyFragment1")
                    .commit();

        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(flag>0) {

            getSupportActionBar().setTitle("Settings");
            ScaleAnimation scaleAnimation = new ScaleAnimation(increaseScale, original, increaseScale, original);
            scaleAnimation.setFillAfter(true);
            scaleAnimation.setDuration(200);

            iconCamera.setVisibility(View.INVISIBLE);

            profilePicLayout.startAnimation(scaleAnimation);
            profilePicLayout.animate().translationX(0).setDuration(200).start();
            frameLayout.animate().translationY(0).setDuration(200).start();
        }
        flag--;
    }

}
