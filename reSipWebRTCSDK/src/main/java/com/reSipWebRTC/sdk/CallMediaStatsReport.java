package com.reSipWebRTC.sdk;

import org.webrtc.StatsReport;

public interface CallMediaStatsReport {
	
	public void onCallMediaStatsReady(StatsReport[] reports);
	
}
