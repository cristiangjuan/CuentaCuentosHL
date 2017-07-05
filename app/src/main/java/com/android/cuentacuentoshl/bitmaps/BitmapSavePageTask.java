package com.android.cuentacuentoshl.bitmaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.android.cuentacuentoshl.utils.Constants;


/**
 * Tarea para decodificar una imagen.
 * @author quayo
 *
 */
public class BitmapSavePageTask extends AsyncTask<Integer, Void, Bitmap> {

	private Resources mResources;
    private float mScale;
    private Context mContext;
    private String mPageName;

    public BitmapSavePageTask(Context context, float scale, String pageName) {
    	Log.v(Constants.Log.METHOD, "BitmapSavePageTask - new() ");

        mResources = context.getResources();
        mScale = scale;
        mContext = context;
        mPageName = pageName;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
       
    	Log.v(Constants.Log.METHOD, "BitmapSavePageTask - doInBackground");

        //1a versión con solo scale
        final Bitmap bitmap = BitMapUtils.decodeBitmapScale(mResources, params[0], mScale);

        BitMapUtils.storeImage(bitmap, mPageName, mContext);

        return bitmap;
    }

}
