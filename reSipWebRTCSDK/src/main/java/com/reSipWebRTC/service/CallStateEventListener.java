package com.reSipWebRTC.service;

public interface CallStateEventListener {

	void onCallStateChange(int call_id, int state_code);
		//public void onDtmf(long callPtr, String tone);
		//public void onMediaStreamReady(long callPtr, int stream_type);
	/**
	 * Callback fired when local video is ready.
	 */
	void onLocalVideoReady(int callId);

	/**
	 * Callback fired when remote video is ready.
	 */
	void onRemoteVideoReady(int callId);

	/**
	 * Callback fired when remote video is change.
	 */
	void onUpdatedByRemote(int callId, boolean video);

	/**
	 * Callback fired when local video is change.
	 */
	void onUpdatedByLocal(int callId, boolean video);

}
