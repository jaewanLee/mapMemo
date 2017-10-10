package com.example.jaewanlee.mapmemo.Database;

import android.support.annotation.Nullable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jaewanlee on 2017. 7. 30..
 */

public class UserDatabase extends RealmObject {

    @PrimaryKey
    String userNo;
    String userId;
    String userOriginalImg;
    String userThumnailImg;
    String userName;
    String userNickName;
    String userEmail;
    String userSingUpDate;
    String userRegisterType;
    String userLastSesionDate;
    String userPushAgreement;

    @Nullable
    String userSex;
    @Nullable
    String userAge;

    @Nullable
    public String getUserAge() {
        return userAge;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserLastSesionDate() {
        return userLastSesionDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public String getUserNo() {
        return userNo;
    }


    public String getUserPushAgreement() {
        return userPushAgreement;
    }

    public String getUserRegisterType() {
        return userRegisterType;
    }

    @Nullable
    public String getUserSex() {
        return userSex;
    }

    public String getUserSingUpDate() {
        return userSingUpDate;
    }

    public void setUserAge(@Nullable String userAge) {
        this.userAge = userAge;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserLastSesionDate(String userLastSesionDate) {
        this.userLastSesionDate = userLastSesionDate;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }


    public void setUserPushAgreement(String userPushAgreement) {
        this.userPushAgreement = userPushAgreement;
    }

    public void setUserRegisterType(String userRegisterType) {
        this.userRegisterType = userRegisterType;
    }

    public void setUserSex(@Nullable String userSex) {
        this.userSex = userSex;
    }

    public void setUserSingUpDate(String userSingUpDate) {
        this.userSingUpDate = userSingUpDate;
    }

    public String getUserOriginalImg() {
        return userOriginalImg;
    }

    public void setUserOriginalImg(String userOriginalImg) {
        this.userOriginalImg = userOriginalImg;
    }

    public String getUserThumnailImg() {
        return userThumnailImg;
    }

    public void setUserThumnailImg(String userThumnailImg) {
        this.userThumnailImg = userThumnailImg;
    }
}
