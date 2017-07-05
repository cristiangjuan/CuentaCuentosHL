package com.android.cuentacuentoshl;


import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.cuentacuentoshl.bitmaps.BitMapUtils;
import com.android.cuentacuentoshl.customs.CustomTransitionDrawable;
import com.android.cuentacuentoshl.customs.CustomView;
import com.android.cuentacuentoshl.utils.Constants;
import com.android.cuentacuentoshl.utils.MusicManager;

import java.util.ArrayList;


public class ReadFragment extends Fragment {

    /**
     * Nº de página.
     */
    private int mImageNum;
	/**
     * Layout del marco de la página.
     */
    private RelativeLayout imageFrame;
	/**
	 * Vista de la imagen
	 */
	private CustomView mImageView;
	/**
	 * Array con las animaciones de los cuadros de texto.
	 */
	//private ArrayList<CustomScaleAnimationSubs> arrayAnimaciones;
	/**
	 * Array con las animaciones(transiciones) de los cuadros de texto.
	 */
	private ArrayList<CustomTransitionDrawable> arrayHighlights;
	/**
	 * Tiempo de auutoplay del fragmento
	 */
	private int mTiempo;
	/**
	 * Id de la imagen.
	 */
	private int resId;
	/**
	 * Modo de lectura: Autoplay o manual. Definido en clase Constants.
	 */
	private int mode;
	
	/**
	 * Gestiona la voz asociada al fragmento
	 */
	private MediaPlayer mediaPlayerVoice;
    
    
    public static ReadFragment newInstance(int imageNum) {
    	Log.v(Constants.Log.METHOD, "ReadFragment - newInstance");
        final ReadFragment f = new ReadFragment();
        final Bundle args = new Bundle();
        args.putInt(Constants.IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);
        return f;
    }
    
    // Empty constructor, required as per Fragment docs
    public ReadFragment() {}
	
    /**
     * Devuelve el nº de página asociada al fragmento.
     * @return
     */
    public int getPage() {
		return mImageNum;
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.v(Constants.Log.METHOD, "ReadFragment - OnCreate");
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(Constants.IMAGE_DATA_EXTRA) : -1;
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	Log.v(Constants.Log.METHOD, "ReadFragment - OnCreateView");
    	
        imageFrame = (RelativeLayout) inflater.inflate(R.layout.fragment_read, container, false);
        mImageView = (CustomView) imageFrame.findViewById(R.id.slide_imageView);
        
        return imageFrame;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	Log.v(Constants.Log.METHOD, "ReadFragment - onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        
        if (ReadActivity.class.isInstance(getActivity())){

            mode = Constants.MODE_HIGHLIGHT;

            //Según la actividad cargamos el tipo de imagen: Con o sin cuadros de texto (Autoplay)
            if (mode == Constants.MODE_HIGHLIGHT) {

                resId = ReadActivity.pages_ct[mImageNum];
            }
            else {

                resId = ReadActivity.pages[mImageNum];
            }


            DisplayMetrics metrics = new DisplayMetrics();
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            display.getMetrics(metrics);
            Point realSize = new Point();
            display.getRealSize(realSize);

            Log.v(Constants.Log.SIZE, "Screen pixels: "+metrics.widthPixels+" x "+metrics.heightPixels);
            Log.v(Constants.Log.SIZE, "Screen ratio = "+(float) metrics.widthPixels
                / (float)metrics.heightPixels);
            Log.v(Constants.Log.SIZE, "Screen dpi: xdpi = "+metrics.xdpi+"  ydpi = "+metrics.ydpi);
            Log.v(Constants.Log.SIZE, "Screen density = "+metrics.density);
            Log.v(Constants.Log.SIZE, "Screen density dpi = "+metrics.densityDpi);
            Log.w(Constants.Log.SIZE, "Screen reference = "+metrics.widthPixels / metrics.density
                          +" x "+metrics.heightPixels / metrics.density );
            Log.v(Constants.Log.SIZE, "Screen Real pixels = "+realSize.toString());
            Log.v(Constants.Log.SIZE, "Screen Real ratio = "+(float) realSize.x / (float) realSize.y);
            Log.w(Constants.Log.SIZE, "Screen Real reference = "+realSize.x / metrics.density
                +" x "+realSize.y / metrics.density );

            //Obtenemos el tiempo de autoplay del fragmento
            mTiempo = ((ReadActivity) getActivity()).getTiempoAutoPlay(mImageNum);


            //Cargamos la imagen principal de la página.
            if (ReadActivity.isStorageNeeded()) {

            ((ReadActivity) getActivity()).loadSavedPageBitmap(
                ReadActivity.pages_names[mImageNum], mImageView);
            }
            else {

                if (ReadActivity.isFrameSizeSet()) {
                    /*
                    * En el primer fragmento una vez se ha procesado el layout
                    * calculamos el tamaño del marco para decodificar las imágenes.
                    * Para los demás fragmentos no esperamos a que se haya procesado el layout.
                    */
                    Log.v(Constants.Log.SIZE, "ReadFragment - FrameSizeSet = True");
                    //Calculamos el coeficiente para reescalar las imagenes y ajustarlas a la pantalla del dispositivo
                    float scale = BitMapUtils.calculateScale(new Point(ReadActivity.frameWidth, ReadActivity.frameHeight),
                      BitMapUtils.getResolution(getResources(), resId, 1));
                    Log.v(Constants.Log.SIZE, "ReadFragment - Scale: "+scale);

                    //Cargamos la imagen principal de la página.
                    ((ReadActivity) getActivity()).loadPageBitmap(resId, mImageView,
                      new Point(ReadActivity.frameWidth, ReadActivity.frameHeight), scale);

                    //Si el modo de lectura es autoplay cargamos los cuadros de texto.
                    if (mode == Constants.MODE_HIGHLIGHT) {

                        //arrayAnimaciones = new ArrayList<CustomScaleAnimationSubs>();
                        arrayHighlights = new ArrayList<CustomTransitionDrawable>();
                        ((ReadActivity) getActivity()).loadTextFrames(resId, imageFrame,
                            new Point(ReadActivity.frameWidth, ReadActivity.frameHeight),
                            arrayHighlights,
                            mImageNum);
                    }

                    //Pasamos a la vista la página en la que se encuentra.
                    mImageView.setPage(mImageNum);
                }
                else {
                    Log.v(Constants.Log.SIZE, "ReadFragment - FrameSizeSet = False");

                    ViewTreeObserver vto = imageFrame.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {

                            imageFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            Log.v(Constants.Log.SIZE, "ReadFragment - onMeasure - Width = "+imageFrame.getMeasuredWidth()+
                              " Height = "+imageFrame.getMeasuredHeight());

                            //ReadActivity.setFragmentSize(imageFrame.getMeasuredWidth(), imageFrame.getMeasuredHeight());

                            //Calculamos el coeficiente para reescalar las imagenes y ajustarlas a la pantalla del dispositivo
                            float scale = BitMapUtils.calculateScale(
                              new Point(imageFrame.getMeasuredWidth(), imageFrame.getMeasuredHeight()),
                              BitMapUtils.getResolution(getResources(), resId, 1));
                            Log.v(Constants.Log.SIZE, "ReadFragment - Scale: "+scale);

                            //Cargamos la imagen principal de la página.
                            ((ReadActivity) getActivity()).loadPageBitmap(resId, mImageView,
                              new Point(ReadActivity.frameWidth, ReadActivity.frameHeight), scale);

                            //Si el modo de lectura es autoplay cargamos los cuadros de texto.
                            if (mode == Constants.MODE_HIGHLIGHT) {

                            //arrayAnimaciones = new ArrayList<CustomScaleAnimationSubs>();
                            arrayHighlights = new ArrayList<CustomTransitionDrawable>();
                            ((ReadActivity) getActivity()).loadTextFrames(resId, imageFrame,
                                new Point(ReadActivity.frameWidth, ReadActivity.frameHeight),
                                arrayHighlights,
                                mImageNum);
                            }

                            //Pasamos a la vista la página en la que se encuentra.
                            mImageView.setPage(mImageNum);
                        }
                    });
                }
            }
        }
    }

	/**
	 * Devuelve la vista principal del fragmento. La imagen de la página.
	 * @return
	 */
	public ImageView getImageView() {
		Log.v(Constants.Log.METHOD, "ReadFragment - getImageView");
		
		return mImageView;
	}
	
	/**
	 * Devuelve el tiempo de autoplay del fragmento
	 * @return
	 */
	public int getTiempo() {
		Log.v(Constants.Log.METHOD, "ReadFragment - getTiempo");
		
		return mTiempo;
	}
	
	/**
	 * Empezamos las animaciones de los cuadros de texto.
	 * Hay que hacerlo en el fragmento para disponer del array de cuadros de texto.
	 */
	public void startAnimations() {
		Log.v(Constants.Log.METHOD, "ReadFragment - startAnimations");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                //Empezamos las animaciones
                ((ReadActivity) getActivity()).startResumeHighlights(arrayHighlights, 0);
                //Empezamos la voz
                MusicManager.startResumeVoice();
            }
            else {

                Log.e(Constants.Log.SUBS, "ReadFragment - Array de animaciones es null");
            }
        }
        else {

            //Empezamos la voz
            MusicManager.startResumeVoice();
        }

	}
	
	/**
	 * Continúa las animaciones donde se pausaron
	 * @param timeLeft
	 */
	public void resumeAnimations(int timeLeft) {
		
		Log.v(Constants.Log.METHOD, "ReadFragment - resumeAnimations");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                Log.v("Subs", "ReadFragment - mTiempo = "+mTiempo+" timeLeft = "+timeLeft);
                //Pasamos el tiempo transcurrido, es decir el tiempo total menos el que queda.
                ((ReadActivity) getActivity()).
                        startResumeHighlights(arrayHighlights,  mTiempo - timeLeft);
                //Empezamos o resumimos la voz
                MusicManager.startResumeVoice();
            }
            else {

                Log.e(Constants.Log.SUBS, "Array de animaciones es null");
            }
        }
        else {

            Log.v("Subs", "ReadFragment - mTiempo = "+mTiempo+" timeLeft = "+timeLeft);

            //Empezamos o resumimos la voz
            MusicManager.startResumeVoice();
        }

	}
	
	/**
	 * Cancela todas las animaciones, lo utilizamos al pausar el autoplay.
	 */
	public void cancelAnimations(int timeLeft) {
		
		Log.w(Constants.Log.METHOD, "ReadFragment - cancelAnimations");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                Log.v("Subs", "mTiempo = "+mTiempo+" timeLeft = "+timeLeft);
                //Le damos el tiempo a la actividad (al reiniciar y al parar)
                ((ReadActivity) getActivity()).setTiempo(mTiempo - timeLeft);
                //Cancelamos
                ((ReadActivity) getActivity()).cancelHighlights(arrayHighlights);
                //Pausamos la voz
                MusicManager.pauseVoice();
            }
            else {

                Log.e(Constants.Log.SUBS, "ReadFragment - Array de animaciones es null");
            }
        }
        else {

            Log.v("Subs", "ReadFragment - mTiempo = "+mTiempo+" timeLeft = "+timeLeft);

            //Le damos el tiempo a la actividad (al reiniciar y al parar)
            ((ReadActivity) getActivity()).setTiempo(mTiempo - timeLeft);

            //Pausamos la voz
            MusicManager.pauseVoice();
        }

	}

	/**
	 * Reinicia todas las animaciones con una animación de retorno, lo utilizamos al pulsar el botón refresh.
	 * Se utiliza junto a resetStateAnimations pero por separado.
	 */
	public void resetAnimations() {
		
		Log.w(Constants.Log.METHOD, "ReadFragment - resetAnimations");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                //Le damos el tiempo a la actividad (al reiniciar y al parar)
                ((ReadActivity) getActivity()).setTiempo(mTiempo);
                //Reseteamos
                ((ReadActivity) getActivity()).resetHighlights(arrayHighlights);
                //Reseteamos la voz
                MusicManager.createVoice(getPage());
            }
            else {

                Log.e(Constants.Log.SUBS, "ReadFragment - Array de animaciones es null");
            }
        }
        else {

            //Le damos el tiempo a la actividad (al reiniciar y al parar)
            ((ReadActivity) getActivity()).setTiempo(mTiempo);
            //Reseteamos la voz
            MusicManager.createVoice(getPage());
        }
	}
	
	/**
	 * Reinicia el estado de las animaciones, lo utilizamos al pulsar el botón refresh.
     * Se tiene que ejecutar después de que se empiecen a reiniciar las animaciones por eso lo separamos.
	 */
	public void resetStateAnimations() {
		
		Log.w(Constants.Log.METHOD, "ReadFragment - resetStateAnimations");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                ((ReadActivity) getActivity()).resetStateHighlights(arrayHighlights);
            }
            else {

                Log.e(Constants.Log.SUBS, "ReadFragment - Array de animaciones es null");
            }
        }
        else {


        }

	}
	
	/**
	 * Reinicia el estado de las animaciones y la animación en sí,
     * lo utilizamos al entrar en un página por si volviéramos después de haber estado en ella antes.
	 */
	public void resetFullHighlights() {
		
		Log.w(Constants.Log.METHOD, "ReadFragment - resetFullHighlights");

        if (mode == Constants.MODE_HIGHLIGHT) {

            if ( (arrayHighlights != null) ) {

                ((ReadActivity) getActivity()).
                        resetFullHighlights(arrayHighlights);
                //Le damos el tiempo a la actividad (al reiniciar y al parar)
                ((ReadActivity) getActivity()).setTiempo(mTiempo);
                //Reseteamos la voz
                MusicManager.createVoice(getPage());
            }
            else {

                Log.e(Constants.Log.SUBS, "ReadFragment - Array de animaciones es null");
            }
        }
        else {

            //Le damos el tiempo a la actividad (al reiniciar y al parar)
            ((ReadActivity) getActivity()).setTiempo(mTiempo);
            //Reseteamos la voz
            MusicManager.createVoice(getPage());
        }

	}
}









