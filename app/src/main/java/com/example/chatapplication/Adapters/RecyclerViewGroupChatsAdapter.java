package com.example.chatapplication.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.MessageModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecyclerViewGroupChatsAdapter extends RecyclerView.Adapter {
    ArrayList<MessageModel> messages;
    ArrayList<Users> groupMembers;
    Map<String,String> mapOfMembers;
    Users myProfile;
    Activity context;
    int OUTGOING_VIEW_TYPE=1;
    int INTCOMING_VIEW_TYPE=2;

    public RecyclerViewGroupChatsAdapter(ArrayList<MessageModel> messages, Users myProfile,  ArrayList<Users> groupMembers,Activity context) {
        this.messages = messages;
        this.groupMembers = groupMembers;
        this.myProfile = myProfile;
        this.context = context;
        mapOfMembers= new HashMap<>();
        for(int i=0;i<groupMembers.size();i++)
        {
            mapOfMembers.put(groupMembers.get(i).getUserId(),groupMembers.get(i).getUserName());
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        if(viewType==OUTGOING_VIEW_TYPE)
        {
            View view = layoutInflater.inflate(R.layout.outgoing_chat_bubble_group,parent,false);
            return new RecyclerViewGroupChatsAdapter.OutgoingViewHolder(view);
        }
        else
        {

            View view = layoutInflater.inflate(R.layout.incoming_chat_bubble_group,parent,false);
            return new RecyclerViewGroupChatsAdapter.IncomingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messages.get(position);

        // formatting time
        Date date = new Date(messageModel.getTimeStamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("hh:mm a");
        String time = simpleDateFormat.format(date);

        if(holder.getClass()== RecyclerViewGroupChatsAdapter.OutgoingViewHolder.class)
        {
            ((RecyclerViewGroupChatsAdapter.OutgoingViewHolder) holder).outgoingMessage.setText(messageModel.getMessage());
            ((OutgoingViewHolder)holder).outgoingSenderName.setText("You");
            ((OutgoingViewHolder)holder).outgoingTime.setText(time);

            Log.d("task09",messageModel.getMessage());

        }
        else {
            ((RecyclerViewGroupChatsAdapter.IncomingViewHolder) holder).incomingMessage.setText(messageModel.getMessage());
            ((IncomingViewHolder) holder).incomingSenderName.setText(mapOfMembers.get(messageModel.getSenderId()));
            ((IncomingViewHolder) holder).incomingTime.setText(time);
           Log.d("task99",mapOfMembers.get(messageModel.getSenderId()));


        }

    }


    public class OutgoingViewHolder extends RecyclerView.ViewHolder
    {
        TextView outgoingMessage,outgoingTime,outgoingSenderName;
        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            outgoingMessage= itemView.findViewById(R.id.outgoing_message_group);
            outgoingTime=itemView.findViewById(R.id.outgoing_time);
            outgoingSenderName=itemView.findViewById(R.id.senderName_outgoing_group);
        }
    }
    public class IncomingViewHolder extends RecyclerView.ViewHolder
    {
        TextView incomingMessage,incomingTime,incomingSenderName;
        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            incomingMessage= itemView.findViewById(R.id.incoming_message_group);
            incomingTime=itemView.findViewById(R.id.incoming_time_group);
            incomingSenderName=itemView.findViewById(R.id.senderName_incoming_group);

        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().equals(myProfile.getUserId()))
            return OUTGOING_VIEW_TYPE;
        else
            return INTCOMING_VIEW_TYPE;
    }
}
