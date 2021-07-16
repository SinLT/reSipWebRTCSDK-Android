package com.reSipWebRTC.util;

import java.util.HashMap;
import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.reSipWebRTC.R;

public class PlaySoundPool {

	  private SoundPool soundPool;
	  private HashMap<Integer,Integer> soundMap;
	  private Context context;
	  
	  public final static int SOUNT_NUM_1 = 0;
	  public final static int SOUNT_NUM_2 = 1;
	  public final static int SOUNT_NUM_3 = 2;
	  public final static int SOUNT_NUM_4 = 3;
	  public final static int SOUNT_NUM_5 = 4;
	  public final static int SOUNT_NUM_6 = 5;
	  public final static int SOUNT_NUM_7 = 6;
	  public final static int SOUNT_NUM_8 = 7;
	  public final static int SOUNT_NUM_9 = 8;
	  public final static int SOUNT_STAR = 9;
	  public final static int SOUNT_NUM_0 = 10;
	  public final static int SOUNT_HASH = 11;
	  public final static int SOUNT_TONE = 12;
	  public final static int SOUNT_MSG = 13;
	  
	  public static PlaySoundPool playSoundPool;
	  public static PlaySoundPool getInstance(Context context){
		  if(playSoundPool == null)
			  playSoundPool = new PlaySoundPool(context);
		  return playSoundPool;
	  }
	  Vibrator vibrator;
	  private PlaySoundPool(Context context){
	    
		this.context = context;
	    soundPool = new SoundPool(2,AudioManager.STREAM_MUSIC,0);
	    soundMap = new HashMap<Integer,Integer>();
	    
	    /*soundMap.put(SOUNT_NUM_1, soundPool.load(context, R.raw.dtmf_1, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_2, soundPool.load(context, R.raw.dtmf_2, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_3, soundPool.load(context, R.raw.dtmf_3, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_4, soundPool.load(context, R.raw.dtmf_4, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_5, soundPool.load(context, R.raw.dtmf_5, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_6, soundPool.load(context, R.raw.dtmf_6, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_7, soundPool.load(context, R.raw.dtmf_7, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_8, soundPool.load(context, R.raw.dtmf_8, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_9, soundPool.load(context, R.raw.dtmf_9, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_STAR, soundPool.load(context, R.raw.dtmf_star, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_NUM_0, soundPool.load(context, R.raw.dtmf_0, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_HASH, soundPool.load(context, R.raw.dtmf_hash, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_TONE, soundPool.load(context, R.raw.hold_tone, 1));  //添加第一个声音，代号1
	    soundMap.put(SOUNT_MSG, soundPool.load(context, R.raw.message, 1));  //添加第一个声音，代号1*/
	    vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
	  }
	  
	  public void playSound(int dtmf_index){
	 
		  AudioManager am = (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
		  float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		  float audioCurrentVolumn = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		  float volumnRatio = audioCurrentVolumn/audioMaxVolumn;
		  
		  //选择代号为    sound  的声音
		  if(dtmf_index <= SOUNT_HASH){
			  if(vibrator != null)
			      vibrator.vibrate(60);
		  }
		  //Debug.i("SoundPool", "soundPool.play = " + dtmf_index);
		  soundPool.play(soundMap.get(dtmf_index), volumnRatio, volumnRatio, 1, 0, 1);
	  }
	  
	  public void vibration()
	  {
		  if(vibrator != null)
		      vibrator.vibrate(60);
	  }
	  
	  public void destory(){
		  if(vibrator != null){
			  vibrator.cancel();
			  vibrator = null;
		  }
		  if(playSoundPool != null)
			  playSoundPool.destory();
		  playSoundPool = null;
	  }
}
