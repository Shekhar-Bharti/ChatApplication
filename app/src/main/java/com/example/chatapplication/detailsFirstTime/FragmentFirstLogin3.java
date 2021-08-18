package com.example.chatapplication.detailsFirstTime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.AddFriend.RecyclerViewFriendSearchAdapter;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.example.chatapplication.UserActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class FragmentFirstLogin3 extends Fragment {
    RelativeLayout backgroundTop, backgroundBottom;
    LinearLayout linearLayoutAnimate;

    Button buttonNext;
    EditText editTextSearchId;

    Users currentUser;

    FirebaseFirestore firebaseFirestore;

    RecyclerView recyclerViewSendRequest;
    RecyclerViewFriendSearchAdapter adapter;
    Pattern patternFirstName, patternSurName, patternEmail;
    ArrayList<Users> allUsersArrayList, searchedList;

    final float original = (float)0, increaseScale = (float)1;
    final float alpha=0;
    final int timeNext=300, timeThis=500;
    final String TAG= "FirstLoginPage3";

    public FragmentFirstLogin3(Users user){
        //constructor;
        currentUser=user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_first_login3,container,false);

        editTextSearchId=view.findViewById(R.id.editTextFirstlogin3Search);
        buttonNext=view.findViewById(R.id.buttonFirstLogin3Next);

        backgroundTop=view.findViewById(R.id.relativeLayoutTop2);
        backgroundBottom=view.findViewById(R.id.relativeLayoutBottom2);
        linearLayoutAnimate=view.findViewById(R.id.linearLayoutPage3);

        searchedList= new ArrayList<>();
        allUsersArrayList=  new ArrayList<>();
        editTextSearchId.setText("");

        animateBackGround();

        firebaseFirestore=FirebaseFirestore.getInstance();

        getDetails();

        editTextSearchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                onSearch(editTextSearchId.getText().toString().trim());
            }
        });

        recyclerViewSendRequest = view.findViewById(R.id.recyclerViewFirstLogin3);
        adapter = new RecyclerViewFriendSearchAdapter(searchedList, currentUser, firebaseFirestore,getContext());
        recyclerViewSendRequest.setAdapter(adapter);
        recyclerViewSendRequest.setLayoutManager(new LinearLayoutManager(getContext()));

        buttonNext.setOnClickListener(v -> {
            animateNext();
            new Handler().postDelayed(() -> next(), 200);
        });

        return view;
    }

    private void next(){
        startActivity(new Intent(getContext(), UserActivity.class));
    }



    private void getDetails(){
        Query query= firebaseFirestore.collection("Users");
        query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for (QueryDocumentSnapshot document : task.getResult()) {
                    allUsersArrayList.add(document.toObject(Users.class));
                }
            }
            else{
                Log.w(TAG,"failed in getting collection Details");
            }
        });
    }

    private void onSearch(String pattern){
        searchedList.clear();
        if(pattern!=null&&pattern.length()!=0) {
            patternFirstName = Pattern.compile(pattern + ".*");
            patternSurName = Pattern.compile(".* " + pattern);
            patternEmail = Pattern.compile(".*" + pattern + ".*@.*");
            for (Users user : allUsersArrayList) {
                if (!user.getUserId().equals(currentUser.getUserId()) && (patternFirstName.matcher(user.getUserName()).matches()
                        || patternSurName.matcher(user.getUserName()).matches()
                        || patternEmail.matcher(user.getEmailId()).matches()) ) {
                    searchedList.add(user);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void animateNext(){
        ScaleAnimation scaleAnimation=new ScaleAnimation(increaseScale,original,increaseScale,increaseScale);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(timeNext);
        linearLayoutAnimate.startAnimation(scaleAnimation);

        linearLayoutAnimate.animate().translationX(300).setDuration(timeNext).setStartDelay(100).start();

        buttonNext.setAlpha(alpha);
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

        backgroundTop.animate().translationY(0).alpha(1).setDuration(timeThis).setStartDelay(100).start();
        backgroundBottom.animate().translationY(0).alpha(1).setDuration(timeThis).setStartDelay(100).start();
        buttonNext.animate().alpha(1).setStartDelay(600).setDuration(timeThis).start();
    }
}