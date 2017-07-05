package com.android.cuentacuentoshl.bitmaps;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.cuentacuentoshl.customs.CustomView;
import com.android.cuentacuentoshl.utils.Constants;

import java.lang.ref.WeakReference;


/**
 * Tarea para decodificar una imagen.
 * @author quayo
 *
 */
public class BitmapLoadStoragedPageTask extends AsyncTask<String, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
	private Context mContext;

    public BitmapLoadStoragedPageTask(CustomView imageView, Context context) {
    	Log.v(Constants.Log.METHOD, "BitmapLoadStoragedPageTask - new() ");
    	
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        mContext = context;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(String... params) {
       
    	Log.v(Constants.Log.METHOD, "BitmapLoadStoragedPageTask - doInBackground");

        //1a versión con solo scale
        final Bitmap bitmap = BitMapUtils.loadImageFromStorage(params[0], mContext);

        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	
    	Log.v(Constants.Log.METHOD, "BitmapLoadStoragedPageTask - onPostExecute");
    	
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
            	
            	//Log.w(Constants.Log.SIZE, "Before Adjusted -  W: "+imageView.getWidth()+" H: "+imageView.getHeight());
            	//Log.w(Constants.Log.SIZE, "Bitmap size -  W: "+bitmap.getWidth()+" H: "+bitmap.getHeight());
            	
            	//Insertamos el bitmap en el view.
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
