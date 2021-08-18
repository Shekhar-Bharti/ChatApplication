package com.example.chatapplication.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class Users implements Serializable {
    String userName;
    String userId, emailId;
    String bio, gender,birthDate;
    ArrayList<String> friends, groups, friendRequests, requestSend, allFriendsTillNow;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Users() {
    }

    public static Users getInstance()
    {
        ArrayList<String> friends = new ArrayList<>();
        ArrayList<String> groups = new ArrayList<>();
        ArrayList<String> friendRequests = new ArrayList<>();
        ArrayList<String> requestSend = new ArrayList<>();
        ArrayList<String> allFriendsTillNow = new ArrayList<>();
        return new Users(friends,groups,friendRequests,requestSend,allFriendsTillNow);
    }

    public Users(ArrayList<String> friends, ArrayList<String> groups, ArrayList<String> friendRequests, ArrayList<String> requestSend, ArrayList<String> allFriendsTillNow) {
        this.friends = friends;
        this.groups = groups;
        this.friendRequests = friendRequests;
        this.requestSend = requestSend;
        this.allFriendsTillNow = allFriendsTillNow;
    }

    public Users(String userName, String userId, String emailId, String bio, String gender, String birthDate, ArrayList<String> friends, ArrayList<String> groups, ArrayList<String> friendRequests, ArrayList<String> requestSend, ArrayList<String> allFriendsTillNow) {
        this.userName = userName;
        this.userId = userId;
        this.emailId = emailId;
        this.bio = bio;
        this.gender = gender;
        this.birthDate = birthDate;
        this.friends = friends;
        this.groups = groups;
        this.friendRequests = friendRequests;
        this.requestSend = requestSend;
        this.allFriendsTillNow = allFriendsTillNow;
    }



    public ArrayList<String> getAllFriendsTillNow() {
        return allFriendsTillNow;
    }

    public void setAllFriendsTillNow(ArrayList<String> allFriendsTillNow) {
        this.allFriendsTillNow = allFriendsTillNow;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ArrayList<String> getFriendRequests() {
        if(this.friendRequests == null) this.friendRequests = new ArrayList<>();
        return friendRequests;
    }

    public void setFriendRequests(ArrayList<String> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public ArrayList<String> getRequestSend() {
        if(requestSend==null){
            requestSend=new ArrayList<>();
        }
        return requestSend;
    }

    public void setRequestSend(ArrayList<String> requestSend) {
        this.requestSend = requestSend;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public ArrayList<String> getFriends() {
        if(friends==null){
            friends=new ArrayList<>();
        }
        return friends;
    }

    public void setFriends(ArrayList<String> friends) {
        this.friends = friends;
    }

    public ArrayList<String> getGroups() {
        if(groups==null){
            groups=new ArrayList<>();
        }
        return groups;
    }

    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    public String getDpLowLocation() {
        return "dp/"+userId+"/dpLow";
    }

    public String getDpHighLocation() {
        return "dp/"+userId+"/dpHigh";
    }

    public String getDpMidLocation() {
        return "dp/"+userId+"/dpMid";
    }
}
