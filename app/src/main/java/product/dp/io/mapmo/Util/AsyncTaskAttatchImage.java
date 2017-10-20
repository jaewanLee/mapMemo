package product.dp.io.mapmo.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by tim on 28/09/2017.
 */

public class AsyncTaskAttatchImage extends AsyncTask<String, String, Bitmap> {

    private ImageView imageView;
    Context context;


    public AsyncTaskAttatchImage(ImageView imageView,Context context) {
        this.imageView = imageView;
        this.context=context;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap _bitmap = null;

        try {
            URL _url = new URL(strings[0]);
            _bitmap = BitmapFactory.decodeStream((InputStream) _url.getContent());
        } catch(IOException e) {

        }
        return _bitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        setBitmapToImageView (bitmap);
    }

    protected void setBitmapToImageView (Bitmap bitmap) {
        if (bitmap==null){
            bitmap=BitmapFactory.decodeResource(context.getResources(), product.dp.io.mapmo.R.drawable.ic_default_user);
        }

        if (bitmap.getWidth() > bitmap.getHeight()) {
            bitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

//        Canvas canvas = new Canvas(bitmap);
//
//        final int color = 0xff424242;
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        float r = 0;

//        if (bitmap.getWidth() > bitmap.getHeight()) {
//            r = bitmap.getHeight() / 2;
//        } else {
//            r = bitmap.getWidth() / 2;
//        }
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);
//        canvas.drawCircle(r, r, r, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);

        imageView.setImageBitmap(bitmap);
    }


}
