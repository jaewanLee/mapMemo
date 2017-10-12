package product.dp.io.ab180blog.Map;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;

import product.dp.io.ab180blog.Util.Logger;

/**
 * Created by jaewanlee on 2017. 8. 28..
 */

public class CustomBallonAdapter implements CalloutBalloonAdapter {

    private final View customCalloutBalloon;
    Activity activity;

    public CustomBallonAdapter(Activity activity) {
        this.activity = activity;
        customCalloutBalloon = this.activity.getLayoutInflater().inflate(product.dp.io.ab180blog.R.layout.customballon_interface, null);

    }

    @Override
    public View getCalloutBalloon(MapPOIItem mapPOIItem) {
        Logger.d("getCalloutBalloon");
        ((TextView) customCalloutBalloon.findViewById(product.dp.io.ab180blog.R.id.customballon_name_textView)).setText("testText");
        ((ImageView) customCalloutBalloon.findViewById(product.dp.io.ab180blog.R.id.customballon_category_imageView)).setImageResource(product.dp.io.ab180blog.R.drawable.ic_action_back);
        ((TextView) customCalloutBalloon.findViewById(product.dp.io.ab180blog.R.id.customballon_time_textView)).setText("date");
        ((TextView) customCalloutBalloon.findViewById(product.dp.io.ab180blog.R.id.customballon_memo_textView)).setText("memo");
        return customCalloutBalloon;
    }

    @Override
    public View getPressedCalloutBalloon(MapPOIItem mapPOIItem) {
        Logger.d("getPressedCalloutBalloon");
        return null;
    }
}
