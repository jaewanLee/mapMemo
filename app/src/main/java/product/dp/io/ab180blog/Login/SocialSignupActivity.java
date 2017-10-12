package product.dp.io.ab180blog.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.kakao.auth.ErrorCode;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;

import product.dp.io.ab180blog.Database.UserDatabase;

/**
 * Created by tim on 27/09/2017.
 */

public class SocialSignupActivity  extends AppCompatActivity {

    public UserDatabase user_db = new UserDatabase();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestSelf();

    }


    // 사용자 상태 체크
    protected void requestSelf() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.d("mapmemo_social_login", errorResult.toString());

                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    finish();
                } else {
                    redirectLogin();
                }


            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(UserProfile profile) {

                setUserDataBase(profile);
                redirectLogin();
            }
        });

    }

    protected void setUserDataBase(UserProfile profile) {
        String user_name = profile.getNickname();
        String email = profile.getEmail();
        String thumnail_img_url = profile.getThumbnailImagePath();
        String original_img_url = profile.getProfileImagePath();


        user_db = new UserDatabase();
        user_db.setUserName(user_name);
        user_db.setUserEmail(email);
        user_db.setUserOriginalImg(original_img_url);
        user_db.setUserThumnailImg(thumnail_img_url);
    }

    // redirect to 로그인 뷰
    protected void redirectLogin() {
        final Intent next_intent = new Intent(this, Login.class);
        next_intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(next_intent);
        finish();

    }

}


