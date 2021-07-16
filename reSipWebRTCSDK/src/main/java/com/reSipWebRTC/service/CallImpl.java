package com.reSipWebRTC.service;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.reSipWebRTC.sdk.CallMediaStatsReport;

import org.webrtc.AudioTrack;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnection.IceConnectionState;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RtpParameters;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.RtpTransceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFrame;
import org.webrtc.VideoSink;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CallImpl implements Call {

	public CallImpl(int accId, int callId,
					CallManager callManager, ScheduledExecutorService executor)  {
		this.accId = accId;
		this.callId = callId;
		mCallManager = callManager;

	    videoCallEnabled = false;
        // Reset variables to initial states.
        peerConnection = null;
        preferIsac = false;
		preferPcma = false;
        videoSourceStopped = false;
        isError = false;
        localSdp = null; // either offer or answer SDP
        mediaStream = null;
        renderVideo = true;
        localVideoTrack = null;
        remoteVideoTrack = null;
        enableAudio = true;
        localVideoSender = null;
        this.executor = executor;
        //statsTimer = new Timer();
		mCallState = -1;
        isNoAccepted = true;
		mCallParams = new CallParamsImpl();
		startTime = new Date().getTime();
		mCallParams.setStartTime(startTime);
		mCallManager.registerCall(this);
		candidateTimeoutHandler = new Handler(mCallManager.getContext().getMainLooper());
	}

	private int accId;
	private int callId;
	private String remoteCallerUri;
	private String remoteDisplayName;
	private CallParams mCallParams;
	private CallManager mCallManager;
    private static final String TAG = "reSipWebRTC";
    private MediaStream mStream = null;
    private boolean isConnected = false;
    private boolean isUpdateCall = false;
    private CallMediaStatsReport mCallMediaStatsReport;
    private boolean isNoAccepted = false;
	private Handler candidateTimeoutHandler = null;
	private boolean iceGatheringCompleteCalled = false;
	public List<IceCandidate> iceCandidates;
    private List<String> mediaStreamLabels = null;
    private boolean isOffer = true;
    private long startTime = 0;
    private long endTime = 0;
	private String errorMessage;

	public static final String VIDEO_TRACK_ID = "ARDAMSv0";
	public static final String AUDIO_TRACK_ID = "ARDAMSa0";
	public static final String VIDEO_TRACK_TYPE = "video";
	private static final String VIDEO_CODEC_VP8 = "VP8";
	private static final String VIDEO_CODEC_VP9 = "VP9";
	private static final String VIDEO_CODEC_H264 = "H264";
	private static final String VIDEO_CODEC_H264_BASELINE = "H264 Baseline";
	private static final String VIDEO_CODEC_H264_HIGH = "H264 High";
	private static final String AUDIO_CODEC_OPUS = "opus";
	private static final String AUDIO_CODEC_ISAC = "ISAC";
	private static final String AUDIO_CODEC_PCMA = "PCMA";
	private static final String VIDEO_CODEC_PARAM_START_BITRATE = "x-google-start-bitrate";
    private static final String VIDEO_CODEC_PARAM_MIN_BITRATE = "x-google-min-bitrate";
    private static final String VIDEO_CODEC_PARAM_MAX_BITRATE = "x-google-max-bitrate";
	private static final String VIDEO_FLEXFEC_FIELDTRIAL =
			"WebRTC-FlexFEC-03-Advertised/Enabled/WebRTC-FlexFEC-03/Enabled/";
	private static final String VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL = "WebRTC-IntelVP8/Enabled/";
	private static final String WEBRTC_IPV6DEFAULT_FIELDTRIAL =
			"WebRTC-IPv6Default/Disabled/";
    private static final String VIDEO_H264_HIGH_PROFILE_FIELDTRIAL =
            "WebRTC-H264HighProfile/Enabled/";
	private static final String DISABLE_WEBRTC_AGC_FIELDTRIAL =
			"WebRTC-Audio-MinimizeResamplingOnMobile/Enabled/";
	private static final String AUDIO_CODEC_PARAM_BITRATE = "maxaveragebitrate";
	private static final String AUDIO_ECHO_CANCELLATION_CONSTRAINT = "googEchoCancellation";
	private static final String AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT = "googAutoGainControl";
	private static final String AUDIO_AUTO_GAIN_CONTROL_LEVEL_CONSTRAINT = "googAutoGainControlLevel";
	private static final String AUDIO_AUTO_GAIN_CONTROL_GAIN_CONSTRAINT = "googAutoGainControlGain";
	private static final String AUDIO_HIGH_PASS_FILTER_CONSTRAINT = "googHighpassFilter";
	private static final String AUDIO_NOISE_SUPPRESSION_CONSTRAINT = "googNoiseSuppression";
	private static final String AUDIO_LEVEL_CONTROL_CONSTRAINT = "levelControl";
	private static final String DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT = "DtlsSrtpKeyAgreement";
	private static final int HD_VIDEO_WIDTH = 1280;
	private static final int HD_VIDEO_HEIGHT = 720;
	private static final int BPS_IN_KBPS = 1000;

	private boolean videoCallEnabled;
	private boolean MediaStreamReady = false;
	private boolean enableAudio;
	private int videoWidth;
	private int videoHeight;
	private int videoFps;
    private boolean dataChannelEnabled;
    private ScheduledExecutorService executor;
    private PeerConnection peerConnection;
    private MediaConstraints pcConstraints;
    private MediaConstraints videoConstraints;
    //private MediaConstraints audioConstraints;
    private ParcelFileDescriptor aecDumpFileDescriptor;
    private MediaConstraints sdpMediaConstraints;
    private VideoSource videoSource;
    private boolean preferIsac;
	private boolean preferPcma;
    private String preferredVideoCodec;
    private boolean videoSourceStopped;
    private boolean isError;
    private final PCObserver pcObserver = new PCObserver();
    private final SDPObserver sdpObserver = new SDPObserver();
    //private VideoSink localRender;
	//private VideoSink remoteRender;
    private VideoTrack localVideoTrack;
    private VideoTrack remoteVideoTrack;
    private AudioTrack localAudioTrack;
	private AudioTrack remoteAudioTrack;
	private RtpSender localVideoSender;
	private RendererCommon.ScalingType scalingType;
	private SurfaceViewRenderer localSurfaceViewRender = null;
	private SurfaceViewRenderer remoteSurfaceViewRender = null;
	private FrameLayout localRenderLayout;
	private FrameLayout remoteRenderLayout;
	private final CallImpl.ProxyVideoSink localProxyRenderer = new CallImpl.ProxyVideoSink();
	private final CallImpl.ProxyVideoSink remoteProxyRenderer = new CallImpl.ProxyVideoSink();

    private boolean isInitiator;
    private SessionDescription localSdp; // either offer or answer SDP
	private String localStringSdp;
	private MediaStream mediaStream;
	private int numberOfCameras;
	private boolean renderVideo;
	private String RemoteSdp = null;

	private String calleeUri;
	private String peerIp;
	private int peerPort;
	boolean isDirectCall = false;
	private int mCallState;
	private int mCallReason;

	/**
	 * The default max video height of video.
	 */
	private static final String DEFAULT_MAX_VIDEO_H = "480";
	/**
	 * The default max video width of video.
	 */
	private static final String DEFAULT_MAX_VIDEO_W = "640";
	//private static final String DEFAULT_STUNSERVER = "stun:123.57.209.70:19302";

	private static final String DEFAULT_STUNSERVER = "stun:39.108.167.93:19302";

	/**
	 * The default turn server(pa server).
	 */
	//private static final String DEFAULT_TURN_URI = "turn:123.57.209.70:19302";
	private static final String DEFAULT_TURN_URI = "turn:39.108.167.93:19302";
	/**
	 * The default user name of turn server(pa server).
	 */
	//private static final String DEFAULT_TURN_USERNAME = "700";
	private static final String DEFAULT_TURN_USERNAME = "websip";
	/**
	 * The default password of turn server(pa server).
	 */
	//private static final String DEFAULT_TURN_PASSWORD = "700";
	private static final String DEFAULT_TURN_PASSWORD = "websip";
	/**
	 * The max video height of video.
	 */
	public static String mMaxVideoHeight = DEFAULT_MAX_VIDEO_H;
	/**
	 * The max video width of video.
	 */
	public static String mMaxVideoWidth = DEFAULT_MAX_VIDEO_W;
	/**
	 * The stun server (STUN protocol).
	 */
	public static String mStunServer = DEFAULT_STUNSERVER;
	/**
	 * The uri of turn server (TURN protocol).
	 */
	public static String mTurnUri = DEFAULT_TURN_URI;
	/**
	 * The user name of turn server (TURN protocol).
	 */
	public static String mTurnUser = DEFAULT_TURN_USERNAME;
	/**
	 * The password of turn server (TURN protocol).
	 */
	public static String mTurnPassword = DEFAULT_TURN_PASSWORD;

	//private CallStateEventListener mCallStateEventListener = null;
	//private CallStreamObserver mCallStreamObserver = null;

	@Override
	public void startVideoRender(final VideoSink remoteSink)
	{
        this.isConnected = true;
        //this.remoteRender = remoteRender;

		executor.execute(new Runnable() {
            @Override
				public void run() {
					remoteVideoTrack = getRemoteVideoTrack();
					remoteVideoTrack.setEnabled(renderVideo);
					//for (VideoSink remoteSink : remoteSinks) {
					remoteVideoTrack.addSink(remoteSink);
					//}
			}
        });
	}

	@Override
	public void closeWebRTC() {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				closeWebRtcInternal();
			}
		});
	}

	private @Nullable
	AudioTrack getRemoteAudioTrack() {
		for (RtpTransceiver transceiver : peerConnection.getTransceivers()) {
			MediaStreamTrack track = transceiver.getReceiver().track();
			if (track instanceof AudioTrack) {
				return (AudioTrack) track;
			}
		}
		return null;
	}

	// Returns the remote VideoTrack, assuming there is only one.
	private @Nullable
	VideoTrack getRemoteVideoTrack() {
		for (RtpTransceiver transceiver : peerConnection.getTransceivers()) {
			MediaStreamTrack track = transceiver.getReceiver().track();
			if (track instanceof VideoTrack) {
				return (VideoTrack) track;
			}
		}
		return null;
	}

	private @Nullable
	VideoTrack getLocalVideoTrack() {
		for (RtpSender transender : peerConnection.getSenders()) {
			MediaStreamTrack track = transender.track();
			if (track instanceof VideoTrack) {
				return (VideoTrack) track;
			}
		}
		return null;
	}

	@Override
	public void makeCall(String calleeUri, CallParams callParams)
	{
		this.calleeUri = calleeUri;
		updateCallParams(callParams);
		//this.mCallParams = callParams;
		{
			candidateTimeoutHandler.removeCallbacksAndMessages(null);
			Runnable runnable = new Runnable() {
				@Override
				public void run()
				{
					onCandidatesTimeout();
				}
			};
			candidateTimeoutHandler.postDelayed(runnable, 5000);
		}
		this.createPeerConnection();
		this.createOffer();
	}

	private void updateCallParams(CallParams callParams) {
		this.mCallParams = callParams;
		Log.e("call", "==========updateCallParams====" +this.mCallParams.remoteUri());
		this.startTime = new Date().getTime();
		this.mCallParams.setStartTime(startTime);
	}

	@Override
	public void directCall(String peerIp, int peerPort, CallParams callParams)
	{
        this.peerIp = peerIp;
		this.peerPort = peerPort;
		this.isDirectCall = true;
		this.calleeUri = peerIp;
		updateCallParams(callParams);
		{
			candidateTimeoutHandler.removeCallbacksAndMessages(null);
			Runnable runnable = new Runnable() {
				@Override
				public void run()
				{
					onCandidatesTimeout();
				}
			};
			candidateTimeoutHandler.postDelayed(runnable, 5000);
		}
		this.createPeerConnection();
		this.createOffer();
	}

	@Override
	public void accept(boolean isVideo) {
		this.videoCallEnabled = isVideo;
		mCallParams.enableVideo(isVideo);
		if(!this.RemoteSdp.isEmpty()) {
			{
				candidateTimeoutHandler.removeCallbacksAndMessages(null);
				Runnable runnable = new Runnable() {
					@Override
					public void run()
					{
						onCandidatesTimeout();
					}
				};
				candidateTimeoutHandler.postDelayed(runnable, 3000);
			}
		    this.createPeerConnection();
    	    this.setRemoteDescription(this.RemoteSdp, Type.OFFER);
		    this.createAnswer();
            this.isNoAccepted = false;
        } else {
            this.isNoAccepted = false;
        }
	}

	@Override
	public void updateByInfo(boolean isVideoCall) {
		//if(_videoCall == isVideoCall)
			//return;

		//isUpdateCall = true;
		//_videoCall = isVideoCall;
		if(!isVideoCall) {
			for (RtpSender sender : peerConnection.getSenders()) {
				if (sender.track() != null) {
					String trackType = sender.track().kind();
					if (trackType.equals(VIDEO_TRACK_TYPE)) {
						sender.track().setEnabled(false);
						peerConnection.removeTrack(sender);
					}
				}
			}
			if(this.mCallManager != null)
				mCallManager.stoplocalVideoSource();
		}

        //createOffer();
		//if(this.mCallManager != null)
			//mCallManager.changeMediaState(callId, true, true);
	}

	@Override
	public void updateCall(boolean isVideoCall) {
		if(this.videoCallEnabled == isVideoCall)
		   return;

		this.videoCallEnabled = isVideoCall;
		isUpdateCall = true;
		isOffer = true;
		mCallParams.enableVideo(this.videoCallEnabled);

		if(!isVideoCall) {
			for (RtpSender sender : peerConnection.getSenders()) {
				if (sender.track() != null) {
					String trackType = sender.track().kind();
					if (trackType.equals(VIDEO_TRACK_TYPE)) {
						sender.track().setEnabled(false);
						//peerConnection.removeTrack(sender);
					}
				}
			}

			//if(mCallManager != null)
				//mCallManager.stoplocalVideoSource();

			//sdpMediaConstraints.mandatory.remove(
				//	new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
			//sdpMediaConstraints.mandatory.add(
				//	new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
		} else {
			for (RtpSender sender : peerConnection.getSenders()) {
				if (sender.track() != null) {
					String trackType = sender.track().kind();
					if (trackType.equals(VIDEO_TRACK_TYPE)) {
						sender.track().setEnabled(true);
					}
				}
			}

			//if(mCallManager != null)
				//mCallManager.createlocalVideoSource(videoWidth, videoHeight, videoFps);

			//if(peerConnection != null) {
				//peerConnection.addTrack(localVideoTrack);
				//peerConnection.addTrack(createVideoTrack(), mediaStreamLabels);
			//}

			//sdpMediaConstraints.mandatory.remove(
					//new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
			//sdpMediaConstraints.mandatory.add(
					//new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
		}

		createOffer();
	}

	@Override
	public void reject(int code, String reason) {
		mCallParams.setRejectReasonCode(code);
		if(this.mCallManager != null)
			mCallManager.reject(callId, code, reason);
	}

	@Override
	public void hangup() {
		Log.e("hangup", "=====hangup======");
		executor.execute(new Runnable() {
		    @Override
		  public void run() {
				closeWebRtcInternal();
				if(mCallManager != null) {
					//if(mCallState == 1) {
						//mCallManager.reject(callId, 603, "Decline");
					//} else {
						mCallManager.hangup(callId);
					//}
				    mCallManager = null;
				}
		      }
		 });
	}

	@Override
	public int getCallId()
	{
		return this.callId;
	}

	@Override
	public CallParams getCallParams() {
		return this.mCallParams;
	}

	@Override
	public void OnCallOffer(String offerSdp) {
        RemoteSdp = offerSdp;
    }

	@Override
	public void OnCallAnswer(String answerSdp)
	{
		RemoteSdp = answerSdp;
		this.setRemoteDescription(this.RemoteSdp, Type.ANSWER);
	}

	@Override
	public void OnMediaStateChange(String remoteSdp, boolean audio, boolean video) {
		this.RemoteSdp = remoteSdp;
		this.isOffer = false;
		this.isUpdateCall = true;
		this.videoCallEnabled = video;
		mCallParams.enableVideo(this.videoCallEnabled);
		this.setRemoteDescription(remoteSdp, Type.OFFER);
		this.createAnswer();
	}

	private String changeCandidate(String sdpDescription) {
		// TODO Auto-generated method stub
		String[] lines = sdpDescription.split("\r\n");
	    int mLineIndex = -1;
	    String mediaPort = null;
	    StringBuilder newSdpDescription = new StringBuilder();
		StringBuilder newCandidateDescription = new StringBuilder();

	    String mediaDescription = "m=video ";
	    if (false) {
	      mediaDescription = "m=audio ";
	    }

	    for (int i = 0; (i < lines.length)
	        && (mLineIndex == -1); i++) {
		     newSdpDescription.append(lines[i]).append("\r\n");
	      if (lines[i].startsWith(mediaDescription)) {
	        mLineIndex = i;
	        continue;
	      }
	    }

	    if (mLineIndex == -1) {
		      Log.w(TAG, "No " + mediaDescription + " line, so can't prefer ");
		   return sdpDescription;
		} else {
			String[] mediaDescriptions =  lines[mLineIndex].split(" ");
			mediaPort = mediaDescriptions[1];

		    for (int i = mLineIndex+1; (i < lines.length); i++) {
		    	if(lines[i].indexOf("candidate:") != -1)
		    	{
		    		mLineIndex = i;
		    		String candidate = lines[i];
		    		String[] candidateDescription =  lines[mLineIndex].split(" ");
		    		candidateDescription[4] = "120.76.225.49";
		    		candidateDescription[5] = mediaPort;
					for(int j = 0; j < candidateDescription.length; j++)
						newCandidateDescription.append(candidateDescription[j]).append(" ");
		    	}
		    	if(mLineIndex != i) {
	    			newSdpDescription.append(lines[i]).append("\r\n");
	    		} else {
	    			newSdpDescription.append(newCandidateDescription.toString()).append("\r\n");
	    		}
		    }

		}

	    mLineIndex = -1;
	    

	   /* for (int i = 0; (i < lines.length); i++) {
	    	if(lines[i].indexOf("candidate:") != -1)
	    	{
	    		mLineIndex = i;
	    		String candidate = lines[i];
	    		String[] candidateDescription =  lines[mLineIndex].split(" ");
	    		candidateDescription[4] = "120.76.225.49";
	    		candidateDescription[5] = mediaPort;
				//mediaPort = mediaDescriptions[1];
				for(int j = 0; j < candidateDescription.length; j++)
					newCandidateDescription.append(candidateDescription[j]).append(" ");
				System.out.println("======mediaDescriptions===========:" +newCandidateDescription.toString());
	    	}
	    	if(mLineIndex != i) {
    			newSdpDescription.append(lines[i]).append("\r\n");
    		} else {
    			newSdpDescription.append(newCandidateDescription.toString()).append("\r\n");
    		}
	    }*/

		return newSdpDescription.toString();
	}

	//@Override
	//public int UpdateCall(boolean enable_video) {
	//	return UpdateCall(nativePtr,enable_video);
	//}
	//@Override
	//public int Hold() {
	//	return Hold(nativePtr);
	//}
	//@Override
	//public int UnHold() {
	//	return UnHold(nativePtr);
	//}
	//@Override
	//public String GetCallerId() {
	//	return GetCallerId(nativePtr);
	//}
	//@Override
	//public Direction GetDirection() {
	//	int dir_int = GetDirection(nativePtr);
	//	return (dir_int == Direction.Incoming.IntgerValue())
		//		? Direction.Incoming : Direction.Outgoing;
	//}
	/*@Override
	public CallState GetCallState() {
		int state_int = GetCallState(nativePtr);
		
		if(state_int == CallState.NewCall.IntgerValue())
			return CallState.NewCall;
		else if(state_int == CallState.Cancel.IntgerValue())
			return CallState.Cancel;
		else if(state_int == CallState.Failed.IntgerValue())
			return CallState.Failed;
		else if(state_int == CallState.Rejected.IntgerValue())
			return CallState.Rejected;
		else if(state_int == CallState.EarlyMedia.IntgerValue())
			return CallState.EarlyMedia;
		else if(state_int == CallState.Ringing.IntgerValue())
			return CallState.Ringing;
		else if(state_int == CallState.Answered.IntgerValue())
			return CallState.Answered;
		else if(state_int == CallState.hangup.IntgerValue())
			return CallState.hangup;
		else if(state_int == CallState.Pausing.IntgerValue())
			return CallState.Pausing;
		else if(state_int == CallState.Paused.IntgerValue())
			return CallState.Paused;
		else if(state_int == CallState.Resuming.IntgerValue())
			return CallState.Resuming;
		else if(state_int == CallState.Resumed.IntgerValue())
			return CallState.Resumed;
		else if(state_int == CallState.Updating.IntgerValue())
			return CallState.Updating;
		else if(state_int == CallState.Updated.IntgerValue())
			return CallState.Updated;
		
		return CallState.Unknown;
	}
	@Override
	public boolean GetSupportVideo() {
		return GetSupportVideo(nativePtr);
	}
	@Override
	public boolean GetSupportData() {
		return GetSupportVideo(nativePtr);
	}
	//@Override
	//public SipProfile GetProfile() {
	//	long sipProfilePtr = GetProfile(nativePtr);
		//return new SipProfileImpl(sipProfilePtr);
	//}
	//@Override
	//public MediaStream GetMediaStream() {
	//	long mediaStreamPtr = GetMediaStream(nativePtr);
	//	return new MediaStreamImpl(mediaStreamPtr);
	//}
	@Override
	public int GetErrorCode() {
		return GetErrorCode(nativePtr);
	}
	@Override
	public String GetErrorReason() {
		return GetErrorReason(nativePtr);
	}
	@Override
	public CallReport GetCallReport() {
		long callReportPtr = GetCallReport(nativePtr);
		return new CallReportImpl(callReportPtr);
	}
	@Override
	public String CallStateName(CallState state) {
		return CallStateName(nativePtr, state.IntgerValue());
	}
	@Override
	public String GetUniqueId() {
		return GetUniqueId(nativePtr);
	}
	@Override
	public void Accept(boolean send_audio, boolean send_video) {
		Accept2(nativePtr,send_audio,send_video);
	}*/


	private void createPeerConnection() {
		    if (mCallParams == null) {
		      Log.e(TAG, "Creating peer connection without callConfig.");
		      return;
		    }
		    //this.localRender = callConfig.localVideoSink;
		    //this.remoteRender = callConfig.remoteVideoSink;
		    //statsTimer = new Timer();
		    executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        createMediaConstraintsInternal();
                        createPeerConnectionInternal();
                    }
		    });
    }

	private void createMediaConstraintsInternal() {

        if(mCallParams != null) {
			videoCallEnabled = mCallParams.videoEnabled();
            // Initialize field trials.
            String fieldTrials = "";
            //if (callConfig.videoFlexfecEnabled) {
              //  fieldTrials += VIDEO_FLEXFEC_FIELDTRIAL;
               // Log.d(TAG, "Enable FlexFEC field trial.");
            //}
            //fieldTrials += VIDEO_VP8_INTEL_HW_ENCODER_FIELDTRIAL;
            //if (callConfig.disableWebRtcAGCAndHPF) {
                //fieldTrials += DISABLE_WEBRTC_AGC_FIELDTRIAL;
                //Log.d(TAG, "Disable WebRTC AGC field trial.");
            //}

            // Check preferred video codec.
            preferredVideoCodec = VIDEO_CODEC_VP8;
            if (videoCallEnabled && mCallParams.videoCodec() != null) {
                switch (mCallParams.videoCodec()) {
                    case VIDEO_CODEC_VP8:
                        preferredVideoCodec = VIDEO_CODEC_VP8;
                        break;
                    case VIDEO_CODEC_VP9:
                        preferredVideoCodec = VIDEO_CODEC_VP9;
                        break;
                    case VIDEO_CODEC_H264_BASELINE:
                        preferredVideoCodec = VIDEO_CODEC_H264;
                        break;
                    case VIDEO_CODEC_H264_HIGH:
                        // TODO(magjed): Strip High from SDP when selecting Baseline instead of using field trial.
                        fieldTrials += VIDEO_H264_HIGH_PROFILE_FIELDTRIAL;
                        preferredVideoCodec = VIDEO_CODEC_H264;
                        break;
                    default:
                        preferredVideoCodec = VIDEO_CODEC_VP8;
                }
            }
            //fieldTrials += WEBRTC_IPV6DEFAULT_FIELDTRIAL;

            Log.d(TAG, "Preferred video codec: " + preferredVideoCodec);
            //PeerConnectionFactory.initializeFieldTrials(fieldTrials);
            //Log.d(TAG, "Field trials: " + fieldTrials);

            // Check if ISAC is used by default.
            preferIsac = mCallParams.audioCodec() != null
                    && mCallParams.audioCodec().equals(AUDIO_CODEC_ISAC);
			preferPcma = mCallParams.audioCodec() != null
					&& mCallParams.audioCodec().equals(AUDIO_CODEC_PCMA);
        }

        // Create peer connection constraints.
        pcConstraints = new MediaConstraints();
        // Enable DTLS for normal calls and disable for loopback calls.
        pcConstraints.optional.add(
                new MediaConstraints.KeyValuePair(DTLS_SRTP_KEY_AGREEMENT_CONSTRAINT, "true"));

        if (videoCallEnabled) {
            videoWidth = mCallParams.videoWidth();
            videoHeight = mCallParams.videoHeight();
            videoFps = mCallParams.videoFps();

            // If video resolution is not specified, default to HD.
            if (videoWidth == 0 || videoHeight == 0) {
                videoWidth = HD_VIDEO_WIDTH;
                videoHeight = HD_VIDEO_HEIGHT;
            }

            // If fps is not specified, default to 15.
            if (videoFps == 0) {
                videoFps = 15;
            }
            Logging.e(TAG, "Capturing format: " + videoWidth + "x" + videoHeight + "@" + videoFps);
        }

        // Create audio constraints.
        //audioConstraints = new MediaConstraints();
        // added for audio performance measurements
        /*if (mCallParams.audioProcessing()) {
            Log.d(TAG, "audio processing");
			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_CONSTRAINT, "true"));
			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_ECHO_CANCELLATION_CONSTRAINT, "true"));

			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_HIGH_PASS_FILTER_CONSTRAINT, "true"));
			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_NOISE_SUPPRESSION_CONSTRAINT, "true"));

			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_LEVEL_CONSTRAINT,
							String.valueOf(mCallParams.agcControlLevel())));
			audioConstraints.mandatory.add(
					new MediaConstraints.KeyValuePair(AUDIO_AUTO_GAIN_CONTROL_GAIN_CONSTRAINT,
							String.valueOf(mCallParams.agcControlGain())));

        }*/

        //}
        ///if (callConfig.enableLevelControl) {
		//Log.d(TAG, "Enabling level control.");
		//audioConstraints.mandatory.add(
				//new MediaConstraints.KeyValuePair(AUDIO_LEVEL_CONTROL_CONSTRAINT, "true"));
        // Create SDP constraints.
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        if (videoCallEnabled) {
            sdpMediaConstraints.mandatory.add(
                    new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        } else {
            sdpMediaConstraints.mandatory.add(
				new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"));
	    }
	  }

	  private void createPeerConnectionInternal() {
	    Log.e(TAG, "Create peer connection.");
	    Log.e(TAG, "PCConstraints: " + pcConstraints.toString());
	    if (videoConstraints != null) {
	      Log.d(TAG, "VideoConstraints: " + videoConstraints.toString());
	    }

	   // if (videoCallEnabled && callConfig.videoCodecHwAcceleration) {
         //     Log.d(TAG, "EGLContext: " + renderEGLContext);
          //  mCallManager.setVideoHwAccelerationOptions(renderEGLContext, renderEGLContext);
        //}

        if (dataChannelEnabled) {
             /* DataChannel.Init init = new DataChannel.Init();
              init.ordered = peerConnectionParameters.dataChannelParameters.ordered;
              init.negotiated = peerConnectionParameters.dataChannelParameters.negotiated;
              init.maxRetransmits = peerConnectionParameters.dataChannelParameters.maxRetransmits;
              init.maxRetransmitTimeMs = peerConnectionParameters.dataChannelParameters.maxRetransmitTimeMs;
              init.id = peerConnectionParameters.dataChannelParameters.id;
              init.protocol = peerConnectionParameters.dataChannelParameters.protocol;
              dataChannel = peerConnection.createDataChannel("ApprtcDemo data", init);*/
          }
          isInitiator = false;

          // Set default WebRTC tracing and INFO libjingle logging.
          // NOTE: this _must_ happen while |factory| is alive!
          Logging.enableTracing("logcat:", EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT));
          Logging.enableLogToDebugOutput(Logging.Severity.LS_ERROR);


          //add by david.xu
	    LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<PeerConnection.IceServer>();
	    PeerConnection.IceServer stunServer = new PeerConnection.IceServer(
	                    mStunServer);
	    PeerConnection.IceServer turnIceServer = new PeerConnection.IceServer(
	    		 mTurnUri, mTurnUser, mTurnPassword);
	    //iceServers.add(stunServer);
	    //iceServers.add(turnIceServer);

	    PeerConnection.RTCConfiguration rtcConfig =
	        new PeerConnection.RTCConfiguration(iceServers);
	    // TCP candidates are only useful when connecting to a server that supports
	    // ICE-TCP.
	    //add by david.xu
	    //rtcConfig.iceTransportsType = PeerConnection.IceTransportsType.RELAY;


	    rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
	    rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXCOMPAT;
	    rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.NEGOTIATE;
	    rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;
	    rtcConfig.iceCandidatePoolSize = 1;
		rtcConfig.disableIPv6OnWifi = true;

	    //rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
	    //rtcConfig.keyType = PeerConnection.KeyType.RSA;
		CallManagerParams callManagerParams = new CallManagerParams(mCallParams.videoEnabled(),
				false, false, false,
				   false, false, false,
				    mCallParams.audioProcessing(), mCallParams.agcControlLevel(), mCallParams.agcControlGain());
	    peerConnection = mCallManager.createPeerConnection(callManagerParams,
	        rtcConfig, pcObserver);

	    Logging.enableLogToDebugOutput(Logging.Severity.LS_INFO);

	    // Set default WebRTC tracing and INFO libjingle logging.
	    // NOTE: this _must_ happen while |factory| is alive!
	   // Logging.enableTracing(
	    //    "logcat:",
	     //   EnumSet.of(Logging.TraceLevel.TRACE_DEFAULT));

	    //mediaStream = mCallManager.getPeerConnectionFactory().createLocalMediaStream("ARDAMS");
	    /*mediaStreamLabels = Collections.singletonList("ARDAMS");
	    if (videoCallEnabled) {
			peerConnection.addTrack(createVideoTrack(), mediaStreamLabels);
			if(mCallManager != null)
			{
				mCallManager.onLocalVideoReady(this.callId);
			}
	    }

	    //mediaStream.addTrack(createAudioTrack());
	    //peerConnection.addStream(mediaStream);
	    peerConnection.addTrack(createAudioTrack(), mediaStreamLabels);
          if (videoCallEnabled) {
              findVideoSender();
          }*/

		  createMediaSenders();
	  }

	  private void setRemoteDescription(final String sdp, final Type type) {
		    executor.execute(new Runnable() {
		      @Override
		      public void run() {
		        if (peerConnection == null || isError) {
		          return;
		        }
		      SessionDescription mRemoteSdp =
		    		  new SessionDescription(type, sdp);

		        String sdpDescription = mRemoteSdp.description;
		        if (preferIsac) {
		          sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
		        }
		        if(preferPcma) {
					sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_PCMA, true);
				}
		        if (videoCallEnabled) {
		          sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
		          sdpDescription = setStartBitrate(
                          VIDEO_CODEC_VP8, true, sdpDescription, 500);
		        }
                  if (mCallParams.audioStartBitrate() > 0) {
                      sdpDescription = setStartBitrate(
                              AUDIO_CODEC_OPUS, false, sdpDescription, mCallParams.audioStartBitrate());
                  }
                  Log.d(TAG, "================Set remote SDP====:" +sdpDescription);
		        SessionDescription sdpRemote = new SessionDescription(
		        		mRemoteSdp.type, sdpDescription);
		        peerConnection.setRemoteDescription(sdpObserver, sdpRemote);
		      }
		    });
		}

		private void createMediaSenders()
		{
			if(peerConnection != null) {
			   mediaStreamLabels = Collections.singletonList("ARDAMS");
			   if (videoCallEnabled) {
				  peerConnection.addTrack(createVideoTrack(), mediaStreamLabels);
				  if(mCallManager != null)
				  {
				  	reattachLocalVideo();
					mCallManager.onLocalVideoReady(this.callId);
				  }
			   }

			   //mediaStream.addTrack(createAudioTrack());
			   //peerConnection.addStream(mediaStream);
			   peerConnection.addTrack(createAudioTrack(), mediaStreamLabels);
			   if (videoCallEnabled) {
				findVideoSender();
			   }
			}
		}

	  private AudioTrack createAudioTrack() {
		    localAudioTrack = mCallManager.createAudioTrack(
		        AUDIO_TRACK_ID,
		        mCallManager.createlocalAudioSource());
		  localAudioTrack.setVolume(4.0);
		  localAudioTrack.setEnabled(true);
		    return localAudioTrack;
	  }

	  private VideoTrack createVideoTrack() {
		    localVideoTrack = mCallManager.createVideoTrack(VIDEO_TRACK_ID,
					mCallManager.createlocalVideoSource(videoWidth, videoHeight, videoFps));

		    localVideoTrack.setEnabled(true);
		    return localVideoTrack;
	 }

	  @Override
	  public void setAudioEnabled(final boolean enable) {
		    executor.execute(new Runnable() {
		      @Override
		      public void run() {
		        enableAudio = enable;
		        //mCallParams.setMute(enableAudio);
		        if (localAudioTrack != null) {
		          localAudioTrack.setEnabled(enableAudio);
		        }
		      }
		    });
		  }

	  @Override
	  public void setVideoEnabled(final boolean enable) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				renderVideo = enable;
		        if (localVideoTrack != null) {
		          localVideoTrack.setEnabled(renderVideo);
		        }
		        if (remoteVideoTrack != null) {
		          remoteVideoTrack.setEnabled(renderVideo);
		        }
			}
		});
	  }

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public String getWebtcErrMessage() {
		return this.errorMessage;
	}


	private void createOffer() {
		    executor.execute(new Runnable() {
		      @Override
		      public void run() {
		        if (peerConnection != null && !isError) {
		          isInitiator = true;
		          //createMediaSenders();
		          peerConnection.createOffer(sdpObserver, sdpMediaConstraints);
		        }
		      }
		    });
		  }

	private void createAnswer() {
		    executor.execute(new Runnable() {
		      @Override
		      public void run() {
		        if (peerConnection != null && !isError) {
		          isInitiator = false;
		          //createMediaSenders();
		          peerConnection.createAnswer(sdpObserver, sdpMediaConstraints);
		        }
		      }
		    });
	}

    private static String setStartBitrate(
            String codec, boolean isVideoCodec, String sdpDescription, int bitrateKbps) {
        String[] lines = sdpDescription.split("\r\n");
        int rtpmapLineIndex = -1;
        boolean sdpFormatUpdated = false;
        String codecRtpMap = null;
        // Search for codec rtpmap in format
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                rtpmapLineIndex = i;
                break;
            }
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec + " codec");
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + " at " + lines[rtpmapLineIndex]);

        // Check if a=fmtp string already exist in remote SDP for this codec and
        // update it with new bitrate parameter.
        regex = "^a=fmtp:" + codecRtpMap + " \\w+=\\d+.*[\r]?$";
        codecPattern = Pattern.compile(regex);
        for (int i = 0; i < lines.length; i++) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                Log.d(TAG, "Found " + codec + " " + lines[i]);
                if (isVideoCodec) {
                    lines[i] += "; " + VIDEO_CODEC_PARAM_MAX_BITRATE + "=" + bitrateKbps;
                    lines[i] += "; " + VIDEO_CODEC_PARAM_MIN_BITRATE + "=" + 200;
                    lines[i] += "; " + VIDEO_CODEC_PARAM_START_BITRATE + "=" + 200;
                } else {
                    lines[i] += "; " + AUDIO_CODEC_PARAM_BITRATE + "=" + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Update remote SDP line: " + lines[i]);
                sdpFormatUpdated = true;
                break;
            }
        }

        StringBuilder newSdpDescription = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            newSdpDescription.append(lines[i]).append("\r\n");
            // Append new a=fmtp line if no such line exist for a codec.
            if (!sdpFormatUpdated && i == rtpmapLineIndex) {
                String bitrateSet;
                if (isVideoCodec) {
                    bitrateSet =
                            "a=fmtp:" + codecRtpMap + " "+VIDEO_CODEC_PARAM_START_BITRATE+"="+200
					 +"; "+VIDEO_CODEC_PARAM_MIN_BITRATE+"="+200+"; "+VIDEO_CODEC_PARAM_MAX_BITRATE+"="+bitrateKbps;
                } else {
                    bitrateSet = "a=fmtp:" + codecRtpMap + " " + AUDIO_CODEC_PARAM_BITRATE + "="
                            + (bitrateKbps * 1000);
                }
                Log.d(TAG, "Add remote SDP line: " + bitrateSet);
                newSdpDescription.append(bitrateSet).append("\r\n");
            }
        }
        return newSdpDescription.toString();
    }

    private static String joinString(
            Iterable<? extends CharSequence> s, String delimiter, boolean delimiterAtEnd) {
        Iterator<? extends CharSequence> iter = s.iterator();
        if (!iter.hasNext()) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(iter.next());
        while (iter.hasNext()) {
            buffer.append(delimiter).append(iter.next());
        }
        if (delimiterAtEnd) {
            buffer.append(delimiter);
        }
        return buffer.toString();
    }

    private static String movePayloadTypesToFront(List<String> preferredPayloadTypes, String mLine) {
        // The format of the media description line should be: m=<media> <port> <proto> <fmt> ...
        final List<String> origLineParts = Arrays.asList(mLine.split(" "));
        if (origLineParts.size() <= 3) {
            Log.e(TAG, "Wrong SDP media description format: " + mLine);
            return null;
        }
        final List<String> header = origLineParts.subList(0, 3);
        final List<String> unpreferredPayloadTypes =
                new ArrayList<String>(origLineParts.subList(3, origLineParts.size()));
        unpreferredPayloadTypes.removeAll(preferredPayloadTypes);
        // Reconstruct the line with |preferredPayloadTypes| moved to the beginning of the payload
        // types.
        final List<String> newLineParts = new ArrayList<String>();
        newLineParts.addAll(header);
        newLineParts.addAll(preferredPayloadTypes);
        newLineParts.addAll(unpreferredPayloadTypes);
        return joinString(newLineParts, " ", false /* delimiterAtEnd */);
    }

    private static String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        final String[] lines = sdpDescription.split("\r\n");
        final int mLineIndex = findMediaDescriptionLine(isAudio, lines);
        if (mLineIndex == -1) {
            Log.w(TAG, "No mediaDescription line, so can't prefer " + codec);
            return sdpDescription;
        }
        // A list with all the payload types with name |codec|. The payload types are integers in the
        // range 96-127, but they are stored as strings here.
        final List<String> codecPayloadTypes = new ArrayList<String>();
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        final Pattern codecPattern = Pattern.compile("^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$");
        for (int i = 0; i < lines.length; ++i) {
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecPayloadTypes.add(codecMatcher.group(1));
            }
        }
        if (codecPayloadTypes.isEmpty()) {
            Log.w(TAG, "No payload types with name " + codec);
            return sdpDescription;
        }

        final String newMLine = movePayloadTypesToFront(codecPayloadTypes, lines[mLineIndex]);
        if (newMLine == null) {
            return sdpDescription;
        }
        Log.d(TAG, "Change media description from: " + lines[mLineIndex] + " to " + newMLine);
        lines[mLineIndex] = newMLine;
        return joinString(Arrays.asList(lines), "\r\n", true /* delimiterAtEnd */);
    }

    /** Returns the line number containing "m=audio|video", or -1 if no such line exists. */
    private static int findMediaDescriptionLine(boolean isAudio, String[] sdpLines) {
        final String mediaDescription = isAudio ? "m=audio " : "m=video ";
        for (int i = 0; i < sdpLines.length; ++i) {
            if (sdpLines[i].startsWith(mediaDescription)) {
                return i;
            }
        }
        return -1;
    }

	/** Returns the line number containing "m=msid:", or -1 if no such line exists. */
	private static int findDirectionDescriptionLine(String[] sdpLines) {
		for (int i = 0; i < sdpLines.length; ++i) {
			if (sdpLines[i].startsWith("a=sendrecv") || sdpLines[i].startsWith("a=sendonly") ||
					sdpLines[i].startsWith("a=recvonly") || sdpLines[i].startsWith("a=inactive")) {
				return i;
			}
		}
		return -1;
	}

	private static String setInviteDirection(
			String sdpDescription, String direction) {
		String[] lines = sdpDescription.split("\r\n");

		final int mVideoLineIndex = findMediaDescriptionLine(false, lines);
		final int mDirectionLineIndex = findDirectionDescriptionLine(lines);

		if (mVideoLineIndex == -1) {
			Log.w(TAG, "No mediaDescription line, so can't reInviteSdp ");
			return sdpDescription;
		}

		Log.w(TAG, "======mVideoLineIndex=====:" +mVideoLineIndex +":" +lines.length);

		StringBuilder newSdpDescription = new StringBuilder();
		for (int i = 0; (i < lines.length); i++) {
			if(mDirectionLineIndex == i) {
				Log.w(TAG, "======mVideoLineIndex=====append:");
				newSdpDescription.append("a="+direction).append("\r\n");
			} else {
			    newSdpDescription.append(lines[i]).append("\r\n");
			}
		}

		return newSdpDescription.toString();
	}

    private static String preferTransportCandidate(
		      String sdpDescription, String codec, boolean isAudio) {
		    String[] lines = sdpDescription.split("\r\n");
		    int mLineIndex = -1;
		    // a=candidate:1510613869 1 udp 2122129151 127.0.0.1 33936 typ host generation 0 network-id 1
		    // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]

		    StringBuilder newSdpDescription = new StringBuilder();
		    for (int i = 0; (i < lines.length); i++) {
		    	if(lines[i].indexOf("candidate:") != -1)
		    	{
		    		if(lines[i].indexOf(codec) != -1) {
				        mLineIndex = i;
		    		}
		    	}
		    	if(mLineIndex != i)
	    			newSdpDescription.append(lines[i]).append("\r\n");
		    }

		    return newSdpDescription.toString();
	 }

	 // Implementation detail: observe ICE & stream changes and react accordingly.
	 public class PCObserver implements PeerConnection.Observer {
	    @Override
	    public void onIceCandidate(final IceCandidate candidate){
	      executor.execute(new Runnable() {
	        @Override
	        public void run() {
				if (iceCandidates == null) {
					iceCandidates = new LinkedList<IceCandidate>();
				}
				iceCandidates.add(candidate);
	        }
	      });
	    }

	    @Override
	    public void onSignalingChange(
	        PeerConnection.SignalingState newState) {
	      Log.d(TAG, "SignalingState: " + newState);
	    }

	    @Override
	    public void onIceConnectionChange(
	        final IceConnectionState newState) {
	      executor.execute(new Runnable() {
	        @Override
	        public void run() {
	          Log.d(TAG, "IceConnectionState: " + newState);
	          if (newState == IceConnectionState.CONNECTED) {
	            //events.onIceConnected();
	          } else if (newState == IceConnectionState.DISCONNECTED) {
	            //events.onIceDisconnected();
	          } else if (newState == IceConnectionState.FAILED) {
	            reportError("ICE connection failed.");
	          }
	        }
	      });
	    }

		 //@Override
		 //public void onStandardizedIceConnectionChange(IceConnectionState iceConnectionState) {
//
		 //}

		 @Override
		 public void onConnectionChange(PeerConnection.PeerConnectionState peerConnectionState) {

		 }

		 @Override
	    public void onIceGatheringChange(
	    		final IceGatheringState newState) {
	    	 executor.execute(new Runnable() {
	    	     @Override
	    	      public void run() {
	   	          Log.d(TAG, "IceGatheringState: " + newState);
	    	      if (newState == IceGatheringState.COMPLETE) {
	    	    	//  iceGatherComplete = true;
	    	    	  //onIceGatherComplete();
	    	    	  CallOrAnswer(peerConnection.getLocalDescription());

	    	      } else if (newState == IceGatheringState.GATHERING) {
	    	        // events.onIceDisconnected();
	    	      }
	    	    }
	        });
	    }

	    @Override
	    public void onIceConnectionReceivingChange(boolean receiving) {
	      Log.d(TAG, "IceConnectionReceiving changed to " + receiving);
	    }

	    @Override
	    public void onAddStream(final MediaStream stream) {
	      executor.execute(new Runnable() {
	        @Override
	        public void run() {
	          if (peerConnection == null || isError) {
	            return;
	          }
	          if (stream.audioTracks.size() > 1 || stream.videoTracks.size() > 1) {
	            reportError("Weird-looking stream: " + stream);
	            return;
	          }

	          //stream.audioTracks.get(0).setVolume(5);
	          MediaStreamReady = true;
	          mStream = stream;

	          //if(isConnected) {
	      		System.out.println("isConnected=onAddStream:");
				if(mCallManager != null)
				{
					reattachRemoteVideo(remoteRenderLayout);
					mCallManager.onRemoteVideoReady(callId);
				}

				remoteAudioTrack = getRemoteAudioTrack();
				remoteAudioTrack.setVolume(4.0);

	      		/*if(mStream != null) {
					  if (mStream.videoTracks.size() == 1) {
						  remoteVideoTrack = mStream.videoTracks.get(0);
						  //add by david.xu
						  if(remoteProxyRenderer != null) {
							  remoteVideoTrack.setEnabled(renderVideo);
							  remoteVideoTrack.addSink(remoteProxyRenderer);
						  }
						  if(mCallManager != null)
						  {
							  mCallManager.onRemoteVideoReady(callId);
						  }
					  }
				  }*/
	          //} else {
	          //}
	        }
	      });
	    }

	    @Override
	    public void onRemoveStream(final MediaStream stream){
	      executor.execute(new Runnable() {
	        @Override
	        public void run() {
	          remoteVideoTrack = null;
	        }
	      });
	    }

	    @Override
	    public void onDataChannel(final DataChannel dc) {
	      reportError("CloudRTC doesn't use data channels, but got: " + dc.label()
	          + " anyway!");
	    }

	    @Override
	    public void onRenegotiationNeeded() {
	      // No need to do anything; CloudRTC follows a pre-agreed-upon
	      // signaling/negotiation protocol.
	    }

		 @Override
		 public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
		 }

		 @Override
		 public void onTrack(RtpTransceiver rtpTransceiver) {

		 }

		 @Override
		public void onIceCandidatesRemoved(IceCandidate[] arg0) {
			// TODO Auto-generated method stub

		}

		 //@Override
		 //public void onSelectedCandidatePairChanged(CandidatePairChangeEvent candidatePairChangeEvent) {
//
		 //}
		 // @Override
		 //public void onAddTrack(RtpReceiver var1, MediaStream[] var2) {

		 //}
	  }

	private void onCandidatesTimeout() {
    	if(iceCandidates != null)
    	  if(iceCandidates.size() > 0)
		     CallOrAnswer(peerConnection.getLocalDescription());
	}


	      // Implementation detail: handle offer creation/signaling and answer setting,
		  // as well as adding remote ICE candidates once the answer SDP is set.
		  private class SDPObserver implements SdpObserver {
		    @Override
		    public void onCreateSuccess(final SessionDescription origSdp) {
		      if (localSdp != null && !isUpdateCall) {
		        reportError("Multiple SDP create.");
		        return;
		      }

		      String sdpDescription = origSdp.description;
		      if (preferIsac) {
		        sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_ISAC, true);
		      }

		      if(preferPcma) {
		      	sdpDescription = preferCodec(sdpDescription, AUDIO_CODEC_PCMA, true);
		      }

		      if (videoCallEnabled) {
		        sdpDescription = preferCodec(sdpDescription, preferredVideoCodec, false);
		      }

		      if(isUpdateCall) {
		      	if(mCallState == CallState.Answered.IntgerValue()) {
		      		mCallManager.updateMediaState(callId, isOffer, sdpDescription, true, videoCallEnabled);
		      		isUpdateCall = false;
		      	}
		      }

		      final SessionDescription sdp = new SessionDescription(
		          origSdp.type, sdpDescription);
		      localSdp = sdp;
		      executor.execute(new Runnable() {
		        @Override
		        public void run() {
		          if (peerConnection != null && !isError) {
		            Log.d(TAG, "create local SDP from " + sdp.type);
		            peerConnection.setLocalDescription(sdpObserver, sdp);
		          }
		        }
		      });
		    }

		    @Override
		    public void onSetSuccess() {
		      executor.execute(new Runnable() {
		        @Override
		        public void run() {
		          if (peerConnection == null || isError) {
		            return;
		          }
		          if (isInitiator) {
		            // For offering peer connection we first create offer and set
		            // local SDP, then after receiving answer set remote SDP.
		            if (peerConnection.getRemoteDescription() == null) {
		              // We've just set our local SDP so time to send it.
		              Log.d(TAG, "offer Local SDP set succesfully");
		              //events.onLocalDescription(localSdp);
		              onLocalDescription(localSdp);
		            } else {
		              // We've just set remote description, so drain remote
		              // and send local ICE candidates.
		              Log.d(TAG, "offer,Remote SDP set succesfully");
		              //drainCandidates();
					  /*if(isUpdateCall) {
							if(mCallState == CallState.Answered.IntgerValue()) {
								mCallManager.update(callId, localSdp.description);
								isUpdateCall = false;
							}
					  }*/
		            }
		          } else {
		            // For answering peer connection we set remote SDP and then
		            // create answer and set local SDP.
		            if (peerConnection.getLocalDescription() != null) {
		              // We've just set our local SDP so time to send it, drain
		              // remote and send local ICE candidates.
		              Log.d(TAG, "answer Local SDP set succesfully");
		              onLocalDescription(localSdp);
		              if(isUpdateCall) {
		              	if(mCallState == CallState.Answered.IntgerValue()) {
							Log.d(TAG, "==isUpdateCall====answer Local SDP set succesfully");
		              		mCallManager.accept(callId, localSdp.description, true, videoCallEnabled);
		              		isUpdateCall = false;
		              	}
		              }
		            } else {
		              // We've just set remote SDP - do nothing for now -
		              // answer will be created soon.
		              Log.d(TAG, "answer,Remote SDP set succesfully");

		            }
		          }
		        }
		      });
		    }

		    @Override
		    public void onCreateFailure(final String error) {
		      reportError("createSDP error: " + error);
		    }

		    @Override
		    public void onSetFailure(final String error) {
		      reportError("setSDP error: " + error);
		      System.out.println("onSetFailure:" +error);
		    }
		  }

		 private void reportError(final String errorMessage) {
			    Log.e(TAG, "Peerconnection error: " + errorMessage);
			    executor.execute(new Runnable() {
			      @Override
			      public void run() {
			        if (!isError) {
			        	mCallParams.setReason(1);
						CallImpl.this.errorMessage = errorMessage;
						if(mCallManager != null)
			               mCallManager.onPeerConnectionError(callId, errorMessage);

			            isError = true;
			        }
			      }
			});
		 }

		 private void onLocalDescription(SessionDescription localSdp) {
				// TODO Auto-generated method stub
	         Log.d(TAG, "======offer==onLocalDescription========");

					String type = null;
					if(localSdp.type == Type.OFFER) {
						type = "offer";
					} else if(localSdp.type == Type.ANSWER) {
						type = "answer";
					}
		 }

		 private void CallOrAnswer(final SessionDescription Localsdp)
		 {
			     candidateTimeoutHandler.removeCallbacksAndMessages(null);
			     if (!iceGatheringCompleteCalled) {
			 	        iceGatheringCompleteCalled = true;
				    	if(Localsdp != null) {
							if(isInitiator) {
								 //Log.d(TAG, "nativeMakeCall:" +Localsdp.description);
								 if(this.mCallManager != null) {
									 if(!isDirectCall) {
										mCallManager.makeCall(accId, this.callId, calleeUri, Localsdp.description);
									 } else if(isDirectCall) {
									 	mCallManager.directCall(accId, this.callId, peerIp, peerPort, Localsdp.description);
									 }
								 }
							} else {
								 //Log.d(TAG, "nativeAnswerCall:" +Localsdp.description);
								 if(this.mCallManager != null)
									mCallManager.accept(this.callId, Localsdp.description, true, videoCallEnabled);
							}
				    	}
			    }
		 }


		 @Override
		 public void closeSip() {
			  executor.execute(new Runnable() {
			    @Override
			  public void run() {
			        closeSipInternal();
			      }
			 });
	    }

		 private void closeWebRtcInternal() {
			 candidateTimeoutHandler.removeCallbacksAndMessages(null);
			 if (peerConnection != null) {
			      peerConnection.dispose();
			      peerConnection = null;
			 }

			    //statsTimer.cancel();
			 if(mCallManager != null) {
			 	mCallManager.stoplocalVideoSource();
			 	mCallManager.stoplocalAudioSource();
			 }
			 this.isNoAccepted = true;
			    //localRender = null;
                //remoteRender = null;
	     }

	    private void closeSipInternal() {
		  //statsTimer.cancel();
		  if(mCallManager != null) {
			mCallManager.unregisterCall(this);
			mCallManager = null;
		  }
		//localRender = null;
		//remoteRender = null;
	    }

		 @Override
		 public void setCallMediaStatsReport(int callId, CallMediaStatsReport mListener) {
				this.mCallMediaStatsReport = mListener;
		 }

	private void getStats() {
		if (peerConnection == null || isError) {
			return;
		}
		boolean success = peerConnection.getStats(new StatsObserver() {
			@Override
			public void onComplete(StatsReport[] reports) {
				if(mCallMediaStatsReport != null)
				    mCallMediaStatsReport.onCallMediaStatsReady(reports);
			}
		}, null);
		if (!success) {
			Log.e(TAG, "getStats() returns false!");
		}
	}

	public void enableStatsEvents(boolean enable, int periodMs) {
		/*if (enable) {
			try {
				statsTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						executor.execute(new Runnable() {
							@Override
							public void run() {
								getStats();
							}
						});
					}
				}, 0, periodMs);
			} catch (Exception e) {
				Log.e(TAG, "Can not schedule statistics timer", e);
			}
		} else {
			statsTimer.cancel();
		}*/
	}

	@Override
	public void startVideoSending()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
				if (localVideoTrack != null) {
					localVideoTrack.setEnabled(true);
				}
            }
        });
	}

	@Override
	public void startVideoReceiving()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
				renderVideo = true;
				if (remoteVideoTrack != null) {
					remoteVideoTrack.setEnabled(true);
				}
            }
        });
	}

	@Override
	public void stopVideoSending()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //if(mCallManager != null)
                //    mCallManager.stopVideoSending(callId);
                if (localVideoTrack != null) {
			         localVideoTrack.setEnabled(false);
		        }
            }
        });
	}

	@Override
	public void stopVideoReceiving()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
				renderVideo = false;
				if (remoteVideoTrack != null) {
                    remoteVideoTrack.setEnabled(false);
                }
            }
        });
	}

	@Override
	public void startVoiceChannel()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
                enableAudio = true;
                if (localAudioTrack != null) {
                    localAudioTrack.setEnabled(enableAudio);
                }
            }
        });
	}

	@Override
	public void stopVoiceChannel()
	{
        executor.execute(new Runnable() {
            @Override
            public void run() {
                enableAudio = false;
                if (localAudioTrack != null) {
                    localAudioTrack.setEnabled(enableAudio);
                }
            }
        });
	}

    @Override
    public void switchCamera()
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(mCallManager != null)
                    mCallManager.switchCamera(callId);
            }
        });
    }

    @Override
    public void changeCaptureFormat(final int width, final int height, final int framerate)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if(mCallManager != null)
                    mCallManager.changeCaptureFormat(callId, width, height, framerate);
            }
        });
    }

	@Override
	public void setLocalVideoRender(FrameLayout localFrameLayout) {
    	System.out.println("==========setLocalVideoRender=========");
    	this.localRenderLayout = localFrameLayout;
		reattachLocalVideo();
		/*executor.execute(new Runnable() {
			@Override
			public void run() {
				if(localVideoTrack != null) {
					//for (VideoSink remoteSink : remoteSinks) {
					localVideoTrack.addSink(localProxyRenderer);
				}
				//}
			}
		});*/
	}

	@Override
	public void setRemoteVideoRender(FrameLayout remoteFrameLayout) {
		System.out.println("==========setRemoteVideoRender=========");
		this.remoteRenderLayout = remoteFrameLayout;
    	reattachRemoteVideo(remoteFrameLayout);
		/*executor.execute(new Runnable() {
			@Override
			public void run() {
				remoteVideoTrack = getRemoteVideoTrack();
				remoteVideoTrack.setEnabled(true);
				//for (VideoSink remoteSink : remoteSinks) {
				remoteVideoTrack.addSink(remoteProxyRenderer);
				//}
			}
		});*/
	}

	@Override
	public void updateCallState(int stateCode, int reasonCode) {
          mCallState = stateCode;
          mCallReason = reasonCode;
		  mCallParams.setState(stateCode);
          mCallParams.setReason(reasonCode);
          if(mCallState == CallState.Hangup.IntgerValue()) {
			  endTime = new Date().getTime();
			  mCallParams.setEndTime(endTime);
		  }
	}

	@Override
	public void onCallReceiveReinvite(String sdp) {
		isUpdateCall = true;
		this.setRemoteDescription(sdp, Type.OFFER);
		//this.createAnswer();
	}

	private void findVideoSender() {
        for (RtpSender sender : peerConnection.getSenders()) {
            if (sender.track() != null) {
                String trackType = sender.track().kind();
                if (trackType.equals(VIDEO_TRACK_TYPE)) {
                    Log.d(TAG, "Found video sender.");
                    localVideoSender = sender;
                }
            }
        }
    }

    @Override
    public void setVideoMaxBitrate(final Integer maxBitrateKbps) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (peerConnection == null || localVideoSender == null || isError) {
                    return;
                }
                Log.d(TAG, "Requested max video bitrate: " + maxBitrateKbps);
                if (localVideoSender == null) {
                    Log.w(TAG, "Sender is not ready.");
                    return;
                }

                RtpParameters parameters = localVideoSender.getParameters();
                if (parameters.encodings.size() == 0) {
                    Log.w(TAG, "RtpParameters are not ready.");
                    return;
                }

                for (RtpParameters.Encoding encoding : parameters.encodings) {
                    // Null value means no limit.
                    encoding.maxBitrateBps = maxBitrateKbps == null ? null : maxBitrateKbps * BPS_IN_KBPS;
                }
                if (!localVideoSender.setParameters(parameters)) {
                    Log.e(TAG, "RtpSender.setParameters failed.");
                }
                Log.d(TAG, "Configured max video bitrate to: " + maxBitrateKbps);
            }
        });
    }

    @Override
    public void setRemoteCallerUri(String peerCallerUri)
    {
        this.remoteCallerUri = remoteCallerUri;
    }

    @Override
    public String getRemoteCallerUri()
    {
        return this.remoteCallerUri;
    }

	@Override
	public void setRemoteDisplayName(String remoteDisplayName) {
       this.remoteDisplayName = remoteDisplayName;
	}

	@Override
	public String getRemoteDisplayName() {
		return this.remoteDisplayName;
	}

	private void reattachLocalVideo()
	{
		EglBase.Context  mEglContext = null;
		Context mContext = null;

		if (this.localRenderLayout == null) {
			return;
		}

		System.out.println("=====reattachLocalVideo====");
		scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

		if(mCallManager != null) {
			mEglContext = mCallManager.getRenderContext();
			mContext = mCallManager.getContext();
		} else {
			//return;
		}

		//&& (mEglContext != null))
		if((localSurfaceViewRender == null))
		{
		  localSurfaceViewRender = new SurfaceViewRenderer(mContext);
		  this.localRenderLayout.addView(localSurfaceViewRender);

		  localSurfaceViewRender.init(null, null);
		  localProxyRenderer.setTarget(localSurfaceViewRender);
		  localSurfaceViewRender.setZOrderMediaOverlay(true);
		  localSurfaceViewRender.setVisibility(View.VISIBLE);
		  localSurfaceViewRender.setScalingType(scalingType);
		  localSurfaceViewRender.setMirror(true);
		  localSurfaceViewRender.requestLayout();
		}

		executor.execute(new Runnable() {
			@Override
			public void run() {
				if(localVideoTrack != null) {
					//for (VideoSink remoteSink : remoteSinks) {
					localVideoTrack.addSink(localProxyRenderer);
				}
				//}
			}
		});
	}

	private void reattachRemoteVideo(final FrameLayout remoteRenderLayout)
	{
		EglBase.Context mEglContext = null;
		Context mContext = null;
		if (remoteRenderLayout == null) {
			return;
		}

		System.out.println("============reattachRemoteVideo===============");
		scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
		this.remoteRenderLayout = remoteRenderLayout;

		if(mCallManager != null) {
			mEglContext = mCallManager.getRenderContext();
			mContext = mCallManager.getContext();
		} else {
			//return;
		}

		//&& (mEglContext != null))
		if((remoteSurfaceViewRender == null)) {
		  remoteSurfaceViewRender = new SurfaceViewRenderer(mContext);
		  //remoteSurfaceViewRender.setZOrderOnTop(true);
		  this.remoteRenderLayout.addView(remoteSurfaceViewRender);
		  //remoteSurfaceViewRender = (SurfaceViewRenderer)remoteRenderLayout.getChildAt(0);
		  //if(mEglContext != null)
			remoteSurfaceViewRender.init(null, null);

		  remoteProxyRenderer.setTarget(remoteSurfaceViewRender);

		  remoteSurfaceViewRender.setVisibility(View.VISIBLE);

		  //remoteRenderLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
		  remoteSurfaceViewRender.setScalingType(scalingType);
		  remoteSurfaceViewRender.setMirror(false);

		//if (this.callParams.containsKey(ParameterKeys.CONNECTION_VIDEO_ENABLED) &&
			//	((Boolean) this.callParams.get(ParameterKeys.CONNECTION_VIDEO_ENABLED)) &&
			//	localRender.getVisibility() != View.VISIBLE) {
			//localRender.setVisibility(View.VISIBLE);
		//}
		//localRenderLayout.setPosition(
				//LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
		  remoteSurfaceViewRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
		  remoteSurfaceViewRender.setMirror(true);

		  if(localSurfaceViewRender != null)
		    localSurfaceViewRender.requestLayout();

		  remoteSurfaceViewRender.requestLayout();
		}

		executor.execute(new Runnable() {
			@Override
			public void run() {
				remoteVideoTrack = getRemoteVideoTrack();
				remoteVideoTrack.setEnabled(true);
				//for (VideoSink remoteSink : remoteSinks) {
				remoteVideoTrack.addSink(remoteProxyRenderer);
				//}
			}
		});
	}

	private static class ProxyVideoSink implements VideoSink {
		private VideoSink target;

		@Override
		synchronized public void onFrame(VideoFrame frame) {
			if (target == null) {
				//Logging.d(TAG, "Dropping frame in proxy because target is null.");
				return;
			}

			target.onFrame(frame);
		}

		synchronized public void setTarget(VideoSink target) {
			this.target = target;
		}
	}
}
