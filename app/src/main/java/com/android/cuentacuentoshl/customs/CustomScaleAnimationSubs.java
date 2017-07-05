package com.android.cuentacuentoshl.customs;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.cuentacuentoshl.utils.Constants;


/**
 * @author quayo
 * Animación para los cuadros de texto.
 * Necesitamos crear esta clase ya que al terminar de agrandar debemos recuperar
 * el tamaño original con otra animación.
 */
public class CustomScaleAnimationSubs extends ScaleAnimation {

	private ImageView image;
	
	private static float scaleInicial = Constants.Subs.CF_SCALE_INICIAL;
	private static float scaleFinal = Constants.Subs.CF_SCALE_FINAL;

	private static float pivotX = Constants.Subs.CF_PIVOT_X;
	private static float pivotY = Constants.Subs.CF_PIVOT_Y;
	
	public CustomScaleAnimationSubs() {
		
		super(scaleInicial, scaleFinal, scaleInicial, scaleFinal, pivotX, pivotY);
		
		Log.v(Constants.Log.METHOD, "CustomScaleAnimationSubs - new()");
	}
	
	public void startAnimation() {
		
		Log.v(Constants.Log.METHOD, "CustomScaleAnimationSubs - startAnimation");
		
		//Preparamos la animación de retorno.
		this.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				
				Log.v(Constants.Log.METHOD, "CustomScaleAnimationSubs - startAnimation.onAnimationEnd ");
				
				ScaleAnimation a =
						new ScaleAnimation(scaleFinal, scaleInicial, scaleFinal, scaleInicial, pivotX, pivotY);
				a.setDuration(CustomScaleAnimationSubs.this.getDuration());
				CustomScaleAnimationSubs.this.image.startAnimation(a);
				
			}
		});
		
		image.startAnimation(this);
		
	}
	
	/**
	 * Almacenamos la referencia a la imagen que se va a animar. Y preparamos el listener
	 * para ejecutar la animación de retorno.
	 * @param image
	 */
	public void prepare(ImageView image) {
		
		Log.v(Constants.Log.METHOD, "CustomScaleAnimationSubs - prepare");
		
		this.image = image;
	}

	@Override
	public void cancel() {
		
		Log.v(Constants.Log.METHOD, "CustomScaleAnimationSubs - cancel");
		
		this.setAnimationListener(null);
		
		super.cancel();
	}
	
}
