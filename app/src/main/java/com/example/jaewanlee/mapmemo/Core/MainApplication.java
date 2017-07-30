package com.example.jaewanlee.mapmemo.Core;

import android.app.Application;

import com.tsengvn.typekit.Typekit;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"BMHANNA_11rs_otf.otf"));
    }
}
