package com.reSipWebRTC.service;

import android.content.Context;

import com.reSipWebRTC.sipengine.RTCCallManager;
import com.reSipWebRTC.sipengine.RTCRegistrationManager;
import com.reSipWebRTC.sipengine.RTCSipEngine;

public class SipEngineImpl implements SipEngine {
	private Context mContext=null;
	private RTCSipEngine nativeSipEngine;
    private static String TAG = "*SipEngineImpl*";

    public SipEngineImpl(Context context) {
		mContext = context;
	}
	@Override
	public boolean SipEngineInitialize() {
		if(nativeSipEngine == null)
		{
			nativeSipEngine = new RTCSipEngine();
			return true;
		}
		return true;
	}
	
	@Override
	public boolean dispose() {
		return nativeSipEngine.dispose();
	}
	
	//@Override
	//public boolean RunEventLoop() {
		//return RunEventLoop(nativePtr);
	//}
	//@Override
	//public SipProfileManager GetSipProfileManager() {
	//	long sipProfileManagerPtr = GetSipProfileManager(nativePtr);
		//return new SipProfileManagerImpl(sipProfileManagerPtr);
	//}
	@Override
	public CallManager GetCallManager() {
		RTCCallManager callManagerPtr = nativeSipEngine.getCallManager();
        return new CallManagerImpl(callManagerPtr, mContext);
	}
	@Override
	public RegistrationManager GetRegistrationManager() {
		RTCRegistrationManager registrationManagerPtr = nativeSipEngine.getRegistrationManager();
		return new RegistrationManagerImpl(registrationManagerPtr);
	}
	@Override
	public TransportInfo GetTransportInfo() {
      return null;
	}

	//@Override
	//public MediaEngine GetMediaEngine() {
	//	long mediaEnginePtr = GetMediaEngine(nativePtr);
	//	return new MediaEngineImpl(mediaEnginePtr);
	//}
	//@Override
	//public Config GetDefaultConfig() {
	//	long configPtr = GetDefaultConfig(nativePtr);
	//	if(configPtr == 0)
	//		return null;
	//	return new ConfigImpl(configPtr);
	//}
}
