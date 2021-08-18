package com.example.chatapplication.detailsFirstTime;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class FirstLogIn extends AppCompatActivity {

    TextView textViewDateOfBirth;
    EditText editTextUserName;
    ImageView imageViewMale, imageViewFemale;
    Button buttonNext;

    LinearLayout linearLayoutBirthDate, linearLayoutAnimate;
    RelativeLayout backgroundTop, backgroundBottom;

    final String MALE="male", FEMALE="female", OTHERS="others";
    String gender;
    float alpha=0;

    Users currentUser;
    FirebaseAuth firebaseAuth;


    //info
    class BirthDate{
        int year,month,day;
        String date;
        public BirthDate(int year,int month,int day){
            this.day=day;
            this.month=month;
            this.year=year;
            this.date = day +"/"+ month +"/"+ year;
        }
        public String getDate() {
            return date;
        }
    }

    BirthDate birthDate;

    final float original = (float)0, increaseScale = (float)1;
    final int timeNext=300 , timeThis=500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_log_in);

        linearLayoutAnimate=findViewById(R.id.linearLayoutPage1);
        backgroundTop=findViewById(R.id.relativeLayoutTop);
        backgroundBottom=findViewById(R.id.relativeLayoutBottom);

        linearLayoutBirthDate=findViewById(R.id.linearLayoutViewBirthday);

        textViewDateOfBirth=findViewById(R.id.textViewFirstTimeLoginDateOfBirth);
        editTextUserName=findViewById(R.id.editTextFirstTimeLoginUserName);
        buttonNext=findViewById(R.id.buttonFirstLoginNext);

        imageViewMale=findViewById(R.id.imageViewMale);
        imageViewFemale=findViewById(R.id.imageViewFemale);

        buttonNext.setBackgroundColor(getResources().getColor(R.color.white));
        buttonNext.setTextColor(getResources().getColor(R.color.light_blue));
        gender=OTHERS;

        firebaseAuth=FirebaseAuth.getInstance();
        animateBackGround();

        currentUser= Users.getInstance();
        editTextUserName.setText(firebaseAuth.getCurrentUser().getDisplayName());
        currentUser.setEmailId(firebaseAuth.getCurrentUser().getEmail());
        currentUser.setUserId(firebaseAuth.getCurrentUser().getUid());

        linearLayoutBirthDate.setOnClickListener(v -> birthDate());

        imageViewMale.setOnClickListener(v -> gender(MALE));

        imageViewFemale.setOnClickListener(v -> gender(FEMALE));

        buttonNext.setOnClickListener(v -> {
            nextPage();
        });

        editTextUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){}
            @Override
            public void afterTextChanged(Editable s) {
                activateNextButton();
            }
        });

        textViewDateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                activateNextButton();
            }
        });

    }

    private void nextPage(){
        if(!isValidGender(gender)){
            Snackbar.make(findViewById(android.R.id.content),"Select Your Gender*",Snackbar.LENGTH_LONG).show();
        }else if(!isValidUserName(editTextUserName.getText().toString().trim())){
            editTextUserName.setError("UserName is Required");
        }else if(!isValidDate(birthDate)){
            Snackbar.make(findViewById(android.R.id.content),"Select Your BirthDat8e",Snackbar.LENGTH_LONG).show();
        }else {
            animateNext();
            new Handler().postDelayed(() ->{
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayoutFirstLogIn, new FragmentFirstlogin2(currentUser))
                        .commit();

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }}, 300);
        }
    }

    private void gender(String sex){
        if(sex.equals(MALE)){
            imageViewMale.setBackgroundColor(getResources().getColor(R.color.light_blue));
            imageViewFemale.setBackgroundColor(getResources().getColor(R.color.white));
        }else{
            imageViewMale.setBackgroundColor(getResources().getColor(R.color.white));
            imageViewFemale.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
        gender=sex;
        activateNextButton();
    }

    private void birthDate(){
        Calendar calendar= Calendar.getInstance();
        final int year= calendar.get(Calendar.YEAR);
        final int month= calendar.get(Calendar.MONTH);
        final int dayOfMonth= calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog= new DatePickerDialog(
                FirstLogIn.this, (view, year1, month1, dayOfMonth1) -> {
                    month1++;
                    birthDate=new BirthDate(year1, month1, dayOfMonth1);
                    textViewDateOfBirth.setText(birthDate.getDate());
                },year,month,dayOfMonth);
        datePickerDialog.show();
    }

    private void activateNextButton(){
        if(isValidDate(birthDate) && isValidUserName(editTextUserName.getText().toString().trim()) && isValidGender(gender)) {
            buttonNext.setBackgroundColor(getResources().getColor(R.color.purple_500));
            buttonNext.setTextColor(getResources().getColor(R.color.white));
        }else {
            buttonNext.setBackgroundColor(getResources().getColor(R.color.white));
            buttonNext.setTextColor(getResources().getColor(R.color.light_blue));
        }
    }

    private boolean isValidUserName(String userName){
        if(!userName.isEmpty()) {
            currentUser.setUserName(userName);
            return true;
        }
        return false;
    }

    private boolean isValidDate(BirthDate birthDate){
        if(birthDate!=null && !birthDate.getDate().isEmpty()) {
            currentUser.setBirthDate(birthDate.getDate());
            return true;
        }
        return false;
    }

    private boolean isValidGender(String gender){
        if(!gender.equals(OTHERS)){
            currentUser.setGender(gender);
            return true;
        }
        return false;
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