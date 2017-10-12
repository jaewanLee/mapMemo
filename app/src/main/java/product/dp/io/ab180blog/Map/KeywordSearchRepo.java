package product.dp.io.ab180blog.Map;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaewanlee on 2017. 8. 7..
 */

public class KeywordSearchRepo {
    @SerializedName("meta")
    KeywordMeta keywordMeta;
    @SerializedName("documents")
    List<KeywordDocuments> keywordDocuments=new ArrayList<>();

    public class KeywordMeta{
        @SerializedName("total_count") int total_count;
        @SerializedName("is_end") boolean is_end;

        public int getTotal_count() {
            return total_count;
        }
        public boolean getIs_end(){
            return is_end;
        }
    }

    public class KeywordDocuments{
        @SerializedName("place_name") String place_name;
        @SerializedName("distance") String distance;
        @SerializedName("place_url") String place_url;
        @SerializedName("category_name") String category_name;
        @SerializedName("address_name") String address_name;
        @SerializedName("road_address_name") String road_address_name;
        @SerializedName("id") String id;
        @SerializedName("phone") String phone;
        @SerializedName("category_group_code") String category_group_code;
        @SerializedName("x") String x;
        @SerializedName("y") String y;

        public String getAddress_name() {
            return address_name;
        }

        public String getCategory_group_code() {
            return category_group_code;
        }

        public String getCategory_name() {
            return category_name;
        }

        public String getDistance() {
            return distance;
        }

        public String getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getPlace_name() {
            return place_name;
        }

        public String getPlace_url() {
            return place_url;
        }

        public String getRoad_address_name() {
            return road_address_name;
        }

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }
    }

    public List<KeywordDocuments> getKeywordDocuments() {
        return keywordDocuments;
    }

    public KeywordMeta getKeywordMeta() {
        return keywordMeta;
    }
}
