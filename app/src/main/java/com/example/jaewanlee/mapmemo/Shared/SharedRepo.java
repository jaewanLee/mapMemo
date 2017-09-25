package com.example.jaewanlee.mapmemo.Shared;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaewanlee on 2017. 9. 13..
 */

public class SharedRepo {


    @SerializedName("memoDatabases")
    SharedMemoDatabases memodatabases;

    @SerializedName("memoGID")
    String memoGID;

    public class SharedMemoDatabases{
        List<SharedMemo> memoDatabaseList=new ArrayList<>();
        public class SharedMemo{
            private int memo_no;
            private int memo_category;
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
        }
    }


}
