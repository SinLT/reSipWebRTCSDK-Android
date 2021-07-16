package com.reSipWebRTC.sdk;

public interface SipOutgoingCallListener {
	
	public  void onCallOutgoing(int callId, String peerCallerUri, String peerDisplayName);

}
