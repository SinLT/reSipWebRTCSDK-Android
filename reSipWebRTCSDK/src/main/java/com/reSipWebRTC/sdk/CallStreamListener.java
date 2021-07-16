package com.reSipWebRTC.sdk;

import org.webrtc.MediaStream;

public interface CallStreamListener {
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
