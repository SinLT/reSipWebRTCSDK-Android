package com.reSipWebRTC.sdk;

public interface SipIncomingCallListener {
	public  void onCallIncoming(int callId, String peerCallerUri,
								String peerDisplayName, boolean existsAudio,
								boolean existsVideo);
}
