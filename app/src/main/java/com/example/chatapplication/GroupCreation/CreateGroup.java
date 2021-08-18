package com.example.chatapplication.GroupCreation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.chatapplication.AddFriend.RecyclerViewFriendSearchAdapter;
import com.example.chatapplication.GroupChatDetailActivity;
import com.example.chatapplication.Models.Groups;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.Settings.FragmentSettingsUpdate;
import com.example.chatapplication.UserActivity;
import com.example.chatapplication.detailsFirstTime.FragmentFirstLogin3;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CreateGroup extends AppCompatActivity implements FragmentCreateGroup.UpdateFromFragment, RecyclerViewFragmentCreateGroupAdapter.UpdateFromAdapterFragment, RecyclerViewCreateGroupAdapter.UpdateFromAdapterMain {
    FrameLayout frameLayout;
    ConstraintLayout constraintLayout;
    LinearLayout linearLayout;

    EditText editTextSearch;
    RecyclerView recyclerViewMain;
    RecyclerViewCreateGroupAdapter adapter;
    final String TAG_FRAGMENT="FragmentCreateGroup";
    float FRAME_LEN=0, childSize=0, child2Size=0;
    final int DOWN=1, UP=0;
    int currentStateOfFragment;
    FirebaseFirestore database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Users currentUser;
    ArrayList<Users> searchedList, addedList, allUsersList;
    Pattern patternFirstName, patternSurName, patternEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        try {
            getSupportActionBar().hide();
        }catch (Exception e){
            //
        }

        editTextSearch= findViewById(R.id.editTextSearchCreateGroup);
        recyclerViewMain=findViewById(R.id.recyclerViewCreateGroup);
        frameLayout= findViewById(R.id.frameLayoutCreateGroup);

        constraintLayout= findViewById(R.id.constraintLayoutCreateGroup);
        linearLayout= findViewById(R.id.linearLayoutCreateGroup);

        searchedList=new ArrayList<>();
        addedList=new ArrayList<>();
        allUsersList=new ArrayList<>();
        currentUser= (Users)getIntent().getSerializableExtra("myProfile");

        database= FirebaseFirestore.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        getFriendDocuments();

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onSearch(editTextSearch.getText().toString().trim());
            }
        });

        adapter = new RecyclerViewCreateGroupAdapter(searchedList, addedList, currentUser,this, this);
        recyclerViewMain.setAdapter(adapter);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));

        setFrame();
    }

    private void getFriendDocuments(){
        if(currentUser.getAllFriendsTillNow()!=null && currentUser.getAllFriendsTillNow().size()!=0) {
            database.collection("Users").whereIn("userId", currentUser.getAllFriendsTillNow()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (QueryDocumentSnapshot document : value) {
                        allUsersList.add(document.toObject(Users.class));
                    }
                    onSearch(null);
                    Log.d("taskGetAllUsersList", allUsersList.size() + "");
                }
            });
        }
    }

    private void setFrame(){
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutCreateGroup, new FragmentCreateGroup(addedList, currentUser, this, this, this), TAG_FRAGMENT)
                    .addToBackStack(TAG_FRAGMENT)
                    .commit();
        }catch (Exception e){
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void onSearch(String pattern){
        searchedList.clear();
        if(pattern!=null && pattern.length()!=0) {
            patternFirstName = Pattern.compile(pattern + ".*");
            patternSurName = Pattern.compile(".* " + pattern);
            patternEmail = Pattern.compile(".*" + pattern + ".*@.*");
            for (Users user : allUsersList) {
                if (patternFirstName.matcher(user.getUserName()).matches()
                        || patternSurName.matcher(user.getUserName()).matches()
                        || patternEmail.matcher(user.getEmailId()).matches()) {
                    searchedList.add(user);
                }
            }
        }else{
            for(Users user: allUsersList) {
                searchedList.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addMember(Users user) {
        addedList.add(user);
        animateToX();
        FragmentCreateGroup fragment;
        fragment = (FragmentCreateGroup) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        fragment.addUser(user);
    }

    @Override
    public void discardMemberMain(Users user) {
        addedList.remove(user);
        animateToX();
        FragmentCreateGroup fragment;
        fragment = (FragmentCreateGroup) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        fragment.removeUser(user);
    }

    @Override
    public void discardMember(Users user) {
        addedList.remove(user);
        animateToX();
        FragmentCreateGroup fragment;
        fragment = (FragmentCreateGroup) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        fragment.removeUser(user);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void dropDown(Integer state) {
        //fragment drop
        currentStateOfFragment=state;
        animateToX();
    }

    private void animateToZero(){
        frameLayout.animate().translationY(0).start();
        linearLayout.animate().translationY(0).start();
    }

    private void animateToX(){
        if(addedList.isEmpty() || currentStateOfFragment==UP){
            animateToZero();
            return;
        }

        if(FRAME_LEN==0) {
            FRAME_LEN =(float) frameLayout.getHeight()-(constraintLayout.getHeight() - linearLayout.getHeight());
            childSize = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 25, this.getResources().getDisplayMetrics() );
            child2Size = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 15, this.getResources().getDisplayMetrics() );
        }

        float len = (float) FRAME_LEN +(addedList.size() - 1) * childSize;
        frameLayout.animate().translationY(len).start();
        linearLayout.animate().translationY(len+child2Size).start();
    }

    @Override
    public void createGroup(Uri uriImage, String groupName) {
        String title, message, posString, negString;
        title="Create Group";
        posString="Create";
        negString="Cancel";
        try {
            createGroupVerify(title,posString,negString,this, uriImage, groupName).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public AlertDialog.Builder createGroupVerify(String title, String posString, String negString, Context context, Uri uriImage,String groupName) throws Exception{
        final AlertDialog.Builder alertDialogVerification= new AlertDialog.Builder(context);

        alertDialogVerification.setTitle(title)
                .setPositiveButton(posString, (dialog, which) -> {
                    //creation
                    String id = database.collection("ChatRooms").document().getId();
                    Groups group= setUpGroup(id,groupName);
                    if(uriImage!=null){
                        try {
                            uploadImages(uriImage,group);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error: tiuu" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        nextActivity(group);
                    }
                }).setNegativeButton(negString, (dialog, which) -> {
                    //nothing
                });
        return alertDialogVerification;
    }

    private Groups setUpGroup(String id,String groupName){
        database.collection("Users").document(currentUser.getUserId()).update("groups",FieldValue.arrayUnion(id));
        ArrayList<String> mem= new ArrayList<>();
        for(Users user: addedList){
            mem.add(user.getUserId());
            database.collection("Users").document(user.getUserId()).update("groups",FieldValue.arrayUnion(id));
        }
        Groups group= new Groups(id,groupName,mem);
        database.collection("Groups").document(id).set(group);
        return group;
    }

    private void uploadImages(Uri uriImage,Groups group) throws Exception{
        StorageReference referenceHigh = null, referenceLow = null, referenceMid = null;
        referenceHigh = storageReference.child(group.getDpHighLocation());
        referenceLow = storageReference.child(group.getDpLowLocation());
        referenceMid = storageReference.child(group.getDpMidLocation());
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImage);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        final ProgressDialog progressDialog = new ProgressDialog(CreateGroup.this);
        progressDialog.setTitle("Setting Up the Group.....");
        progressDialog.show();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        referenceLow.putFile(getImageUri(reduceBitmapSize(bitmap, 19200)));
        referenceMid.putFile(getImageUri(reduceBitmapSize(bitmap, 1920000)));

        referenceHigh.putFile(uriImage).addOnFailureListener(exception -> {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Toast.makeText(CreateGroup.this, "Error in Uploading files " + exception.getMessage(), Toast.LENGTH_LONG).show();
        }).addOnSuccessListener(taskSnapshot -> {
            progressDialog.dismiss();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            nextActivity(group);
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
        });

    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, currentUser.getUserId(), null);
        return Uri.parse(path);
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

    @Override
    public void onBackPressed() {
        if(currentStateOfFragment==DOWN){
            currentStateOfFragment=UP;
            FragmentCreateGroup fragment;
            fragment = (FragmentCreateGroup) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            fragment.setState(UP);
            animateToZero();
        }else{
            String title="Are You Sure?",message="Go Back To Main Menu?", posString="Leave", negString="Stay";
            final AlertDialog.Builder alertDialogVerification= new AlertDialog.Builder(CreateGroup.this);
            alertDialogVerification.setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(posString, (dialog, which) -> {
                        super.onBackPressed();
                        super.onBackPressed();
                    }).setNegativeButton(negString, (dialog, which) -> {
                //nothing
            }).show();
        }
    }

    private void nextActivity(Groups group){
        Intent intent= new Intent(getApplicationContext(), GroupChatDetailActivity.class);
        intent.putExtra("group",(Serializable) group);
        intent.putExtra("myProfile",(Serializable)currentUser);
        startActivity(intent);
        finish();
    }

}