package com.prad.cs160.apilibrary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private Bitmap bitmap;

    public SerializableBitmap(Bitmap b) {
        bitmap = b;
    }

    public SerializableBitmap(URL url) {
        class BitmapURLTask extends AsyncTask<URL, Void, Bitmap> {
            protected Bitmap doInBackground(URL... url) {
                try {
                    Bitmap image = BitmapFactory.decodeStream(url[0].openConnection().getInputStream());
                    return image;
                    } catch (Exception e) {
                        Log.d("T", "Exception fetching image: " + e.toString());
                    }
                return null;
            }
        }
        BitmapURLTask task = new BitmapURLTask();
        task.execute(url);
        try {
            bitmap = task.get();
        } catch (Exception e) {
            Log.d("T", "Exception fetching image: " + e.toString());
        }
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
