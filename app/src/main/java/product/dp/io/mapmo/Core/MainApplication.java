package product.dp.io.mapmo.Core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.util.Log;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.appboy.Appboy;
import com.appboy.AppboyLifecycleCallbackListener;
import com.appboy.support.AppboyLogger;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kakao.auth.KakaoSDK;

import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import product.dp.io.mapmo.AddMemoView.LatLngSearchInterface;
import product.dp.io.mapmo.Database.UserDatabase;
import product.dp.io.mapmo.Map.KeywordSearchInterface;
import product.dp.io.mapmo.Util.KakaoSDKAdapter;
import product.dp.io.mapmo.Util.Logger;
import product.dp.io.mapmo.Util.TranscHash;

import static product.dp.io.mapmo.Util.Constant.USER_SHARED;


/**
 * Created by jaewanlee on 2017. 7. 27..
 */

public class MainApplication extends Application {

    private static volatile MainApplication instance = null;
    private static volatile Activity currentActivity = null;
    private static final String AF_DEV_KEY="Q8oW9QqM3NDSixGP4XuuwT";

    public SharedPreferences sharedPreferences;
    public UserDatabase onUserDatabase;
    private KeywordSearchInterface keywordSearchInterface;
    private LatLngSearchInterface latLngSearchInterface;
    Realm realm;
    private FirebaseAnalytics firebaseAnalytics;
    private String user_email;

    InstallReferrerClient mReferrerClient;


    @Override
    public void onCreate() {
        super.onCreate();
        Locale.getDefault().getCountry();

        instance = this;

//        Typekit.getInstance().addNormal(Typekit.createFromAsset(this, "BMHANNA_11yrs_otf.otf"));
//        AirBridge.turnOnIntegration(new AppboyIntegrator());
//        AirBridge.setDebugMode(true);
//        AirBridge.init(this, "mapmo", "b145d75aee4f45609f6c891a709177c0");



        mReferrerClient = InstallReferrerClient.newBuilder(getApplicationContext()).build();


        //키워드 중심 검색
        keywordSearchInterface = KeywordSearchInterface.retrofit.create(KeywordSearchInterface.class);

        //위경도 기반 검색
        latLngSearchInterface=LatLngSearchInterface.retrofit.create(LatLngSearchInterface.class);

        //CategoryHash init
        TranscHash.init();

//        Realm.init(this);
        initRealm();
        userDataInit();

        KakaoSDK.init(new KakaoSDKAdapter());

        firebaseAnalytics=FirebaseAnalytics.getInstance(this);

        setAppsFlyer();

        setAppboy();


        // Preload CUstom Tabs Service for Improved Perfomance
//        registerActivityLifecycleCallbacks(new CustomTabsActivityLifecycleCallbacks());

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
            user_email = sharedPreferences.getString("user_email", "");
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
    public static MainApplication getMainApplicationInstance() {
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
    public LatLngSearchInterface getLatLngSearchInterface(){
        return  this.latLngSearchInterface;
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

    synchronized public FirebaseAnalytics getFirebaseAnalytics(){
        if(firebaseAnalytics==null)
            firebaseAnalytics=FirebaseAnalytics.getInstance(this);
        return firebaseAnalytics;
    }

    private void setAppsFlyer(){
        AppsFlyerConversionListener conversionDataListener=new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {

            }

            @Override
            public void onInstallConversionFailure(String s) {

            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {

            }

            @Override
            public void onAttributionFailure(String s) {

            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY, conversionDataListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);
    }
    public void setAppboy(){
        AppboyLogger.setLogLevel(Log.VERBOSE);
        if(user_email!=null){
            Appboy.getInstance(getApplicationContext()).getCurrentUser().setFirstName(user_email);
        }else{
            Appboy.getInstance(getApplicationContext()).getCurrentUser().setFirstName("guest");
        }
        registerActivityLifecycleCallbacks(new AppboyLifecycleCallbackListener());

    }

    public void getReferrer(){
        if(!mReferrerClient.isReady()){
            mReferrerClient= InstallReferrerClient.newBuilder(getApplicationContext()).build();
        }
        mReferrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        Log.d("Referrer","InstallReferrer is conneted");
                        ReferrerDetails response = null;
                        try {
                            response = mReferrerClient.getInstallReferrer();
                            String installReferrer=response.getInstallReferrer();
                            if(installReferrer==null||installReferrer.equals("")){
                                Log.d("Referrer","insatllReferrelr null or blank");
                            }
                            response.getInstallReferrer();

                            Log.d("Referrer","install Referrer"+response.getInstallReferrer());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app
                        Log.d("Referrer","InstallREferrer is not supported by Playstore");
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        Log.d("Referrer","InstallReferrer can't be established");
                        // Connection could not be established
                        break;
                }
                mReferrerClient.endConnection();
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d("Referrer","InstallReferrer is disconnected");
            }
        });
    }

}
