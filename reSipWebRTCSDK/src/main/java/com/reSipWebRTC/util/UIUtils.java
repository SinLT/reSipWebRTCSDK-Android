package com.reSipWebRTC.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.reSipWebRTC.R;

public class UIUtils {
	public static float density; //得到密度
	public static float width;//得到宽度
	public static float height;//得到高度
	
	private static final String TAG=UIUtils.class.getSimpleName();
	public static LayoutInflater getLayoutInflater(Context context) {

		return (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/***
	 * 
	 * @param bitmap
	 * @return
	 */
	public static byte[] bitmap2Byte(Bitmap bitmap){
		if (bitmap != null) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return byteArray;
		}
		return null;
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
	    float scale = context.getResources().getDisplayMetrics().density;
		if(scale>2.0)
		scale=(float)2.0;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int getDimensionPixelSize(Context context, int dipId) {

		return context.getResources().getDimensionPixelSize(dipId);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float px2Sp(Context context, float px) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return px / scaledDensity;
	}

	public static float sp2Pix(Context context, float sp) {

		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return sp * scaledDensity;

	}

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Activity context) {

		return context.getWindowManager().getDefaultDisplay().getHeight();

	}

	public static int getScreenWidth(Context context) {

		return ((Activity) context).getWindowManager().getDefaultDisplay()
				.getWidth();

	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Activity context) {

		return context.getWindowManager().getDefaultDisplay().getWidth();

	}

	
	/** 
	 * 返回当前程序版本名 
	 */  
	public static String getAppVersionName(Context context) {  
	    String versionName = "";  
	    try {  
	        // ---get the package info---  
	        PackageManager pm = context.getPackageManager();  
	        PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);  
	        versionName = pi.versionName;  
	        if (versionName == null || versionName.length() <= 0) {  
	            return "";  
	        }  
	    } catch (Exception e) {  
	    	e.printStackTrace();
	        Debug.e(TAG,"Exception ");  
	    }  
	    return versionName;  
	}  
	
	
	public static void hideSoftInput(Context context) {
		View focusView = ((Activity) context).getCurrentFocus();
		if (focusView == null)
			return;
		// ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(focusView.getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
	}

	public static void showSoftInput(Context context) {
		// ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(focusView,
		// 0);
		((InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(InputMethodManager.SHOW_FORCED,
						InputMethodManager.HIDE_IMPLICIT_ONLY);

	}

	/**
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, String text) {

		if (context == null) {
			return;
		}
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}


	// public static Dialog showLoadingDialog(Context context) {
	//
	// Dialog dialog = new Dialog(context, R.style.myLoadingDialogTheme);
	// dialog.setContentView(R.layout.dialog_loading);
	// dialog.show();
	// return dialog;
	// }
	//
	// public static Dialog showLoadingDialog(Context context, String text) {
	//
	// Dialog dialog = new Dialog(context, R.style.myDialogTheme);
	// dialog.setContentView(R.layout.dialog_loading);
	// TextView textView = (TextView) dialog.findViewById(R.id.tv_loading_text);
	// textView.setText(text);
	// dialog.show();
	// return dialog;
	// }

	/**
	 *  转义特殊字符
	 * @param target
	 * @return
	 */
	public static String  convertSql(String target){
		target = target.replace("[","[[]"); 
	
		target = target.replace("_","[_]"); 
		target = target.replace("%","[%]"); 
		return target;
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static boolean checkCameraAndChoiceBetter() {

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			int count = Camera.getNumberOfCameras();

			for (int i = 0; i < count; i++) {

				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(i, info);

				if (info != null) {

					if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
						return true;
					}
				}
			}
			return false;
		}

		return false;
	}

	public static int getNumberOfCameras() {
		int count = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			count = Camera.getNumberOfCameras();
		}
		return count;
	}
	
	public static File getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir;
	}

	public static boolean hasInternet(Activity activity) {

		ConnectivityManager manager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			// UIUtils.showToast(activity, "网络链接失败");

			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false

			return true;
		}
		return true;

	}

	/**
	 * 播放手机铃声
	 */
	public static MediaPlayer playCallRing(Context context) {

		Uri ringUri = RingtoneManager.getActualDefaultRingtoneUri(context,
				RingtoneManager.TYPE_RINGTONE);
		if (ringUri == null) {
			return null;
		}
		MediaPlayer mMediaPlayer = MediaPlayer.create(context, ringUri);
		mMediaPlayer.setLooping(true);

		return mMediaPlayer;
	}

	public static String[] devideData(String date) {

		String[] str = new String[2];
		int len = date.length();
		int pos = date.indexOf(' ');
		str[0] = date.substring(0, pos).trim();
		str[1] = date.substring(pos + 1, len).trim();
		return str;
	}

	/**
	 * 手机震动
	 */
	public static Vibrator Vibrate(final Context context, boolean isRepeat) {

		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(new long[] { 1000, 1000, 1000, 1000, 1000 }, isRepeat ? 1
				: -1);
		return vib;

	}

	public static Vibrator msgVibrate(final Context context, boolean isRepeat) {

		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(new long[] { 100,10,100,100 }, isRepeat ? 1
				: -1);
		return vib;

	}

	public static Vibrator keyVibrate(final Context context, boolean isRepeat) {

		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);

		vib.vibrate(new long[] { 60,10,60,60 }, isRepeat ? 1
				: -1);
		return vib;
	}
	
	public static String base64Encode(String str){
		
		byte[] bytes = str.getBytes();
		String strOut = Base64.encodeToString(bytes, Base64.DEFAULT);
		//Debug.i("UIUitls","    base64Encode (strOut = "+ strOut  + ")");
		strOut = strOut.replaceAll(" ", "").replaceAll("\r", "").replaceAll("\n", "");
		return strOut;
	}
	
	public static String convertImgUrl(String str){
		
		String picUrl = base64Encode(str);
		picUrl = picUrl+".jpg";
		return picUrl;
	}
	
	/**
	 * 去电等待
	 * 
	 * @param context
	 * @return
	 */
	public static MediaPlayer phoneCallingtone(Context context, int callingtone){
		MediaPlayer mp = null;
		try {
			mp = MediaPlayer.create(context, callingtone);
			mp.setLooping(true);
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		return mp;
	}
	
	/**
	 * 播放对方保持声音
	 * 
	 * @param context
	 * @return
	 */
	public static MediaPlayer playHoldSound(Context context){
		MediaPlayer mp = null;
		/*try {
			mp = MediaPlayer.create(context, R.raw.hold_tone);
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}*/
		return mp;
	}
	
	/**
	 * 来电声音
	 * 
	 * @param context
	 * @return
	 */
	public static MediaPlayer phoneSound(Context context, int ringtone){
		MediaPlayer mp = null;
		try {
			mp = MediaPlayer.create(context, ringtone);
			mp.setLooping(true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 
		return mp;
	}
	
	/**
	 * 短信提示音乐
	 * 
	 * @param context
	 * @return
	 */
	public static MediaPlayer shortMsgSound(Context context){
		MediaPlayer mp = null;
		/*try {
			mp = MediaPlayer.create(context, R.raw.message);
			mp.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}*/
		return mp;
	}

	public static boolean isActivityRunning(Context context) {
		boolean temp = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals("com.cloudwebrtc")
					&& info.baseActivity.getPackageName().equals("com.cloudwebrtc")) {
				Debug.i(TAG, "正在运行当前应用");
				temp = true;
				break;
			}
		}
		return temp;
	}

	/**
	 * 拨打普通电话
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void call(Context context, String phoneNumber) {
		//Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
		//		+ phoneNumber));
		//context.startActivity(intent);
	}

	public static String formatCurrentData() {
		SimpleDateFormat simDateFromat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String time = simDateFromat.format(new Date());
		return time;
	}

	public static String formatData() {
		SimpleDateFormat simDateFromat = new SimpleDateFormat(
				"yyyy-MM-dd");
		String time = simDateFromat.format(new Date());
		return time;
	}
	
	public static void showNotificationWhenCall(Context context,String peer_caller,boolean set_beechat_ringing,boolean set_beechat_shock,boolean is_video_call){
		/*String service = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(service); // get

		int icon = R.drawable.bg_telephone;
		//int icon = R.drawable.ic_chat;
		long when = System.currentTimeMillis();
		// 新建一个通知，指定其图标和标题
		// 第一个参数为图标，第二个参数为标题，第三个参数为通知时间
		String str = context.getResources().getString(R.string.comming);
		Notification notification = new Notification(icon, str, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		//notification.icon
		if(set_beechat_ringing && !set_beechat_shock){
			notification.defaults = Notification.DEFAULT_SOUND;
		}else if(!set_beechat_ringing && set_beechat_shock){
			notification.defaults = Notification.DEFAULT_VIBRATE;
			long[] vibreate= new long[]{1000,1500,1500,1500,1500};
			notification.vibrate = vibreate;
		}else if(set_beechat_ringing && set_beechat_shock){
			notification.defaults = Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE;// 发出默认声音
			long[] vibreate= new long[]{1000,1500,1500,1500,1500};
			notification.vibrate = vibreate;
		}else {
			notification.defaults = Notification.DEFAULT_LIGHTS;
		}
		
		// 当点击消息时就会向系统发送openintent意图
		Intent intent;
		if(is_video_call){
			intent = new Intent(context, VideoCallActivity.class);  
		    intent.putExtra(Contacts.PHONESTATE, Contacts.RECEIVE_VIDEO_REQUEST);
		    intent.putExtra(Contacts.PHONNUMBER, peer_caller);
		}else{
			intent = new Intent(context, VoiceCallActivity.class);  
		    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		    intent.putExtra(Contacts.PHONESTATE, Contacts.PHONESTATE_INCOMMING);
		    intent.putExtra(Contacts.PHONNUMBER, peer_caller);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		String code = SettingSharedPreference.getSharedPreferenceUtils().getDate(context, Contacts.SHARE_AREA_CODE, Contacts.COUNTRY_CODE);
		String strNum = ContactsUtil.convertPrefix(peer_caller, code);
		Contact contact = BeeChatDataBase.getBeeChatDataBase().queryFriendByNumber(context.getContentResolver(), strNum);
		String friendName = "";
		if(contact!= null){
			if(TextUtils.isEmpty(contact.name)){
				friendName = contact.beechatPhone;
			}
		}else{
			friendName = peer_caller;
		}
		
		notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name), friendName+str,
				contentIntent);
		nm.notify(Contacts.NOTIFICATION_ID, notification);// 发送通知*/
	}
	
	public static void showNotificationWhenMsg(Context context,String peer_caller,String shortmsg){
		/*String service = Context.NOTIFICATION_SERVICE;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(service); // get

		int icon = R.drawable.ic_tel_h;
		//int icon = R.drawable.ic_chat;
		long when = System.currentTimeMillis();
		// 新建一个通知，指定其图标和标题
		// 第一个参数为图标，第二个参数为标题，第三个参数为通知时间
		String str = context.getResources().getString(R.string.call_missed);
		//notification.icon
		
		String peer_name = peer_caller;
		String code = SettingSharedPreference.getSharedPreferenceUtils().getDate(context, Contacts.SHARE_AREA_CODE, Contacts.COUNTRY_CODE);
		String strNum = ContactsUtil.convertPrefix(peer_caller, code);
		Contact contact = BeeChatDataBase.getBeeChatDataBase().queryFriendByNumber(context.getContentResolver(), strNum);
		if(contact != null){
			peer_name = contact.name;
		}else{
			contact = new Contact();
			contact.beechatPhone = peer_caller;
		}
		
		if(shortmsg.startsWith("<img>")){
			shortmsg = context.getResources().getString(R.string.shortmsg_img);
		}
		
		Notification notification = new Notification(icon, peer_name+": "+shortmsg, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		// 当点击消息时就会向系统发送openintent意图
		Intent intent = new Intent(context, ApplicationMainActivity.class); 
		//intent.putExtra(Contacts.SELECT_ITEM, contact);
		//intent.putExtra(Contacts.CALLDIRECTION, Contacts.CALLFROM_CHATMSG);
	    //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		String friendName = peer_caller;

		notification.setLatestEventInfo(context, context.getResources().getString(R.string.app_name)+" "+str, peer_name+": "+shortmsg,
				contentIntent);
		nm.notify(Contacts.NOTIFICATION_ID, notification);// 发送通知
		
		ApplicationMainActivity.reCallCount = 1;*/
	}	
	
	/**
	 * 注册无响应 = 0, 注册成功 = 200, 未认证 = 401, 账户或密码错误 = 403, 账户不存在 = 404, 代理服务器认证失败 =
	 * 407, 不允许注册 = 606, 正在处理=100, 注销成功=99, 网络不可用=-1,
	 */
	public static String showReqStatus(int status, Context context) {
		String result = null;
		/*switch (status) {
		case 0:
			result = context.getResources().getString(R.string.norep);
			break;
		case 200:
			result = context.getResources().getString(R.string.regsucss);
			break;
		case 401:
			result = context.getResources().getString(R.string.nocard);
			break;
		case 403:
			result = context.getResources().getString(R.string.wrongnp);
			break;
		case 404:
			result = context.getResources().getString(R.string.noexist);
			break;
		case 407:
			result = context.getResources().getString(R.string.proxynocard);
			break;
		case 606:
			result = context.getResources().getString(R.string.notallowreg);
			break;
		case 100:
			result = context.getResources().getString(R.string.doing);
			break;
		case 99:
			result = context.getResources().getString(R.string.sucss);
			break;
		case -1:
			result = context.getResources().getString(R.string.netnouse);
			break;
		}*/
		return result;
	}
	
	/**
	 *  通话结果的返回值
	 * @param status
	 * @param context
	 * @return
	 */
    public static String showCallReqStatus(int status, Context context) {
		String result = null;
		/*switch (status) {
		case 401:
			result = context.getResources().getString(R.string.call_result_data_wrong);
			break;
		case 400:
			result = context.getResources().getString(R.string.call_result_wrong_req);
			break;
		case 403:
			result = context.getResources().getString(R.string.call_result_auth_wrong);
			break;
		case 405:
			result = context.getResources().getString(R.string.call_result_noallow_call);
			break;
		case 407:
			result = context.getResources().getString(R.string.call_result_proserver_fail);
			break;
		case 408:
			result = context.getResources().getString(R.string.call_result_time_out);
			break;
		case 404:
			result = context.getResources().getString(R.string.call_result_noexist_regist);
			break;
		case 415:
			result = context.getResources().getString(R.string.call_result_nosupport_media);
			break;
		case 486:
			result = context.getResources().getString(R.string.call_result_busy);
			break;
		case 480:
			result = context.getResources().getString(R.string.call_result_not_conned);
			break;
		case 487:
			result = context.getResources().getString(R.string.call_result_reqterminated);
			break;
		case 500:
			result = context.getResources().getString(R.string.call_result_internal_error);
			break;
		case 600:
			result = context.getResources().getString(R.string.call_result_disturb);
			break;
		case 603:
			result = context.getResources().getString(R.string.call_result_other_line);
			break;
		case 477:
			result = "477";
			break;
		}*/
		
		return result;
	}
    
    
    public static String getHourAndMin(Context context){
    	 // 获取24小时制显示的时间
    	 SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
         Date currentTime = new Date(System.currentTimeMillis());
         String time = format1.format(currentTime);
         // 判断系统时间
         boolean is24 =  DateFormat.is24HourFormat(context);   
         if(!is24){
        	 String[] times = time.split(":");
             String hour = times[0];
             int target = 0;
             // 如果是以0开头的数字则，掉0;
             if("0".equals(hour.charAt(0))){
            	  target = Integer.valueOf(hour.charAt(1));
              }else{
            	  target = Integer.valueOf(hour);
              }
             //如果小于12 则为上午
        	 if(target < 12){
        		return time+" AM";
        	 }else{
        		return time+" PM";
        	 }
         }
         
         return time;
    }
    
    /**
     *  将秒转换成 00：00：00 形式
     * @param seconds
     * @return
     */
    public static String changeSecond2Min(String seconds){
    	long sec = Long.valueOf(seconds);
		String result = "";
		 if(sec <= 0){
			 result = "00:00";
		 }else{
			 long hour = sec / 3600;
			 long resNum = sec % 3600;
			 long min = resNum / 60;
			 resNum = resNum % 60;
			 long second = resNum;
			 if(hour != 0){
				String strHour =  String.valueOf(hour);
				if(strHour.length() < 2){
					strHour = "0"+strHour;
				}
				result = strHour+":";
			 }
			String strMin =  String.valueOf(min);
			if(strMin.length() < 2){
				strMin = "0"+strMin;
			}
			result = result + strMin +":";
			String strSecond =  String.valueOf(second);
			if(strSecond.length() < 2){
				strSecond = "0"+strSecond;
			}
			result = result + strSecond;
		 }
		 
		 return result;
	}
    
    public static String[] divideTime(String date){
    	
    	String[] str = date.split(" ");
    	int index = str[1].lastIndexOf(":");
    	str[1] = str[1].substring(0,index);
    	return str;
    }
}
