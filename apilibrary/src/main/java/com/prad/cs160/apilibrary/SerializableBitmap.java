package com.prad.cs160.apilibrary;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;

/**
 * Created by eviltwin on 3/9/16.
 */
public class SerializableBitmap implements Serializable {
    private static final long serialVersionUID = -5228835919664263905L;
    private Bitmap bitmap;

    public SerializableBitmap(URL url) {
        class BitmapURLTask extends AsyncTask<URL, Void, Bitmap> {
            protected Bitmap doInBackground(URL... url) {
                try {
                    Bitmap image = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
                    return image;
                    } catch (Exception e) {
                        Log.d("SerializableBitmap", "Exception fetching image: " + e.toString());
                    }
                return null;
            }
        }
        BitmapURLTask task = new BitmapURLTask();
        task.execute(url);
        try {
            bitmap = task.get();
        } catch (Exception e) {
            Log.d("SerializableBitmap", "Exception fetching image: " + e.toString());
        }
        bitmap = getCircularBitmap(bitmap);
    }
    // TODO(prad): Acknowledge: http://stackoverflow.com/questions/11932805/cropping-circular-area-from-bitmap-in-android
    public Bitmap getCircularBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    // Converts the Bitmap into a byte array for serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteStream);
        byte bitmapBytes[] = byteStream.toByteArray();
        if (success)
            out.write(bitmapBytes, 0, bitmapBytes.length);
    }

    // Deserializes a byte array representing the Bitmap and decodes it
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        int b;
        while((b = in.read()) != -1)
            byteStream.write(b);
        byte bitmapBytes[] = byteStream.toByteArray();
        bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

}
