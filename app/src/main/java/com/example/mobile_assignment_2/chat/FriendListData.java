package com.example.mobile_assignment_2.chat;

/**
 * @author: Yao-Wen Chang
 * @description: This class stores the attributes of each friend, and defines the getter and setter
 * methods
 */
public class FriendListData {
    private String UID;
    private String username;
    private String remark;
    private String imgURL;


    public FriendListData(String _UID, String _username, String _remark, String _imgURL) {
        this.UID = _UID;
        this.username = _username;
        this.remark = _remark;
        this.imgURL = _imgURL;
    }

    // Following is Get/Set function
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String _username) {
        this.username = _username;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String _remark) {
        this.remark = _remark;
    }

    public String getImgURL() {
        return this.imgURL;
    }

    public void setImgURL(String _imgURL) {
        this.imgURL = _imgURL;
    }

    public String getUID() {
        return this.UID;
    }

    public void setUID(String _UID) {
        this.UID = _UID;
    }


}
