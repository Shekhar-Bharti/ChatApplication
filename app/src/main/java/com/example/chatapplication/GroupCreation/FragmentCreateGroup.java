package com.example.chatapplication.GroupCreation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentCreateGroup extends Fragment {
    RecyclerView recyclerView;
    RecyclerViewFragmentCreateGroupAdapter adapter;
    ImageButton imageButtonDropDown;
    Button buttonCreate;
    CircleImageView imageViewProfilePic;
    TextView textViewAddParticipants;
    EditText editTextGroupName;
    Uri uriImage;

    int currentStateOfFragment;
    final int DOWN=1, UP=0;
    private final int RESULT_CODE=103, RESULT_OK=-1;

    ArrayList<Users> result;
    Users currentUser;
    Context context;
    RecyclerViewFragmentCreateGroupAdapter.UpdateFromAdapterFragment updateFromAdapter;

    UpdateFromFragment updateFromFragment;

    public FragmentCreateGroup() {
    }

    public FragmentCreateGroup(ArrayList<Users> result, Users currentUser, Context context,
                               RecyclerViewFragmentCreateGroupAdapter.UpdateFromAdapterFragment updateFromAdapter,
                               UpdateFromFragment updateFromFragment) {
        this.result = result;
        this.currentUser = currentUser;
        this.context = context;
        this.updateFromAdapter = updateFromAdapter;
        this.updateFromFragment=updateFromFragment;
        this.currentStateOfFragment=UP;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_group,container,false);

        recyclerView = view.findViewById(R.id.recyclerViewFragmentCreateGroup);
        imageButtonDropDown= view.findViewById(R.id.imageButtonDropDownFragmentCreateGroup);
        imageViewProfilePic= view.findViewById(R.id.circleImageViewFragmentCreateGroup);
        textViewAddParticipants=view.findViewById(R.id.textViewFragmentCreateGroupAddParticipant);
        buttonCreate=view.findViewById(R.id.buttonFragmentCreateGroup);
        editTextGroupName= view.findViewById(R.id.editTextFragmentCreateGroupName);

        textViewAddParticipants.setText("Add Participants");
        imageButtonDropDown.setImageResource(R.drawable.ic_navigate_next);
        uriImage=null;
        setStateEmpty();

        adapter = new RecyclerViewFragmentCreateGroupAdapter(result, currentUser,updateFromAdapter, context);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.scrollToPosition(adapter.getItemCount()-1);

        imageButtonDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragmentState();
            }
        });

        imageViewProfilePic.setOnClickListener(v -> changeImage());
        buttonCreate.setOnClickListener(v -> createGroupFragment());

        return view;
    }

    private void createGroupFragment(){
        if(editTextGroupName.getText().toString().trim().isEmpty()){
            editTextGroupName.setError("Group Name is Required");
            return;
        }
        //create
        updateFromFragment.createGroup(uriImage,editTextGroupName.getText().toString().trim());
    }

    private void setStateEmpty() {
        if (result.size()==0){
            imageButtonDropDown.setVisibility(View.INVISIBLE);
            textViewAddParticipants.setVisibility(View.VISIBLE);

        }else{
            imageButtonDropDown.setVisibility(View.VISIBLE);
            textViewAddParticipants.setVisibility(View.INVISIBLE);
        }
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
        }
    }

    private void setFragmentState(){
        if(result.isEmpty()){
            currentStateOfFragment=DOWN;
        }
        switch(currentStateOfFragment){
            case UP:
                currentStateOfFragment= DOWN;
                break;

            default:
                currentStateOfFragment= UP;
        }
        updateFromFragment.dropDown(currentStateOfFragment);
        animate();
    }

    private void animate(){
        float angle;
        switch(currentStateOfFragment){
            case UP:
                angle=(float)0;
                break;

            default:
                angle=(float)-90;
        }
        imageButtonDropDown.animate().rotation(angle).start();
    }

    public void removeUser(Users user){
        adapter.notifyDataSetChanged();
        setStateEmpty();
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }

    public void addUser(Users user){
        adapter.notifyDataSetChanged();
        setStateEmpty();
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
    }

    public void setState(int state){
        currentStateOfFragment=state;
        animate();
    }

    interface UpdateFromFragment{
        void dropDown(Integer state);
        void createGroup(Uri uriImage, String groupName);
    }
}
