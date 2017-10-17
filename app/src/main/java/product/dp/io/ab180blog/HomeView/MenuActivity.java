package product.dp.io.ab180blog.HomeView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import product.dp.io.ab180blog.Database.UserDatabase;
import product.dp.io.ab180blog.LockScreen.Service.ScreenService;
import product.dp.io.ab180blog.Shared.NetworkManager;
import product.dp.io.ab180blog.Util.AsyncTaskAttatchImage;
import product.dp.io.ab180blog.Util.Logger;


public class MenuActivity extends AppCompatActivity {

    private ImageView userImage;
    TextView userName;
    TextView userId;
    com.kakao.usermgmt.LoginButton kakaoLogin;
    //TODO typeSomething 버튼들 만들기
    Switch lockScreen;


    SharedPreferences sharedPreferences;

    public UserDatabase _user_db = new UserDatabase();
    private KakaoSessionCallback _kakaoSessionCallback;
    private boolean _kakao_login_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(product.dp.io.ab180blog.R.layout.activity_menu);

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
        sharedPreferences = getSharedPreferences("Config",MODE_PRIVATE);

        userImage = (ImageView) findViewById(product.dp.io.ab180blog.R.id.menu_profile_image);
        userName = (TextView) findViewById(product.dp.io.ab180blog.R.id.user_profile_name);
        userId = (TextView) findViewById(product.dp.io.ab180blog.R.id.user_profile_email);
        kakaoLogin=(LoginButton)findViewById(product.dp.io.ab180blog.R.id.menu_kakaologin_button);
        lockScreen = (Switch) findViewById(product.dp.io.ab180blog.R.id.menu_Lockscreen_Switch);

        Glide.with(this).load(product.dp.io.ab180blog.R.drawable.glide_testing_image).apply(RequestOptions.circleCropTransform()).into(userImage);

        lockScreen.setChecked(sharedPreferences.getBoolean("lockScreen",false));

    }


    private void initFunction() {
        lockScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(b){
                    editor.putBoolean("lockScreen",true);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    startService(intent);
                }else{
                    editor.putBoolean("lockScreen",false);
                    editor.apply();
                    Intent intent = new Intent(getApplicationContext(), ScreenService.class);
                    stopService(intent);
                }

            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(true  == _kakao_login_bool) {

                    String _message = "이미 카카오톡 로그인이 되어 있습니다~";
                    Toast.makeText(getApplicationContext(), _message, Toast.LENGTH_SHORT).show();

                    return;
                }
//                final Intent next_intent = new Intent(MenuActivity.this, Login.class);
//                next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//
//                startActivity(next_intent);
//                openKaKaoLogin();


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
            if(null != exception) {
                Toast.makeText(MenuActivity.this, "네트워크를 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
//                Log.d("sociallogin", exception.toString());
            }

            setContentView(product.dp.io.ab180blog.R.layout.activity_menu);
        }
    }


    // 카카오 세션 호출
    protected void KakaoRequestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                _kakao_login_bool = true;

                int _error_code = errorResult.getErrorCode();
                int _network_error_code = -777;

                if(_network_error_code == _error_code) {
                    String _error_message = "카카오톡의 서버 네트워트가 불안정합니다. 다시 시도해 주세요.";
                    Toast.makeText(getApplicationContext(), _error_message, Toast.LENGTH_SHORT).show();

                    return;
                }
                Log.d("Login", errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {
                _kakao_login_bool = false;

            }

            @Override
            public void onSuccess(UserProfile result) {
                _kakao_login_bool = true;

                setUserDataBase(result);


//                redirectLogin();
            }
        });
    }

    protected void setUserDataBase(UserProfile profile) {

        String _user_name = profile.getNickname();
        String _email = profile.getEmail();
        String _thumnail_img_url = profile.getThumbnailImagePath();
        String _original_img_url = profile.getProfileImagePath();


//        Log.d("Login", thumnail_img_url);
//        Log.d("Login", original_img_url);

        _user_db.setUserName(_user_name);
        _user_db.setUserEmail(_email);
        _user_db.setUserOriginalImg(_original_img_url);
        _user_db.setUserThumnailImg(_thumnail_img_url);

//        Bitmap _profile_bitmap = getBitmapViaURL(thumnail_img_url);
//        userImage.setImageBitmap(_profile_bitmap);
        setProfileInfo(_user_name, _email, _original_img_url);
        sendUserProfile(_user_name, _email, _original_img_url);

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
                Toast.makeText(MenuActivity.this, message, Toast.LENGTH_SHORT).show();     }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                Logger.d("this access token is for userId=" + userId);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");
                kakaoLogin.setActivated(false);
    }
        });

    }


    private void  setProfileInfo(String user_name,  String email, String image_url) {
        userName.setText(user_name);
        userId.setText(email);
        Glide.with(this).load(image_url).apply(RequestOptions.circleCropTransform()).into(userImage);

        new AsyncTaskAttatchImage(userImage,this).execute(image_url);
    }
    private void sendUserProfile(String user_name,String email,String image_url){
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
                .add("user_name",user_name)
                .add("user_image_url",image_url);

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
