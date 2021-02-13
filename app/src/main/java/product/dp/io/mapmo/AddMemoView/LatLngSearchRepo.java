package product.dp.io.mapmo.AddMemoView;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by jaewanlee on 2017. 12. 28..
 */

public class LatLngSearchRepo {
    @SerializedName("meta")
    LatLngMeta latLngMeta;
    @SerializedName("documents")
    ArrayList<LatLngDocuments> latLngDocumentsArrayList=new ArrayList<>();

    public class LatLngMeta{
        @SerializedName("total_count") int total_count;

        public int getTotal_count() {
            return total_count;
        }
    }
    public class LatLngDocuments{
        @SerializedName("road_address") LatLngRoadAddress latLngRoadAddress;
        @SerializedName("address") LatLngAddress latLngAddress;

    }
    public class LatLngRoadAddress{
        @SerializedName("address_name") String road_address_name;


    }
    public class LatLngAddress{
        @SerializedName("address_name") String address_name;

    }

    public ArrayList<LatLngDocuments> getLatLngDocuments(){
        return latLngDocumentsArrayList;
    }

}
