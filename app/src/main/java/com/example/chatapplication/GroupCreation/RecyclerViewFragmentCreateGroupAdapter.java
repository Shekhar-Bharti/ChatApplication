package com.example.chatapplication.GroupCreation;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.chatapplication.AddFriend.RecyclerViewFriendSearchAdapter;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.ArrayList;

public class RecyclerViewFragmentCreateGroupAdapter extends RecyclerView.Adapter<RecyclerViewFragmentCreateGroupAdapter.ViewHolder> {
    ArrayList<Users> result;
    Users currentUser;
    Context context;
    UpdateFromAdapterFragment updateFromAdapter;

    public RecyclerViewFragmentCreateGroupAdapter(ArrayList<Users> result, Users currentUser, UpdateFromAdapterFragment updateFromAdapter, Context context) {
        this.result = result;
        this.currentUser = currentUser;
        this.updateFromAdapter=updateFromAdapter;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewFragmentCreateGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sample_create_group_added,parent,false);
        return new RecyclerViewFragmentCreateGroupAdapter.ViewHolder(view,updateFromAdapter);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewFragmentCreateGroupAdapter.ViewHolder holder, int position) {
        Users user=result.get(position);

        holder.name.setText(user.getUserName());
        setIcons(holder);

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(user.getDpLowLocation());
        try{
            setProfilePic(storageReference,holder);
        }catch(Exception e){
            holder.profilePic.setImageResource(R.drawable.avatar);
        }

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discardFromGroup(user,holder);
            }
        });
    }

    private void setIcons(ViewHolder holder){
        holder.removeButton.setImageResource(R.drawable.ic_minus);
    }

    private void discardFromGroup(Users user, ViewHolder holder){
        try{
            holder.updateFromAdapter.discardMember(user);
        }catch (Exception e){
            Log.d("CreateGroupAdapter","Error: "+e.getMessage());
        }
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

    void setProfilePic(StorageReference storageReference, ViewHolder holder) throws Exception{

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String imageURL = uri.toString();
                Glide.with(context)
                        .load(imageURL)
                        .placeholder(R.drawable.avatar)
                        .into(holder.profilePic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.profilePic.setImageResource(R.drawable.avatar);
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView profilePic, removeButton;
        UpdateFromAdapterFragment updateFromAdapter;

        public ViewHolder(@NonNull View itemView,UpdateFromAdapterFragment updateFromAdapter) {
            super(itemView);
            this.updateFromAdapter=updateFromAdapter;
            name = itemView.findViewById(R.id.textViewUserNameCreateGroupAdded);
            profilePic = itemView.findViewById(R.id.profilePicOfUserCreateGroupAdded);
            removeButton= itemView.findViewById(R.id.imageButtonCreateGroupAdded);
        }
    }

    interface UpdateFromAdapterFragment{
        void discardMember(Users user);
    }
}
