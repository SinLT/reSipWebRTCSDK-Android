package com.reSipWebRTC.sipengine;

public interface RTCRegistrationStateObserver {
	
	@CalledByNative void OnRegistrationProgress(int acc_id);

	@CalledByNative void OnRegistrationSuccess(int acc_id);

	@CalledByNative void OnRegistrationCleared(int acc_id);

	@CalledByNative void OnRegistrationFailed(int acc_id, int code, String reason);
}
