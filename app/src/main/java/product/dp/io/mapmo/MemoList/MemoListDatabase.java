package product.dp.io.mapmo.MemoList;

import product.dp.io.mapmo.Database.MemoDatabase;

/**
 * Created by jaewanlee on 2017. 9. 20..
 */

public class MemoListDatabase {
    Boolean isChecked;
    Boolean isNew;

    private int memo_no;
    private long memo_createDate;
    private String memo_own_user_id;
    private String memo_content;

    private String memo_document_place_name;
    private String memo_document_distance;
    private String memo_document_place_url;
    private String memo_document_category_name;
    private String memo_document_address_name;
    private String memo_document_road_address_name;
    private String memo_document_id;
    private String memo_document_phone;
    private String memo_document_category_group_code;
    private String memo_document_x;
    private String memo_document_y;

    public MemoListDatabase() {
        this.isChecked = false;
        this.isNew = false;
    }

    public MemoListDatabase(MemoDatabase memoDatabase) {
        this.isChecked = false;
        this.isNew = false;

        this.memo_no = memoDatabase.getMemo_no();
        this.memo_createDate = memoDatabase.getMemo_createDate();
        this.memo_own_user_id = memoDatabase.getMemo_own_user_id();
        this.memo_content = memoDatabase.getMemo_content();
        this.memo_document_place_name = memoDatabase.getMemo_document_place_name();
        this.memo_document_distance = memoDatabase.getMemo_document_distance();
        this.memo_document_place_url = memoDatabase.getMemo_document_place_url();
        this.memo_document_category_name = memoDatabase.getMemo_document_category_name();
        this.memo_document_address_name = memoDatabase.getMemo_document_address_name();
        this.memo_document_road_address_name = memoDatabase.getMemo_document_road_address_name();
        this.memo_document_id = memoDatabase.getMemo_document_id();
        this.memo_document_phone = memoDatabase.getMemo_document_phone();
        this.memo_document_category_group_code = memoDatabase.getMemo_document_category_group_code();
        this.memo_document_x = memoDatabase.getMemo_document_x();
        this.memo_document_y = memoDatabase.getMemo_document_y();
    }


    public void setMemoDatabase(MemoDatabase memoDatabase) {
        this.memo_no = memoDatabase.getMemo_no();
        this.memo_createDate = memoDatabase.getMemo_createDate();
        this.memo_own_user_id = memoDatabase.getMemo_own_user_id();
        this.memo_content = memoDatabase.getMemo_content();
        this.memo_document_place_name = memoDatabase.getMemo_document_place_name();
        this.memo_document_distance = memoDatabase.getMemo_document_distance();
        this.memo_document_place_url = memoDatabase.getMemo_document_place_url();
        this.memo_document_category_name = memoDatabase.getMemo_document_category_name();
        this.memo_document_address_name = memoDatabase.getMemo_document_address_name();
        this.memo_document_road_address_name = memoDatabase.getMemo_document_road_address_name();
        this.memo_document_id = memoDatabase.getMemo_document_id();
        this.memo_document_phone = memoDatabase.getMemo_document_phone();
        this.memo_document_category_group_code = memoDatabase.getMemo_document_category_group_code();
        this.memo_document_x = memoDatabase.getMemo_document_x();
        this.memo_document_y = memoDatabase.getMemo_document_y();
    }

    public Boolean getIsChecked() {
        return this.isChecked;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public String getMemo_content() {
        return memo_content;
    }

    public String getMemo_own_user_id() {
        return memo_own_user_id;
    }

    public int getMemo_no() {
        return memo_no;
    }

    public Boolean getChecked() {
        return isChecked;
    }

    public long getMemo_createDate() {
        return memo_createDate;
    }

    public String getMemo_document_address_name() {
        return memo_document_address_name;
    }

    public String getMemo_document_category_group_code() {
        return memo_document_category_group_code;
    }

    public String getMemo_document_category_name() {
        return memo_document_category_name;
    }

    public String getMemo_document_distance() {
        return memo_document_distance;
    }

    public String getMemo_document_id() {
        return memo_document_id;
    }

    public String getMemo_document_phone() {
        return memo_document_phone;
    }

    public String getMemo_document_place_name() {
        return memo_document_place_name;
    }

    public String getMemo_document_place_url() {
        return memo_document_place_url;
    }

    public String getMemo_document_road_address_name() {
        return memo_document_road_address_name;
    }

    public String getMemo_document_x() {
        return memo_document_x;
    }

    public String getMemo_document_y() {
        return memo_document_y;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setIsNew(Boolean aNew) {
        isNew = aNew;
    }

    public void setMemo_content(String memo_content) {
        this.memo_content = memo_content;
    }

    public void setMemo_own_user_id(String memo_own_user_id) {
        this.memo_own_user_id = memo_own_user_id;
    }

    public void setMemo_no(int memo_no) {
        this.memo_no = memo_no;
    }

    public void setMemo_createDate(long memo_createDate) {
        this.memo_createDate = memo_createDate;
    }

    public void setChecked(Boolean checked) {
        isChecked = checked;
    }

    public void setMemo_document_address_name(String memo_document_address_name) {
        this.memo_document_address_name = memo_document_address_name;
    }

    public void setMemo_document_category_group_code(String memo_document_category_group_code) {
        this.memo_document_category_group_code = memo_document_category_group_code;
    }

    public void setMemo_document_category_name(String memo_document_category_name) {
        this.memo_document_category_name = memo_document_category_name;
    }

    public void setMemo_document_distance(String memo_document_distance) {
        this.memo_document_distance = memo_document_distance;
    }

    public void setMemo_document_id(String memo_document_id) {
        this.memo_document_id = memo_document_id;
    }

    public void setMemo_document_phone(String memo_document_phone) {
        this.memo_document_phone = memo_document_phone;
    }

    public void setMemo_document_place_name(String memo_document_place_name) {
        this.memo_document_place_name = memo_document_place_name;
    }

    public void setMemo_document_place_url(String memo_document_place_url) {
        this.memo_document_place_url = memo_document_place_url;
    }

    public void setMemo_document_road_address_name(String memo_document_road_address_name) {
        this.memo_document_road_address_name = memo_document_road_address_name;
    }

    public void setMemo_document_x(String memo_document_x) {
        this.memo_document_x = memo_document_x;
    }

    public void setMemo_document_y(String memo_document_y) {
        this.memo_document_y = memo_document_y;
    }
}
