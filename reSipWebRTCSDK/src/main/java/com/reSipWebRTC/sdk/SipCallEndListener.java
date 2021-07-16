package com.reSipWebRTC.sdk;

public interface SipCallEndListener {
	
	public  void onCallEnd(int callId, int status, CallLogBean mCallLogBean);
}
