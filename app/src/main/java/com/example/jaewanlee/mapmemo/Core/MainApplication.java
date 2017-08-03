package com.example.jaewanlee.mapmemo.Core;

import android.app.Application;
import android.content.SharedPreferences;

import com.tsengvn.typekit.Typekit;

import io.realm.Realm;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    Realm UserData;
    SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"BMHANNA_11yrs_otf.otf"));
    }

    public void userDataInit(){
//        this.UserData=Realm.getInstance();
//        RealmResults<UserDatabase> userDatabases=this.UserData.where(UserDatabase.class).findAll();
    }


}
