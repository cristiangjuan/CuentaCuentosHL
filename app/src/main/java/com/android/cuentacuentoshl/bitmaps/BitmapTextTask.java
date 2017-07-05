package com.android.cuentacuentoshl.bitmaps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.cuentacuentoshl.ReadActivity;
import com.android.cuentacuentoshl.customs.CustomTransitionDrawable;
import com.android.cuentacuentoshl.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Tarea para decodificar una imagen.
 * @author quayo
 *
 */
public class BitmapTextTask extends AsyncTask< Integer, Void, Integer > {
	
	private Context mContext;
	private Resources mResources;
	private RelativeLayout mImageFrame;
	private Point mFrameRes;
    private Bitmap mBitmapRatio;
    private int mRatio;
    private float mScale;
	private int[][][] mMatrizCT;
	private int[][][] mMatrizCT_light;
	private int[][][] mMatrizTiemposCT;
	private Point[] mPositionsCT;
	//private ArrayList<CustomScaleAnimationSubs> mArrayAnimaciones;
	private ArrayList<CustomTransitionDrawable> mArrayTransiciones;
	private ArrayList<ArrayList<Bitmap>> matrizBitmaps;
	private ArrayList<ArrayList<Bitmap>> matrizBitmaps_light;

    public BitmapTextTask(Context context, Resources res, RelativeLayout imageFrame, Point frameRes,
                          ArrayList<CustomTransitionDrawable> arrayTransiciones,
                          int[][][] matrizCT, int[][][] matrizCT_light, int[][][] matrizTiemposCT, Point[] positionsCT) {
    	
    	Log.v(Constants.Log.METHOD, "BitmapTextTask - new() ");
    	
        // Use a WeakReference to ensure the ImageView can be garbage collected
    	mContext = context;
    	mImageFrame = imageFrame;
		mFrameRes = frameRes;
		Log.v(Constants.Log.SIZE, "BitmapTextTask - FrameSize = "+imageFrame.getWidth()+" x "+imageFrame.getHeight());
    	//mArrayAnimaciones = arrayAnimaciones;
    	mArrayTransiciones = arrayTransiciones;
        mResources = res;
        mMatrizCT = matrizCT;
        mMatrizCT_light = matrizCT_light;
        mMatrizTiemposCT = matrizTiemposCT;
        mPositionsCT = positionsCT;
    }

    // Decode image in background.
    @Override
    protected Integer doInBackground(Integer... params) {
       
    	Log.v(Constants.Log.METHOD, "BitmapTextTask - doInBackground");
    	
    	matrizBitmaps = new ArrayList<ArrayList<Bitmap>>();
    	matrizBitmaps_light = new ArrayList<ArrayList<Bitmap>>();

		//Obtenemos el ratio de la primega pág para aplicar después a cada texto
        /**
         * La solución ideal sería obtener el Ratio y Scale de BitmapPageTask y no
         * tener que repetirlo.
         */
        mRatio = BitMapUtils.calculateRatioFromRes(mResources, params[0], mFrameRes);
        mBitmapRatio = BitMapUtils.decodeBitmapRatioAlready(mResources, params[0], mRatio);
        //Calculamos el coeficiente para reescalar las imagenes y ajustarlas a la pantalla del dispositivo
        mScale = BitMapUtils.calculateScale(new Point(ReadActivity.frameWidth, ReadActivity.frameHeight),
                new Point(mBitmapRatio.getWidth(), mBitmapRatio.getHeight()));
        Log.v(Constants.Log.SIZE, "Scale = "+mScale);
    	
    	//Bucle cuadros de texto
    	for (int i=0; i<mMatrizCT.length; i++){
    		
    		ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    		ArrayList<Bitmap> bitmapArray_light = new ArrayList<Bitmap>();
        	
    		//Bucle lineas cuadros de texto
        	for (int j=0; j<mMatrizCT[i].length; j++) {
        		
        		//Bucle palabras bitmaps
        		for (int k=0; k<mMatrizCT[i][j].length; k++) {

        			bitmapArray.add(BitMapUtils.scaleBitmap(
							BitMapUtils.decodeBitmapRatioAlready(mResources, mMatrizCT[i][j][k], mRatio), mScale));
        			bitmapArray_light.add(BitMapUtils.scaleBitmap(
							BitMapUtils.decodeBitmapRatioAlready(mResources,  mMatrizCT_light[i][j][k], mRatio), mScale));
        		}
        	}
        	matrizBitmaps.add(bitmapArray);
        	matrizBitmaps_light.add(bitmapArray_light);
    	}
    	
        return 0;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Integer result) {
    	
    	Log.v(Constants.Log.METHOD, "BitmapTextTask - onPostExecute");
    	
    	/*
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
            
            	imageView.setImageBitmap(bitmap);
				mLinea.addView(imageView);
            }
        }
        */
  
    	Iterator<ArrayList<Bitmap>> iteratorMatrix = matrizBitmaps.iterator();
    	Iterator<ArrayList<Bitmap>> iteratorMatrix_light = matrizBitmaps_light.iterator();
    	
    	//Bucle cuadros de texto
    	for (int i=0; i<mMatrizCT.length; i++) {
    	
	    	//cuadroTextoLayout representa un cuadro de texto
	    	LinearLayout cuadroTextoLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams cuadroTextoParamsLayout = new LinearLayout.LayoutParams(
	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			//textUnitParamsLayout.gravity = Gravity.RIGHT;
			Point positionCuadroTexto = mPositionsCT[i];
			
			RelativeLayout.LayoutParams positionParams = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			positionParams.leftMargin = (int) (positionCuadroTexto.x * mScale / mRatio);
			positionParams.topMargin = (int) (positionCuadroTexto.y * mScale / mRatio);
			
			/*
			cuadroTextoParamsLayout.setMargins(
					(int) (positionCuadroTexto.x * mScale),
					(int) (positionCuadroTexto.getHeight() * mScale), 0, 0);
			*/
			
			//lineLayout.setGravity(Gravity.RIGHT);
			cuadroTextoLayout.setLayoutParams(cuadroTextoParamsLayout);
			cuadroTextoLayout.setOrientation(LinearLayout.VERTICAL);
			
			//Variable que controla el tiempo en el que empieza cada animación
			//int startTime = 0;
	    	
			Iterator<Bitmap> iteratorBitmapsCT = iteratorMatrix.next().iterator();
			Iterator<Bitmap> iteratorBitmapsCT_light = iteratorMatrix_light.next().iterator();
		
			//Bucle lineas cuadros de texto
			for (int j=0; j<mMatrizCT[i].length; j++) {

				//linearLayout representa una linea de texto
				LinearLayout linea = new LinearLayout(mContext);
				LinearLayout.LayoutParams lineaParams = new LinearLayout.LayoutParams(
		                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lineaParams.gravity = Gravity.CENTER_HORIZONTAL;
				linea.setLayoutParams(lineaParams);
				linea.setOrientation(LinearLayout.HORIZONTAL);
				//linea.setGravity(Gravity.CENTER_HORIZONTAL);
				cuadroTextoLayout.addView(linea);
				
				int numPalabras = mMatrizCT[i][j].length;
				
				//Añadimos las palabras de una linea
				for (int k=0; k<numPalabras; k++) {
					
					//Creamos el transitionDrawable para la animación de resaltado
					ImageView image = new ImageView(mContext);
					ImageView image2 = new ImageView(mContext);
					image.setImageBitmap(iteratorBitmapsCT.next());
					image2.setImageBitmap(iteratorBitmapsCT_light.next());
					
					Drawable[] layers = {image.getDrawable(), image2.getDrawable()};
					CustomTransitionDrawable transition = new CustomTransitionDrawable(layers);
					
					//Añadimos la imagen a la pantalla
					ImageView imageTransition = new ImageView(mContext);
					imageTransition.setImageDrawable(transition);
					linea.addView(imageTransition);
					//linea.addView(image);
					
					//Añadimos la animación
					//CustomScaleAnimationSubs a = new CustomScaleAnimationSubs();
					//a.setStartOffset(startTime);
					
					/*
					 * Calculamos el tiempo en el que empezará la siguiente animación.
					 * Esto será el tiempo que dura la animación de la palabra reducido por un coeficiente
					 * para que haya más continuidad.
					 */
					//startTime += tiempoPalabra * Constants.Subs.CF_START_OFFSET;
					//a.setDuration(tiempoPalabra);
					transition.setDuration(mMatrizTiemposCT[i][j][k]);
					//a.prepare(image);
					//mArrayAnimaciones.add(a);
					mArrayTransiciones.add(transition);
				}
			}
			mImageFrame.addView(cuadroTextoLayout, positionParams);
		}
    }

}
