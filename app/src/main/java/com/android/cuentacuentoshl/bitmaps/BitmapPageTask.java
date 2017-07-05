package com.android.cuentacuentoshl.bitmaps;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.android.cuentacuentoshl.utils.Constants;

import java.lang.ref.WeakReference;

/**
 * Tarea para decodificar una imagen.
 * @author quayo
 *
 */
public class BitmapPageTask extends AsyncTask<Integer, Void, Bitmap> {
	
	private final WeakReference<ImageView> imageViewReference;
	private Resources mResources;
    private Point mFrameRes;
    private float mScale;

    public BitmapPageTask(ImageView imageView, Resources res, Point frameRes, float scale) {
    	Log.v(Constants.Log.METHOD, "BitmapPageTask - new() ");
    	
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
        mResources = res;
        mFrameRes = frameRes;
        mScale = scale;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
       
    	Log.v(Constants.Log.METHOD, "BitmapPageTask - doInBackground");

        //1a versión con solo scale
        final Bitmap bitmap = BitMapUtils.decodeBitmapScale(mResources, params[0], mScale);

        //2a versión con ratio y scale
        /*
        Bitmap bitmapRatio = BitMapUtils.decodeBitmapRatio(mResources, params[0], mFrameRes);
        //Calculamos el coeficiente para reescalar las imagenes y ajustarlas a la pantalla del dispositivo
        float scale = BitMapUtils.calculateScale(new Point(ReadActivity.frameWidth, ReadActivity.frameHeight),
                new Point(bitmapRatio.getWidth(), bitmapRatio.getHeight()));
        Log.v(Constants.Log.SIZE, "Scale = "+scale);
        final Bitmap bitmap = BitMapUtils.scaleBitmap(bitmapRatio, scale);
        */

        //3a versión sin ratio y sin scale. La imagen correspondiente está en res ya.
        //final Bitmap bitmap = BitMapUtils.decodeBitmap(mResources, params[0]);

        return bitmap;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
    	
    	Log.v(Constants.Log.METHOD, "BitmapPageTask - onPostExecute");
    	
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {

                /*
            	Log.w(Constants.Log.SIZE, "BitmapPageTask - ImageView Size: "+imageView.getWidth()+" x "+imageView.getHeight());
            	Log.w(Constants.Log.SIZE, "BitmapPageTask - Bitmap size: "+bitmap.getWidth()+" x "+bitmap.getHeight());
            	*/
            	
            	//Insertamos el bitmap en el view.
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
