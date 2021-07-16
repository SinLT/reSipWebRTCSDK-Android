package com.reSipWebRTC.service;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.reSipWebRTC.sdk.CallLogBean;
import com.reSipWebRTC.sdk.CallMediaStatsReport;
import com.reSipWebRTC.sdk.CallStreamListener;
import com.reSipWebRTC.sdk.SipCallConnectedListener;
import com.reSipWebRTC.sdk.SipCallEndListener;
import com.reSipWebRTC.sdk.SipIncomingCallListener;
import com.reSipWebRTC.sdk.SipOutgoingCallListener;
import com.reSipWebRTC.sdk.SipRegisterListener;
import com.reSipWebRTC.util.Debug;
import com.reSipWebRTC.util.Direction;
import com.reSipWebRTC.util.RegistrationErrorCode;
import com.reSipWebRTC.util.UIUtils;
import com.reSipWebRTC.util.Version;
import com.reSipWebRTC.util.reSipWebRTCAudioManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

public class PhoneService implements RegistrationEventListener,
        CallStateEventListener, IncomingCallObserver {
    private final String TAG = PhoneService.class.getSimpleName();
    private static PhoneService the_service_instance_ = null;//new PhoneService();
    private MediaPlayer mediaPlayer;
    private Account current_account = null;
    //private Call current_call = null;
    private String transport_data = null;
    private boolean dozeModeEnabled = false;
    private CallState callState = CallState.fromInt(CallState.Unknown.IntgerValue());
    private int mLastNetworkType = -1;
    private ConnectivityManager mConnectivityManager;
    private Direction dir;
    private RegistrationManager mRegistrationManager = null;
    private CallManager mCallManager = null;
    private static SipEngine the_sipengine_ = null;
    private Vibrator vib = null;
    private int _maxVolume = 0; // Android max level (commonly 5)
    private int _volumeLevel = 200;
    public String  username = "";
    public String  password = "";
    public String  server = "";
    public int port = 0;
    public int trans_type = 1;
    private boolean networkStateReachable = false;
    Handler mainHandler = null;
    Timer mTimer = new Timer("SipEngine scheduler");
    private AccountConfig current_AccountConfig = null;
    private Context mContext;
    private PowerManager.WakeLock mCpuLock;
    final int REGIST_TIMER_PERIOD = 20*1000;//
    final int REGIST_TIMER_ATONCE = 300;
    final int REGIST_MAX_RETRY = 15;
    int retryTime = 0;
    Handler handler = null;

    public static boolean isready() {
        return (the_service_instance_ != null);
    }

    public static PhoneService instance() {
        if (the_service_instance_ == null) {
            the_service_instance_ = new PhoneService();
        } else {
        }
        return the_service_instance_;
    }

    //@Override
    /*public IBinder onBind(Intent intent) {
        return null;
    }*/

    //add by david.xu
    private SipRegisterListener mSipRegisterListener;
    private SipCallConnectedListener mSipCallConnectedListener;
    private SipIncomingCallListener mSipIncomingCallListener;
    private SipOutgoingCallListener mSipOutgoingCallListener;
    private SipCallEndListener mSipCallEndListener;
    private CallStreamListener mCallStreamListener;

    public void setSipRegisterListener(SipRegisterListener mListener) {
        this.mSipRegisterListener = mListener;
    }

    public void setSipCallConnectedListener(SipCallConnectedListener mListener) {
        this.mSipCallConnectedListener = mListener;
    }

    public void setSipIncomingListener(SipIncomingCallListener mListener) {
        this.mSipIncomingCallListener = mListener;
    }

    public void setSipOutgoingListener(SipOutgoingCallListener mListener) {
        this.mSipOutgoingCallListener = mListener;
    }

    public void setSipCallEndListener(SipCallEndListener mListener) {
        this.mSipCallEndListener = mListener;
    }

    public void setCallMediaStatsReport(int callId, CallMediaStatsReport mListener) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).setCallMediaStatsReport(callId, mListener);
    }

    public void setCallStreamListener(CallStreamListener mListener) {
        this.mCallStreamListener = mListener;
    }

    public boolean holdCall(boolean yesno) {
        return false;
    }

    public boolean makeUrlCall(String url, boolean video_mode) {
        return false;
    }

    public boolean sendDtmf(String tone) {
        return false;
    }

    //@Override onCreate
    public void initSDK(Context context) {
        //super.onCreate();
        mContext = context;
        the_service_instance_ = this;
        sendServiceInstanced();

        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetWorkBroadCast = new NetWorkBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetWorkBroadCast, filter);

        if (the_sipengine_ == null) {
            the_sipengine_ = SipEngineFactory.instance().CreateSipEngine(mContext);
            the_sipengine_.SipEngineInitialize();

            mRegistrationManager = the_sipengine_.GetRegistrationManager();
            mRegistrationManager.registerRegistrationEventListener(this);
            mCallManager = the_sipengine_.GetCallManager();
            mCallManager.registerIncomingCallObserver(this);
            mCallManager.registerCallStateObserver(this);
            //transportInfo = the_sipengine_.GetTransportInfo();
        }
        mainHandler = new Handler(mContext.getMainLooper());
        this.startAudioManager();
        handler = new Handler();

        /*AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        _maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        //Debug.e(TAG, "=================get max volume========:" +_maxVolume +":"
          //      +am.isVolumeFixed());
        //am.setStreamVolume(AudioManager.STREAM_MUSIC, _maxVolume, 0);

        if (_maxVolume <= 0) {
            Debug.e(TAG, "=================Could not get max volume========!");
        } else {
            int androidVolumeLevel = (_volumeLevel * _maxVolume) / 255;
            _maxVolume = (androidVolumeLevel * 255) / _maxVolume;
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, _maxVolume, 0);
        }*/
    }

    private void sendServiceInstanced() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PhoneServiceBoot");
        //sendBroadcast(intent);
    }

    private void initDefaultAccount() {
        if(current_account == null) {
          current_account = mRegistrationManager.createAccount();
          this.current_AccountConfig = new AccountConfig();
          current_AccountConfig.username = "1001";
          current_AccountConfig.password = "2345";
          current_AccountConfig.server = "192.168.1.1";
          current_AccountConfig.port = 5060;
          current_account.makeRegister(current_AccountConfig);
        }
    }

    private NetWorkBroadCast mNetWorkBroadCast;

    /*public static void startService(Context ctx) {
        //Debug.i("PhoneService", "Need start service ="
        //	+ (the_service_instance_ == null));
        Intent intent = new Intent(ctx, PhoneService.class);
        ctx.startService(intent);
    }*/

    /**
     * 登陆
     */
    public void registerSipAccount(AccountConfig accConfig /*(String username, String password, String server, int server_port, String transport_type*/) {
       // if (transport_type.equalsIgnoreCase("udp")) {
        //    this.transport_data = "";
       // } else if (transport_type.equalsIgnoreCase("tcp")) {
        //    this.transport_data = ";transport=tcp";
       // } else if (transport_type.equalsIgnoreCase("tls")) {
        //    this.transport_data = ";transport=tls";
        //}
        this.current_AccountConfig = accConfig;
        if (this.mRegistrationManager != null) {
            current_account = mRegistrationManager.createAccount();
            //this.username = username;
            //this.password = password;
            //this.server = server;
            //this.port = server_port;
            //this.trans_type = transport_type;

            //AccountConfig accConfig = new AccountConfig();
           // accConfig.username = username;
           // accConfig.password = password;
           // accConfig.server = server;
            //accConfig.port = server_port;
            current_account.makeRegister(accConfig);
        }
    }

    /**
     * 注销账号
     *
     * @param accId 账号ID
     */
    public void unRegisterSipAccount(int accId) {
        if (this.current_account != null) {
            current_account.makeDeRegister();
            current_account = null;
        }
    }

    /**
     * 修改账号注册
     *
     */
    //public void reRegisterSipAccount(int accId, AccountConfig accConfig) {
      //  if (this.current_account != null) {
         //   current_account.makeDeRegister();
           // current_account.makeRegister(accConfig);
        //}
    //}

    //@Override
    /*public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }*/


    //@Override onDestroy
    public void uninitSDK() {
        //super.onDestroy();
        if(the_service_instance_ != null)
            the_service_instance_ = null;

        if (mNetWorkBroadCast != null)
            mContext.unregisterReceiver(mNetWorkBroadCast);

        if (audioManager != null) {
            audioManager = null;
        }

        if(mCallManager != null)
        {
            mCallManager = null;
        }

        if(the_sipengine_ != null)
        {
            the_sipengine_.dispose();
            the_sipengine_ = null;
        }

        this.current_AccountConfig = null;
        this.current_account = null;
        handler.removeCallbacks(regiestRefresher);
    }

    /**
     * 创建一个呼叫
     *
     * @param calleeUri 被叫账号
     * @param callParams 拨号配置
     */
    public int call(final String calleeUri, CallParams callParams) {
        if (this.mCallManager != null && this.current_account != null
                && this.current_account.getAccountId() > 0) {
            Call current_call = mCallManager.createCall(this.current_account.getAccountId());
            current_call.setRemoteCallerUri(calleeUri);
            callParams.setLocalDisplayName(this.current_account.getAccountConfig().displayname);
            callParams.setLocalUri(this.current_account.getAccountConfig().username);
            callParams.setRemoteDisplayName(calleeUri);
            callParams.setRemoteUri(calleeUri);
            callParams.setDirection(Direction.Outgoing);
            current_call.makeCall(calleeUri, callParams);
            this.onNewCall(current_call.getCallId(), Direction.Outgoing,
                    calleeUri, calleeUri, true, true);
            return current_call.getCallId();
        } else {
            return -1;
        }
    }

    /**
     * 直接呼叫对方
     *
     * @param peerIp 对方的IP地址
     * @param peerPort 对面的端口号
     * @param callParams 拨号配置
     */
    /*public void directCall(final String peerIp, final int peerPort, final CallParams callParams) {
        if (this.mCallManager != null && (current_account != null)) {
            current_call = mCallManager.createCall(this.current_account.getAccountId());
            //current_call.RegisterCallStateObserver(this);
            current_call.directCall(peerIp, peerPort, callParams);
            this.onNewCall(current_call.getCallId(), Direction.Outgoing, peerIp);
        } else {
            //onCallFailed(-1, -1);
        }
    }*/

    /**
     * 拒绝电话
     *
     * @param callId CallID
     * @return true or false
     */
    public void rejectCall(int callId, int reasonCode) {
        stopOutgoingSound();
        stopIncomingSound();
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            this.mCallManager.getCallByCallId(callId).reject(reasonCode, "Decline");
        }
    }

    /**
     * 挂断电话
     *
     * @param callId CallID
     * @return true or false
     */
    public void hangupCall(int callId) {
        stopOutgoingSound();
        stopIncomingSound();
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            this.mCallManager.getCallByCallId(callId).hangup();
            //current_call = null;
        }
    }

    public void hangupAllCall() {
        stopOutgoingSound();
        stopIncomingSound();
        if(this.mCallManager != null) {
            this.mCallManager.hangupAllCall();
        }
    }

    /**
     * 接听电话
     *
     * @param callId CallID
     */
    public void answerCall(final int callId, boolean isVideo) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            this.mCallManager.getCallByCallId(callId).accept(isVideo);
        }
    }

    /**
     * 升级通话
     *
     * @param callId CallID
     */
    public void updateCallByInfo(final int callId, boolean isVideoCall) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            this.mCallManager.getCallByCallId(callId).updateByInfo(isVideoCall);
        }
    }

    /**
     * 升级通话
     *
     * @param callId CallID
     */
    public void updateCall(final int callId, boolean video) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            this.mCallManager.getCallByCallId(callId).updateCall(video);
        }
    }


    private void onNewCall(int callId, Direction IncomingOrOutgoing,
                           String peerCallerUri, String peerDisplayName, boolean existsAudio,
                           boolean existsVideo) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if (IncomingOrOutgoing == Direction.Incoming) {
                    if (mSipIncomingCallListener != null) {
                        startIncomingSound(true, false);
                        dir = Direction.Incoming;
                        mSipIncomingCallListener.onCallIncoming(callId, peerCallerUri,
                                peerDisplayName, existsAudio, existsVideo);
                    }
                } else if (IncomingOrOutgoing == Direction.Outgoing) {
                    if (mSipOutgoingCallListener != null) {
                        startOutgoingSound();
                        dir = Direction.Outgoing;
                        mSipOutgoingCallListener.onCallOutgoing(callId, peerCallerUri, peerDisplayName);
                    }
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    public Direction getCallDirection(int callId) {
        return dir;
    }

    private MediaPlayer waitMedia = null;
    private int callingtone = -1, ringtone = -1;

    public void setPhoneSound(int callingtone, int ringtone) {
        this.callingtone = callingtone;
        this.ringtone = ringtone;
    }

    /**
     * 播放去电铃声
     *
     */
    private void startOutgoingSound() {
        if (waitMedia == null && callingtone > 0) { // 播放去电等待声音
            waitMedia = UIUtils.phoneCallingtone(this.mContext, callingtone);
        }
    }

    /**
     * 停止播放去电铃声
     */
    private void stopOutgoingSound() {
        if (waitMedia != null) {
            waitMedia.stop();
            waitMedia.release();
            waitMedia = null;
        }
    }

    /**
     * 播放来电铃声、震动
     *
     * @param isRinging 是否播放铃声
     * @param isShock   是否震动
     */
    private void startIncomingSound(boolean isRinging, boolean isShock) {
        if (mediaPlayer == null && ringtone > 0) {
            if (isRinging) {
                mediaPlayer = UIUtils.phoneSound(this.mContext.getApplicationContext(), ringtone);
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    mediaPlayer.start();
                }
            }
            if (isShock) {
                vib = UIUtils.Vibrate(this.mContext, true);
            }
        }
    }

    /**
     * 停止来电振铃
     */
    private void stopIncomingSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            mediaPlayer = null;
        }

        if (vib != null) {
            vib.cancel();
            vib = null;
        }
    }

    public void onCallConnected(int callId) {
        stopOutgoingSound();
        stopIncomingSound();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if (mSipCallConnectedListener != null) {
                    mSipCallConnectedListener.onCallConnected(callId);
                }
            }
        };
        mainHandler.post(myRunnable);
    }


    public void onCallEnded(int callId, CallParams mCallParams) {
        stopOutgoingSound();
        stopIncomingSound();
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                CallLogBean mCallLogBean = new CallLogBean();
                if(mCallParams != null) {
                    mCallLogBean.setLocalUsername(mCallParams.localUri());
                    mCallLogBean.setLocalDisplayName(mCallParams.localDisplayName());
                    mCallLogBean.setRemoteUsername(mCallParams.remoteUri());
                    mCallLogBean.setRemoteDisplayName(mCallParams.remoteDisplayName());
                    int type = 0;
                    if(mCallParams.getDirection() == Direction.Incoming)
                        type = 1;
                    mCallLogBean.setType(type);
                    mCallLogBean.setStatus(mCallParams.getState());
                    mCallLogBean.setReasonCode(mCallParams.getReasion());
                    mCallLogBean.setRejectReasonCode(mCallParams.getRejectReasonCode());
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mCallLogBean.setStartTime(format.format(mCallParams.getStartTime()));
                    mCallLogBean.setEndTime(format.format(mCallParams.getEndTime()));
                    mCallLogBean.setVideoCall(mCallParams.videoEnabled());
                }
                if (mSipCallEndListener != null) {
                    mSipCallEndListener.onCallEnd(callId, CallState.Hangup.IntgerValue(), mCallLogBean);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    public void onCallFailed(int callId, int status) {
        stopOutgoingSound();
        stopIncomingSound();
        if (mSipCallEndListener != null)
            mSipCallEndListener.onCallEnd(callId, CallState.Hangup.IntgerValue(), null);
    }

    /**
     * 账号是否已经注册
     *
     * @param accId 账号ID
     * @return true or false
     */
    public boolean isRegistered(int accId) {
        return this.current_account != null && (this.current_account.getRegistrationState() == RegistrationState.Sucess);
    }

    public boolean isAccountInstance() {
        return this.current_account != null;
    }

    public CallParams getCallParams(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
           return this.mCallManager.getCallByCallId(callId).getCallParams();
        } else return null;
    }


    /**
     * 网络变化广播接收器
     */
     class NetWorkBroadCast extends BroadcastReceiver {
       // @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean connected = false;
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isConnected();

            if (networkInfo == null && Version.sdkAboveOrEqual(Version.API21_LOLLIPOP_50)) {
                for (Network network : mConnectivityManager.getAllNetworks()) {
                    if (network != null) {
                        networkInfo = mConnectivityManager.getNetworkInfo(network);
                        if (networkInfo != null && networkInfo.isConnected()) {
                            connected = true;
                            break;
                        }
                    }
                }
            }

            if (networkInfo == null || !connected) {
                Debug.e(TAG, "网络状态[DOWN]!");
                if (the_sipengine_ != null && mRegistrationManager != null) {
                       //mRegistrationManager.setNetworkReachable(false);
                    if(mCallManager != null && mCallManager.isActivityCall()) {
                        Debug.e(TAG, "网络状态DOWNhangUpCall!");
                        hangupAllCall();
                    }
                    if(current_account != null && current_account.getAccountId() > 0)
                        unRegisterSipAccount(current_account.getAccountId());
                    networkStateReachable = false;
                }
            } else if (dozeModeEnabled) {

            } else if (connected){
                boolean wifiOnly = false;
                if (wifiOnly){
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                    }
                    else {

                    }
                } else {
                    int curtype = networkInfo.getType();
                    if (curtype != mLastNetworkType) {
                        //if kind of network has changed, we need to notify network_reachable(false) to make sure all current connections are destroyed.
                        //they will be re-created during setNetworkReachable(true).
                        //Log.i("Connectivity has changed.");
                        //mLc.setNetworkReachable(false);
                        Debug.e(TAG, "网络状态[CHANGE]!");
                        if (the_sipengine_ != null && mRegistrationManager != null) {
                            //mRegistrationManager.setNetworkReachable(false);

                            if(mCallManager != null && mCallManager.isActivityCall()) {
                                Debug.e(TAG, "网络状态CHANGEhangUpCall!");
                                hangupAllCall();
                            }
                        }
                    }
                    if (the_sipengine_ != null && mRegistrationManager != null)
                    {
                        //mRegistrationManager.setNetworkReachable(true);
                        System.out.println("NetWorkBroadCast网络状态[UP]!");
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                                && !TextUtils.isEmpty(server)) {
                            if(current_AccountConfig != null)
                                registerSipAccount(current_AccountConfig);
                        }
                    }
                    mLastNetworkType = curtype;
                    networkStateReachable = true;
                }
            }

            // 获得网络连接服务
            /*ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo mNetworkInfo = connManager.getActiveNetworkInfo();
            try {
                if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                    Debug.d(TAG, "网络状态[UP]!");
                    networkStateReachable = true;
                    if (the_sipengine_ != null && mRegistrationManager != null)
                    {
                        mRegistrationManager.SetNetworkReachable(true);
                        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
                                && !TextUtils.isEmpty(server)) {
                            System.out.println("==============NetWorkBroadCast=====网络状态[UP]!===");
                            registerSipAccount(username, password, server, port, "tcp");
                        }
                    }
                } else {
                    Debug.e(TAG, "网络状态[DOWN]!");
                    networkStateReachable = false;
                    if (the_sipengine_ != null && mRegistrationManager != null) {
                        mRegistrationManager.SetNetworkReachable(false);
                        if(current_call != null && current_call.GetCallId() > 0)
                            hangUpCall(0);
                        if(current_account != null && current_account.getAccountId() > 0)
                            unRegisterSipAccount(current_account.getAccountId());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Debug.e(TAG, "网络状态[DOWN]!");
                networkStateReachable = false;
                if (the_sipengine_ != null && mRegistrationManager != null) {
                    mRegistrationManager.SetNetworkReachable(false);
                    if(current_account != null && current_account.getAccountId() > 0)
                        unRegisterSipAccount(current_account.getAccountId());
                }
            }*/
        }
    }

    private final int STOPHOLDSOUND = 89;
    private AudioManager audioManager = null;

    public  boolean isNetworkReachable()
    {
        return networkStateReachable;
    }

    public void playHoldSound() {
        /*try {
            if (audioManager == null) {
				audioManager = (AudioManager) this
						.getSystemService(Context.AUDIO_SERVICE);
			}
			audioManager.setMode(AudioManager.MODE_IN_CALL);
			//UIUtils.playHoldSound(this);
			Message msg = new Message();
			msg.what = STOPHOLDSOUND;
			//handler.sendMessageDelayed(msg, 2000);
		} catch (Exception e) {
			e.printStackTrace();
			audioManager.setMode(AudioManager.MODE_NORMAL);
		}*/
    }

    /**
     * 判断是否有系统来电
     */
    public static boolean isSystemCalling = false;

    /**
     * 判断是否在通话
     *
     * @param callId CallID
     * @return true or false
     */
    public boolean isCallActive(int callId) {
        // TODO Auto-generated method stub
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
            return this.mCallManager.getCallByCallId(callId).isActive();
        } else return false;
    }

    /**
     * 判断是否在视频通话
     *
     * @param callId CallID
     * @return true or false
     */
    public boolean isInVideoCalling(int callId) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isTvBoxMode() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * 刷新注册信息
     *
     * @param accId 账号ID
     */
    public void refreshRegistration(int accId) {
        // TODO Auto-generated method stub
         if(current_account != null)
             current_account.refreshRegistration();
    }

    /**
     * 获取最后的注册状态
     *
     * @param accId 账号ID
     * @return RegistrationState
     */
    public RegistrationState getLastRegistrationState(int accId) {
        // TODO Auto-generated method stub
        if(this.current_account != null)
           return current_account.getRegistrationState();
        else return RegistrationState.Cleared;
    }

    /**
     * 获取注册账号信息
     *
     * @param accId 账号ID
     * @return RegistrationState
     */
    public AccountConfig getRegistrationInfo(int accId) {
        if(this.current_account != null)
            return current_account.getAccountConfig();
        else return null;
    }

    // Account连接状态 code
    public String getAccountRegistrationState(int accId) {
        RegistrationState state;
        if(this.current_account != null) {
            state = current_account.getRegistrationState();
            if(state == RegistrationState.Sucess)
                return "Sucess";
            if(state == RegistrationState.None)
                return "None";
            if(state == RegistrationState.Cleared)
                return "Cleared";
            if(state == RegistrationState.Failed)
                return "Failed";
            if(state == RegistrationState.Progress)
                return "Progress";
            return "None";
        } else {
            return "None";
        }
    }

    public int getAccountRegistrationCode(int accId) {
        if(this.current_account != null) {
            return current_account.getRegistrationCode();
        } else return -1;
    }

    // Account连接状态 Reason
    public String getAccountRegistrationReason(int accId) {
        String  reason;
        if(this.current_account != null) {
            reason = current_account.getRegistrationReason();
            return "reason";
        } else {
            return null;
        }
    }

    /**
     * 获取最后注册错误码
     *
     * @param accId 账号ID
     * @return RegistrationErrorCode
     */
    public RegistrationErrorCode getLastRegistrationErrorCode(int accId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 发送文本消息
     *
     * @param targetNum 目标Number
     * @param strMsg    要发送的消息
     * @return
     */
    public String sendTextMessage(String targetNum, String strMsg) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 开启状态监听
     *
     * @param callId  CallID
     * @param enable   是否开启
     * @param periodMs 周期 毫秒
     */
    public void enableStatsEvents(int callId, boolean enable, int periodMs) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).enableStatsEvents(enable, periodMs);
    }

    /**
     * 开始视频渲染
     *
     * @param callId      CallID
     * @param localFrameLayout  本地视频
     */
    public void setLocalVideoRender(final int callId,
                                    final FrameLayout localFrameLayout) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).setLocalVideoRender(localFrameLayout);
    }

    /**
     * 开始视频渲染
     *
     * @param callId      CallID
     * @param remoteFrameLayout 远程视频
     */
    public void setRemoteVideoRender(final int callId,
                                     final FrameLayout remoteFrameLayout) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).setRemoteVideoRender(remoteFrameLayout);
    }

    /**
     * 呼叫状态改变的回调
     *
     * @param callId    CallID
     * @param state_code 状态码
     */
    @Override
    public void onCallStateChange(int callId, int state_code) {
        // TODO Auto-generated method stub
        callState = CallState.fromInt(state_code);
        System.out.println("state_code:" + state_code);
        if (callState.IntgerValue() == CallState.NewCall.IntgerValue()) {
            //if (current_call != null) {
                //current_call.reject(, "Busy Here");
                //return;
            //}
        }

        // 挂断
        if (callState.IntgerValue() == CallState.Hangup.IntgerValue()) {
            System.out.println("state_code:" + state_code);
            if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null) {
                //if(current_call.getCallId() != callId)
                    //return;
                //add by david 2012.08.16
                CallParams mCallParams = this.mCallManager.getCallByCallId(callId).getCallParams();
                this.mCallManager.getCallByCallId(callId).closeWebRTC();
                this.mCallManager.getCallByCallId(callId).closeSip();
                this.onCallEnded(callId, mCallParams);
                //current_call = null;
            }
        }

        // 接听
        if (callState.IntgerValue() == CallState.Answered.IntgerValue()) {
            //if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
              onCallConnected(callId);
        }
    }

    @Override
    public void onLocalVideoReady(int callId) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if(mCallStreamListener != null)
                {
                    mCallStreamListener.onLocalVideoReady(callId);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onRemoteVideoReady(int callId) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if(mCallStreamListener != null)
                {
                    mCallStreamListener.onRemoteVideoReady(callId);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onUpdatedByRemote(int callId, boolean video) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if(mCallStreamListener != null)
                {
                    mCallStreamListener.onUpdatedByRemote(callId, video);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    @Override
    public void onUpdatedByLocal(int callId, boolean video) {
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                if(mCallStreamListener != null)
                {
                    mCallStreamListener.onUpdatedByLocal(callId, video);
                }
            }
        };
        mainHandler.post(myRunnable);
    }

    public String getWebRtcErrMessage(int callId) {
        String errorMessage = null;
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            errorMessage = this.mCallManager.getCallByCallId(callId).getWebtcErrMessage();

        return errorMessage;
    }

    /**
     * 正在注册的回调
     *
     * @param accId 账号ID
     */
    @Override
    public void onRegistrationProgress(int accId) {
        // TODO Auto-generated method stub
        if(mSipRegisterListener != null)
           mSipRegisterListener.onRegistrationProgress(accId);
    }

    /**
     * 注册成功的回调
     *
     * @param accId 账号ID
     */
    @Override
    public void onRegistrationSuccess(int accId) {
        // TODO Auto-generated method stub
        setCpuRun(true);//lock cpu on wake state
        handler.removeCallbacks(regiestRefresher);
        if(mSipRegisterListener != null)
          mSipRegisterListener.onRegistrationSuccess(accId);
    }

    /**
     * 注销的回调
     *
     * @param accId 账号ID
     */
    @Override
    public void onRegistrationCleared(int accId) {
        // TODO Auto-generated method stub
        setCpuRun(false);//release cpu lock
        handler.removeCallbacks(regiestRefresher);
        if(mSipRegisterListener != null)
           mSipRegisterListener.onRegistrationCleared(accId);
    }

    Runnable regiestRefresher = new Runnable() {
        @Override
        public void run() {
            Log.e("PhoneService", "==========regiestRefresher========");
            if(PhoneService.this.current_AccountConfig != null)
                registerSipAccount(PhoneService.this.current_AccountConfig);
        }
    };

    /**
     * 注册失败的回调
     *
     * @param accId 账号ID
     */
    @Override
    public void onRegistrationFailed(int accId, int code, String reason) {
        setCpuRun(false);//release cpu lock
        // TODO Auto-generated method stub
        Log.e("PhoneService", "==========onRegistrationFailed======");
        switch (code) {
            case 403://forbidden
            case 404://not found
            case 401://unauthorized
                break;
            case 8888:
                break;

            default://
                handler.removeCallbacks(regiestRefresher);
                handler.postDelayed(regiestRefresher, REGIST_TIMER_PERIOD * retryTime);
                break;
        }

        retryTime = retryTime > REGIST_MAX_RETRY ? REGIST_MAX_RETRY : ++retryTime;

        if(mSipRegisterListener != null)
           mSipRegisterListener.onRegistrationFailed(accId, code, reason);
    }

    /**
     * 来电的回调
     *
     * @param call Call
     */
    @Override
    public void onIncomingCall(Call call) {
        // TODO Auto-generated method stub
        /*if (current_call != null) {
            call.reject(486, "busy");
            return;
        }
        current_call = call;*/
        AccountConfig mAccountConfig = current_account.getAccountConfig();
        call.getCallParams().setLocalDisplayName(mAccountConfig.displayname);
        call.getCallParams().setLocalUri(mAccountConfig.username);
        this.onNewCall(call.getCallId(), Direction.Incoming,
                call.getCallParams().remoteUri(),
                call.getCallParams().remoteDisplayName(),
                call.getCallParams().audioEnabled(),
                call.getCallParams().videoEnabled());
    }

    public String getLocalSipListenIP() {
        return null;//transportInfo.localhost_ip;
    }

    public int getLocalSipListenPort() {
        return 0;
        //transportInfo.localhost_port;
    }

    public void setVideoMaxBitrate(int callId, int maxBitrateKbps)
    {
           if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
               this.mCallManager.getCallByCallId(callId).setVideoMaxBitrate(maxBitrateKbps);
    }

    public void startVideoSending(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).startVideoSending();

    }

    public void startVideoReceiving(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).startVideoReceiving();

    }

    public void stopVideoSending(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).stopVideoSending();

    }

    private void stopVideoReceiving(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).stopVideoReceiving();
    }
    /**
     * 设置是否可以语音
     *
     * @param callId CallID
     * @param enable  true or false
     */
    public void setAudioEnabled(int callId, final boolean enable) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).setAudioEnabled(enable);
    }

    public void startVoiceChannel(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).startVoiceChannel();
    }

    public void stopVoiceChannel(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).stopVoiceChannel();
    }

    /**
     * 切换摄像头
     *
     * @param callId CallID
     */
    public void switchCamera(int callId) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).switchCamera();
    }

    public void ChangeCaptureFormat(int callId, int width, int height, int framerate) {
        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).changeCaptureFormat(width, height, framerate);
    }

    /**
     * 设置是否开启扬声器
     *
     * @param callId CallIDre
     * @param yesno   true or false
     */
    public void setSpeakerphoneOn(int callId, boolean yesno) {
        if(audioManager != null) {
            //if(yesno) {
        	   audioManager.setSpeakerphoneOn(yesno);
               if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
                this.mCallManager.getCallByCallId(callId).getCallParams().setHf(yesno);
           // } else {
              // audioManager.setDefaultAudioDevice(CloudRTCAudioManager.AudioDevice.EARPIECE);
            //}
        }
    }

    /**
     * 设置是否静音
     *
     * @param callId CallID
     * @param yesno   true or false
     */
    public void setMicrophoneMute(int callId, boolean yesno) {
        // TODO Auto-generated method stub
        if(audioManager != null)
           audioManager.setMicrophoneMute(yesno);

        if(this.mCallManager != null && this.mCallManager.getCallByCallId(callId) != null)
            this.mCallManager.getCallByCallId(callId).getCallParams().setMute(yesno);
    }

    /**
     * 开启AudioManager
     */
    private void startAudioManager() {
        if (audioManager == null) {
            audioManager = ((AudioManager) this.mContext.getSystemService(
                    Context.AUDIO_SERVICE));
        }

        audioManager.setSpeakerphoneOn(true);
    }

    /*private void onAudioManagerDevicesChanged(
            final reSipWebRTCAudioManager.AudioDevice device, final Set<reSipWebRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
    }*/

    public static void checkAndRequestPermission(Activity context, String permission, int result) {
        int permissionGranted = context.getPackageManager().checkPermission(permission, context.getPackageName());
        if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
        }
    }

    public static void checkAndRequestPermission(Activity context, String[] permissions, int result) {
        if (permissions == null || permissions.length == 0) {
            return;
        }

        List<String> pers = new ArrayList<>();

        for (String permission : permissions) {
            int permissionGranted = context.getPackageManager().checkPermission(permission, context.getPackageName());
            if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
                pers.add(permission);
            }
        }

        if (!pers.isEmpty()) {
            //ActivityCompat.requestPermissions(context, (String[]) pers.toArray(), result);
        }
    }

    public static final int REQUEST_CAMERA = 0;

    public static void checkAndRequestCameraPermission(Activity activity) {
        checkAndRequestPermission(activity, Manifest.permission.CAMERA, REQUEST_CAMERA);
    }

    public static final int REQUEST_RECORDAUDIO = 1;

    public static void checkAndRequestRecordAudioPermission(Activity activity) {
        checkAndRequestPermission(activity, Manifest.permission.RECORD_AUDIO, REQUEST_RECORDAUDIO);
    }

    public static void checkAndRequestCallPhonePermission(Activity activity) {
        checkAndRequestPermission(activity, Manifest.permission.CALL_PHONE, 0);
    }

    public static boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    public static boolean isVoicePermission() {
        AudioRecord record = null;
        try {
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(22050,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState = record.getRecordingState();
            if (recordingState == AudioRecord.RECORDSTATE_STOPPED) {
                return false;
            }
            //第一次  为true时，先释放资源，在进行一次判定
            //************
            record.release();
            record = new AudioRecord(MediaRecorder.AudioSource.MIC, 22050,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(22050,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT));
            record.startRecording();
            int recordingState1 = record.getRecordingState();
            if (recordingState1 == AudioRecord.RECORDSTATE_STOPPED) {
            }
            //**************
            //如果两次都是true， 就返回true  原因未知
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (record != null) {
                record.release();
            }
        }
    }

    public void setCpuRun(boolean bOn){
        PowerManager powerManager = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        if(bOn){ //open
            if(mCpuLock == null){
                if((mCpuLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "resipwebrtc:CpuLock")) == null){
                    return;
                }
                mCpuLock.setReferenceCounted(false);
            }

            synchronized(mCpuLock){
                if(!mCpuLock.isHeld()){
                    mCpuLock.acquire();
                }
            }
        }else{//
            if(mCpuLock != null){
                synchronized(mCpuLock){
                    if(mCpuLock.isHeld()){
                        mCpuLock.release();
                    }
                }
            }
        }
    }
}