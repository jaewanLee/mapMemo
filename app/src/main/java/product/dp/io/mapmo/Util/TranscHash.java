package product.dp.io.mapmo.Util;

import java.util.HashMap;

import product.dp.io.mapmo.R;

/**
 * Created by jaewanlee on 2017. 8. 8..
 */

public class TranscHash {
    public static HashMap<String, String> categoryHash;
    public static HashMap<String,Integer> categoryToImage;
    public static TranscHash instance;

    TranscHash() {
        init();

    }

    public static TranscHash getInstance() {
        if (instance == null) {
            instance = new TranscHash();
        }
        return instance;
    }

    static public String rawToreFinedCategory(String raw_category){
        return categoryHash.get(raw_category);
    }

    static public int rawToImageCategory(String raw_category){
        Logger.d(raw_category);
        return categoryToImage.get(raw_category);
    }

    static public void init() {

        categoryHash = new HashMap<>();

        categoryHash.put("MT1", "대형마트");
        categoryHash.put("CS2", "편의점");
        categoryHash.put("PS3", "어린이집,유치원");
        categoryHash.put("SC4", "학교");
        categoryHash.put("AC5", "학원");
        categoryHash.put("PK6", "주차장");
        categoryHash.put("OL7", "주유소,충전소");
        categoryHash.put("SW8", "지하철역");
        categoryHash.put("BK9", "은행");
        categoryHash.put("CT1", "문화시설");
        categoryHash.put("AG2", "중개업소");
        categoryHash.put("PO3", "공공기관");
        categoryHash.put("AT4", "관광명소");
        categoryHash.put("AD5", "숙소");
        categoryHash.put("FD6", "음식점");
        categoryHash.put("CE7", "카페");
        categoryHash.put("HP8", "병원");
        categoryHash.put("PM9", "약국");
        categoryHash.put("CM","내가 찍은 장소");

        categoryToImage=new HashMap<>();

        categoryToImage.put("MT1", R.drawable.ic_category_shoppingcart);
        categoryToImage.put("CS2", R.drawable.ic_category_convenince_store);
        categoryToImage.put("PS3", R.drawable.ic_category_kinder_garden);
        categoryToImage.put("SC4", R.drawable.ic_category_school);
        categoryToImage.put("AC5", R.drawable.ic_category_school);
        categoryToImage.put("PK6", R.drawable.ic_category_parking);
        categoryToImage.put("OL7", R.drawable.ic_category_gas_station);
        categoryToImage.put("SW8", R.drawable.ic_category_gas_station);
        categoryToImage.put("BK9", R.drawable.ic_category_bank);
        categoryToImage.put("CT1", R.drawable.ic_category_facility);
        categoryToImage.put("AG2", R.drawable.ic_category_facility);
        categoryToImage.put("PO3", R.drawable.ic_category_facility);
        categoryToImage.put("AT4", R.drawable.ic_category_attration);
        categoryToImage.put("AD5", R.drawable.ic_category_hotel);
        categoryToImage.put("FD6", R.drawable.ic_category_restaunrant);
        categoryToImage.put("CE7", R.drawable.ic_category_cafe);
        categoryToImage.put("HP8", R.drawable.ic_category_hospital);
        categoryToImage.put("PM9", R.drawable.ic_category_pharmacy);
        categoryToImage.put("CM",R.drawable.ic_category_custom);

//        spinnerToMarkerTag=new HashMap<>();
//        spinnerToMarkerTag.put(0,9107);
//        spinnerToMarkerTag.put(1,9108);
//        spinnerToMarkerTag.put(2,9109);
//        spinnerToMarkerTag.put(3,9110);
//        spinnerToMarkerTag.put(4,9111);
//        spinnerToMarkerTag.put(5,9112);
//
//        markerTagToSpinner=new HashMap<>();
//        markerTagToSpinner.put(9107,0);
//        markerTagToSpinner.put(9108,1);
//        markerTagToSpinner.put(9109,2);
//        markerTagToSpinner.put(9110,3);
//        markerTagToSpinner.put(9111,4);
//        markerTagToSpinner.put(9112,5);

    }


}
