package product.dp.io.ab180blog.MemoList;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import product.dp.io.ab180blog.Database.MemoDatabase;

/**
 * Created by jaewanlee on 2017. 10. 18..
 */

public class MemoListSearch {

    public ArrayList<MemoListDatabase> search(String keyWord) {
        ArrayList<MemoListDatabase> memoDatabaseArrayList = new ArrayList();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<MemoDatabase> addressSearchResults = realm
                .where(MemoDatabase.class)
                .contains("memo_document_road_address_name", keyWord)
                .or()
                .contains("memo_document_address_name", keyWord)
                .findAll();
        if (addressSearchResults.size() > 0) {
            for (MemoDatabase memoDatabase : addressSearchResults) {
                memoDatabaseArrayList.add(new MemoListDatabase(memoDatabase));
            }
        }
        return memoDatabaseArrayList;
    }

}
