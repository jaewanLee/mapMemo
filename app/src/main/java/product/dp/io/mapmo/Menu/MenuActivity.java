package product.dp.io.mapmo.Menu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.airbridge.AirBridge;
import io.airbridge.statistics.events.inapp.SignInEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import product.dp.io.mapmo.Core.MainApplication;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Database.UserDatabase;
import product.dp.io.mapmo.PushMessage.PersistentService;
import product.dp.io.mapmo.PushMessage.RestartService;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Shared.NetworkManager;
import product.dp.io.mapmo.Util.Logger;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;
import static product.dp.io.mapmo.Util.Constant.USER_SHARED;


public class MenuActivity extends AppCompatActivity {

    ImageButton back_ib;

    ImageView userImage;
    TextView userName;
    com.kakao.usermgmt.LoginButton kakaoLogin;
    Button loginFake;
    Button logout_bt;
    RelativeLayout backup_rl;
    Switch lockScreen;
    RelativeLayout format_layout, faq_layout, userterm_layout;
    ImageButton back_bt;

    SharedPreferences sharedPreferences;
    SharedPreferences sharedPreferencesConfig;

    public UserDatabase _user_db = new UserDatabase();
    private UserDatabase mainApplication_userdb;
    private KakaoSessionCallback _kakaoSessionCallback;

    RestartService restartService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.mapmo.R.layout.activity_menu);

        initLayout();

        initFunction();

        socialLogin();

    }

    private void initLayout() {
        sharedPreferencesConfig = getSharedPreferences("Config", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(USER_SHARED, MODE_PRIVATE);

        back_ib = (ImageButton) findViewById(R.id.header_back_button);
        userImage = (ImageView) findViewById(product.dp.io.mapmo.R.id.menu_profile_image);
        userName = (TextView) findViewById(product.dp.io.mapmo.R.id.user_profile_name);
        kakaoLogin = (LoginButton) findViewById(product.dp.io.mapmo.R.id.menu_kakaologin_button);
        loginFake = (Button) findViewById(R.id.menu_kakaologinfake_button);
        logout_bt = (Button) findViewById(R.id.menu_logout_button);

        format_layout = (RelativeLayout) findViewById(R.id.format_set_lay);
        backup_rl = (RelativeLayout) findViewById(R.id.memu_backup_relativelayout);
        faq_layout = (RelativeLayout) findViewById(R.id.faq_set_lay);
        userterm_layout = (RelativeLayout) findViewById(R.id.userterm_set_lay);

        lockScreen = (Switch) findViewById(R.id.switch_memo);
        lockScreen.setChecked(sharedPreferencesConfig.getBoolean("lockScreen", false));

        mainApplication_userdb = MainApplication.getMainApplicationContext().getOnUserDatabase();

    }


    private void initFunction() {
        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferencesConfig.edit();
                if (b) {

                    editor.putBoolean("lockScreen", true);
                    editor.apply();
//                    Intent intent=new Intent(getApplicationContext(), PushActivity.class);
//                    startActivity(intent);
                    restartService = new RestartService();
                    Intent intent = new Intent(getApplicationContext(), PersistentService.class);

                    IntentFilter intentFilter = new IntentFilter("product.dp.io.mapmo.PushMessage.PersistentService");
                    registerReceiver(restartService, intentFilter);
                    startService(intent);
                } else {
                    editor.putBoolean("lockScreen", false);
                    editor.apply();
                    if (restartService != null) {
                        unregisterReceiver(restartService);
                        Intent intent = new Intent(getApplicationContext(), PersistentService.class);
                        stopService (intent);
                    }

                }

            }
        });

        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
//                        Toast.makeText(MenuActivity.this, "성공적으로 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                        kakaoLogin.setClickable(true);
                        logout_bt.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        super.onFailure(errorResult);
                        Logger.d("err code: " + errorResult.getErrorMessage());
                        Toast.makeText(MenuActivity.this, "로그아웃에 실패하였습니다", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onSuccess(Long result) {
                        super.onSuccess(result);

                        kakaoLogin.setClickable(true);
                        kakaoLogin.setVisibility(View.VISIBLE);
                        logout_bt.setVisibility(View.INVISIBLE);

                        Logger.d(String.valueOf("onSuccess result: " + result));

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();
                        UserDatabase guestUser = new UserDatabase();
                        guestUser.setUser_email("guest");
                        MainApplication.getMainApplicationContext().setOnUserDatabase(guestUser);
                        mainApplication_userdb = guestUser;
                        logout_bt.setVisibility(View.INVISIBLE);
                        loginFake.setVisibility(View.VISIBLE);
                        kakaoLogin.setVisibility(View.VISIBLE);

                        userImage.setImageResource(R.drawable.guest_profile);
                        userName.setText("guset");
                    }
                });
                Logger.d("로그아웃 버튼 클릭");
            }
        });

        format_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle("포멧하시겠습니까?");
                builder.setMessage("저장되어 있던 모든 내용이 삭제되고 처음 어플을 설치했을 때로 되돌아 갑니다.");

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Realm realm = Realm.getDefaultInstance();
                        final RealmResults<MemoDatabase> realmResults = realm.where(MemoDatabase.class).findAll();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realmResults.deleteAllFromRealm();
                            }
                        });
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        faq_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 0;

                callToChromeCustomTab(index);
            }
        });


        userterm_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = 1;

                callToChromeCustomTab(index);
            }
        });

        backup_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuActivity.this, "업데이트 준비중입니다", Toast.LENGTH_SHORT).show();
            }
        });

        back_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void socialLogin() {
        String user_id = mainApplication_userdb.getUser_email();
        _kakaoSessionCallback = new KakaoSessionCallback();
        Session.getCurrentSession().addCallback(_kakaoSessionCallback);
        if (!user_id.equals("guest")) {
            Bitmap bitmap = loadImage(user_id);
            if (bitmap != null) {
                Glide.with(this)
                        .load(bitmap)
                        .centerCrop()
                        .bitmapTransform(new CropCircleTransformation(MenuActivity.this))
                        .into(userImage);
            }
            userName.setText(user_id);
            kakaoLogin.setVisibility(View.INVISIBLE);
            loginFake.setVisibility(View.INVISIBLE);
            logout_bt.setVisibility(View.VISIBLE);
            Session.getCurrentSession().checkAndImplicitOpen();
        }
    }

    // 카카오 세션 콜백,앱에 카카오 로그인이 성공한 경우(기존 로그인시에는 checkAndImplicitopen으로 자동 실행
    private class KakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            //세션이 오픈되어 있는 ㅅ아태
            KakaoRequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if (null != exception) {
                Toast.makeText(MenuActivity.this, "네트워크를 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
//                Log.d("sociallogin", exception.toString());
            }

        }
    }

    // 카카오 세션 호출
    protected void KakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

                int _error_code = errorResult.getErrorCode();
                int _network_error_code = -777;

                if (_network_error_code == _error_code) {
                    String _error_message = "카카오톡의 서버 네트워트가 불안정합니다. 다시 시도해 주세요.";
                    Toast.makeText(getApplicationContext(), _error_message, Toast.LENGTH_SHORT).show();

                    return;
                }
                Log.d("Login", errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                Toast.makeText(MenuActivity.this, "nothing", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onSuccess(UserProfile result) {

                setUserDataBase(result);
                kakaoLogin.setClickable(false);
                kakaoLogin.setVisibility(View.INVISIBLE);
                logout_bt.setVisibility(View.VISIBLE);

            }
        });
    }

    protected void setUserDataBase(UserProfile profile) {

        String _user_name = profile.getNickname();
        String _email = profile.getEmail();
        String _thumnail_img_url = profile.getThumbnailImagePath();
        String _original_img_url = profile.getProfileImagePath();

        _user_db.setUser_name(_user_name);
        _user_db.setUser_email(_email);
        _user_db.setUser_image_url(_original_img_url);
        _user_db.setUserThumnailImg(_thumnail_img_url);

        setProfileInfo(_user_name, _email, _original_img_url);
        sendUserProfile(_user_name, _email, _original_img_url);
        setUserDatabase(_user_db);

        AuthService.requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onFailure(ErrorResult errorResult) {
                super.onFailure(errorResult);
                String message = "failed to get access token info. msg=" + errorResult;
                Logger.e(message);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                Logger.d("this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");
            }
        });

    }

    private void setProfileInfo(final String user_name, String email, String image_url) {
        userName.setText(user_name);

        Glide.with(MenuActivity.this)
                .load(image_url)
                .centerCrop()
                .crossFade()
                .bitmapTransform(new CropCircleTransformation(MenuActivity.this))
                .into(userImage);

        Glide.with(this)
                .load(image_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        saveImage(resource, user_name);
                    }
                });

        kakaoLogin.setVisibility(View.INVISIBLE);
        loginFake.setVisibility(View.INVISIBLE);
        logout_bt.setVisibility(View.VISIBLE);

    }

    private void saveImage(Bitmap resource, String user_name) {
        Bitmap user_image = resource;
        try {
            FileOutputStream fos = openFileOutput(user_name + ".png", Context.MODE_PRIVATE);
            user_image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap loadImage(String user_name) {
        String imagepath = "data/data" + "/product.dp.io.mapmo" + "/files/" + user_name + ".png";
        Bitmap user_image = BitmapFactory.decodeFile(imagepath);
        return user_image;
    }

    private void sendUserProfile(String user_name, String email, String image_url) {
        NetworkManager networkManager = NetworkManager.getInstance();
        OkHttpClient client = networkManager.getClient();
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http");
        builder.host("ec2-52-199-177-224.ap-northeast-1.compute.amazonaws.com");
        builder.port(80);
        builder.addPathSegment("mapmo");
        builder.addPathSegment("users");
        builder.addPathSegment("kakao");
        builder.addPathSegment("post.php");
        String user_image_url;
        if(image_url==null||image_url.equals("")){
            user_image_url="null";
        }else{
            user_image_url=image_url;
        }

        //TODO myValue에다가 내 아이디랑 시간 써서 넣기
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("user_email", email)
                .add("user_name", user_name)
                .add("user_image_url", user_image_url);

        RequestBody body = formBuilder.build();

        final Request request = new Request.Builder()
                .url(builder.build())
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Logger.d("User profile uploading err ");
                    }
                });
            }

            @Override
            public void onResponse(okhttp3.Call call, final okhttp3.Response response) throws IOException {
                final String result = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result.contains("200")) {
                            Logger.d("response of send user profile : "+result);
                            Toast.makeText(MenuActivity.this, "유저 프로필이 정상적으로 등록되지 않았습니다", Toast.LENGTH_SHORT).show();
                            AirBridge.getTracker().send(new SignInEvent());
                        } else {
                            Logger.d("response of send user profile : 400 err");
                            Toast.makeText(MenuActivity.this, "유저 프로필 등록에 실패하였습니다 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void setUserDatabase(UserDatabase userDatabase) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserDatabase> saveUserDatabase = realm.where(UserDatabase.class).equalTo("user_email", userDatabase.getUser_email()).findAll();
        if (saveUserDatabase.size() > 0 && !mainApplication_userdb.getUser_email().equals("guest")) {
            //이미 로그인이 되어 있는 경우
            realm.beginTransaction();
            UserDatabase updateUserDatabse = saveUserDatabase.first();
            updateUserDatabse.setUser_image_url(userDatabase.getUser_image_url());
            realm.commitTransaction();

        } else if (saveUserDatabase.size() > 0 && mainApplication_userdb.getUser_email().equals("guest")) {
            //이미 등록되어 있는 사용자가 다시 로그인하는 경우
            realm.beginTransaction();
            UserDatabase updateUserDatabse = saveUserDatabase.first();
            updateUserDatabse.setUser_image_url(userDatabase.getUser_image_url());
            realm.commitTransaction();
        } else {
            //새로운 유저인 경우
            realm.beginTransaction();
            UserDatabase newUserDatabase = realm.copyToRealm(userDatabase);
            realm.commitTransaction();
        }
        //메인 어플리케이션의 UserDatabase를 수정해줌
        MainApplication.getMainApplicationContext().setOnUserDatabase(userDatabase);
        mainApplication_userdb = userDatabase;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAutoLogin", true);
        editor.putString("user_email", userDatabase.getUser_email());
        editor.apply();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(_kakaoSessionCallback);
    }

    private void callToChromeCustomTab(int index) {

        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .enableUrlBarHiding()
                .setShowTitle(false)
                .setToolbarColor(getResources().getColor(R.color.black))
//                .addDefaultShareMenuItem()
//                .setToolbarColor(this.getResources().getColor(R.color.colorPrimary))
//                .setShowTitle(true)
//                .setCloseButtonIcon(backArrow)
                .build();

// This is optional but recommended
        CustomTabsHelper.addKeepAliveExtra(this, customTabsIntent.intent);


        switch (index) {

            case 0:

                CustomTabsHelper.openCustomTab(this, customTabsIntent,
                        Uri.parse("https://goo.gl/vcbHyd"),
                        new WebViewFallback());

                break;

            case 1:

                CustomTabsHelper.openCustomTab(this, customTabsIntent,
                        Uri.parse("https://goo.gl/KNs4Na"),
                        new WebViewFallback());
                break;

        }


    }

}
