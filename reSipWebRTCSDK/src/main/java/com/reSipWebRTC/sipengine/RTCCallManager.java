package com.reSipWebRTC.sipengine;

public class RTCCallManager {
	//implement for CallManager_JNI.cpp
	private final long nativeCallManager;
        
	private native int nativeCreateCall(int accId);
	private native void nativeMakeCall(int accId, int callId, String calleeUri, String localSdp);
	private native void nativeDirectCall(int accId, int callId, String peerIp, int peerPort, String localSdp);
	private native void nativeAccept(int callId,String localSdp, boolean send_audio, boolean send_video);
	private native void nativeUpdate(int callId,String localSdp);
	private native void nativeUpdateMediaState(int callId, boolean isOffer, String localSdp, boolean audio, boolean video);
	private native void nativeReject(int callId, int code, String reason);
	private native void nativeHangup(int callId);
	private native void nativeRegisterCallStateObserver(RTCCallStateObserver observer);
	private native void nativeDeRegisterCallStateObserver();
	
	public RTCCallManager(long aNativePtr)
	{
		nativeCallManager = aNativePtr;

	}

	@CalledByNative
	long getNativeCallManager() {
           return nativeCallManager;
        }

    public void registerCallStateObserver(RTCCallStateObserver observer) {
		   this.nativeRegisterCallStateObserver(observer);
    }

	public void deRegisterCallStateObserver() {
		//DeRegisterCallStateObserver(nativePtr);
	}
	
	public int createCall(int accId) {
		return nativeCreateCall(accId);
	}
    
	public void makeCall(int accId, int callId, String calleeUri,  String localSdp)
	{
		nativeMakeCall(accId, callId, calleeUri, localSdp);
	}


	public void directCall(int accId, int callId,
			String peerIp, int peerPort, String localSdp)
	{
		nativeDirectCall(accId, callId, peerIp, peerPort, localSdp);
	}

	public void accept(int callId, String localSdp, boolean send_audio, boolean send_video) {
		nativeAccept(callId, localSdp, send_audio, send_video);
	}

	public void updateMediaState(int callId, boolean isOffer, String localSdp, boolean audio, boolean video) {
		nativeUpdateMediaState(callId, isOffer, localSdp, audio, video);
	}

	public void update(int callId, String localSdp) {
		nativeUpdate(callId, localSdp);
	}

	public void reject(int callId, int code, String reason) {
		nativeReject(callId, code, reason);
	}
	

	public void hangup(int callId) {
		nativeHangup(callId);
	}
	

}
