package com.example.chatapplication.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Groups implements Serializable {
    String groupId;
    String groupName;
    ArrayList<String> members;

    public Groups() {
    }

    public Groups(String groupId, String groupName, ArrayList<String> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.members = members;
    }

    public String getDpLowLocation() {
        return "dpGroup/"+groupId+"/dpLow";
    }

    public String getDpHighLocation() {
        return "dpGroup/"+groupId+"/dpHigh";
    }

    public String getDpMidLocation() {
        return "dpGroup/"+groupId+"/dpMid";
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }
}
