package com.reSipWebRTC.service;

import android.content.Context;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

public interface CallManager {

     Context getContext();
     EglBase.Context getRenderContext();
     Call createCall(int accId);
     void makeCall(int accId, int callId, String calleeUri, String localSdp);
	 void directCall(int accId, int callId, String peerIp, int peerPort, String localSdp);
	 void accept(int callId, String localSdp, boolean send_audio, boolean send_video);
     void update(int callId, String localSdp);
     void updateMediaState(int callId, boolean isOffer, String localSdp, boolean audio, boolean video);
	 void reject(int callId, int code, String reason);
     void hangup(int callId);
     void registerIncomingCallObserver(IncomingCallObserver observer);
     void registerCallStateObserver(CallStateEventListener observer);
     void deRegisterCallStateObserver();
     void registerCall(Call call);
   	 void unregisterCall(Call call);
     Call getCallByCallId(int callId);
     void onPeerConnectionError(int callId, final String description);
     void hangupAllCall();
     AudioSource createlocalAudioSource();
   	 VideoSource createlocalVideoSource(int width, int height, int framerate);
	 void setVideoHwAccelerationOptions(org.webrtc.EglBase.Context localEglContext,
                                        org.webrtc.EglBase.Context remoteEglContext);
	 void stoplocalAudioSource();
   	 void stoplocalVideoSource();
	// public int UpdateCall(boolean  enable_video);
   // public int Hold();
   // public int UnHold();
     void switchCamera(int callId);
     void changeCaptureFormat(int callId, int width, int height, int framerate);
     boolean isActivityCall();
     PeerConnection createPeerConnection(CallManagerParams callManagerParams,
                                               PeerConnection.RTCConfiguration rtcConfig,
                                               CallImpl.PCObserver pcObserver);
     AudioTrack createAudioTrack(String audioTrackId, AudioSource localAudioSource);
     VideoTrack createVideoTrack(String videoTrackId, VideoSource localVideoSource);
    /**
     * Callback fired when local video is ready.
     */
    void onLocalVideoReady(int callId);
    /**
     * Callback fired when remote video is ready.
     */
    void onRemoteVideoReady(int callId);

    //public String GetCallerId();
	//public Direction GetDirection() ;
	//public CallState GetCallState();
	//public boolean GetSupportVideo();
	//public boolean GetSupportData();
	//public SipProfile GetProfile();
    //public MediaStream GetMediaStream();
   // public int GetErrorCode();
    //public String GetErrorReason();
   // public  CallReport GetCallReport();
   // public String CallStateName(CallState state);
   // public String GetUniqueId();
}
