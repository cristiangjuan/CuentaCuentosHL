package com.android.cuentacuentoshl.customs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.android.cuentacuentoshl.utils.Constants;


public class CustomView extends ImageView {


	private float newScaleFactor = 1.f;
	private float oldScaleFactor = 1.f;
	private Matrix matrix;
	//private Matrix initialMatrix;
	private PointF midPoint;
	private PointF startPoint;
	private PointF currentPan;
	private float oldDist;
	private int mode;
	private boolean inZoom = false;
	private static int NONE = 0;
	private static int DRAG = 1;
	private static int ZOOM = 2;
	
	private Context mContext;
	
	/**
	 * Número de página correspondiente a la vista.
	 */
	private int pageNum = 0;
	/**
	 * Gestor del evento doble tap.
	 */
	private GestureDetector gestureDetector;
	
	
	public CustomView(Context context) {
        this(context, null, 0);
    }
    
    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    
    public CustomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        Log.v(Constants.Log.METHOD, "CustomView - new()");
        
        mContext = context;
        
        //Inicializamos el receptor del gesto doble tap
        gestureDetector = new GestureDetector(mContext, new GestureListener(this));
        
        //Inicializamos la matriz de reescalado y los valores inciales del zoom
        matrix = new Matrix();
        midPoint = new PointF();
        startPoint = new PointF();
        currentPan = new PointF(0,0);
        mode = NONE;
        oldDist = 1.f;
    	oldScaleFactor = 1.f;
        newScaleFactor = 1.f;
        
        this.setScaleType(ScaleType.MATRIX);
        this.setImageMatrix(matrix);
    }
    
    public void setPage(int p) {
    	pageNum = p;
    }
    
    
    /**
	 * Reseteamos la imagen a su escala y colocacion inicial, volviendo a la
	 * matriz inicial. 
	 */
	public void resetMatrix() {
		
		Log.v(Constants.Log.TOUCH, "CustomView - resetMatrix");
		
		currentPan.set(0, 0);
		oldDist = 1.f;
    	oldScaleFactor = 1.f;
        newScaleFactor = 1.f;
		inZoom = false;
		matrix.reset();
		
		this.setImageMatrix(matrix);
		
		invalidate();
	}
    
	/*
    public ScaleGestureDetector getScaleDetector() {
		return mScaleDetector;
	}
	*/
   
	public boolean isInZoom() {
		
		Log.v(Constants.Log.TOUCH, "CustomView - isInZoom");
		
		return inZoom;
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		Log.v(Constants.Log.TOUCH, "CustomView - onTouchEvent");
		
		myOnTouch(event);
		gestureDetector.onTouchEvent(event);
		//super.onTouchEvent(event); 
		//Log.v(Constants.Log.TOUCH, "CustomView - onTouchEvent returns: "+b);
		return true;
	}

	/**
	 * Recoge cualquier gesto excepto el doble tap
	 * @param event
	 * @return
	 */
	public boolean myOnTouch(MotionEvent event){
    	
		Log.v(Constants.Log.TOUCH, "CustomView - myOnTouch");
		
	    switch (event.getAction() & MotionEvent.ACTION_MASK) {
	    
	    
	    case MotionEvent.ACTION_DOWN:
	    	
	    	mode = DRAG;
	    	Log.v(Constants.Log.TOUCH, "mode=DRAG");
	    	startPoint.set(event.getX(), event.getY());
	    	
	    	break;
	    
	    case MotionEvent.ACTION_POINTER_DOWN:
	    	
	    	//Calculamos el punto intermedio en el Pinchzoom
    		midPoint(midPoint, event);
    		oldDist = spacing(event);
    		
    		if ( oldDist > 10f) {
    			
    			mode = ZOOM;
    			Log.v(Constants.Log.TOUCH, "mode=ZOOM");
    		}
            break;
	    case MotionEvent.ACTION_UP:
	    case MotionEvent.ACTION_POINTER_UP:
	        mode = NONE;
	        Log.v(Constants.Log.TOUCH, "mode=NONE");
	        break;
	    
		case MotionEvent.ACTION_MOVE:
		
			Log.v(Constants.Log.TOUCH, "CustomView - myOnTouch ACTION_MOVE");
			
			if (mode == DRAG) {
				
				Log.v(Constants.Log.TOUCH, "mode=DRAG");
				
				currentPan.x += event.getX() - startPoint.x;
			    currentPan.y += event.getY() - startPoint.y;
			    //Log.v("Scale","Draggin' "+currentPan.x+","+currentPan.y);
			    startPoint.set(event.getX(), event.getY());

			    doPanAndZoom();
			} 
			else if( mode == ZOOM) {

				Log.v(Constants.Log.TOUCH, "mode=ZOOM");
				
				float newDist = spacing(event);
				
				if (newDist > 10f) {
					
					float scale = newDist / oldDist;
					oldDist = newDist;
					oldScaleFactor = newScaleFactor;
		    		newScaleFactor *= scale;
		    		Log.v(Constants.Log.TOUCH,"Pure Scale: "+newScaleFactor+" OldDist: "+oldDist+" NewDist: "+newDist);
		            
		            // Limitamos coeficiente del zoom.
		            newScaleFactor = Math.max(1.f, Math.min(newScaleFactor, 4.0f));
		            
		            if (newScaleFactor == 1.f){
		            	inZoom=false;
		            }
		            else {
		            	inZoom=true;
		            }
					
					float width = this.getWidth();
				    float height = this.getHeight();
				    float oldScaledWidth = width * oldScaleFactor;
				    float oldScaledHeight = height * oldScaleFactor;
				    float newScaledWidth = width * newScaleFactor;
				    float newScaledHeight = height * newScaleFactor;
				    
				    float reqXPos = (midPoint.x - currentPan.x) / oldScaledWidth;
			        float reqYPos = (midPoint.y - currentPan.y) / oldScaledHeight;
			        float actualXPos = (midPoint.x - currentPan.x) / newScaledWidth;
			        float actualYPos = (midPoint.y - currentPan.y) / newScaledHeight;
			        currentPan.x += (actualXPos - reqXPos) * newScaledWidth;
			        currentPan.y += (actualYPos - reqYPos) * newScaledHeight;
					
			        doPanAndZoom();
				}
			}
			
            break;
		}
	    
	    invalidate();
	    
    	return true;
    }

	/**
	 * Limitamos el pan y ajustamos el tamaño de la imagen a la ventana
	 */
	private void doPanAndZoom() {
		
		Log.v(Constants.Log.TOUCH, "CustomView - doPanAndZoom");

		float maxPanX = this.getWidth() * (newScaleFactor - 1f) ;
		float maxPanY = this.getHeight() * (newScaleFactor - 1f) ;
		
		currentPan.x = Math.max(-maxPanX, Math.min(0, currentPan.x));
		currentPan.y = Math.max(-maxPanY, Math.min(0, currentPan.y));
		
		Bitmap bm = ( (BitmapDrawable) this.getDrawable()).getBitmap();
		
		float bmWidth = bm.getWidth();
        float bmHeight = bm.getHeight();

        float fitToWindow = Math.min(this.getWidth() / bmWidth, this.getHeight() / bmHeight);
        float xOffset = (this.getWidth() - bmWidth * fitToWindow) * 0.5f * newScaleFactor;
        float yOffset = (this.getHeight() - bmHeight * fitToWindow) * 0.5f * newScaleFactor;

        matrix.reset();
        Log.v(Constants.Log.TOUCH,"Scale: "+newScaleFactor+" Fit: "+fitToWindow+" Total: "+newScaleFactor * fitToWindow);
        Log.v(Constants.Log.TOUCH,"PanX: "+currentPan.x+" XOffset: "+xOffset+" Total: "+ (currentPan.x + xOffset));
        Log.v(Constants.Log.TOUCH,"PanY: "+currentPan.y+" XOffset: "+yOffset+" Total: "+ (currentPan.y + yOffset));
        matrix.postScale(newScaleFactor * fitToWindow, newScaleFactor * fitToWindow);
		matrix.postTranslate(currentPan.x + xOffset, currentPan.y + yOffset);

		this.setImageMatrix(matrix);
	}
	
	//Calcula el punto intermedio en el Pinchzoom
	private void midPoint(PointF point, MotionEvent event) {
	    
	    float x = event.getX(0) + event.getX(1);
	    float y = event.getY(0) + event.getY(1);
	    point.set(x / 2, y / 2);
	}
	
	// Determine the space between the first two fingers 
	private float spacing(MotionEvent event) {

	    float x = event.getX(0) - event.getX(1);
	    float y = event.getY(0) - event.getY(1);
	    return (float) Math.sqrt(x * x + y * y);
	}
    
	/**
     * Controlamos con el singletap que aparezacan los controles en pantalla.
     * Con el doubletap regresamos la matriz aumentada por el zoom a su posición
     * original.
     * 
     * @author cgj
     *
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		private CustomView view;
		private Point screenResolution;
    	
    	public GestureListener(CustomView v) {
    		super();
    
    		view = v;
    		DisplayMetrics metrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			Log.v(Constants.Log.SIZE, "CustomView - Screen: "+metrics.widthPixels+" x "+metrics.heightPixels);
			screenResolution = new Point(metrics.widthPixels, metrics.heightPixels);
    	}
    	
    	/*
		@Override
        public boolean onDown(MotionEvent e) {
			
			if (view.getScaleDetector().isInProgress()) 
				return false;
			else return true;
        }
        */
    	
    	@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			Log.v(Constants.Log.TOUCH, "CustomView - onSingleTapConfirmed");
			//((ScreenSlidePagerActivityManual) mContext).showHideControls(false);
			return super.onSingleTapConfirmed(e);
		}
    	
		
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	
        	Log.v(Constants.Log.TOUCH, "CustomView - onDoubleTap");
        	
        	//Comprobamos si la imagen está aumentada o no
        	if (view.isInZoom()){
        	
        		view.resetMatrix();
        	}
        	
			return super.onDoubleTap(e);
        }
    }
	
	/**
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        
    	
		@Override
        public boolean onScale(ScaleGestureDetector detector) {
            
    		oldScaleFactor = newScaleFactor;
    		newScaleFactor *= detector.getScaleFactor();
    		 Log.v("ScaleZoom","Pure Scale: "+newScaleFactor);
            
            // Limitamos coeficiente del zoom.
            newScaleFactor = Math.max(1.f, Math.min(newScaleFactor, 3.0f));
            
            if (newScaleFactor == 1.f){
            	inZoom=false;
            }
            else {
            	inZoom=true;
            }

            return true;
        }
    }
    */
}
