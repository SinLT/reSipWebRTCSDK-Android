package com.reSipWebRTC.service;

import org.webrtc.MediaStream;

public interface CallStreamObserver {
	public void onAddStream(final int call_id, final MediaStream stream);
	public void onRemoveStream(final MediaStream stream);
}
