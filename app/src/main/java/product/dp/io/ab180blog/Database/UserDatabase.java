package product.dp.io.ab180blog.Database;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by jaewanlee on 2017. 7. 30..
 */

public class UserDatabase extends RealmObject {

    @PrimaryKey
    String user_email;
    @NonNull
    String user_name;
    String user_nickname;
    String user_image_url;
    String user_phone_number;
    String user_birth;
    @NonNull
    int user_login_type;
    String user_age_range;
    String user_address;
    int user_gender;
    String user_locale;
    String user_work_organization;
    String userThumnailImg;

    public int getUser_gender() {
        return user_gender;
    }

    @NonNull
    public int getUser_login_type() {
        return user_login_type;
    }

    public String getUser_address() {
        return user_address;
    }

    public String getUser_age_range() {
        return user_age_range;
    }

    public String getUser_birth() {
        return user_birth;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_image_url() {
        return user_image_url;
    }

    public String getUser_locale() {
        return user_locale;
    }

    @NonNull
    public String getUser_name() {
        return user_name;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public String getUser_work_organization() {
        return user_work_organization;
    }

    public String getUserThumnailImg() {
        return userThumnailImg;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public void setUser_age_range(String user_age_range) {
        this.user_age_range = user_age_range;
    }

    public void setUser_birth(String user_birth) {
        this.user_birth = user_birth;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public void setUser_gender(int user_gender) {
        this.user_gender = user_gender;
    }

    public void setUser_image_url(String user_image_url) {
        this.user_image_url = user_image_url;
    }

    public void setUser_locale(String user_locale) {
        this.user_locale = user_locale;
    }

    public void setUser_login_type(@NonNull int user_login_type) {
        this.user_login_type = user_login_type;
    }

    public void setUser_name(@NonNull String user_name) {
        this.user_name = user_name;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public void setUser_work_organization(String user_work_organization) {
        this.user_work_organization = user_work_organization;
    }

    public void setUserThumnailImg(String userThumnailImg) {
        this.userThumnailImg = userThumnailImg;
    }
}
