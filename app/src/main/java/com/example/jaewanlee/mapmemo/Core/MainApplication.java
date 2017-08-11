package com.example.jaewanlee.mapmemo.Core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.jaewanlee.mapmemo.Map.KeywordSearchInterface;
import com.example.jaewanlee.mapmemo.Util.TranscHash;
import com.tsengvn.typekit.Typekit;

import io.airbridge.AirBridge;
import io.realm.Realm;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    Realm UserData;
    SharedPreferences sharedPreferences;
    private KeywordSearchInterface keywordSearchInterface;

    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"BMHANNA_11yrs_otf.otf"));
        AirBridge.init(this, "ablog", "38acf1efa9fc4f0987173f5a76516eb1");
        AirBridge.setDebugMode(true);


        //키워드 중심 검색
        keywordSearchInterface=KeywordSearchInterface.retrofit.create(KeywordSearchInterface.class);

        //CategoryHash init
        TranscHash.init();

        Realm.init(this);
    }

    public void userDataInit(){
//        this.UserData=Realm.getInstance();
//        RealmResults<UserDatabase> userDatabases=this.UserData.where(UserDatabase.class).findAll();
    }

    public static Context getContext(){
        return getContext();
    }
    public KeywordSearchInterface getKeywordSearchInterface(){
        return this.keywordSearchInterface;
    }


}
