package com.android.cuentacuentoshl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.cuentacuentoshl.bitmaps.BitMapUtils;
import com.android.cuentacuentoshl.bitmaps.BitmapLoadStoragedPageTask;
import com.android.cuentacuentoshl.bitmaps.BitmapPageTask;
import com.android.cuentacuentoshl.bitmaps.BitmapSavePageTask;
import com.android.cuentacuentoshl.bitmaps.BitmapTextTask;
import com.android.cuentacuentoshl.customs.CustomPagerAdapter;
import com.android.cuentacuentoshl.customs.CustomScaleAnimationSubs;
import com.android.cuentacuentoshl.customs.CustomTransitionDrawable;
import com.android.cuentacuentoshl.customs.CustomView;
import com.android.cuentacuentoshl.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;


public abstract class ReadActivity extends FragmentActivity {

	/**
	 * Ancho del marco que engloba la imagen
	 */
	public static int frameWidth;
	/**
	 * Alto del marco que engloba la imagen
	 */
	public static int frameHeight;
	/**
	 * Flag que nos indica si conocemos el tamaño del marco
	 */
	protected static boolean frameSizeSet = false;
	/**
	 * Flag que nos indica si es necesario reescalar las páginas y almacenarlas
	 */
	protected static boolean storageNeeded = false;
	
	
	/**
	 * Array con las referencias a las páginas del cuento
	 */
	protected final static Integer[] pages = new Integer[] {
	    
		R.drawable.vic_00001, R.drawable.vic_00002,
		R.drawable.vic_00003, R.drawable.vic_00004,
		R.drawable.vic_00005, R.drawable.vic_00006,
		R.drawable.vic_00007
	};

    protected final static String[] pages_names = new String[] {

            "vic_00001","vic_00002",
            "vic_00003","vic_00004",
            "vic_00005","vic_00006",
            "vic_00007"
    };

	/**
	 * Array con las referencias a los audios con la voces
	 */
	protected final static Integer[] voices = new Integer[] {
	    
		R.raw.voice_001_test
	};
	
	
	/**
	 * Array con las referencias a las páginas del cuento sin cuadros de texto
	 */
	protected final static Integer[] pages_ct = new Integer[] {
	    
		R.drawable.vic_00001_ct, R.drawable.vic_00002_ct,
		R.drawable.vic_00003_ct,R.drawable.vic_00004_ct,
		R.drawable.vic_00005_ct,R.drawable.vic_00006_ct,
		R.drawable.vic_00007_ct
	};
	
	/**
	 * Cuadro de texto compuesto de líneas que contienen palabras.
	 */
	protected static int[][] cuadroTexto_001_01 = {
		
		{R.drawable.vic_001_ct001_l001_w001_alpha, R.drawable.vic_001_ct001_l001_w002_alpha, R.drawable.vic_001_ct001_l001_w003_alpha, R.drawable.vic_001_ct001_l001_w004_alpha},
		{R.drawable.vic_001_ct001_l002_w001_alpha, R.drawable.vic_001_ct001_l002_w002_alpha, R.drawable.vic_001_ct001_l002_w003_alpha, R.drawable.vic_001_ct001_l002_w004_alpha},
		{R.drawable.vic_001_ct001_l003_w001_alpha, R.drawable.vic_001_ct001_l003_w002_alpha, R.drawable.vic_001_ct001_l003_w003_alpha, R.drawable.vic_001_ct001_l003_w004_alpha}
	};
	
	/**
	 * Cuadro de texto compuesto de líneas que contienen palabras.
	 */
	protected static int[][] cuadroTexto_001_01_light = {
		
		{R.drawable.vic_001_ct001_l001_w001_light_alpha, R.drawable.vic_001_ct001_l001_w002_light_alpha, R.drawable.vic_001_ct001_l001_w003_light_alpha, R.drawable.vic_001_ct001_l001_w004_light_alpha},
		{R.drawable.vic_001_ct001_l002_w001_light_alpha, R.drawable.vic_001_ct001_l002_w002_light_alpha, R.drawable.vic_001_ct001_l002_w003_light_alpha, R.drawable.vic_001_ct001_l002_w004_light_alpha},
		{R.drawable.vic_001_ct001_l003_w001_light_alpha, R.drawable.vic_001_ct001_l003_w002_light_alpha, R.drawable.vic_001_ct001_l003_w003_light_alpha, R.drawable.vic_001_ct001_l003_w004_light_alpha}
	};
	
	/**
	 * Matriz que contiene los cuadros de texto de una página.
	 */
	protected static int[][][] matrizCT_001 = {
		
		cuadroTexto_001_01
	};
	
	/**
	 * Matriz que contiene los cuadros de texto de una página.
	 */
	protected static int[][][] matrizCT_001_light = {
		
		cuadroTexto_001_01_light
	};
	
	/**
	 * Contiene las posiciones en píxeles de un cuadro de texto.
	 */
	protected static Point[] arrayPositionsCT_001 = {
		
		new Point(1606, 626)
	};
	
	/**
	 * Contiene los tiempos de cada línea dentro de un cuadro de texto
	 */
	protected static int[][][] arrayTimesCT_001 = {
		
		//Como tiene mas de || dos años, ya hace || cosas de niño grande
		//La vampira estaba muy || emocionada pues la noche || de las brujas se|| acercaba
		{{250,500,350,400},{750,175,175,300},{200,200,400,150}} 
	};
	
	protected static int[][] cuadroTexto_002_01 = {
		
		{R.drawable.vic_002_ct001_l001_w001, R.drawable.vic_002_ct001_l001_w002, R.drawable.vic_002_ct001_l001_w003}
	};
	
	protected static int[][] cuadroTexto_002_01_light = {
		
		{R.drawable.vic_002_ct001_l001_w001_light_purple, R.drawable.vic_002_ct001_l001_w002_light_purple, R.drawable.vic_002_ct001_l001_w003_light_purple}
	};
	
	protected static int[][] cuadroTexto_002_02 = {
		
		{R.drawable.vic_002_ct002_l001_w001}
	};
	
	protected static int[][] cuadroTexto_002_02_light = {
		
		{R.drawable.vic_002_ct002_l001_w001_light_purple}
	};
	
	protected static int[][] cuadroTexto_002_03 = {
		
		{R.drawable.vic_002_ct003_l001_w001, R.drawable.vic_002_ct003_l001_w002}
	};
	
	protected static int[][] cuadroTexto_002_03_light = {
		
		{R.drawable.vic_002_ct003_l001_w001_light_purple, R.drawable.vic_002_ct003_l001_w002_light_purple}
	};
	
	protected static int[][] cuadroTexto_002_04 = {
		
		{R.drawable.vic_002_ct004_l001_w001, R.drawable.vic_002_ct004_l001_w002},
		{R.drawable.vic_002_ct004_l002_w001}
	};
	
	protected static int[][] cuadroTexto_002_04_light = {
		
		{R.drawable.vic_002_ct004_l001_w001_light_blue2, R.drawable.vic_002_ct004_l001_w002_light_blue2},
		{R.drawable.vic_002_ct004_l002_w001_light_blue2}
	};
	
	protected static int[][] cuadroTexto_002_05 = {
		
		{R.drawable.vic_002_ct005_l001_w001},
		{R.drawable.vic_002_ct005_l002_w001}
	};
	
	protected static int[][] cuadroTexto_002_05_light = {
		
		{R.drawable.vic_002_ct005_l001_w001_light},
		{R.drawable.vic_002_ct005_l002_w001_light}
	};
	
	protected static int[][] cuadroTexto_002_06 = {
		
		{R.drawable.vic_002_ct006_l001_w001,R.drawable.vic_002_ct006_l001_w002,R.drawable.vic_002_ct006_l001_w003,R.drawable.vic_002_ct006_l001_w004},
		{R.drawable.vic_002_ct006_l002_w001,R.drawable.vic_002_ct006_l002_w002,R.drawable.vic_002_ct006_l002_w003,R.drawable.vic_002_ct006_l002_w004, R.drawable.vic_002_ct006_l002_w005},
		{R.drawable.vic_002_ct006_l003_w001,R.drawable.vic_002_ct006_l003_w002,R.drawable.vic_002_ct006_l003_w003,R.drawable.vic_002_ct006_l003_w004, R.drawable.vic_002_ct006_l003_w005}
	};
	
	protected static int[][] cuadroTexto_002_06_light = {
		
		{R.drawable.vic_002_ct006_l001_w001_light,R.drawable.vic_002_ct006_l001_w002_light,R.drawable.vic_002_ct006_l001_w003_light,R.drawable.vic_002_ct006_l001_w004_light},
		{R.drawable.vic_002_ct006_l002_w001_light,R.drawable.vic_002_ct006_l002_w002_light,R.drawable.vic_002_ct006_l002_w003_light,R.drawable.vic_002_ct006_l002_w004_light, R.drawable.vic_002_ct006_l002_w005_light},
		{R.drawable.vic_002_ct006_l003_w001_light,R.drawable.vic_002_ct006_l003_w002_light,R.drawable.vic_002_ct006_l003_w003_light,R.drawable.vic_002_ct006_l003_w004_light, R.drawable.vic_002_ct006_l003_w005_light}
	};
	
	protected static int[][] cuadroTexto_002_07 = {
		
		{R.drawable.vic_002_ct007_l001_w001,R.drawable.vic_002_ct007_l001_w002,R.drawable.vic_002_ct007_l001_w003},
		{R.drawable.vic_002_ct007_l002_w001,R.drawable.vic_002_ct007_l002_w002}
	};
	
	protected static int[][] cuadroTexto_002_07_light = {
		
		{R.drawable.vic_002_ct007_l001_w001_light_pink,R.drawable.vic_002_ct007_l001_w002_light_pink,R.drawable.vic_002_ct007_l001_w003_light_pink},
		{R.drawable.vic_002_ct007_l002_w001_light_pink,R.drawable.vic_002_ct007_l002_w002_light_pink}
	};
	
	protected static int[][][] matrizCT_002 = {
		
		cuadroTexto_002_01, cuadroTexto_002_02, cuadroTexto_002_03, cuadroTexto_002_04,
		cuadroTexto_002_05, cuadroTexto_002_06, cuadroTexto_002_07
	};
	
	protected static int[][][] matrizCT_002_light = {
		
		cuadroTexto_002_01_light, cuadroTexto_002_02_light, cuadroTexto_002_03_light, cuadroTexto_002_04_light,
		cuadroTexto_002_05_light, cuadroTexto_002_06_light, cuadroTexto_002_07_light
	};
	
	protected static Point[] arrayPositionsCT_002 = {

			new Point(203, 155), new Point(161, 275), new Point(133, 419), new Point(689, 925),
			new Point(131, 1615), new Point(1611, 113), new Point(2373, 669)
	};

	protected static int[][] cuadroTexto_003_01 = {

			{R.drawable.vic_00003_ct0001_l001_w001,R.drawable.vic_00003_ct0001_l001_w002,R.drawable.vic_00003_ct0001_l001_w003,R.drawable.vic_00003_ct0001_l001_w004},
			{R.drawable.vic_00003_ct0001_l002_w001,R.drawable.vic_00003_ct0001_l002_w002,R.drawable.vic_00003_ct0001_l002_w003}
	};

	protected static int[][] cuadroTexto_003_01_light = {

			{R.drawable.vic_00003_ct0001_l001_w001_light,R.drawable.vic_00003_ct0001_l001_w002_light,R.drawable.vic_00003_ct0001_l001_w003_light,R.drawable.vic_00003_ct0001_l001_w004_light},
			{R.drawable.vic_00003_ct0001_l002_w001_light,R.drawable.vic_00003_ct0001_l002_w002_light,R.drawable.vic_00003_ct0001_l002_w003_light}
	};

	protected static int[][] cuadroTexto_003_02 = {

			{R.drawable.vic_00003_ct0002_l001_w001,R.drawable.vic_00003_ct0002_l001_w002},
			{R.drawable.vic_00003_ct0002_l002_w001,R.drawable.vic_00003_ct0002_l002_w002}
	};

	protected static int[][] cuadroTexto_003_02_light = {

			{R.drawable.vic_00003_ct0002_l001_w001_light,R.drawable.vic_00003_ct0002_l001_w002_light},
			{R.drawable.vic_00003_ct0002_l002_w001_light,R.drawable.vic_00003_ct0002_l002_w002_light}
	};

	protected static int[][] cuadroTexto_003_03 = {

			{R.drawable.vic_00003_ct0003_l001_w001,R.drawable.vic_00003_ct0003_l001_w002,R.drawable.vic_00003_ct0003_l001_w003,R.drawable.vic_00003_ct0003_l001_w004},
			{R.drawable.vic_00003_ct0003_l002_w001,R.drawable.vic_00003_ct0003_l002_w002,R.drawable.vic_00003_ct0003_l002_w003,R.drawable.vic_00003_ct0003_l002_w004},
			{R.drawable.vic_00003_ct0003_l003_w001,R.drawable.vic_00003_ct0003_l003_w002,R.drawable.vic_00003_ct0003_l003_w003,R.drawable.vic_00003_ct0003_l003_w004,R.drawable.vic_00003_ct0003_l003_w005,R.drawable.vic_00003_ct0003_l003_w006},
	};

	protected static int[][] cuadroTexto_003_03_light = {

			{R.drawable.vic_00003_ct0003_l001_w001_light,R.drawable.vic_00003_ct0003_l001_w002_light,R.drawable.vic_00003_ct0003_l001_w003_light,R.drawable.vic_00003_ct0003_l001_w004_light},
			{R.drawable.vic_00003_ct0003_l002_w001_light,R.drawable.vic_00003_ct0003_l002_w002_light,R.drawable.vic_00003_ct0003_l002_w003_light,R.drawable.vic_00003_ct0003_l002_w004_light},
			{R.drawable.vic_00003_ct0003_l003_w001_light,R.drawable.vic_00003_ct0003_l003_w002_light,R.drawable.vic_00003_ct0003_l003_w003_light,R.drawable.vic_00003_ct0003_l003_w004_light,R.drawable.vic_00003_ct0003_l003_w005_light,R.drawable.vic_00003_ct0003_l003_w006_light},
	};

	protected static int[][] cuadroTexto_003_04 = {

			{R.drawable.vic_00003_ct0004_l001_w001}
	};

	protected static int[][] cuadroTexto_003_04_light = {

			{R.drawable.vic_00003_ct0004_l001_w001_light}
	};

	protected static int[][] cuadroTexto_003_05 = {

			{R.drawable.vic_00003_ct0005_l001_w001,R.drawable.vic_00003_ct0005_l001_w002}
	};

	protected static int[][] cuadroTexto_003_05_light = {

			{R.drawable.vic_00003_ct0005_l001_w001_light,R.drawable.vic_00003_ct0005_l001_w002_light}
	};

	protected static int[][] cuadroTexto_003_06 = {

			{R.drawable.vic_00003_ct0006_l001_w001,R.drawable.vic_00003_ct0006_l001_w002}
	};

	protected static int[][] cuadroTexto_003_06_light = {

			{R.drawable.vic_00003_ct0006_l001_w001_light,R.drawable.vic_00003_ct0006_l001_w002_light}
	};

	protected static int[][][] matrizCT_003 = {

			cuadroTexto_003_01,cuadroTexto_003_02,cuadroTexto_003_03,
			cuadroTexto_003_04,cuadroTexto_003_05,cuadroTexto_003_06
	};

	protected static int[][][] matrizCT_003_light = {

			cuadroTexto_003_01_light,cuadroTexto_003_02_light,cuadroTexto_003_03_light,
			cuadroTexto_003_04_light,cuadroTexto_003_05_light,cuadroTexto_003_06_light
	};

	protected static Point[] arrayPositionsCT_003 = {

			new Point(165,100), new Point(243,393), new Point(1688,82),
			new Point(1785,1575), new Point(1700,1675), new Point(1800,1760)
	};
	
	protected static int[][][] arrayTimesCT_002 = {
		{{400,800,400}}, //Ya sube al
		{{800}}, //tobogán
		{{400,600}}, //sin ayuda...
		{{600,400},{800}}, // Monta en || triciclo
		{{600},{600}}, //Come solo
		{{800,400,800,400},{600,400,400,400,600},{600,400,400,400,600}}, //Incluso ha aprendido a || hacer pis en el váter || Vicen se cree muy mayor
		{{400,400,600,400},{400,400}} //Soy muy mayor y || molo mucho
	};

	protected static int[][][] arrayTimesCT_003 = {
			{{400,400,200,400},{400,200,600}}, // Pero aunque ya hace || cosas de mayores
			{{400,400},{200,600}}, // ¡Vicen duerme || con chupete!
			{{400,400,400,200},{200,800,200,600},{400,200,400,200,200,800}}, // Cuando eres mayor, ya || no necesitas el chupete... || dentro de poco te lo quitarán.
			{{400}}, // ¡Quiero
			{{400,200}}, // dormir con
			{{200,400}}, // el tete!
	};
	
	protected static int [][][][] matrizCT = {
		
		matrizCT_001, matrizCT_002, matrizCT_003
	};
	
	protected static int [][][][] matrizCT_light = {
		
		matrizCT_001_light, matrizCT_002_light, matrizCT_003_light
	};
	
	protected static Point [][] matrizPositionsCT = {
		
		arrayPositionsCT_001, arrayPositionsCT_002, arrayPositionsCT_003
	};
	
	protected static int [][][][] matrizTiemposCT = {
		
		arrayTimesCT_001, arrayTimesCT_002, arrayTimesCT_003
	};

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    protected CustomPagerAdapter mAdapter;
    
    /**
     * Texto nº de página 1
     */
    protected TextView numPagTextView;
    
    /**
     * Texto nº de página 2
     */
    protected TextView numPagTextView_2;
    
    protected ImageButton close_btn;
    
    //Animaciones
    protected Animation animFadeOutLong;
	protected AnimationSet showPlay;
    protected ScaleAnimation showProgressBar;
	protected AnimationSet clickPlay;
	protected AnimationSet clickPlayForPause;
    protected Animation clickProgressBar;
    protected AnimationSet hideCloseForReveal;
    protected AnimationSet hideCloseBL;
    protected AnimationSet hideLikeForLikeTransition;
    protected AnimationSet clickRefresh;
    protected AnimationSet clickVoice;
    protected AnimationSet clickMusic;
    protected Animation showBottomControls;
    protected AnimationSet hideBottomControls;
    protected TranslateAnimation showTopLeftControls;
    protected TranslateAnimation hideTopLeftControls;
    protected TranslateAnimation showTopRightControls;
    protected TranslateAnimation hideTopRightControls;
	protected AnimationSet hidePlay;
	protected ScaleAnimation hideProgressBar;
    protected Animation hideClose;
    protected Animation hideMusic;
    protected Animation hideVoice;
	protected Animation hideHighlight;
	protected Animation hideHighlightClose;
    protected Animation hideHighlightBackgr;
    protected AnimationSet hideHighlightIcon;
    protected AnimationSet hideHighlightIconClose;
    protected AnimationSet hideHighlight_1;
    protected AnimationSet hideHighlight_2;
	protected AnimationSet hideHighlight_1_long;
	protected AnimationSet hideHighlight_2_long;
    protected AnimationSet hideRefresh;
	protected AnimationSet hideRefreshForRestart;
    protected Animation showClose;
    protected Animation showMusic;
    protected Animation showVoice;
	protected Animation showHighlight;
	protected Animation showHighlightBackgr;
    protected AnimationSet showHighlightIcon;
    protected AnimationSet showHighlightClose;
	protected AnimationSet showHighlight_1;
    protected AnimationSet showHighlight_2;
    protected AnimationSet showRefresh;
	protected AnimationSet showLikeSet;
    protected AnimationSet hideLike;
    protected AnimationSet hideLikeForRestartBL;
    protected ScaleAnimation heartBeatAutoplayEnd;
    protected Animation autoplayEndReturn;
	protected AnimationSet preRestartTransition;
	protected AnimationSet hidePlayForLikeTransition;
    protected ScaleAnimation showPlayRestart;
    protected ScaleAnimation showPlayRestartBL;
    protected TranslateAnimation pagerInTransition;
    protected TranslateAnimation pagerInTransitionIntro;
	protected TranslateAnimation pagerOutTransition;
    protected TranslateAnimation pagerOutTransitionExit;
	protected TranslateAnimation pagerOutTransitionLike;
    
    /**
   	 * Es un temporizador que dispara el desvanecimiento de los controles. 
   	 */
   	protected Timer fadeOutTimer;
   	/**
   	 * Tarea que dispasar el temporizador anterior. Realizará el desvanecimiento de los controles.
   	 */
   	protected TimerTask fadeOutTimerTask;
   	/**
   	 * Manejador para el hilo del temporizador de desvanecimiento de controles.
   	 */
   	protected Handler mHandler = new Handler();

    /**
     * Layout de los controles medios
     */
    protected FrameLayout controlsLayout;
    /**
     * Layout para el efecto reveal
     */
    protected FrameLayout revealLayout;
    /**
     * Layout general
     */
    protected RelativeLayout mainLayout;
    /**
     * Layout de barra de color inferior
     */
    protected RelativeLayout colorBarLayout;
    
    /**
     * Layout de controles inferiores
     */
    protected FrameLayout controls_bottom;

	/**
	 * Layout de controles superiores derecha
	 */
	protected FrameLayout controls_top_left;
    /**
     * Layout de controles superiores derecha
     */
    protected FrameLayout controls_top_right;

    /**
     * Controla que solo mostremos los controles al venir de un onPause(). 
     */
    protected boolean resumeShowControls = false;
  
    /**
     * Controla si los controles están visibles
     */
    protected boolean areControlsVisible = false;
	/**
     * Contexto
     */
	protected Context mContext;
	/**
     * Vista para gestionar los cambios de visibilidad de las barras de status y nav
     */
	protected View mDecorView;
	/**
     * Botón de pausa/play
     */
	protected ImageButton play_btn;
	/**
     * Botón de audio
     */
	protected ImageButton music_btn;
	/**
     * Botón de voz
     */
	protected ImageButton voice_btn;
	/**
     * Botón de actualizar página
     */
	protected ImageButton refresh_btn;
	/**
     * Barra de progreso
     */
	protected ProgressBar progressBar;
	/**
     * Controla que la música siga sonando al terminar la actividad. En caso de volver
     * a la actividad anterior seguirá sonando.
     */
	protected boolean continue_music = false;
	/**
     * Constante que controla si el autoplay está parado o no.
     */
	protected boolean paused = Constants.Autoplay.PAUSADO_INICIO;
	/**
	 * Constante que controla hemos llegado al final del autoplay.
	 */
	protected boolean finCuento = false;
	/**
     * Constante que controla que se está reiniciando el autoplay.
     * Para no interrumpir la animación.
     */
	protected boolean btn_locked = false;
	/**
     * Constante que controla que se está reiniciando el autoplay.
     * Para no interrumpir la animación.
     */
	protected boolean tap_locked = false;
	/**
	 * Si es mayor que cero se aplica el lock
	 */
	private int lockButtons = 0;
	/**
	 * Si es mayor que cero se aplica el lock
	 */
	private int lockTap = 0;
	/**
     * Indica si el timer está al 100%. Para controlar cuando mostrar refresh_btn.
     */
	protected boolean timerLleno = true;
	/**
     * Botón de like
     */
	protected ImageButton like_btn;
	/**
     * Millis que han transcurrido del autoplay
     * Lo utilizamos para saber donde reniciar la voz al hacer onRestart
     */
	protected int tiempo;
	/**
     * Botón de anterior página
     */
	protected ImageButton arrow_left_btn;
	/**
     * Botón de siguiente página
     */
	protected ImageButton arrow_right_btn;
	/**
     * Controla que el autoplay de una pág haya terminado
     */
	protected boolean nextPagina = false;

	public void setTiempo(int tiempo) {

        Log.v(Constants.Log.METHOD, "ReadActivity - setTiempo");

        this.tiempo = tiempo;
    }

	/**
     * Indica si estamos en el estado fin de página
     * @return
     */
    public boolean isNextPagina() {

        return nextPagina;
    }

	@Override
	protected void onCreate(Bundle arg0) {

		super.onCreate(arg0);
	}

    /**
     * Decodifica, escala y almacena las páginas
     *
     */
    public static void decodeScaleAndSaveAllPages(int frameWidth, int frameHeight, Context context){

        Log.v(Constants.Log.METHOD, "ReadActivity - decodeScaleAndSaveAllPages");

		//Marcamos que se van a almacenar las páginas para que ReadFragment las recupere
		storageNeeded = true;

        Bitmap bitmap;

        for (int i=0; i<pages.length; i++) {

            File file = new File(context.getFilesDir(), pages_names[i]);
            if (file == null) {
              Log.e(Constants.Log.STORE,
                  "Error creating media file, check storage permissions: ");// e.getMessage());
              return;
            }
            //Si ya existiera no decodificamos y almacenamos
            if (!file.exists()) {

              //Calculamos el coeficiente para reescalar las imagenes y ajustarlas a la pantalla del dispositivo
              float scale = BitMapUtils.calculateScale(
                  new Point(frameWidth, frameHeight),
                  BitMapUtils.getResolution(context.getResources(), pages[i], 1));
              Log.v(Constants.Log.SIZE, "ReadActivity Page "+i+" - Scale: "+scale);

              savePageBitmap(pages[i], context, scale, pages_names[i]);
            }
        }
    }

	/**
     * Carga una imagen en la página.
     * @param resId
     * @param imageView
     */
    public void loadPageBitmap(int resId, ImageView imageView, Point frameRes, float scale) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - loadPageBitmap");
    	/*
	 	final String imageKey = String.valueOf(resId);

	    final Bitmap bitmap = getBitmapFromMemCache(imageKey);
	    if (bitmap != null) {
	    	Log.i("II", "Caché!!");
	    	imageView.setImageBitmap(bitmap);
	    } else {
	    	Log.i("II", "Not found in cache: "+imageKey);
	    	//imageView.setImageResource(R.drawable.image_placeholder);
	        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	        task.execute(resId);
	    }
	    */
    	
	 	BitmapPageTask task = new BitmapPageTask(imageView, getResources(), frameRes, scale);
        task.execute(resId);
    }

    /**
     * Carga una imagen en la página guardada previamente.
     * @param resId
     * @param imageView
     */
    public void loadSavedPageBitmap(String resId, CustomView imageView) {

        Log.v(Constants.Log.METHOD, "ReadActivity - loadSavedPageBitmap");

        BitmapLoadStoragedPageTask task = new BitmapLoadStoragedPageTask(imageView, mContext);
        task.execute(resId);
    }

    /**
     * Carga una imagen en la página.
     * @param resId
     */
    public static void savePageBitmap(int resId, Context context, float scale, String pageName) {

        Log.v(Constants.Log.METHOD, "ReadActivity - loadPageBitmap");

        BitmapSavePageTask task = new BitmapSavePageTask(context, scale, pageName);
        task.execute(resId);
    }
    
    /**
     * Carga los cuadros de texto o subtitulado.
     * Genera la vistas de los cuadros de texto y las añade al marco de la página (frame),
     * así como un array con las referencias a todas las vistas de las palabras (arrayVistas) para animarlas después.
     * @param frame Marco de la página
     * @param arrayTransiciones Array con todas las vistas o palabras de los cuadros de texto.
     * @param pageNum página en la que nos encontramos.
     */
    public void loadTextFrames (int resId, RelativeLayout frame, Point frameRes,
                                ArrayList<CustomTransitionDrawable> arrayTransiciones,
                                int pageNum) {
		
    	Log.v(Constants.Log.METHOD, "ReadActivity - loadTextFrames");
    	
    	//Protegemos de un fallo de configuración, en caso de que haya más páginas que entradas en la matriz
    	if (pageNum >= matrizCT.length ||
    			pageNum >= matrizPositionsCT.length ||
    			pageNum >= matrizTiemposCT.length) {
    		
    		Log.e(Constants.Log.SUBS, "No se pueden cargar los cuadros de texto");
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= MatrizCT.lenght = "+ matrizCT.length);
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= matrizPositionsCT.lenght = "+ matrizCT.length);
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= matrizTiemposCT.lenght = "+ matrizTiemposCT.length);
    	}
    	else {
    		
    		BitmapTextTask task = new BitmapTextTask(this, getResources(), frame, frameRes,
    				arrayTransiciones, 
    				matrizCT[pageNum], matrizCT_light[pageNum],
    				matrizTiemposCT[pageNum], matrizPositionsCT[pageNum]);
            task.execute(resId);
    	}
	}
    
    /**
     * Cancela todas las animaciones, lo utilizamos al pausar el autoplay
     * @param arrayAnimaciones array con todas las animaciones
     */
    public void cancelAnimations(ArrayList<CustomScaleAnimationSubs> arrayAnimaciones) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - cancelAnimations");
    	
    	Iterator<CustomScaleAnimationSubs> iteratorAnimaciones = arrayAnimaciones.iterator();
		
		while (iteratorAnimaciones.hasNext()) {
			
			iteratorAnimaciones.next().cancel();
		}
    }
    
    /**
     * Cancela todas las animaciones de resaltado, lo utilizamos al pausar el autoplay
     * @param arrayHighlights array con todas las animaciones
     */
    public void cancelHighlights(ArrayList<CustomTransitionDrawable> arrayHighlights) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - cancelHighlights");
    	
    	Iterator<CustomTransitionDrawable> iteratorHighlights = arrayHighlights.iterator();
		
		while (iteratorHighlights.hasNext()) {
			
			mHandler.removeCallbacks(iteratorHighlights.next());
		}
    }
    
    /**
     * Reinicia todas las animaciones de resaltado con una animación de retorno, lo utilizamos al pulsar el botón refresh.
     * @param arrayHighlights array con todas las animaciones
     */
    public void resetHighlights(ArrayList<CustomTransitionDrawable> arrayHighlights) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - resetHighlights");
    	
    	Iterator<CustomTransitionDrawable> iteratorHighlights = arrayHighlights.iterator();
		
		while (iteratorHighlights.hasNext()) {
			
			CustomTransitionDrawable transition = iteratorHighlights.next();
			
			if (transition.isFirstStepDone() && !transition.isFinished()) {
				
				mHandler.post(transition);
			}
		}
    }
    
    /**
     * Reinicia el estado de las animaciones, lo utilizamos al pulsar el botón refresh.
     * Se tiene que ejecutar después de que se empiecen a reiniciar las animaciones por eso lo separamos.
     * @param arrayHighlights array con todas las animaciones
     */
    public void resetStateHighlights(ArrayList<CustomTransitionDrawable> arrayHighlights) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - resetStateHighlights");
    	
    	Iterator<CustomTransitionDrawable> iteratorHighlights = arrayHighlights.iterator();
		
		while (iteratorHighlights.hasNext()) {
			
			iteratorHighlights.next().reset();
		}
    }
    
    /**
     * Reinicia el estado de las animaciones y la animación en sí,
     * lo utilizamos al entrar en un página por si volviéramos después de haber estado en ella antes.
     * @param arrayHighlights array con todas las animaciones
     */
    public void resetFullHighlights(ArrayList<CustomTransitionDrawable> arrayHighlights) {
    	
    	Log.w(Constants.Log.METHOD, "ReadActivity - resetFullHighlights");
    	
    	Iterator<CustomTransitionDrawable> iteratorHighlights = arrayHighlights.iterator();
		
		while (iteratorHighlights.hasNext()) {
			
			CustomTransitionDrawable transition = iteratorHighlights.next();
			
			transition.resetTransition();
			transition.reset();
		}
    }
    
    /**
     * Reanuda o empieza las animaciones
     * @param arrayAnimaciones arrayAnimaciones array con todas las animaciones
     * @param tiempoTranscurrido tiempo que ha pasado del autoplay
     */
    public void startResumeAnimations(ArrayList<CustomScaleAnimationSubs> arrayAnimaciones,
    		int tiempoTranscurrido) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - startResumeAnimations");
    	
    	Iterator<CustomScaleAnimationSubs> iteratorAnimaciones = arrayAnimaciones.iterator();
    	
    	//Variable para llevar el offset trancurrido en general
		long startTime = 0;
    	long tiempoResume = 0;
		long tiempoPalabra = 0;
    	
		while (iteratorAnimaciones.hasNext()) {
			
			CustomScaleAnimationSubs anim = iteratorAnimaciones.next();
			
			//Tiempo de la animación de la palabra
			tiempoPalabra = anim.getDuration();
			//tiempoResume seria el time offset para las animaciones que vamos a resumir
			tiempoResume = startTime - tiempoTranscurrido;

			//Log.w(Constants.Log.SUBS, "ReadActivity - startResumeAnimations - tiempoResume = "+tiempoResume);
			
			// Si es mayor entramos en las animaciones a resumir
			if (tiempoResume >= 0) {
				
				anim.setStartOffset(tiempoResume);
				//Pasarle el TransitionDrawable y que lo empiece en onStart
				anim.startAnimation();
			}
			
			//Actualizamos el offset general
			startTime += tiempoPalabra * Constants.Subs.CF_START_OFFSET;
		}
    }
    
    /**
     * Reanuda o empieza las animaciones
     * @param arrayHighlights arrayAnimaciones array con todas las animaciones
     * @param tiempoTranscurrido tiempo que ha pasado del autoplay
     */
    public void startResumeHighlights(ArrayList<CustomTransitionDrawable> arrayHighlights,
    		int tiempoTranscurrido) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - startResumeHighlights");
    	
    	Iterator<CustomTransitionDrawable> iteratorHighlights = arrayHighlights.iterator();
    	
    	//Variable para llevar el offset trancurrido en general
		long startTime = Constants.Subs.INITIAL_DELAY;
    	long tiempoResume = 0;
		long tiempoPalabra = 0;
		int index = 0;
    	
		while (iteratorHighlights.hasNext()) {
			
			CustomTransitionDrawable transition = iteratorHighlights.next();
			
			//Tiempo de la animación de la palabra
			tiempoPalabra = transition.getDuration();
			//tiempoResume seria el time offset para las animaciones que vamos a resumir
			tiempoResume = startTime - tiempoTranscurrido;
			
			Log.w(Constants.Log.SUBS, "ReadActivity - startResumeHighlights - tiempoTranscurrido = "+tiempoTranscurrido);
			Log.w(Constants.Log.SUBS, "startResumeHighlights - Animacion "+(++index)+" = "+tiempoResume);
			Log.w(Constants.Log.SUBS, "startResumeHighlights - Animacion "+(index)+" Retorno = "
					+(tiempoPalabra+Constants.Subs.BETWEEN_DELAY)+" ReadActivity");
			
			
			//Comprobamos en que estado está la animación
			if (!transition.isFinished()) {
				
				if (!transition.isFirstStepDone()) {
					
					mHandler.postDelayed(transition, tiempoResume);
					mHandler.postDelayed(transition, tiempoResume+tiempoPalabra+Constants.Subs.BETWEEN_DELAY);
				} 
				//No se ha producido la anim retorno por lo que la planificamos
				else {
					
					mHandler.postDelayed(transition, tiempoResume+tiempoPalabra+Constants.Subs.BETWEEN_DELAY);
				}
			}
			
			//Actualizamos el offset general
			startTime += tiempoPalabra;
		}
    }
    
    /**
     * Calcula el tiempo de autoplay de una página en base a los tiempos introducidos en configuración.
     * @param pageNum Nº de página.
     * @return
     */
    public int getTiempoAutoPlay(int pageNum) {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivity - getTiempoAutoPlay");
		
    	//Protegemos de un fallo de configuración, en caso de que haya más páginas que entradas en la matriz
    	if (pageNum >= matrizCT.length ||
    			pageNum >= matrizPositionsCT.length ||
    			pageNum >= matrizTiemposCT.length) {
    		
    		Log.e(Constants.Log.SUBS, "ReadActivity - getTiempoAutoPlay - No se pueden obtener tiempo Autoplay");
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= MatrizCT.lenght = "+ matrizCT.length);
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= matrizPositionsCT.lenght = "+ matrizCT.length);
    		Log.e(Constants.Log.SUBS, "Nº pag = "+pageNum+" >= matrizTiemposCT.lenght = "+ matrizTiemposCT.length);
    		
    		return Constants.Autoplay.PAGE_JUMP_TIME;
    	}
    	else {
    	
	    	int startTime = Constants.Subs.INITIAL_DELAY;
	    	int numCT = matrizCT[pageNum].length;
	    	int numPalabras = 0;
	    	int tiempoPalabra = 0;
	    	
	    	for (int i=0; i<numCT; i++) {
	    		
	    		//Recorremos líneas
	    		for (int j=0; j<matrizCT[pageNum][i].length; j++) {
	    			
	    			numPalabras = matrizCT[pageNum][i][j].length;
		    		
					//Recorremos palabras en cuadro de texto
					for (int k=0; k<numPalabras; k++) {
						
						startTime += matrizTiemposCT[pageNum][i][j][k];
					}
	    		}
	    	}
	    	startTime += tiempoPalabra;
	    	startTime += Constants.Subs.FINAL_DELAY;
	    	return startTime;
    	}
    }

	/**
	 * Rellena los valores del tamaño del marco de la app.
	 * @param measuredWidth
	 * @param measuredHeight
	 */
	public static void setFragmentSize(int measuredWidth, int measuredHeight) {
		
		frameWidth = measuredWidth;
		frameHeight = measuredHeight;
		frameSizeSet = true;
	}
	
	/**
	 * Nos dice si el tamaño del marco está ya fijado.
	 * @return
	 */
	public static boolean isFrameSizeSet() {
		return frameSizeSet;
	}

	/**
	 * Nos dice si es necesario almacenar las páginas.
	 * @return
	 */
	public static boolean isStorageNeeded() {
		return storageNeeded;
	}


	/**
	 * Bloquea el botón de play
	 */
	public void lockTap() {

		lockTap++;
		Log.i(Constants.Log.METHOD, "ReadActivity - lockTap = "+lockTap);

		//tap_locked = true;
	}
	/**
	 * Desbloquea el botón de play
	 */
	public void unlockTap() {

		lockTap--;
		Log.i(Constants.Log.METHOD, "ReadActivity - unlockTap = "+lockTap);
		//Protegemos que no baje de cero
		if (lockTap < 0) lockTap = 0;

		//tap_locked = false;
	}

	/**
	 * Comprueba si el botón está bloqueado o no
	 * @return
	 */
	public boolean isTapLocked() {

		Log.w(Constants.Log.METHOD, "ReadActivity - isTapLocked - "+(lockTap > 0));

		return (lockTap > 0)? true : false;
		//return tap_locked;
	}

	/**
	 * Bloquea el botón de play
	 */
	public void lockButtons() {

		lockButtons++;
		Log.i(Constants.Log.METHOD, "ReadActivity - lockButtons = "+lockButtons);

		//btn_locked = true;
	}

	/**
	 * Desbloquea el botón de play
	 */
	public void unlockButtons() {

		lockButtons--;
		Log.i(Constants.Log.METHOD, "ReadActivity - unlockButtons = "+lockButtons);
		//Protegemos que no baje de cero
		if (lockButtons < 0) lockButtons = 0;

		//btn_locked = false;
	}

	/**
	 * Desbloqueo forzado
	 */
	public void unlockForced(){

		Log.i(Constants.Log.METHOD, "ReadActivity - unlockForced - btns = "+lockButtons+" - tap = "+lockTap);

		lockButtons = 0;
		lockTap = 0;
	}

	/**
	 * Comprueba si el botón está bloqueado o no
	 * @return
	 */
	public boolean areButtonsLocked() {

		Log.v(Constants.Log.METHOD, "ReadActivity - areButtonsLocked: "+(lockButtons > 0));

		return (lockButtons > 0)? true : false;
		//return btn_locked;
	}

    /**
     * Programa desbloqueo dentro de "duration" ms.
     * @param duration
     */
    protected void programUnlock(long duration) {

        Log.i(Constants.Log.METHOD, "ReadActivity - programUnlock: "+duration);

        //Programamos desbloqueo
        new Handler().postDelayed(new Runnable() {
            public void run() {

                unlockButtons();
                unlockTap();
            }
        }, duration);
    }

}
