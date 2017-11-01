package product.dp.io.mapmo.HomeView;

/**
 * Created by jaewanlee on 2017. 10. 31..
 */

public class SaveData {
    public Callback callback;

    public SaveData(Callback callback){
        this.callback=callback;

    }
    public void save(){

    }
    public interface Callback{
        void call();
    }
}
