package com.example.chatapplication.Adapters;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.Models.MessageModel;
import com.example.chatapplication.Models.Users;
import com.example.chatapplication.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecyclerViewChatsAdapter extends RecyclerView.Adapter {

    ArrayList<MessageModel> messages;
Users myprofile;
Activity context;
int OUTGOING_VIEW_TYPE=1;
int INTCOMING_VIEW_TYPE=2;


    public RecyclerViewChatsAdapter(ArrayList<MessageModel> messages, Users myprofile, Activity context) {
        this.messages = messages;
        this.myprofile = myprofile;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        if(viewType==OUTGOING_VIEW_TYPE)
        {
            View view = layoutInflater.inflate(R.layout.outgoing_chat_bubble,parent,false);
            return new OutgoingViewHolder(view);
        }
        else
        {

            View view = layoutInflater.inflate(R.layout.incoming_chat_bubble,parent,false);
            return new IncomingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel messageModel = messages.get(position);

        // formatting time
        Date date = new Date(messageModel.getTimeStamp());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("hh:mm a");
        String time = simpleDateFormat.format(date);

        if(holder.getClass()==OutgoingViewHolder.class)
        {
            ((OutgoingViewHolder) holder).outgoingMessage.setText(messageModel.getMessage());
            ((OutgoingViewHolder) holder).outgoingTime.setText(time);
            if(messageModel.getSeen()==true)
            ((OutgoingViewHolder) holder).isSeen.setColorFilter(Color.argb(255, 255, 255, 255));
            else
                ((OutgoingViewHolder) holder).isSeen.setColorFilter(Color.argb(0, 0, 0, 255));




        }
        else {
            ((IncomingViewHolder) holder).incomingMessage.setText(messageModel.getMessage());
            ((IncomingViewHolder)holder).incomingTime.setText(time);
        }



    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        if(messages.get(position).getSenderId().equals(myprofile.getUserId()))
            return OUTGOING_VIEW_TYPE;
        else
            return INTCOMING_VIEW_TYPE;
    }
    public class OutgoingViewHolder extends RecyclerView.ViewHolder
    {
        TextView outgoingMessage,outgoingTime;
        ImageView isSeen;
        public OutgoingViewHolder(@NonNull View itemView) {
            super(itemView);
            outgoingMessage= itemView.findViewById(R.id.outgoing_message);
            outgoingTime=itemView.findViewById(R.id.outgoing_time);
            isSeen=itemView.findViewById(R.id.isSeen);
        }
    }
    public class IncomingViewHolder extends RecyclerView.ViewHolder
    {
        TextView incomingMessage,incomingTime;
        public IncomingViewHolder(@NonNull View itemView) {
            super(itemView);
            incomingMessage= itemView.findViewById(R.id.incoming_message);
            incomingTime=itemView.findViewById(R.id.incoming_time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
