package com.example.jaewanlee.mapmemo.HomeView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.jaewanlee.mapmemo.Database.UserDatabase;
import com.example.jaewanlee.mapmemo.LockScreen.Service.ScreenService;
import com.example.jaewanlee.mapmemo.R;
import com.example.jaewanlee.mapmemo.Util.AsyncTaskAttatchImage;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;


public class MenuActivity extends AppCompatActivity {

    private ImageView userImage;
    TextView userName;
    TextView userId;
    Button cancelButton;
    Button okButton;
    //TODO typeSomething 버튼들 만들기
    Switch lockScreen;


    SharedPreferences sharedPreferences;

    public UserDatabase _user_db = new UserDatabase();
    private KakaoSessionCallback _kakaoSessionCallback;
    private boolean _kakao_login_bool = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initLayout();

        initFunction();

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

        userImage = (ImageView) findViewById(R.id.menu_profile_image);
        userName = (TextView) findViewById(R.id.user_profile_name);
        userId = (TextView) findViewById(R.id.menu_userId_TextView);
        cancelButton = (Button) findViewById(R.id.menu_cancle_button);
        okButton = (Button) findViewById(R.id.menu_ok_button);
        lockScreen = (Switch) findViewById(R.id.menu_Lockscreen_Switch);

        Glide.with(this).load(R.drawable.glide_testing_image).apply(RequestOptions.circleCropTransform()).into(userImage);

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

                socialLogin();
            }
        });

    }

    private void socialLogin() {
        _kakaoSessionCallback = new KakaoSessionCallback();

        Session.getCurrentSession().addCallback(_kakaoSessionCallback);
        Session.getCurrentSession().checkAndImplicitOpen();

        Session.getCurrentSession().open(AuthType.KAKAO_TALK_EXCLUDE_NATIVE_LOGIN, this);

    }

    // 카카오 세션 콜백
    private class KakaoSessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            KakaoRequestMe();
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(null != exception) {
//                Logger.e(exception.toString());
                Log.d("sociallogin", exception.toString());
            }

            setContentView(R.layout.activity_menu);
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

                String _result_message = "카카오 로그인에 성공했습니다!";
                Toast.makeText(getApplicationContext(), _result_message, Toast.LENGTH_SHORT).show();

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

    }


    private void  setProfileInfo(String user_name,  String email, String image_url) {
        userName.setText(user_name);
        userId.setText(email);

        new AsyncTaskAttatchImage(userImage).execute(image_url);
    }


    // redirect to 로그인 뷰
//    protected void redirectLogin() {
//        final Intent next_intent = new Intent(this, MenuActivity.class);
//        next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        startActivity(next_intent);
//        finish();
//
//    }
}
