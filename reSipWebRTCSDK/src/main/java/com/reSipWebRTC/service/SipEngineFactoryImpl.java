package com.reSipWebRTC.service;

import android.content.Context;
import android.util.Log;

public class SipEngineFactoryImpl extends SipEngineFactory {
	//private native static boolean NativeInit();
	private static String TAG = "*SipEngineV2*";
	static {
		Log.d(TAG, "Loading jingle_peerconnection_so.so ...");
        System.loadLibrary("jingle_peerconnection_so");
	}
	
	public SipEngine CreateSipEngine(Context context) {
		return new SipEngineImpl(context);
	}
}