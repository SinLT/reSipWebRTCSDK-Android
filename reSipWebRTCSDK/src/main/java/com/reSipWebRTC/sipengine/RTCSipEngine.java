package com.reSipWebRTC.sipengine;

import android.util.Log;

public class RTCSipEngine {

	static NativeLibraryLoader nativeLibraryLoader = new NativeLibrary.DefaultLoader();
	static String nativeLibraryName = "reSIProcate-1.12";

	private final long nativeSipEngine;
	private native long nativeInitialize();
	private native boolean nativeFreeSipEngine();
	private native long nativeGetCallManager();
	private native long nativeGetRegistrationManager();
	//private native TransportInfo GetTransportInfo(long nativePtr);

	static {
        NativeLibrary.initialize(nativeLibraryLoader, nativeLibraryName);
	}
	
	public RTCSipEngine() {
		Log.e("SipEngine", "new SipEngine");
		nativeSipEngine = nativeInitialize();
	}

	@CalledByNative
	long getNativeSipEngine() {
           return nativeSipEngine;
        }

	public boolean dispose() {
		return nativeFreeSipEngine();
	}

	public RTCCallManager getCallManager() {
		long callManagerPtr = nativeGetCallManager();
		return new RTCCallManager(callManagerPtr);
	}


	public RTCRegistrationManager getRegistrationManager() {
		long registrationManagerPtr = nativeGetRegistrationManager();
		return new RTCRegistrationManager(registrationManagerPtr);
	}

	//@Override
	//public TransportInfo GetTransportInfo() {
          //  return GetTransportInfo(nativePtr);
	//}

}
