package com.reSipWebRTC.service;

import android.widget.FrameLayout;

import com.reSipWebRTC.sdk.CallMediaStatsReport;

import org.webrtc.VideoSink;

public interface Call
{
	 void makeCall(String calleeUri, CallParams callParams);
	 void directCall(String peerIp, int peerPort, CallParams callParams);
	 void accept(boolean isVideo);
     void updateByInfo(boolean isVideoCall);
     void updateCall(boolean isVideoCall);
     void setRemoteCallerUri(String remoteCallerUri);
     String getRemoteCallerUri();
     void setRemoteDisplayName(String rempoteDisplayName);
     String getRemoteDisplayName();
	 void reject(int code, String reason);
     int getCallId();
     CallParams getCallParams();
     void hangup();
	 void OnCallOffer(String offerSdp);
	 void OnCallAnswer(String answerSdp);
     void OnMediaStateChange(String remoteSdp, boolean audio, boolean video);
     void setVideoMaxBitrate(final Integer maxBitrateKbps);
	 void startVideoRender(VideoSink remoteRender);
     void closeWebRTC();
     void closeSip();
	 void setCallMediaStatsReport(int callId, CallMediaStatsReport mListener);
	 void enableStatsEvents(boolean enable, int periodMs);
	 void setAudioEnabled(final boolean enable);
	 void setVideoEnabled(final boolean enable);
     boolean isActive();
     String getWebtcErrMessage();

   // public int Hold();

   // public int UnHold();

    //public String GetCallerId();
	
	//public Direction GetDirection() ;
	
	//public CallState GetCallState();

     void startVideoSending();
     void startVideoReceiving();
     void stopVideoSending();
     void stopVideoReceiving();
     void startVoiceChannel();
     void stopVoiceChannel();
     void switchCamera();
     void changeCaptureFormat(int width, int height, int framerate);
     void setLocalVideoRender(FrameLayout localFrameLayout);
     void setRemoteVideoRender(FrameLayout remoteFrameLayout);
     void updateCallState(int stateCode, int reasonCode);
     void onCallReceiveReinvite(String sdp);

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
