package product.dp.io.ab180blog.Util;

import java.util.HashMap;

/**
 * Created by jaewanlee on 2017. 8. 8..
 */

public class TranscHash {
    public static HashMap<String, String> categoryHash;
    public static HashMap<Integer,Integer> spinnerToMarkerTag;
    public static HashMap<Integer,Integer> markerTagToSpinner;
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

    static public void init() {

        categoryHash = new HashMap<>();

        categoryHash.put("HM1", "대형마트");
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
