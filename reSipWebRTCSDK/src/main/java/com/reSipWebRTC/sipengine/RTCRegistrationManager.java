package com.reSipWebRTC.sipengine;

public class RTCRegistrationManager {
	private final long nativeRegistrationManager;

	private native int nativeMakeRegister(String username, String password, String server);
	private native int nativeModifyRegister(int accId, String username, String password, String server);
	private native void nativeMakeDeRegister(int accId);
	private native void nativeRefreshRegistration(int accId);
	private native void nativeRegisterRegistrationObserver(RTCRegistrationStateObserver observer);
	private native void nativeDeRegisterRegistrationObserver();
	private native void nativeSetNetworkReachable(boolean yesno);

	public RTCRegistrationManager(long aNativePtr)
	{
		nativeRegistrationManager = aNativePtr;
		//RegisterRegistrationObserver(nativePtr, this);
	}

        @CalledByNative
        long getNativeRegistrationManager() {
           return nativeRegistrationManager;
        }
	
	public int makeRegister(String username, String password, String server) {
		return nativeMakeRegister(username, password, server);
	}

        public void registerRegistrationObserver(RTCRegistrationStateObserver observer) {
                  nativeRegisterRegistrationObserver(observer);
        }

	public void modifyRegister(int accId, String username, String password, String server) {
		nativeModifyRegister(accId, username, password, server);
	}

	public void makeDeRegister(int accId) {
		nativeMakeDeRegister(accId);
	}

	public void refreshRegistration(int accId) {
		nativeRefreshRegistration(accId);
	}
	
	public void setNetworkReachable(boolean yesno) {
		nativeSetNetworkReachable(yesno);
	}
	
	public boolean profileIsRegistered()
	{
		return true;
	}
}
