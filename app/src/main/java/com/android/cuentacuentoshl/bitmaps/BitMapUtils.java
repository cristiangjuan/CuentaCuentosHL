package com.android.cuentacuentoshl.bitmaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.util.Log;

import com.android.cuentacuentoshl.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class BitMapUtils {

	
    /**
     * Decodificamos la imagen a la resolución que le pasamos
     * @param res Resources.
     * @param resId Id de la imagen.
     * @param frameResolution Resolución del marco.
     * @return
     */
    public static Bitmap decodeBitmapRatio(Resources res, int resId, Point frameResolution) {

    	Log.v(Constants.Log.SIZE, "BitMapUtils - decodeBitmapRatio");
    	
        // First decode with inJustDecodeBounds=true to check Points
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateRatio(options, frameResolution);
        
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.v(Constants.Log.SIZE, "+"+options.outWidth+","+options.outHeight+" - "+options.inSampleSize+resId);
        Bitmap b = BitmapFactory.decodeResource(res, resId, options);
        Log.v(Constants.Log.SIZE, "-"+options.outWidth+","+options.outHeight+" - "+options.inSampleSize+resId);
        return b;
    }

    /**
     * Decodificamos la imagen a la resolución que le pasamos
     * @param res Resources.
     * @param resId Id de la imagen.
     * @param ratio ratio.
     * @return
     */
    public static Bitmap decodeBitmapRatioAlready(Resources res, int resId, int ratio) {

        Log.v(Constants.Log.SIZE, "BitMapUtils - decodeBitmapRatioAlready");

        // First decode with inJustDecodeBounds=true to check Points
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = ratio;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Log.v(Constants.Log.SIZE, "+"+options.outWidth+","+options.outHeight+" - "+options.inSampleSize+resId);
        Bitmap b = BitmapFactory.decodeResource(res, resId, options);
        Log.v(Constants.Log.SIZE, "-"+options.outWidth+","+options.outHeight+" - "+options.inSampleSize+resId);
        return b;
    }
    
    /**
     * Decodificamos la imagen con un coeficiente de reescalado.
     * @param res Resources.
     * @param resId Id de la imagen.
     * @param scale Coeficiente de reescalado.
     * @return
     */
    public static Bitmap decodeBitmapScale(Resources res, int resId, float scale) {

    	Log.v(Constants.Log.SIZE, "BitMapUtils - decodeBitmapScale");

		Log.w(Constants.Log.SIZE, "BitMapUtils - decodeBitmapScale - resID: "+resId+", scale = "+scale);

        Bitmap originalBitmap = BitmapFactory.decodeResource(res, resId);

        if (scale != 1.0) {

            Log.w(Constants.Log.SIZE, "BitMapUtils - decodeBitmapScale - Scaling...");

            Matrix mx = new Matrix();
            mx.postScale(scale, scale);
            Bitmap scaledBitmap = Bitmap.createBitmap(
                    originalBitmap, 0, 0,
                    originalBitmap.getWidth(), originalBitmap.getHeight(),
                    mx, true);

            Log.w(Constants.Log.SIZE, "BitMapUtils - Original: "+originalBitmap.getWidth()+" x "+originalBitmap.getHeight());
            Log.w(Constants.Log.SIZE, "BitMapUtils - Scaled: "+scaledBitmap.getWidth()+" x "+scaledBitmap.getHeight());

            return scaledBitmap;
        }
        else return originalBitmap;
    }

    /**
     * Decodificamos la imagen con un coeficiente de reescalado.
     * @param res Resources.
     * @param resId Id de la imagen.
     * @return
     */
    public static Bitmap decodeBitmap(Resources res, int resId) {

        Log.v(Constants.Log.SIZE, "BitMapUtils - decodeBitmap");

        Log.w(Constants.Log.SIZE, "BitMapUtils - decodeBitmap resID: "+resId);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Decodificamos la imagen con Ratio y Scale
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmapRatio, float scale) {

        Log.v(Constants.Log.SIZE, "BitMapUtils - scaleBitmap");

        Matrix mx = new Matrix();
        mx.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(
                bitmapRatio, 0, 0,
                bitmapRatio.getWidth(), bitmapRatio.getHeight(),
                mx, true);

        return scaledBitmap;
    }
    
    /**
     * Calculamos el radio de reducción de la resolución de una imagen.
     * @param options
     * @param frameResolution
     * @return
     */
    public static int calculateRatio(BitmapFactory.Options options, Point frameResolution) {
    	
    	Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio");
    	
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int ratio = 1;

        Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - image = "+width+" x "+height+"");
        Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - frame = "+frameResolution.x+" x "+frameResolution.y+" BitMapUtils");

	    //Reduciremos la resolución solo cuando, la res de la imagen sea superior a la de la pantalla.
	    if (height > frameResolution.y || width > frameResolution.x) {
	
	        // Calculamos el radio redondeando. Ej: 1.51 = 2; 1.49 = 1
            float heightRatioF = (float) height / (float) frameResolution.y;
            float widthRatioF = (float) width / (float) frameResolution.x;
	        final int heightRatio = Math.round(heightRatioF);
	        final int widthRatio = Math.round(widthRatioF);

            Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - widthRatioFloat = "+widthRatioF+"");
            Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - heightRatioFloat = "+heightRatioF+"");
            Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - widthRatio = "+widthRatio+"");
			Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - heightRatio = "+heightRatio+"");
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both Points larger than or equal to the
	        // requested height and width.
	        //Si uno de los dos ratios es = 1 no habrá reducción.
	        ratio = heightRatio < widthRatio ? heightRatio : widthRatio;

            Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatio - ratio = "+ratio+"");
	    }
	
	    return ratio;
    }

    /**
     * Calculamos el radio de reducción de la resolución de una imagen
     * @param res Resources.
     * @param resId Id de la imagen.
     * @param frameResolution Resolución del marco.
     * @return
     */
    public static int calculateRatioFromRes(Resources res, int resId, Point frameResolution) {

    	Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatioFromRes");

    	// First decode with inJustDecodeBounds=true to check Points
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        final int height = options.outHeight;
	    final int width = options.outWidth;
	    int ratio = 1;

	    if (height > frameResolution.y || width > frameResolution.x) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) frameResolution.y);
	        final int widthRatio = Math.round((float) width / (float) frameResolution.x);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both Points larger than or equal to the
	        // requested height and width.
	        ratio = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

        Log.v(Constants.Log.SIZE, "BitMapUtils - calculateRatioFromRes - ratio = "+ratio);

    return ratio;
    }
    
    /**
     * Devuelve la resolución del bitmap en un objeto Point
     * @param res Resources.
     * @param resId Id de la imagen.
     * @param ratio Ratio de reescalado.
     * @return
     */
    public static Point getResolution(Resources res, int resId, int ratio){
    	
    	Log.v(Constants.Log.SIZE, "BitMapUtils - getResolution");
    	
    	Point d;
    	
    	// First decode with inJustDecodeBounds=true to check Points
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = ratio;
        BitmapFactory.decodeResource(res, resId, options);
    	
    	d = new Point(options.outWidth, options.outHeight);
    	return d;
    }

	/**
	 * Devuelve la relación entre marco e imagen, esto es el coeficiente para reescalar
	 * las imágenes y ajustarlas a la pantalla del dispositivo.
	 * @param frameResolution Resolución del marco.
	 * @param imageResolution Resolución de la imagen.
	 * @return Coeficiente.
	 */
	public static float calculateScale(Point frameResolution,
			Point imageResolution) {
		
		Log.v(Constants.Log.SIZE, "BitMapUtils - calculateScale");

    	float imageRatio;
    	float frameRatio;
    	float constant;
    	
		  frameRatio = (float) frameResolution.x / (float) frameResolution.y;
      Log.v(Constants.Log.SIZE,"BitMapUtils - calculateScale - FramePoints = "
          +frameResolution.x+", "+frameResolution.y);
    	Log.v(Constants.Log.SIZE,"BitMapUtils - calculateScale - FrameRatio "+frameRatio);
    	imageRatio = (float) imageResolution.x / (float) imageResolution.y;
      Log.v(Constants.Log.SIZE,"BitMapUtils - calculateScale - ImagePoints = "
          +imageResolution.x+", "+imageResolution.y);
    	Log.v(Constants.Log.SIZE,"BitMapUtils - calculateScale - ImageRatio "+imageRatio);
    	
    	//Comprobamos si nuestra referencia para la constante de relacion es el ancho o el alto
    	//Imagen + ancha que marco proporcionalmente. El ajuste se realiza en base al ancho
    	if (imageRatio > frameRatio){
    		
    		constant = (float) frameResolution.x / (float) imageResolution.x;
    	}
		//Imagen - ancha que marco proporcionalmente. El ajuste se realiza en base al alto
    	else {
    		constant = (float) frameResolution.y / (float) imageResolution.y;
    	}
    	
		return constant;
	}

    public static void storeImage(Bitmap image, String name, Context context) {

        Log.v(Constants.Log.STORE, "BitMapUtils - storeImage");

        File file = new File(context.getFilesDir(), name);

        if (file == null) {
            Log.e(Constants.Log.STORE,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.e(Constants.Log.STORE, "Error accessing file: " + e.getMessage());
        }
    }

    public static Bitmap loadImageFromStorage(String name, Context context) {

        Log.v(Constants.Log.STORE, "BitMapUtils - loadImageFromStorage");

        Bitmap b;
        try {
            File f = new File(context.getFilesDir(), name);
            b = BitmapFactory.decodeStream(new FileInputStream(f));

            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }
	
}












