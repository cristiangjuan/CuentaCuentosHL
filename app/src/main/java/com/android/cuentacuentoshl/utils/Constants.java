package com.android.cuentacuentoshl.utils;

public class Constants {

	public static final int ACTIVITY_REQUEST_CODE_AUTOPLAY = 1;
	public static final int ACTIVITY_REQUEST_CODE_LIKE = 2;
	public static final int ACTIVITY_REQUEST_CODE_INFO = 3;
	public static final int ACTIVITY_REQUEST_CODE_STANDARD = 4;
    public static final int ACTIVITY_REQUEST_CODE_MANUAL = 5;
	public static final String EXTRA_VIGNETTE_NUM = "vigNum";
	public static final int SCROLL_FACTOR = 2;
	public static final String ACTIVITY_TYPE = "activity_type";
	public static final int MODE_NO_HIGHLIGHT = 0;
	public static final int MODE_HIGHLIGHT = 1;
    public static final String LIKE_ACTIVITY_FROM = "LIKE_ACTIVITY_FROM";
	public static final int LIKE_ACTIVITY_FROM_MAIN = 1;
    public static final int LIKE_ACTIVITY_FROM_READ = 2;
	public static final int PIXELS_TO_MOVE = 50;
	public static final String IMAGE_DATA_EXTRA = "resId";
	public static final String FAKE = "";
	
	
	public class Log {
		
		public static final String METHOD = "Meth";
		public static final String SIZE = "Size";
		public static final String STORE = "Store";
		public static final String SUBS = "Subs";
		public static final String DEBUG = "Dbug";
		public static final String CONTROLS = "Ctrl";
		public static final String TOUCH = "Tuch";
		public static final String TIMER = "Time";
		public static final String SCROLL = "Scrol";
		public static final String AUDIO = "Audi";
		public static final String MEMORY = "Memr";
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con opciones generales de la app.
	 */
	public class Options {
		
		public static final boolean ZOOM = false;
    public static final boolean STORAGE = true;
		
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con la funcionalidad de autoplay
	 */
	public class Autoplay {
		
		
		public static final int PAGE_JUMP_TIME = 6000;
		public static final int PROGRESSBAR_MAX = 1000;
		public static final long PROGRESSBAR_REFRESH_INTERVAL = 10;
		public static final boolean PAUSADO_INICIO = true;
		/**
		 * Tiempo de animación de relleno de la progressBar
		 */
		public static final long PROGRESSBAR_INITIAL_TIME_ANIMATION = 500;
		public static final boolean VOLUME_INICIO = true;
		public static final boolean MUSIC_INICIO = true;
		public static final int FADE_OUT_CONTROLS_QUICK = 1500;
		public static final int FADE_OUT_CONTROLS = 3000;
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con el subtitulado o las animaciones de texto
	 */
	public class Subs {
		
		/**
		 * Retraso inicial en la animacion de subtítulos
		 */
		public static final int INITIAL_DELAY = 1500;
		/**
		 * Retraso final en la animacion de subtítulos
		 */
		public static final int FINAL_DELAY = 1500;
		/**
		 * Retraso entre medias de la animacion de subtítulos
		 */
		public static final int BETWEEN_DELAY = 250;
		/**
		 * Retraso en la animacion de subtítulos
		 */
		public static final int REVERSE_DELAY = 50;
		/**
		 * Nos indicar cuando empezará la siguiente animación siendo 1 el final.
		 */
		public static final float CF_START_OFFSET = 0.8f;
		//Coeficientes para los tamaños de las animaciones
		public static final float CF_SCALE_INICIAL = 1f;
		public static final float CF_SCALE_FINAL = 0.8f;
		public static final float CF_PIVOT_X = 50f;
		public static final float CF_PIVOT_Y = 50f;
		
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con el tamaño de la pantalla y la interfaz
	 */
	public class Size {
		
		public static final int W592dp = 592;
		public static final int W960dp = 960;
		public static final int SW600dp = 600;
		public static final int SW720dp = 720;
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con las animaciones de la aplicación
	 */
	public class Animations {
		
		/**
		 * Retraso inicial en la animacion de subtítulos
		 */
		public static final int EXTRA_RADIO_REVEAL_EFFECT_ANIM = 50;
	}
	
	/**
	 * @author quayo
	 * Constantes relacionadas con el audio de la aplicación
	 */
	public class Audio {
		
		public static final int MAX_MUSIC = 100;
		public static final int MAX_MUSIC_LIMITED = 60;
		public static final int MAX_VOICE_LIMITED = 80;
		public static final int FADE_MUSIC_TIME = 2500;
		public static final int FADE_MUSIC_TIME_RETURN = 2000;
		public static final int FADE_MUSIC_INTERVALS = 80;
		
	}
}
