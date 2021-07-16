package com.reSipWebRTC.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

import android.content.Context;
import android.util.Log;

import com.reSipWebRTC.sipengine.RTCCallManager;
import com.reSipWebRTC.sipengine.RTCCallStateObserver;
import com.reSipWebRTC.util.Direction;

public class CallManagerImpl implements CallManager, RTCCallStateObserver {
	private RTCCallManager rtcCallManager;
    private Map<Integer, Call> mCallMap = new HashMap<Integer, Call>();
    private CallState mCallState = CallState.Unknown;
    private CallStateEventListener mCallStateEventListener;
	private IncomingCallObserver mIncomingCallObserver;
	private static final String TAG = "reSipWebRTC";
    private VideoSource videoSource;
   private AudioSource audioSource;
   private VideoCapturer videoCapturer;
   private boolean videoCapturerStopped;
   private Context mContext;
   private SurfaceTextureHelper surfaceTextureHelper;
   private EglBase rootEglBase;
   private boolean videoCallEnabled;
   private boolean isError;
   private CallManagerParams callManagerParams;
   PeerConnectionFactory.Options options = null;
   private final ScheduledExecutorService executor;

   private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
   private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
   private static final String AUDIO_AUTO_GAIN_CONTROL_LEVEL_CONSTRAINT = "googAutoGainControlLevel";
   private static final String AUDIO_AUTO_GAIN_CONTROL_GAIN_CONSTRAINT = "googAutoGainControlGain";
   private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
   private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
   private static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";

    private PeerConnectionFactory factory;
	
	public CallManagerImpl(RTCCallManager callManager, final Context context)
	{
		executor = Executors.newSingleThreadScheduledExecutor();
        rtcCallManager = callManager;
		mContext = context;
        rtcCallManager.registerCallStateObserver(this);
        videoSource = null;
        audioSource = null;
        videoCapturer = null;
        videoCapturerStopped = true;
        options = new PeerConnectionFactory.Options();
        options.disableEncryption = false;
        options.networkIgnoreMask = 16;
        //options.disableNetworkMonitor = true;
        rootEglBase = EglBase.create();
    }

	private void createPeerConnectionFactory()
	{
		if(callManagerParams == null)
			   return;

        videoCallEnabled = callManagerParams.videoCallEnabled;

        String fieldTrials = "";
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(mContext)
                .setFieldTrials(fieldTrials)
                .setEnableInternalTracer(true)
                .createInitializationOptions());
        createPeerConnectionFactoryInternal(mContext);

        //rootEglBase = EglBase.create();
    }

    @Override
    public boolean isActivityCall()
    {
        boolean isActivityCall = false;
        if(mCallMap != null)
            isActivityCall =  !mCallMap.isEmpty();

       return isActivityCall;
    }

    @Override
    public PeerConnection createPeerConnection(CallManagerParams callManagerParams,
                                               PeerConnection.RTCConfiguration rtcConfig,
                                               CallImpl.PCObserver pcObserver) {
	    this.callManagerParams = callManagerParams;
	    if(this.factory == null)
	        createPeerConnectionFactory();

	    if(this.factory != null) {
	        return factory.createPeerConnection(rtcConfig, pcObserver);
	    } else return null;
    }

    @Override
    public AudioTrack createAudioTrack(String audioTrackId, AudioSource localAudioSource) {
	    if(factory != null) {
	        return  factory.createAudioTrack(audioTrackId, localAudioSource);
	    } else return null;
    }

    @Override
    public VideoTrack createVideoTrack(String videoTrackId, VideoSource localVideoSource) {
	    if(factory != null) {
	        return  factory.createVideoTrack(videoTrackId, localVideoSource);
	    } else return null;
    }

    @Override
    public void onLocalVideoReady(int callId) {
	    if(this.mCallStateEventListener != null)
        {
            this.mCallStateEventListener.onLocalVideoReady(callId);
        }
    }

    @Override
    public void onRemoteVideoReady(int callId) {
        if(this.mCallStateEventListener != null)
        {
            this.mCallStateEventListener.onRemoteVideoReady(callId);
        }
    }

    @Override
	public void setVideoHwAccelerationOptions(EglBase.Context localEglContext, EglBase.Context remoteEglContext)
	{
        if(factory == null) {
            createPeerConnectionFactory();
        }
        //if(this.factory != null)
			//this.factory.setVideoHwAccelerationOptions(localEglContext, remoteEglContext);
	}

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public EglBase.Context getRenderContext() {
	    if(rootEglBase != null)
          return rootEglBase.getEglBaseContext();
	    else return null;
    }

    @Override
	public Call createCall(int accId) {
		int callId = rtcCallManager.createCall(accId);
        Call call = new CallImpl(accId, callId, this, executor);
        return call;
	}

	@Override
	public void registerCallStateObserver(CallStateEventListener observer) {
		this.mCallStateEventListener = observer;
	}

	@Override
	public void deRegisterCallStateObserver() {
		//DeRegisterCallStateObserver(nativePtr);
	}
	
	@Override
	public void makeCall(int accId, int callId, String calleeUri, String localSdp)
	{
        rtcCallManager.makeCall(accId, callId, calleeUri, localSdp);
	}

	@Override
	public void directCall(int accId, int callId,
								   String peerIp, int peerPort, String localSdp)
	{
           //DirectCall(nativePtr, accId, callId, peerIp, peerPort, localSdp);
	}

	@Override
	public void accept(int callId, String localSdp, boolean send_audio, boolean send_video) {
        rtcCallManager.accept(callId, localSdp, send_audio, send_video);
	}

    @Override
    public void update(int callId, String localSdp) {
        rtcCallManager.update(callId, localSdp);
    }

    @Override
    public void updateMediaState(int callId, boolean isOffer, String localSdp, boolean audio, boolean video) {
        rtcCallManager.updateMediaState(callId, isOffer, localSdp, audio, video);
        if(mCallStateEventListener != null)
            mCallStateEventListener.onUpdatedByLocal(callId, video);
    }

    @Override
	public void reject(int callId, int code, String reason) {
		//Reject(nativePtr, callId, code, reason);
        rtcCallManager.reject(callId, code, reason);
	}
	
	@Override
	public void hangup(int callId) {
        rtcCallManager.hangup(callId);
    }

    @Override
    public void OnCallStateChange(int callId, int stateCode, int reasonCode) {
        // TODO Auto-generated method stub
        synchronized(mCallMap) {
            if(!mCallMap.isEmpty()) {
                Call mCall = mCallMap.get(callId);
                if(mCall != null)
                    mCall.updateCallState(stateCode, reasonCode);
            }
           if(this.mCallStateEventListener != null)
              mCallStateEventListener.onCallStateChange(callId, stateCode);
        }
    }

    @Override
    public void OnIncomingCall(final int accId, final int callId, final String callerDisplayName, final String callerUri) {
        // TODO Auto-generated method stub
        synchronized(mCallMap) {
          Call call  = new CallImpl(accId, callId, this, executor);
          call.setRemoteCallerUri(callerUri);
          call.setRemoteDisplayName(callerDisplayName);
          call.getCallParams().setDirection(Direction.Incoming);
          call.getCallParams().setRemoteDisplayName(callerDisplayName);
          call.getCallParams().setRemoteUri(callerUri);
          call.getCallParams().setState(0);
          call.getCallParams().setReason(200);
          call.updateCallState(0, 200);
        }
    }

	@Override
	public void OnCallOffer(int callId, String offerSdp,
                            final boolean audioCall, final boolean videoCall) {
		// TODO Auto-generated method stub
        synchronized(mCallMap) {
           if(!mCallMap.isEmpty()) {
              mCallMap.get(callId).getCallParams().enableAudio(audioCall);
              mCallMap.get(callId).getCallParams().enableVideo(videoCall);
              mCallMap.get(callId).OnCallOffer(offerSdp);
              if(this.mIncomingCallObserver != null) {
                mIncomingCallObserver.onIncomingCall(mCallMap.get(callId));
              }
           }
        }
	}

    @Override
    public void OnCallReceiveReinvite(int callId, String sdp) {
        synchronized(mCallMap) {
            if(!mCallMap.isEmpty()) {
                Call mCall = mCallMap.get(callId);
                if(mCall != null)
                    mCall.onCallReceiveReinvite(sdp);
            }
        }
    }

    @Override
	public void OnCallAnswer(int callId, String answerSdp, boolean audioCall, boolean videoCall) {
		// TODO Auto-generated method stub
        synchronized(mCallMap) {
         if(!mCallMap.isEmpty()) {
            Call mCall = mCallMap.get(callId);
            if(mCall != null) {
                mCall.getCallParams().enableAudio(audioCall);
                mCall.getCallParams().enableVideo(videoCall);
                mCall.OnCallAnswer(answerSdp);
            }
         }
        }
	}

    @Override
    public void OnInfoEvent(int callId, String info) {

    }

    @Override
    public void OnMediaStateChange(int callId, String remoteSdp, boolean audio, boolean video) {
        synchronized(mCallMap) {
            if(!mCallMap.isEmpty()) {
                Call mCall = mCallMap.get(callId);
                if(mCall != null)
                    mCall.OnMediaStateChange(remoteSdp, audio, video);
            }
        }

        if(this.mCallStateEventListener != null) {
            mCallStateEventListener.onUpdatedByRemote(callId, video);
        }
    }

    @Override
    public void registerCall(Call call)
    {
        synchronized(mCallMap) {
            mCallMap.put(call.getCallId(), call);
        }
    }
	
	@Override
    public void unregisterCall(Call call)
    {
        synchronized(mCallMap) {
            mCallMap.remove(call.getCallId());
        }
    }

    @Override
    public Call getCallByCallId(int callId) {
        synchronized(mCallMap) {
          if(!mCallMap.isEmpty()) {
            Call mCall = mCallMap.get(callId);
            if(mCall != null)
                return mCall;
            else return null;
          } else return null;
        }
    }

    @Override
    public void onPeerConnectionError(int callId, String description) {
        if(this.mCallStateEventListener != null)
            mCallStateEventListener.onCallStateChange(callId, 7);
    }

    @Override
    public void hangupAllCall() {

    }

    private void createPeerConnectionFactoryInternal(Context context) {
		// Enable/disable OpenSL ES playback.
		Log.d(TAG, "Peer connection factory created");
		// Create peer connection factory.
        final AudioDeviceModule adm = createJavaAudioDevice();
        final VideoEncoderFactory encoderFactory;
        final VideoDecoderFactory decoderFactory;
        encoderFactory = new SoftwareVideoEncoderFactory();
        decoderFactory = new SoftwareVideoDecoderFactory();

        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(adm)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        adm.release();
	}

    AudioDeviceModule createJavaAudioDevice() {
        // Enable/disable OpenSL ES playback.
        if (!callManagerParams.useOpenSLES) {
            Log.w(TAG, "External OpenSLES ADM not implemented yet.");
             //TODO(magjed): Add support for external OpenSLES ADM.
        }

        // Set audio record error callbacks.
        JavaAudioDeviceModule.AudioRecordErrorCallback audioRecordErrorCallback = new JavaAudioDeviceModule.AudioRecordErrorCallback() {
            @Override
            public void onWebRtcAudioRecordInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordInitError: " + errorMessage);
                //reportError(errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordStartError(
                    JavaAudioDeviceModule.AudioRecordStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordStartError: " + errorCode + ". " + errorMessage);
                //reportError(errorMessage);
            }

            @Override
            public void onWebRtcAudioRecordError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioRecordError: " + errorMessage);
               // reportError(errorMessage);
            }
        };

        JavaAudioDeviceModule.AudioTrackErrorCallback audioTrackErrorCallback = new JavaAudioDeviceModule.AudioTrackErrorCallback() {
            @Override
            public void onWebRtcAudioTrackInitError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackInitError: " + errorMessage);
               // reportError(errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackStartError(
                    JavaAudioDeviceModule.AudioTrackStartErrorCode errorCode, String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackStartError: " + errorCode + ". " + errorMessage);
                //reportError(errorMessage);
            }

            @Override
            public void onWebRtcAudioTrackError(String errorMessage) {
                Log.e(TAG, "onWebRtcAudioTrackError: " + errorMessage);
                //reportError(errorMessage);
            }
        };

        /*return JavaAudioDeviceModule.builder(mContext)
                .setUseHardwareAcousticEchoCanceler(!callManagerParams.disableBuiltInAEC)
                .setUseHardwareNoiseSuppressor(!callManagerParams.disableBuiltInNS)
                .setAudioRecordErrorCallback(audioRecordErrorCallback)
                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .createAudioDeviceModule();*/

        return JavaAudioDeviceModule.builder(mContext)
                .setUseHardwareAcousticEchoCanceler(false)
                .setUseHardwareNoiseSuppressor(false)
                .setAudioRecordErrorCallback(audioRecordErrorCallback)
                .setAudioTrackErrorCallback(audioTrackErrorCallback)
                .createAudioDeviceModule();
    }

    private boolean useCamera2() {
        return false;//Camera2Enumerator.isSupported(mContext);
    }

    private boolean captureToTexture() {
        return false;
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        String videoFileAsCamera = null;
        if (videoFileAsCamera != null) {
           // try {
               // videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            //} catch (IOException e) {
             //   reportError("Failed to open video file for emulated camera");
               // return null;
            //}
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError("Camera2 only supports capturing to texture. Either disable Camera2 or enable capturing to texture in the options.");
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(mContext));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

   /* @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            reportError("User didn't give permission to capture the screen.");
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                reportError("User revoked permission to capture the screen.");
            }
        });
    }*/

	@Override
	public VideoSource createlocalVideoSource(int width, int height, int framerate)
	{
        if(factory == null) {
            createPeerConnectionFactory();
        }

		if(videoSource != null)
			return videoSource;

		if(rootEglBase != null) {
          surfaceTextureHelper =
                SurfaceTextureHelper.create("CaptureThread", rootEglBase.getEglBaseContext());
		}
        videoCapturer = createVideoCapturer();
        videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, mContext, videoSource.getCapturerObserver());
        videoCapturer.startCapture(width, height, framerate);
        videoCapturerStopped = false;
        return videoSource;
	}
	
	 private void reportError(final String errorMessage) {
		    Log.e(TAG, "Peerconnection error: " + errorMessage);
		  //  executor.execute(new Runnable() {
		     // @Override
		     // public void run() {
		        if (!isError) {
		          isError = true;
		        }
		      //}
		    //});
	 }
	 
	@Override
	public AudioSource createlocalAudioSource()
	{
        if(factory == null) {
            createPeerConnectionFactory();
        }

		if(audioSource != null)
			return audioSource;

		MediaConstraints audioConstraints = new MediaConstraints();
        if (callManagerParams.audioProcessing) {
            Log.d(TAG, "audio processing");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true"));
            //audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAutoGainControl2", "true"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));

            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
            //audioConstraints.mandatory.add(new MediaConstraints.KeyValuePair("googAudioMirroring", "true"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));

            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_LEVEL_CONSTRAINT,
                            String.valueOf(callManagerParams.agcControlLevel)));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_GAIN_CONSTRAINT,
                            String.valueOf(callManagerParams.agcControlGain)));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true"));
        } else {
            Log.d(TAG, "audio processing");
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "false"));

            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "false"));
            audioConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "false"));
        }
        audioSource = this.factory.createAudioSource(audioConstraints);
        return audioSource;
	}

    @Override
    public void stoplocalAudioSource()
    {
        // TODO Auto-generated method stub
        System.out.println("================stoplocalAudioSource======================");
        synchronized(mCallMap) {
            if(mCallMap.isEmpty() || mCallMap.size() == 1) {
                if(audioSource != null) {
                    audioSource.dispose();
                    audioSource = null;
                }

                if(factory != null) {
                    factory.dispose();
                    factory = null;
                }

                PeerConnectionFactory.stopInternalTracingCapture();
                PeerConnectionFactory.shutdownInternalTracer();
            }
        }
    }

	@Override
	public void stoplocalVideoSource() {
		// TODO Auto-generated method stub
        synchronized(mCallMap) {
            if(mCallMap.isEmpty() || mCallMap.size() == 1) {
                if (videoCapturer != null) {
                    try {
                        videoCapturer.stopCapture();
                    } catch(InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    videoCapturer.dispose();
                    videoCapturer = null;
                }

                if(videoSource != null) {
                    videoSource.dispose();
                    videoSource = null;
                }
                if (surfaceTextureHelper != null) {
                    surfaceTextureHelper.dispose();
                    surfaceTextureHelper = null;
                }
            }
        }
	}
	
	@Override
    public void registerIncomingCallObserver(IncomingCallObserver observer)
    {
		 mIncomingCallObserver = observer;
    }


    /*@Override
    public void setCallConfig(CallConfig callConfig)
    {
        this.callConfig = callConfig;
        this.videoCallEnabled = callConfig.videoCallEnabled;
    }

    @Override
    public CallConfig getCallConfig()
    {
        return callConfig;
    }*/

	/*@Override
	public void startVideoSending(int callId)
	{
        if(this.videoCapturer != null && videoCapturerStopped) {
            this.videoCapturer.startCapture(this.callConfig.videoWidth,
                    this.callConfig.videoHeight, this.callConfig.videoFps);
            videoCapturerStopped = false;
        }
	}*/

    @Override
    public void switchCamera(int callId)
    {
        if (videoCapturer instanceof CameraVideoCapturer) {
        if (!videoCallEnabled || isError || videoCapturer == null) {
            Log.e(TAG, "Failed to switch camera. Video: " + videoCallEnabled + ". Error : " + isError);
            return; // No video is sent or only one camera is available or error happened.
        }
        Log.d(TAG, "Switch camera");
        CameraVideoCapturer cameraVideoCapturer = (CameraVideoCapturer) videoCapturer;
        cameraVideoCapturer.switchCamera(null);
        } else {
        Log.d(TAG, "Will not switch camera, video caputurer is not a camera");
        }
    }

    @Override
    public void changeCaptureFormat(int callId, int width, int height, int framerate)
    {
        if (!videoCallEnabled || isError || videoCapturer == null) {
            Log.e(TAG,
                    "Failed to change capture format. Video: " + videoCallEnabled + ". Error : " + isError);
            return;
        }
        Log.d(TAG, "changeCaptureFormat: " + width + "x" + height + "@" + framerate);
        //videoSource.adaptOutputFormat(width, height, framerate);
    }


    /**
     * Peer connection events.
     */
    public interface PeerConnectionEvents {
        /**
         * Callback fired once local SDP is created and set.
         */
        void onLocalDescription(final SessionDescription sdp);

        /**
         * Callback fired once local Ice candidate is generated.
         */
        void onIceCandidate(final IceCandidate candidate);

        /**
         * Callback fired when Ice Gathering completes
         */
        void onIceGatheringComplete();

        /**
         * Callback fired once local ICE candidates are removed.
         */
        void onIceCandidatesRemoved(final IceCandidate[] candidates);

        /**
         * Callback fired once connection is established (IceConnectionState is
         * CONNECTED).
         */
        void onIceConnected();

        /**
         * Callback fired once connection is closed (IceConnectionState is
         * DISCONNECTED).
         */
        void onIceDisconnected();

        /**
         * Callback fired once peer connection is closed.
         */
        void onPeerConnectionClosed();

        /**
         * Callback fired once peer connection statistics is ready.
         */
        void onPeerConnectionStatsReady(final StatsReport[] reports);

        /**
         * Callback fired once peer connection error happened.
         */
        void onPeerConnectionError(final String description);

        /**
         * Callback fired when local video is ready.
         */
        void onLocalVideo();

        /**
         * Callback fired when remote video is ready.
         */
        void onRemoteVideo();

        /**
         * Callback fired when video is paused after call to pauseVideo()
         */
        void onVideoDetached();

        /**
         * Callback fired when video is resumed after call to resumeVideo()
         */
        void onVideoReattached();
    }
}
