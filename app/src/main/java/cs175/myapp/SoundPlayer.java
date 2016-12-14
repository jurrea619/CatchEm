package cs175.myapp;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 * Created by joshua on 12/9/16.
 */

/*
Sound player class to handle gameplay sounds like ball catching and hit collision
 */
public class SoundPlayer {

    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 2;

    private static SoundPool soundPool;
    private static int hitSound;
    private static int overSound;

    public SoundPlayer(Context context){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

            soundPool = new SoundPool.Builder().setMaxStreams(SOUND_POOL_MAX).build();
        }
        else{
            //soundpool (int maxStreams, int streamType, srcQuality)
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        hitSound = soundPool.load(context, R.raw.hit, 1);
        overSound = soundPool.load(context, R.raw.game_over, 1);
    }

    public void playHitSound(){

        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(hitSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playGameOverSound(){
        // play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate)
        soundPool.play(overSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

}
