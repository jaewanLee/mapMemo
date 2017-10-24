package product.dp.io.mapmo.Core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.kakao.auth.KakaoSDK;

import io.airbridge.AirBridge;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import product.dp.io.mapmo.Database.UserDatabase;
import product.dp.io.mapmo.Map.KeywordSearchInterface;
import product.dp.io.mapmo.Util.KakaoSDKAdapter;
import product.dp.io.mapmo.Util.Logger;
import product.dp.io.mapmo.Util.TranscHash;
import saschpe.android.customtabs.CustomTabsActivityLifecycleCallbacks;

import static product.dp.io.mapmo.Util.Constant.USER_SHARED;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    private static volatile MainApplication instance = null;
    private static volatile Activity currentActivity = null;

    public SharedPreferences sharedPreferences;
    public UserDatabase onUserDatabase;
    private KeywordSearchInterface keywordSearchInterface;
    Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

//        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "BMHANNA_11yrs_otf.otf"));
        AirBridge.init(this, "ablog", "38acf1efa9fc4f0987173f5a76516eb1");
        AirBridge.setDebugMode(true);

        //키워드 중심 검색
        keywordSearchInterface = KeywordSearchInterface.retrofit.create(KeywordSearchInterface.class);

        //CategoryHash init
        TranscHash.init();

//        Realm.init(this);
        initRealm();
        userDataInit();

        KakaoSDK.init(new KakaoSDKAdapter());


        // Preload CUstom Tabs Service for Improved Perfomance
        registerActivityLifecycleCallbacks(new CustomTabsActivityLifecycleCallbacks());

    }


    // Realm Object 초기화
    public void initRealm() {
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

    }

    public void userDataInit() {
        sharedPreferences = getSharedPreferences(USER_SHARED, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isAutoLogin", false)) {
            onUserDatabase = new UserDatabase();
            onUserDatabase.setUser_email("guest");
        } else {
            String user_email = sharedPreferences.getString("user_email", "");
            if (!user_email.equals("")) {
                this.realm = Realm.getDefaultInstance();
                RealmResults<UserDatabase> userDatabases = realm.where(UserDatabase.class).equalTo("user_email", user_email).findAll();
                onUserDatabase = userDatabases.first();
            } else {
                onUserDatabase = new UserDatabase();
                onUserDatabase.setUser_email("guest");
            }

        }
        Logger.d("Login User : "+onUserDatabase.getUser_email());
    }

    public static Context getContext() {
        return getContext();
    }

    // singleton application object 획득이 목적
    public static MainApplication getMainApplicationContext() {
        if (null == instance) {
            throw new IllegalStateException("state error");
        }

        return instance;

    }

    public static void setCurrentActivity(Activity activity) {
        MainApplication.currentActivity = currentActivity;
    }


    public KeywordSearchInterface getKeywordSearchInterface() {
        return this.keywordSearchInterface;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        instance = null;
    }

    public UserDatabase getOnUserDatabase() {
        return onUserDatabase;
    }

    public void setOnUserDatabase(UserDatabase onUserDatabase) {
        Logger.d("Login User change: "+this.onUserDatabase.getUser_email()+"-> "+onUserDatabase.getUser_email());
        this.onUserDatabase = onUserDatabase;

    }
}
