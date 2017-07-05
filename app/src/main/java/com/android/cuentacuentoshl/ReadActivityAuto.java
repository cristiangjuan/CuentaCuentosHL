package com.android.cuentacuentoshl;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewAnimationUtils;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.PathInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.cuentacuentoshl.customs.CustomPagerAdapter;
import com.android.cuentacuentoshl.customs.CustomViewPagerAuto;
import com.android.cuentacuentoshl.utils.Constants;
import com.android.cuentacuentoshl.utils.MusicManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class ReadActivityAuto extends ReadActivity {

    /**
* The pager widget, which handles animation and allows swiping horizontally to access previous
* and next wizard steps.
*/
    protected CustomViewPagerAuto mPager;
    protected Animation hideCenterControls;
    protected Animation showCenterControls;
    /**
     * Control de gesto para esconder barras de status y navegación.
     */
    private GestureDetector gestureDetector;

    /**
     * Layout principal
     */
    private FrameLayout frameLayout;
    /**
     * Botón de resaltado
     */
    private ImageButton highlight_btn;
    /**
     * Botón de resaltado
     */
    private ImageButton highlight_btn_close;
    /**
     * Botón de resaltado
     */
    private ImageButton highlight_backgr_btn;
    /**
     * Botón de resaltado
     */
    private ImageButton highlight_btn_1;
    /**
     * Botón de resaltado
     */
    private ImageButton highlight_btn_2;

    /**
     * Indica si está desplegado el botón highlight
     */
    private boolean highlightButtonsVisible = false;
    /**
     * Lista con los tipos de highlights disponibles
     */
    private ArrayList<Drawable> listaHighlights = new ArrayList<Drawable>();
    private Drawable highlightSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	Log.v(Constants.Log.METHOD, "ReadActivityAuto - OnCreate");

		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);

        mContext = this;

        mDecorView = getWindow().getDecorView();

        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {

                boolean visible = (mDecorView.getSystemUiVisibility()
                        & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;

                boolean visibleStatus = (mDecorView.getSystemUiVisibility()
                        & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0;

                Log.w(Constants.Log.CONTROLS, "ReadActivityAuto - NavigationBar - "+visible);
                Log.w(Constants.Log.CONTROLS, "ReadActivityAuto - StatusBar - "+visibleStatus);
            }
        });

        setContentView(R.layout.activity_read);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout_slide);
        revealLayout = (FrameLayout) findViewById(R.id.revealLayout);
        controlsLayout = (FrameLayout) findViewById(R.id.controls_layout);
        controls_bottom = (FrameLayout) findViewById(R.id.controls_bottom);
        controls_bottom.setVisibility(View.INVISIBLE);
        controls_top_right = (FrameLayout) findViewById(R.id.controls_top_right);

        //Inicializamos el botón de flecha izquierda
        arrow_left_btn = (ImageButton) findViewById(R.id.arrow_left_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider outline_left_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            arrow_left_btn.setOutlineProvider(outline_left_btn);
            arrow_left_btn.setClipToOutline(true);
        }

        //Inicializamos el botón de flecha derecha
        arrow_right_btn = (ImageButton) findViewById(R.id.arrow_right_btn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider outline_right_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            arrow_right_btn.setOutlineProvider(outline_right_btn);
            arrow_right_btn.setClipToOutline(true);
        }
        //Inicializamos el botón de pausa
      	play_btn = (ImageButton) findViewById(R.id.btn_play);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Le damos forma circular
            ViewOutlineProvider outline_pause_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_play_btn);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            play_btn.setOutlineProvider(outline_pause_btn);
            play_btn.setClipToOutline(true);
        }

        //Inicializamos el botón de cerrar
        close_btn = (ImageButton) findViewById(R.id.btn_close);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            //Le damos forma circular
            ViewOutlineProvider outline_close_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            close_btn.setOutlineProvider(outline_close_btn);
            close_btn.setClipToOutline(true);
        }

        //Invisible al iniciar la app. Con los otros botones ocultamos el layout.
        close_btn.setClickable(false);
        close_btn.setVisibility(View.INVISIBLE);
		
		//Inicializamos el botón de música
        music_btn = (ImageButton) findViewById(R.id.btn_music);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_music_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            music_btn.setOutlineProvider(outline_music_btn);
            music_btn.setClipToOutline(true);
        }

		//Invisible al iniciar la app. Con los otros botones ocultamos el layout.
        music_btn.setClickable(false);
		music_btn.setVisibility(View.INVISIBLE);
		
		//Inicializamos el botón de voz
        voice_btn = (ImageButton) findViewById(R.id.btn_voice);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_voice_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            voice_btn.setOutlineProvider(outline_voice_btn);
            voice_btn.setClipToOutline(true);
        }

		//Invisible al iniciar la app. Con los otros botones ocultamos el layout.
		voice_btn.setClickable(false);
        voice_btn.setVisibility(View.INVISIBLE);

        /** Highlight button deactivated
        //Inicializamos el botón de voz
        highlight_btn = (ImageButton) findViewById(R.id.btn_highlight);
        //Invisible al iniciar la app. Con los otros botones ocultamos el layout.
        highlight_btn.setClickable(false);
        highlight_btn.setVisibility(View.INVISIBLE);

        //Inicializamos el botón de voz
        highlight_btn_close = (ImageButton) findViewById(R.id.btn_highlight_close);

        //Inicializamos el botón de voz
        highlight_backgr_btn = (ImageButton) findViewById(R.id.btn_highlight_backgr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_highlight_btn_backr = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            highlight_backgr_btn.setOutlineProvider(outline_highlight_btn_backr);
            highlight_backgr_btn.setClipToOutline(true);
        }

        //Invisible al iniciar la app. Con los otros botones ocultamos el layout.
        highlight_backgr_btn.setClickable(false);
        highlight_backgr_btn.setVisibility(View.INVISIBLE);

        //Inicializamos el botón de voz
        highlight_btn_1 = (ImageButton) findViewById(R.id.btn_highlight_1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_highlight_btn_1 = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            highlight_btn_1.setOutlineProvider(outline_highlight_btn_1);
            highlight_btn_1.setClipToOutline(true);
        }

        //Inicializamos el botón de voz
        highlight_btn_2 = (ImageButton) findViewById(R.id.btn_highlight_2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_highlight_btn_2 = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_tool_btns);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            highlight_btn_2.setOutlineProvider(outline_highlight_btn_2);
            highlight_btn_2.setClipToOutline(true);
        }
        */
		//Inicializamos el botón de actualizar página
        refresh_btn = (ImageButton) findViewById(R.id.btn_refresh);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_refresh_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_refresh_btn);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            refresh_btn.setOutlineProvider(outline_refresh_btn);
            refresh_btn.setClipToOutline(true);
        }

        //Inicializamos el botón de like
        like_btn = (ImageButton) findViewById(R.id.btn_like);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Le damos forma circular
            ViewOutlineProvider outline_like_btn = new ViewOutlineProvider() {

                @Override
                public void getOutline(View view, Outline outline) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int diameter = getResources().getDimensionPixelSize(R.dimen.diameter_divide_btn);
                        outline.setOval(0, 0, diameter, diameter);
                    }
                }
            };
            like_btn.setOutlineProvider(outline_like_btn);
            like_btn.setClipToOutline(true);
        }

      	//Inicializamos barra de progreso
      	progressBar = (ProgressBar) findViewById(R.id.progressBar);
      	progressBar.setVisibility(View.INVISIBLE);
      	//Rotamos para que la barra de progreso disminuya en el sentido de las agujas del reloj
      	progressBar.setRotationY(180);
      	//progressBar.setVisibility(View.INVISIBLE);
      	//Inicializamos el texto del nº de página
      	numPagTextView = (TextView) findViewById(R.id.tview_numPage);
      	numPagTextView_2 = (TextView) findViewById(R.id.tview_numPage_2);

        mAdapter = new CustomPagerAdapter(getSupportFragmentManager(), pages.length);
        mPager = (CustomViewPagerAuto) findViewById(R.id.slide_pager);
        mPager.setVisibility(View.INVISIBLE);
        mPager.setNumPages(pages.length);
        //mPager.setOffscreenPageLimit(2);
        mPager.setAdapter(mAdapter);
        //mPager.setPageTransformer(true, new CustomPageTransformer());
        mPager.setScrollDurationFactor(Constants.SCROLL_FACTOR);
        mPager.initControls(progressBar, numPagTextView, numPagTextView_2);  
        
        resumeShowControls = false;

        MusicManager.build(mContext);
        prepareMusicAndVoice();
        prepareOnClicks();
        prepareAnimations();
        /** Highlight button deactivated
        prepareHighlightSelection();
        */

        frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                frameLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ReadActivity.setFragmentSize( frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight());

                //Bloqueamos el botón para que no se ponga en marcha el autoplay durante la animación inicial
                lockButtons();
                //Bloqueamos el tap para que no se escondan los controles ni se pase de pag. durante la animación
                lockTap();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //Empezamos la animación inicial
                    startUnvealRead();
                }
                else {
                    //Empezamos la animación inicial antes de Lollipop
                    startShowLayoutBL();
                }
            }
        });
    }

    private void prepareOnClicks() {

        close_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick closeButton");

                if (!areButtonsLocked()) {

                    //El bloqueo de botones lo hacemos dentro de exit.
                    exit();
                }
            }
        });

        voice_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick voiceButton");

                if (!areButtonsLocked()) {

                    //Bloqueamos el botón de play y el tap
                    lockButtons();
                    lockTap();

                    //Pausamos el autoplay mostrando refresh
                    pausaAutoplayControles(false);

                    voice_btn.startAnimation(clickVoice);

                    //Programamos desbloqueo
                    programUnlock(clickVoice.getDuration());

                    //Animación transformación voice on/off
                    //On - > Off
                    if (MusicManager.isVoice_on()) {

                        AnimationDrawable frameAnimation;
                        TransitionDrawable transition;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_volume_on_off, null);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off, null);
                        }
                        else {
                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_volume_on_off);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off);
                        }

                        voice_btn.setImageDrawable(frameAnimation);
                        voice_btn.setBackground(transition);
                        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));
                        frameAnimation.start();

                        //Quitamos volumen de la voz
                        MusicManager.setVoiceVolumeOff();
                    }
                    //Play - > Pause
                    else {

                        AnimationDrawable frameAnimation;
                        TransitionDrawable transition;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_volume_off_on, null);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on, null);

                        }
                        else {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_volume_off_on);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on);
                        }

                        voice_btn.setImageDrawable(frameAnimation);
                        voice_btn.setBackground(transition);
                        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));
                        frameAnimation.start();

                        //Restablecemos volumen de la voz
                        MusicManager.setVoiceVolumeOn();
                    }
                }
            }
        });

        music_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick musicButton");

                if (!areButtonsLocked()) {

                    //Bloqueamos el botón de play y el tap
                    lockButtons();
                    lockTap();

                    //Pausamos el autoplay mostrando refresh
                    pausaAutoplayControles(false);

                    music_btn.startAnimation(clickMusic);

                    //Programamos desbloqueo
                    programUnlock(clickMusic.getDuration());

                    //animación transformación voice on/off
                    //On - > Off
                    if (MusicManager.isMusic_on()) {

                        AnimationDrawable frameAnimation;
                        TransitionDrawable transition;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_music_on_off, null);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off, null);
                        } else {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_music_on_off);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off);
                        }

                        music_btn.setImageDrawable(frameAnimation);
                        music_btn.setBackground(transition);
                        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));
                        frameAnimation.start();

                        //Quitamos volumen de la música
                        MusicManager.setMusicVolumeOff();
                    }
                    //Off - > On
                    else {

                        AnimationDrawable frameAnimation;
                        TransitionDrawable transition;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_music_off_on, null);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on, null);
                        }
                        else {

                            frameAnimation =
                                    (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_music_off_on);

                            transition =
                                    (TransitionDrawable) getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on);
                        }

                        music_btn.setImageDrawable(frameAnimation);
                        music_btn.setBackground(transition);
                        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));
                        frameAnimation.start();

                        //Restablecemos volumen de la música
                        MusicManager.setMusicVolumeOn();
                    }
                }
            }
        });

        play_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - play_btn.OnClick");

                if (!areButtonsLocked()) {

                    //Bloqueamos el botón de play y el tap para que no se interrumpa la animación de reinicio de autoplay.
                    lockButtons();
                    lockTap();

                    //En caso de haber llegado al final del autoplay, el botón tiene otra función, volver a la 1a pag.
                    if (finCuento) {

                        //Desbloqueamos después de reiniciar barra de progreso en unvealRestart

                        //Interrumpimos animación de heartbeat de autoplay y like
                        play_btn.clearAnimation();
                        like_btn.clearAnimation();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            //Reducimos elevacion para que no sobrepasen el reveal
                            play_btn.setElevation(0);
                            refresh_btn.setElevation(0);
                            like_btn.setElevation(0);
                        }

                        //Escondemos refresh boton
                        //refresh_btn.startAnimation(hideRefreshForRestart);
                        timerLleno = true;

                        //Quitamos música
                        MusicManager.fadeMusic();

                        //Antes de Lollipop los botones no los tapa el reveal asi que los escondemos
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                            play_btn.startAnimation(preRestartTransition);
                        }
                        else {

                            refresh_btn.startAnimation(hideRefresh);
                            like_btn.startAnimation(hideLikeForRestartBL);
                            mPager.startAnimation(pagerOutTransition);
                        }

                        finCuento = false;
                    }
                    //Pausa o reanudación normal
                    else {
                        //Cancelamos tarea de desvanecimiento
                        cancelDesvanecimientoControles();

                        //Paramos o reaunadamos el timer cambiando el icono.
                        //1a parte. Pone en marcha o para el autoplay.
                        //Play - > Pause
                        if (!paused) {

                            mPager.pauseTimer(false);
                            //Pausamos la música
                            MusicManager.pauseMusic();
                        }
                        //Pause - > Play
                        else {

                            mPager.resumeTimer();
                            //Resumimos la música
                            MusicManager.startResumeMusic();
                            //Renovamos el tiempo que están visibles los controles
                            planificarDesvanecimientoControles(true);

                            /** Highlight button deactivated
                             //Escondemos los botones de highlight si estuvieran visibles
                             if (highlightButtonsVisible) {

                             highlightButtonsVisible = false;

                             highlight_btn_close.startAnimation(hideHighlightIconClose);
                             highlight_btn_1.startAnimation(hideHighlight_1);
                             highlight_btn_2.startAnimation(hideHighlight_2);
                             }*/
                        }
                        //Programamos desbloqueo
                        programUnlock(clickPlay.getDuration());
                        startClickPlayAnimations();
                    }
                }
            }
        });

        refresh_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - refreshButton.OnClick");

                if (!areButtonsLocked()) {

                    //Bloqueamos los botones y el tap
                    lockButtons();
                    lockTap();

                    //En caso de haber llegado al final del autoplay, restauramos el botón normal de play
                    if (finCuento) {

                        //Pausamos la música
                        MusicManager.pauseMusic();

                        play_btn.clearAnimation();
                        refresh_btn.startAnimation(clickRefresh);
                        like_btn.startAnimation(hideLike);
                        //Reiniciamos el timer cambiando el icono.
                        setButtonIconToPlay();
                        progressBar.setVisibility(View.VISIBLE);
                        //En la última página tenemos el mediaplayer construido pero el flag a true lo bloquea
                        MusicManager.setVoice_finished(false);
                        mPager.startProgressBarInitialAnimation();
                        finCuento = false;
                    } else {

                        //Cancelamos tarea de desvanecimiento
                        cancelDesvanecimientoControles();
                        refresh_btn.startAnimation(clickRefresh);
                        //Reiniciamos el timer cambiando el icono.
                        mPager.resetProgressBarAnimation();
                    }

                    programUnlock(clickRefresh.getDuration());

                    timerLleno = true;
                }
            }
        });

        arrow_left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - arrow_left_btn.OnClick");

                if (!areButtonsLocked()) {

                    //Comprobamos que no estamos en la primera página
                    if (mPager.getCurrentItem() != 0) {

                        pageBack();
                    }
                }
            }
        });

        arrow_right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - arrow_right_btn.OnClick");

                if (!areButtonsLocked()) {

                    //Comprobamos que no estamos en la última página
                    if (mPager.getCurrentItem() != (mPager.getNumPages()-1)) {

                        pageForward();
                    }
                }
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick likeButton");

                if (!areButtonsLocked()) {


                }
            }
        });

        /** Highlight button deactivated
         highlight_btn.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick highlight_btn");

        if (!areButtonsLocked()) {

        lockButtons();
        lockTap();

        highlightButtonsVisible = true;

        pausaAutoplayControles();

        //Empezamos animaciones para mostrar las opciones de resaltado
        highlight_btn.startAnimation(hideHighlightIcon);
        highlight_btn_1.startAnimation(showHighlight_1);
        highlight_btn_2.startAnimation(showHighlight_2);
        }
        }
        });

         highlight_btn_close.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - onClick highlight_btn_close");

        if (!areButtonsLocked()) {

        lockButtons();
        lockTap();

        highlightButtonsVisible = false;

        highlight_btn_close.startAnimation(hideHighlightIconClose);
        highlight_btn_1.startAnimation(hideHighlight_1);
        highlight_btn_2.startAnimation(hideHighlight_2);
        }
        }
        });

         highlight_btn_1.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

        Log.v(Constants.Log.CONTROLS, "onClick highlight_btn_1 ReadActivityAuto");

        if (!areButtonsLocked()) {

        lockButtons();
        lockTap();

        highlightButtonsVisible = false;

        highlight_btn_close.startAnimation(hideHighlightIconClose);
        highlight_btn_1.startAnimation(hideHighlight_1);
        highlight_btn_2.startAnimation(hideHighlight_2);

        Drawable[] highlights = {highlightSelected, listaHighlights.get(0)};
        TransitionDrawable transition = new TransitionDrawable(highlights);

        highlight_backgr_btn.setBackground(transition);
        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));

        highlightSelected = listaHighlights.get(0);
        }
        }
        });

         highlight_btn_2.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

        Log.v(Constants.Log.CONTROLS, "onClick highlight_btn_2 ReadActivityAuto");

        if (!areButtonsLocked()) {

        lockButtons();
        lockTap();

        highlightButtonsVisible = false;

        highlight_btn_close.startAnimation(hideHighlightIconClose);
        highlight_btn_1.startAnimation(hideHighlight_1);
        highlight_btn_2.startAnimation(hideHighlight_2);

        Drawable[] highlights = {highlightSelected, listaHighlights.get(1)};
        TransitionDrawable transition = new TransitionDrawable(highlights);

        highlight_backgr_btn.setBackground(transition);
        transition.startTransition(getResources().getInteger(R.integer.tool_btn_color_transition));

        highlightSelected = listaHighlights.get(1);
        }
        }
        });
         */
    }

    /**
     * Configura las animaciones.
     */
    private void prepareAnimations() {

        prepareStandardAnimations();
        prepareHideShowControlsAnimations();
    }

    private void prepareStandardAnimations() {
        //Cargamos la animación de escala para el botón de pausa/play del temporizador
        clickPlay = new AnimationSet(false);

        ScaleAnimation firstClickPlayScale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        firstClickPlayScale.setDuration(getResources().getInteger(R.integer.scale_click));
        firstClickPlayScale.setRepeatMode(Animation.REVERSE);
        firstClickPlayScale.setRepeatCount(1);

        ScaleAnimation secondClickPlayScale = new ScaleAnimation(1f, 1f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        secondClickPlayScale.setDuration(getResources().getInteger(R.integer.wait_icon_animation));

        clickPlay.addAnimation(firstClickPlayScale);
        clickPlay.addAnimation(secondClickPlayScale);

        clickPlay.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickPlay.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickPlay.onAnimationEnd");

                if (!paused) setButtonIconToPause();
            }
        });

        //Cargamos la animación de escala para el botón de pausa/play del temporizador
        clickPlayForPause = new AnimationSet(false);

        ScaleAnimation firstclickPlayForPauseScale = new ScaleAnimation(1f, 1.4f, 1f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        firstclickPlayForPauseScale.setDuration(getResources().getInteger(R.integer.scale_click));
        firstclickPlayForPauseScale.setRepeatMode(Animation.REVERSE);
        firstclickPlayForPauseScale.setRepeatCount(1);

        ScaleAnimation secondclickPlayForPauseScale = new ScaleAnimation(1f, 1f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        secondclickPlayForPauseScale.setDuration(getResources().getInteger(R.integer.wait_icon_animation));

        clickPlayForPause.addAnimation(firstclickPlayForPauseScale);
        clickPlayForPause.addAnimation(secondclickPlayForPauseScale);

        clickPlayForPause.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickPlayForPause.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickPlayForPause.onAnimationEnd");

                if (paused) setButtonIconToPlay();
            }
        });

        //Cargamos la animación de escala para la barra de progreso
        clickProgressBar = new ScaleAnimation(1f, 1.4f, 1f, 1.4f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clickProgressBar.setDuration(getResources().getInteger(R.integer.scale_click));
        clickProgressBar.setRepeatMode(Animation.REVERSE);
        clickProgressBar.setRepeatCount(1);

        //Cargamos la animación de escala y rotación para el botón de actualizar
        clickRefresh = new AnimationSet(false);

        /*
        ScaleAnimation clickRefreshScale = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clickRefreshScale.setRepeatMode(Animation.REVERSE);
        clickRefreshScale.setRepeatCount(1);
        clickRefreshScale.setDuration(getResources().getInteger(R.integer.scale_click));
        clickRefreshScale.setInterpolator(AnimationUtils.loadInterpolator(
                getApplicationContext(), android.R.interpolator.linear));

        RotateAnimation clickRefreshRotate = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clickRefreshRotate.setDuration(
                getResources().getInteger(R.integer.rotateRefresh));
        clickRefreshRotate.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        */
        ScaleAnimation scaleRefreshHide = new ScaleAnimation(1, 0.2f, 1, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleRefreshHide.setDuration(getResources().getInteger(R.integer.hide_controls));

        TranslateAnimation clickTranslateRefresh = new TranslateAnimation(0 ,
                getResources().getDimension(R.dimen.margin_refresh_btn) ,0, 0);
        clickTranslateRefresh.setDuration(getResources().getInteger(R.integer.hide_controls));

        ScaleAnimation clickRefreshWait = new ScaleAnimation(1f, 1f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clickRefreshWait.setDuration(getResources().getInteger(R.integer.wait_icon_animation));

        //clickRefresh.addAnimation(clickRefreshScale);
        //clickRefresh.addAnimation(clickRefreshRotate);
        clickRefresh.addAnimation(scaleRefreshHide);
        clickRefresh.addAnimation(clickTranslateRefresh);
        clickRefresh.addAnimation(clickRefreshWait);

        clickRefresh.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickRefresh.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickRefresh.onAnimationRepeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickRefresh.onAnimationEnd");

                refresh_btn.setClickable(false);
                refresh_btn.setVisibility(View.INVISIBLE);
            }
        });

        //Cargamos la animación de escala para el botón de voz de la barra de controles
        clickVoice = new AnimationSet(false);

        ScaleAnimation firstClickVoiceScale = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        firstClickVoiceScale.setDuration(getResources().getInteger(R.integer.scale_click));
        firstClickVoiceScale.setRepeatMode(Animation.REVERSE);
        firstClickVoiceScale.setRepeatCount(1);

        ScaleAnimation secondClickVoiceScale = new ScaleAnimation(1f, 1f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        secondClickVoiceScale.setDuration(getResources().getInteger(R.integer.wait_icon_animation));

        clickVoice.addAnimation(firstClickVoiceScale);
        clickVoice.addAnimation(secondClickVoiceScale);

        clickVoice.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickVoice.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickVoice.onAnimationEnd");

                //animación transformación voice on/off
                //On - > Off
                if (MusicManager.isVoice_on()) {

                    //Quitamos volumen de la voz. Duplicado por si se interrumpe.
                    MusicManager.setVoiceVolumeOff();

                    setVoiceIconOff();
                    MusicManager.setVoice_on(false);
                }
                //Play - > Pause
                else {

                    //Quitamos volumen de la voz. Duplicado por si se interrumpe.
                    MusicManager.setVoiceVolumeOn();

                    setVoiceIconOn();
                    MusicManager.setVoice_on(true);
                }
            }
        });

        //Cargamos la animación de escala para el botón de música de la barra de controles
        clickMusic = new AnimationSet(false);

        ScaleAnimation firstClickMusicScale = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        firstClickMusicScale.setDuration(getResources().getInteger(R.integer.scale_click));
        firstClickMusicScale.setRepeatMode(Animation.REVERSE);
        firstClickMusicScale.setRepeatCount(1);

        ScaleAnimation secondClickMusicScale = new ScaleAnimation(1f, 1f, 1f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        secondClickMusicScale.setDuration(getResources().getInteger(R.integer.wait_icon_animation));

        clickMusic.addAnimation(firstClickMusicScale);
        clickMusic.addAnimation(secondClickMusicScale);

        clickMusic.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickMusic.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - clickMusic.onAnimationEnd");

                //animación transformación voice on/off
                //On - > Off
                if (MusicManager.isMusic_on()) {

                    //Quitamos volumen de la música. Duplicado por si se interrumpe.
                    MusicManager.setMusicVolumeOff();

                    setMusicIconOff();
                    MusicManager.setMusic_on(false);
                }
                //Play - > Pause
                else {

                    //Restablecemos volumen de la música. Duplicado por si se interrumpe.
                    MusicManager.setMusicVolumeOn();

                    setMusicIconOn();
                    MusicManager.setMusic_on(true);
                }
            }
        });
    }

    private void prepareHideShowControlsAnimations() {

        //Cargamos la animación de escala y rotación para el botón close al salir de la app
        hideCloseForReveal = new AnimationSet(false);

        ScaleAnimation preHideClose = new ScaleAnimation(1f, 1.3f, 1f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        preHideClose.setDuration(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            preHideClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        RotateAnimation rotateHideClose = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateHideClose.setDuration(
                getResources().getInteger(R.integer.rotateClose));

        ScaleAnimation scaleHideClose = new ScaleAnimation(1.3f, 0, 1.3f, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleHideClose.setDuration(getResources().getInteger(R.integer.hide_standard));
        scaleHideClose.setStartOffset(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            scaleHideClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        hideCloseForReveal.addAnimation(preHideClose);
        hideCloseForReveal.addAnimation(rotateHideClose);
        hideCloseForReveal.addAnimation(scaleHideClose);

        hideCloseForReveal.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideCloseForReveal.onAnimationStart");

                close_btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideCloseForReveal.onAnimationEnd");

                startRevealReadReturn();
            }
        });

        //Aparición controles centrales, playButton y progressBar
        showCenterControls = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showCenterControls.setDuration(getResources().getInteger(R.integer.show_center_controls));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showCenterControls.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        //Desvanecemos controles centrales, playButton y progressBar.
        hideCenterControls = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideCenterControls.setDuration(getResources().getInteger(R.integer.hide_controls));
        hideCenterControls.setFillAfter(true);
        hideCenterControls.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideCenterControls.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideCenterControls.onAnimationEnd");
                play_btn.setClickable(false);
                play_btn.setVisibility(View.INVISIBLE);
                refresh_btn.setClickable(false);
                refresh_btn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });

        //Cargamos la animación de aparición del botón de cerrar
        showClose = new TranslateAnimation(0, 0,
                -getResources().getDimension(R.dimen.hide_controls), 0);
        showClose.setDuration(getResources().getInteger(R.integer.hide_controls));
        showClose.setStartOffset(getResources().getInteger(R.integer.delay_show_first));
        showClose.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        //Cargamos la animación de ocultación del botón de cerrar
        hideClose = new TranslateAnimation(0, 0, 0,
                -getResources().getDimension(R.dimen.hide_controls));
        hideClose.setDuration(getResources().getInteger(R.integer.hide_controls));
        hideClose.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        hideClose.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideClose.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideClose.onAnimationEnd");

                close_btn.setClickable(false);
                close_btn.setVisibility(View.INVISIBLE);

            }
        });

        //Cargamos la animación de aparición del botón de música
        showMusic = new TranslateAnimation(0, 0,
                -getResources().getDimension(R.dimen.hide_controls), 0);
        showMusic.setDuration(getResources().getInteger(R.integer.hide_controls));
        showMusic.setStartOffset(getResources().getInteger(R.integer.delay_show_second));
        showMusic.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showMusic.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        //Cargamos la animación de ocultación de botón de música
        hideMusic = new TranslateAnimation(0, 0, 0,
                -getResources().getDimension(R.dimen.hide_controls));
        hideMusic.setDuration(getResources().getInteger(R.integer.hide_controls));
        hideMusic.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideMusic.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        hideMusic.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideMusic.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideMusic.onAnimationEnd");

                music_btn.setClickable(false);
                music_btn.setVisibility(View.INVISIBLE);
            }
        });

        //Cargamos la animación de aparición del botón de voz
        showVoice = new TranslateAnimation(0, 0,
                -getResources().getDimension(R.dimen.hide_controls), 0);
        showVoice.setDuration(getResources().getInteger(R.integer.hide_controls));
        showVoice.setStartOffset(getResources().getInteger(R.integer.delay_show_third));
        showVoice.setFillAfter(true);

        //Lo elegimos como animación para desbloquear los botones después bloquearlos al mostrar controles
        showVoice.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showVoice.onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showVoice.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        //Cargamos la animación de ocultación de botón de voz
        hideVoice = new TranslateAnimation(0, 0, 0,
                -getResources().getDimension(R.dimen.hide_controls));
        hideVoice.setDuration(getResources().getInteger(R.integer.hide_controls));
        hideVoice.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideVoice.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        hideVoice.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideVoice.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideVoice.onAnimationEnd");

                voice_btn.setClickable(false);
                voice_btn.setVisibility(View.INVISIBLE);
            }
        });

        /** Highlight button deactivated
         //Cargamos la animación de aparición del botón de voz
         showHighlight = new TranslateAnimation(0, 0,
         -getResources().getDimension(R.dimen.hide_controls), 0);
         showHighlight.setDuration(getResources().getInteger(R.integer.hide_controls));
         showHighlight.setStartOffset(getResources().getInteger(R.integer.delay_show_fourth));
         showHighlight.setFillAfter(true);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlight.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         //Cargamos la animación de aparición del botón de voz
         showHighlightBackgr = new TranslateAnimation(0, 0,
         -getResources().getDimension(R.dimen.hide_controls), 0);
         showHighlightBackgr.setDuration(getResources().getInteger(R.integer.hide_controls));
         showHighlightBackgr.setStartOffset(getResources().getInteger(R.integer.delay_show_fourth));
         showHighlightBackgr.setFillAfter(true);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlightBackgr.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         //Lo elegimos como animación para desbloquear los botones después bloquearlos al mostrar controles
         showHighlightBackgr.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showHighlightBackgr.onAnimationEnd");

        unlockButtons();
        unlockTap();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Cargamos la animación de ocultación de botón de resaltado
         hideHighlight = new TranslateAnimation(0, 0, 0,
         -getResources().getDimension(R.dimen.hide_controls));
         hideHighlight.setDuration(getResources().getInteger(R.integer.hide_controls));
         hideHighlight.setFillAfter(true);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlight.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }
         hideHighlight.setAnimationListener(new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlight.onAnimationStart");

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlight.onAnimationEnd");

        highlight_btn.setClickable(false);
        highlight_btn.setVisibility(View.INVISIBLE);
        }
        });

         //Cargamos la animación de ocultación de botón de voz
         hideHighlightClose = new TranslateAnimation(0, 0, 0,
         -getResources().getDimension(R.dimen.hide_controls));
         hideHighlightClose.setDuration(getResources().getInteger(R.integer.hide_controls));
         hideHighlightClose.setFillAfter(true);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlightClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }
         hideHighlightClose.setAnimationListener(new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightClose.onAnimationStart");

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightClose.onAnimationEnd");

        highlight_btn_close.setClickable(false);
        highlight_btn_close.setVisibility(View.INVISIBLE);
        }
        });

         //Cargamos la animación de ocultación de botón de voz
         hideHighlightBackgr = new TranslateAnimation(0, 0, 0,
         -getResources().getDimension(R.dimen.hide_controls));
         hideHighlightBackgr.setDuration(getResources().getInteger(R.integer.hide_controls));
         hideHighlightBackgr.setFillAfter(true);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlightBackgr.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }
         hideHighlightBackgr.setAnimationListener(new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightBackgr.onAnimationStart");

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightBackgr.onAnimationEnd");

        highlight_backgr_btn.setClickable(false);
        highlight_backgr_btn.setVisibility(View.INVISIBLE);
        }
        });

         hideHighlightIcon = new AnimationSet(false);

         RotateAnimation rotateHideHighlightIcon = new RotateAnimation(0, 180,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         rotateHideHighlightIcon.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }
         rotateHideHighlightIcon.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         ScaleAnimation scaleHideHighlightIcon = new ScaleAnimation(1f, 0, 1f, 0,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlightIcon.setDuration(getResources().getInteger(R.integer.change_icon_standard));
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         scaleHideHighlightIcon.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }

         hideHighlightIcon.addAnimation(rotateHideHighlightIcon);
         hideHighlightIcon.addAnimation(scaleHideHighlightIcon);

         hideHighlightIcon.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightIcon.onAnimationStart");
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightIcon.onAnimationEnd");

        highlight_btn.setClickable(false);
        highlight_btn.setVisibility(View.INVISIBLE);
        highlight_btn_close.startAnimation(showHighlightClose);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         hideHighlightIconClose = new AnimationSet(false);

         RotateAnimation rotateHideHighlightClose = new RotateAnimation(0, 180,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         rotateHideHighlightClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }
         rotateHideHighlightClose.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         ScaleAnimation scaleHideHighlightClose = new ScaleAnimation(1f, 0, 1f, 0,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlightClose.setDuration(getResources().getInteger(R.integer.change_icon_standard));
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         scaleHideHighlightClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
         }

         hideHighlightIconClose.addAnimation(rotateHideHighlightClose);
         hideHighlightIconClose.addAnimation(scaleHideHighlightClose);

         hideHighlightIconClose.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideHighlightIconClose.onAnimationEnd");

        highlight_btn_close.setClickable(false);
        highlight_btn_close.setVisibility(View.INVISIBLE);
        highlight_btn.startAnimation(showHighlightIcon);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         showHighlightIcon = new AnimationSet(true);

         ScaleAnimation scaleShowHighlightIcon = new ScaleAnimation(0f, 1f, 0f, 1f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleShowHighlightIcon.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         RotateAnimation rotateShowHighlightIcon = new RotateAnimation(180, 360,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         rotateShowHighlightIcon.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         showHighlightIcon.addAnimation(scaleShowHighlightIcon);
         showHighlightIcon.addAnimation(rotateShowHighlightIcon);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         }
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlightIcon.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         showHighlightIcon.setAnimationListener(new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showHighlightIcon.onAnimationStart");

        highlight_btn.setVisibility(View.VISIBLE);
        highlight_btn.setClickable(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showHighlightIcon.onAnimationEnd");

        }
        });

         showHighlightClose = new AnimationSet(true);

         ScaleAnimation scaleShowHighlightClose = new ScaleAnimation(0f, 1f, 0f, 1f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleShowHighlightClose.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         RotateAnimation rotateShowHighlightClose = new RotateAnimation(180, 360,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         rotateShowHighlightClose.setDuration(getResources().getInteger(R.integer.change_icon_standard));

         showHighlightClose.addAnimation(scaleShowHighlightClose);
         showHighlightClose.addAnimation(rotateShowHighlightClose);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlightClose.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         showHighlightClose.setAnimationListener(new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showHighlightClose.onAnimationStart");

        highlight_btn_close.setVisibility(View.VISIBLE);
        highlight_btn_close.setClickable(true);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showHighlightClose.onAnimationEnd");

        }
        });

         //Preparamos animación de mostrar el botón highlight option 1
         showHighlight_1 = new AnimationSet(true);

         ScaleAnimation scaleShowHighlight_1 = new ScaleAnimation(0.4f, 1, 0.4f, 1,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleShowHighlight_1.setDuration(getResources().getInteger(R.integer.show_standard));

         int verticalTranslation = (int) (getResources().getDimension(R.dimen.margin_highlight_option_top) -
         getResources().getDimension(R.dimen.margin_highlight_top));

         int horizontalTranslation = (int) (getResources().getDimension(R.dimen.margin_highlight_option_1) -
         getResources().getDimension(R.dimen.margin_tool_3));

         TranslateAnimation translateShowHighlight_1 = new TranslateAnimation(
         horizontalTranslation, 0, -verticalTranslation, 0);
         translateShowHighlight_1.setDuration(getResources().getInteger(R.integer.show_standard));

         showHighlight_1.addAnimation(scaleShowHighlight_1);
         showHighlight_1.addAnimation(translateShowHighlight_1);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlight_1.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         showHighlight_1.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "showHighlight_1.onAnimationStart ReadActivityAuto");

        highlight_btn_1.setVisibility(View.VISIBLE);
        highlight_btn_1.setClickable(true);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "showHighlight_1.onAnimationEnd ReadActivityAuto");

        unlockButtons();
        unlockTap();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Preparamos animación de mostrar el botón de rate
         showHighlight_2 = new AnimationSet(true);

         ScaleAnimation scaleShowHighlight_2 = new ScaleAnimation(0.4f, 1, 0.4f, 1,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleShowHighlight_2.setDuration(getResources().getInteger(R.integer.show_standard));

         TranslateAnimation translateShowHighlight_2 = new TranslateAnimation(
         -horizontalTranslation, 0, -verticalTranslation, 0);
         translateShowHighlight_2.setDuration(getResources().getInteger(R.integer.show_standard));

         showHighlight_2.addAnimation(scaleShowHighlight_2);
         showHighlight_2.addAnimation(translateShowHighlight_2);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         showHighlight_2.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         showHighlight_2.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "showHighlight_2.onAnimationStart ReadActivityAuto");

        highlight_btn_2.setVisibility(View.VISIBLE);
        highlight_btn_2.setClickable(true);
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Preparamos animación de mostrar el botón highlight option 1
         hideHighlight_1 = new AnimationSet(true);

         ScaleAnimation scaleHideHighlight_1 = new ScaleAnimation( 1, 0.4f, 1, 0.4f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlight_1.setDuration(getResources().getInteger(R.integer.show_standard));

         TranslateAnimation translateHideHighlight_1 = new TranslateAnimation(
         0, horizontalTranslation, 0, -verticalTranslation);
         translateHideHighlight_1.setDuration(getResources().getInteger(R.integer.show_standard));

         hideHighlight_1.addAnimation(scaleHideHighlight_1);
         hideHighlight_1.addAnimation(translateHideHighlight_1);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlight_1.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         hideHighlight_1.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "hideHighlight_1.onAnimationEnd ReadActivityAuto");

        highlight_btn_1.setClickable(false);
        highlight_btn_1.setVisibility(View.INVISIBLE);
        unlockButtons();
        unlockTap();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Preparamos animación de mostrar el botón de rate
         hideHighlight_2 = new AnimationSet(true);

         ScaleAnimation scaleHideHighlight_2 = new ScaleAnimation( 1, 0.4f, 1, 0.4f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlight_2.setDuration(getResources().getInteger(R.integer.show_standard));

         TranslateAnimation translateHideHighlight_2 = new TranslateAnimation(
         0, -horizontalTranslation, 0, -verticalTranslation);
         translateHideHighlight_2.setDuration(getResources().getInteger(R.integer.show_standard));

         hideHighlight_2.addAnimation(scaleHideHighlight_2);
         hideHighlight_2.addAnimation(translateHideHighlight_2);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlight_2.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         hideHighlight_2.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "hideHighlight_2.onAnimationEnd ReadActivityAuto");

        highlight_btn_2.setClickable(false);
        highlight_btn_2.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Preparamos animación de mostrar el botón highlight option 1
         hideHighlight_1_long = new AnimationSet(true);

         ScaleAnimation scaleHideHighlight_1_long = new ScaleAnimation( 1, 0.4f, 1, 0.4f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlight_1_long.setDuration(getResources().getInteger(R.integer.hide_highlight));

         //Se esconde la suma de las distancias de ambos casos
         TranslateAnimation translateHideHighlight_1_long = new TranslateAnimation(
         0, horizontalTranslation, 0,
         -verticalTranslation-getResources().getDimension(R.dimen.hide_controls));
         translateHideHighlight_1_long.setDuration(getResources().getInteger(R.integer.hide_highlight));

         hideHighlight_1_long.addAnimation(scaleHideHighlight_1_long);
         hideHighlight_1_long.addAnimation(translateHideHighlight_1_long);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlight_1_long.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         hideHighlight_1_long.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "hideHighlight_1_long.onAnimationEnd ReadActivityAuto");

        highlightButtonsVisible = false;
        highlight_btn_1.setClickable(false);
        highlight_btn_1.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });

         //Preparamos animación de mostrar el botón de rate
         hideHighlight_2_long = new AnimationSet(true);

         ScaleAnimation scaleHideHighlight_2_long = new ScaleAnimation( 1, 0.4f, 1, 0.4f,
         Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
         scaleHideHighlight_2_long.setDuration(getResources().getInteger(R.integer.hide_highlight));

         TranslateAnimation translateHideHighlight_2_long = new TranslateAnimation(
         0, -horizontalTranslation, 0,
         -verticalTranslation-getResources().getDimension(R.dimen.hide_controls));
         translateHideHighlight_2_long.setDuration(getResources().getInteger(R.integer.hide_highlight));

         hideHighlight_2_long.addAnimation(scaleHideHighlight_2_long);
         hideHighlight_2_long.addAnimation(translateHideHighlight_2_long);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

         hideHighlight_2_long.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
         getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
         }

         hideHighlight_2_long.setAnimationListener(new AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        Log.v(Constants.Log.CONTROLS, "hideHighlight_2_long.onAnimationEnd ReadActivityAuto");

        highlight_btn_2.setClickable(false);
        highlight_btn_2.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        });
         */

        //Cargamos la animación de aparición del botón de actualizar página
        showRefresh = new AnimationSet(true);

        ScaleAnimation scaleRefreshShow = new ScaleAnimation(0.4f, 1, 0.4f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleRefreshShow.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        TranslateAnimation translateRefreshShow = new TranslateAnimation(
                getResources().getDimension(R.dimen.margin_refresh_btn), 0 ,0, 0);
        translateRefreshShow.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        showRefresh.addAnimation(scaleRefreshShow);
        showRefresh.addAnimation(translateRefreshShow);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showRefresh.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        showRefresh.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showRefresh.onAnimationStart");

                refresh_btn.setVisibility(View.VISIBLE);
                refresh_btn.setClickable(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showRefresh.onAnimationEnd");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Cargamos la animación de ocultación de botón de actualizar página
        hideRefresh = new AnimationSet(true);

        ScaleAnimation scaleRefreshHide = new ScaleAnimation(1, 0.2f, 1, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleRefreshHide.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        TranslateAnimation translateRefreshHide = new TranslateAnimation(0 ,
                getResources().getDimension(R.dimen.margin_refresh_btn) ,0, 0);
        translateRefreshHide.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        hideRefresh.addAnimation(scaleRefreshHide);
        hideRefresh.addAnimation(translateRefreshHide);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideRefresh.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }
        hideRefresh.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideRefresh.onAnimationStart");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideRefresh.onAnimationEnd");

                refresh_btn.setClickable(false);
                refresh_btn.setVisibility(View.INVISIBLE);
            }
        });

        //Cargamos la animación de aparición del botón de actualizar página
        showLikeSet = new AnimationSet(true);

        ScaleAnimation scaleLikeShow = new ScaleAnimation(0.4f, 1, 0.4f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleLikeShow.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        TranslateAnimation translateLikeShow = new TranslateAnimation(
                -getResources().getDimension(R.dimen.margin_like_btn), 0, 0 ,0);
        translateLikeShow.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        //Cargamos animación de latido para el botón de like
        ScaleAnimation heartBeatLike = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        heartBeatLike.setRepeatMode(Animation.REVERSE);
        heartBeatLike.setRepeatCount(Animation.INFINITE);
        heartBeatLike.setDuration(getResources().getInteger(R.integer.heartbeat_autoplay_like));
        //heartBeatLike.setStartOffset(getResources().getInteger(R.integer.heartbeat_autoplay_like));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            heartBeatLike.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        showLikeSet.addAnimation(scaleLikeShow);
        showLikeSet.addAnimation(translateLikeShow);
        showLikeSet.addAnimation(heartBeatLike);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showLikeSet.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        showLikeSet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showLikeSet.onAnimationStart");

                like_btn.setVisibility(View.VISIBLE);
                like_btn.setClickable(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Cargamos la animación de aparición del botón de actualizar página
        hideLike = new AnimationSet(true);

        ScaleAnimation scaleLikeHide = new ScaleAnimation(1, 0.2f, 1, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleLikeHide.setDuration(getResources().getInteger(R.integer.hide_controls));

        TranslateAnimation translateLikeHide = new TranslateAnimation( 0,
                -getResources().getDimension(R.dimen.margin_like_btn), 0, 0);
        translateLikeHide.setDuration(getResources().getInteger(R.integer.hide_controls));

        hideLike.addAnimation(scaleLikeHide);
        hideLike.addAnimation(translateLikeHide);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideLike.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        hideLike.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideLike.onAnimationEnd");

                like_btn.setClickable(false);
                like_btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Cargamos la animación de aparición del botón de actualizar página
        hideLikeForRestartBL = new AnimationSet(true);

        ScaleAnimation scaleLikeHideBL = new ScaleAnimation(1, 0.2f, 1, 0.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleLikeHideBL.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        TranslateAnimation translateLikeHideBL = new TranslateAnimation( 0,
                -getResources().getDimension(R.dimen.margin_like_btn), 0, 0);
        translateLikeHideBL.setDuration(getResources().getInteger(R.integer.show_hide_refresh));

        hideLikeForRestartBL.addAnimation(scaleLikeHideBL);
        hideLikeForRestartBL.addAnimation(translateLikeHideBL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideLikeForRestartBL.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        hideLikeForRestartBL.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideLikeForRestartBL.onAnimationEnd");

                play_btn.startAnimation(preRestartTransition);

                like_btn.setClickable(false);
                like_btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //Cargamos la animación de aparición de controles inferiores
        showBottomControls = new TranslateAnimation(0, 0,
                getResources().getDimension(R.dimen.hide_controls), 0);
        showBottomControls.setDuration(getResources().getInteger(R.integer.hide_controls));
        showBottomControls.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showBottomControls.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        //Cargamos la animación de ocultación de los controles inferiores
        hideBottomControls = new AnimationSet(true);

        TranslateAnimation hideBottomControlsTranslate =
                new TranslateAnimation(0, 0, 0, getResources().getDimension(R.dimen.hide_controls));
        hideBottomControlsTranslate.setDuration(getResources().getInteger(R.integer.hide_controls));
        hideBottomControlsTranslate.setFillAfter(true);

        hideBottomControls.addAnimation(hideBottomControlsTranslate);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideBottomControls.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        hideBottomControls.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideBottomControls.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hideBottomControls.onAnimationEnd");
                controls_bottom.setVisibility(View.INVISIBLE);
            }
        });

        hidePlay = new AnimationSet(false);

        ScaleAnimation scaleHidePlay = new ScaleAnimation(1, 0, 1f, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleHidePlay.setDuration(getResources().getInteger(R.integer.change_icon_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            scaleHidePlay.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        RotateAnimation rotatePlayPlay = new RotateAnimation(0, 180,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            rotatePlayPlay.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        rotatePlayPlay.setDuration(getResources().getInteger(R.integer.change_icon_standard));

        hidePlay.addAnimation(rotatePlayPlay);
        hidePlay.addAnimation(scaleHidePlay);

        hidePlay.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hidePlay.onAnimationStart ");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hidePlay.onAnimationEnd ");

                play_btn.setClickable(false);
                play_btn.setVisibility(View.INVISIBLE);
            }
        });

        showPlay =  new AnimationSet(true);

        ScaleAnimation scaleShowPlay = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleShowPlay.setDuration(getResources().getInteger(R.integer.change_icon_standard));

        RotateAnimation rotateShowPlay = new RotateAnimation(180, 360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateShowPlay.setDuration(getResources().getInteger(R.integer.change_icon_standard));

        showPlay.addAnimation(scaleShowPlay);
        showPlay.addAnimation(rotateShowPlay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showPlay.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        showPlay.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivity - showPlay.onAnimationStart ");

                play_btn.setVisibility(View.VISIBLE);
                play_btn.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivity - showPlay.onAnimationEnd ");
            }
        });

        showProgressBar = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showProgressBar.setDuration(getResources().getInteger(R.integer.change_icon_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            showProgressBar.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        hideProgressBar = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideProgressBar.setDuration(getResources().getInteger(R.integer.hide_progressbar));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideProgressBar.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        //Cargamos la animación del botón de play al llegar al final del autoplay
        heartBeatAutoplayEnd = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        heartBeatAutoplayEnd.setDuration(getResources().getInteger(R.integer.heartbeat_autoplay_end));
        heartBeatAutoplayEnd.setRepeatMode(Animation.REVERSE);
        heartBeatAutoplayEnd.setRepeatCount(Animation.INFINITE);
        heartBeatAutoplayEnd.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            heartBeatAutoplayEnd.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        heartBeatAutoplayEnd.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - heartBeatAutoplayEnd.onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

        //Cargamos la animación que precede al reveal del restart
        preRestartTransition = new AnimationSet(false);
        ScaleAnimation preHideRevealRestart = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        preHideRevealRestart.setDuration(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            preHideRevealRestart.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        ScaleAnimation hideRevealRestart = new ScaleAnimation(1.2f, 0, 1.2f, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideRevealRestart.setDuration(getResources().getInteger(R.integer.hide_reveal_play));
        hideRevealRestart.setStartOffset(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideRevealRestart.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        preRestartTransition.addAnimation(preHideRevealRestart);
        preRestartTransition.addAnimation(hideRevealRestart);

        preRestartTransition.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - preRestartTransition.onAnimationStart");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - preRestartTransition.onAnimationEnd");

                setButtonIconToPlay();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    play_btn.startAnimation(showPlayRestart);
                    restartReveal();
                }
                else {

                    play_btn.startAnimation(showPlayRestartBL);
                    //Movemos a la 1a página.
                    mPager.toFirstPage();
                }
            }
        });

        //Cargamos la animación que precede al reveal que transiciona a la actividad Like
        hidePlayForLikeTransition = new AnimationSet(false);
        ScaleAnimation preHideRevealPlayLike = new ScaleAnimation(1f, 1.1f, 1f, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        preHideRevealPlayLike.setDuration(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            preHideRevealPlayLike.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));

        }
        ScaleAnimation hideRevealPlayLike = new ScaleAnimation(1.1f, 0, 1.1f, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        hideRevealPlayLike.setDuration(getResources().getInteger(R.integer.hide_reveal_play));
        hideRevealPlayLike.setStartOffset(getResources().getInteger(R.integer.pre_hide_standard));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            hideRevealPlayLike.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        hidePlayForLikeTransition.addAnimation(preHideRevealRestart);
        hidePlayForLikeTransition.addAnimation(hideRevealRestart);

        hidePlayForLikeTransition.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - hidePlayForLikeTransition.onAnimationEnd");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    setButtonIconToPlay();
                    play_btn.startAnimation(showPlayRestart);
                }
                else {
                    play_btn.setVisibility(View.INVISIBLE);
                }
            }
        });

        showPlayRestart = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showPlayRestart.setDuration(getResources().getInteger(R.integer.show_standard));

        showPlayRestartBL = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        showPlayRestartBL.setDuration(getResources().getInteger(R.integer.show_standard));

        showPlayRestartBL.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showPlayRestartBL.onAnimationStart");

                mPager.startAnimation(pagerInTransition);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showPlayRestartBL.onAnimationEnd");

                progressBar.setVisibility(View.VISIBLE);
                //Creamos música
                MusicManager.createMusic(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pagerInTransition = new TranslateAnimation(
                getResources().getDimension(R.dimen.translate_logos), 0, 0, 0);
        pagerInTransition.setDuration(getResources().getInteger(R.integer.translate_hide_logo));
        pagerInTransition.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pagerInTransition.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }

        pagerInTransition.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pagerInTransition.onAnimationEnd");

                //Desbloqueamos después de la barra de progreso
                programUnlock(Constants.Autoplay.PROGRESSBAR_INITIAL_TIME_ANIMATION);
                mPager.startProgressBarInitialAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pagerInTransitionIntro = new TranslateAnimation(
                getResources().getDimension(R.dimen.translate_logos), 0, 0, 0);
        pagerInTransitionIntro.setDuration(getResources().getInteger(R.integer.translate_hide_logo));
        pagerInTransitionIntro.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pagerInTransitionIntro.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
        }
        pagerInTransitionIntro.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pagerInTransitionIntro.onAnimationEnd");

                mPager.startProgressBarInitialAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        pagerOutTransition = new TranslateAnimation(
                0, getResources().getDimension(R.dimen.translate_logos), 0, 0);
        pagerOutTransition.setDuration(getResources().getInteger(R.integer.translate_hide_logo));
        pagerOutTransition.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pagerOutTransition.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }

        pagerOutTransitionExit = new TranslateAnimation(
                0, getResources().getDimension(R.dimen.translate_logos), 0, 0);
        pagerOutTransitionExit.setDuration(getResources().getInteger(R.integer.translate_hide_logo));
        pagerOutTransitionExit.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pagerOutTransitionExit.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        pagerOutTransitionExit.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pagerOutTransitionExit.onAnimationEnd");

                ReadActivityAuto.this.setResult(RESULT_OK);
                //Finalizamos la actividad
                ReadActivityAuto.this.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pagerOutTransitionLike = new TranslateAnimation(
                0, getResources().getDimension(R.dimen.translate_logos), 0, 0);
        pagerOutTransitionLike.setDuration(getResources().getInteger(R.integer.translate_hide_logo));
        pagerOutTransitionLike.setFillAfter(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            pagerOutTransitionLike .setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                    getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        }
        pagerOutTransitionLike.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pagerOutTransitionLike.onAnimationEnd");

                /*
                //Damos paso a la actividad
                Intent i = new Intent(mContext, LikeActivity.class);

                //Pasamos la info para identificar a la actividad Like que venimos de Read
                i.putExtra(Constants.LIKE_ACTIVITY_FROM, Constants.LIKE_ACTIVITY_FROM_READ);

                //Lo añadimos para que cuando volvamos a ReadActivityAuto sepamos que venimos de Like
                ReadActivityAuto.this.setResult(RESULT_CANCELED);
                startActivity(i);
                //Lo añadimos para quitar el blink, lo que hace es eliminar animaciones de transición.
                overridePendingTransition(0, 0);

                //Terminamos la actividad
                ReadActivityAuto.this.finish();
                */
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Avanza una página
     */
    private void pageForward() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pageForward");

        //Bloqueamos el botón de play y el tap para que no se interrumpa la animación de reinicio de autoplay.
        lockButtons();
        lockTap();

        if (!finCuento) {

            //Pausamos el autoplay pero dejamos la música
            pausaAutoplayControles(true);

            //Escondemos el botón refresh si estuviera visible
            ((ReadActivityAuto) mContext).hideRefreshChangePage();

            timerLleno = true;
        }

        mPager.setCurrentItem(mPager.getCurrentItem()+1, true);
    }

    /**
     * Retrocede una página
     */
    private void pageBack() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - pageBack");

        //Bloqueamos el botón de play y el tap
        lockButtons();
        lockTap();

        if (!finCuento) {

            //Pausamos el autoplay pero dejamos la música
            pausaAutoplayControles(true);

            //Escondemos el botón refresh si estuviera visible
            ((ReadActivityAuto) mContext).hideRefreshChangePage();

            timerLleno = true;
        }

        mPager.setCurrentItem(mPager.getCurrentItem()-1, true);
    }

    private void prepareMusicAndVoice() {

        //Restauramos valor por defecto para que se pare la música en onStop
        continue_music = false;
        //Preparamos la voz para la primera página
        MusicManager.createVoice(0);
        //Recordamos el estado del volumen
        if (MusicManager.wasReadVoiceOn()) {

            MusicManager.setVoiceVolumeOn();
            MusicManager.setVoice_on(true);
            setVoiceIconOn();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                voice_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn, null));
            }
            else {

                voice_btn.setBackground(getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off));
            }
        } else {

            MusicManager.setVoiceVolumeOff();
            MusicManager.setVoice_on(false);
            setVoiceIconOff();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                voice_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn_disabled, null));
            }
            else{

                voice_btn.setBackground(getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on));
            }
        }
    }

    /**
     * Pausa el autoplay al tocar los controles
     */
    private void pausaAutoplayControles(boolean cambioPagina) {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - pausaAutoplayControles");

        if (!paused) {

            //Paramos el autoplay al desplegar esta opción
            //Cancelamos tarea de desvanecimiento
            cancelDesvanecimientoControles();

            mPager.pauseTimer(cambioPagina);
            //Pausamos la música
            MusicManager.pauseMusic();

            paused = true;

            //Animación click
            play_btn.startAnimation(clickPlayForPause);
            progressBar.startAnimation(clickProgressBar);
            AnimationDrawable frameAnimation;
            //Animación transformación play/pause
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play, null);
            }
            else {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play);
            }
            play_btn.setImageDrawable(frameAnimation);
            frameAnimation.start();
        }
    }

    /**
     * Pone en marcha las animaciones al hacer click en el botón de play
     */
    private void startClickPlayAnimations() {

        progressBar.startAnimation(clickProgressBar);
        //Animación transformación play/pause
        //Icono Pause - > Icono Play
        //Play - > Pause
        if (!paused) {

            paused = true;
            play_btn.startAnimation(clickPlayForPause);

            AnimationDrawable frameAnimation;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play, null);
            }
            else {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play);
            }
            play_btn.setImageDrawable(frameAnimation);

            frameAnimation.start();
        }
        //Icono Play - > Icono Pause
        //Pause - > Play
        else {

            paused = false;
            play_btn.startAnimation(clickPlay);

            AnimationDrawable frameAnimation;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_play_pause, null);
            }
            else {
                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_play_pause);
            }

            play_btn.setImageDrawable(frameAnimation);

            frameAnimation.start();
            if (refresh_btn.getVisibility() == View.VISIBLE) refresh_btn.startAnimation(hideRefresh);
        }

    }

    /**
     * Prepara la selección de resaltados
     */
    /** Highlight button deactivated
    private void prepareHighlightSelection() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            listaHighlights.add(getResources().getDrawable(R.drawable.ripple_highlight_accent, null));
            listaHighlights.add(getResources().getDrawable(R.drawable.ripple_highlight_pink, null));

            highlightSelected = getResources().getDrawable(R.drawable.ripple_highlight_accent, null);
        }
        else{

            listaHighlights.add(getResources().getDrawable(R.drawable.round_accent_btn));
            listaHighlights.add(getResources().getDrawable(R.drawable.round_pink_btn));

            highlightSelected = getResources().getDrawable(R.drawable.round_accent_btn);
        }

    }
    */
    /**
     * Esconde el botón refresh en un paso de página.
     */
    public void hideRefreshChangePage(){

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - hideRefreshChangePage");

        if (refresh_btn.getVisibility() == View.VISIBLE)  {

            refresh_btn.startAnimation(hideRefresh);
        }

        //timerLleno = true;
    }

    /**
     * Muestra el botón de refresh
     */
    public void showRefreshMethod() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - showRefreshMethod");

        refresh_btn.startAnimation(showRefresh);
        timerLleno = false;
    }

    /**
	 * Pone en marcha animación Reveal Effect en reverso. Utilizada al entrar en la actividad,
	 * después de la primera parte realizada en MainActivity.
	 */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void startUnvealRead() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - startUnvealRead");
		
		revealLayout.addOnLayoutChangeListener(new OnLayoutChangeListener() {
			
			@Override
			public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
				
				v.removeOnLayoutChangeListener(this);

				//Calculamos el centro de la animación
				int cx = (revealLayout.getLeft() + revealLayout.getRight()) / 2;
				int cy = (revealLayout.getTop() + revealLayout.getBottom()) / 2;
				
				//Calculamos el radio de la animación
				//int initialRadius = Math.max(frameLayout.getWidth(), frameLayout.getHeight());
				int initialRadius = (int) Math.sqrt( Math.pow(revealLayout.getWidth(), 2) +
						Math.pow(revealLayout.getHeight(), 2));
				
				//Creamos la animación
				Animator anim =
				    ViewAnimationUtils.createCircularReveal(revealLayout,
				    		cx, cy, initialRadius,
							getResources().getDimension(R.dimen.diameter_logo_btns) / 2);
				anim.setDuration(getResources().getInteger(R.integer.unveal_long));
		        
				//Hacemos visible la vista y empezamos la animación
				anim.addListener(new AnimatorListener() {
					
					@Override
					public void onAnimationStart(Animator animation) {
						Log.v(Constants.Log.METHOD, "ReadActivityAuto - startUnvealRead.onAnimationStart");
						
						revealLayout.setVisibility(View.VISIBLE);
						//Mostramos las paginas de cuento
						mPager.setVisibility(View.VISIBLE);
						//play_btn.startAnimation(animPlayButtonScale);
						//play_btn.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onAnimationRepeat(Animator animation) {
						
					}
					
					@Override
					public void onAnimationEnd(Animator animation) {
						Log.v(Constants.Log.METHOD, "ReadActivityAuto - startUnvealRead.onAnimationEnd");
						
						revealLayout.setVisibility(View.INVISIBLE);
						play_btn.setElevation(getResources().getDimension(R.dimen.button_higher_elevation));
						progressBar.setVisibility(View.VISIBLE);
						mPager.startProgressBarInitialAnimation();

                        //Creamos la música aquí para dar un poco más de tiempo al fadeMusic
                        MusicManager.createMusic(false);
                        //Recordamos el estado del volumen
                        if (MusicManager.wasReadMusicOn()) {

                            MusicManager.setMusicVolumeOn();
                            MusicManager.setMusic_on(true);
                            setMusicIconOn();
                            music_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn, null));
                        } else {

                            MusicManager.setMusicVolumeOff();
                            MusicManager.setMusic_on(false);
                            setMusicIconOff();
                            music_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn_disabled, null));
                        }
					}
					
					@Override
					public void onAnimationCancel(Animator animation) {
						
					}
				});
				anim.setStartDelay(getResources().getInteger(R.integer.delay_unveal_read));
				anim.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
						getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
				anim.start();
			}
		});
	}

    private void startShowLayoutBL() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - startShowLayoutBL");

        progressBar.setVisibility(View.VISIBLE);
        //Mostramos las paginas de cuento
        mPager.setVisibility(View.VISIBLE);
        mPager.startAnimation(pagerInTransitionIntro);
        play_btn.startAnimation(showPlay);
        progressBar.startAnimation(showProgressBar);

        //Creamos la música aquí para dar un poco más de tiempo al fadeMusic
        MusicManager.createMusic(false);
        //Recordamos el estado del volumen
        if (MusicManager.wasReadMusicOn()) {

            MusicManager.setMusicVolumeOn();
            MusicManager.setMusic_on(true);
            setMusicIconOn();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                music_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn, null));
            }
            else {
                music_btn.setBackground(getResources().getDrawable(R.drawable.transition_toolbar_icon_on_off));
            }
        } else {

            MusicManager.setMusicVolumeOff();
            MusicManager.setMusic_on(false);
            setMusicIconOff();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                music_btn.setBackground(getResources().getDrawable(R.drawable.ripple_toolbar_btn_disabled, null));
            }
            else {
                music_btn.setBackground(getResources().getDrawable(R.drawable.transition_toolbar_icon_off_on));
            }
        }
    }

    /**
     * Pone en marcha animación Reveal Effect al pulsar el botón de cerrar.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startRevealReadReturn() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - startRevealReadReturn");

        //Calculamos el centro de la animación
        int cx = (close_btn.getLeft() + close_btn.getRight()) / 2;
        int cy = (close_btn.getTop() + close_btn.getBottom()) / 2;

        //Calculamos el radio de la animación
        int finalRadius = (int) Math.sqrt(Math.pow(revealLayout.getWidth(), 2) +
                Math.pow(revealLayout.getHeight(), 2));

        //Creamos la animación
        Animator anim =
                ViewAnimationUtils.createCircularReveal(revealLayout,
                        cx, cy, 0, finalRadius);
        anim.setDuration(getResources().getInteger(R.integer.reveal_read_return));

        //Hacemos visible la vista y empezamos la animación
        anim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                Log.v(Constants.Log.METHOD, "ReadActivityAuto - startRevealReadReturn.onAnimationStart");

                //Hacemos visible el reveal y le cambiamos el color
                revealLayout.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    revealLayout.setBackgroundColor(getResources().getColor(R.color.color_primary, null));
                } else {
                    revealLayout.setBackgroundColor(getResources().getColor(R.color.color_primary));
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(Constants.Log.METHOD, "ReadActivityAuto - startRevealReadReturn.onAnimationEnd");

                //Indicamos que no se debe interrumpir la música en onStop
                continue_music = true;
                //Guardamos si la música está activa o no
                MusicManager.setWasReadMusicOn(MusicManager.isMusic_on());
                MusicManager.setWasReadVoiceOn(MusicManager.isVoice_on());

                ReadActivityAuto.this.setResult(RESULT_OK);
                //Finalizamos la actividad
                ReadActivityAuto.this.finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        anim.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
        anim.start();
    }

    /**
     * Esconde controles superiores y inferiores - boton play y progressBar
     */
    public void startExitBL() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - startExitBL");

        //Indicamos que no se debe interrumpir la música en onStop
        continue_music = true;
        //Guardamos si la música está activa o no
        MusicManager.setWasReadMusicOn(MusicManager.isMusic_on());
        MusicManager.setWasReadVoiceOn(MusicManager.isVoice_on());

        mPager.startAnimation(pagerOutTransitionExit);

        //Ocultamos los controles superiores e inferiores
        if (areControlsVisible) {

            //Bloqueamos el botón de play y el tap
            lockButtons();
            lockTap();

            close_btn.startAnimation(hideClose);
            music_btn.startAnimation(hideMusic);
            voice_btn.startAnimation(hideVoice);
            play_btn.startAnimation(hidePlay);

            /** Highlight button deactivated
            if (highlightButtonsVisible) {

                hideHighlightIcon.cancel();
                showHighlightClose.cancel();
                highlight_btn_close.clearAnimation();
                highlight_btn_close.startAnimation(hideHighlightClose);
                highlight_btn_1.startAnimation(hideHighlight_1_long);
                highlight_btn_2.startAnimation(hideHighlight_2_long);
            }
            else {

                highlight_btn.startAnimation(hideHighlight);
            }
            highlight_backgr_btn.startAnimation(hideHighlightBackgr);
             */
            //Ya lo escondemos en exit
            //if (refresh_btn.getVisibility() == View.VISIBLE) refresh_btn.startAnimation(hideRefresh);
            controls_bottom.startAnimation(hideBottomControls);
            //actionBar.hide();
            //Aquí no sacamos fuera de las animaciones el setVisibility porque se adelanta a la animacion hideCenterControls

            areControlsVisible = false;
        }
    }
	
	/**
	 * Pone en marcha animación Reveal Effect al reiniciar el autoplay una vez terminado
	 */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void restartReveal() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartReveal");

		//Calculamos el centro de la animación
		int cx = (revealLayout.getLeft() + revealLayout.getRight()) / 2;
		int cy = (revealLayout.getTop() + revealLayout.getBottom()) / 2;

		//Calculamos el radio de la animación
		int finalRadius = (int) Math.sqrt( Math.pow(revealLayout.getWidth()/2, 2) +
				Math.pow(revealLayout.getHeight()/2, 2));
		
		//Creamos la animación
		Animator anim =
		    ViewAnimationUtils.createCircularReveal(revealLayout,
		    		cx, cy, 0, finalRadius);
		anim.setDuration(getResources().getInteger(R.integer.reveal_standard));
        
		//Hacemos visible la vista y empezamos la animación
		anim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartReveal.onAnimationStart");

                revealLayout.setVisibility(View.VISIBLE);
                //Cambiamos el tamaño del botón de play para que no haya un cambio brusco después de la animación de transformación del icono.
                //play_btn.setScaleX((float) 1.2);
                //play_btn.setScaleY((float) 1.2);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartReveal.onAnimationEnd");

                //Ponemos en marcha la segunda parte de la animación
                restartUnveal();
                //Movemos a la 1a página.
                mPager.toFirstPage();
                //Escondemos los botones de refresh y like
                refresh_btn.setClickable(false);
                refresh_btn.setVisibility(View.INVISIBLE);
                like_btn.setClickable(false);
                like_btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        });
		anim.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                getApplicationContext(), android.R.interpolator.fast_out_linear_in)));
		anim.start();
	}
	
	/**
	 * Pone en marcha la 2a parte de la animación Reveal Effect al reiniciar el autoplay una vez terminado
	 */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void restartUnveal() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartUnveal");

		//Calculamos el centro de la animación
		int cx = (revealLayout.getLeft() + revealLayout.getRight()) / 2;
		int cy = (revealLayout.getTop() + revealLayout.getBottom()) / 2;
		
		//Calculamos el radio de la animación
		int initialRadius = (int) Math.sqrt( Math.pow(revealLayout.getWidth()/2, 2) +
				Math.pow(revealLayout.getHeight()/2, 2));
		
		//Creamos la animación
		Animator anim =
		    ViewAnimationUtils.createCircularReveal(revealLayout,
		    		cx, cy, initialRadius, getResources().getDimension(R.dimen.diameter_play_btn) / 2);
		anim.setDuration(getResources().getInteger(R.integer.reveal_standard_return));
        
		//Hacemos visible la vista y empezamos la animación
		anim.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

                Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartUnveal.onAnimationStart");

                //Devolvemos al botón de play su tamaño original y ponemos en marcha un animación de scale a su tamaño.
                /*
                play_btn.setScaleX((float) 1);
                play_btn.setScaleY((float) 1);
                play_btn.startAnimation(autoplayEndReturn);
                */
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                Log.v(Constants.Log.METHOD, "ReadActivityAuto - restartUnveal.onAnimationEnd");

                //Ocultamos la capa revealEffect
                revealLayout.setVisibility(View.INVISIBLE);
                //Volvemos a poner el icono de play en el botón
                //setButtonIconToPlay();
                //Restauramos la elevación y la barra de progreso
                play_btn.setElevation(getResources().getDimension(R.dimen.button_higher_elevation));
                refresh_btn.setElevation((getResources().getDimension(R.dimen.button_higher_elevation)));
                like_btn.setElevation((getResources().getDimension(R.dimen.button_higher_elevation)));
                progressBar.setVisibility(View.VISIBLE);
                //Desbloqueamos después de la barra de progreso
                programUnlock(Constants.Autoplay.PROGRESSBAR_INITIAL_TIME_ANIMATION);
                //Empezamos la animación de relleno de la barra de progreso
                mPager.startProgressBarInitialAnimation();
                //Creamos música
                MusicManager.createMusic(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
		anim.setInterpolator((PathInterpolator) (AnimationUtils.loadInterpolator(
                getApplicationContext(), android.R.interpolator.fast_out_slow_in)));
		anim.setStartDelay(getResources().getInteger(R.integer.delay_restart_reveal));
		anim.start();
	}


    /**
     * Transición a Like antes de Lollipop
     */
    private void startTransitionToLikeBL(){

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - startTransitionToLike");

        //Indicamos que no se debe interrumpir la música en onStop
        continue_music = true;
        //Guardamos si la música está activa o no
        MusicManager.setWasReadMusicOn(MusicManager.isMusic_on());
        MusicManager.setWasReadVoiceOn(MusicManager.isVoice_on());

        //Escondemos controles superiores e inferiores
        mPager.startAnimation(pagerOutTransitionLike);

        play_btn.clearAnimation();
        play_btn.startAnimation(hidePlayForLikeTransition);
        close_btn.startAnimation(hideClose);
        music_btn.startAnimation(hideMusic);
        voice_btn.startAnimation(hideVoice);

        /** Highlight button deactivated
        if (highlightButtonsVisible) {

            hideHighlightIcon.cancel();
            showHighlightClose.cancel();
            highlight_btn_close.clearAnimation();
            highlight_btn_close.startAnimation(hideHighlightClose);
            highlight_btn_1.startAnimation(hideHighlight_1_long);
            highlight_btn_2.startAnimation(hideHighlight_2_long);
        }
        else {

            highlight_btn.startAnimation(hideHighlight);
        }
        highlight_backgr_btn.startAnimation(hideHighlightBackgr);
        */
        //if (refresh_btn.getVisibility() == View.VISIBLE) refresh_btn.startAnimation(hideRefresh);
        controls_bottom.startAnimation(hideBottomControls);
    }

	/**
	 * Muestra o esconde los controles del autoplay, según están visibles o no. Respuesta al evento Single Tap.
	 */
	public void showOrHideControls() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - showOrHideControls");
		
		//Controles visibles
		if (areControlsVisible) {
			
			Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showOrHideControls Desvanecer ");
			
			//Escondemos los controles
			//Primero cancelamos la tarea de desvanecimiento.
			cancelDesvanecimientoControles();
			ocultarControles(false);
		}
		//Controles no visibles
		else {
			
			Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - showOrHideControls Mostrar");
			
			//Mostramos controles
	        mostrarControles(false);
		}
	}
	
	/**
	 * Hace visibles todos los controles. Si end es true obviamos el bloqueo
	 */
	public void mostrarControles(boolean force) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - mostrarControles");

        if (!areButtonsLocked() || force) {

            //Planificamos el desvanecimiento de los controles solo si está en marcha el autoplay
            if (!paused) planificarDesvanecimientoControles(false);

            //Mostramos controles
            if (!areControlsVisible) {

                //Bloqueamos el botón de play y el tap para que no se interrumpa la animación de reinicio de autoplay.
                lockButtons();
                lockTap();

                //Desbloqueamos en mostrarControlesSupInf

                controlsLayout.startAnimation(showCenterControls);
                play_btn.setVisibility(View.VISIBLE);
                play_btn.setClickable(true);
                progressBar.setVisibility(View.VISIBLE);
                if ((refresh_btn.getVisibility() == View.INVISIBLE) && !timerLleno && paused) {

                    refresh_btn.startAnimation(showRefresh);
                }
                mostrarControlesSupInf();
            }
        }
	}

	/**
	 * Muestra controles superiores y inferiores - boton play y progressBar
	 */
	public void mostrarControlesSupInf() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - mostrarControlesSupInf");

		close_btn.startAnimation(showClose);
		music_btn.startAnimation(showMusic);
		voice_btn.startAnimation(showVoice);

        //Programamos desbloqueo
        programUnlock(showVoice.getDuration() + showVoice.getStartOffset());

        /** Highlight button deactivated
        highlight_btn.startAnimation(showHighlight);
        highlight_backgr_btn.startAnimation(showHighlightBackgr);
         */
		controls_bottom.startAnimation(showBottomControls);
		
		//Sacar fuera de las animaciones el setVisibility, a veces no se ejecutan al ser simultáneas 2 animaciones sobre la misma view.
		close_btn.setVisibility(View.VISIBLE);
        close_btn.setClickable(true);
		music_btn.setVisibility(View.VISIBLE);
        music_btn.setClickable(true);
		voice_btn.setVisibility(View.VISIBLE);
        voice_btn.setClickable(true);
        /** Highlight button deactivated
        highlight_btn.setVisibility(View.VISIBLE);
        highlight_btn.setClickable(true);
         */
		controls_bottom.setVisibility(View.VISIBLE);
		
    	areControlsVisible = true;
	}

	/**
	 * Oculta los controles
	 */
	public void ocultarControles(boolean auto) {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - ocultarControles");

        if (!areButtonsLocked() || auto) {

            //Si hemos llegado al final del autoplay no los ocultamos
            if (!finCuento) {

                //Ocultamos los controles
                if (areControlsVisible) {

                    //Bloqueamos el botón de play y el tap
                    lockButtons();
                    lockTap();

                    controlsLayout.startAnimation(hideCenterControls);
                    close_btn.startAnimation(hideClose);
                    music_btn.startAnimation(hideMusic);
                    voice_btn.startAnimation(hideVoice);

                    /** Highlight button deactivated
                    if (highlightButtonsVisible) {

                        hideHighlightIcon.cancel();
                        showHighlightClose.cancel();
                        highlight_btn_close.clearAnimation();
                        highlight_btn_close.startAnimation(hideHighlightClose);
                        highlight_btn_1.startAnimation(hideHighlight_1_long);
                        highlight_btn_2.startAnimation(hideHighlight_2_long);
                    }
                    else {

                        highlight_btn.startAnimation(hideHighlight);
                    }
                    highlight_backgr_btn.startAnimation(hideHighlightBackgr);
                     */
                    //Escondemos refresh en hideCenterControls
                    //if (refresh_btn.getVisibility() == View.VISIBLE) refresh_btn.startAnimation(hideRefresh);
                    controls_bottom.startAnimation(hideBottomControls);

                    //Desbloqueamos
                    programUnlock(hideBottomControls.getDuration());

                    areControlsVisible = false;
                }
            }
        }
	}

	/**
	 * Planifica el desvanecimiento de los controles.
	 * 2 modos. Rápido si hemos pulsado el botón de play, lento en otro caso.
	 */
	private void planificarDesvanecimientoControles(boolean quick){
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - planificarDesvanecimientoControles");
		
		//Cancelamos el temporizador y la tarea si ya existieran
		cancelDesvanecimientoControles();
		
		//Inicializamos temporizadores para el desvanecimiento de los controles
		fadeOutTimer = new Timer();
        fadeOutTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						
						Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - fadeOutTimerTask");
						ocultarControles(true);
					}
				});
			}
		};
		if (quick)
		{
			fadeOutTimer.schedule(fadeOutTimerTask, Constants.Autoplay.FADE_OUT_CONTROLS_QUICK);
		}
		else {
			
			fadeOutTimer.schedule(fadeOutTimerTask, Constants.Autoplay.FADE_OUT_CONTROLS);
		}
	}

	/**
	 * Cancelamos el temporizador y la tarea de planificacion del desvanecimiento de los controles.
	 */
	private void cancelDesvanecimientoControles() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - cancelDesvanecimientoControles");
		
		//Cancelamos el temporizador y la tarea si existen
		if (fadeOutTimer != null) fadeOutTimer.cancel();
		if (fadeOutTimerTask != null) fadeOutTimerTask.cancel();
	}

    /**
     * Muestra los controles al terminar el autoplay de una página
     */
    public void comienzoPagina() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - comienzoPagina ");

        paused = false;
        timerLleno = true;

        mPager.startTimer();
    }

	/**
	 * Muestra el botón con el icono de refresh para reiniciar el autoplay.
	 */
	public void finCuento() {
		
		Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - finCuento");
		//Paramos el autoplay y marcamos el flag de que hemos llegado al final de él.
		paused = true;
        finCuento = true;
		
		//Cancelamos desvanecimiento
		cancelDesvanecimientoControles();
        //Pausamos autoplay
        mPager.pauseTimer(false);
        //Mostramos controles
		mostrarControles(true);

        //Bloqueamos
        lockButtons();
        lockTap();

        //Programamos desbloqueo para cuando se haya mostrado refresh y like
        programUnlock(getResources().getInteger(R.integer.show_hide_refresh));

		//animación que muestra el icono de reinicio en el botón de play
		play_btn.startAnimation(heartBeatAutoplayEnd);
        like_btn.startAnimation(showLikeSet);
		progressBar.setVisibility(View.INVISIBLE);
		setButtonIconToReplay();
	}
	
	/**
	 * Realizamos animación del botón play y volvemos al menú principal
	 */
	private void exit() {

        Log.v(Constants.Log.CONTROLS, "ReadActivityAuto - exit");

		//Bloqueamos el botón para que no se ponga en marcha el autoplay durante la animación inicial
        lockButtons();
        //Bloqueamos el tap para que no se escondan los controles ni se pase de pag. durante la animación
        lockTap();

		cancelDesvanecimientoControles();

		if (!paused) {

            mPager.pauseTimer(true);
            paused = true;

            AnimationDrawable frameAnimation;

			//animación icono pause -> icono play
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play, null);
            }
            else{
                frameAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.frame_anim_pause_play);
            }

			play_btn.setImageDrawable(frameAnimation);
			frameAnimation.start();
		}
        //Si está pausado el temporizador y visible refresh, lo escondemos
        else if (refresh_btn.getVisibility() == View.VISIBLE) {

            refresh_btn.startAnimation(hideRefresh);
        }

        //Quitamos música
        MusicManager.fadeMusic();

		//play_btn.startAnimation(animPlayButtonExit);
        //Sin reveal ocultamos los controles superiores e inferiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            close_btn.startAnimation(hideCloseForReveal);
        }
        else {

            startExitBL();
        }
        progressBar.setVisibility(View.INVISIBLE);
	}

    /**
     * Indica si están visibles o no los controles
     * @return
     */
    public boolean areControlsVisible() {

        return areControlsVisible;
    }
    /**
     * Pone el icono de pause el botón de play/pause según densidad de pixel.
     */
    private void setButtonIconToPause() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setButtonIconToPause");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            play_btn.setImageResource(R.drawable.ic_pause_white_24dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE ) {
            play_btn.setImageResource(R.drawable.ic_pause_white_36dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE ) {
            play_btn.setImageResource(R.drawable.ic_pause_white_36dp);
        }
        else {
            play_btn.setImageResource(R.drawable.ic_pause_white_24dp);
        }
    }

    /**
     * Pone el icono de play el botón de play/pause según densidad de pixel.
     */
    private void setButtonIconToPlay() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setButtonIconToPlay");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask ==  Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            play_btn.setImageResource(R.drawable.ic_play_white_24dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            play_btn.setImageResource(R.drawable.ic_play_white_36dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            play_btn.setImageResource(R.drawable.ic_play_white_36dp);
        }
        else {
            play_btn.setImageResource(R.drawable.ic_play_white_24dp);
        }
    }

    /**
     * Cambia el icono del botón de play al terminarse el autoplay
     */
    private void setButtonIconToReplay() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setButtonIconToReplay");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask ==  Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            play_btn.setImageResource(R.drawable.ic_replay_white_24dp);
        }
        else if (mask ==  Configuration.SCREENLAYOUT_SIZE_LARGE) {
            play_btn.setImageResource(R.drawable.ic_replay_white_36dp);
        }
        else if (mask ==  Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            play_btn.setImageResource(R.drawable.ic_replay_white_36dp);
        }
        else {
            play_btn.setImageResource(R.drawable.ic_replay_white_24dp);
        }
    }

    /**
     * Pone el icono de voz de la barra de controles según densidad de pixel.
     */
    private void setVoiceIconOn() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setVoiceIconOn");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask ==  Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            voice_btn.setImageResource(R.drawable.ic_volume_high_white_18dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            voice_btn.setImageResource(R.drawable.ic_volume_high_white_24dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            voice_btn.setImageResource(R.drawable.ic_volume_high_white_24dp);
        }
        else {
            voice_btn.setImageResource(R.drawable.ic_volume_high_white_18dp);
        }
    }

    /**
     * Pone el icono de voz de la barra de controles según densidad de pixel.
     */
    private void setVoiceIconOff() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setVoiceIconOff");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            voice_btn.setImageResource(R.drawable.ic_volume_off_white_18dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            voice_btn.setImageResource(R.drawable.ic_volume_off_white_24dp);
        }
        else  if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            voice_btn.setImageResource(R.drawable.ic_volume_off_white_24dp);
        }
        else {
            voice_btn.setImageResource(R.drawable.ic_volume_off_white_18dp);
        }
    }

    /**
     * Pone el icono de música de la barra de controles según densidad de pixel.
     */
    private void setMusicIconOn() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setMusicIconOn");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            music_btn.setImageResource(R.drawable.ic_music_note_white_18dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE ) {
            music_btn.setImageResource(R.drawable.ic_music_note_white_24dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE ) {
            music_btn.setImageResource(R.drawable.ic_music_note_white_24dp);
        }
        else {
            music_btn.setImageResource(R.drawable.ic_music_note_white_18dp);
        }
    }

    /**
     * Pone el icono de música de la barra de controles según densidad de pixel.
     */
    private void setMusicIconOff() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - setMusicIconOff");

        final int mask = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (mask == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            music_btn.setImageResource(R.drawable.ic_music_note_off_white_18dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            music_btn.setImageResource(R.drawable.ic_music_note_off_white_24dp);
        }
        else if (mask == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            music_btn.setImageResource(R.drawable.ic_music_note_off_white_24dp);
        }
        else {
            music_btn.setImageResource(R.drawable.ic_music_note_off_white_18dp);
        }
    }

    // This snippet hides the system bars.
    private void hideSystemUI() {

        Log.w(Constants.Log.METHOD, "ReadActivityAuto - hideSystemUI");

        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
	protected void onPause() {

    	Log.v(Constants.Log.METHOD, "ReadActivityAuto - OnPause");

        //Hay que cancelar el desvanecimiento ya que sigue en segundo plano
        cancelDesvanecimientoControles();

    	if (!paused) {

            mPager.pauseTimer(false);
			paused = true;
			setButtonIconToPlay();
		}
    	resumeShowControls = true;

        //Pausamos música
        MusicManager.pauseMusic();
    	
    	//Debug.stopMethodTracing();
		super.onPause();
	}

    @Override
    protected void onRestart() {
        Log.v(Constants.Log.METHOD, "ReadActivityAuto - onRestart");

        MusicManager.createMusicRestart(false);
        MusicManager.createVoiceRestart(tiempo);

        super.onRestart();
    }

    @Override
    protected void onStop() {

        Log.v(Constants.Log.METHOD, "ReadActivityAuto - onStop");

        if (!continue_music) MusicManager.stopMusic();
        MusicManager.stopVoice();

        super.onStop();
    }

	@Override
	protected void onDestroy() {
		
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - OnDestroy");
		
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - OnResume");

        //Escondemos barras
        hideSystemUI();

		//Mostrar los controles con el autoplay parado.
		if (resumeShowControls) {

            //Desbloqueamos siempre por si hubiera lock fallido
            unlockButtons();
            unlockTap();

            mostrarControles(false);
        }

		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.v(Constants.Log.METHOD, "ReadActivityAuto - onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}
	
    @Override
    public void onBackPressed() {
    	
    	Log.v(Constants.Log.METHOD, "ReadActivityAuto - onBackPressed");

        //Desbloqueamos siempre por si hubiera lock fallido
        unlockForced();

    	if (!areButtonsLocked()) {
    		
    		if (mPager.getCurrentItem() == 0) {
            	Log.v(Constants.Log.METHOD, "ReadActivityAuto - onBackPressed - Primera página");
                exit();
                
            } else {
            	Log.v(Constants.Log.METHOD, "ReadActivityAuto - onBackPressed - Retrocede página ");
                //Mostramos si estuvieran escondidos
                mostrarControles(true);
                // Retrocedemos una página
            	pageBack();
            }
    	}
    }

}
