package product.dp.io.ab180blog.Core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kakao.auth.KakaoSDK;
import com.tsengvn.typekit.Typekit;

import io.airbridge.AirBridge;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import product.dp.io.ab180blog.Map.KeywordSearchInterface;
import product.dp.io.ab180blog.Util.KakaoSDKAdapter;
import product.dp.io.ab180blog.Util.TranscHash;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    private static volatile MainApplication instance = null;
    private static volatile Activity currentActivity = null;

    Realm UserData;
    public SharedPreferences sharedPreferences;
    private KeywordSearchInterface keywordSearchInterface;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Typekit.getInstance().addNormal(Typekit.createFromAsset(this,"BMHANNA_11yrs_otf.otf"));
        AirBridge.init(this, "ablog", "38acf1efa9fc4f0987173f5a76516eb1");
        AirBridge.setDebugMode(true);


        //키워드 중심 검색
        keywordSearchInterface=KeywordSearchInterface.retrofit.create(KeywordSearchInterface.class);

        //CategoryHash init
        TranscHash.init();

//        Realm.init(this);
        initRealm();
        userDataInit();


        KakaoSDK.init(new KakaoSDKAdapter());

    }


    // Realm Object 초기화
    public void initRealm()  {
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    public void userDataInit(){
//        this.UserData=Realm.getInstance();
//        RealmResults<UserDatabase> userDatabases=this.UserData.where(UserDatabase.class).findAll();
    }

    public static Context getContext(){
        return getContext();
    }

    // singleton application object 획득이 목적
    public static MainApplication getMainApplicationContext() {
        if(null == instance ) {
            throw new IllegalStateException("state error");
        }

        return instance;

    }

    public static void setCurrentActivity(Activity activity) {
        MainApplication.currentActivity = currentActivity;
    }



    public KeywordSearchInterface getKeywordSearchInterface(){
        return this.keywordSearchInterface;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        instance = null;
    }
}
