package product.dp.io.mapmo.Menu;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import product.dp.io.mapmo.Core.MainApplication;
import product.dp.io.mapmo.Database.MemoDatabase;
import product.dp.io.mapmo.Database.UserDatabase;
import product.dp.io.mapmo.LockScreen.Service.ScreenService;
import product.dp.io.mapmo.R;
import product.dp.io.mapmo.Shared.NetworkManager;
import product.dp.io.mapmo.Util.Logger;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;


public class MenuActivity extends AppCompatActivity {

    private ImageView userImage;
    TextView userName;
    TextView userId;
    com.kakao.usermgmt.LoginButton kakaoLogin;
    //TODO typeSomething 버튼들 만들기
    Switch lockScreen;
    Button logout_bt;
    TextView resetMemo_tv;


    SharedPreferences sharedPreferences;

    public UserDatabase _user_db = new UserDatabase();
    private KakaoSessionCallback _kakaoSessionCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.mapmo.R.layout.activity_menu);

        initLayout();

        initFunction();

        socialLogin();

//        userImage = (ImageButton) findViewById(R.id.menu_imgae_ImageButton);
//        userName = (TextView) findViewById(R.id.menu_userName_TextView);
//        userId = (TextView) findViewById(R.id.menu_userId_TextView);
//        cancelButton = (Button) findViewById(R.id.menu_cancle_button);
//        okButton = (Button) findViewById(R.id.menu_ok_button);
//        lockScreen = (Switch) findViewById(R.id.menu_Lockscreen_Switch);
//
//        sharedPreferences=getSharedPreferences("Config",MODE_PRIVATE);
//
//        Glide.with(this).load(R.drawable.glide_testing_image).apply(RequestOptions.circleCropTransform()).into(userImage);
//
//        lockScreen.setChecked(sharedPreferences.getBoolean("lockScreen",false));
//
//        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                if(b){
//                    editor.putBoolean("lockScreen",true);
//                    editor.apply();
//                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
//                    startService(intent);
//                }else{
//                    editor.putBoolean("lockScreen",false);
//                    editor.apply();
//                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
//                    stopService(intent);
//                }
//
//            }
//        });


    }


    private void initLayout() {
        sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);

        userImage = (ImageView) findViewById(product.dp.io.mapmo.R.id.menu_profile_image);
        userName = (TextView) findViewById(product.dp.io.mapmo.R.id.user_profile_name);
        userId = (TextView) findViewById(product.dp.io.mapmo.R.id.user_profile_email);
        kakaoLogin = (LoginButton) findViewById(product.dp.io.mapmo.R.id.menu_kakaologin_button);
        lockScreen = (Switch) findViewById(product.dp.io.mapmo.R.id.menu_Lockscreen_Switch);
        logout_bt = (Button) findViewById(R.id.menu_logout_button);
        logout_bt.setVisibility(View.INVISIBLE);
        resetMemo_tv = (TextView) findViewById(R.id.menu_memo_reset);

        lockScreen.setChecked(sharedPreferences.getBoolean("lockScreen", false));

        _user_db=MainApplication.getMainApplicationContext().getOnUserDatabase();
        if(_user_db!=null){
            if(_user_db.getUser_image_url()!=null&&!_user_db.getUser_email().equals("guest")){
                File file=new File(_user_db.getUser_image_url());
                if(file.canRead()){
                    Glide.with(this)
                            .load(file)
                            .centerCrop()
                            .into(userImage);
                    userName.setText(_user_db.getUser_email());
                    userId.setText(_user_db.getUser_email());
                }
            }
            else
                _user_db=new UserDatabase();
        }else
            _user_db=new UserDatabase();



    }


    private void initFunction() {
        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (b) {
                    editor.putBoolean("lockScreen", true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent);
                } else {
                    editor.putBoolean("lockScreen", false);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    stopService(intent);
                }

            }
        });

        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManagement.requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        Toast.makeText(MenuActivity.this, "성공적으로 로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
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
                    }


                });
                Logger.d("로그아웃 버튼 클릭");
            }
        });
        resetMemo_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
                builder.setTitle("포멧하시겠습니까?");
                builder.setMessage("저장되어 있던 모든 내용이 삭제되고 처음 어플을 설치했을 때로 되돌아 갑니다");

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


    }

    private void socialLogin() {


        _kakaoSessionCallback = new KakaoSessionCallback();

        Session.getCurrentSession().addCallback(_kakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();
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

            setContentView(product.dp.io.mapmo.R.layout.activity_menu);
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
                Toast.makeText(MenuActivity.this, message, Toast.LENGTH_SHORT).show();
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

    private void setUserDatabase(UserDatabase userDatabase) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserDatabase> saveUserDatabase = realm.where(UserDatabase.class).equalTo("user_email", userDatabase.getUser_email()).findAll();
        if (saveUserDatabase.size() > 0&&MainApplication.getMainApplicationContext().getOnUserDatabase()!=null&&MainApplication.getMainApplicationContext().getOnUserDatabase().getUser_image_url()!=null) {
            //이미 데이터베이스가 있는 경우
            realm.beginTransaction();
            UserDatabase updateUserDatabse = saveUserDatabase.first();
            updateUserDatabse.setUser_image_url(userDatabase.getUser_image_url());
            realm.commitTransaction();

        } else {
            //새로운 유저인 경우
            realm.beginTransaction();
            //TODO userNo 설정하기
            UserDatabase newUserDatabase = realm.copyToRealm(userDatabase);
            realm.commitTransaction();
        }
        //메인 어플리케이션의 UserDatabase를 수정해줌
        MainApplication.getMainApplicationContext().setOnUserDatabase(userDatabase);

        SharedPreferences sharedPreferences = getSharedPreferences("USER_SHARED", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isAutoLogin", true);
        editor.putString("user_email", userDatabase.getUser_email());
        editor.apply();

    }


    private void setProfileInfo(final String user_name, String email, String image_url) {
        userName.setText(user_name);
        userId.setText(email);
        Glide.with(this)
                .load(image_url)
                .centerCrop()
                .into(userImage);
        Glide.with(this)
                .load(image_url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(SIZE_ORIGINAL,SIZE_ORIGINAL) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        saveImage(resource,user_name);
                    }
                });

    }

    private void saveImage(Bitmap image,String user_name){
        String savedImagePath = null;

        String imageFileName = "JPEG_" + user_name + ".jpg";
        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        + "/MapMo");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        _user_db.setUser_image_url(savedImagePath);
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

        //TODO myValue에다가 내 아이디랑 시간 써서 넣기
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("user_email", email)
                .add("user_name", user_name)
                .add("user_image_url", image_url);

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
                        Toast.makeText(MenuActivity.this, "request Err", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MenuActivity.this, result, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MenuActivity.this, "request Err 400", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // redirect to 로그인 뷰
//    protected void redirectLogin() {
//        final Intent next_intent = new Intent(this, MenuActivity.class);
//        next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(next_intent);
//        finish();
//
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(_kakaoSessionCallback);
    }
}
