package com.android.cuentacuentoshl.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;

import com.android.cuentacuentoshl.R;


/**
 * Created by quayo on 14/03/2016.
 */
public class MusicManager {

    /**
     * Array con las referencias a los audios con la voces
     */
    private final static Integer[] voices = new Integer[] {

            R.raw.vicen_smurf_001_i2_sp75_t10, R.raw.vicen_smurf_002_i2_sp75_t10_200cut
    };

    private static MediaPlayer mediaPlayerMusic;
    private static MediaPlayer mediaPlayerVoice;
    private static float currVolumeMusic = Constants.Audio.MAX_MUSIC_LIMITED;
    private static float currVolumeVoice = Constants.Audio.MAX_VOICE_LIMITED;
    private static CountDownTimer fadeMusicTimer;
    /**
     * página en la que se encuentra el autoplay
     */
    private static int mPage;
    private static Context mContext;

    /**
     * Constante que controla si la música esta en off o en on.
     */
    private static boolean music_on = Constants.Autoplay.MUSIC_INICIO;
    private static boolean voice_on = Constants.Autoplay.VOLUME_INICIO;

    public static boolean isMusic_on() {
        return music_on;
    }
    public static void setMusic_on(boolean music_on) {
        MusicManager.music_on = music_on;
    }
    public static boolean isVoice_on() {
        return voice_on;
    }
    public static void setVoice_on(boolean voice_on) {
        MusicManager.voice_on = voice_on;
    }
    /**
     * Constante que controla si la música esta en off o en on.
     */
    private static boolean voice_finished = false;
    public static void setVoice_finished(boolean voice_finished) {
        MusicManager.voice_finished = voice_finished;
    }

    //Recuerdan si la música o la voz estaban activas o no al salir de la actividad
    private static boolean wasMainMusicOn = true;
    private static boolean wasReadMusicOn = true;
    private static boolean wasReadVoiceOn = true;

    public static void setWasMainMusicOn(boolean wasMainMusicOn) {
        MusicManager.wasMainMusicOn = wasMainMusicOn;
    }
    public static void setWasReadMusicOn(boolean wasReadMusicOn) {
        MusicManager.wasReadMusicOn = wasReadMusicOn;
    }
    public static void setWasReadVoiceOn(boolean wasReadVoiceOn) {
        MusicManager.wasReadVoiceOn = wasReadVoiceOn;
    }
    public static boolean wasMainMusicOn() {
        return wasMainMusicOn;
    }
    public static boolean wasReadMusicOn() {
        return wasReadMusicOn;
    }
    public static boolean wasReadVoiceOn() {
        return wasReadVoiceOn;
    }


    public static void build(Context context) {

        Log.v(Constants.Log.AUDIO, "MusicManager - build");

        mContext = context;

        //Preparamos timer para desvanecer la música una vez pasamos a la siguiente actividad
        fadeMusicTimer = new CountDownTimer(Constants.Audio.FADE_MUSIC_TIME,
                Constants.Audio.FADE_MUSIC_TIME / Constants.Audio.FADE_MUSIC_INTERVALS) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.v(Constants.Log.AUDIO, "MusicManager - fadeMusicTuner.onTick");

                currVolumeMusic = Constants.Audio.MAX_MUSIC_LIMITED *
                        ( (float) millisUntilFinished / (float) Constants.Audio.FADE_MUSIC_TIME );

                //currVolumeMusic -= (float) Constants.Audio.MAX_MUSIC / (float) Constants.Audio.FADE_MUSIC_INTERVALS;

                float log1=(float)(Math.log((float)Constants.Audio.MAX_MUSIC- currVolumeMusic)/ Math.log(Constants.Audio.MAX_MUSIC));

                Log.v(Constants.Log.AUDIO, "Volume = "+ currVolumeMusic +" - Millis = "+millisUntilFinished+" - log = "+log1);

                if (mediaPlayerMusic != null) mediaPlayerMusic.setVolume(1 - log1, 1 - log1);
            }

            @Override
            public void onFinish() {

                Log.v(Constants.Log.AUDIO, "MusicManager - fadeMusicTuner.onFinish");

                if (mediaPlayerMusic != null) {

                    mediaPlayerMusic.release();
                    mediaPlayerMusic = null;
                }
            }
        };
    }

    public static void createMusicRestart(boolean intro) {

        Log.v(Constants.Log.AUDIO, "MusicManager - createMusicRestart");

        if (mediaPlayerMusic == null) createMusic(intro);
    }

    public static void createMusic(boolean intro) {

        Log.v(Constants.Log.AUDIO, "MusicManager - createMusic");

        if (fadeMusicTimer != null) {
            fadeMusicTimer.cancel();
        }

        if (mediaPlayerMusic != null) {
            mediaPlayerMusic.release();
            mediaPlayerMusic = null;
        }

       if (intro) {

           mediaPlayerMusic = MediaPlayer.create(mContext, R.raw.melodie_victoria);
       }
        else {
           mediaPlayerMusic = MediaPlayer.create(mContext, R.raw.music_box_theme);
       }

        mediaPlayerMusic.setLooping(true);
        currVolumeMusic = Constants.Audio.MAX_MUSIC_LIMITED;

        if (music_on)
        {
            setMusicVolumeOn();
        } else {
            setMusicVolumeOff();
        }
    }

    public static void startResumeMusic() {

        Log.v(Constants.Log.AUDIO, "MusicManager - startResumeMusic");

        if ((mediaPlayerMusic != null) && (!mediaPlayerMusic.isPlaying()))
            mediaPlayerMusic.start();
    }

    public static void pauseMusic() {

        Log.v(Constants.Log.AUDIO, "MusicManager - pauseMusic");

        if ((mediaPlayerMusic != null) && (mediaPlayerMusic.isPlaying()))
            mediaPlayerMusic.pause();
    }

    public static void stopMusic() {

        Log.v(Constants.Log.AUDIO, "MusicManager - stopMusic");

        if (fadeMusicTimer != null) {
            fadeMusicTimer.cancel();
        }

        if (mediaPlayerMusic != null) {

            mediaPlayerMusic.release();
            mediaPlayerMusic = null;
        }
    }

    public static void fadeMusic() {

        Log.v(Constants.Log.METHOD, "MusicManager - fadeMusic");

        if (music_on) fadeMusicTimer.start();
    }

    public static void setMusicVolumeOn() {

        Log.v(Constants.Log.AUDIO, "MusicManager - setMusicVolumeOn");

        float log1 = (float) (Math.log((float)Constants.Audio.MAX_MUSIC- currVolumeMusic) / Math.log(Constants.Audio.MAX_MUSIC));
        Log.v(Constants.Log.AUDIO, "currVolumeMusic = " + currVolumeMusic + " log1 = " + log1);
        if (mediaPlayerMusic != null) mediaPlayerMusic.setVolume(1 - log1, 1 - log1);
    }

    public static void setMusicVolumeOff() {

        Log.v(Constants.Log.AUDIO, "MusicManager - setMusicVolumeOff");

        if (mediaPlayerMusic != null) mediaPlayerMusic.setVolume(0, 0);
    }

    public static void createVoiceRestart(int tiempo) {

        Log.v(Constants.Log.AUDIO, "MusicManager - createVoiceRestart");

        if (mediaPlayerVoice == null) createVoice(mPage);

        //Ponemos la voz donde se paró
        //Cuando supera al tiempo de duración del audio no arranca al hacer start
        mediaPlayerVoice.seekTo(tiempo);
    }

    public static void createVoice(int page) {

        Log.v(Constants.Log.AUDIO, "MusicManager - createVoice");

        if (mediaPlayerVoice != null) {
            mediaPlayerVoice.release();
            mediaPlayerVoice = null;
        }

        if (page < voices.length) {

            mediaPlayerVoice = MediaPlayer.create(mContext, voices[page]);
            currVolumeVoice = Constants.Audio.MAX_VOICE_LIMITED;

            if (voice_on)
            {
                setVoiceVolumeOn();
            }
            else {
                setVoiceVolumeOff();
            }
        }
        else {

            Log.e(Constants.Log.AUDIO, "MusicManager - createVoice - Page overflow -");

            mediaPlayerVoice = MediaPlayer.create(mContext, voices[0]);
            currVolumeVoice = Constants.Audio.MAX_VOICE_LIMITED;

            if (voice_on)
            {
                setVoiceVolumeOn();
            }
            else {
                setVoiceVolumeOff();
            }
        }

        mediaPlayerVoice.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                Log.w(Constants.Log.AUDIO, "MusicManager - mediaPlayerVoice.onCompletion");
                voice_finished = true;
            }
        });

        voice_finished = false;
        mPage = page;
    }

    public static void startResumeVoice() {

        Log.v(Constants.Log.AUDIO, "MusicManager - startResumeVoice");

        //Si no estuviera preparado el mediaplayer
        if (mediaPlayerVoice == null) createVoice(mPage);

        if (!mediaPlayerVoice.isPlaying() && !voice_finished) mediaPlayerVoice.start();
    }

    public static void pauseVoice() {

        Log.v(Constants.Log.AUDIO, "MusicManager - pauseVoice");

        //Si no estuviera preparado el mediaplayer, utilizado en la 1a página
        if (mediaPlayerVoice == null) createVoice(mPage);

        if (mediaPlayerVoice.isPlaying()) mediaPlayerVoice.pause();
    }

    public static void stopVoice() {

        Log.v(Constants.Log.AUDIO, "MusicManager - stopVoice");

        if (mediaPlayerVoice != null) {

            mediaPlayerVoice.release();
            mediaPlayerVoice = null;
        }
    }

    public static void setVoiceVolumeOff() {

        Log.v(Constants.Log.AUDIO, "MusicManager - setVoiceVolumeOff");

        //Si no estuviera preparado el mediaplayer
        if (mediaPlayerVoice == null) createVoice(mPage);
        mediaPlayerVoice.setVolume(0, 0);
    }

    public static void setVoiceVolumeOn() {

        Log.v(Constants.Log.AUDIO, "MusicManager - setVoiceVolumeOn");

        //Si no estuviera preparado el mediaplayer
        if (mediaPlayerVoice == null) createVoice(mPage);

        float log1 = (float) (Math.log((float)Constants.Audio.MAX_MUSIC-currVolumeVoice) / Math.log(Constants.Audio.MAX_MUSIC));
        Log.v(Constants.Log.AUDIO, "currVolumeMusic = "+currVolumeVoice+" log1 = "+ log1);
        mediaPlayerVoice.setVolume(1-log1, 1-log1);
    }


}
