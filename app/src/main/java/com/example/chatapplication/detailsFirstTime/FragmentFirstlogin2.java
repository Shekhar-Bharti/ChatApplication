package com.example.chatapplication.detailsFirstTime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.UserActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FragmentFirstlogin2 extends Fragment {

    RelativeLayout backgroundTop, backgroundBottom;
    LinearLayout linearLayoutAnimate;
    FragmentActivity myContext;

    Button buttonNext, buttonSkip;
    EditText editTextAboutMe;
    ImageView imageViewProfilePic;


    Uri uriImage;
    Users currentUser;
    String uriPathPhone;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    final float alpha=0;
    private final int RESULT_CODE=100, RESULT_OK=-1;
    final float original = (float)0, increaseScale = (float)1;
    final int timeNext=300 , timeThis=500;

    public FragmentFirstlogin2(Users user){
        //constructor;
        currentUser=user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_firstlogin2,container,false);

        backgroundTop=view.findViewById(R.id.relativeLayoutTop1);
        backgroundBottom=view.findViewById(R.id.relativeLayoutBottom1);
        linearLayoutAnimate=view.findViewById(R.id.linearLayoutPage2);

        editTextAboutMe=view.findViewById(R.id.editTextFirstlogin2AboutMe);
        imageViewProfilePic=view.findViewById(R.id.fragmentFirstLogin2ProfilePic);
        buttonNext=view.findViewById(R.id.buttonFirstLogin2Next);
        buttonSkip=view.findViewById(R.id.buttonFirstLogin2Skip);

        buttonNext.setBackgroundColor(getResources().getColor(R.color.white));
        buttonNext.setTextColor(getResources().getColor(R.color.light_blue));

        animateBackGround();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()!=null){
            Glide.with(this)
                    .asBitmap()
                    .load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            imageViewProfilePic.setImageBitmap(resource);
                            uriImage= getImageUri(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            activateNextButton();
        }

        buttonSkip.setOnClickListener(v -> {
            skip();
        });

        buttonNext.setOnClickListener(v -> {
            next();
        });

        imageViewProfilePic.setOnClickListener(v -> changeImage());

        editTextAboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                activateNextButton();
            }
        });

        return view;
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, currentUser.getUserId(), null);
        return Uri.parse(path);
    }

    private void uploadDetails() throws Exception{
        firebaseFirestore.collection("Users").document(currentUser.getUserId()).set(currentUser).addOnSuccessListener(aVoid -> {
            Map<String, Boolean> data = new HashMap<>();
            data.put("isOnline",false);
            firebaseFirestore.collection("Status").document(currentUser.getUserId()).set(data);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Upload Fail: "+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_CODE && resultCode==RESULT_OK && data.getData()!=null){
            uriImage=data.getData();
            imageViewProfilePic.setImageURI(uriImage);
            activateNextButton();
        }
    }

    private void skip(){
        try {
            uploadDetails();
        }catch (Exception e){
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        animateNext();
        FragmentManager fragManager = myContext.getSupportFragmentManager();
        fragManager
                .beginTransaction()
                .replace(R.id.frameLayoutFirstLogIn2,new FragmentFirstLogin3(currentUser))
                .commit();
    }

    private void next(){
        if(!isImageValid(uriImage)){
            Snackbar.make(getView().findViewById(android.R.id.content),"Select Your Image/ Else can Skip for now",Snackbar.LENGTH_LONG).show();
        }else if(!isAboutMeValid(editTextAboutMe.getText().toString().trim())){
            editTextAboutMe.setError("Tell about yourself, Else can Skip for now");
        }else {
            uploadImagesAll();
        }
    }

    private void uploadImagesAll() {
        StorageReference referenceHigh, referenceLow, referenceMid;
        referenceHigh = storageReference.child(currentUser.getDpHighLocation());
        referenceLow = storageReference.child(currentUser.getDpLowLocation());
        referenceMid = storageReference.child(currentUser.getDpMidLocation());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriImage);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Setting Up your Profile.....");
        progressDialog.show();
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        referenceLow.putFile(getImageUri(reduceBitmapSize(bitmap, 19200)));
        referenceMid.putFile(getImageUri(reduceBitmapSize(bitmap, 1920000)));

        referenceHigh.putFile(uriImage).addOnFailureListener(exception -> {
            progressDialog.dismiss();
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(getContext(), "Error in Uploading files " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(taskSnapshot -> {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            progressDialog.dismiss();
            try {
                currentUser.setBio(editTextAboutMe.getText().toString());
                uploadDetails();
                animateNext();
                new Handler().postDelayed(() -> {
                    try {
                        FragmentManager fragManager = myContext.getSupportFragmentManager();
                        fragManager
                                .beginTransaction()
                                .replace(R.id.frameLayoutFirstLogIn2, new FragmentFirstLogin3(currentUser))
                                .commit();

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }, 300);
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
        });

    }

    private void activateNextButton(){
        if(isImageValid(uriImage) && isAboutMeValid(editTextAboutMe.getText().toString().trim())){
            buttonNext.setBackgroundColor(getResources().getColor(R.color.purple_500));
            buttonNext.setTextColor(getResources().getColor(R.color.white));
        }else {
            buttonNext.setBackgroundColor(getResources().getColor(R.color.white));
            buttonNext.setTextColor(getResources().getColor(R.color.light_blue));
        }
    }

    private boolean isImageValid(Uri uriImage){
        if (uriImage!=null)
            return true;
        return false;
    }

    private boolean isAboutMeValid(String aboutMe){
        if(!aboutMe.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    private void animateNext(){
        ScaleAnimation scaleAnimation=new ScaleAnimation(increaseScale,original,increaseScale,increaseScale);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(timeNext);
        linearLayoutAnimate.startAnimation(scaleAnimation);

        linearLayoutAnimate.animate().translationX(300).setDuration(timeNext).setStartDelay(100).start();

        buttonNext.setAlpha(alpha);
        buttonSkip.setAlpha(alpha);
        backgroundTop.animate().translationY(-300).alpha(0).setDuration(timeNext).start();
        backgroundBottom.animate().translationY(300).alpha(0).setDuration(timeNext).start();
    }

    private void animateBackGround(){
        linearLayoutAnimate.setTranslationX(300);
        ScaleAnimation scaleAnimation = new ScaleAnimation(original, increaseScale, increaseScale, increaseScale);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(timeThis);
        linearLayoutAnimate.startAnimation(scaleAnimation);
        linearLayoutAnimate.animate().translationX(0).translationY(0).setDuration(timeThis).setStartDelay(100).start();

        backgroundBottom.setTranslationY(300);
        backgroundBottom.setAlpha(alpha);
        backgroundTop.setTranslationY(-300);
        backgroundTop.setAlpha(alpha);
        buttonNext.setAlpha(alpha);
        buttonSkip.setAlpha(alpha);

        backgroundTop.animate().translationY(0).alpha(1).setDuration(timeThis).setStartDelay(100).start();
        backgroundBottom.animate().translationY(0).alpha(1).setDuration(timeThis).setStartDelay(100).start();
        buttonNext.animate().alpha(1).setStartDelay(600).setDuration(timeThis).start();
        buttonSkip.animate().alpha(1).setStartDelay(600).setDuration(timeThis).start();
    }

    public Bitmap reduceBitmapSize(Bitmap bitmap,int MAX_SIZE) {
        double ratioSquare;
        int bitmapHeight, bitmapWidth;
        bitmapHeight = bitmap.getHeight();
        bitmapWidth = bitmap.getWidth();
        ratioSquare = (bitmapHeight * bitmapWidth) / MAX_SIZE;
        if (ratioSquare <= 1)
            return bitmap;
        double ratio = Math.sqrt(ratioSquare);
        int requiredHeight = (int) Math.round(bitmapHeight / ratio);
        int requiredWidth = (int) Math.round(bitmapWidth / ratio);
        return Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, true);
    }
}